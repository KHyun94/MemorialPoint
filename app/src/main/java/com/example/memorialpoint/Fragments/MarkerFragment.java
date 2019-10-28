package com.example.memorialpoint.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.memorialpoint.Adapters.MarkerRecyclerAdapter;
import com.example.memorialpoint.Models.MarkerData;
import com.example.memorialpoint.Models.PlaceResponse;
import com.example.memorialpoint.Models.Place;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;
import com.naver.maps.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkerFragment extends android.support.v4.app.Fragment implements Serializable, View.OnClickListener {

    String TAG = "TAG";

    //넘겨받은 데이터
    //onAttach()에서 받는다.
    LatLng latLng;

    //onStart()에서 받는다.
    Uri uri;

    Place receivePlace;

    RecyclerView recyclerView;

    MarkerRecyclerAdapter adapter;
    //
    //isValue: addTextChangedListener()의 실행 여부를 가진 alert
    //isShared: (미정) 공개 게시글에 올릴지에 대한 여부
    boolean isShared = false;

    ImageView boardImgView;
    EditText cAddressEdit, dAddressEdit, markerContent, markerFriend, markerHashTag;
    Switch sharedSwitch;
    Button enrollBtn;

    ImageButton[] colorBtns = null;
    int[] colorBtnID = {R.id.redBtn, R.id.pinkBtn, R.id.blueBtn, R.id.lBuleBtn, R.id.greenBtn, R.id.yellowBtn};

    //결과
    double reLat, reLng;
    String cAddressStr = "", dAddressStr ="";
    String markerContentStr = "";
    String friendStr = "";
    String hashTagStr = "";
    int colorNum = 4;

    public MarkerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Marker_info 실행하자마자 LatLng을 받는다.
        if (getArguments() != null) {
            latLng = (LatLng) this.getArguments().getParcelable("latLng");
        }

    }

    public void init(View view){
        cAddressEdit = (EditText) view.findViewById(R.id.cAddressEdit);
        dAddressEdit = (EditText) view.findViewById(R.id.dAddressEdit);

        recyclerView = view.findViewById(R.id.recyclerView);

        markerContent = (EditText) view.findViewById(R.id.markerContent);
        boardImgView = (ImageView) view.findViewById(R.id.boardImgView);
        markerFriend = (EditText) view.findViewById(R.id.markerFriend);
        markerHashTag = (EditText) view.findViewById(R.id.markerHashTag);
        sharedSwitch = (Switch) view.findViewById(R.id.sharedSwitch);

        enrollBtn = (Button) view.findViewById(R.id.enrollBtn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_marker_fragment_second, container, false);

        init(view);

        colorBtns = new ImageButton[colorBtnID.length];

        for (int i = 0; i < colorBtnID.length; i++) {
            colorBtns[i] = (ImageButton) view.findViewById(colorBtnID[i]);
        }

        if(getActivity().getIntent().getSerializableExtra("place") != null){
            receivePlace = ((Place)getActivity().getIntent().getSerializableExtra("place"));

            cAddressEdit.clearFocus();
            cAddressEdit.setText(receivePlace.getName());
            cAddressEdit.setFocusableInTouchMode(false);
            dAddressEdit.setText(receivePlace.getRoad_address());

            if(receivePlace.getRoad_address() != "" && receivePlace.getRoad_address() != null){
                dAddressEdit.setFocusableInTouchMode(false);
            }

            Log.d(TAG, "onCreateView: 낫 널");
        }else{
            getAddressList(latLng);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {

            uri = this.getArguments().getParcelable("sendUri");

            if (uri != null)
                boardImgView.setImageURI(uri);
            else
                boardImgView.setImageResource(R.drawable.p_empty_image);

        } else
            boardImgView.setImageResource(R.drawable.p_empty_image);

        sharedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isShared = true;
                else
                    isShared = false;
            }
        });

        for (int i = 0; i < colorBtnID.length; i++) {
            colorBtns[i].setOnClickListener(this);
        }

        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (receivePlace != null) {

                    reLat = Double.parseDouble(receivePlace.getY());
                    reLng = Double.parseDouble(receivePlace.getX());

                    cAddressStr = receivePlace.getName();
                    dAddressStr = receivePlace.getRoad_address();
                } else {

                    reLat = latLng.latitude;
                    reLng = latLng.longitude;

                    if (cAddressEdit.getText().toString().isEmpty())
                        cAddressStr = null;
                    else
                        cAddressStr = cAddressEdit.getText().toString();

                    if (dAddressEdit.getText().toString().isEmpty())
                        dAddressStr = null;
                    else
                        dAddressStr = dAddressEdit.getText().toString();
                }

                if (markerContent.getText().toString().isEmpty()) {
                    markerContentStr = ".";
                } else {
                    markerContentStr = markerContent.getText().toString();
                }


                if (markerFriend.getText().toString().isEmpty())
                    friendStr = null;
                else {
                    friendStr = markerFriend.getText().toString().trim();
                }

                if (markerHashTag.getText().toString().isEmpty())
                    hashTagStr = null;
                else
                    hashTagStr = markerHashTag.getText().toString().trim();

                MarkerData markerData;

                if (uri != null) {
                    markerData = new MarkerData(MyApplication.USER_ID, uri.toString(), reLat, reLng, cAddressStr, dAddressStr, markerContentStr, friendStr, hashTagStr, colorNum, isShared);
                } else {
                    markerData = new MarkerData(MyApplication.USER_ID, null, reLat, reLng, cAddressStr, dAddressStr, markerContentStr, friendStr, hashTagStr, colorNum, isShared);
                }

                Intent intent = new Intent(getActivity(), NMapFragment.class);
                intent.putExtra("marker", markerData);
                getActivity().setResult(Activity.RESULT_OK, intent);

                getActivity().finish();

            }
        });
    }

    public ArrayList<String> searchWord(String str, String findStr) {

        ArrayList<String> arr = new ArrayList<String>();

        while (str.indexOf(findStr) != -1) {

            int i = str.indexOf(findStr);
            int j;

            if (i > 0) {
                if (Character.toString(str.charAt(i - 1)).isEmpty()
                        || Character.toString(str.charAt(i - 1)).equals(" ")) {

                    if (str.indexOf(" ", i) != -1) {
                        j = str.indexOf(" ", i);

                        String test1 = str.substring(i, j);
                        String test2 = str.substring(j + 1);

                        str = test2;

                        if (test1.length() != 1) {
                            arr.add(test1);
                        }

                    } else {
                        String test1 = str.substring(i);

                        if (test1.length() != 1) {
                            arr.add(test1);
                        }
                        break;
                    }

                } else {

                    if (str.indexOf(" ", i) != -1) {
                        j = str.indexOf(" ", i);

                        String test1 = str.substring(i, j);
                        String test2 = str.substring(j + 1);

                        str = test2;

                    } else {
                        String test1 = str.substring(i);

                        break;
                    }
                }
            } else {
                if (str.indexOf(" ", i) != -1) {
                    j = str.indexOf(" ", i);

                    String test1 = str.substring(i, j);
                    String test2 = str.substring(j + 1);

                    str = test2;

                    if (test1.length() != 1) {
                        arr.add(test1);
                    }

                } else {
                    String test1 = str.substring(i);

                    if (test1.length() != 1) {
                        arr.add(test1);
                    }
                    break;
                }
            }

        }

        for (int i = 0; i < arr.size(); i++) {
            int k, j;

            k = arr.get(i).indexOf(findStr);
            j = arr.get(i).lastIndexOf(findStr);

            if ((k == 0) && (j == 0)) {

            } else {
                arr.remove(i);
                i -= 1;
            }

        }

        for (int i = 0; i < arr.size(); i++) {
            Log.d(TAG, "searchWord: " + arr.get(i));
        }
        return arr;

    }

    //cAddressEdit의 값이 실시간으로 변하는거에 따른 결과값 recyclerView로 넘김
    public void getAddressList(LatLng latLng) {

        try {
            Thread.sleep(1000);

            cAddressEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    recyclerView.setVisibility(View.VISIBLE);

                    String latLngStr = Double.toString(latLng.longitude) + "," + Double.toString(latLng.latitude);
                    String searchWord = s.toString();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (!searchWord.isEmpty()) {

                                        Call<PlaceResponse> call = MyApplication.conn.retrofitService.getSearch(searchWord, latLngStr);

                                        call.enqueue(new Callback<PlaceResponse>() {
                                            @Override
                                            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {

                                                if (response.body() != null) {

                                                    List<Place> placeList = response.body().getPlaces();

                                                    adapter = new MarkerRecyclerAdapter();

                                                    LinearLayoutManager linearManager = new LinearLayoutManager(getActivity());
                                                    recyclerView.setLayoutManager(linearManager);

                                                    recyclerView.setAdapter(adapter);

                                                    for (int i = 0; i < placeList.size(); i++)
                                                        adapter.addItem(placeList.get(i));

                                                    adapter.notifyDataSetChanged();

                                                    Log.d(TAG, "onResponse: " + searchWord);
                                                    adapter.setItemClickListener(new MarkerRecyclerAdapter.onItemClickListener() {
                                                        @Override
                                                        public void itemClick(Place places, boolean value) {

                                                            receivePlace = places;
                                                            cAddressEdit.clearFocus();
                                                            cAddressEdit.setText(receivePlace.getName());
                                                            dAddressEdit.setText(receivePlace.getRoad_address());
                                                            dAddressEdit.setFocusableInTouchMode(false);
                                                            recyclerView.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                                                t.printStackTrace();
                                            }
                                        });
                                    } else {
                                        recyclerView.removeAllViews();
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    });

                    thread.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int viewID = v.getId();
        switch (viewID) {
            case R.id.redBtn:
                colorNum = 0;
                break;

            case R.id.pinkBtn:
                colorNum = 1;
                break;

            case R.id.blueBtn:
                colorNum = 2;
                break;

            case R.id.lBuleBtn:
                colorNum = 3;
                break;

            case R.id.greenBtn:
                colorNum = 4;
                break;

            case R.id.yellowBtn:
                colorNum = 5;
                break;

            default:
                break;

        }

        if (colorNum == 4 || colorNum == 5)
            enrollBtn.setTextColor(getResources().getColor(R.color.BLACK));
        else
            enrollBtn.setTextColor(getResources().getColor(R.color.WHITE));

        enrollBtn.setBackground(colorBtns[colorNum].getBackground());
    }
}
