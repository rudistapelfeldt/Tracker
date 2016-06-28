package com.lavalamp.assessment.tracker_v11;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private Button  btnSaveNumber, btnSaveRadius,  btnSearch ;
    private EditText etNumber, etRadius, etSearch, etLat, etLng;
    private SharedPreferences sharedPreferences;
    private int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSaveRadius = (Button)findViewById(R.id.btnSaveRadius);
        btnSaveNumber = (Button)findViewById(R.id.btnSaveNumber);
        etNumber = (EditText)findViewById(R.id.etNumber);
        etRadius = (EditText)findViewById(R.id.etRadius);
        etSearch = (EditText)findViewById(R.id.etSearch);
        etLat = (EditText)findViewById(R.id.etLat);
        etLng = (EditText)findViewById(R.id.etLng);
        etLat.setEnabled(false);
        etLng.setEnabled(false);
        btnSaveRadius.setOnClickListener(this);
        btnSaveNumber.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);

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
            case R.id.btnSearch:
                String input = etSearch.getText().toString().replace(" ", "+");
                String url = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=" + input + "&sensor=true&key=AIzaSyBKkCNPQ7QmcPHkdAImrf_8Oa0GiFzUXwU";
                Log.i("SEARCHLOG", "URL = " + url);
                SearchLocationTask searchLocationTask = new SearchLocationTask(new AsyncLocationResponse() {
                    @Override
                    public void processFinish(String [] text) {

                    }
                });
                searchLocationTask.execute(url);
        }
    }

    private void showToast(String text){
        Toast.makeText(SettingsActivity.this, text, Toast.LENGTH_LONG).show();
    }

}
