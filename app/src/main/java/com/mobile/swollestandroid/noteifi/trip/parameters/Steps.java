package com.mobile.swollestandroid.noteifi.trip.parameters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rudi on 7/25/2016.
 */
public class Steps implements Serializable{

        private String htmlInstructions;
        private String distance;
        private String duration;
        private LatLong startLocation = null;
        private LatLong endLocation = null;
        private String polyline;
        private ArrayList<String> innersteps = new ArrayList<>(); //for transit mode

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

        public LatLong getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(LatLong startLocation) {
            this.startLocation = startLocation;
        }

        public LatLong getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(LatLong endLocation) {
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


