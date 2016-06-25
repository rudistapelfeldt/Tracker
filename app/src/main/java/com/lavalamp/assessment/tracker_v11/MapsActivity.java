package com.lavalamp.assessment.tracker_v11;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private PendingIntent mGeofencePendingIntent;
    private double lat, lng, geofenceLat, geofenceLng;
    protected ArrayList<Geofence> mGeofenceList;
    private Button mAddGeofencesButton, mRemoveGeofenceButton;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean isGeofenceAdded = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Set buttons
        mAddGeofencesButton = (Button) findViewById(R.id.btnAddGeofence);
        mRemoveGeofenceButton = (Button)findViewById(R.id.btnRemoveGeofence);
        //LocationManager init
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Init Geofence list
        mGeofenceList = new ArrayList<Geofence>();
        //Pending Intent  for Geofence
        mGeofencePendingIntent = null;
        //Google API Client build
        buildGoogleApiClient();
        //Show GPS setting alert if not enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("TRACKERLOG", "GPS not set");
            showSettingsAlert();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
       if (id == R.id.action_settings) {
           Intent settingsIntent = new Intent(MapsActivity.this, SettingsActivity.class);
           startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            enableMyLocation();
            mGoogleApiClient.connect();
        }catch(SecurityException sec){

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (current != null) {
                LatLng currentLocation = new LatLng(current.getLatitude(), current.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(5000); //5 seconds
                mLocationRequest.setFastestInterval(3000); //3 seconds
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }catch(SecurityException sec){
            showToast(sec.getMessage());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        if(result.getStatus().isSuccess()) {
            showToast(getString(isGeofenceAdded ? R.string.geofences_added : R.string.geofences_removed));
        }else{
            showToast("Error");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    protected void setMarker(Location location){
        lat = location.getLatitude();
        lng = location.getLongitude();
        LatLng current = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(current).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }

    protected void populateGeofenceList() {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("GeoFence")
                .setCircularRegion(
                        geofenceLat,
                        geofenceLng,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    //Add Geofence method

    protected void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            showToast(getString(R.string.not_connected));
            return;
        }
        populateGeofenceList();
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(geofenceLat, geofenceLng))
                    .radius(100)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
            isGeofenceAdded = true;
            setButtonsEnabledState();
        } catch (SecurityException securityException) {
            showToast(securityException.getMessage());
        }
    }

    //Remove Geofence Method

    public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            mMap.clear();
            isGeofenceAdded = false;
            setButtonsEnabledState();
        } catch (SecurityException securityException) {
            showToast(securityException.getMessage());
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void showToast(String text){
        Toast.makeText(this,text, Toast.LENGTH_LONG).show();
    }

    private void enableMyLocation() {
        try{
            mMap.setMyLocationEnabled(true);
        }catch(SecurityException sec){
            showToast(sec.getMessage());
        }
    }

    // On location change, set the new lat, lng for the geofence
    @Override
    public void onLocationChanged(Location location) {
        geofenceLat = location.getLatitude();
        geofenceLng = location.getLongitude();
    }

    //BUTTON STATE

    private void setButtonsEnabledState() {
        if (isGeofenceAdded) {
            mAddGeofencesButton.setEnabled(false);
            mRemoveGeofenceButton.setEnabled(true);
        } else {
            mAddGeofencesButton.setEnabled(true);
            mRemoveGeofenceButton.setEnabled(false);
        }
    }
}
