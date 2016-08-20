package com.mobile.swollestandroid.noteifi.trip.parameters;

import java.io.Serializable;

/**
 * Created by Rudi on 8/19/2016.
 */
public class LatLong implements Serializable{

    double lat = 0;
    double lng = 0;

    public LatLong(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
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
}
