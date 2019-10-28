package com.example.memorialpoint.Models;

import java.io.Serializable;

//public class ChatUser implements Serializable {
//
//    String userName;
//    Socket socket;
//    RoomData roomData;
//
//    public ChatUser(String userName, Socket socket, RoomData roomData) {
//        this.userName = userName;
//        this.socket = socket;
//        this.roomData = roomData;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//    public void setSocket(Socket socket) {
//        this.socket = socket;
//    }
//
//    public RoomData getRoomData() {
//        return roomData;
//    }
//
//    public void setRoomData(RoomData roomData) {
//        this.roomData = roomData;
//    }
//}

public class ChatUser implements Serializable {

    String userName;
    RoomData roomData;
    int ROOM_PERMISSION;
    //방 권한 0:방장, 1:게스트

    public ChatUser(String userName, RoomData roomData, int ROOM_PERMISSION) {
        this.userName = userName;
        this.roomData = roomData;
        this.ROOM_PERMISSION = ROOM_PERMISSION;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RoomData getRoomData() {
        return roomData;
    }

    public void setRoomData(RoomData roomData) {
        this.roomData = roomData;
    }

    public int getROOM_PERMISSION() {
        return ROOM_PERMISSION;
    }

    public void setROOM_PERMISSION(int ROOM_PERMISSION) {
        this.ROOM_PERMISSION = ROOM_PERMISSION;
    }
}


