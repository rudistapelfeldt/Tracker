package com.mobile.swollestandroid.noteifi.util;

import com.mobile.swollestandroid.noteifi.trip.parameters.Legs;
import com.mobile.swollestandroid.noteifi.trip.parameters.Route;
import com.mobile.swollestandroid.noteifi.trip.parameters.Waypoints;

import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class GoogleDirectionsResponseHandler {

    private String status;
    private Legs legs = new Legs();
    private ArrayList<Route> route = new ArrayList<>();
    private Waypoints waypoints = new Waypoints();

    public Legs getLegs() {
        return legs;
    }

    public ArrayList<Route> getRoute() {
        return route;
    }

    public Waypoints getWaypoints() {
        return waypoints;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
