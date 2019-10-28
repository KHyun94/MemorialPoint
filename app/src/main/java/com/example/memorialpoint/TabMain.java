package com.example.memorialpoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Fragments.NMapFragment;
import com.example.memorialpoint.Fragments.PostFragment;
import com.example.memorialpoint.Fragments.RoomManagerFragment;
import com.example.memorialpoint.Models.ProfileImage;
import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.Models.UserData;
import com.example.memorialpoint.Settings.Setting_permissionCheck;
import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabMain extends AppCompatActivity {

    Context context;
    Activity act;
    String TAG = "Memorial.TabMain.";

    UserData userData = new UserData();

    // 이미지 업로드
    Uri uriPath;
    String absolutePath;
    final int PICK_FROM_ALBUM = 1, PICK_FROM_CAMERA = 2, EDIT_FROM_IMG = 3;

    //사이드 메뉴(Head + Body)
    NavigationView navigationDrawer;
    DrawerLayout drawerLayout;
    ImageButton profileImage;
    TextView showCurrentTimeText, showEmail;
    View headerView;

    //다이얼로그
    DialogSet ds;
    String uploadTimeStr = null;

    //회원 정보 수정
    final int REVISE_DATA = 5;

    //메인
    TabLayout mainTab;
    public static ViewPager mainViewPager;
    public MainAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    //처음 포스트 프래그먼트를 눌렀을 때 한번만 새로고침을 할 수 있게 한다.
    boolean isTab = true;

    //탭레이아웃 + 뷰페이저 자식 뷰의 수
    int numOfPage = 3;

    boolean isSync = false;

    //사이드 메뉴 - 드로어레이아웃을 자식 프래그먼트에게 넘기는 함수
    public DrawerLayout onDrawingSideMenu() {
        return drawerLayout;
    }

    //SwipeRefreshLayout을 자식 프래그먼트에게 넘기는 함수
    public SwipeRefreshLayout onSwipeRefresh() {
        return mSwipeRefreshLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);

        //앱을 켰을 때 쿠키 청소
        MyApplication.clearCookies(this);

        context = this;
        act = this;
        //---------------------------------------------------------------------

        // 사이드 메뉴
        navigationDrawer = (NavigationView) findViewById(R.id.navigationDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // 사이드 메뉴 - Header
        headerView = navigationDrawer.getHeaderView(0);

        // 사이드 메뉴 - Header - 시간
        showCurrentTimeText = (TextView) headerView.findViewById(R.id.showCurrentTimeText); //사이드 메뉴 Header 상단 부분에 시간을 나타낸다.
        showEmail = (TextView) headerView.findViewById(R.id.showEmail); //사이드 메뉴 Header 하단 부분에 유저의 이름과 이메일을 나타낸다.
        profileImage = (ImageButton) headerView.findViewById(R.id.profileImage);

        //프로필 이미지 가져오기
        getProfile(MyApplication.USER_ID);

        //회원 정보 load
        Call<UserData> call = MyApplication.conn.retrofitService.getUser(MyApplication.USER_ID);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                userData = response.body();

                showEmail.setText(userData.getId() + "(" + userData.getName() + ")" + "님 환영합니다." + "\n" + userData.getEmail());
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                t.printStackTrace();
            }
        });

        currentTimeMethod();    //현재 시간을 갱신
        selectedUpload();   //사이드 메뉴 - 프로필 이미지를 클릭할 때 뜨는 이벤트 함수
        setToolbar();   //상단바 설정
        //---------------------------------------------------------------------

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setEnabled(false);

        mainTab = (TabLayout) findViewById(R.id.mainTab);
        mainViewPager = (ViewPager) findViewById(R.id.mainViewPager);

        adapter = new MainAdapter(getSupportFragmentManager(), numOfPage, null);

        mainViewPager.setAdapter(adapter);
        mainViewPager.setCurrentItem(0);

        mainTab.addTab(mainTab.newTab().setIcon(R.drawable.p_map));
        mainTab.addTab(mainTab.newTab().setIcon(R.drawable.p_post_list));

        //메인 액티비티 하단 3번째 탭
        mainTab.addTab(mainTab.newTab().setIcon(R.drawable.p_chatting_icon));
        mainTab.setTabGravity(TabLayout.GRAVITY_FILL);

        mainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTab));

        mainTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewPager.setCurrentItem(tab.getPosition());

                Log.d(TAG + "Tab_Num", "What is Tab Num: " + tab.getPosition());

                if (tab.getPosition() == 0) {
                    mSwipeRefreshLayout.setEnabled(false);
                    if (drawerLayout != null) {
                        //네비게이션 드로어를 드로잉 못하게 막는 것
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }
                } else {
                    mSwipeRefreshLayout.setEnabled(true);
                    if (drawerLayout != null) {
                        //네비게이션 드로어를 드로잉 못하게 막는 것
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }

                if (isTab && tab.getPosition() == 1) {

                    mainViewPager.getAdapter().notifyDataSetChanged();
                    Log.d(TAG, "refresh");
                    isTab = false;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //포스트 탭에 한에서 포스트 페이지에서 포스트 탭을 한번 더 누를 경우 새로고침
                if(tab.getPosition() == 1){
                    mainViewPager.getAdapter().notifyDataSetChanged();
                    Log.d(TAG, "Double Refresh");
                }
            }
        });

        //키보드가 뷰나 위젯 안가리게 설정
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG + "Menu", "Value: " + item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {

            if(mainTab.getSelectedTabPosition() != 0){
                mainViewPager.setCurrentItem(0);
            }else{
                super.onBackPressed();
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }

        }
    }

    public void setToolbar() {

        //네비게이션 드로어를 드로잉 못하게 막는 것
        Log.d(TAG + "setToolbar", "setToolbar");

        //사이드 메뉴 설정 - body part
        navigationDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Log.d(TAG + "setToolbar", "navigationDrawer의 아이템 클릭 리스너");

                drawerLayout.closeDrawers();

                // 각 메뉴 클릭시 이뤄지는 이벤트
                switch (item.getItemId()) {
                    case R.id.navigation_item_profile:
                        Log.d(TAG + "setToolbar", "Item:profile click");

                        //프로필 화면으로 넘어간다.
                        showProfile();
                        break;

                    case R.id.navigation_item_my_post:

                        //My Post 화면으로 넘어간다.
                        Log.d(TAG + "setToolbar", "Item:My Post");
                        Intent intent = new Intent(getApplicationContext(), MyPost.class);
                        startActivity(intent);
                        break;

                    case R.id.navigation_item_sign_out:

                        Log.d(TAG + "setToolbar", "Item:sign out click");

                        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialog_Style);
                        alert.setTitle("로그아웃");
                        alert.setMessage("로그아웃하시겠습니까?");
                        alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //로그아웃 시 저장해둔 로그인 데이터를 지운다.
                                SharedPreferences loginInfo = getSharedPreferences("login", 0);
                                SharedPreferences.Editor editor = loginInfo.edit();

                                editor.clear();
                                editor.commit();

                                //이때까지 쌓인 스택 정리
                                Intent intent = new Intent(context, SignIn.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }).setNegativeButton("아니오", null).show();

                        break;
                }

                return true;
            }
        });
    }

    //현재 시간을 구하는 함수
    public String currentTime() {
        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        return sdf.format(date);
    }

    //메뉴 Header 상단 시간 출력
    public void currentTimeMethod() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);

                        if (getApplicationContext() != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showCurrentTimeText.setText("현재 시간 " + currentTime());
                                }
                            });
                        }

                    } catch (InterruptedException e) {
                        showCurrentTimeText.setText("현재 시간을 출력할 수 없습니다.");
                    }
                }


            }
        });
        thread.start();
    }

    //프로필 화면으로 넘어가는 함수
    public void showProfile() {
        Log.d(TAG + "showProfile", "Go Profile");

        Intent intent = new Intent(context, Profile.class);
        //가지고 있는 유저의 데이터를 넘긴다.
        intent.putExtra("user", userData);
        startActivityForResult(intent, REVISE_DATA);
    }

    //프로필 사진 업로드,

    // 이미지 저장을 위한 디렉토리 생성 함수
    public File createDir() {

        Log.d(TAG + "createDir", "디렉토리 생성");

        //저장될 파일의 이름
        String imgName = "mePo_" + System.currentTimeMillis() + ".jpg";

        //저장 디렉토리 주소
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MemorialPoint_Photo");

        //디렉토리가 없을 시 생성
        if (!storageDir.exists())
            storageDir.mkdirs();

        //저장될 파일의 주소
        File imgFilePath = new File(storageDir, imgName);
        absolutePath = imgFilePath.getAbsolutePath();

        return imgFilePath;
    }

    // 프로필 사진을 누를 시 프로필 사진 확대한 것과 앨범/카메라/취소 선택 다이얼로그가 출력됩니다.
    public void selectedUpload() {

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG + "selectedUpload", "이미지 클릭");

                //권한
                Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(context, act);

                //권한 요청에 따른 Positive Negative
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        ds = new DialogSet(context);

                        ds.customDialog("프로필 사진");

                        ds.centerImg.setImageBitmap(MyApplication.PROFILE_IMAGE);

                        ds.leftButton.setText("앨범");
                        ds.middleButton.setText("카메라");
                        ds.rightButton.setText("취소");

                        //이미지가 등록되고 나서의 시간
                        if (ds.centerImg.getDrawable() != null || ds.centerImg.getDrawable() != context.getResources().getDrawable(R.drawable.p_nmap_blank_person)) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

                            try {
                                Date currentTime = simpleDateFormat.parse(currentTime());

                                Date saveTime;
                                if(uploadTimeStr != null)
                                   saveTime = simpleDateFormat.parse(uploadTimeStr);
                                else
                                    saveTime = simpleDateFormat.parse(currentTime());

                                long sTime = (currentTime.getTime() - saveTime.getTime()) / 1000;
                                long mTime = sTime / 60;
                                long hTime = mTime / 60;
                                long dTime = hTime / 24;

                                if (sTime < 60)
                                    ds.uploadTime.setText(sTime + "seconds ago");
                                else if (mTime < 60) {
                                    ds.uploadTime.setText(mTime + "minutes ago");
                                } else if (hTime < 24)
                                    ds.uploadTime.setText(hTime + "hours ago");
                                else if (dTime < 31)
                                    ds.uploadTime.setText(dTime + "days ago");
                                else
                                    ds.uploadTime.setText("A long time ago...");

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "onPermissionGranted: " + e.getMessage());
                                ds.uploadTime.setText("Time Error");
                            }
                        }

                        //앨범 버튼 클릭
                        ds.leftButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.d(TAG + "selectedUpload", "앨범 클릭");

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                startActivityForResult(intent, PICK_FROM_ALBUM);

                                ds.dlg.dismiss();
                            }
                        });

                        ds.middleButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.d(TAG + "selectedUpload", "카메라 클릭");

                                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                                        File photoFile = createDir();

                                        if (photoFile != null) {

                                            //디바이스의 sdk 버전에 따라 이미지 파일 저장 방식의 분기
                                            if (Build.VERSION.SDK_INT >= 24) {
                                                Log.d(TAG + "selectedUpload", "onClick: 24버전 이상");
                                                Uri providerPath = FileProvider.getUriForFile
                                                        (context, getApplicationContext().getPackageName() + ".file_provider", photoFile);

                                                uriPath = providerPath;
                                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                                                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                                            } else {
                                                Log.d(TAG + "selectedUpload", "onClick: 24버전 이하");
                                                uriPath = Uri.fromFile(photoFile);
                                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                                                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "외장 메모리 미지원", Toast.LENGTH_LONG).show();
                                }

                                ds.dlg.dismiss();
                            }
                        });

                        //취소 버튼
                        ds.rightButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG+ "selectedUpload", "취소");
                                ds.dlg.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Log.d(TAG+ "selectedUpload", "권한 거부");
                    }
                };

                setting_permissionCheck.setPermissionListener_camera(permissionListener);
            }
        });
    }

    //서버 내의 프로필 이미지를 가져온다.
    public void getProfile(String id) {

        Log.d(TAG + "getProfile", "프로필 이미지 로드");

        Call<ProfileImage> call = MyApplication.conn.retrofitService.getProfile(id);

        Drawable emptyImage = getResources().getDrawable(R.drawable.p_nmap_blank_person, context.getTheme());

        if (profileImage.getDrawable().getConstantState() == emptyImage.getConstantState()
                || profileImage.getDrawable() == null) {

            Log.d(TAG+"getProfile", "이미지가 빈값일 때");


            call.enqueue(new Callback<ProfileImage>() {
                @Override
                public void onResponse(Call<ProfileImage> call, Response<ProfileImage> response) {

                    if(response.body() != null){

                        userData.setProfile(response.body());

                        String path = MyApplication.ip + "memorial_point" + userData.getProfile().getProfile().substring(2);
                        uploadTimeStr = userData.getProfile().getDate();
                        userData.getProfile().setProfile(path);

                        Thread profileT = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL tmpURL = new URL(userData.getProfile().getProfile());
                                    InputStream in = tmpURL.openStream();
                                    MyApplication.PROFILE_IMAGE = BitmapFactory.decodeStream(in);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        try {
                            profileT.start();

                            URL tmpURL = new URL(userData.getProfile().getProfile());
                            MyApplication.ButtonCircuitCrop(context, tmpURL, profileImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                            MyApplication.PROFILE_IMAGE = ((BitmapDrawable) emptyImage).getBitmap();
                            MyApplication.ButtonCircuitCrop(context, emptyImage, profileImage);
                        }
                    }


                }

                @Override
                public void onFailure(Call<ProfileImage> call, Throwable t) {
                    MyApplication.ButtonCircuitCrop(context, emptyImage, profileImage);
                    MyApplication.PROFILE_IMAGE = ((BitmapDrawable) emptyImage).getBitmap();
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        Log.d(TAG + "onActivityResult", "선택 반환 번호: " + requestCode);
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                try {
                    //앨범에서 이미지를 선택했을 때
                    Uri imgUri = data.getData();

                    Log.d(TAG + "onActivityResult", "앨범 이미지: " + imgUri.toString());

                    Intent editIntent = new Intent(context, ImageEditor.class);
                    editIntent.putExtra("imgUri", imgUri);
                    startActivityForResult(editIntent, EDIT_FROM_IMG);

                    break;
                } catch (Exception e) {
                    break;
                }

            case PICK_FROM_CAMERA:
                try {
                    //카메라로 이미지를 촬영한 값

                    Intent syncIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File absolutionFile = new File(absolutePath);

                    uriPath = Uri.fromFile(absolutionFile);

                    Log.d(TAG + "onActivityResult", "카메라 이미지: " + uriPath.toString());

                    //디바이스 갤러리의 동기화 작업
                    syncIntent.setData(uriPath);
                    context.sendBroadcast(syncIntent);

                    if (Build.VERSION.SDK_INT >= 24) {
                        Log.d(TAG, "onClick: 24버전 이상");
                        Uri providerPath = FileProvider.getUriForFile
                                (context, getPackageName() + ".file_provider", absolutionFile);

                        uriPath = providerPath;

                        syncIntent.setData(uriPath);
                        context.sendBroadcast(syncIntent);
                    } else {
                        Log.d(TAG, "onClick: 24버전 이하");
                        uriPath = Uri.fromFile(absolutionFile);

                        syncIntent.setData(uriPath);
                        context.sendBroadcast(syncIntent);
                    }

                    Intent intent_edit = new Intent(context, ImageEditor.class);
                    intent_edit.putExtra("imgUri", uriPath);
                    startActivityForResult(intent_edit, EDIT_FROM_IMG);

                    break;
                } catch (Exception e) {
                    break;
                }

            case EDIT_FROM_IMG:

                //이미지 편집 화면에서 돌아온 값
                try {
                    Uri resultUri = data.getParcelableExtra("result");

                    Log.d(TAG + "onActivityResult", "결과 이미지: " + resultUri.toString());

                    if (resultUri != null) {

                        Intent sync = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        sync.setData(resultUri);
                        sync.setData(resultUri);
                        context.sendBroadcast(sync);

                        //서버로 이미지 업로드
                        MethodSet.uploadImg(context, resultUri);

                        try {
                            uploadTimeStr = currentTime();
                            MyApplication.ButtonCircuitCrop(context, resultUri, profileImage);
                            MyApplication.PROFILE_IMAGE = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(context, "이미지가 정상적으로 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "이미지가 정상적으로 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case REVISE_DATA:
                userData = (UserData) data.getSerializableExtra("revise_user");
                break;

        }
    }

    public void syncMap(){
        adapter.setSync(true);
        mainViewPager.getAdapter().notifyDataSetChanged();
    }
}

class MainAdapter extends FragmentStatePagerAdapter {

    int numOfPage;
    Bundle data;
    boolean isSync = false;

    public  void setSync(boolean sync) {
        isSync = sync;
    }

    public MainAdapter(FragmentManager fm, int numOfPage, Bundle data) {
        super(fm);
        this.numOfPage = numOfPage;
        this.data = data;
    }

    //프래그먼트 갱신안되게
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        if(isSync && position == 0 ){
            super.destroyItem(container, position, object);
            isSync = false;
        }

        if( position == 1){
            super.destroyItem(container, position, object);
        }
        //super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NMapFragment NMapFragment = new NMapFragment();
                return NMapFragment;
            case 1:
                PostFragment postFragment = new PostFragment();
                return postFragment;
            case 2:
                RoomManagerFragment roomManagerFragment= new RoomManagerFragment();
                return roomManagerFragment;
            default:
                return null;
        }
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

