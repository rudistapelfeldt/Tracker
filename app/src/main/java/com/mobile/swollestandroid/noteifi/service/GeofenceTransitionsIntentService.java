package com.mobile.swollestandroid.noteifi.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mobile.swollestandroid.noteifi.activity.MapsActivity;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";

    private SharedPreferences sharedPreferences;

    private int mode;
    private GeofencingEvent geofencingEvent;

    private int geofenceTransition;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            int error = geofencingEvent.getErrorCode();
            Log.i(TAG,"Error Code = " +error);
            return;
        }

        geofenceTransition = geofencingEvent.getGeofenceTransition();

        //if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                //geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Log.i(TAG, "GEO ENTERED");
                    transition();
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    transition();
                    break;
                default:
                    Toast.makeText(this, R.string.geofence_transition_invalid_type, Toast.LENGTH_LONG).show();
                    break;
            }
    }

    private void transition(){
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        String geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
        );
        sendNotification(geofenceTransitionDetails, geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude());
        Log.i(TAG, geofenceTransitionDetails);
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);
        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails, double lat, double lng) {
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);
        Intent notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int)System.currentTimeMillis(), notificationIntent, 0);
        Notification builder = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(pIntent).setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder);
        Set<String> numbers = sharedPreferences.getStringSet("Recipients", null);
        if(numbers != null) {
            Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
            for (String number : numbers) {
                SmsManager sms = SmsManager.getDefault();
                String strMessage = notificationDetails + ". Location = " + lat + " , " + lng;
                sms.sendTextMessage(number, null, strMessage, null, null);
            }
        }
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:

                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}