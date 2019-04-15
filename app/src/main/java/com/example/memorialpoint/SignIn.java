package com.example.memorialpoint;

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

class Login_Data {
    String no;
    String value;

    public Login_Data(String no, String value) {
        this.no = no;
        this.value = value;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

public class SignIn extends AppCompatActivity implements Serializable {
    String TAG = "TAG";
    int value;
    Context mContext = this;


    // DB_loadUser에서 받아온 값을 저장
    ArrayList<Data_User> memberDataArrayList;

    // 로그인 성공한 user의 값
    Data_User sendInfo;


    EditText editID, editPWD;
    Button btnSignIn, btnSignUp;

    //자동 로그인
    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;
    CheckBox auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //DB에서 받아온 유저들의 데이터
        DB_loadUser DBLoadUser = new DB_loadUser();
        String ip = getResources().getString(R.string.ip) + "/";
        //   memberDataArrayList = DBLoadUser.parsingJSON(ip);

        //아이디 비밀번호
        editID = (EditText) findViewById(R.id.editId);
        editPWD = (EditText) findViewById(R.id.editPwd);

        //로그인, 회원가입
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        auto_login = (CheckBox) findViewById(R.id.auto_login);
        loginInfo = getSharedPreferences("setting", 0);
        editor = loginInfo.edit();

        if (loginInfo.getBoolean("auto_login_enabled", false)) {
            auto_login.setChecked(true);
            editID.setText(loginInfo.getString("id", "실패"));
            editPWD.setText(loginInfo.getString("pwd", "실패"));
        } else {
            auto_login.setChecked(false);
        }
        //비밀번호 입력 후 enter를 누를 시 로그인 버튼으로 넘어간다.
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

    public void goSignIn() {

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = getResources().getString(R.string.ip);
                RetrofitCon conn = new RetrofitCon(ip);

                String getID = editID.getText().toString();
                String getPWD = editPWD.getText().toString();

                Map map = new HashMap();

                map.put("id", getID);
                map.put("pwd", getPWD);

                Call<List<Login_Data>> call = conn.retrofitService.getLogin(map);

                call.enqueue(new Callback<List<Login_Data>>() {
                    @Override
                    public void onResponse(Call<List<Login_Data>> call, Response<List<Login_Data>> response) {

                        Log.d(TAG, "onResponse: " + response.body());
                        if(response.body().isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "통신 오류...", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            List<Login_Data> dataList = response.body();
                            String no = dataList.get(0).getNo();
                            int snum = Integer.parseInt(dataList.get(0).getValue());

                            switch (snum) {
                                case 0:
                                    Log.d(TAG, "onClick: 무슨일이야");
                                    break;
                                case 1:
                                    autoSignIn();
                                    Log.d(TAG, "onClick: 실행");
                                    Intent intent = new Intent(getApplicationContext(), NMapMain.class);
                                    intent.putExtra("user", no);
                                    mContext.startActivity(intent);
                                    finish();
                                    break;
                                case 2:
                                    Toast.makeText(getApplicationContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Login_Data>> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getApplicationContext(), "통신 오류", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    /*//로그인
    private void goSignIn() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    switch (value)
                    {
                        case 0:
                            Log.d(TAG, "onClick: 무슨일이야");
                            break;
                        case 1:
                            autoSignIn();
                            Log.d(TAG, "onClick: 살행");
                        *//*Intent intent = new Intent(getApplicationContext(), NMapMain.class);
                        intent.putExtra("user", sendInfo);
                        mContext.startActivity(intent);*//*
                            //finish();
                            break;
                        case 2:
                            Toast.makeText(getApplicationContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(getApplicationContext(), "통신에 문제가 생겼습니다..", Toast.LENGTH_SHORT).show();
                            break;

                    }

                Log.d(TAG, "onClick: " + value);



                *//*try {
                    if (getID.isEmpty() || getPWD.isEmpty()) {
                        if (getID.isEmpty() && getPWD.isEmpty())
                            Toast.makeText(getApplicationContext(), "아이디와 패스워드를 입력해주십시오.", Toast.LENGTH_SHORT).show();
                        else if (getID.isEmpty())
                            Toast.makeText(getApplicationContext(), "아이디를 입력해주십시오.", Toast.LENGTH_SHORT).show();
                        else if (getPWD.isEmpty())
                            Toast.makeText(getApplicationContext(), "패스워드를 입력해주십시오.", Toast.LENGTH_SHORT).show();
                    } else {
                        int check = 2;

                        for (int i = 0; i < memberDataArrayList.size(); i++) {
                            if (getID.equals(memberDataArrayList.get(i).getId()) && getPWD.equals(memberDataArrayList.get(i).getPassword())) {
                                sendInfo = memberDataArrayList.get(i);
                                check = 1;
                                break;
                            } else {
                                if ((!getID.equals(memberDataArrayList.get(i).getId()) && !getPWD.equals(memberDataArrayList.get(i).getPassword())))
                                    check = 2;
                                else if (getID.equals(memberDataArrayList.get(i).getId()) && !getPWD.equals(memberDataArrayList.get(i).getPassword()))
                                    check = 3;


                            }
                        }

                        if (check == 1) {
                            autoSignIn();
                            Intent intent = new Intent(getApplicationContext(), NMapMain.class);
                            intent.putExtra("user", sendInfo);
                            mContext.startActivity(intent);
                            finish();

                        } else if (check == 2)
                            Toast.makeText(getApplicationContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                        else if (check == 3)
                            Toast.makeText(getApplicationContext(), "패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }*//*

            }
        });
    }
*/


    //회원가입
    private void goSignUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editID.setText("");
                editPWD.setText("");
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                intent.putExtra("user", memberDataArrayList);
                mContext.startActivity(intent);
            }
        });
    }

    //자동 로그인 및 SharedPreferences
    private void autoSignIn() {

        if (auto_login.isChecked()) {
            String getID = editID.getText().toString();
            String getPWD = editPWD.getText().toString();

            editor.putString("id", getID);
            editor.putString("pwd", getPWD);
            editor.putBoolean("auto_login_enabled", true);


            editor.commit();


        } else {
            auto_login.setChecked(false);
            editor.clear();
            editor.commit();
        }

    }
}

