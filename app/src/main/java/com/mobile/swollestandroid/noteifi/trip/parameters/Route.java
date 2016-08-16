package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Route {

    private ArrayList<String> summary = new ArrayList<>();
    private ArrayList<LatLng> overview_polyline = new ArrayList<>();
    private ArrayList<String> copyrights = new ArrayList<>();
    private ArrayList<String> warnings = new ArrayList<>();
    private ArrayList<Legs> legs = new ArrayList<>();

    public ArrayList<Legs> getLegs() {
        return legs;
    }

    public ArrayList<String> getSummary() {
        return summary;
    }

    public ArrayList<LatLng> getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(ArrayList<LatLng> overview_polyline) {
        this.overview_polyline = overview_polyline;
    }

    public ArrayList<String> getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(ArrayList<String> copyrights) {
        this.copyrights = copyrights;
    }

    public ArrayList<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(ArrayList<String> warnings) {
        this.warnings = warnings;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    private BigDecimal fare;

}
