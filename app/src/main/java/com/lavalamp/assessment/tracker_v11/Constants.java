package com.lavalamp.assessment.tracker_v11;

/**
 * Created by Rudolph on 2016/06/21.
 */
/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static String contactNumber;

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100; //100 meters

}
