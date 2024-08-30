package com.example.smartonstreetparking;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ParkingDurationActivity extends Activity {
    private EditText durationInput;
    private Button submitButton;
    private int userId, spotId;
    private DatabaseHelper dbHelper;
    private Handler handler = new Handler();
    private Runnable checkEndTime;

    private Context context;


    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_duration);

        context = ParkingDurationActivity.this;

        dbHelper = new DatabaseHelper(this);
        durationInput = findViewById(R.id.durationInput);
        submitButton = findViewById(R.id.submitButton);

        // Retrieve user_id and spot_id from the intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        spotId = intent.getIntExtra("spot_id", -1);
        //spotId = 1111;

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int duration = Integer.parseInt(durationInput.getText().toString());
                    if (duration > 0 && userId != -1 && spotId != -1) {
                        if (dbHelper.areIdsValid(userId, spotId)) {
                            long endTime = System.currentTimeMillis() + duration * 3600000L; // Convert hours to milliseconds
                            endTime = System.currentTimeMillis() + duration * 60000L; // As prototype consider hours as minutes
                            sendNotification(duration); // Send notification when duration is set
                            saveDurationToDatabase(userId, spotId, duration);
                        } else {
                            Toast.makeText(ParkingDurationActivity.this, "Invalid user ID or spot ID", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(ParkingDurationActivity.this, "Invalid input or missing information", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(ParkingDurationActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDurationToDatabase(int userId, int spotId, int duration) {
        boolean isSuccess = dbHelper.insertOrUpdateAllocation(userId, spotId, duration);
        if (isSuccess) {
            Toast.makeText(this, "Parking duration set successfully", Toast.LENGTH_LONG).show();
            finish(); // Optionally close this activity
        } else {
            Toast.makeText(this, "Failed to set parking duration", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(int durationInput) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, PERMISSION_REQUEST_CODE);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        // Calculate the end time based on duration (in minutes) PROTOTYPE
        long endTimeMillis = System.currentTimeMillis() + durationInput * 60000L;

        // Calculate the time difference to trigger the notification (10 seconds before end)
        long notifyTimeMillis = endTimeMillis - 10000;

        // Schedule the notification using a Handler
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Build and send the notification
                buildAndSendNotification();
            }
        }, notifyTimeMillis - System.currentTimeMillis());


    }


    private void buildAndSendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ParkingDurationActivity.this, "My Notification");
        builder.setContentTitle("Smart On-Street Parking System");
        builder.setContentText("This is a notice to remind you that the parking session is over in 5 minutes! ");
        builder.setSmallIcon(R.drawable.car_icon);
        builder.setAutoCancel(true);
        try {
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ParkingDurationActivity.this);
            managerCompat.notify(1, builder.build());
            // Toast.makeText(this, "Notification sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the notification
                submitButton.setOnClickListener(v -> {
                    // Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                    buildAndSendNotification();
                });
            } else {
                Toast.makeText(this, "Permission denied for notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }


}