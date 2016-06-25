package com.lavalamp.assessment.tracker_v11;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private Button  btnSave;
    private EditText etNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnSave = (Button)findViewById(R.id.btnSave);
        etNumber = (EditText)findViewById(R.id.etNumber);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.contactNumber = etNumber.getText().toString();
                Toast.makeText(SettingsActivity.this, "Number Saved", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
