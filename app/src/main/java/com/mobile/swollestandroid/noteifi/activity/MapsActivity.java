package com.mobile.swollestandroid.noteifi.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.server.converter.StringToIntConverter;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobile.swollestandroid.noteifi.trip.parameters.LatLong;
import com.mobile.swollestandroid.noteifi.trip.parameters.Legs;
import com.mobile.swollestandroid.noteifi.trip.parameters.Route;
import com.mobile.swollestandroid.noteifi.trip.parameters.Steps;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.util.DatabaseHelper;
import com.mobile.swollestandroid.noteifi.service.GeofenceTransitionsIntentService;
import com.mobile.swollestandroid.noteifi.util.GoogleDirectionsResponseHandler;
import com.mobile.swollestandroid.noteifi.util.Model;
import com.mobile.swollestandroid.noteifi.adapter.MyAdapter;
import com.mobile.swollestandroid.noteifi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.mobile.swollestandroid.noteifi.R.layout.activity_maps2;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, ListView.OnItemClickListener, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private static GoogleMap mMap;
    private LocationManager locationManager;
    private static PendingIntent mGeofencePendingIntent;
    protected static ArrayList<Geofence> mGeofenceList;
    private static Button mAddGeofencesButton, mRemoveGeofenceButton;
    public GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean isGeofenceAdded = false;
    private static Context mContext;
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
    private SharedPreferences.Editor editor;
    private ProgressBar mapsProgressbar;
    private Map<Route,Polyline> polylines = new HashMap<>();
    private ArrayList<LatLng> listPoints = new ArrayList<>();
    private Bundle extras;
    private ViewGroup myHeader;
    private boolean isBusy = false;
    private Boolean isPolyClicked = false;
    private final ArrayList<Steps> geofenceList = new ArrayList<>();
    private final StringBuilder mOrigin = new StringBuilder("");
    private final StringBuilder mDestination = new StringBuilder("");
    //bottom sheet variables
    private View bottomSheet;
    private TextView tvbsHdr, tvbsSummary, tvbsSummaryData, tvbsWarning, tvbsWarningData, tvbsDuration, tvbsDurationData, tvbsDurationT, tvbsDurationTData;
    private Button btnbsSelectRoute;

    //selected polyline
    private Polyline selectedPolyline;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //public static GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        extras = getIntent().getExtras();

        //bottom sheet
        bottomSheet = findViewById(R.id.design_bottom_sheet);
        tvbsHdr = (TextView)findViewById(R.id.bottom_sheet_hdr);
        tvbsSummary = (TextView)findViewById(R.id.bottom_sheet_summary);
        tvbsSummaryData = (TextView)findViewById(R.id.bottom_sheet_summary_data);
        tvbsWarning = (TextView)findViewById(R.id.bottom_sheet_warning);
        tvbsWarningData = (TextView)findViewById(R.id.bottom_sheet_warning_data);
        tvbsDuration = (TextView)findViewById(R.id.bottom_sheet_duration);
        tvbsDurationData = (TextView)findViewById(R.id.bottom_sheet_duration_data);
        tvbsDurationT = (TextView)findViewById(R.id.bottom_sheet_duration_traffic);
        tvbsDurationTData = (TextView)findViewById(R.id.bottom_sheet_duration_traffic_data);
        btnbsSelectRoute = (Button)findViewById(R.id.bottom_sheet_btn_select);
        btnbsSelectRoute.setOnClickListener(this);

        final BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
        behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                                             @Override
                                             public void onStateChanged(@NonNull View bottomSheet, int newState) {
                                                 switch (newState) {
                                                     case BottomSheetBehavior.STATE_DRAGGING:
                                                         Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                                                         break;
                                                     case BottomSheetBehavior.STATE_SETTLING:
                                                         Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                                                         break;
                                                     case BottomSheetBehavior.STATE_EXPANDED:
                                                         Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                                                         break;
                                                     case BottomSheetBehavior.STATE_COLLAPSED:
                                                         Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                                                         break;
                                                     case BottomSheetBehavior.STATE_HIDDEN:
                                                         Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                                                         break;
                                                 }
                                             }

                                             @Override
                                             public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                                             }
                                         });
            //progress bar
        mapsProgressbar = (ProgressBar)findViewById(R.id.map_progressBar);
        mContext = this;
        //SharedPreferences
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);
        editor = sharedPreferences.edit();

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
        myHeader = (ViewGroup) myinflater.inflate(R.layout.list_view_header, geofenceListView, false);
        geofenceListView.addHeaderView(myHeader, null, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_main_logo);
        setActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

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
        //mGoogleApiClient.connect();

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
        mGoogleApiClient.connect();
        try {
            mMap = googleMap;
            enableMyLocation();
        } catch (SecurityException sec) {
            Log.i(TAG, sec.getMessage());
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        showToast("client connected");
        try {
            if (extras != null){
                Log.i(TAG, "Got extras");
                setUpMap((GoogleDirectionsResponseHandler)extras.getSerializable("routesResponse"));
            }
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
            isBusy = false;
            setViewVisibility(isBusy);
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

    public static void populateStepsGeofenceList(float radius, List<Steps> steps) {
        //Init Geofence list
        mGeofenceList = new ArrayList<Geofence>();
        double lat = 0.00;
        double lng = 0.00;
        Geocoder geocode = new Geocoder(mContext, Locale.getDefault());
        for(int i = 0; i < steps.size(); i++){
            if (i == (steps.size() - 1)){
                lat = steps.get(i).getEndLocation().getLat();
                lng = steps.get(i).getEndLocation().getLng();
            }else if (i < (steps.size() - 1)){
                lat = steps.get(i).getStartLocation().getLat();
                lng = steps.get(i).getStartLocation().getLng();
            }
            try {
                ListIterator<Address> iterator = geocode.getFromLocation(lat, lng, 1).listIterator();
                String reqId = iterator.next().getAddressLine(0);
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(reqId)
                        .setCircularRegion(
                                lat,
                                lng,
                                radius
                        )
                        .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }catch(IOException ioex){
                Log.e("MAPSACTIVITYLOG", ioex.getMessage());
            }
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
                                .fillColor(Color.argb(100, 233, 195, 160)));
                        isGeofenceAdded = true;
                        setButtonsEnabledState(isGeofenceAdded);
                    } catch (SecurityException securityException) {
                        showToast(securityException.getMessage());
                    }
                }
            }
        }
    }

    public static GoogleMap getMap(){
        return mMap;
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
            editor.remove("Recipients");
            editor.commit();
            setButtonsEnabledState(isGeofenceAdded);
        } catch (SecurityException securityException) {
            showToast(securityException.getMessage());
        }
    }

    public static PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    public static GeofencingRequest getGeofencingRequest() {
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

    public static void setButtonsEnabledState(boolean isGeofenceAdded) {
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
        mGoogleApiClient.connect();
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

    private void setUpMap(GoogleDirectionsResponseHandler responseHandler){
        if (responseHandler != null) {


            ArrayList<Route> routes = responseHandler.getRoute();
            for (int l = 0; l < routes.size(); l++) {
                Random rnd = new Random();
                listPoints = decodePoly(routes.get(l).getEncodedString());
                final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                Log.i("JSONPARSERLOG", "ROUTE NUMBER " + l);
                ArrayList<Legs> legs = routes.get(l).getLegs();
                for (int m = 0; m < legs.size(); m++) {

                    if (!mOrigin.toString().equals("") && !mDestination.toString().equals("")) {
                        mOrigin.append(responseHandler.getRoute().get(m).getLegs().get(m).getStartAddress());
                        responseHandler.getRoute().get(m).getRouteDetails().setOrigin(mOrigin.toString());

                        mDestination.append(responseHandler.getRoute().get(m).getLegs().get(m).getEndAddress());
                        responseHandler.getRoute().get(m).getRouteDetails().setDestination(mDestination.toString());
                    }
                }
                PolylineOptions options = new PolylineOptions().width(14).color(color).geodesic(true);

                ArrayList<LatLng> polyList = listPoints;

                //for (int z = 0; z < polyList.size(); z++) {
                //    LatLng point = polyList.get(z);
                    options.addAll(polyList);

                //}
                //options.clickable(true);

                Polyline polyLine = mMap.addPolyline(options);

                polyLine.setClickable(true);

                polylines.put(responseHandler.getRoute().get(l), polyLine);
                try {
                    if (!mGoogleApiClient.isConnected()){
                        Log.i(TAG, "cLIENT NOT CONNECTED");
                    }
                    Location current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LatLng currentLocation = new LatLng(current.getLatitude(), current.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));

                }catch (SecurityException sec){
                    Log.e("JSONPARSERLOG", sec.getMessage());

                }
                mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        Route routeInfo = new Route();
                        Log.i("JSONPARSERLOG", "POLYLINE CLICKED. POLYLINES SIZE = " + polylines.size());
                        for (Map.Entry<Route, Polyline> entry : polylines.entrySet()){
                            Log.i("JSONPARSERLOG", "POLYLINEID = " + entry.getValue().getId());
                            if(entry.getValue().getId().equals(polyline.getId())){
                                Log.i("JSONPARSERLOG", "YOU CLICKED ROUTE: " + entry.getKey().getSummary());
                                routeInfo = entry.getKey();
                                selectedPolyline = entry.getValue();
                            }
                        }
                        tvbsSummaryData.setText(routeInfo.getSummary());
                        tvbsWarningData.setText(routeInfo.getWarnings());
                        tvbsDurationData.setText(routeInfo.getRouteDetails().getDuration().toString());
                        tvbsDurationTData.setText(String.valueOf(routeInfo.getRouteDetails().getDurationInTrafficTotal()));
                        bottomSheet.setVisibility(View.VISIBLE);



                    }
                });
            }
        }else{
            Log.i("JSONPARSERLOG", "RESPONSE IS NULL");
        }
    }

    private void setRoute(Polyline polyline){
        if (!isPolyClicked) {
            Log.i("GEOFENCELOG", "POLYLINE HAS BEEN CLICKED " + polyline.getId());
            isBusy = true;
            setViewVisibility(isBusy);
            Route selectedRoute = null;
            String id = polyline.getId();
            Iterator<Map.Entry<Route, Polyline>> itr = polylines.entrySet().iterator();

            while (itr.hasNext()) {
                Map.Entry<Route, Polyline> entry = itr.next();
                if (!entry.getValue().getId().equals(id)) {
                    Log.i("JSONPARSERLOG", "Key : " + entry.getKey() + " Removed.");
                    entry.getValue().remove();
                    itr.remove();  // Call Iterator's remove method.

                } else {
                    selectedRoute = entry.getKey();
                    Log.i("GEOFENCELOG", "SELECTED ROUTE IS " + selectedRoute.getSummary());
                    ArrayList<Legs> selectedLegs = selectedRoute.getLegs();
                    for (Legs l : selectedLegs) {
                        ArrayList<Steps> selectedSteps = l.getSteps();
                        for (int r = 0; r < selectedSteps.size(); r++) {

                            //mMap.addMarker(new MarkerOptions().position(new LatLng(selectedSteps.get(r).getStartLocation().getLat(), selectedSteps.get(r).getStartLocation().getLng())));

                            Log.i("GEOFENCELOG", "ADDING STEP NO. " + r);
                            geofenceList.add(r, selectedSteps.get(r));
                            try {
                                Log.i("GEOFENCELOG", "ADDING CIRCLE NO. " + r);
                                mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(selectedSteps.get(r).getStartLocation().getLat(), selectedSteps.get(r).getStartLocation().getLng()))
                                        .radius(Constants.GEOFENCE_POINTS_RADIUS)
                                        .strokeColor(Color.RED)
                                        .strokeWidth(2)
                                        .fillColor(Color.argb(100, 233, 195, 160)));
                            } catch (SecurityException securityException) {
                                Log.e("JSONPARSELOG", securityException.getMessage());
                            }

                        }
                    }

                    if (!mOrigin.equals(mDestination)) {
                        try {
                            Log.i("GEOFENCELOG", "ADDING GEOFENCES, POPULATING GEOFENCE LIST");
                            populateStepsGeofenceList(Constants.GEOFENCE_POINTS_RADIUS, geofenceList);
                            LocationServices.GeofencingApi.addGeofences(
                                    mGoogleApiClient,
                                    getGeofencingRequest(),
                                    getGeofencePendingIntent()
                            ).setResultCallback(MapsActivity.this);
                            isGeofenceAdded = true;
                            Log.i("GEOFENCELOG", "DONE REQUESTING GEOFENCES");
                            setButtonsEnabledState(isGeofenceAdded);
                        } catch (SecurityException se) {
                            Log.e("JSONPARSERLOG", se.getMessage());
                        }
                    } else {
                        showToast("Origin and destination cannot be the same");
                    }
                }

            }
            isPolyClicked = true;
        }
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void setViewVisibility(boolean isBusy){
        if (!isBusy) {
            myHeader.setVisibility(View.VISIBLE);
            geofenceListView.setVisibility(View.VISIBLE);
            mapsProgressbar.setVisibility(View.GONE);
        }else{
            myHeader.setVisibility(View.GONE);
            geofenceListView.setVisibility(View.GONE);
            mapsProgressbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.bottom_sheet_btn_select){
            setRoute(selectedPolyline);
            bottomSheet.setVisibility(View.GONE);
        }
    }
}

