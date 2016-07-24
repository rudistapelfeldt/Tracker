package com.mobile.swollestandroid.noteifi.util;

/**
 * Created by Rudolph on 2016/06/21.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    private static final HashMap<String, LatLng> GEO_MAP = new HashMap<>();

    public static String contactNumber;

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static float GEOFENCE_RADIUS_IN_METERS;

    public static final String MY_PREFS = "trackerSharedPreferences";

    public static HashMap<String, LatLng> getGeoMap() {
        return GEO_MAP;
    }
}
