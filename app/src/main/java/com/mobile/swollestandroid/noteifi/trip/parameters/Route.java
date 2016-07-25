package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Route {

    private String summary;
    private ArrayList<LatLng> overview_polyline;
    private ArrayList<String> copyrights;
    private ArrayList<String> warnings;
    private ArrayList<Legs> legs;

    public ArrayList<Legs> getLegs() {
        return legs;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
