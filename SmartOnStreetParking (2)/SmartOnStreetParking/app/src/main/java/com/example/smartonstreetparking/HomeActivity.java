package com.example.smartonstreetparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
    private TextView tvGreeting;
    private Button btnStartParkingSession;
    private Button btnPaymentSettings;
    private Button btnPersonalInfo;
    private TextView tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvGreeting = findViewById(R.id.tvGreeting);
        btnStartParkingSession = findViewById(R.id.btnStartParkingSession);
        btnPaymentSettings = findViewById(R.id.btnPaymentSettings);
        btnPersonalInfo = findViewById(R.id.btnProfile);
        tvLogout = findViewById(R.id.tvLogout);

        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        String userName = intent.getStringExtra("user_name");

        if (userId != -1 && userName != null) {
            tvGreeting.setText("Welcome " + userName + "!");
        } else {
            tvGreeting.setText("Welcome Guest!");
        }

        btnStartParkingSession.setOnClickListener(v -> {
            Intent parkingIntent = new Intent(HomeActivity.this, MapActivity.class);
            parkingIntent.putExtra("user_id", userId); // Pass user ID to the MapActivity
            startActivity(parkingIntent);
        });

        btnPaymentSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(HomeActivity.this, PaymentSettingsActivity.class);
            settingsIntent.putExtra("user_id", userId); // Pass user ID to the PaymentSettingsActivity
            startActivity(settingsIntent);
        });

        btnPersonalInfo.setOnClickListener(v -> {
            Intent PersonalInfoIntent = new Intent(HomeActivity.this, PersonalInformationActivity.class);
            PersonalInfoIntent.putExtra("user_id", userId); // Pass user ID to the PersonalInformationActivity
            startActivity(PersonalInfoIntent);
        });



        tvLogout.setOnClickListener(v -> logoutUser());

    }

    private void logoutUser() {
        // Here you can clear any saved data or states if necessary
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(HomeActivity.this, "Logged out successfully!", Toast.LENGTH_LONG).show();
        finish();  // Ensure this activity is closed and removed from the stack
    }
}
