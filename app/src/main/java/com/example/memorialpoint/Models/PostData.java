package com.example.memorialpoint.Models;

import java.io.Serializable;

public class PostData implements Serializable {

    int no;
    String writer;
    String uri;
    double lat;
    double lng;
    String cAddress;
    String dAddress;
    String contents;
    String friend;
    String hashTag;
    int colorNum;
    boolean isShared;
    String cDate;
    String profile;
    int like_no;

    public PostData(int no, String writer, String uri, double lat, double lng, String cAddress, String dAddress, String contents, String friend, String hashTag, int colorNum, boolean isShared, String cDate, String profile, int like_no) {
        this.no = no;
        this.writer = writer;
        this.uri = uri;
        this.lat = lat;
        this.lng = lng;
        this.cAddress = cAddress;
        this.dAddress = dAddress;
        this.contents = contents;
        this.friend = friend;
        this.hashTag = hashTag;
        this.colorNum = colorNum;
        this.isShared = isShared;
        this.cDate = cDate;
        this.profile = profile;
        this.like_no = like_no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getcAddress() {
        return cAddress;
    }

    public void setcAddress(String cAddress) {
        this.cAddress = cAddress;
    }

    public String getdAddress() {
        return dAddress;
    }

    public void setdAddress(String dAddress) {
        this.dAddress = dAddress;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public int getColorNum() {
        return colorNum;
    }

    public void setColorNum(int colorNum) {
        this.colorNum = colorNum;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getLike_no() {
        return like_no;
    }

    public void setLike_no(int like_no) {
        this.like_no = like_no;
    }
}
