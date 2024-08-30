package com.example.smartonstreetparking;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView tvSignUp;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.etLoginEmail);
        editTextPassword = findViewById(R.id.etLoginPassword);
        Button buttonLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        dbHelper = new DatabaseHelper(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                Cursor userCursor = dbHelper.checkEmailPassword(email, password);
                if (userCursor != null && userCursor.moveToFirst()) {
                    // Safely retrieve data from the cursor with checks for valid indices
                    int idIndex = userCursor.getColumnIndex("id");
                    int nameIndex = userCursor.getColumnIndex("name");
                    if (idIndex != -1 && nameIndex != -1) {
                        int userId = userCursor.getInt(idIndex);
                        String userName = userCursor.getString(nameIndex);
                        Toast.makeText(LoginActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("user_id", userId);
                        intent.putExtra("user_name", userName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: Necessary columns not found.", Toast.LENGTH_SHORT).show();
                    }
                    userCursor.close(); // Make sure to close the cursor
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    if (userCursor != null) {
                        userCursor.close();
                    }
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });



    }
}
