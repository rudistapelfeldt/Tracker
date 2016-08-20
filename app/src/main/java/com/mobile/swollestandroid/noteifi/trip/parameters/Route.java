package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.mobile.swollestandroid.noteifi.util.RouteDetail;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Route implements Serializable{

    private String summary;
    private ArrayList<String> copyrights = new ArrayList<>();
    private String warnings;
    private String encodedString;
    private ArrayList<Legs> legs = new ArrayList<>();
    public ArrayList<Legs> getLegs() {
        return legs;
    }
    private RouteDetail routeDetails = new RouteDetail();

    public String getEncodedString() {
        return encodedString;
    }

    public void setEncodedString(String encodedString) {
        this.encodedString = encodedString;
    }

    public RouteDetail getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(RouteDetail routeDetails) {
        this.routeDetails = routeDetails;
    }

    public String getSummary() {
        return summary;
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