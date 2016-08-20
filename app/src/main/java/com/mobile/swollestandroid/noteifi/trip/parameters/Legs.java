package com.mobile.swollestandroid.noteifi.trip.parameters;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Legs implements Serializable {

    private String startAddress;
    private String endAddress;
    private ArrayList<Steps> steps = new ArrayList<>();
    private ArrayList<String> distance = new ArrayList<>();
    private ArrayList<String> duration = new ArrayList<>();
    private ArrayList<String> durationInTraffic = new ArrayList<>();
    private ArrayList<String> arrivalTime = new ArrayList<>();

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

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

