package com.example.memorialpoint.Models;

import com.example.memorialpoint.Profile;

import java.io.Serializable;

public class UserData implements Serializable {

    String no;
    String id;
    String pwd;
    String name;
    String sex;
    String email;
    ProfileImage profile;

    public UserData(String no, String id, String pwd, String name, String sex, String email) {
        this.no = no;
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.sex = sex;
        this.email = email;
    }

    public UserData() {
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProfileImage getProfile() {
        return profile;
    }

    public void setProfile(ProfileImage profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "no='" + no + '\'' +
                ", id='" + id + '\'' +
                ", pwd='" + pwd + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
