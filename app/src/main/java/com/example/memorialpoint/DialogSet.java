package com.example.memorialpoint;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Models.ResponseData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class DialogSet {

    String TAG = "TAG";

    Context context;
    public Dialog dlg;

    //customDialog
    LinearLayout customDialog_layout;
    TextView title, uploadTime;
    public Button leftButton;
    public Button middleButton;
    public Button rightButton;
    public ImageView centerImg;

    ProgressDialog progressDialog;
    InputMethodManager imm;

    //이메일 전송 다이얼로그
    TextView sendEmail_title;
    EditText sendEmail_editId;
    Button sendEmail_leftBtn, sendEmail_rightBtn;
    Spinner sendEmail_spinnerEmail;

    String emailID, emailAddress;

    //localPost
    public Dialog lpDlg;
    LinearLayout localPostLinear;
    public ImageView postHostImg;
    public TextView postHostID;
    public ImageButton menuIcon;

    public TextView cAddressText;
    public TextView dAddressText;
    public ImageView postImg;

    public TextView contentText;
    TextView addText;

    public DialogSet(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void customDialog(String sTitle) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_custom);

        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        customDialog_layout = (LinearLayout) dlg.findViewById(R.id.customDialog_layout);
        title = (TextView) dlg.findViewById(R.id.title);
        leftButton = (Button) dlg.findViewById(R.id.leftButton);
        middleButton = (Button) dlg.findViewById(R.id.middleButton);
        rightButton = (Button) dlg.findViewById(R.id.rightButton);
        centerImg = (ImageView) dlg.findViewById(R.id.centerImg);
        uploadTime = (TextView) dlg.findViewById(R.id.uploadTime);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int DEVISE_WIDTH, DEVISE_HEIGHT;

        DEVISE_WIDTH = displayMetrics.widthPixels;
        DEVISE_HEIGHT = displayMetrics.heightPixels;

        FrameLayout.LayoutParams params;
        params = new FrameLayout.LayoutParams((DEVISE_WIDTH * 4 / 5), (DEVISE_HEIGHT * 4 / 5));

        customDialog_layout.setLayoutParams(params);

        Rect rect = new Rect();

        title.setText(sTitle);
        title.getPaint().getTextBounds(title.getText().toString(), 0, title.getText().length(), rect);
        title.setTextSize(rect.height());

    }

    public void sendEmailDialog() {

        Dialog seDlg = new Dialog(context);

        seDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        seDlg.setContentView(R.layout.dialog_send_email);

        seDlg.show();

        sendEmail_title = (TextView) seDlg.findViewById(R.id.sendEmail_title);
        sendEmail_editId = (EditText) seDlg.findViewById(R.id.sendEmail_editID);
        sendEmail_spinnerEmail = (Spinner) seDlg.findViewById(R.id.sendEmail_spinnerEmail);
        sendEmail_leftBtn = (Button) seDlg.findViewById(R.id.sendEmail_leftButton);
        sendEmail_rightBtn = (Button) seDlg.findViewById(R.id.sendEmail_rightButton);

        sendEmail_title.setText("이메일 인증");

        Rect rect = new Rect();

        sendEmail_title.getPaint().getTextBounds(sendEmail_title.getText().toString(), 0, sendEmail_title.getText().length(), rect);
        sendEmail_title.setTextSize(rect.height());
        sendEmail_leftBtn.setText("전송");
        sendEmail_rightBtn.setText("취소");

        //이메일 주소
        ArrayAdapter emailAdapter = ArrayAdapter.createFromResource(context,
                R.array.email_address, android.R.layout.simple_spinner_dropdown_item);

        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sendEmail_spinnerEmail.setAdapter(emailAdapter);

        sendEmail_spinnerEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!sendEmail_spinnerEmail.getSelectedItem().toString().equals("---------")) {
                    emailAddress = sendEmail_spinnerEmail.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //이메일 주소

        sendEmail_leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendEmail_leftBtn.getWindowToken(), 0);

                emailID = sendEmail_editId.getText().toString();
                String fullEmail = emailID + "@" + emailAddress;

                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("로그인 중...");
                progressDialog.show();

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(fullEmail).matches()) {

                    //해당 MySql Server - PHP 파일과 통신 및 데이터를 넘겨주는 객체
                    Call<List<ResponseData>> call = MyApplication.conn.retrofitService.getAuthenticationEmail(fullEmail);

                    call.enqueue(new Callback<List<ResponseData>>() {
                        @Override
                        public void onResponse(Call<List<ResponseData>> call, Response<List<ResponseData>> response) {

                            progressDialog.dismiss();

                            String responseData = response.body().get(0).getResponseData();

                            if (responseData.isEmpty() || responseData.equals("exist")) {
                                Toast.makeText(context, "존재하는 이메일입니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, responseData, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(context, SignUp.class);
                                intent.putExtra("email", fullEmail);
                                intent.putExtra("authentication", responseData);
                                context.startActivity(intent);
                                seDlg.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ResponseData>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "통신이 연결되지 않았습니다.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });


                }
            }
        });

        sendEmail_rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendEmail_leftBtn.getWindowToken(), 0);
                Toast.makeText(context, "취소", Toast.LENGTH_LONG).show();
                seDlg.dismiss();
            }
        });
    }

    public void localPost() {

        lpDlg = new Dialog(context);

        lpDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        lpDlg.setContentView(R.layout.dialog_local_post);

        lpDlg.show();

        localPostLinear = (LinearLayout) lpDlg.findViewById(R.id.localPostLinear);
        postHostImg = (ImageView) lpDlg.findViewById(R.id.localPostHostImg);
        postHostID = (TextView) lpDlg.findViewById(R.id.localPostHostID);
        menuIcon = (ImageButton) lpDlg.findViewById(R.id.localMenuIcon);

        cAddressText = (TextView) lpDlg.findViewById(R.id.localCAddressText);
        dAddressText = (TextView) lpDlg.findViewById(R.id.localDAddressText);
        postImg = (ImageView) lpDlg.findViewById(R.id.localPostImg);

        contentText = (TextView) lpDlg.findViewById(R.id.localContentText);
        addText = (TextView) lpDlg.findViewById(R.id.localAddText);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int DEVISE_WIDTH, DEVISE_HEIGHT;

        DEVISE_WIDTH = displayMetrics.widthPixels;
        DEVISE_HEIGHT = displayMetrics.heightPixels;

        FrameLayout.LayoutParams params;
        params = new FrameLayout.LayoutParams((DEVISE_WIDTH * 4 / 5), (DEVISE_HEIGHT * 2/3
        ));
        localPostLinear.setLayoutParams(params);

        lpDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if(addText != null)
        {
            addText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentText.setMaxLines(10);
                    addText.setVisibility(View.GONE);
                }
            });
        }

        if (contentText.isLaidOut()) {
            if (contentText.getLineCount() > 1) {
                contentText.setMaxLines(1);
            }
            else{
                addText.setVisibility(View.GONE);
            }
        } else {
            final TextView postTextView = contentText;

            postTextView.post(new Runnable() {
                @Override
                public void run() {
                    
                    if (postTextView.getLineCount() > 1) {
                        postTextView.setMaxLines(1);
                    }else{
                        addText.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

}

