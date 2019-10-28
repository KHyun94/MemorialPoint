package com.example.memorialpoint.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.memorialpoint.Adapters.RoomManagerAdapter;
import com.example.memorialpoint.ChattingClient;
import com.example.memorialpoint.Models.ChatUser;
import com.example.memorialpoint.Models.RoomData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomManagerFragment extends android.support.v4.app.Fragment implements Serializable {

    String TAG = "Memorial.RoomManagerFragment.";

    //대화방 목록 리사이클러뷰
    RecyclerView roomRV;
    RoomManagerAdapter rmAdapter;

    //대기 방을 부르는 레트로핏
    Call<List<RoomData>> r_call;

    //방을 만드는 레트로핏
    Call<RoomData> cr_call;

    //방 목록을 가지고 있는 리스트
    List<RoomData> roomList;

    //각각 대기방 갱신 버튼, 방만들기 버튼
    ImageView refreshBtn, createRoomBtn;

    ChatUser chatUser;

    public RoomManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_manager, container, false);

        roomRV = view.findViewById(R.id.roomRV);
        createRoomBtn = view.findViewById(R.id.rm_createRoomBtn);
        refreshBtn = view.findViewById(R.id.rm_refreshBtn);

        //MySQL에 저장되어있는 방목록을 불러오는 작업
        r_call = MyApplication.conn.retrofitService.getRooms();

        r_call.enqueue(new Callback<List<RoomData>>() {
            @Override
            public void onResponse(Call<List<RoomData>> call, Response<List<RoomData>> response) {

                //대기방에 방 목록을 넣는 작업
                if (response.body() != null) {
                    roomList = response.body();

                    rmAdapter = new RoomManagerAdapter(getActivity(), (ArrayList<RoomData>) roomList);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomRV.setAdapter(rmAdapter);
                        }
                    });

                    //대기방 목록에 있는 방을 클릭하면 해당 방으로 들어간다.
                    //방 권한은 1:게스트로 들어가며 권한은 방을 나올때 호스트는 방 자체를 없애고, 게스트는 해당 방에서 한 대화 로그를 저장하는데 쓰인다.
                    rmAdapter.setRoomClickListener(new RoomManagerAdapter.onRoomClickListener() {
                        @Override
                        public void onRoomClicked(RoomData room) {


                            chatUser = new ChatUser(MyApplication.USER_ID, room, 1);
                            Intent intent = new Intent(getActivity(), ChattingClient.class);
                            intent.putExtra("chatUser", chatUser);
                            getActivity().startActivity(intent);

                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "방이 없습니다. 새로 만드세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RoomData>> call, Throwable t) {
                Toast.makeText(getActivity(), "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });


        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity());
        roomRV.setLayoutManager(linearManager);

        //방목록 새로고침 버튼
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r_call.clone().enqueue(new Callback<List<RoomData>>() {
                    @Override
                    public void onResponse(Call<List<RoomData>> call, Response<List<RoomData>> response) {

                        if(response.body() != null){

                            //존재하던 방을 전부 지우고 다시 불러오는 작업
                            roomList.clear();
                            rmAdapter.removeItem();

                            roomList = response.body();

                            for(RoomData roomData : roomList){
                                rmAdapter.addItem(roomData);
                            }

                            rmAdapter.notifyDataSetChanged();

                        }else{
                            Toast.makeText(getActivity(), "목록 없음", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<List<RoomData>> call, Throwable t) {
                        Toast.makeText(getActivity(), "갱신 실패" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //새로운 방 만들기 버튼
        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //(임시) 다이얼로그에 방제목을 적고 만들기 버튼을 누르면 MySQL에 새로운 방 정보가 저장되고 해당 방으로 들어간다.
                EditText et = new EditText(getActivity());

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Style);
                alert.setTitle("방만들기")
                        .setView(et)
                        .setPositiveButton("취소", null)
                        .setNegativeButton("만들기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //방 만들기에 성공할 시 방권한은 0:호스트을 가진다.
                                //방 권한은 0:호스트로 들어가며 권한은 방을 나올때 호스트는 방 자체를 없애고, 게스트는 해당 방에서 한 대화 로그를 저장하는데 쓰인다.
                                if(!et.getText().toString().equals("")){
                                    cr_call = MyApplication.conn.retrofitService.createRooms(et.getText().toString(), MyApplication.USER_ID);

                                    cr_call.enqueue(new Callback<RoomData>() {
                                        @Override
                                        public void onResponse(Call<RoomData> call, Response<RoomData> response) {

                                            if(response.body() != null){

                                                chatUser = new ChatUser(MyApplication.USER_ID, response.body(), 0);

                                                Intent intent = new Intent(getActivity(), ChattingClient.class);
                                                intent.putExtra("chatUser", chatUser);
                                                getActivity().startActivity(intent);
                                            }else{
                                                Toast.makeText(getActivity(), "방만들기 실패", Toast.LENGTH_SHORT);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<RoomData> call, Throwable t) {
                                            Toast.makeText(getActivity(), "방만들기 실패" + t.getMessage(), Toast.LENGTH_SHORT);
                                        }
                                    });
                                }else{
                                    Toast.makeText(getActivity(), "방 제목을 지어주세요", Toast.LENGTH_SHORT);
                                }


                            }
                        }).show();
            }
        });

        return view;
    }
}

