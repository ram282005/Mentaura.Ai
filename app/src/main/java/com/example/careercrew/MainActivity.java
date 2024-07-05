package com.example.careercrew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton dropdownMenu;
    private ImageButton messageButton;
    private TextView goalText;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LAST_ACTIVITY = "last_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastActivity = sharedPreferences.getString(LAST_ACTIVITY, SplashScreen.class.getName());

        try {
            Class<?> activityClass = Class.forName("com.example.careercrew." + lastActivity);
            startActivity(new Intent(this, activityClass));
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            setContentView(R.layout.activity_main);
            initUI();
        }
    }

    private void initUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        dropdownMenu = findViewById(R.id.dropdown_menu);
        messageButton = findViewById(R.id.message_button);
        goalText = findViewById(R.id.goal_text);
        mAuth = FirebaseAuth.getInstance();

        goalText.setText("Goal:- XYZ");

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_sign_out) {
                signOut();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        dropdownMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        messageButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Message button clicked", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            moveTaskToBack(true);
        }
    }

    private void signOut() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
