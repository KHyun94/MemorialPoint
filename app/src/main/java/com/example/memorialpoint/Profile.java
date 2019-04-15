package com.example.memorialpoint;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {

    final int REVISE_DATA = 3;
    Data_User receiveInfo;
    String ip;
    // 앱 전반적 데이터
    Activity act;
    Context mContext;

    // 다른 곳 클릭 시 포커스 해제
    InputMethodManager imm;
    String no, pwd, address, tel;

    LinearLayout ProfileLayout;
    LinearLayout showLayout, reviseLayout;

    TextView Profile_showID, Profile_showPWD, Profile_showNAME, Profile_showBirthday, Profile_showADDRESS, Profile_showEMAIL, Profile_showTEL;
    RadioButton Profile_rbtnMALE, Profile_rbtnFEMALE;
    Button Profile_reviseBtn, Profile_endBtn;

    TextView Profile_reviseID, Profile_reviseNAME, Profile_reviseBirthday, Profile_reviseEMAIL;
    EditText Profile_revisePWD, Profile_reviseRePWD, Profile_reviseADDRESS, Profile_reviseTEL;
    RadioButton Profile_reviseRbtnMALE, Profile_reviseRbtnFEMALE;
    Button Profile_reviseOk;
    TextView Profile_discol;

    boolean[] values = {true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 앱 전반적 데이터
        act = this;
        mContext = this;
        ip = getResources().getString(R.string.ip);

        Intent intent = getIntent();
        receiveInfo = (Data_User) intent.getSerializableExtra("user");


        ProfileLayout = (LinearLayout) findViewById(R.id.ProfileLayout);

        showLayout = (LinearLayout) findViewById(R.id.showLayout);
        reviseLayout = (LinearLayout) findViewById(R.id.reviseLayout);

        //이벤트 처리가 발생하는 뷰를 제외한 공간을 터치할 시 포커스 해제
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        ProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(ProfileLayout.getWindowToken(), 0);
                Log.d("TAG", "onClick 바깥부분: ");
            }
        });

        reviseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(reviseLayout.getWindowToken(), 0);
                Log.d("TAG", "onClick reviselayout: ");
            }
        });

        //showLayout을 보여주고 수정 및 탈퇴 이벤트를 포함
        showData();


    }

    //프로필 데이터 초기값 설정 및 '수정' 또는 '탈퇴' 이벤트를 처리
    private void showData() {
        Profile_showID = (TextView) findViewById(R.id.Profile_showID);
        Profile_showPWD = (TextView) findViewById(R.id.Profile_showPWD);
        Profile_showNAME = (TextView) findViewById(R.id.Profile_showNAME);
        Profile_showBirthday = (TextView) findViewById(R.id.Profile_showBirthday);
        Profile_showADDRESS = (TextView) findViewById(R.id.Profile_showADDRESS);
        Profile_showEMAIL = (TextView) findViewById(R.id.Profile_showEMAIL);
        Profile_showTEL = (TextView) findViewById(R.id.Profile_showTEL);

        Profile_rbtnMALE = (RadioButton) findViewById(R.id.Profile_rbtnMALE);
        Profile_rbtnFEMALE = (RadioButton) findViewById(R.id.Profile_rbtnFEMALE);

        Profile_reviseBtn = (Button) findViewById(R.id.Profile_btnRevise);
        Profile_endBtn = (Button) findViewById(R.id.Profile_btnEnd);

        //회원 정보를 showLayout 내부 위젯에 삽입입
        Profile_showID.setText(receiveInfo.getId());
        Profile_showPWD.setText(receiveInfo.getPwd());
        Profile_showNAME.setText(receiveInfo.getName());

        if (receiveInfo.getSex().equals("Male"))
            Profile_rbtnMALE.setButtonDrawable(R.drawable.p_signup_male);
        else
            Profile_rbtnFEMALE.setButtonDrawable(R.drawable.p_signup_female);

        Profile_showBirthday.setText(receiveInfo.getBirthday());
        Profile_showADDRESS.setText(receiveInfo.getAddress());
        Profile_showEMAIL.setText(receiveInfo.getEmail());
        Profile_showTEL.setText(receiveInfo.getTel());

        //'수정' 버튼을 누를 시 이벤트 처리
        Profile_reviseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviseDialog();
            }
        });

        //'탈퇴' 버튼을 누를 시 아이디 삭제 -> 로그인 창으로 넘어간다.
        Profile_endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeID();
            }
        });
    }

    // '수정' 버튼을 누를 시 다이얼로그 생성
    public void reviseDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialog_Style);
        alert.setTitle("회원 정보 수정");
        alert.setMessage("회원정보를 수정하시겠습니까?");
        alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLayout.setVisibility(View.GONE);
                reviseLayout.setVisibility(View.VISIBLE);

                reviseData();
            }
        });
        alert.show();
    }

    //수정 다이얼로그 'Yes'에 해당되는 값
    private void reviseData() {

        //reviseLayout 위젯 선언
        Profile_reviseID = (TextView) findViewById(R.id.Profile_reviseID);
        Profile_reviseNAME = (TextView) findViewById(R.id.Profile_reviseNAME);
        Profile_reviseBirthday = (TextView) findViewById(R.id.Profile_reviseBirthday);
        Profile_reviseEMAIL = (TextView) findViewById(R.id.Profile_reviseEMAIL);

        Profile_reviseRbtnMALE = (RadioButton) findViewById(R.id.Profile_reviseRbtnMALE);
        Profile_reviseRbtnFEMALE = (RadioButton) findViewById(R.id.Profile_reviseRbtnFEMALE);

        Profile_revisePWD = (EditText) findViewById(R.id.Profile_revisePWD);
        Profile_reviseRePWD = (EditText) findViewById(R.id.Profile_reviseRePWD);
        Profile_reviseADDRESS = (EditText) findViewById(R.id.Profile_reviseADDRESS);
        Profile_reviseTEL = (EditText) findViewById(R.id.Profile_reviseTEL);

        Profile_reviseOk = (Button) findViewById(R.id.Profile_reviseOk);
        Profile_discol = (TextView) findViewById(R.id.Profile_discol);

        //저장된 값 출력
        Profile_reviseID.setText(receiveInfo.getId());
        Profile_revisePWD.setText(receiveInfo.getPwd());
        Profile_reviseNAME.setText(receiveInfo.getName());

        if (receiveInfo.getSex().equals("Male"))
            Profile_reviseRbtnMALE.setButtonDrawable(R.drawable.p_signup_male);
        else
            Profile_reviseRbtnFEMALE.setButtonDrawable(R.drawable.p_signup_female);

        Profile_reviseBirthday.setText(receiveInfo.getBirthday());
        Profile_reviseADDRESS.setText(receiveInfo.getAddress());
        Profile_reviseEMAIL.setText(receiveInfo.getEmail());
        Profile_reviseTEL.setText(receiveInfo.getTel());

        revisePWD();
        reviseADDRESS();
        reviseTEL();

        Profile_reviseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean pass = false;

                for (int i = 0; i < values.length; i++) {
                    if (!values[i]) {
                        pass = false;
                        break;
                    } else {
                        pass = true;
                    }
                }

                if (pass) {
                    String no = receiveInfo.getNo();
                    receiveInfo.setPwd(Profile_revisePWD.getText().toString());
                    receiveInfo.setAddress(Profile_reviseADDRESS.getText().toString());
                    receiveInfo.setTel(Profile_reviseTEL.getText().toString());

                    DB_updateUser DB_updateUser = new DB_updateUser();
                    DB_updateUser.execute(ip, no, receiveInfo.getPwd(), receiveInfo.getAddress(), receiveInfo.getTel());

                    Intent intent = new Intent();
                    intent.putExtra("revise_user", receiveInfo);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(getApplicationContext(), "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "다 써넣어주세여", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void revisePWD() {

        Profile_revisePWD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String inputPwd = Profile_revisePWD.getText().toString();
                    String rePwd = Profile_reviseRePWD.getText().toString();

                    if (inputPwd.isEmpty()) {
                        values[0] = false;

                        if (rePwd.length() > 0) {
                            Profile_discol.setText("패스워드를 입력해주십시오.");
                            Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                            Profile_discol.setVisibility(View.VISIBLE);
                        }

                        //        Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {

                        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                            values[0] = false;
                            Profile_discol.setText("패스워드 형식에 맞추어 작성하십시오.");
                            Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                            Profile_discol.setVisibility(View.VISIBLE);
                        } else {
                            if (rePwd.length() > 0) {
                                if (rePwd.equals(inputPwd)) {
                                    values[0] = true;
                                    Toast.makeText(getApplicationContext(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                    Profile_discol.setText("패스워드 일치합니다.");
                                    Profile_discol.setTextColor(Color.parseColor("#6BEC62"));
                                    Profile_discol.setVisibility(View.VISIBLE);
                                } else {
                                    values[0] = false;
                                    Profile_discol.setText("패스워드 불일치합니다.");
                                    Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                                    Profile_discol.setVisibility(View.VISIBLE);
                                }


                            } else {
                                Profile_discol.setVisibility(View.INVISIBLE);
                                values[0] = false;
                            }


                        }

                    }
                }
            }
        });


        Profile_reviseRePWD.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    String inputPwd = Profile_revisePWD.getText().toString();
                    String rePwd = Profile_reviseRePWD.getText().toString();

                    if (rePwd.isEmpty()) {
                        values[0] = false;
                        Profile_discol.setText("패스워드 불일치합니다.");
                        Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                        Profile_discol.setVisibility(View.VISIBLE);
                    } else {
                        if (!inputPwd.equals(rePwd)) {
                            values[0] = false;
                            Profile_discol.setText("패스워드 불일치합니다.");
                            Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                            Profile_discol.setVisibility(View.VISIBLE);
                            //     Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                                values[0] = false;
                                Profile_discol.setText("패스워드 형식에 맞추어 작성하십시오.");
                                Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                                Profile_discol.setVisibility(View.VISIBLE);
                                //   Toast.makeText(getApplicationContext(), "비밀번호 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                            } else {
                                values[0] = true;
                                pwd = inputPwd;
                                Toast.makeText(getApplicationContext(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                Profile_discol.setText("패스워드 일치합니다.");
                                Profile_discol.setTextColor(Color.parseColor("#6BEC62"));
                                Profile_discol.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                }
            }
        });
    }

    public void reviseADDRESS() {
        Profile_reviseADDRESS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String insertAddress = Profile_reviseADDRESS.getText().toString();

                if (!hasFocus) {

                    if (insertAddress.isEmpty()) {
                        //    Toast.makeText(getApplicationContext(), "주소를 등록해주십시오.", Toast.LENGTH_SHORT).show();
                        values[1] = false;
                    } else {
                        address = insertAddress;
                        values[1] = true;
                    }
                }

            }
        });
    }

    public void reviseTEL() {
        Profile_reviseTEL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (!hasFocus) {

                    String insertTel = Profile_reviseTEL.getText().toString();
                    String tels[] = new String[3];

                    if (insertTel.isEmpty()) {
                        values[2] = false;
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


                        Profile_reviseTEL.setText(tel);

                        if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", tel)) {
                            values[2] = false;

                            Toast.makeText(getApplicationContext(), "형식에 맞추어 작성부탁드립니다.", Toast.LENGTH_SHORT).show();

                        } else {
                            tel = Profile_reviseTEL.getText().toString();
                            values[2] = true;

                        }

                    }
                }

            }
        });
    }

    public void removeID() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("회원 탈퇴");
        alert.setMessage("정말로 탈퇴하시겠습니까?\n*탈퇴 시 데이터 복구는 안됩니다.");
        alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                no = receiveInfo.getNo();

                DB_removeUser DB_removeUser = new DB_removeUser();
                DB_removeUser.execute(ip, no);

                Toast.makeText(getApplicationContext(), "이때까지 이용해주셔서 감사드립니다.\n다음에 다시 뵙길 바라겠습니다.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(act);
            }
        });
        alert.show();
    }
}
