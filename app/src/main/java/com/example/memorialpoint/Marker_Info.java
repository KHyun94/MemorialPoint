package com.example.memorialpoint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Fragments.MarkerFragment;
import com.example.memorialpoint.Fragments.MarkerImageFragment;
import com.example.memorialpoint.Fragments.NMapFragment;
import com.naver.maps.geometry.LatLng;

public class Marker_Info extends AppCompatActivity implements MarkerImageFragment.OnSendUriListener {

    Context mContext;

    ViewPager viewPager;
    markerPagerAdapter adapter;

    LinearLayout markerInfo_layout;
    ImageButton preBtn, nextBtn;
    TextView title;

    LatLng latLng;  //선택 위치

    Uri firstFragmentResultUri; //Fragment1의 인터페이스로부터 받아온 Uri 변수

    int numOfPage = 2;  //Count(페이지)

    int cur = 0;    //현재 위치

    Bundle sendBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker__info);

        mContext = this;
        viewPager = (ViewPager) findViewById(R.id.markerViewPager);

        preBtn = (ImageButton) findViewById(R.id.pre_Btn);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        markerInfo_layout = (LinearLayout) findViewById(R.id.markerInfo_layout);
        title = (TextView) findViewById(R.id.markerTitle);

        //초기값
        MethodSet.autoTextSize(title, "이미지 등록");

        //ViewPager adapter 인스턴스 화
        adapter = new markerPagerAdapter(getSupportFragmentManager(), numOfPage, null);
        //ViewPager = adapter
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        latLng = getIntent().getParcelableExtra("latLng");

        //(터치) 초기 위치
        sendBundle = new Bundle();
        sendBundle.putParcelable("latLng", latLng);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                pageIndexChanged(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    //Marker_Info 하단 페이지 수 변경 메소드
    public void pageIndexChanged(int cur) {

        Thread textChangedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (cur) {
                            case 0:
                                title.setText("이미지 등록");
                                break;
                            case 1:
                                title.setText("새 포스트 작성");
                                break;
                        }
                    }
                });
            }
        });

        textChangedThread.start();
    }

    public void onClick(View v) {

        //버튼(preBtn, nextBtn)
        int view = v.getId();

        cur = viewPager.getCurrentItem();
        switch (view) {
            case R.id.pre_Btn:

                if (cur > 0) {
                    //현재 위치가 첫 번째 페이지가 아닐 때
                    viewPager.setCurrentItem(cur - 1, true);
                    nextBtn.setVisibility(View.VISIBLE);
                } else if (cur == 0) {
                    //현재 위치가 첫 번째 페이지일 때
                    Toast.makeText(getApplicationContext(), "첫 페이지입니다. 작성을 취소하시겠습니까?", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialog_Style);

                    alert.setIcon(R.drawable.p_alert_dialog_icon)
                            .setTitle("작성 취소")
                            .setMessage("등록 중인 데이터는 삭제됩니다.")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //클릭 시 메인 화면으로 넘어간다.
                                    Intent intent = new Intent(mContext, NMapFragment.class);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }).setNegativeButton("아니오", null).show();
                }
                break;

            case R.id.nextBtn:
                cur = viewPager.getCurrentItem();

                if (cur < numOfPage - 1) {
                    //현재 위치가 마지막 페이지보다 작을 때
                    //이미지 등록 시
                    if (firstFragmentResultUri != null) {
                        //이미지 있으면 해당 아이디, uri와 LatLng을 Bundle로 값을 넘긴다.
                        //Fragment2로 넘어간다.
                        adapter = new markerPagerAdapter(getSupportFragmentManager(), numOfPage, sendBundle);
                        viewPager.setAdapter(adapter);
                        viewPager.setCurrentItem(cur + 1, true);

                        nextBtn.setVisibility(View.GONE);
                    } else {
                        //등록이미지 없을 시 넘어가지 않던가, 없는 상태로 넘어간다.
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialog_Style);
                        alert.setMessage("이미지를 등록해주세요.")
                                .setNeutralButton("확인", null)
                                .show();
                    }
                }
                break;
        }
    }

    @Override
    public void onSendUri(Uri uri) {
        try {
            if (uri != null) {
                firstFragmentResultUri = uri;
                sendBundle.putParcelable("sendUri", firstFragmentResultUri);
            } else
                Toast.makeText(getApplicationContext(), "이미지가 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}

class markerPagerAdapter extends FragmentStatePagerAdapter {

    int numOfPage;
    private Bundle pathBundle;

    public markerPagerAdapter(FragmentManager fm, int numOfPage, Bundle data) {
        super(fm);
        this.numOfPage = numOfPage;
        this.pathBundle = data;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                MarkerImageFragment fragment_first = new MarkerImageFragment();
                return fragment_first;
            case 1:
                MarkerFragment fragment_second = new MarkerFragment();
                fragment_second.setArguments(pathBundle);
                return fragment_second;
            default:
                return null;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return numOfPage;
    }

}

