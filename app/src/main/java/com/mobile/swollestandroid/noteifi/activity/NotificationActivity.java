package com.mobile.swollestandroid.noteifi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.activity.R;

import java.util.HashSet;
import java.util.Set;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener,LoaderManager.LoaderCallbacks<Cursor> {
    private EditText etName, etRecipients;
    private Set<String> mobileNumbers;
    private Button btnClearRecipients;
    private StringBuilder sb = new StringBuilder("");
    private String mSearchString;
    private ListView contactsListView;
    private SharedPreferences sharedPreferences;
    private int mode;
    SharedPreferences.Editor editor;
    private static final String TAG = "NOTIFICATIONACTIVITY";

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER};

    private final static int[] TO_IDS = {R.id.text1, R.id.text2};

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER

            };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    @SuppressLint("InlinedApi")
    private static final String SELECTION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ?" :
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mobileNumbers = new HashSet<>();
        mode = Activity.MODE_PRIVATE;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFS, mode);
        editor = sharedPreferences.edit();
        btnClearRecipients = (Button)findViewById(R.id.btnClearRecipients);
        btnClearRecipients.setOnClickListener(this);
        etRecipients = (EditText)findViewById(R.id.etRecipients);
        etName = (EditText) findViewById(R.id.etName);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchString = etName.getText().toString();

            }

            @Override
            public void afterTextChanged(Editable s) {
                getLoaderManager().restartLoader(0, null, NotificationActivity.this);
                mCursorAdapter.notifyDataSetChanged();
            }
        });
        getLoaderManager().restartLoader(0, null, NotificationActivity.this);
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        getLayoutInflater().inflate(R.layout.contact_row, null);
        mCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contact_row,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        contactsListView.setOnItemClickListener(this);
        contactsListView.setAdapter(mCursorAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] mSelectionArgs = {mSearchString};
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
        mCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.changeCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Notification Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.lavalamp.assessment.tracker_v11/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Notification Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.lavalamp.assessment.tracker_v11/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt = (TextView) parent.getChildAt(position - contactsListView.getFirstVisiblePosition()).findViewById(R.id.text2);
        String text = txt.getText().toString();
        sb.append(text + " ");
        etRecipients.setText(sb.toString());
        mobileNumbers.add(text);
        editor.putStringSet("Recipients", mobileNumbers);
        editor.commit();

    }

    @Override
    public void onClick(View v) {
        sb = new StringBuilder("");
        etRecipients.setText("");
        mobileNumbers.clear();
        editor.remove("Recipients");
        editor.commit();
    }
}
