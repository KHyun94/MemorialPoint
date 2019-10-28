package com.example.memorialpoint.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.DB_InsertUser;
import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;
import com.example.memorialpoint.SignIn;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment extends android.support.v4.app.Fragment {

    //데이터
    EditText editID, editPWD, editRePWD, editNAME;
    TextView titleText, textEMAIL;

    RadioButton rbtnMALE, rbtnFEMALE;
    Button btnSignUp;

    //조건에 맞추어 작성했는지, 빈칸이 없는지 확인
    Boolean values[] = {false, false, false, false};

    //DB에 삽입하기 위해 정돈
    String id, pwd, name, sex, email;

    //비밀번호와 재입력이 맞는지 아닌지 나태내는 뷰
    TextView matchText;

    //아이디 비교 결과를 담는 값.
    int cntID;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null) {
            email = (String) this.getArguments().getString("email");
        }
    }

    public void init(View view){

        textEMAIL = (TextView) view.findViewById(R.id.SignIn_textEmail);

        editID = (EditText) view.findViewById(R.id.SignUp_editID);
        editPWD = (EditText) view.findViewById(R.id.SignUp_editPWD);
        editRePWD = (EditText) view.findViewById(R.id.SignUp_editRePWD);
        editNAME = (EditText) view.findViewById(R.id.SignUp_editNAME);
        matchText = (TextView) view.findViewById(R.id.matchText);

        rbtnMALE = (RadioButton) view.findViewById(R.id.SignUp_rbtnMALE);
        rbtnFEMALE = (RadioButton) view.findViewById(R.id.SignUp_rbtnFEMALE);

        btnSignUp = (Button) view.findViewById(R.id.SignUp_btnSignUp);

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up_fragment2, container, false);

        init(view);

        textEMAIL.setText("인증 이메일 " + email);

        checkID();
        checkPWD();
        checkNAME();
        selectedGender();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setFocusableInTouchMode(true);
                btnSignUp.requestFocus();

                boolean isPass = false;

                for (int i = 0; i < values.length; i++) {

                    if (!values[i]) {
                        isPass = false;
                        break;
                    } else {
                        isPass = true;
                    }
                }

                if (isPass) {

                    DB_InsertUser dbInsertDB = new DB_InsertUser();

                    dbInsertDB.execute(MyApplication.ip, id, pwd, name, sex, email);

                    Intent intent = new Intent(getActivity(), SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    Toast.makeText(getActivity(), "회원가입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "빈 칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    public void checkID() {
        editID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String inputID = editID.getText().toString();

                    Call<List<ResponseData>> call = MyApplication.conn.retrofitService.getDuplicateId(inputID);
                    call.enqueue(new Callback<List<ResponseData>>() {
                        @Override
                        public void onResponse(Call<List<ResponseData>> call, Response<List<ResponseData>> response) {
                            String responseData = response.body().get(0).getResponseData();

                            if (inputID.equals("")) {
                                cntID = 1;
                            } else {
                                if (!Pattern.matches("^[a-z0-9-_]{6,12}$", inputID)) {
                                    cntID = 2;
                                } else {
                                    if (responseData.equals("exist")) {
                                        cntID = 3;
                                    } else if (responseData.equals("noneExist")) {
                                        cntID = 4;
                                    }
                                }
                            }

                            switch (cntID) {
                                case 0:
                                    Toast.makeText(getActivity(), "통신오류...", Toast.LENGTH_SHORT).show();
                                    values[0] = false;
                                    break;
                                case 1:
                                    Toast.makeText(getActivity(), "아이디를 등록해주세요.", Toast.LENGTH_SHORT).show();
                                    values[0] = false;
                                    break;
                                case 2:
                                    Toast.makeText(getActivity(), "아이디 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                                    values[0] = false;
                                    break;
                                case 3:
                                    Toast.makeText(getActivity(), "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    editID.setText("");
                                    values[0] = false;
                                    break;
                                case 4:
                                    Toast.makeText(getActivity(), "사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    values[0] = true;
                                    id = inputID;
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ResponseData>> call, Throwable t) {
                            Toast.makeText(getActivity(), "통신오류...", Toast.LENGTH_SHORT).show();
                            values[0] = false;
                        }
                    });
                }
            }
        });
    }

    public void checkPWD() {

        editPWD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String inputPwd = editPWD.getText().toString();
                    String rePwd = editRePWD.getText().toString();

                    if (inputPwd.isEmpty()) {
                        values[1] = false;

                        if (rePwd.length() > 0) {
                            matchText.setText("패스워드를 입력해주십시오.");
                            matchText.setTextColor(Color.parseColor("#ff0000"));
                            matchText.setVisibility(View.VISIBLE);
                        }
                    } else {

                        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                            values[1] = false;
                            matchText.setText("패스워드 형식에 맞추어 작성하십시오.");
                            matchText.setTextColor(Color.parseColor("#ff0000"));
                            matchText.setVisibility(View.VISIBLE);
                        } else {
                            if (rePwd.length() > 0) {
                                if (rePwd.equals(inputPwd)) {
                                    values[1] = true;
                                    Toast.makeText(getActivity(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                    matchText.setText("패스워드드 일치합니다.");
                                    matchText.setTextColor(Color.parseColor("#689F38"));
                                    matchText.setVisibility(View.VISIBLE);
                                } else {
                                    values[1] = false;
                                    matchText.setText("패스워드 불일치합니다.");
                                    matchText.setTextColor(Color.parseColor("#ff0000"));
                                    matchText.setVisibility(View.VISIBLE);
                                }
                            } else {
                                matchText.setVisibility(View.GONE);
                                values[1] = false;
                            }
                        }

                    }
                }
            }
        });


        editRePWD.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    String inputPwd = editPWD.getText().toString();
                    String rePwd = editRePWD.getText().toString();

                    if (rePwd.isEmpty()) {
                        values[1] = false;
                        matchText.setText("패스워드 불일치합니다.");
                        matchText.setTextColor(Color.parseColor("#ff0000"));
                        matchText.setVisibility(View.VISIBLE);
                    } else {
                        if (!inputPwd.equals(rePwd)) {
                            values[1] = false;
                            matchText.setText("패스워드 불일치합니다.");
                            matchText.setTextColor(Color.parseColor("#ff0000"));
                            matchText.setVisibility(View.VISIBLE);
                            //     Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                                values[1] = false;
                                matchText.setText("패스워드 형식에 맞추어 작성하십시오.");
                                matchText.setTextColor(Color.parseColor("#ff0000"));
                                matchText.setVisibility(View.VISIBLE);
                                //   Toast.makeText(getActivity(), "비밀번호 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                            } else {
                                values[1] = true;
                                pwd = inputPwd;
                                Toast.makeText(getActivity(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                matchText.setText("패스워드 일치합니다.");
                                matchText.setTextColor(Color.parseColor("#689F38"));
                                matchText.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                }
            }
        });
    }

    public void checkNAME() {
        editNAME.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String inputName = editNAME.getText().toString();

                if (!hasFocus) {

                    if (inputName.isEmpty()) {
                        //     Toast.makeText(getActivity(), "이름을 등록해주십시오.", Toast.LENGTH_SHORT).show();
                        values[2] = false;
                    } else {

                        if (!Pattern.matches("^([가-힣]{2,5})|([a-zA-Z]{2,10}\\s[a-zA-Z]{2,10})|([a-zA-Z]{2,10}\\s[a-zA-Z]{2,10}\\s[a-zA-Z]{2,10})$", inputName)) {
                            values[2] = true;
                            Toast.makeText(getActivity(), "한글 또는 영어 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                        } else {

                            // Toast.makeText(getActivity(), "이름 확인되었습니다.", Toast.LENGTH_SHORT).show();
                            values[2] = true;
                            name = inputName;
                        }

                    }
                }

            }
        });
    }

    public void selectedGender() {
        rbtnMALE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnFEMALE.setChecked(false);
                sex = "Male";
                values[3] = true;
            }
        });

        rbtnFEMALE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnMALE.setChecked(false);
                sex = "Female";
                values[3] = true;
            }
        });
    }

}
