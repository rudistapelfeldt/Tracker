package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.google.android.gms.maps.model.LatLng;
import com.mobile.swollestandroid.noteifi.util.RouteDetail;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Route {

    private String summary;
    private ArrayList<LatLng> overview_polyline = new ArrayList<>();
    private ArrayList<String> copyrights = new ArrayList<>();
    private String warnings;
    private ArrayList<Legs> legs = new ArrayList<>();
    private ArrayList<LatLng> listPoints = new ArrayList<>();
    public ArrayList<Legs> getLegs() {
        return legs;
    }
    private RouteDetail routeDetails = new RouteDetail();

    public RouteDetail getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(RouteDetail routeDetails) {
        this.routeDetails = routeDetails;
    }

    public ArrayList<LatLng> getListPoints() {
        return listPoints;
    }

    public void setListPoints(ArrayList<LatLng> listPoints) {
        this.listPoints = listPoints;
    }

    public String getSummary() {
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

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    private BigDecimal fare;

    public void setSummary(String summary) {
        this.summary = summary;
    }
}