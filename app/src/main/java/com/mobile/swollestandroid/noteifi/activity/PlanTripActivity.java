package com.mobile.swollestandroid.noteifi.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobile.swollestandroid.noteifi.R;
import com.mobile.swollestandroid.noteifi.asynktask.GetGoogleDirectionsTask;
import com.mobile.swollestandroid.noteifi.fragment.RouteDetailFragment;
import com.mobile.swollestandroid.noteifi.interfaces.AsyncGoogleDirectionResponse;
import com.mobile.swollestandroid.noteifi.service.GeofenceTransitionsIntentService;
import com.mobile.swollestandroid.noteifi.trip.parameters.Legs;
import com.mobile.swollestandroid.noteifi.trip.parameters.Route;
import com.mobile.swollestandroid.noteifi.trip.parameters.Steps;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.util.GoogleDirectionsResponseHandler;
import com.mobile.swollestandroid.noteifi.util.Model;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;


public class PlanTripActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemSelectedListener, ResultCallback {
    private ArrayAdapter<CharSequence> adapter;
    private ArrayList<Geofence> mGeofenceList;
    private Spinner spOrigin, spDestination, spMode;
    private static HashMap<String, LatLng> geoMap;
    private List<String> list;
    private EditText etDepartureTime;
    private boolean isGeofenceAdded = false;
    private PendingIntent mGeofencePendingIntent;
    protected GoogleApiClient mGoogleApiClient;
    //url parameters
    private long departureTime = 0;
    private String mode;
    private LatLng origin;
    private LatLng destination;
    private Button btnFindRoutes;
    private ProgressBar progressBar;
    private MapsActivity mapsActivity;


    private Bundle args;
    private Map<Route,Polyline> polylines = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trip);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //instance of MapsActivity
        mapsActivity = new MapsActivity();
        //****spinner adapters****
        //origin and destination
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mode
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mGeofencePendingIntent = null;
        buildGoogleApiClient();
        //spinners
        spOrigin = (Spinner) findViewById(R.id.spinOrigin);
        spDestination = (Spinner) findViewById(R.id.spinDestination);
        spMode = (Spinner) findViewById(R.id.spinMode);
        spOrigin.setAdapter(adapter);
        spDestination.setAdapter(adapter);
        spMode.setAdapter(modeAdapter);
        spDestination.setOnItemSelectedListener(this);
        spOrigin.setOnItemSelectedListener(this);
        spMode.setOnItemSelectedListener(this);

        //edit text
        etDepartureTime = (EditText) findViewById(R.id.etDepartureTime);
        etDepartureTime.setOnClickListener(this);

        //button
        btnFindRoutes = (Button) findViewById(R.id.btnGetRoutes);
        btnFindRoutes.setOnClickListener(this);


    }

    private List<String> getList() {
        list = new ArrayList<>();
        geoMap = Constants.getGeoMap();
        if (geoMap.size() > 0) {
            for (Map.Entry<String, LatLng> entry : geoMap.entrySet()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etDepartureTime:
                Calendar mCurrentDate = Calendar.getInstance();
                Date now = new Date();
                Log.i("DATECALLOG", "UTIL DATE = " + now);
                mCurrentDate.setTime(now);

                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mCurrentDate.get(Calendar.MONTH) + 1;
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mHour = mCurrentDate.get(Calendar.HOUR_OF_DAY);
                int mMinute = mCurrentDate.get(Calendar.MINUTE);
                Log.i("DATECALLOG", mYear + "/" + mMonth + "/" + mDay);
                final long dateNotTime = 31556926 * mYear + 2629743 + mMonth + 86400 * mDay;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etDepartureTime.setText(String.valueOf(hourOfDay).concat(":").concat(String.valueOf(minute)).concat(":00"));
                        hideSoftKeyboard(PlanTripActivity.this);
                        long currentTime = (3600 * hourOfDay + 60 * minute) * 1000;
                        long now = System.currentTimeMillis() + currentTime;
                        departureTime = now / 1000;
                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.btnGetRoutes:
                if (mGoogleApiClient.isConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    GetGoogleDirectionsTask task = new GetGoogleDirectionsTask(new AsyncGoogleDirectionResponse() {
                        @Override
                        public void processFinish(final GoogleDirectionsResponseHandler responseHandler) {
                            if (responseHandler != null) {

                                ArrayList<Route> routes = responseHandler.getRoute();
                                final ArrayList<Steps> geofenceList = new ArrayList<>();
                                for (int l = 0; l < routes.size(); l++) {
                                    Random rnd = new Random();
                                    final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                                    Log.i("JSONPARSERLOG", "ROUTE NUMBER " + l);
                                    ArrayList<Legs> legs = routes.get(l).getLegs();
                                    for (int m = 0; m < legs.size(); m++) {

                                        responseHandler.getRoute().get(m).getRouteDetails().setOrigin(spOrigin.getSelectedItem().toString());
                                        responseHandler.getRoute().get(m).getRouteDetails().setDestination(spDestination.getSelectedItem().toString());
                                    }
                                    PolylineOptions options = new PolylineOptions().width(10).color(color).geodesic(true);

                                    ArrayList<LatLng> polyList = responseHandler.getRoute().get(l).getListPoints();
                                    for (int z = 0; z < polyList.size(); z++) {
                                        LatLng point = polyList.get(z);
                                        options.add(point);

                                    }
                                    options.clickable(true);
                                    Polyline polyLine = MapsActivity.getMap().addPolyline(options);
                                    polyLine.setClickable(true);
                                    polylines.put(responseHandler.getRoute().get(l), polyLine);
                                    try {
                                        Location current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                        LatLng currentLocation = new LatLng(current.getLatitude(), current.getLongitude());
                                        MapsActivity.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                                        Log.i("GEOFENCELOG", "LEAVING ACTIVITY");
                                        progressBar.setVisibility(View.GONE);
                                        Log.i("GEOFENCELOG", "TURNED OFF PROGRESSBAR");
                                        finish();
                                    }catch (SecurityException sec){
                                        Log.e("JSONPARSERLOG", sec.getMessage());

                                    }
                                    MapsActivity.getMap().setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                                        @Override
                                        public void onPolylineClick(Polyline polyline) {
                                            Log.i("GEOFENCELOG", "POLYLINE HAS BEEN CLICKED " + polyline.getId());
                                            MapsActivity.getMapsProgressbar().setVisibility(View.VISIBLE);
                                           Route selectedRoute = null;
                                            String id = polyline.getId();
                                            Iterator<Map.Entry<Route, Polyline>> itr = polylines.entrySet().iterator();

                                            while(itr.hasNext())
                                            {
                                                Map.Entry<Route, Polyline> entry = itr.next();
                                                if(!entry.getValue().getId().equals(id)){
                                                    Log.i("JSONPARSERLOG", "Key : "+entry.getKey()+" Removed.");
                                                    entry.getValue().remove();
                                                    itr.remove();  // Call Iterator's remove method.
                                                }else{
                                                    selectedRoute = entry.getKey();
                                                    Log.i("GEOFENCELOG", "SELECTED ROUTE IS " + selectedRoute.getSummary());
                                                }
                                            }

                                            ArrayList<Legs> selectedLegs = selectedRoute.getLegs();
                                            for (Legs l : selectedLegs){
                                                ArrayList<Steps> selectedSteps = l.getSteps();
                                                for (int r = 0; r < selectedSteps.size();r++){
                                                    Log.i("GEOFENCELOG", "ADDING STEP NO. " + r);
                                                    geofenceList.add(r, selectedSteps.get(r));
                                                    try {
                                                        Log.i("GEOFENCELOG", "ADDING CIRCLE NO. " + r);
                                                        MapsActivity.getMap().addCircle(new CircleOptions()
                                                                .center(new LatLng(selectedSteps.get(r).getStartLocation().latitude, selectedSteps.get(r).getStartLocation().longitude))
                                                                .radius(Constants.GEOFENCE_POINTS_RADIUS)
                                                                .strokeColor(Color.RED)
                                                                .fillColor(Color.argb(100, 233, 195, 160)));
                                                    } catch (SecurityException securityException) {
                                                        Log.e("JSONPARSELOG", securityException.getMessage());
                                                    }
                                                }
                                            }

                                            if (!spOrigin.getSelectedItem().equals(spDestination.getSelectedItem())) {
                                                try {
                                                    Log.i("GEOFENCELOG", "ADDING GEOFENCES, POPULATING GEOFENCE LIST");
                                                    MapsActivity.populateStepsGeofenceList(Constants.GEOFENCE_POINTS_RADIUS, geofenceList);
                                                    LocationServices.GeofencingApi.addGeofences(
                                                            MapsActivity.mGoogleApiClient,
                                                            MapsActivity.getGeofencingRequest(),
                                                            MapsActivity.getGeofencePendingIntent()
                                                    ).setResultCallback(PlanTripActivity.this);
                                                    isGeofenceAdded = true;
                                                    Log.i("GEOFENCELOG", "DONE REQUESTING GEOFENCES");
                                                    MapsActivity.setButtonsEnabledState(isGeofenceAdded);
                                                } catch (SecurityException se) {
                                                    Log.e("JSONPARSERLOG", se.getMessage());
                                                }
                                            } else {
                                                showToast("Origin and destination cannot be the same");
                                            }
                                        }
                                    });
                                }
                            }


                        }
                    });
                    task.setProgressBar(progressBar);
                    Log.i("JSONPARSERLOG", "URL IS = " + getGoogleDirectionsUrl());
                    task.execute(getGoogleDirectionsUrl());
                    break;
                } else {
                    showToast("client not connected");
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String input = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.spinDestination:
                Log.i("JSONPARSERLOG", "DESTINATION = " + parent.getItemAtPosition(position));
                destination = geoMap.get(input);
                break;
            case R.id.spinOrigin:
                Log.i("JSONPARSERLOG", "origin = " + parent.getItemAtPosition(position));
                origin = geoMap.get(input);
                break;
            case R.id.spinMode:
                mode = (String) parent.getItemAtPosition(position);
                Log.i("JSONPARSERLOG", "mode = " + parent.getItemAtPosition(position));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()) {
            case R.id.spinDestination:
                showToast("Please select a destination");
                break;
            case R.id.spinOrigin:
                showToast("Please select an origin");
                break;
            case R.id.spinMode:
                showToast("Please select a mode");
                break;
        }
    }

    private String getGoogleDirectionsUrl() {
        if (origin != null && destination != null && mode != null && departureTime > 0) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&mode=" + mode + "&departure_time=" + departureTime + "&alternatives=true&key=" + Constants.GOOGLE_DIRECTIONS_SERVER_ID;
            return url;
        } else if (origin != null && destination != null && mode == null && departureTime == 0) {

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&alternatives=true&key=" + Constants.GOOGLE_DIRECTIONS_SERVER_ID;
            return url;

        } else if (origin != null && destination != null && mode != null && departureTime == 0) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&mode=" + mode + "&alternatives=true&key=" + Constants.GOOGLE_DIRECTIONS_SERVER_ID;
            return url;

        } else if (origin != null && destination != null && mode == null && departureTime > 0) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&departure_time=" + departureTime + "&alternatives=true&key=" + Constants.GOOGLE_DIRECTIONS_SERVER_ID;
            return url;
        }
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResult(@NonNull Result result) {
        if (result.getStatus().isSuccess()) {
            Log.i("GEOFENCELOG", "SUCCESSFULLY CREATED GEOFENCES. isGeofenceAdded = " + isGeofenceAdded);
            MapsActivity.getMapsProgressbar().setVisibility(View.GONE);
            showToast(getString(isGeofenceAdded ? R.string.geofences_added : R.string.geofences_removed));


        } else {
            showToast("Error");
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        showToast("Client Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
}