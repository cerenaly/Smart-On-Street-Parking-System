package com.example.smartonstreetparking;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;

    @Before
    public void setUp() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new DatabaseHelper(appContext);
    }

    @Test
    public void testDatabaseConnection() {
        // Try to open the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        assertTrue("Database should be open", db.isOpen());

        // Clean up
        db.close();
    }
}
