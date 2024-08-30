package com.example.smartonstreetparking;

import android.content.Context;

public class ParkingSpotController {

    private final DatabaseHelper dbHelper;
    private boolean flag;

    public ParkingSpotController(Context context) {
        dbHelper = new DatabaseHelper(context);
        flag = false;
    }
    public boolean getFlag() {
        return flag;
    }
    public void setFlag() {
        flag = true;
    }

    public boolean checkAndUpdateSpotAvailability(int spotId) {
        return dbHelper.toggleAvailability(spotId);
    }
}