package com.example.memorialpoint.Models;

import java.util.List;

public class PlaceResponse {

    String status;
    Meta meta;
    List<Place> places;
    String errorMessage;

    public PlaceResponse(String status, Meta meta, List<Place> places, String errorMessage) {
        this.status = status;
        this.meta = meta;
        this.places = places;
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
