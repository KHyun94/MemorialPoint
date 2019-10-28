package com.example.memorialpoint.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkerData implements Parcelable {

    String writer;
    String urlStr;
    double lat;
    double lng;
    String cAddress;
    String dAddress;
    String contents;
    String friendsList;
    String hashTagList;
    int colorNum;
    boolean isShared;

    public MarkerData(String writer, String urlStr, double lat, double lng, String cAddress, String dAddress, String contents, String friendsList, String hashTagList, int colorNum, boolean isShared) {
        this.writer = writer;
        this.urlStr = urlStr;
        this.lat = lat;
        this.lng = lng;
        this.cAddress = cAddress;
        this.dAddress = dAddress;
        this.contents = contents;
        this.friendsList = friendsList;
        this.hashTagList = hashTagList;
        this.colorNum = colorNum;
        this.isShared = isShared;
    }

    protected MarkerData(Parcel in) {
        writer = in.readString();
        urlStr = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        cAddress = in.readString();
        dAddress = in.readString();
        contents = in.readString();
        friendsList = in.readString();
        hashTagList = in.readString();
        colorNum = in.readInt();
        isShared = in.readByte() != 0;
    }

    public static final Creator<MarkerData> CREATOR = new Creator<MarkerData>() {

        @Override
        public MarkerData createFromParcel(Parcel in) {
            return new MarkerData(in);
        }

        @Override
        public MarkerData[] newArray(int size) {
            return new MarkerData[size];
        }
    };

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
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

    public String getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(String friendsList) {
        this.friendsList = friendsList;
    }

    public String getHashTagList() {
        return hashTagList;
    }

    public void setHashTagList(String hashTagList) {
        this.hashTagList = hashTagList;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(writer);
        dest.writeString(urlStr);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(cAddress);
        dest.writeString(dAddress);
        dest.writeString(contents);
        dest.writeString(friendsList);
        dest.writeString(hashTagList);
        dest.writeInt(colorNum);
        dest.writeByte((byte) (isShared ? 1 : 0));
    }
}
