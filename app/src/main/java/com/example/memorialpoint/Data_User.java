package com.example.memorialpoint;

import java.io.Serializable;

public class Data_User implements Serializable {

    String no;
    String id;
    String pwd;
    String name;
    String birthday;
    String sex;
    String address;
    String email;
    String tel;

    public Data_User(String no, String id, String pwd, String name, String birthday, String sex, String address, String email, String tel) {
        this.no = no;
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.address = address;
        this.email = email;
        this.tel = tel;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }





}
