package com.example.memorialpoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignIn extends AppCompatActivity implements Serializable {

    String TAG = "TAG";

    Context mContext;

    EditText editID, editPWD;   //각각 레이아웃의 ID, PWD의 EditText(이하 eT)
    Button btnSignIn, btnSignUp;    // 각각 레이아웃의 로그인, 회원가입 Button(이하 btn)

    //자동 로그인
    SharedPreferences loginInfo;    //자동로그인 체크박스를 누를 시 editID, editPWD에 입력한 값 유지 및 디바이스 내부에 저장
    SharedPreferences.Editor editor;    //(SharedPreferences) loginInfo의 데이터를 관리
    CheckBox autoLogin;    //체크 시 loginInfo 사용

    ProgressDialog progressDialog; //로딩 중 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mContext = this;

        //아이디 비밀번호
        editID = (EditText) findViewById(R.id.SignIn_editId);
        editPWD = (EditText) findViewById(R.id.SignIn_editPwd);

        //로그인, 회원가입
        btnSignIn = (Button) findViewById(R.id.SignIn_btnSignIn);
        btnSignUp = (Button) findViewById(R.id.SignIn_btnSignUp);

        //자동 로그인 체크박스
        autoLogin = (CheckBox) findViewById(R.id.SignIn_autoLogin);

        //디바이스 내부 저장된 SharedPreferences 파일을 가져온다.
        loginInfo = getSharedPreferences("login", 0);

        //해당 SharedPreferences 객체의 관리 권한 이양
        editor = loginInfo.edit();

        //불러온 loginInfo의 데이터 확인 값이 없을 시 defValue:false로 반환
        if (loginInfo.getBoolean("autoLogin_enabled", false)) {

            //autoLogin의 값 유지
            autoLogin.setChecked(true);
            //editID, editPWD에 loginInfo에 각각 저장된 id, pwd 값을 입력한다. 실패 시 '실패'라고 값을 반환한다.
            editID.setText(loginInfo.getString("id", ""));
            editPWD.setText(loginInfo.getString("pwd", ""));
            SignInProcess(editID.getText().toString(), editPWD.getText().toString());


        } else {
            //1.5second 이후 editID, editPWD의 Text를 초기화
            autoLogin.setChecked(false);

            editID.setText("");
            editPWD.setText("");
        }


        //editPWD에 커서가 있을 때 자판상 Enter를 누르면 포커스가 btnSignIn으로 이동 후 실행한다.
        editPWD.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                    btnSignIn.performClick();
                    return true;
                }

                return false;
            }
        });
        goSignIn();
        goSignUp();
    }

    //로그인
    public void goSignIn() {

        //btnSignIn 원클릭 시 이벤트
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //editID, editPWD의 입력값
                String getID = editID.getText().toString();
                String getPWD = editPWD.getText().toString();
                SignInProcess(getID, getPWD);
            }
        });
    }

    public void SignInProcess(String getID, String getPWD) {

        //HashMap으로 ID, PWD 관리
        Map compareDataMap = new HashMap();

        compareDataMap.put("id", getID);
        compareDataMap.put("pwd", getPWD);

        //커스텀할 예정
        progressDialog = new ProgressDialog(mContext);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("로그인 중...");

        progressDialog.show();

        //해당 MySql Server - PHP 파일과 통신 및 데이터를 넘겨주는 객체
        Call<Integer> call = MyApplication.conn.retrofitService.getLogin(compareDataMap);

        // 통신 중
        call.enqueue(new Callback<Integer>() {

            //통신 성공
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                progressDialog.dismiss();
                //반환값이 null일 때 통신 오류 메시지 전송
                if (response.body() == null) {
                    Toast.makeText(mContext, "통신 오류...", Toast.LENGTH_SHORT).show();
                } else {
                    //쿼리를 통해 반환된 유저의 정보
                    int react = response.body();

                    switch (react) {
                                /*
                                통신 성공 로그인 NMapFragment.class로 이동
                                SignIn.class --- no:회원번호 ---> NMapFragment.class
                                이동 후 액티비티 종료
                                */
                        case 1:
                            saveLoginData();
                            Intent intent = new Intent(mContext, TabMain.class);
                            intent.putExtra("user", getID);
                            startActivity(intent);
                            MyApplication.USER_ID = getID;
                            finish();
                            Toast.makeText(mContext, "로그인 성공", Toast.LENGTH_LONG).show();
                            break;
                        //통신 성공 But 아이디 또는 비밀번호가 틀린 경우
                        case 0:
                            Toast.makeText(mContext, "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            //통신 실패
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();
                Toast.makeText(mContext, "로그인 실패\nReason: 통신 미연결", Toast.LENGTH_LONG).show();
            }
        });
    }

    //회원가입
    private void goSignUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editID.setText("");
                editPWD.setText("");

                DialogSet ds = new DialogSet(mContext);
                ds.sendEmailDialog();

            }
        });
    }

    //자동 로그인 및 SharedPreferences
    private void saveLoginData() {

        if (autoLogin.isChecked()) {
            String getID = editID.getText().toString();
            String getPWD = editPWD.getText().toString();

            editor.putString("id", getID);
            editor.putString("pwd", getPWD);
            editor.putBoolean("autoLogin_enabled", true);

            editor.commit();

        } else {
            //autoLogin.setChecked(false);
            editor.clear();
            editor.commit();
        }

    }


}

