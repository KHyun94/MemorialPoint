package com.example.memorialpoint;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Models.UserData;

import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {

    UserData receiveInfo;

    // 앱 전반적 데이터
    Activity act;
    Context mContext;

    // 다른 곳 클릭 시 포커스 해제
    String no, pwd;

    LinearLayout showLayout, reviseLayout;

    TextView Profile_showID, Profile_showPWD, Profile_showNAME, Profile_showEMAIL;
    RadioButton Profile_rbtnMALE, Profile_rbtnFEMALE;
    Button Profile_reviseBtn, Profile_endBtn;

    TextView Profile_reviseID, Profile_reviseNAME, Profile_reviseEMAIL;
    EditText Profile_revisePWD, Profile_reviseRePWD;
    RadioButton Profile_reviseRbtnMALE, Profile_reviseRbtnFEMALE;
    Button Profile_reviseOk;
    TextView Profile_discol;

    boolean isValue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 앱 전반적 데이터
        act = this;
        mContext = this;

        Intent intent = getIntent();
        receiveInfo = (UserData) intent.getSerializableExtra("user");

        showLayout = (LinearLayout) findViewById(R.id.showLayout);
        reviseLayout = (LinearLayout) findViewById(R.id.reviseLayout);

        //showLayout을 보여주고 수정 및 탈퇴 이벤트를 포함
        showData();
    }

    //프로필 데이터 초기값 설정 및 '수정' 또는 '탈퇴' 이벤트를 처리
    private void showData() {
        Profile_showID = (TextView) findViewById(R.id.profile_showID);
        Profile_showPWD = (TextView) findViewById(R.id.profile_showPWD);
        Profile_showNAME = (TextView) findViewById(R.id.profile_showName);

        Profile_showEMAIL = (TextView) findViewById(R.id.profile_showEmail);

        Profile_rbtnMALE = (RadioButton) findViewById(R.id.profile_showMALE);
        Profile_rbtnFEMALE = (RadioButton) findViewById(R.id.profile_showFEMALE);

        Profile_reviseBtn = (Button) findViewById(R.id.Profile_btnRevise);
        Profile_endBtn = (Button) findViewById(R.id.Profile_btnEnd);

        //회원 정보를 showLayout 내부 위젯에 삽입입
        Profile_showEMAIL.setText(receiveInfo.getEmail());
        Profile_showEMAIL.setSelected(true);
        Profile_showID.setText(receiveInfo.getId());
        Profile_showPWD.setText(receiveInfo.getPwd());
        Profile_showNAME.setText(receiveInfo.getName());

        Log.d("TAG", "showData: " + receiveInfo.toString());
        if (receiveInfo.getSex().equals("Male")){
            Profile_rbtnMALE.setButtonDrawable(R.drawable.p_signup_male);
            Profile_rbtnFEMALE.setButtonDrawable(R.drawable.p_signup_female_nonclick);
        } else{
            Profile_rbtnMALE.setButtonDrawable(R.drawable.p_signup_male_nonclick);
            Profile_rbtnFEMALE.setButtonDrawable(R.drawable.p_signup_female);
        }

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
        Profile_reviseID = (TextView) findViewById(R.id.profile_reviseID);
        Profile_reviseNAME = (TextView) findViewById(R.id.profile_reviseName);
        Profile_reviseEMAIL = (TextView) findViewById(R.id.profile_reviseEmail);

        Profile_reviseRbtnMALE = (RadioButton) findViewById(R.id.profile_reviseMALE);
        Profile_reviseRbtnFEMALE = (RadioButton) findViewById(R.id.profile_reviseFEMALE);

        Profile_revisePWD = (EditText) findViewById(R.id.profile_revisePWD);
        Profile_reviseRePWD = (EditText) findViewById(R.id.profile_reviseRePWD);
        Profile_discol = findViewById(R.id.profile_discol);
        Profile_reviseOk = (Button) findViewById(R.id.profile_reviseOk);

        //저장된 값 출력

        Profile_reviseEMAIL.setText(receiveInfo.getEmail());
        Profile_reviseEMAIL.setSelected(true);

        Profile_reviseID.setText(receiveInfo.getId());
        Profile_revisePWD.setText(receiveInfo.getPwd());
        Profile_reviseNAME.setText(receiveInfo.getName());

        if (receiveInfo.getSex().equals("Male")){
            Profile_reviseRbtnMALE.setButtonDrawable(R.drawable.p_signup_male);
            Profile_reviseRbtnFEMALE.setButtonDrawable(R.drawable.p_signup_female_nonclick);
        } else{
            Profile_reviseRbtnMALE.setButtonDrawable(R.drawable.p_signup_male_nonclick);
            Profile_reviseRbtnFEMALE.setButtonDrawable(R.drawable.p_signup_female);
        }

        Profile_reviseEMAIL.setText(receiveInfo.getEmail());

        revisePWD();

        Profile_reviseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValue) {
                    String no = receiveInfo.getNo();
                    receiveInfo.setPwd(Profile_revisePWD.getText().toString());

                    DB_updateUser DB_updateUser = new DB_updateUser();
                    DB_updateUser.execute(MyApplication.ip, no, receiveInfo.getPwd());

                    Intent intent = new Intent();
                    intent.putExtra("revise_user", receiveInfo);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(mContext, "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(mContext, "비밀 번호를 다시 확인부탁드립니다.", Toast.LENGTH_SHORT).show();
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
                        isValue = false;

                        if (rePwd.length() > 0) {
                            Profile_discol.setText("패스워드를 입력해주십시오.");
                            Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                            Profile_discol.setVisibility(View.VISIBLE);
                        }

                        //        Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {

                        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                            isValue = false;
                            Profile_discol.setText("패스워드 형식에 맞추어 작성하십시오.");
                            Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                            Profile_discol.setVisibility(View.VISIBLE);
                        } else {
                            if (rePwd.length() > 0) {
                                if (rePwd.equals(inputPwd)) {
                                    isValue = true;
                                    Toast.makeText(getApplicationContext(), "비밀번호 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                    Profile_discol.setText("패스워드 일치합니다.");
                                    Profile_discol.setTextColor(Color.parseColor("#6BEC62"));
                                    Profile_discol.setVisibility(View.VISIBLE);
                                } else {
                                    isValue = false;
                                    Profile_discol.setText("패스워드 불일치합니다.");
                                    Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                                    Profile_discol.setVisibility(View.VISIBLE);
                                }


                            } else {
                                Profile_discol.setVisibility(View.INVISIBLE);
                                isValue = false;
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
                        isValue = false;
                        Profile_discol.setText("패스워드 불일치합니다.");
                        Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                        Profile_discol.setVisibility(View.VISIBLE);
                    } else {
                        if (!inputPwd.equals(rePwd)) {
                            isValue = false;
                            Profile_discol.setText("패스워드 불일치합니다.");
                            Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                            Profile_discol.setVisibility(View.VISIBLE);
                            //     Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", inputPwd)) {
                                isValue = false;
                                Profile_discol.setText("패스워드 형식에 맞추어 작성하십시오.");
                                Profile_discol.setTextColor(Color.parseColor("#ff0000"));
                                Profile_discol.setVisibility(View.VISIBLE);
                                //   Toast.makeText(getApplicationContext(), "비밀번호 형식에 맞추어 작성해주십시오.", Toast.LENGTH_SHORT).show();
                            } else {
                                isValue = true;
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

    public void removeID() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialog_Style);
        alert.setTitle("회원 탈퇴");
        alert.setMessage("정말로 탈퇴하시겠습니까?\n*탈퇴 시 데이터 복구는 안됩니다.");
        alert.setNegativeButton("아니오",null);

        alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                no = receiveInfo.getNo();

                DB_removeUser DB_removeUser = new DB_removeUser();
                DB_removeUser.execute(MyApplication.ip, no);

                Toast.makeText(getApplicationContext(), "이때까지 이용해주셔서 감사드립니다.\n다음에 다시 뵙길 바라겠습니다.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(mContext, SignIn.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(act);
            }
        });
        alert.show();
    }
}
