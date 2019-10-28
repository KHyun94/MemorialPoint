package com.example.memorialpoint.Models;

import java.io.Serializable;

public class Place implements Serializable {

    String name;
    String road_address;
    String jibun_address;
    String phone_number;
    String x;
    String y;
    String distance;
    String sessionId;

    public Place(String name, String road_address, String jibun_address, String phone_number, String x, String y, String distance, String sessionId) {
        this.name = name;
        this.road_address = road_address;
        this.jibun_address = jibun_address;
        this.phone_number = phone_number;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoad_address() {
        return road_address;
    }

    public void setRoad_address(String road_address) {
        this.road_address = road_address;
    }

    public String getJibun_address() {
        return jibun_address;
    }

    public void setJibun_address(String jibun_address) {
        this.jibun_address = jibun_address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
