package com.mobile.swollestandroid.noteifi.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mobile.swollestandroid.noteifi.R;
import com.mobile.swollestandroid.noteifi.asynktask.GetGoogleDirectionsTask;
import com.mobile.swollestandroid.noteifi.interfaces.AsyncGoogleDirectionResponse;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.util.GoogleDirectionsResponseHandler;
import com.mobile.swollestandroid.noteifi.util.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlanTripActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ArrayAdapter<CharSequence> adapter;
    private Spinner spOrigin, spDestination, spMode;
    private static HashMap<String, LatLng> geoMap;
    private List<String> list;
    private EditText etDepartureTime;
    //url parameters
    private long departureTime = 0;
    private String mode;
    private LatLng origin;
    private LatLng destination;
    private Button btnFindRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //****spinner adapters****
        //origin and destination
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,getList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mode
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinners
        spOrigin = (Spinner)findViewById(R.id.spinOrigin);
        spDestination = (Spinner)findViewById(R.id.spinDestination);
        spMode = (Spinner)findViewById(R.id.spinMode);
        spOrigin.setAdapter(adapter);
        spDestination.setAdapter(adapter);
        spMode.setAdapter(modeAdapter);
        spDestination.setOnItemSelectedListener(this);
        spOrigin.setOnItemSelectedListener(this);
        spMode.setOnItemSelectedListener(this);

        //edit text
        etDepartureTime = (EditText)findViewById(R.id.etDepartureTime);
        etDepartureTime.setOnClickListener(this);

        //button
        btnFindRoutes = (Button)findViewById(R.id.btnGetRoutes);
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
        switch (v.getId()){
            case R.id.etDepartureTime:
                final Calendar mCurrentDate = Calendar.getInstance();

                int mHour = mCurrentDate.get(Calendar.HOUR_OF_DAY);
                int mMinute = mCurrentDate.get(Calendar.MINUTE);
                final long currentTime = 3600 * mHour + 60 * + mMinute;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etDepartureTime.setText("" + hourOfDay + ":" + minute + ":00");
                        mCurrentDate.setTimeInMillis(currentTime / 1000L);
                        departureTime = mCurrentDate.getTimeInMillis() * 1000L;

                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.btnGetRoutes:
                GetGoogleDirectionsTask task = new GetGoogleDirectionsTask(new AsyncGoogleDirectionResponse() {
                    @Override
                    public void processFinish(GoogleDirectionsResponseHandler responseHandler) {

                    }
                });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(view.getId()){
            case R.id.spinDestination:
                destination = geoMap.get(parent.getItemAtPosition(position));
                break;
            case R.id.spinOrigin:
                origin = geoMap.get(parent.getItemAtPosition(position));
                break;
            case R.id.spinMode:
                mode = (String)parent.getItemAtPosition(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch(parent.getId()){
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

    private String getGoogleDirectionsUrl(){
        if (origin != null && destination != null && mode != null && departureTime > 0) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&mode=" + mode + "&departure_time=" + departureTime + "&key=" + R.string.google_maps_key;
            return url;
        }else if(origin != null && destination != null && mode == null && departureTime == 0){
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&key=" + R.string.google_maps_key;
            return url;

        }else if(origin != null && destination != null && mode != null && departureTime == 0){
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&mode=" + mode + "&key=" + R.string.google_maps_key;
            return url;

        }else if(origin != null && destination != null && mode == null && departureTime > 0) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination="
                    + destination.latitude + "," + destination.longitude + "&departure_time=" + departureTime + "&key=" + R.string.google_maps_key;
            return url;
        }
        return null;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
