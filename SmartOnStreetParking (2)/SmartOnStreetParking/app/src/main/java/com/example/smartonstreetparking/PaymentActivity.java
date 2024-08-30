package com.example.smartonstreetparking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends Activity {
    private TextView hoursTextView;
    private TextView costTextView;
    private Button payButton;

    private int userId;
    private int spotId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        hoursTextView = findViewById(R.id.hoursTextView);
        costTextView = findViewById(R.id.costTextView);
        payButton = findViewById(R.id.payButton);

        // Retrieve the duration, user_id, and spot_id from the intent
        int hours = getIntent().getIntExtra("hours", 0);
        userId = getIntent().getIntExtra("user_id", -1);
        spotId = getIntent().getIntExtra("spot_id", -1);
        int cost = hours * 10; // Assuming the cost is $10 per hour

        hoursTextView.setText("Hours Parked: " + hours);
        costTextView.setText("Total Cost: $" + cost);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete the allocation after payment
                DatabaseHelper dbHelper = new DatabaseHelper(PaymentActivity.this);
                if (dbHelper.deleteAllocation(userId, spotId)) {
                    Toast.makeText(PaymentActivity.this, "Payment Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Payment Failed or Session Not Cleared", Toast.LENGTH_LONG).show();
                }
                finish();  // Close this activity and return to the previous screen
            }
        });
    }
}


