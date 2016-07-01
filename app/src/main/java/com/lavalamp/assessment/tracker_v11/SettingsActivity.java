package com.lavalamp.assessment.tracker_v11;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private Button  btnSaveNumber, btnSaveRadius,  btnPlaceFind, btnAddPlace;
    private EditText etNumber, etRadius, etPlaceName, etPlaceLat, etPlaceLng;
    private SharedPreferences sharedPreferences;
    private int mode;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = "SETTINGSLOG";
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnSaveRadius = (Button)findViewById(R.id.btnSaveRadius);
        btnSaveNumber = (Button)findViewById(R.id.btnSaveNumber);
        btnPlaceFind = (Button)findViewById(R.id.btnPlaceFind);
        btnAddPlace = (Button)findViewById(R.id.btnAddPlaceGeofence);
        etNumber = (EditText)findViewById(R.id.etNumber);
        etRadius = (EditText)findViewById(R.id.etRadius);
        etPlaceName = (EditText)findViewById(R.id.etPlaceName);
        etPlaceLat = (EditText)findViewById(R.id.etPlaceLat);
        etPlaceLng = (EditText)findViewById(R.id.etPlaceLng);
        btnSaveRadius.setOnClickListener(this);
        btnSaveNumber.setOnClickListener(this);
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
            case R.id.btnSaveNumber:
                showToast("Number Saved");
                editor.putString("Number", etNumber.getText().toString());
                editor.commit();
                break;
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
                    // TODO: Handle the error.
                }
            case R.id.btnAddPlaceGeofence:
                if (!etPlaceLat.getText().toString().equals("") && !etPlaceLng.getText().toString().equals("")) {
                    double latitude = Double.parseDouble(etPlaceLat.getText().toString());
                    double longitude = Double.parseDouble(etPlaceLng.getText().toString());
                    dbHelper.InsertRecord(etPlaceName.getText().toString(), latitude, longitude);
                    showToast("Place Added");
                    dbHelper.close();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "LATLNG = " + place.getLatLng().latitude + " , " + place.getLatLng().longitude);
                etPlaceName.setVisibility(View.VISIBLE);
                etPlaceLat.setVisibility(View.VISIBLE);
                etPlaceLng.setVisibility(View.VISIBLE);
                etPlaceName.setText(place.getName());
                etPlaceLat.setText(String.valueOf(place.getLatLng().latitude));
                etPlaceLng.setText(String.valueOf(place.getLatLng().longitude));
                btnAddPlace.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void showToast(String text){
        Toast.makeText(SettingsActivity.this, text, Toast.LENGTH_LONG).show();
    }

}
