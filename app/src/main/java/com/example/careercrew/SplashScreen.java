package com.example.careercrew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds delay
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the theme from SharedPreferences before setting the content view
        SharedPreferences sharedPreferences = getSharedPreferences("settings_preferences", MODE_PRIVATE);
        boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d("SplashScreen", "User is logged in: " + currentUser.getEmail());
                    checkUserLastActivity(currentUser.getEmail().replace(".", ","));
                } else {
                    Log.d("SplashScreen", "No user logged in, navigating to EntryPage");
                    startActivity(new Intent(SplashScreen.this, EntryPage.class));
                    finish();
                }
            }
        }, SPLASH_DELAY);
    }

    private void checkUserLastActivity(String emailKey) {
        DatabaseReference userRef = databaseReference.child("users").child(emailKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastActivity = dataSnapshot.child("lastActivity").getValue(String.class);

                    if (lastActivity != null && !lastActivity.isEmpty()) {
                        try {
                            Class<?> activityClass = Class.forName("com.example.careercrew." + lastActivity);
                            Log.d("SplashScreen", "Navigating to last activity: " + lastActivity);
                            startActivity(new Intent(SplashScreen.this, activityClass));
                        } catch (ClassNotFoundException e) {
                            Log.e("SplashScreen", "ClassNotFoundException: " + e.getMessage());
                            startActivity(new Intent(SplashScreen.this, HomePage.class));
                        }
                    } else {
                        Log.d("SplashScreen", "No lastActivity found, navigating to HomePage");
                        startActivity(new Intent(SplashScreen.this, HomePage.class));
                    }
                } else {
                    Log.d("SplashScreen", "No user data found, navigating to Register page");
                    startActivity(new Intent(SplashScreen.this, Register.class));
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SplashScreen", "Database error: " + databaseError.getMessage());
                startActivity(new Intent(SplashScreen.this, EntryPage.class));
                finish();
            }
        });
    }
}
