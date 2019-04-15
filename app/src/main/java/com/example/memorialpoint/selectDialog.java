package com.example.memorialpoint;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class selectDialog {

    private Context context;
    Dialog dlg;
    LinearLayout customDialog_layout;
    TextView title, upload_Img_time;
    Button leftButton, middleButton, rightButton;
    ImageView centerImg;
    public selectDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String sTitle) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.select_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        customDialog_layout = (LinearLayout) dlg.findViewById(R.id.customDialog_layout);
        title = (TextView) dlg.findViewById(R.id.title);
        leftButton = (Button) dlg.findViewById(R.id.leftButton);
        middleButton = (Button) dlg.findViewById(R.id.middleButton);
        rightButton = (Button) dlg.findViewById(R.id.rightButton);
        centerImg = (ImageView) dlg.findViewById(R.id.centerImg);
        upload_Img_time = (TextView) dlg.findViewById(R.id.upload_Img_time);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int DEVISE_WIDTH, DEVISE_HEIGHT;

        DEVISE_WIDTH = displayMetrics.widthPixels;
        DEVISE_HEIGHT = displayMetrics.heightPixels;

        FrameLayout.LayoutParams params;
        params = new FrameLayout.LayoutParams((DEVISE_WIDTH * 4/5), (DEVISE_HEIGHT * 4/5));

        customDialog_layout.setLayoutParams(params);

        Rect rect = new Rect();

        title.setText(sTitle);
        title.getPaint().getTextBounds(title.getText().toString(), 0 , title.getText().length(), rect);
        title.setTextSize(rect.height());

    }

}