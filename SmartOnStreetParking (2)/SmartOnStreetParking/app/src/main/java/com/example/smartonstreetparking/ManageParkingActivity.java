package com.example.smartonstreetparking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManageParkingActivity extends Activity {
    private EditText parkingIdEditText, parkingNameEditText, latitudeEditText,
            longitudeEditText, isAvailableEditText;
    private DatabaseHelper db;

    //@SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_parking);



        parkingIdEditText = findViewById(R.id.parkingIdEditText);
        parkingNameEditText = findViewById(R.id.parkingNameEditText);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        isAvailableEditText = findViewById(R.id.isAvailable);
        Button addButton = findViewById(R.id.btnAddParking);

        db = new DatabaseHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parkingId = parkingIdEditText.getText().toString();
                String parkingName = parkingNameEditText.getText().toString();
                double latitude = Double.parseDouble(latitudeEditText.getText().toString());
                double longitude = Double.parseDouble(longitudeEditText.getText().toString());
                int isAvailable = Integer.parseInt(isAvailableEditText.getText().toString());

                boolean insertSuccessful = db.insertParkingSpot(Integer.parseInt(parkingId), parkingName, latitude, longitude, isAvailable);

                if (insertSuccessful) {
                    Toast.makeText(ManageParkingActivity.this, "Parking spot added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageParkingActivity.this, "Failed to add parking spot.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
