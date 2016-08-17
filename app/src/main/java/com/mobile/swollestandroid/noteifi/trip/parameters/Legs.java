package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Legs {

    private ArrayList<Steps> steps = new ArrayList<>();
    private ArrayList<String> distance = new ArrayList<>();
    private ArrayList<String> duration = new ArrayList<>();
    private ArrayList<String> durationInTraffic = new ArrayList<>();
    private ArrayList<String> arrivalTime = new ArrayList<>();

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public ArrayList<String> getDistance() {
        return distance;
    }

    public ArrayList<String> getDuration() {
        return duration;
    }

    public ArrayList<String> getDurationInTraffic() {
        return durationInTraffic;
    }

    public ArrayList<String> getArrivalTime() {
        return arrivalTime;
    }
}

