package com.example.memorialpoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Marker_Info extends AppCompatActivity {

    Context mContext;
   // List<> markerList = new ArrayList<>();

    ViewPager viewPager;
    FragmentManager fm;
    Marker_fragment_first frag1;
    Marker_fragment_second frag2;
    Marker_fragment_thrid frag3;

    RelativeLayout markerInfo_layout;
    FloatingActionButton preBtn, nextBtn;
    TextView title;

    int numOfPage = 3;
    int SET_MARKER = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker__info);

        mContext = this;
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new pagerAdapter(getSupportFragmentManager(), numOfPage));
        viewPager.setCurrentItem(0);

        frag1 = new Marker_fragment_first();
        frag2 = new Marker_fragment_second();
        frag3 = new Marker_fragment_thrid();

        preBtn = (FloatingActionButton) findViewById(R.id.pre_Btn);
        nextBtn = (FloatingActionButton) findViewById(R.id.nextBtn);
        markerInfo_layout = (RelativeLayout) findViewById(R.id.markerInfo_layout);
        title = (TextView) findViewById(R.id.markerTitle);
        title.setTextSize(markerInfo_layout.getHeight());
        Log.d("TAG", "onCreate: 높이값" + markerInfo_layout.getHeight());

    }

    public void onClick(View v) {
        int view = v.getId();

        switch (view) {
            case R.id.pre_Btn:
                int cur = viewPager.getCurrentItem();

                if (cur > 0)
                    viewPager.setCurrentItem(cur - 1, true);
                else if (cur == 0) {
                    Toast.makeText(getApplicationContext(), "첫 페이지입니다. 작성을 취소하시겠습니까?", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialog_Style);

                    alert.setIcon(R.drawable.p_alert_dialog_icon)
                            .setTitle("작성 취소")
                            .setMessage("등록 중인 데이터는 삭제됩니다.")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, NMapMain.class);
                                    setResult(SET_MARKER, intent);
                                    finish();
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
                break;
            case R.id.nextBtn:
                cur = viewPager.getCurrentItem();

                if (cur < numOfPage - 1)
                    viewPager.setCurrentItem(cur + 1, true);
                else if (cur == numOfPage - 1)
                    Toast.makeText(getApplicationContext(), "마지막 장입니다.", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                Log.d("TAG", "onActivityResult: 11");
                break;

            case 2:
                Log.d("TAG", "onActivityResult: 22");
                break;

        }
    }
}

class pagerAdapter extends FragmentPagerAdapter {


    int numOfPage;

    public pagerAdapter(FragmentManager fm, int numOfPage) {
        super(fm);
        this.numOfPage = numOfPage;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                Marker_fragment_first fragment_first = new Marker_fragment_first();
                return fragment_first;
            case 1:
                Marker_fragment_second fragment_second = new Marker_fragment_second();
                return fragment_second;
            case 2:
                Marker_fragment_thrid fragment_thrid = new Marker_fragment_thrid();
                return fragment_thrid;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numOfPage;
    }
}
