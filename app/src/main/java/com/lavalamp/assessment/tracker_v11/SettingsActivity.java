package com.lavalamp.assessment.tracker_v11;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
    private Button btnSaveRadius,  btnPlaceFind, btnAddPlace;
    private EditText etNumber, etRadius, etPlaceName, etPlaceLat, etPlaceLng;
    private AutoCompleteTextView autoName;
    private ListView contactsListView;
    private SharedPreferences sharedPreferences;
    private int mode;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = "SETTINGSLOG";
    private DatabaseHelper dbHelper;
    @SuppressLint("InlinedApi")
    private final static String [] FROM_COLUMNS = {Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ,ContactsContract.CommonDataKinds.Phone.NUMBER};

    private final static int[] TO_IDS = {R.id.text1, R.id.text2};

    @SuppressLint("InlinedApi")
    private static final String [] PROJECTION =
            {
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER

            };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    @SuppressLint("InlinedApi")
    private static final String SELECTION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ?" :
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    private String mSearchString = "Ursula";
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };

    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;
    ArrayList<String> contactNames = new ArrayList<String>();
    ContentResolver cr;
    Cursor nameCur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cr = getContentResolver();
        nameCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (nameCur.moveToNext())
        {
            String name = nameCur.getString(nameCur.getColumnIndex(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY: ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactNames.add(name);
        }
        nameCur.close();
        String[] cNames = new String[contactNames.size()];
        contactNames.toArray(cNames);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, contactNames);
        btnSaveRadius = (Button)findViewById(R.id.btnSaveRadius);

        btnPlaceFind = (Button)findViewById(R.id.btnPlaceFind);
        btnAddPlace = (Button)findViewById(R.id.btnAddPlaceGeofence);
        autoName = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        autoName.setAdapter(adapter);
        etRadius = (EditText)findViewById(R.id.etRadius);
        etPlaceName = (EditText)findViewById(R.id.etPlaceName);
        btnSaveRadius.setOnClickListener(this);

        btnPlaceFind.setOnClickListener(this);
        btnAddPlace.setOnClickListener(this);
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);
        dbHelper = new DatabaseHelper(this);
        getLoaderManager().initLoader(0, null, this);
        getLayoutInflater().inflate(R.layout.contact_row, null);
        contactsListView = (ListView)findViewById(R.id.contactsListView);
        Log.i(TAG, "ID size = " + TO_IDS.length);
        Log.i(TAG, "FROM size = " + FROM_COLUMNS.length);
        // Gets a CursorAdapter
        getLayoutInflater().inflate(R.layout.contact_row, null);
        mCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contact_row,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        contactsListView.setAdapter(mCursorAdapter);

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
                    showToast("Service Not Available");
                }finally{
                    break;
                }
            case R.id.btnAddPlaceGeofence:
                if (!etPlaceLat.getText().toString().equals("") && !etPlaceLng.getText().toString().equals("")) {
                    float latitude = Float.parseFloat(etPlaceLat.getText().toString());
                    float longitude = Float.parseFloat(etPlaceLng.getText().toString());
                    boolean result = dbHelper.InsertRecord(etPlaceName.getText().toString(), latitude, longitude);
                    if (result) {
                        showToast("Place Added");
                    }else{
                        showToast("Error Adding Place");
                    }
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
                etPlaceName.setVisibility(View.VISIBLE);
                etPlaceName.setText(place.getName());
                btnAddPlace.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                showToast(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                showToast("Operation Canceled");
            }
        }
    }

    private void showToast(String text){
        Toast.makeText(SettingsActivity.this, text, Toast.LENGTH_LONG).show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
