package com.example.memorialpoint.Models;

public class ReceiveMsg {

    String userID;
    String msg;
    String date;
    int type;

    public ReceiveMsg(String userID, String msg, String date, int type) {

        this.userID = userID;
        this.msg = msg;
        this.date = date;
        this.type = type;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReceiveMsg{" +
                "userID='" + userID + '\'' +
                ", msg='" + msg + '\'' +
                ", date='" + date + '\'' +
                ", type=" + type +
                '}';
    }
}