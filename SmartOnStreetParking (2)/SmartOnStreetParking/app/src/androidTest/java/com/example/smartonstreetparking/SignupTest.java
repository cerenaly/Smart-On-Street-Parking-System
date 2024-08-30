package com.example.smartonstreetparking;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@RunWith(AndroidJUnit4.class)
public class SignupTest {

    @Rule
    public ActivityScenarioRule<SignupActivity> activityRule = new ActivityScenarioRule<>(SignupActivity.class);

    @Test
    public void testSignUpProcess() {           // Component Testing
        // Enter the name in the name field
        onView(withId(R.id.etName))
                .perform(typeText("example"), closeSoftKeyboard());  // Closing keyboard after each input for better visibility of elements

        // Enter the email in the email field
        onView(withId(R.id.etMail))
                .perform(typeText("test@example.com"), closeSoftKeyboard());

        // Enter the password in the password field
        onView(withId(R.id.etPassword))
                .perform(typeText("password"), closeSoftKeyboard());

        // Enter the plate in the plate field
        onView(withId(R.id.etPlate))
                .perform(typeText("plate"), closeSoftKeyboard());

        // Click the sign-up button
        onView(withId(R.id.btnSignUp)).perform(click());


        // Test if the information was inserted to the database correctly
        SQLiteDatabase db = new DatabaseHelper(InstrumentationRegistry.getInstrumentation().getTargetContext()).getReadableDatabase();
        Cursor cursor = db.query("allusers", new String[] {"email"}, "email=?", new String[] {"test@example.com"}, null, null, null);
        assertTrue("Cursor should return at least one result", cursor.moveToFirst());
        assertEquals("Email should match", "test@example.com", cursor.getString(cursor.getColumnIndex("email")));
        cursor.close();
        db.close();
    }
}
