package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Legs {

    private ArrayList<Steps> steps;
    private String distance;
    private String duration;
    private String durationInTraffic;
    private String arrivalTime;

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Steps> steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationInTraffic() {
        return durationInTraffic;
    }

    public void setDurationInTraffic(String durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    class Steps{
        private String htmlInstructions;
        private String distance;
        private String duration;
        private LatLng startLocation;
        private LatLng endLocation;
        private ArrayList<LatLng> polyline;
        private ArrayList<String> innersteps; //for transit mode

        public String getHtmlInstructions() {
            return htmlInstructions;
        }

        public void setHtmlInstructions(String htmlInstructions) {
            this.htmlInstructions = htmlInstructions;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public LatLng getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(LatLng startLocation) {
            this.startLocation = startLocation;
        }

        public LatLng getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(LatLng endLocation) {
            this.endLocation = endLocation;
        }

        public ArrayList<LatLng> getPolyline() {
            return polyline;
        }

        public void setPolyline(ArrayList<LatLng> polyline) {
            this.polyline = polyline;
        }

        public ArrayList<String> getInnersteps() {
            return innersteps;
        }

        public void setInnersteps(ArrayList<String> innersteps) {
            this.innersteps = innersteps;
        }
    }
}

