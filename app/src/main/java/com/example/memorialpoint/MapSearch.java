package com.example.memorialpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.EventLog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.memorialpoint.Adapters.MapSearchRecyclerAdapter;
import com.example.memorialpoint.Models.PlaceResponse;
import com.example.memorialpoint.Models.Place;
import com.example.memorialpoint.Settings.Setting_GPSInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapSearch extends AppCompatActivity implements TextWatcher, MapSearchRecyclerAdapter.OnPlaceClickListener, MapSearchRecyclerAdapter.OnClearClickListener {

    String TAG = "Memorial.MapSearch.";

    ImageView ms_preBtn;
    ImageView ms_clsBtn;

    EditText ms_searchEt;

    RecyclerView ms_searchRv;
    MapSearchRecyclerAdapter searchRecyclerAdapter = new MapSearchRecyclerAdapter(false);
    MapSearchRecyclerAdapter searchHistoryRecyclerAdapter = new MapSearchRecyclerAdapter(true);

    RecyclerView ms_searchHistoryRv;


    Gson gson = new Gson();
    SharedPreferences historySp;
    SharedPreferences.Editor editor;
    List<Place> historyPlaces;

    //생성자
    public void init() {
        ms_preBtn = findViewById(R.id.ms_preBtn);
        ms_clsBtn = findViewById(R.id.ms_clsBtn);
        ms_searchEt = findViewById(R.id.ms_searchEt);
        ms_searchRv = findViewById(R.id.ms_searchRv);
        ms_searchHistoryRv = findViewById(R.id.ms_searchHistoryRv);
    }

    public void getHistory(){
        //해당 방에서 채팅한 이력이 있다면 그전 대화 목록을 불러오는 부분
        historySp = getSharedPreferences("History", MODE_PRIVATE);
        editor = historySp.edit();

        //해당 sharedPreferences 파일에 지정 키값이 있는지 확인
        if (historySp.contains("History")) {
            Log.d(TAG + "LOAD", "저장된 값 있음");
            String logStr = historySp.getString("History", "");
            historyPlaces = gson.fromJson(logStr, new TypeToken<List<Place>>() {}.getType());
        } else {
            historyPlaces = new ArrayList<>();
        }

        searchHistoryRecyclerAdapter.addItems(historyPlaces);
        searchHistoryRecyclerAdapter.notifyDataSetChanged();
    }

    //검색 결과의 리사이클러 뷰 설정
    public void setMs_recyclerView() {
        ms_searchRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ms_searchRv.setAdapter(searchRecyclerAdapter);

        ms_searchHistoryRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ms_searchHistoryRv.setAdapter(searchHistoryRecyclerAdapter);

        searchHistoryRecyclerAdapter.setClearClickListener(this::clearClick);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

        init();

        setMs_recyclerView();

        getHistory();

        searchHistoryRecyclerAdapter.setPlaceClickListener(this::itemClick);

        //실시간 검색 결과 체크 리스너
        ms_searchEt.addTextChangedListener(this);

        //enter key 막기
        ms_searchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) { }

                    if(keyCode == KeyEvent.KEYCODE_DEL && ms_searchEt.length() <= 1){
                        ms_searchHistoryRv.setVisibility(View.VISIBLE);

                        searchRecyclerAdapter.removeItems();
                        searchRecyclerAdapter.notifyDataSetChanged();
                    }
                }


                return false;
            }
        });

        //뒤로 가기
        ms_preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //검색 결과 지우기
        ms_clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색 조건 지우기
                ms_searchEt.setText("");
                searchRecyclerAdapter.removeItems();
                searchRecyclerAdapter.notifyDataSetChanged();
                //실시간 검색 결과인 리사이클러 뷰 초기화
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //실시간으로 searchEt의 값을 체크
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Value: " + s);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!s.toString().isEmpty()) {

                            ms_searchHistoryRv.setVisibility(View.GONE);

                            Call<PlaceResponse> call = MyApplication.conn.retrofitService.getSearch(s.toString(), getLatLng());

                            call.enqueue(new Callback<PlaceResponse>() {
                                @Override
                                public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {

                                    if (response.body() != null) {

                                        List<Place> placeList = response.body().getPlaces();

                                        searchRecyclerAdapter.addItems(placeList);
                                        searchRecyclerAdapter.notifyDataSetChanged();

                                        searchRecyclerAdapter.setPlaceClickListener(new MapSearchRecyclerAdapter.OnPlaceClickListener() {
                                            @Override
                                            public void itemClick(Place place) {
                                                Intent intent = new Intent();
                                                intent.putExtra("place", place);

                                                SaveHistory(place);

                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onFailure(Call<PlaceResponse> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }else {

                            ms_searchHistoryRv.setVisibility(View.VISIBLE);

                            searchRecyclerAdapter.removeItems();
                            searchRecyclerAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }
        }).start();
    }

    public void SaveHistory(Place place){

        //해당 방에서 채팅한 이력이 있다면 그전 대화 목록을 불러오는 부분
        historySp = getSharedPreferences("History", MODE_PRIVATE);
        editor = historySp.edit();

        //해당 sharedPreferences 파일에 지정 키값이 있는지 확인
        if (historySp.contains("History")) {
            Log.d(TAG + "LOAD", "저장된 값 있음");
            String logStr = historySp.getString("History", "");
            historyPlaces = gson.fromJson(logStr, new TypeToken<List<Place>>() {}.getType());
        } else {
            historyPlaces = new ArrayList<>();
        }

        historyPlaces.add(place);

        String historyStr = gson.toJson(historyPlaces, new TypeToken<List<Place>>() {}.getType());

        historySp = getSharedPreferences("History", MODE_PRIVATE);
        editor = historySp.edit();
        editor.putString("History", historyStr);
        editor.commit();
    }

    //현재 위치 받아오기 - 좌표값
    public String getLatLng() {

        //GPS 요청
        Setting_GPSInfo settingGpsInfo = new Setting_GPSInfo(this);

        //Gps에서 좌표를 가져왔는지 아닌지 확인
        if (settingGpsInfo.isGetLocation) {
            String location = settingGpsInfo.getLongitude() + "," + settingGpsInfo.getLatitude();
            settingGpsInfo.stopUsingGPS();
            return location;
        } else {
            MyApplication.sendToast(this, "위치 실패");
            settingGpsInfo.stopUsingGPS();
            return null;
        }

    }

    @Override
    public void clearClick(int position) {
        if(historyPlaces.size() > 0 && historyPlaces != null){

            historyPlaces.remove(position);

            String historyStr = gson.toJson(historyPlaces, new TypeToken<List<Place>>() {}.getType());

            historySp = getSharedPreferences("History", MODE_PRIVATE);
            editor = historySp.edit();
            editor.putString("History", historyStr);
            editor.commit();
        }

    }

    @Override
    public void itemClick(Place place) {
        Intent intent = new Intent();
        intent.putExtra("place", place);
        setResult(RESULT_OK, intent);
        finish();
    }
}
