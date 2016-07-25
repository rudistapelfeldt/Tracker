package com.mobile.swollestandroid.noteifi.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.util.DatabaseHelper;
import com.mobile.swollestandroid.noteifi.service.GeofenceTransitionsIntentService;
import com.mobile.swollestandroid.noteifi.util.Model;
import com.mobile.swollestandroid.noteifi.adapter.MyAdapter;
import com.mobile.swollestandroid.noteifi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mobile.swollestandroid.noteifi.R.layout.activity_maps2;

public class MapsActivity extends FragmentActivity implements ListView.OnItemClickListener, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private PendingIntent mGeofencePendingIntent;
    protected ArrayList<Geofence> mGeofenceList;
    private Button mAddGeofencesButton, mRemoveGeofenceButton;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean isGeofenceAdded = false;
    private SharedPreferences sharedPreferences;
    private int mode;
    private DatabaseHelper databaseHelper;
    private ListView geofenceListView;
    private ArrayAdapter<Model> adapter;
    private List<Model> list = new ArrayList<Model>();
    private static HashMap<String, LatLng> geoMap;
    private HeaderViewListAdapter hlva;
    private MyAdapter postAdapter;
    private String TAG = "MapsActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_maps2);
        //SharedPreferences
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);
        // Set buttons
        mAddGeofencesButton = (Button) findViewById(R.id.btnAddGeofence);
        mRemoveGeofenceButton = (Button) findViewById(R.id.btnRemoveGeofence);
        mRemoveGeofenceButton.setEnabled(false);
        //LocationManager init
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Pending Intent  for Geofence
        mGeofencePendingIntent = null;
        //Google API Client build
        buildGoogleApiClient();
        //Show GPS setting alert if not enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsAlert();
        }
        databaseHelper = new DatabaseHelper(this);
        geoMap = Constants.getGeoMap();
        geofenceListView = (ListView) findViewById(R.id.lvChooseGeofence);
        LayoutInflater myinflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.list_view_header, geofenceListView, false);
        geofenceListView.addHeaderView(myHeader, null, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initListView() {
        //fill hashmap with name, lat and lang from database
        populateMap();
        adapter = new MyAdapter(this, populateList());
        geofenceListView.setAdapter(adapter);
        geofenceListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        CheckBox checkbox = (CheckBox) v.getTag(R.id.cbListRow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_add_notification:
                Intent notificationIntent = new Intent(MapsActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.action_plan_journey:
                Intent planTripIntent = new Intent(MapsActivity.this, PlanTripActivity.class);
                startActivity(planTripIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            enableMyLocation();
            mGoogleApiClient.connect();
        } catch (SecurityException sec) {

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (current != null) {
                LatLng currentLocation = new LatLng(current.getLatitude(), current.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
            }
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000); //5 seconds
            mLocationRequest.setFastestInterval(3000); //3 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException sec) {
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
        if (result.getStatus().isSuccess()) {
            showToast(getString(isGeofenceAdded ? R.string.geofences_added : R.string.geofences_removed));
        } else {
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

    protected void populateGeofenceList(float radius, List<String> selected) {
        //Init Geofence list
        mGeofenceList = new ArrayList<Geofence>();
        for (String check : selected) {

            double lat = geoMap.get(check).latitude;
            double lng = geoMap.get(check).longitude;
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(check.toString())
                    .setCircularRegion(
                            lat,
                            lng,
                            radius
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    //Add Geofence method

    public void addGeofencesButtonHandler(View view) {
        hlva = (HeaderViewListAdapter) geofenceListView.getAdapter();
        postAdapter = (MyAdapter) hlva.getWrappedAdapter();
        ;
        if (postAdapter.getSelectBoxes().size() == 0) {
            AlertDialog.Builder addPlace = new AlertDialog.Builder(this);
            addPlace.setIcon(R.mipmap.ic_launcher).setTitle("Add Geofence Places")
                    .setMessage("Please add a place. If \"PLACES\" is emply then find a place in settings")
                    .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            addPlace.show();
        } else {
            float radius = sharedPreferences.getFloat("GeofenceRadius", 0);
            if (!mGoogleApiClient.isConnected()) {
                showToast(getString(R.string.not_connected));
                return;
            }
            if (radius == 0.0) {
                showToast("Please Set Geofence Radius");
            } else {
                populateGeofenceList(radius, postAdapter.getSelectBoxes());
                for (String cb : postAdapter.getSelectBoxes()) {
                    Log.i(TAG, "CHECKBOX SELECTED to add" + cb);
                    double lat = geoMap.get(cb).latitude;
                    double lng = geoMap.get(cb).longitude;
                    try {
                        LocationServices.GeofencingApi.addGeofences(
                                mGoogleApiClient,
                                getGeofencingRequest(),
                                getGeofencePendingIntent()
                        ).setResultCallback(this);
                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(lat, lng))
                                .radius(radius)
                                .strokeColor(Color.RED)
                                .fillColor(Color.BLUE));
                        isGeofenceAdded = true;
                        setButtonsEnabledState();
                    } catch (SecurityException securityException) {
                        showToast(securityException.getMessage());
                    }
                }
            }
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
            batchPickUnselectAll();
            postAdapter.notifyDataSetChanged();
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

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void enableMyLocation() {
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException sec) {
            showToast(sec.getMessage());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    // On location change, set the new lat, lng for the geofence
    @Override
    public void onLocationChanged(Location location) {

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

    private void populateMap() {
        Cursor cursor = databaseHelper.getAllRecords();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                geoMap.put(cursor.getString(cursor.getColumnIndex("NAME")), new LatLng(cursor.getDouble(cursor.getColumnIndex("LATITUDE")), cursor.getDouble(cursor.getColumnIndex("LONGITUDE"))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        databaseHelper.close();
    }

    private List<Model> populateList() {
        list = new ArrayList<>();
        if (geoMap.size() > 0) {
            for (Map.Entry<String, LatLng> entry : geoMap.entrySet()) {
                list.add(new Model(entry.getKey()));
            }
        }
        return list;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now`
        outState.putSerializable("HashMap", geoMap);
        outState.putBoolean("GeofenceAdded", isGeofenceAdded);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void batchPickUnselectAll() {

        if (!geofenceListView.getAdapter().isEmpty()) {

            geofenceListView.setItemChecked(0, true);

            geofenceListView.clearChoices();

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(null),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.mobile.swollestandroid.noteifi/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(null),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.mobile.swollestandroid.noteifi.activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }
}

