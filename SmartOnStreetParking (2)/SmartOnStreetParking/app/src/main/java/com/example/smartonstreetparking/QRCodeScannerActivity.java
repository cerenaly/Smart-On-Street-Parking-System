package com.example.smartonstreetparking;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import android.widget.Toast;



public class QRCodeScannerActivity extends Activity {
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private Button btnScanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);  // Set the correct layout


        // Initialize the scan button using its ID
        btnScanQR = findViewById(R.id.btnScanQR);
        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for camera permission before starting the scanner
                if (ContextCompat.checkSelfPermission(QRCodeScannerActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request camera permissions if not granted
                    ActivityCompat.requestPermissions(QRCodeScannerActivity.this,
                            new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    // Permission has already been granted, start the camera scanner
                    initiateScan();
                }
            }
        });
    }


    private void initiateScan() {
        // Start the QR code scanner
        new IntentIntegrator(this)
                .setCaptureActivity(CaptureActivity.class)
                .setOrientationLocked(false)
                .setBeepEnabled(true)
                .setPrompt("Scan a QR code")
                .initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Handle the scanned data; for example:
                // doSomethingWith(result.getContents());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateScan();
            } else {
                Toast.makeText(this, "Camera permission is required to use the scanner", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
