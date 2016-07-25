package com.mobile.swollestandroid.noteifi.util;

import com.mobile.swollestandroid.noteifi.trip.parameters.Legs;
import com.mobile.swollestandroid.noteifi.trip.parameters.Route;
import com.mobile.swollestandroid.noteifi.trip.parameters.Waypoints;

/**
 * Created by Rudi on 7/24/2016.
 */
public class GoogleDirectionsResponseHandler {

    private String status;
    private Legs legs;
    private Route route;
    private Waypoints waypoints;

    public Legs getLegs() {
        return legs;
    }

    public Route getRoute() {
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
