package com.mobile.swollestandroid.noteifi.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobile.swollestandroid.noteifi.trip.parameters.Legs;
import com.mobile.swollestandroid.noteifi.trip.parameters.Route;
import com.mobile.swollestandroid.noteifi.trip.parameters.Steps;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.util.DatabaseHelper;
import com.mobile.swollestandroid.noteifi.R;
import com.mobile.swollestandroid.noteifi.util.GoogleDirectionsResponseHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnSaveRadius,  btnPlaceFind, btnAddPlace;
    private EditText etName,etRadius, etPlaceName;
    private ListView contactsListView;
    private SharedPreferences sharedPreferences;
    private int mode;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = "SETTINGSLOG";
    private DatabaseHelper dbHelper;
    private float lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnSaveRadius = (Button)findViewById(R.id.btnSaveRadius);
        btnPlaceFind = (Button)findViewById(R.id.btnPlaceFind);
        btnAddPlace = (Button)findViewById(R.id.btnAddPlaceGeofence);
        etRadius = (EditText)findViewById(R.id.etRadius);
        etPlaceName = (EditText)findViewById(R.id.etPlaceName);
        btnSaveRadius.setOnClickListener(this);
        btnPlaceFind.setOnClickListener(this);
        btnAddPlace.setOnClickListener(this);
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = v.getId();

        switch(id){
            case R.id.btnSaveRadius:
                try{
                    Float.valueOf(etRadius.getText().toString());
                    editor.putFloat("GeofenceRadius", Float.valueOf(etRadius.getText().toString()));
                    editor.commit();
                    showToast("Geofence Radius saved");
                }catch(Exception e) {
                    showToast("Please enter a valid distance");
                    etRadius.setText("");
                    etRadius.requestFocus();
                }finally{
                    break;
                }
            case R.id.btnPlaceFind:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    showToast("Service Not Available");
                }finally{
                    break;
                }
            case R.id.btnAddPlaceGeofence:
                    boolean result = dbHelper.InsertRecord(etPlaceName.getText().toString(), lat, lng);
                    if (result) {
                        showToast("Place Added");
                    }else{
                        showToast("Error Adding Place");
                    }
                    dbHelper.close();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                etPlaceName.setVisibility(View.VISIBLE);
                etPlaceName.setText(place.getName());
                lat = (float)place.getLatLng().latitude;
                lng = (float)place.getLatLng().longitude;
                btnAddPlace.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                showToast(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                showToast("Operation Canceled");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void showToast(String text){
        Toast.makeText(SettingsActivity.this, text, Toast.LENGTH_LONG).show();
    }
}
