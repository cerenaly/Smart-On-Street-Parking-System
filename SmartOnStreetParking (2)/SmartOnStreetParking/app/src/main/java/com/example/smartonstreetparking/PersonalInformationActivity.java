package com.example.smartonstreetparking;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PersonalInformationActivity extends Activity {

    private EditText nameSurnameEditText, phoneIdEditText, carPlateEditText, carModelEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        // Initialize EditText fields
        nameSurnameEditText = findViewById(R.id.name_surname_edittext);
        phoneIdEditText = findViewById(R.id.phone_id_edittext);
        carPlateEditText = findViewById(R.id.car_plate_edittext);
        carModelEditText = findViewById(R.id.car_model_edittext);

        Button backToHomeButton = findViewById(R.id.back_to_home_button);
        backToHomeButton.setOnClickListener(v -> {
            // Navigate back to the previous page (Homepage)
            onBackPressed();
        });
    }


}
