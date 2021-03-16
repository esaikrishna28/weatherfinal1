package com.example.weather.airmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AirResponse {

    @SerializedName("coord")
    @Expose
    private Coord coord;
    @SerializedName("list")
    @Expose
    private java.util.List<com.example.weather.airmodel.List> list = null;

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public void setList(java.util.List<List> list) {
        this.list = list;
    }
}
