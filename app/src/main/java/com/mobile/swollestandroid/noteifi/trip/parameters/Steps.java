package com.mobile.swollestandroid.noteifi.trip.parameters;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Rudi on 7/25/2016.
 */
public class Steps {

        private String htmlInstructions;
        private String distance;
        private String duration;
        private LatLng startLocation;
        private LatLng endLocation;
        private String polyline;
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

        public String getPolyline() {
            return polyline;
        }

        public void setPolyline(String polyline) {
            this.polyline = polyline;
        }

        public ArrayList<String> getInnersteps() {
            return innersteps;
        }

        public void setInnersteps(ArrayList<String> innersteps) {
            this.innersteps = innersteps;
        }
    }


