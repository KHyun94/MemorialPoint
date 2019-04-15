package com.example.memorialpoint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity implements Serializable {

    private Handler handler;
    Context mContext = this;
    String TAG = "TAG";
    //이벤트 처리되는 곳을 제외한 나머지를 클릭 시 포커스 해제
    //이벤트 처리되는 곳을 제외한 나머지를 클릭 시 포커스 해제
    LinearLayout mainLayout, subLayout;
    InputMethodManager imm;

    //데이터
    EditText editID, editPWD, editRePWD, editNAME, editADDRESS, editTEL, editEAIL;
    Spinner yearSpinner, monthSpinner, daySpinner;
    RadioButton rbtnMALE, rbtnFEMALE;
    Button btnSignUp;

    //아이디 중복 체크를 위해서 데이터베이스 내 데이터를 Array화
    ArrayList<Data_User> memberArrayList;

    //조건에 맞추어 작성했는지, 빈칸이 없는지 확인
    Boolean values[] = {false, false, false, false, false, false, false, false, false, false};

    //DB에 삽입하기 위해 정돈
    String id, pwd, name, birthday, year, month, day, sex, address, email, tel;

    //비밀번호와 재입력이 맞는지 아닌지 나태내는 뷰
    TextView discol;


    //아이디 비교 결과를 담는 값.
    int cntID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();
        memberArrayList = (ArrayList<Data_User>) intent.getSerializableExtra("user");

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        subLayout = (LinearLayout) findViewById(R.id.subLayout);

        editID = (EditText) findViewById(R.id.SignUp_editID);
        editPWD = (EditText) findViewById(R.id.SignUp_editPWD);
        editRePWD = (EditText) findViewById(R.id.SignUp_editRePWD);
        editNAME = (EditText) findViewById(R.id.SignUp_editNAME);
        editEAIL = (EditText) findViewById(R.id.SignUp_editEMAIL);
        editADDRESS = (EditText) findViewById(R.id.SignUp_editADDRESS);
        editTEL = (EditText) findViewById(R.id.SignUp_editTEL);
        discol = (TextView) findViewById(R.id.discol);

        rbtnMALE = (RadioButton) findViewById(R.id.SignUp_rbtnMALE);
        rbtnFEMALE = (RadioButton) findViewById(R.id.SignUp_rbtnFEMALE);

        btnSignUp = (Button) findViewById(R.id.SignUp_btnSignUp);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        compareID();
        comparePWD();
        checkNAME();
        spinnerYearMonthDay();
        selectedGender();
        checkADDRESS();
        checkEMAIL();
        checkTEL();

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
            }
        });

        subLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(subLayout.getWindowToken(), 0);
            }
        });

        editTEL.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                    btnSignUp.performClick();
                    return true;
                }

                return false;
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setFocusableInTouchMode(true);
                btnSignUp.requestFocus();
                imm.hideSoftInputFromWindow(btnSignUp.getWindowToken(), 0);
                try {
                    birthday = year + "-" + month + "-" + day;
                } catch (Exception e) {
                    Log.d(TAG, "생년월일 에러: ", e);
                }

                boolean pass = false;

                for (int i = 0; i < values.length; i++) {

                    if (values[i] == false) {
                        pass = false;
                        break;
                    } else {
                        pass = true;
                    }

                }

                Log.d(TAG, "onClick: pass: " + pass);

                if (pass) {

                    DB_InsertUser dbInsertDB = new DB_InsertUser();
                    String ip = getResources().getString(R.string.iptwo);
                    dbInsertDB.execute(ip, id, pwd, name, birthday, sex, address, email, tel);

                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "빈 칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void compareID() {
        editID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String inputID = editID.getText().toString();
                    String ip = getResources().getString(R.string.ip);

                    RetrofitCon conn = new RetrofitCon(ip);
                    Call<String> call = conn.retrofitService.getOverlap(inputID);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String id_Value = response.body();

                            if (inputID.equals("")) {
                                cntID = 1;
                            } else {
                                if (!Pattern.matches("^[a-z0-9-_]{6,12}$", inputID)) {
                                    cntID = 2;
                                } else {
                                    cntID = Integer.parseInt(id_Value);
                                }
                            }

                            switch (cntID) {
                                case 0:
                                    Toast.makeText(getApplicationContext(), "통신오류...", Toast.LENGTH_SHORT).show();
                                    values[0] = false;
                                    break;
                                case 1:
                                    Toast.makeText(getApplicationContext(), "아이디를 등록해주세요.", Toast.LENGTH_SHORT).show();
                                    values[0] = false;
                                    break;
                                case 2:
                                    Toast.makeText(getApplicationContext(), "아이디 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                                    values[0] = false;
                                    break;
                                case 3:
                                    Toast.makeText(getApplicationContext(), "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    editID.setText("");
                                    values[0] = false;
                                    break;
                                case 4:
                                    Toast.makeText(getApplicationContext(), "사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    values[0] = true;
                                    id = inputID;
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "통신오류...", Toast.LENGTH_SHORT).show();
                            values[0] = false;
                        }
                    });


                    Log.d(TAG, "7. onFocusChange이름: " + cntID);

                }
            }
        });
    }

    public void comparePWD() {

        editPWD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String inputPwd = editPWD.getText().toString();
                    String rePwd = editRePWD.getText().toString();

                    if (inputPwd.isEmpty()) {
                        values[1] = false;

                        if (rePwd.length() > 0) {
                            discol.setText("패스워드를 입력해주십시오.");
                            discol.setTextColor(Color.parseColor("#ff0000"));
                            discol.setVisibility(View.VISIBLE);
                        }

                        //        Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {

                        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                            values[1] = false;
                            discol.setText("패스워드 형식에 맞추어 작성하십시오.");
                            discol.setTextColor(Color.parseColor("#ff0000"));
                            discol.setVisibility(View.VISIBLE);
                        } else {
                            if (rePwd.length() > 0) {
                                if (rePwd.equals(inputPwd)) {
                                    values[1] = true;
                                    Toast.makeText(getApplicationContext(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                    discol.setText("패스워드드 일치합니다.");
                                    discol.setTextColor(Color.parseColor("#6BEC62"));
                                    discol.setVisibility(View.VISIBLE);
                                } else {
                                    values[1] = false;
                                    discol.setText("패스워드 불일치합니다.");
                                    discol.setTextColor(Color.parseColor("#ff0000"));
                                    discol.setVisibility(View.VISIBLE);
                                }


                            } else {
                                discol.setVisibility(View.GONE);
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
                        discol.setText("패스워드 불일치합니다.");
                        discol.setTextColor(Color.parseColor("#ff0000"));
                        discol.setVisibility(View.VISIBLE);
                    } else {
                        if (!inputPwd.equals(rePwd)) {
                            values[1] = false;
                            discol.setText("패스워드 불일치합니다.");
                            discol.setTextColor(Color.parseColor("#ff0000"));
                            discol.setVisibility(View.VISIBLE);
                            //     Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                                values[1] = false;
                                discol.setText("패스워드 형식에 맞추어 작성하십시오.");
                                discol.setTextColor(Color.parseColor("#ff0000"));
                                discol.setVisibility(View.VISIBLE);
                                //   Toast.makeText(getApplicationContext(), "비밀번호 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                            } else {
                                values[1] = true;
                                pwd = inputPwd;
                                Toast.makeText(getApplicationContext(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                discol.setText("패스워드 일치합니다.");
                                discol.setTextColor(Color.parseColor("#6BEC62"));
                                discol.setVisibility(View.VISIBLE);

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
                        //     Toast.makeText(getApplicationContext(), "이름을 등록해주십시오.", Toast.LENGTH_SHORT).show();
                        values[2] = false;
                    } else {

                        if (!Pattern.matches("^([가-힣]{2,5})|([a-zA-Z]{2,10}\\s[a-zA-Z]{2,10})|([a-zA-Z]{2,10}\\s[a-zA-Z]{2,10}\\s[a-zA-Z]{2,10})$", inputName)) {
                            values[2] = true;
                            Toast.makeText(getApplicationContext(), "한글 또는 영어 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                        } else {

                            // Toast.makeText(getApplicationContext(), "이름 확인되었습니다.", Toast.LENGTH_SHORT).show();
                            values[2] = true;
                            name = inputName;
                        }

                    }
                }

            }
        });
    }

    public void spinnerYearMonthDay() {
        /*
         * 연도를 거꾸로 받아올 예정
         * 월마다 일 수 바뀌는거 수정 예정
         *
         * */
        yearSpinner = (Spinner) findViewById(R.id.spinner_year);
        final ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        monthSpinner = (Spinner) findViewById(R.id.spinner_month);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_month, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        daySpinner = (Spinner) findViewById(R.id.spinner_day);
        ArrayAdapter dayAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_day, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                imm.hideSoftInputFromWindow(yearSpinner.getWindowToken(), 0);

                if (yearSpinner.getSelectedItem().toString().equals("년/연도")) {
                    values[3] = false;
                } else {

                    year = yearSpinner.getSelectedItem().toString();
                    values[3] = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "연도 선택.", Toast.LENGTH_SHORT).show();
            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                imm.hideSoftInputFromWindow(monthSpinner.getWindowToken(), 0);

                if (monthSpinner.getSelectedItem().toString().equals("월")) {
                    values[4] = false;
                } else {
                    month = monthSpinner.getSelectedItem().toString();
                    values[4] = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "월 선택.", Toast.LENGTH_SHORT).show();
            }
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                imm.hideSoftInputFromWindow(daySpinner.getWindowToken(), 0);

                if (daySpinner.getSelectedItem().toString().equals("일")) {
                    values[5] = false;
                } else {
                    day = daySpinner.getSelectedItem().toString();
                    values[5] = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "일 선택.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectedGender() {
        rbtnMALE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(rbtnMALE.getWindowToken(), 0);
                rbtnFEMALE.setChecked(false);
                sex = "Male";
                values[6] = true;
            }
        });

        rbtnFEMALE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(rbtnFEMALE.getWindowToken(), 0);
                rbtnMALE.setChecked(false);
                sex = "Female";
                values[6] = true;
            }
        });
    }

    public void checkADDRESS() {
        editADDRESS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String insertAddress = editADDRESS.getText().toString();

                if (!hasFocus) {

                    if (insertAddress.isEmpty()) {
                        //    Toast.makeText(getApplicationContext(), "주소를 등록해주십시오.", Toast.LENGTH_SHORT).show();
                        values[7] = false;
                    } else {
                        address = insertAddress;
                        values[7] = true;
                    }
                }

            }
        });
    }

    public void checkEMAIL() {
        editEAIL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String insertEmail = editEAIL.getText().toString();

                if (!hasFocus) {

                    if (insertEmail.isEmpty()) {
                        //      Toast.makeText(getApplicationContext(), "이메일을 등록해주십시오.", Toast.LENGTH_SHORT).show();
                        values[8] = false;
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(insertEmail).matches()) {
                        Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                        values[8] = false;
                    } else {
                        values[8] = true;
                        email = insertEmail;
                    }
                }

            }
        });
    }

    public void checkTEL() {
        editTEL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (!hasFocus) {

                    String insertTel = editTEL.getText().toString();
                    String tels[] = new String[3];

                    Log.d(TAG, "onFocusChange 길이: " + insertTel.length());
                    if (insertTel.isEmpty()) {
                        //         Toast.makeText(getApplicationContext(), "번호를 등록해주십시오.", Toast.LENGTH_SHORT).show();
                        values[9] = false;
                    } else {

                        if (insertTel.length() == 10) {
                            tels[0] = insertTel.substring(0, 3);
                            tels[1] = insertTel.substring(3, 6);
                            tels[2] = insertTel.substring(6, 10);
                            tel = tels[0] + "-" + tels[1] + "-" + tels[2];

                        } else if (insertTel.length() == 11) {
                            tels[0] = insertTel.substring(0, 3);
                            tels[1] = insertTel.substring(3, 7);
                            tels[2] = insertTel.substring(7, 11);

                            tel = tels[0] + "-" + tels[1] + "-" + tels[2];
                        } else {
                            tel = insertTel;
                        }


                        editTEL.setText(tel);

                        if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", tel)) {
                            values[9] = false;

                            Toast.makeText(getApplicationContext(), "형식에 맞추어 작성부탁드립니다.", Toast.LENGTH_SHORT).show();

                        } else {
                            tel = editTEL.getText().toString();
                            values[9] = true;

                        }

                    }
                }

            }
        });
    }


}
