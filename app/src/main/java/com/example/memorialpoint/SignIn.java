package com.example.memorialpoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class LoginData {
    String no;  //로그인 시 DB에서 해당 회원 번호

    public LoginData(String no, String reaction) {
        this.no = no;
        this.reaction = reaction;
    }

    String reaction;    //DB와 통신할 때 반응

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

}

public class SignIn extends AppCompatActivity implements Serializable {

    String TAG = "TAG";
    Context mContext;

    EditText editID, editPWD;   //각각 레이아웃의 ID, PWD의 EditText(이하 eT)
    Button btnSignIn, btnSignUp;    // 각각 레이아웃의 로그인, 회원가입 Button(이하 btn)

    //자동 로그인
    SharedPreferences loginInfo;    //자동로그인 체크박스를 누를 시 editID, editPWD에 입력한 값 유지 및 디바이스 내부에 저장
    SharedPreferences.Editor editor;    //(SharedPreferences) loginInfo의 데이터를 관리
    CheckBox autoLogin;    //체크 시 loginInfo 사용

    ProgressDialog asyncDialog; //로딩 중 다이얼로그
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mContext = this;

        //아이디 비밀번호
        editID = (EditText) findViewById(R.id.editId);
        editPWD = (EditText) findViewById(R.id.editPwd);

        //로그인, 회원가입
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        //자동 로그인 체크박스
        autoLogin = (CheckBox) findViewById(R.id.auto_login);

        //디바이스 내부 저장된 SharedPreferences 파일을 가져온다.
        loginInfo = getSharedPreferences("setting", 0);

        //해당 SharedPreferences 객체의 관리 권한 이양
        editor = loginInfo.edit();

        //불러온 loginInfo의 데이터 확인 값이 없을 시 defValue:false로 반환
        if (loginInfo.getBoolean("autoLogin_enabled", false)) {
            //autoLogin의 값 유지
            autoLogin.setChecked(true);
            //editID, editPWD에 loginInfo에 각각 저장된 id, pwd 값을 입력한다. 실패 시 '실패'라고 값을 반환한다.
            editID.setText(loginInfo.getString("id", "실패"));
            editPWD.setText(loginInfo.getString("pwd", "실패"));
        } else {
            //1.5second 이후 editID, editPWD의 Text를 초기화
            try {
                Thread.sleep(1500);

                editID.setText("");
                editPWD.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //autoLogin의 체크를 false
            autoLogin.setChecked(false);
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

                //R.String.ip => 서버 ip(현재로서는 로컬 네트워크를 이용)
                String ip = getResources().getString(R.string.ip);
                //RetrofitCon.java 인스턴스화 매개 변수 ip
                RetrofitCon conn = new RetrofitCon(ip);

                //editID, editPWD의 입력값
                String getID = editID.getText().toString();
                String getPWD = editPWD.getText().toString();

                //HashMap으로 ID, PWD 관리
                Map map = new HashMap();

                map.put("id", getID);
                map.put("pwd", getPWD);

                //커스텀할 예정
                asyncDialog = new ProgressDialog(mContext);

                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("로그인 중...");

                asyncDialog.show();

                //해당 MySql Server - PHP 파일과 통신 및 데이터를 넘겨주는 객체
                Call<List<LoginData>> call = conn.retrofitService.getLogin(map);

                // 통신 중
                call.enqueue(new Callback<List<LoginData>>() {
                    
                    //통신 성공
                    @Override
                    public void onResponse(Call<List<LoginData>> call, Response<List<LoginData>> response) {

                        asyncDialog.dismiss();
                        //반환값이 null일 때 통신 오류 메시지 전송
                        if (response.body().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "통신 오류...", Toast.LENGTH_SHORT).show();
                        } else {
                            //쿼리를 통해 반환된 유저의 정보
                            List<LoginData> userDataList = response.body();

                            Log.d(TAG, "onResponse: " + userDataList.get(0).reaction);
                            //no:회원번호, reaction:성공여부
                            String no = userDataList.get(0).getNo();
                            Log.d(TAG, "onResponse: " + no);
                            int snum = Integer.parseInt(userDataList.get(0).getReaction());

                            switch (snum) {
                                /*
                                통신 성공 로그인 NMapMain.class로 이동
                                SignIn.class --- no:회원번호 ---> NMapMain.class
                                이동 후 액티비티 종료
                                */
                                case 1:
                                    autoSignIn();
                                    Intent intent = new Intent(getApplicationContext(), NMapMain.class);
                                    intent.putExtra("user", no);
                                    mContext.startActivity(intent);
                                    finish();
                                    break;
                                 //통신 성공 But 아이디 또는 비밀번호가 틀린 경우
                                case 2:
                                    Toast.makeText(getApplicationContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    break;

                            }
                        }
                    }

                    //통신 실패
                    @Override
                    public void onFailure(Call<List<LoginData>> call, Throwable t) {
                        t.printStackTrace();

                        asyncDialog.dismiss();
                        Toast.makeText(mContext, "로그인 실패\nReason: 통신 미연결", Toast.LENGTH_LONG).show();
                    }
                });


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
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                mContext.startActivity(intent);
            }
        });
    }

    //자동 로그인 및 SharedPreferences
    private void autoSignIn() {

        if (autoLogin.isChecked()) {
            String getID = editID.getText().toString();
            String getPWD = editPWD.getText().toString();

            editor.putString("id", getID);
            editor.putString("pwd", getPWD);
            editor.putBoolean("autoLogin_enabled", true);


            editor.commit();


        } else {
            autoLogin.setChecked(false);
            editor.clear();
            editor.commit();
        }

    }
}

