package com.example.smartonstreetparking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends Activity {
    private EditText editTextName;
    private EditText editTextMail;
    private EditText editTextPassword;
    private EditText editTextPlate;
    private TextView tvGoToLogin;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.etName);
        editTextMail = findViewById(R.id.etMail);
        editTextPassword = findViewById(R.id.etPassword);
        editTextPlate = findViewById(R.id.etPlate);

        Button buttonLogin = findViewById(R.id.btnSignUp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        dbHelper = new DatabaseHelper(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.insertData(editTextName.getText().toString().trim(),editTextMail.getText().toString().trim(),
                        editTextPassword.getText().toString().trim(), editTextPlate.getText().toString().trim());
                Toast.makeText(SignupActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(signupIntent);
            }
        });
    }




}

