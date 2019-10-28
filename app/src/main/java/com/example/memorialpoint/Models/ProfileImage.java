package com.example.memorialpoint.Models;

import java.io.Serializable;

public class ProfileImage implements Serializable {

    String profile;
    String date;

    public ProfileImage(String profile, String date) {
        this.profile = profile;
        this.date = date;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
