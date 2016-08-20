package com.mobile.swollestandroid.noteifi.trip.parameters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rudi on 7/24/2016.
 */
public class Waypoints implements Serializable{

    private ArrayList<String> placeID = new ArrayList<>();

    public ArrayList<String> getPlaceID() {
        return placeID;
    }
}
