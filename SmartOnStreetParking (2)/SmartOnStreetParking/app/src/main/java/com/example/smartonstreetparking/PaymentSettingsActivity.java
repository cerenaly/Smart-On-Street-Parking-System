package com.example.smartonstreetparking;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentSettingsActivity extends Activity {

    private RadioGroup paymentRadioGroup;
    private RadioButton payByFlatRateRadio;
    private RadioButton payByDurationRadio;
    private RadioButton lastCheckedRadioButton; // Track the last checked radio button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_settings);

        paymentRadioGroup = findViewById(R.id.payment_radio_group);
        payByFlatRateRadio = findViewById(R.id.pay_by_flat_rate_radio);
        payByDurationRadio = findViewById(R.id.pay_by_duration_radio);

        Button backToHomeButton = findViewById(R.id.back_to_home_button);
        backToHomeButton.setOnClickListener(v -> {
            // Navigate back to the previous page (Homepage)
            onBackPressed();
        });
        // Set initial colors
        payByFlatRateRadio.setTextColor(Color.BLACK);
        payByDurationRadio.setTextColor(Color.BLACK);

        // Set listener for radio button changes
        paymentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = findViewById(checkedId);
            if (checkedRadioButton != null) {
                // Check if the newly checked radio button is different from the last checked one
                if (lastCheckedRadioButton != null && lastCheckedRadioButton != checkedRadioButton) {
                    // Set the color of the last checked radio button to black
                    lastCheckedRadioButton.setTextColor(Color.BLACK);
                }

                // Update the last checked radio button
                lastCheckedRadioButton = checkedRadioButton;

                String selectedOption = checkedRadioButton.getText().toString();
                if ("Pay by Duration Time".equals(selectedOption)) {
                    checkedRadioButton.setTextColor(Color.parseColor("#A099EE"));
                } else if ("Pay by Flat Rate".equals(selectedOption)) {
                    checkedRadioButton.setTextColor(Color.parseColor("#A099EE"));
                } else {
                    // Default case
                }
            }
        });
    }
}
