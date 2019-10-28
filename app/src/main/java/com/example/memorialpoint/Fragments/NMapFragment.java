package com.example.memorialpoint.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memorialpoint.DialogSet;
import com.example.memorialpoint.MapSearch;
import com.example.memorialpoint.Marker_Info;
import com.example.memorialpoint.Models.MarkerData;
import com.example.memorialpoint.Models.Place;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;
import com.example.memorialpoint.SearchWebView;
import com.example.memorialpoint.Settings.Setting_GPSInfo;
import com.example.memorialpoint.Settings.Setting_permissionCheck;
import com.example.memorialpoint.TabMain;
import com.example.memorialpoint.Util.CustomPopUpMenu;
import com.gun0912.tedpermission.PermissionListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.MarkerIcons;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback,Serializable {

    // 앱 전반적 데이터
    Activity act;
    Context context;

    String TAG = "Memorial.NMapFragment.";

    //맵을 눌렀을 때 상단바의 유무를 정하는 값
    boolean CONTROL_MAP_CLICK = true;

    //마커를 등록했을 떄 반환하는 값
    final int GET_MARKER = 10;

    View view;

    FrameLayout nMap;   //네이버 맵을 띄우는 역할
    NaverMap tmpNMap;   //네이버 객체 받아오는 역할
    LinearLayout uiLayout;  //맵위에 떠있는 UI(위젯 + 툴바)를 포함한 레이아웃
    boolean show_hide_value = true; //UI를 제외한 지도를 누를 시 UI를 숨김 표시하는 값 - > true: floating / false: non-floating

    //권한
    boolean permissionValue = false;

    //UI
    android.support.v7.widget.Toolbar toolbar;
    FloatingActionButton fab;
    ImageButton actionMenu;

    //GPS DATA
    CameraAnimation cameraAnimation;
    LocationOverlay locationOverlay;
    CameraPosition cameraPosition;
    Setting_GPSInfo settingGpsInfo;
    LatLng latLng;

    //다이얼로그
    DialogSet ds;

    //로그아웃
    SharedPreferences loginInfo;    //자동로그인 체크박스를 누를 시 editID, editPWD에 입력한 값 유지 및 디바이스 내부에 저장
    SharedPreferences.Editor editor;    //(SharedPreferences) loginInfo의 데이터를 관리

    //마커
    ArrayList<Marker> markerList;
    HashMap<Marker, PostData> markerHash;
    PostData rmData;

    CustomPopUpMenu customPop;
    PopupMenu popupMenu;

    //검색 결과 마커
    Marker searchMarker;

    final int SEARCH_PLACE = 333;

    public NMapFragment() {

    }

    DrawerLayout getDrawerLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null && getActivity() instanceof TabMain) {
            getDrawerLayout = ((TabMain) getActivity()).onDrawingSideMenu();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            view = inflater.inflate(R.layout.fragment_nmap_fragment, container, false);
            Log.d(TAG, "NMapFragment View");
        } catch (Exception e) {
            Log.d(TAG, "2");
            e.printStackTrace();
        }

        context = getActivity();
        act = getActivity();

        // UI
        uiLayout = (LinearLayout) view.findViewById(R.id.uiLinear);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        actionMenu = (ImageButton) view.findViewById(R.id.action_btn);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        setHasOptionsMenu(true);

        //마커
        markerList = new ArrayList<>();
        markerHash = new HashMap<>();

        // 네이버 지도
        nMap = (FrameLayout) view.findViewById(R.id.nmap);

        //네이버 서버로부터 지도를 받아온다.
        NaverMapSdk.getInstance(context).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(getResources().getString(R.string.naverMapID)));

        //네이버 지도를 프래그먼트에 띄운다
        com.naver.maps.map.MapFragment mapFragment = (com.naver.maps.map.MapFragment) getChildFragmentManager().findFragmentById(R.id.nmap);

        if (mapFragment == null) {
            mapFragment = com.naver.maps.map.MapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.nmap, mapFragment).commit();
            Log.d(TAG, "맵이 없을 떄 새로 불러오기");
        }

        //불러온 맵을 동기화 처리한다.
        mapFragment.getMapAsync(this);

        //상단바 사이드바 관련 함수
        setToolbar();

        return view;
    }

    //ToolBar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.nmap_toolbar, menu);
    }

    public void setToolbar() {

        toolbar.setTitle("");

        //툴바를 적용
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //툴바 상단 좌측 메뉴 버튼을 누를 시 네비게이션 드로우 메뉴를 보여준다.
        actionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //Toolbar에 속한 메뉴
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_marker:
                if (!CONTROL_MAP_CLICK) {
                    CONTROL_MAP_CLICK = true;
                    item.setIcon(R.drawable.p_nmap_action_marker);
                    MyApplication.sendToast(getActivity(), "마커 기능 비활성화");
                } else {
                    CONTROL_MAP_CLICK = false;
                    item.setIcon(R.drawable.p_nmap_action_marker_click);
                    MyApplication.sendToast(getActivity(), "마커 기능 활성화");
                }
                break;
            case R.id.action_search:

                //검색 액티비티로 넘어간다.
                Log.d(TAG + "onOptionsItemSelected", "검색 액티비티로 이동");

                Intent searchIntent = new Intent(getActivity(), MapSearch.class);

                startActivityForResult(searchIntent, SEARCH_PLACE);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //네이버 맵 출력 및 출력 이후 이벤트
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        tmpNMap = naverMap;

        //권한
        Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(context, act);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 허가되었을 시 현재 위치로 이동
                presentedLocation();
                permissionValue = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 허가되지 않았을 때
                permissionValue = false;
            }
        };

        setting_permissionCheck.setPermissionListener_location(permissionListener);
        //------------------------------------------------------------------------------------------

        //Floating Action Button 누를 시 현재 위치로 이동
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //권한 허가 시 실행, 권한 미허가 시 권한 요청 메시지 재요청
                if (permissionValue)
                    presentedLocation();
                else
                    setting_permissionCheck.setPermissionListener_location(permissionListener);
            }
        });

        //------------------------------------------------------------------------------------------

        //db 내 저장된 마커들을 표시
        Call<List<PostData>> markerCall = MyApplication.conn.retrofitService.loadMarker(MyApplication.USER_ID);

        markerCall.enqueue(new Callback<List<PostData>>() {
            @Override
            public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {

                List<PostData> list = response.body();

                for (int i = 0; i < list.size(); i++) {

                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(list.get(i).getLat(), list.get(i).getLng()));
                    marker.setCaptionText(list.get(i).getcAddress());
                    marker.setForceShowIcon(true);

                    //지도에서 마커가 보이는 위치의 수치
                    marker.setMinZoom(10);

                    //마커의 색
                    int n = list.get(i).getColorNum();

                    switch (n) {
                        case 0:
                            marker.setIcon(MarkerIcons.RED);
                            break;
                        case 1:
                            marker.setIcon(MarkerIcons.PINK);
                            break;
                        case 2:
                            marker.setIcon(MarkerIcons.BLUE);
                            break;
                        case 3:
                            marker.setIcon(MarkerIcons.LIGHTBLUE);
                            break;
                        case 4:
                            marker.setIcon(MarkerIcons.GREEN);
                            break;
                        case 5:
                            marker.setIcon(MarkerIcons.YELLOW);
                            break;
                        case 6:
                            marker.setIcon(MarkerIcons.GRAY);
                            break;
                        case 7:
                            break;
                    }

                    //지도에 마커를 등록한다.
                    marker.setMap(tmpNMap);

                    //마커 클릭 이벤트
                    clickMarker(marker, list.get(i));

                    markerList.add(marker);
                    markerHash.put(marker, list.get(i));
                }
            }

            @Override
            public void onFailure(Call<List<PostData>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        //------------------------------------------------------------------------------------------

        //Naver지도 API 내의 UI 제거
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);

        //Naver지도를 클릭 시 떠있는 UI 감추기 및 마커 생성 버튼 후 지도 클릭
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

                if (CONTROL_MAP_CLICK) {
                    show_hide_value = !show_hide_value;

                    if (!show_hide_value)
                        uiLayout.setVisibility(View.INVISIBLE);
                    else
                        uiLayout.setVisibility(View.VISIBLE);

                } else {

                    cameraPosition = new CameraPosition(latLng, 15);
                    cameraAnimation = CameraAnimation.Easing;
                    tmpNMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(cameraAnimation, 500).finishCallback(new CameraUpdate.FinishCallback() {
                        @Override
                        public void onCameraUpdateFinish() {

                            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialog_Style);

                            alert.setMessage("마커 등록")
                                    .setNegativeButton("등록", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(context, act);

                                            PermissionListener permissionListener = new PermissionListener() {
                                                @Override
                                                public void onPermissionGranted() {
                                                    Intent intent = new Intent(context, Marker_Info.class);
                                                    intent.putExtra("user", MyApplication.USER_ID);
                                                    intent.putExtra("latLng", latLng);
                                                    startActivityForResult(intent, GET_MARKER);
                                                    CONTROL_MAP_CLICK = true;
                                                    toolbar.getMenu().getItem(1).setIcon(R.drawable.p_nmap_action_marker);
                                                    //         ds.dlg.dismiss();

                                                }

                                                @Override
                                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                                    Toast.makeText(context, "카메라 권한 및 위치 권한이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            };

                                            setting_permissionCheck.setPermissionListener_camera(permissionListener);
                                        }
                                    }).setPositiveButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CONTROL_MAP_CLICK = true;
                                    toolbar.getMenu().getItem(1).setIcon(R.drawable.p_nmap_action_marker);
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    CONTROL_MAP_CLICK = true;
                                    toolbar.getMenu().getItem(1).setIcon(R.drawable.p_nmap_action_marker);
                                }
                            }).show();
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
            settingGpsInfo = new Setting_GPSInfo(context);

            //Gps에서 좌표를 가져왔는지 아닌지 확인
            if (settingGpsInfo.isGetLocation) {
                latitude = settingGpsInfo.getLatitude();   //위도
                longitude = settingGpsInfo.getLongitude(); //경도
                latLng = new LatLng(latitude, longitude);   //LatLng에 값 대입

            } else {
                Toast.makeText(context, "위치 실패", Toast.LENGTH_SHORT).show();
            }

            //지도 상 보이는 위치 조정
            cameraPosition = new CameraPosition(latLng, 15);

            locationOverlay = tmpNMap.getLocationOverlay();    //현재위치 찍어주는 표시
            locationOverlay.setPosition(latLng);
            locationOverlay.setVisible(true);   // locationOverlay 가시성

            cameraAnimation = CameraAnimation.Easing;

            tmpNMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition)
                    .animate(cameraAnimation, 500).finishCallback(() -> {
                        settingGpsInfo.stopUsingGPS();
                        //이동 후 GPS 종료
                    }));

        } catch (Exception e) {
            //핸드폰 상 위치 서비스가 꺼져있다면 위치 서비스를 켜라고 요청하는 다이얼로그
            settingGpsInfo.showSettingsAlert();
        }
    }

    public void clickMarker(Marker marker, PostData rmd) {

        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {

                cameraPosition = new CameraPosition(new LatLng(rmd.getLat(), rmd.getLng()), 15);
                cameraAnimation = CameraAnimation.Easing;
                tmpNMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(cameraAnimation, 500).finishCallback(new CameraUpdate.FinishCallback() {
                    @Override
                    public void onCameraUpdateFinish() {

                        ds = new DialogSet(context);
                        ds.localPost();

                        MyApplication.ViewCircuitCrop(context, MyApplication.PROFILE_IMAGE, ds.postHostImg);

                        ds.postHostID.setText(MyApplication.USER_ID);
                        ds.postHostID.setSelected(true);

                        try {
                            //상대 경로를 URL로 변경
                            String path = MyApplication.ip + "memorial_point" + rmd.getUri().substring(2);
                            URL tmpUrl = new URL(path);

                            Glide.with(getActivity())
                                    .load(tmpUrl)
                                    .placeholder(R.drawable.p_empty_image)
                                    .error(R.drawable.p_empty_image)
                                    .into(ds.postImg);


                        } catch (Exception e) {
                            Log.d(TAG, "run error: ");
                            e.printStackTrace();
                        }

                        //유저 본인인지 타인지에 따라 레이아웃 파일 변경

                        customPop = new CustomPopUpMenu(getActivity(), ds.menuIcon);
                        popupMenu = customPop.showPopUp(R.menu.my_post_menu);

                        //포스트 우측 상단의 팝업 메뉴
                        ds.menuIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {

                                        int id = item.getItemId();

                                        switch (id) {
//                                    case R.id.pm_move:
//                                        MyApplication.sendToast(context, item.getTitle().toString());
//                                        return true;
                                            case R.id.pm_remove:
                                                Call<ResponseData> removeCall = MyApplication.conn.retrofitService.removePost(rmd.getNo());
                                                removeCall.enqueue(new Callback<ResponseData>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                                        marker.setMap(null);
                                                        ds.lpDlg.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseData> call, Throwable t) {
                                                        MyApplication.sendToast(context, "삭제 실패");
                                                    }
                                                });

                                                MyApplication.sendToast(context, item.getTitle().toString());
                                                return true;
//                                    case R.id.pm_revise:
//                                        MyApplication.sendToast(context, item.getTitle().toString());
//                                        return true;
                                            default:
                                                return true;

                                        }
                                    }
                                });

                                popupMenu.show();
                            }
                        });


                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                            // noinspection deprecation
                            if (rmd.getcAddress() != null)
                                ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + rmd.getcAddress()));
                            else
                                ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  ."));


                            if (rmd.getdAddress() != null)
                                ds.dAddressText.setText(Html.fromHtml("<b>" + "상세" + "</b>" + "  " + rmd.getdAddress()));
                            else
                                ds.dAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  ."));


                            ds.contentText.setText(Html.fromHtml("<b>" + rmd.getWriter() + "</b>" + "  " + rmd.getContents()));
                        } else {
                            if (rmd.getcAddress() != null)
                                ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + rmd.getcAddress(), Html.FROM_HTML_MODE_LEGACY));
                            else
                                ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  .", Html.FROM_HTML_MODE_LEGACY));

                            if (rmd.getdAddress() != null)
                                ds.dAddressText.setText(Html.fromHtml("<b>" + "상세" + "</b>" + "  " + rmd.getdAddress(), Html.FROM_HTML_MODE_LEGACY));
                            else
                                ds.dAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  .", Html.FROM_HTML_MODE_LEGACY));

                            ds.contentText.setText(Html.fromHtml("<b>" + rmd.getWriter() + "</b>" + "  " + rmd.getContents(), Html.FROM_HTML_MODE_LEGACY));
                        }
                        if (rmd.getFriend() != null) {
                            ds.contentText.append("\n" + rmd.getFriend());
                        }

                        if (rmd.getHashTag() != null) {
                            ds.contentText.append("\n" + rmd.getHashTag());
                        }

                    }
                }));

                return false;
            }
        });
    }

    //마커생성
    public void createMarker(MarkerData markerData) {

        Marker marker = new Marker();

        LatLng reLatLng = new LatLng(markerData.getLat(), markerData.getLng());
        marker.setPosition(reLatLng);
        marker.setCaptionText(markerData.getcAddress());

        marker.setForceShowIcon(true);

        //원근감
        marker.setMinZoom(10);
        int n = markerData.getColorNum();

        switch (n) {
            case 0:
                marker.setIcon(MarkerIcons.RED);
                break;
            case 1:
                marker.setIcon(MarkerIcons.PINK);
                break;
            case 2:
                marker.setIcon(MarkerIcons.BLUE);
                break;
            case 3:
                marker.setIcon(MarkerIcons.LIGHTBLUE);
                break;
            case 4:
                marker.setIcon(MarkerIcons.GREEN);
                break;
            case 5:
                marker.setIcon(MarkerIcons.YELLOW);
                break;
            default:
                break;
        }

        if (markerData.getUrlStr() == null) {

            Log.d(TAG, "uri가 없는 경우: " + markerData.getContents());
            Call<List<PostData>> call = MyApplication.conn.retrofitService.uploadMarkerStr(markerData.getWriter(), null, markerData.getLat(), markerData.getLng(), markerData.getcAddress(), markerData.getdAddress(),
                    markerData.getContents(), markerData.getFriendsList(), markerData.getHashTagList(), markerData.getColorNum(), markerData.isShared());

            call.enqueue(new Callback<List<PostData>>() {
                @Override
                public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {
                    rmData = response.body().get(0);
                    clickMarker(marker, rmData);
                }

                @Override
                public void onFailure(Call<List<PostData>> call, Throwable t) {
                    Log.d(TAG, "onFailure: 네트워크 아웃1");
                    marker.setIcon(MarkerIcons.GRAY);
                    t.printStackTrace();
                }
            });
        } else {
            Log.d(TAG, "uri가 있는 경우: ");
            File file = new File(Uri.parse(markerData.getUrlStr()).getPath());

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData
                            ("upload_image", file.getName(), requestFile);

            Call<List<PostData>> call = MyApplication.conn.retrofitService.uploadMarkerUri(markerData.getWriter(), body, markerData.getLat(), markerData.getLng(), markerData.getcAddress(), markerData.getdAddress(),
                    markerData.getContents(), markerData.getFriendsList(), markerData.getHashTagList(), markerData.getColorNum(), markerData.isShared());

            call.enqueue(new Callback<List<PostData>>() {
                @Override
                public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {
                    rmData = response.body().get(0);
                    clickMarker(marker, rmData);
                }

                @Override
                public void onFailure(Call<List<PostData>> call, Throwable t) {
                    t.printStackTrace();
                    Log.d(TAG, "onFailure: 네트워크 아웃2");
                    marker.setIcon(MarkerIcons.GRAY);
                }
            });
        }

        marker.setMap(tmpNMap);


        markerList.add(marker);
        markerHash.put(marker, rmData);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_OK)
            return;

        Log.d(TAG, "onActivityResult: " + requestCode);
        switch (requestCode) {

            case GET_MARKER:
                MarkerData markerData = (MarkerData) data.getParcelableExtra("marker");

                if (markerData != null)
                    createMarker(markerData);
                break;
            case SEARCH_PLACE:

                //검색 결과의 결과
                //마커를 지도상 입력 - 타이틀: 검색 결과
                //해당 마커를 클릭하면 웹뷰로 네이버 검색이 된다.
                Place place = (Place) data.getSerializableExtra("place");

                cameraPosition = new CameraPosition(new LatLng(Double.parseDouble(place.getY()), Double.parseDouble(place.getX())), 15);
                cameraAnimation = CameraAnimation.Easing;
                tmpNMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(cameraAnimation, 500));
                Log.d(TAG, "onActivityResult: " + place);

                searchMarker = new Marker();

                searchMarker.setPosition(new LatLng(Double.parseDouble(place.getY()), Double.parseDouble(place.getX())));
                searchMarker.setCaptionText("검색 결과\n"+place.getName());
                searchMarker.setForceShowIcon(true);
                searchMarker.setMap(tmpNMap);

                searchMarker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialog_Style);
                        alert.setMessage("검색 결과: " + place.getName())
                                .setNeutralButton("검색", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, SearchWebView.class);
                                intent.putExtra("word", place.getName());
                                startActivity(intent);
                            }
                        }).setNegativeButton("마커 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Setting_permissionCheck setting_permissionCheck = new Setting_permissionCheck(context, act);

                                PermissionListener permissionListener = new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {
                                        Intent intent = new Intent(context, Marker_Info.class);
                                        intent.putExtra("user", MyApplication.USER_ID);
                                        intent.putExtra("place", place);
                                        intent.putExtra("latLng", new LatLng(Double.parseDouble(place.getY()), Double.parseDouble(place.getX())));
                                        startActivityForResult(intent, GET_MARKER);
                                        CONTROL_MAP_CLICK = true;
                                        toolbar.getMenu().getItem(1).setIcon(R.drawable.p_nmap_action_marker);
                                    }
                                    @Override
                                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                        Toast.makeText(context, "카메라 권한 및 위치 권한이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                };

                                setting_permissionCheck.setPermissionListener_camera(permissionListener);
                            }
                        }).setPositiveButton("취소", null).show();

                        return false;
                    }
                });

                break;
        }
    }
}
