package com.example.memorialpoint.Models;

import android.net.Uri;

import java.io.Serializable;

public class RoomData implements Serializable {

    int r_no;
    String r_name;
    String r_host;
    String r_host_img;
    String r_date;

    public RoomData(int r_no, String r_name, String r_host, String r_host_img, String r_date) {
        this.r_no = r_no;
        this.r_name = r_name;
        this.r_host = r_host;
        this.r_host_img = r_host_img;
        this.r_date = r_date;
    }

    public int getR_no() {
        return r_no;
    }

    public void setR_no(int r_no) {
        this.r_no = r_no;
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public String getR_host() {
        return r_host;
    }

    public void setR_host(String r_host) {
        this.r_host = r_host;
    }

    public String getR_host_img() {
        return r_host_img;
    }

    public void setR_host_img(String r_host_img) {
        this.r_host_img = r_host_img;
    }

    public String getR_date() {
        return r_date;
    }

    public void setR_date(String r_date) {
        this.r_date = r_date;
    }
}
