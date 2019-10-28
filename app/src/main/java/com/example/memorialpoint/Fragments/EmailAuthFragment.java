package com.example.memorialpoint.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;
import com.example.memorialpoint.SignUp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailAuthFragment extends android.support.v4.app.Fragment implements SignUp.onKeyBackPressListener {

    LinearLayout linearLayout;
    RelativeLayout relativeLayout1, relativeLayout2;
    TextView titleText, resendText, timerText;
    EditText certificationNum;
    Button certificationBtn;

    int HOURS = 2;
    int MINUTES = 60;
    boolean isValue = true;

    String TAG = "TAG";
    String authenticationStr;
    String email;

    public EmailAuthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null) {
            authenticationStr = (String) this.getArguments().getString("authentication");
            email = (String) this.getArguments().getString("email");
        }

        ((SignUp) context).setOnKeyBackPressListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_fragment1, container, false);

        relativeLayout1 = (RelativeLayout) view.findViewById(R.id.signUpFrag1_Relative1);
        relativeLayout2 = (RelativeLayout) view.findViewById(R.id.signUpFrag1_Relative2);
        resendText = (TextView) view.findViewById(R.id.signUpFrag1_resend);
        certificationNum = (EditText) view.findViewById(R.id.signUpFrag1_certificationEditNum);
        certificationBtn = (Button) view.findViewById(R.id.signUpFrag1_certificationBtn);
        timerText = (TextView) view.findViewById(R.id.signUpFrag1_timer);

        CountTimer().start();

        certificationNum.setText(authenticationStr);

        resendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValue) {
                    isValue = true;
                    timerText.setBackgroundColor(Color.WHITE);
                }

                HOURS = 2;
                MINUTES = 60;

                //해당 MySql Server - PHP 파일과 통신 및 데이터를 넘겨주는 객체
                Call<List<ResponseData>> call = MyApplication.conn.retrofitService.getAuthenticationEmail(email);

                call.enqueue(new Callback<List<ResponseData>>() {
                    @Override
                    public void onResponse(Call<List<ResponseData>> call, Response<List<ResponseData>> response) {
                        authenticationStr = response.body().get(0).getResponseData();
                    }

                    @Override
                    public void onFailure(Call<List<ResponseData>> call, Throwable t) {

                        Toast.makeText(getActivity(), "통신이 연결되지 않았습니다.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        });

        certificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isValue = false;

                if (certificationNum != null) {
                    String certificationStr = certificationNum.getText().toString();

                    if (authenticationStr.equals(certificationStr)) {
                        Log.d(TAG, "인증번호: " + authenticationStr + "\n입력 번호: " + certificationStr);
                        isValue = false;

                        ((SignUp) getActivity()).nextPage();
                    } else {
                        Toast.makeText(getActivity(), "인증 번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    public Thread CountTimer() {

        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (isValue) {
                    try {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                MINUTES--;

                                String setTime = String.format("%02d : %02d", HOURS, MINUTES);

                                if (MINUTES > 0) {
                                    timerText.setText(setTime);
                                } else if (MINUTES == 0) {

                                    if (HOURS == 0 && MINUTES == 0) {
                                        authenticationStr = null;
                                        timerText.setBackgroundColor(Color.RED);
                                        timerText.setText(setTime);
                                        isValue = false;
                                        Toast.makeText(getContext(), "시간 초과", Toast.LENGTH_LONG).show();
                                    } else {
                                        HOURS--;
                                        timerText.setText(setTime);
                                        MINUTES = 60;
                                    }

                                }
                            }
                        });

                    } catch (InterruptedException e) {
                    }
                }
            }
        });

        return timerThread;
    }

    @Override
    public void onBack() {
        SignUp signUp = (SignUp) getActivity();
        signUp.setOnKeyBackPressListener(null);
        isValue = false;
    }
}
