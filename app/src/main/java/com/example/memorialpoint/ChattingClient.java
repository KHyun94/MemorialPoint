package com.example.memorialpoint;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Adapters.ChattingClientRecyclerAdapter;
import com.example.memorialpoint.Models.ChatUser;
import com.example.memorialpoint.Models.ReceiveMsg;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingClient extends AppCompatActivity implements Serializable {

    String TAG = "Memorial.ChattingClient.";

    //순서대로 뒤로가기 버튼
    ImageView backBtn;
    TextView roomTitle;
    //채팅 내용 리스트
    RecyclerView chattingBoxRv;

    //전송할 메시지 적는 곳
    EditText chatWriteEt;

    //메시지 전송 버튼
    TextView chatSubmitTv;

    //리사이클러뷰 어댑터
    ChattingClientRecyclerAdapter clientAdapter;

    //모든 메시지 객체 저장
    ArrayList<ReceiveMsg> rMsgList = new ArrayList<>();

    //클라이언트 소켓
    Socket clientSocket = new Socket();

    //접속한 클라이언트의 데이터 객체
    ChatUser chatUser;

    //호스트가 방을 나갔을 때 방을 삭제하는 레트로핏
    Call<Void> removeCall;

    //해당 방에서 채팅을 한 기록이 있다면 대화 내용을 불러오는 기능과 (게스트)나갈때 대화 내용을 저장
    SharedPreferences chattingLog;
    SharedPreferences.Editor editor;

    Gson gson = new GsonBuilder().create();

    //sharedPreferences의 키값
    String logName;

    //위젯 초기화
    public void init() {
        backBtn = findViewById(R.id.cc_backBtn);
        roomTitle = findViewById(R.id.cc_room_title);
        chattingBoxRv = findViewById(R.id.cc_chattingBoxRv);
        chatWriteEt = findViewById(R.id.cc_chatWriteEt);
        chatSubmitTv = findViewById(R.id.cc_chatSubmitTv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_client);

        //대기방(RoomManagerFragment)에서 넘어올 때 보낸 클라이언트의 데이터
        if (getIntent() != null) {
            chatUser = (ChatUser) getIntent().getSerializableExtra("chatUser");
            Log.d(TAG + "getIntent", "chatUser: " + chatUser.getUserName());
            logName = chatUser.getUserName() + "@" + chatUser.getRoomData().getR_no();

        } else {
            //클라이언트의 데이터가 안넘어올 시 액티비티 종료
            sendToastMsg("에러로 인한 접속 불가");
            finish();
        }

        init();

        //해당 방에서 채팅한 이력이 있다면 그전 대화 목록을 불러오는 부분
        chattingLog = getSharedPreferences("Chatting_Log", MODE_PRIVATE);
        editor = chattingLog.edit();

        //해당 sharedPreferences 파일에 지정 키값이 있는지 확인
        if (chattingLog.contains(logName)) {
            Log.d(TAG + "LOAD", "저장된 값 있음");
            String logStr = chattingLog.getString(logName, "");
            rMsgList = gson.fromJson(logStr, new TypeToken<ArrayList<ReceiveMsg>>() {
            }.getType());

            sendToastMsg("저장된 대화 기록을 불러옵니다.\n즐거운 채팅시간 되십시오.");

        } else {
            Log.d(TAG + "LOAD", "저장된 값없음");
            sendToastMsg("즐거운 채팅시간 되십시오.");
        }

        //방의 제목
        roomTitle.setText(chatUser.getRoomData().getR_no() + "번 방 - " + chatUser.getRoomData().getR_name());

        //리사이클러뷰
        chattingBoxRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //리사이클러뷰 아이템 간격
        chattingBoxRv.addItemDecoration(new RecyclerDecoration(20));

        clientAdapter = new ChattingClientRecyclerAdapter(rMsgList, this);

        chattingBoxRv.setAdapter(clientAdapter);

        chattingBoxRv.scrollToPosition(chattingBoxRv.getAdapter().getItemCount() - 1);

        //클라이언트의 소켓이 채팅 서버에 접속하는 부분
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket.connect(new InetSocketAddress(getResources().getString(R.string.chattingIP), Integer.parseInt(getResources().getString(R.string.chattingPORT))), 3000);

                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    //접속하는 클라이언트의 데이터를 json화 해서 서버로 보낸다.
                    JSONObject userJO = new JSONObject();
                    userJO.put("u_id", chatUser.getUserName());
                    userJO.put("u_room_no", chatUser.getRoomData().getR_no());
                    userJO.put("u_room_name", chatUser.getRoomData().getR_name());
                    userJO.put("u_room_permission", chatUser.getROOM_PERMISSION());

                    Log.d(TAG + "접속", "User + Room Data: " + userJO.toString());
                    dos.writeUTF(userJO.toString());

                    dos.flush();

                    //서버에서 오는 데이터(메시지나 명령)를 관리하는 스레드
                    new MsgThread().start();

                } catch (SocketException e) {

                    sendToastMsg("현재 서버 접속 시간이 아닙니다.");

                    e.printStackTrace();
                    finish();
                } catch (IOException e) {
                    sendToastMsg("현재 서버 접속 시간이 아닙니다.");
                    e.printStackTrace();
                    finish();
                } catch (JSONException e) {
                    sendToastMsg("현재 서버 접속 시간이 아닙니다.");
                    e.printStackTrace();
                    finish();
                }
            }
        }).start();

        //보내는 글의 길이에 따라 chatSubmitTv(전송 버튼)을 활성/비활성을 설정정
        changeBtnState(chatSubmitTv, chatWriteEt);

        //메시지 전송
        chatSubmitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //메시지를 적어서 전송 버튼을 누를 때 해당 데이터(메시지 등)이 서버로 넘어가는 메서드
                    sendToServer(chatUser, chatWriteEt.getText().toString());
                } catch (SocketException e) {
                    sendToastMsg("접속 에러 재접속 부탁드립니다.");
                    e.printStackTrace();
                }

                chatWriteEt.setText("");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅을 종료하고 대기방(RoomManagerFragment)로 돌아간다.
                new ExitThread(clientSocket, chatUser).start();
                finish();
            }
        });
    }

    //채팅 서버로 메시지를 보내는 역할
    public void sendToServer(ChatUser chatUser, String msg) throws SocketException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("u_id", chatUser.getUserName());
                    jsonObject.put("msg", msg);
                    jsonObject.put("roomNum", chatUser.getRoomData().getR_no());

                    dos.writeUTF(jsonObject.toString());
                    dos.flush();

                    Log.d(TAG + "send", "Send: " + jsonObject.toString());
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //chatWriteEt의 내용에 따라 chatSubmitTv(전송글)의 활성 비활성 결정
    private void changeBtnState(TextView tv, EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //메시지 전송란에 들어있는 문자열의 길이에 따라 메시지 전송 버튼의 색과 클릭 활성 비활성이 정해진다.
                                if (et.getText().toString().length() > 0) {
                                    tv.setEnabled(true);
                                    tv.setTextColor(Color.parseColor("#3F51B5"));
                                } else {
                                    tv.setEnabled(false);
                                    tv.setTextColor(Color.parseColor("#C5CAE9"));
                                }
                            }
                        });
                    }
                }).start();

            }
        });
    }

    public void sendToastMsg(String msg) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }, 0);
    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (chatUser.getROOM_PERMISSION() == 0) {
            //방 호스트일 경우 저장 안하고 방자체를 삭제
            //mysql에 저장되어있는 방을 삭제
            removeCall = MyApplication.conn.retrofitService.removeRooms(chatUser.getRoomData().getR_no());
            removeCall.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    sendToastMsg("즐거운 시간되셨습니까?");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    sendToastMsg("방 삭제에 실패하셨습니다.");
                }
            });
        } else {
            //방 게스트일 경우 자신이 들어와서 나갈 때까지의 대화 기록을 저장
            //다만 방이 사라졌을 경우 처음 불러올때 일치하지 않으면 해당 파일 지우기

            String logStr = gson.toJson(rMsgList, new TypeToken<ArrayList<ReceiveMsg>>() {}.getType());

            chattingLog = getSharedPreferences("Chatting_Log", MODE_PRIVATE);
            editor = chattingLog.edit();
            editor.putString(logName, logStr);
            editor.commit();
        }
        //나갈떄 저장

        new ExitThread(clientSocket, chatUser).start();

    }

    //서버로부터 메시지를 받는 스레드 클래스
    class MsgThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (clientSocket.isConnected()) {

                Log.d(TAG, "둘다: " + (!clientSocket.isClosed() && clientSocket.isConnected()));

                if (!(!clientSocket.isClosed() && clientSocket.isConnected()))
                    break;

                try {
                    //서버로부터 오는 데이터(JSON 형태의 문자열)을 받아서 메시지 객체에 넣어준다.
                    //메시지 객체를 대화창(Recycler view)에 추가 해준다.
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

                    String receiveJson = dis.readUTF();  // err

                    Log.d(TAG, "receiveFromServer: " + receiveJson);

                    JSONObject jsonObject = new JSONObject(receiveJson);

                    String userID = jsonObject.getString("u_id");
                    String msg = jsonObject.getString("msg");
                    String date = jsonObject.getString("date");
                    int type = jsonObject.getInt("type");

                    ReceiveMsg receiveMsg = new ReceiveMsg(userID, msg, date, type);

                    Log.d(TAG + "receive", "Data: " + receiveMsg.toString());

                    if (receiveMsg.getMsg().equals("SYSTEM_OUT")) {

                        //방의 호스트가 나갔을 때 게스트들은 해당 방의 대화 기록을 지우고 나간다.
                        chattingLog = getSharedPreferences("Chatting_Log", MODE_PRIVATE);
                        editor = chattingLog.edit();
                        editor.remove(logName);
                        editor.commit();

                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clientAdapter.addItem(receiveMsg);
                                clientAdapter.notifyItemInserted(chattingBoxRv.getAdapter().getItemCount() - 1);
                                chattingBoxRv.scrollToPosition(chattingBoxRv.getAdapter().getItemCount() - 1);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}

class ExitThread extends Thread {

    //채팅방 종료 스레드

    Socket socket;
    ChatUser chatUser;

    public ExitThread(Socket socket, ChatUser chatUser) {
        this.socket = socket;
        this.chatUser = chatUser;
    }

    @Override
    public void run() {
        super.run();

        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("u_id", chatUser.getUserName());

            //방 권한에 따라 보내는 메시지를 달리한다.
            if (chatUser.getROOM_PERMISSION() == 0) {
                //방의 호스트가 나갈때 방의 게스트들을 전부 퇴장 시키기 위한 키워드
                jsonObject.put("msg", "ALL_EXIT_THIS_ROOM");
            } else {
                //방의 게스트가 나갈 떄
                jsonObject.put("msg", "I_EXIT_THIS_ROOM");
            }

            jsonObject.put("roomNum", chatUser.getRoomData().getR_no());

            dos.writeUTF(jsonObject.toString());
            dos.flush();

            if (socket != null)
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
