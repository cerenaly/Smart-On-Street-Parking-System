package com.example.smartonstreetparking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.SQLOutput;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "Parking.db";
    public DatabaseHelper(@Nullable Context context) {
        super(context, "Parking.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE allusers(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT, plate_no TEXT)");
            db.execSQL("CREATE TABLE parking_spot(spot_id INTEGER PRIMARY KEY, spot_name TEXT, latitude REAL, longitude REAL, is_available INTEGER)");
            db.execSQL("CREATE TABLE allocation(user_id INTEGER, spot_id INTEGER, time INTEGER, PRIMARY KEY (user_id, spot_id), FOREIGN KEY (user_id) REFERENCES allusers(id), FOREIGN KEY (spot_id) REFERENCES parking_spot(spot_id))");
            Log.d("DatabaseHelper", "Database tables created successfully");
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error creating tables", e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS allusers");
        db.execSQL("DROP TABLE IF EXISTS parking_spot");
        db.execSQL("DROP TABLE IF EXISTS allocation");
        onCreate(db);
    }

    public Boolean insertData(String name, String email, String password, String plate_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("plate_no", plate_number);
        long result = db.insert("allusers", null, contentValues);
        db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from allusers where email = ?", new String[]{email});

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, email FROM allusers WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            return cursor;  // Return the cursor pointing to the user data
        } else {
            return null;  // Login failed, return null
        }
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    public boolean insertParkingSpot(int spot_id, String spot_name, double latitude, double longitude, int isAvailable) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("spot_id", spot_id);
            values.put("spot_name", spot_name);
            values.put("latitude", latitude);
            values.put("longitude", longitude);
            values.put("is_available", isAvailable); // Expecting 1 or 0

            long result = db.insert("parking_spot", null, values);
            if (result == -1) {
                Log.e("DatabaseHelper", "Failed to insert parking spot");
                return false;
            } else {
                return true;
            }

        } finally {
            db.close(); // Ensure the database is always closed
        }
    }

    public boolean toggleAvailability(int spotId) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isStartingSession = false;
        String query = "SELECT is_available FROM parking_spot WHERE spot_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(spotId)});

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("is_available");
            if (columnIndex != -1) {
                int currentAvailability = cursor.getInt(columnIndex);
                int newAvailability = (currentAvailability == 0) ? 1 : 0;

                ContentValues values = new ContentValues();
                values.put("is_available", newAvailability);
                db.update("parking_spot", values, "spot_id = ?", new String[]{String.valueOf(spotId)});

                // Determine if the session is starting or ending
                isStartingSession = (currentAvailability == 0);
            } else {
                throw new IllegalArgumentException("The column 'is_available' does not exist.");
            }
        }
        cursor.close();
        db.close();
        return isStartingSession;
    }

    public boolean insertOrUpdateAllocation(int userId, int spotId, int time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("spot_id", spotId);
        values.put("time", time);


        long result = db.insertWithOnConflict("allocation", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
    }

    public boolean areIdsValid(int userId, int spotId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor userCursor = db.rawQuery("SELECT 1 FROM allusers WHERE id = ?", new String[] {String.valueOf(userId)});
        boolean userExists = userCursor.moveToFirst();
        userCursor.close();

        Cursor spotCursor = db.rawQuery("SELECT 1 FROM parking_spot WHERE spot_id = ?", new String[] {String.valueOf(spotId)});
        boolean spotExists = spotCursor.moveToFirst();
        spotCursor.close();

        return userExists && spotExists;
    }

    public int fetchParkingDuration(int spotId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT time FROM allocation WHERE spot_id = ?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(spotId)});

        if (cursor.moveToFirst()) {
            long hours = cursor.getLong(0);
            cursor.close();
            db.close();
            return (int)hours;
        } else {
            cursor.close();
            db.close();
            return 0; // Return 0 if no start time is found, indicating an error or no parking session
        }
    }

    public boolean deleteAllocation(int userId, int spotId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int result = db.delete("allocation", "user_id=? AND spot_id=?", new String[] {String.valueOf(userId), String.valueOf(spotId)});
            if (result > 0) {
                db.setTransactionSuccessful();
            }
            return result > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error during delete", e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

}
