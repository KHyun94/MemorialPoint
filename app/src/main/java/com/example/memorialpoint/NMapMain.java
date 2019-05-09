package com.example.memorialpoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gun0912.tedpermission.PermissionListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NMapMain extends AppCompatActivity implements OnMapReadyCallback {

    // 앱 전반적 데이터
    Activity act;
    Context mContext;
    Data_User receiveInfo; // 회원 정보 from [액티비티 SignIn]
    String TAG = "TAG";

    //MapClik 제어 및 마커
    boolean CONTROL_MAPCLICK = true;
    int GET_MARKER = 10;

    //Naver 지도
    FrameLayout nmap;   //네이버 맵을 띄우는 역할
    NaverMap getNmap;   //네이버 객체 받아오는 역할
    LinearLayout uiLayout;  //맵위에 떠있는 UI(위젯 + 툴바)를 포함한 레이아웃
    boolean show_hide_value = true; //UI를 제외한 지도를 누를 시 UI를 숨김 표시하는 값 - > true: floating / false: non-floating

    // 이미지 업로드
    Uri uriPath;
    String absolutePath;
    final int PICK_FROM_ALBUM = 1, PICK_FROM_CAMERA = 2, EDIT_FROM_IMG = 3;
    Bitmap bm;

    //사이드 메뉴(Head + Body)
    NavigationView navigationDrawer;
    DrawerLayout drawerLayout;
    ImageButton profile_Photo;
    TextView showCurrentTimeText, showEmail;
    View headerView;

    //회원 정보 수정
    final int REVISE_DATA = 5;

    //권한
    boolean permission_value = false;

    //UI
    android.support.v7.widget.Toolbar toolbar;
    FloatingActionButton fab;
    ImageButton action_menu;
    EditText search_edit; //Floating EditText

    //GPS DATA
    CameraAnimation cameraAnimation;
    LocationOverlay locationOverlay;
    CameraPosition cameraPosition;
    Setting_GPSInfo settingGpsInfo;
    LatLng latLng;

    //다이얼로그
    selectDialog sd;
    String upload_img_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap__main);

        mContext = this;
        act = this;

        // 회원 정보 from Act[SignIn]
        Intent intent = getIntent();
        String no = intent.getExtras().getString("user");

        String ip = getResources().getString(R.string.ip);
        RetrofitCon conn = new RetrofitCon(ip);

        Call<List<Data_User>> call = conn.retrofitService.getUser(no);

        call.enqueue(new Callback<List<Data_User>>() {
            @Override
            public void onResponse(Call<List<Data_User>> call, Response<List<Data_User>> response) {
                List<Data_User> dataList = response.body();
                receiveInfo = dataList.get(0);

                // 처음 아무것도 없을 때 사진
                if (profile_Photo.getDrawable() == null) {
                    if (receiveInfo.getSex().equals("Male"))
                        profile_Photo.setImageDrawable(getResources().getDrawable(R.drawable.p_nmap_blank_male, getApplicationContext().getTheme()));
                    else
                        profile_Photo.setImageDrawable(getResources().getDrawable(R.drawable.p_nmap_blank_female, getApplicationContext().getTheme()));
                }

                showEmail.setText(receiveInfo.getId() + "(" + receiveInfo.getName() + ")" + "님 환영합니다." + "\n" + receiveInfo.getEmail());

            }

            @Override
            public void onFailure(Call<List<Data_User>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // 네이버 지도
        nmap = (FrameLayout) findViewById(R.id.nmap);

        NaverMapSdk.getInstance(mContext).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("fylal7uvmf"));

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.nmap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.nmap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        // 사이드 메뉴
        navigationDrawer = (NavigationView) findViewById(R.id.navigationDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        // 사이드 메뉴 - Header
        headerView = navigationDrawer.getHeaderView(0);

        // 사이드 메뉴 - Header - 시간
        showCurrentTimeText = (TextView) headerView.findViewById(R.id.showCurrentTimeText); //사이드 메뉴 Header 상단 부분에 시간을 나타낸다.
        showEmail = (TextView) headerView.findViewById(R.id.showEmail); //사이드 메뉴 Header 하단 부분에 유저의 이름과 이메일을 나타낸다.
        profile_Photo = (ImageButton) headerView.findViewById(R.id.profile_Photo);

        currentTimeMethod();    //현재 시간을 갱신


        selectedUpload();

        // UI
        uiLayout = (LinearLayout) findViewById(R.id.uiLinear);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        action_menu = (ImageButton) findViewById(R.id.action_btn);
        search_edit = (EditText) findViewById(R.id.search_edit);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setToolbar();


    }

    //네이버 맵 출력 및 출력 이후 이벤트
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        //권한
        Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(mContext, act);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 허가되었을 시 현재 위치로 이동
                presentedLocation();
                permission_value = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 허가되지 않았을 때
                permission_value = false;
            }
        };

        setting_permissionCheck.setPermissionListener_location(permissionListener);

        //Floating Action Button 누를 시 현재 위치로 이동
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //권한 허가 시 실행, 권한 미허가 시 권한 요청 메시지 재요청
                if (permission_value)
                    presentedLocation();
                else
                    setting_permissionCheck.setPermissionListener_location(permissionListener);
            }
        });
        getNmap = naverMap;

        //Naver지도 API 내의 UI 제거
        UiSettings uiSettings = getNmap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);

        //Naver지도를 클릭 시 떠있는 UI 감추기
        getNmap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

                Log.d(TAG, "onMapClickaa: " + CONTROL_MAPCLICK);
                if (CONTROL_MAPCLICK) {
                    show_hide_value = !show_hide_value;

                    if (!show_hide_value)
                        uiLayout.setVisibility(View.INVISIBLE);
                    else
                        uiLayout.setVisibility(View.VISIBLE);

                } else {

                    cameraPosition = new CameraPosition(latLng, 15);
                    cameraAnimation = CameraAnimation.Easing;
                    getNmap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(cameraAnimation, 500).finishCallback(new CameraUpdate.FinishCallback() {
                        @Override
                        public void onCameraUpdateFinish() {
                            selectDialog sd = new selectDialog(mContext);
                            sd.callFunction("마커 등록");
                            sd.centerImg.setVisibility(View.GONE);
                            sd.leftButton.setText("등록");
                            sd.leftButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(mContext, act);

                                    PermissionListener permissionListener = new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted() {
                                            Intent intent = new Intent(getApplicationContext(), Marker_Info.class);
                                            intent.putExtra("latLng", latLng);
                                            startActivityForResult(intent, GET_MARKER);
                                        }

                                        @Override
                                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                            Toast.makeText(getApplicationContext(), "카메라 권한 및 위치 권한이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    };

                                    setting_permissionCheck.setPermissionListener_camera(permissionListener);


                                }
                            });
                            sd.middleButton.setVisibility(View.GONE);
                            sd.rightButton.setText("취소");
                            sd.rightButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CONTROL_MAPCLICK = true;
                                    toolbar.getMenu().getItem(2).setIcon(R.drawable.p_nmap_action_marker);
                                    sd.dlg.dismiss();
                                }
                            });
                        }
                    }));
                }

            }
        });
    }

    //현재 위치
    public void presentedLocation() {


        double latitude, longitude;

        try {
            //GPS 요청
            settingGpsInfo = new Setting_GPSInfo(mContext);

            //Gps에서 좌표를 가져왔는지 아닌지 확인
            if (settingGpsInfo.isGetLocation) {
                latitude = settingGpsInfo.getLatitude();   //위도
                longitude = settingGpsInfo.getLongitude(); //경도
                latLng = new LatLng(latitude, longitude);   //LatLng에 값 대입

                Log.d(TAG, "onMapReady GPS 접근 권한: " + settingGpsInfo.isGetLocation);
                Log.d(TAG, "onMapReady 위도: " + latitude);
                Log.d(TAG, "onMapReady 경도: " + longitude);

            } else {
                Toast.makeText(getApplicationContext(), "위치 실패", Toast.LENGTH_SHORT).show();
            }

            //지도 상 보이는 위치 조정
            cameraPosition = new CameraPosition(latLng, 15);

            locationOverlay = getNmap.getLocationOverlay();    //현재위치 찍어주는 표시
            locationOverlay.setPosition(latLng);
            locationOverlay.setVisible(true);   // locationOverlay 가시성

            cameraAnimation = CameraAnimation.Easing;

            getNmap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition)
                    .animate(cameraAnimation, 500).finishCallback(() -> {
                        settingGpsInfo.stopUsingGPS();
                        //이동 후 GPS 종료
                    }));

        } catch (Exception e) {
            //핸드폰 상 위치 서비스가 꺼져있다면 위치 서비스를 켜라고 요청하는 다이얼로그
            settingGpsInfo.showSettingsAlert();
        }

    }


    //ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nmap_toolbar, menu);
        return true;
    }

    public void setToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //툴바 상단 좌측 메뉴 버튼을 누를 시 네비게이션 드로우 메뉴를 보여준다.
        action_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //네비게이션 드로어를 드로잉 못하게 막는 것
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navigationDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);
                drawerLayout.closeDrawers();

                // 각 메뉴 클릭시 이뤄지는 이벤트
                switch (item.getItemId()) {
                    case R.id.navigation_item_notice:
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    case R.id.navigation_item_profile:
                        showProfile();
                        break;

                    case R.id.navigation_item_favorites:
                        navigationDrawer.inflateMenu(R.menu.drawer_body);
                        break;

                    case R.id.navigation_item_setting:
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    case R.id.navigation_item_sign_out:
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle("로그아웃");
                        alert.setMessage("로그아웃하시겠습니까?");
                        alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                startActivity(intent);
                                ActivityCompat.finishAffinity(act);
                            }
                        });

                        alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        alert.show();

                        break;

                }

                return true;
            }

        });
    }

    //Toolbar에 속한 메뉴
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_marker:
                if (!CONTROL_MAPCLICK) {
                    CONTROL_MAPCLICK = true;
                    item.setIcon(R.drawable.p_nmap_action_marker);
                } else {
                    CONTROL_MAPCLICK = false;
                    item.setIcon(R.drawable.p_nmap_action_marker_click);
                }
                break;
            case R.id.action_search:

                if (search_edit.getVisibility() == View.INVISIBLE)
                    search_edit.setVisibility(View.VISIBLE);
                else
                    search_edit.setVisibility(View.INVISIBLE);
                return true;

            case R.id.action_home:
                Log.d(TAG, "onOptionsItemSelected: " + CONTROL_MAPCLICK);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //마커생성
    public void createMarker() {
        Marker marker = new Marker();
    }


    //뒤로가기
    /*
     * 해야 할 것
     * 1. 한번 누를 시
     * 1-1. 메인 화면에서는 종료에 대한 다이얼로그 출력
     * 1-2. 메인 화면이 아닐 시 뒤로 가기(메인 화면으로)
     * 2. 두번 누를 시
     * 2-1. 종료
     * */
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public String currentTime() {
        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");

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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showCurrentTimeText.setText("현재 시간 " + currentTime());
                            }
                        });
                    } catch (InterruptedException e) {
                        showCurrentTimeText.setText("현재 시간을 출력할 수 없습니다.");
                    }
                }
            }
        });
        thread.start();
    }

    //프로필 정보
    public void showProfile() {
        // bitmap = ((BitmapDrawable)profile_Photo.getDrawable()).getBitmap();
        Intent intent = new Intent(mContext, Profile.class);
        intent.putExtra("user", receiveInfo);
        startActivityForResult(intent, REVISE_DATA);

        Log.d(TAG, "showProfile: " + receiveInfo.getPwd());
    }

    //프로필 사진 업로드,

    // 디렉토리 생성
    public File createDir() {

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

        profile_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(mContext, act);

                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        sd = new selectDialog(mContext);

                        sd.callFunction("프로필 사진");
                        sd.centerImg.setImageDrawable(profile_Photo.getDrawable());

                        sd.leftButton.setText("앨범");
                        sd.middleButton.setText("카메라");
                        sd.rightButton.setText("취소");

                        if (sd.centerImg.getDrawable() != null) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
                            try {
                                Date currentTime = simpleDateFormat.parse(currentTime());
                                Date saveTime = simpleDateFormat.parse(upload_img_time);

                                long sTime = (currentTime.getTime() - saveTime.getTime()) / 1000;
                                long mTime = sTime / 60;
                                long hTime = mTime / 60;
                                long dTime = hTime / 24;

                                if (sTime < 60)
                                    sd.upload_Img_time.setText(sTime + "seconds ago");
                                else if (mTime < 60) {
                                    sd.upload_Img_time.setText(mTime + "minutes ago");
                                } else if (hTime < 24)
                                    sd.upload_Img_time.setText(hTime + "hours ago");
                                else if (dTime < 31)
                                    sd.upload_Img_time.setText(dTime + "days ago");
                                else
                                    sd.upload_Img_time.setText("A long time ago...");

                            } catch (Exception e) {

                            }
                        }

                        sd.leftButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                startActivityForResult(intent, PICK_FROM_ALBUM);

                                /*Intent albumIntent = new Intent();
                                albumIntent.setType("image/*");
                                albumIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(albumIntent, PICK_FROM_ALBUM);*/

                                sd.dlg.dismiss();
                            }
                        });

                        sd.middleButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                                        File photoFile = createDir();

                                        if (photoFile != null) {

                                            if(Build.VERSION.SDK_INT >= 24)
                                            {
                                                Log.d(TAG, "onClick: 24버전 이상");
                                                Uri providerPath = FileProvider.getUriForFile
                                                        (mContext, getApplicationContext().getPackageName() + ".file_provider", photoFile);

                                                uriPath = providerPath;
                                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                                                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "onClick: 24버전 이하");
                                                uriPath = Uri.fromFile(photoFile);
                                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                                                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                                            }


                                        }


                                    }

                                } else {
                                    Toast.makeText(mContext, "외장 메모리 미지원", Toast.LENGTH_LONG).show();
                                }

                                sd.dlg.dismiss();
                            }
                        });

                        sd.rightButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sd.dlg.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                };

                setting_permissionCheck.setPermissionListener_camera(permissionListener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        Log.d(TAG, "onActivityResult: " + requestCode);
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                try {
                    Uri imgUri = data.getData();

                    Intent editIntent = new Intent(mContext, Editing_Img.class);
                    editIntent.putExtra("imgUri", imgUri);
                    startActivityForResult(editIntent, EDIT_FROM_IMG);

                    upload_img_time = currentTime();

                    break;
                } catch (Exception e) {
                    break;
                }

            case PICK_FROM_CAMERA:
                try {
                    Intent syncIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File absolution_file = new File(absolutePath);

                    if(Build.VERSION.SDK_INT >= 24)
                    {
                        Log.d(TAG, "onClick: 24버전 이상");
                        Uri providerPath = FileProvider.getUriForFile
                                (mContext, getApplicationContext().getPackageName() + ".file_provider", absolution_file);

                        uriPath = providerPath;

                    }
                    else
                    {
                        Log.d(TAG, "onClick: 24버전 이하");
                        uriPath = Uri.fromFile(absolution_file);

                    }

                    syncIntent.setData(uriPath);
                    mContext.sendBroadcast(syncIntent);

                    Intent intent_edit = new Intent(mContext, Editing_Img.class);

                    intent_edit.putExtra("imgUri", uriPath);
                    startActivityForResult(intent_edit, EDIT_FROM_IMG);

                    /*try{
                        //uri를 비트맵으로 변환
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        Bitmap bmImg = BitmapFactory.decodeStream(in);
                    }catch(Exception e)
                    {

                    }*/

                    upload_img_time = currentTime();

                    break;
                } catch (Exception e) {
                    break;
                }

           /* case EDIT_FROM_IMG:
                Log.d(TAG, "onActivityResult: aaaaaaaa");

                String uriString = data.getStringExtra("editImg");
                boolean value = data.getBooleanExtra("value", false);

                if(value)
                {
                    String tmpUriString = data.getStringExtra("imgUri");

                    Uri saveUri = Uri.parse(tmpUriString);

                    if(saveUri != null)
                    {
                        //저장 위치
                        File photoFile = createDir();

                        //저장 위치를 uri화
                        Uri provider_uri = Uri.fromFile(photoFile);

                        //intent->crop
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setDataAndType(saveUri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.putExtra("scale", true);
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, provider_uri);
                        mContext.sendBroadcast(intent);
                        intent.setData(saveUri);
           //             startActivity(intent);

                        profile_Photo.setImageURI(saveUri);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "이미지가 정상적으로 등록되지 않았습니다." + uriString, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "취소하셨습니다." + uriString, Toast.LENGTH_LONG).show();
                }


                break;*/
            case REVISE_DATA:
                receiveInfo = (Data_User) data.getSerializableExtra("revise_user");
                break;
        }

    }

}
