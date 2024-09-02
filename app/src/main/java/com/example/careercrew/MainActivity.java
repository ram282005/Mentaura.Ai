package com.example.careercrew;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton dropdownMenu;
    private TextView goalText;
    private TextView userNameTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LAST_ACTIVITY = "last_activity";
    private static final String CHOSEN_ROLE = "chosen_role";
    private static final String AI_RECOMMENDED_JOB1 = "aijobrecommended1";
    private static final String AI_RECOMMENDED_JOB2 = "aijobrecommended2";
    private static final String AI_RECOMMENDED_JOB3 = "aijobrecommended3";
    private static final String AI_RECOMMENDED_JOB4 = "aijobrecommended4";
    private static final String AI_RECOMMENDED_JOB5 = "aijobrecommended5";
    private static final String AI_RECOMMENDED_JOB6 = "aijobrecommended6";
    private static final String USER_NAME = "username";
    private static final String PREFS_NAME1 = "AppPinningPrefs";
    private static final String KEY_PINNING_ENABLED = "PinningEnabled";
    private SharedPreferences sharedPreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences(PREFS_NAME1, Context.MODE_PRIVATE);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // If coming from PaymentPage, clear or update last activity
        if (getIntent().getBooleanExtra("fromPaymentPage", false)) {
            Log.d(TAG, "Navigating from PaymentPage");
            sharedPreferences.edit().putString(LAST_ACTIVITY, MainActivity.class.getName()).apply();
        } else {
            String lastActivity = sharedPreferences.getString(LAST_ACTIVITY, SplashScreen.class.getName());
            Log.d(TAG, "Last activity: " + lastActivity);
            if (!lastActivity.equals(MainActivity.class.getName())) {
                try {
                    Class<?> activityClass = Class.forName(lastActivity);
                    Log.d(TAG, "Navigating to last activity: " + lastActivity);
                    startActivity(new Intent(this, activityClass));
                    finish();
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "ClassNotFoundException: " + e.getMessage());
                }
            }
        }

        setContentView(R.layout.activity_main);
        initUI();

        fetchAndDisplayUserName();

        checkAndShowRecommendedJobsDialog();

        storeUserVisit();
    }

    private void initUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        dropdownMenu = findViewById(R.id.menu_icon);
        goalText = findViewById(R.id.user_goal);
        userNameTextView = findViewById(R.id.user_greeting); // Assuming you have a TextView with this ID

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            handleMenuClick(menuItem);
            drawerLayout.closeDrawers();  // Close drawer after selection
            return true;
        });

        dropdownMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        findViewById(R.id.gradient_button1).setOnClickListener(v -> navigateToActivity(CommunitiesActivity.class));
        findViewById(R.id.gradient_button2).setOnClickListener(v -> navigateToActivity(GoalsActivity.class));
        findViewById(R.id.gradient_button3).setOnClickListener(v -> navigateToActivity(AchievementActivity.class));
        findViewById(R.id.gradient_button4).setOnClickListener(v -> navigateToActivity(SubscriptionsActivity.class));
        findViewById(R.id.gradient_button5).setOnClickListener(v -> navigateToActivity(CoursesActivity.class));
        findViewById(R.id.gradient_button6).setOnClickListener(v -> navigateToActivity(jobportal.class));
    }

    private void fetchAndDisplayUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                Log.d(TAG, "User email: " + userEmail);
                String sanitizedEmail = userEmail.replace(".", ",");
                Log.d(TAG, "Sanitized email: " + sanitizedEmail);

                databaseReference.child(sanitizedEmail).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Fetched name: " + name);
                        if (name != null) {
                            userNameTextView.setText("Hi " + name + "!");
                        } else {
                            userNameTextView.setText("Hi User!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error: " + databaseError.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    private void signOut() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, EntryPage.class);
        startActivity(intent);
        finish();
    }

    private void storeUserVisit() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");
                databaseReference.child(sanitizedEmail).child("lastActivity").setValue("MainActivity")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User visit stored successfully");
                            } else {
                                Log.e(TAG, "Error storing user visit", task.getException());
                            }
                        });
            } else {
                Log.e(TAG, "User email is null, cannot store visit.");
            }
        } else {
            Log.e(TAG, "Current user is null, cannot store visit.");
        }
    }

    private void checkAndShowRecommendedJobsDialog() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");
                databaseReference.child(sanitizedEmail).child(CHOSEN_ROLE).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String chosenRole = dataSnapshot.getValue(String.class);
                        if (chosenRole == null || chosenRole.isEmpty()) {
                            fetchAndShowRecommendedJobs(sanitizedEmail);
                        } else {
                            goalText.setText("Preferred Role: " + chosenRole);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error: " + databaseError.getMessage());
                    }
                });
            }
        }
    }

    private void fetchAndShowRecommendedJobs(String sanitizedEmail) {
        databaseReference.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String job1 = dataSnapshot.child(AI_RECOMMENDED_JOB1).getValue(String.class);
                String job2 = dataSnapshot.child(AI_RECOMMENDED_JOB2).getValue(String.class);
                String job3 = dataSnapshot.child(AI_RECOMMENDED_JOB3).getValue(String.class);
                String job4 = dataSnapshot.child(AI_RECOMMENDED_JOB4).getValue(String.class);
                String job5 = dataSnapshot.child(AI_RECOMMENDED_JOB5).getValue(String.class);
                String job6 = dataSnapshot.child(AI_RECOMMENDED_JOB6).getValue(String.class);


                List<String> jobs = new ArrayList<>();
                if (job1 != null) jobs.add(job1);
                if (job2 != null) jobs.add(job2);
                if (job3 != null) jobs.add(job3);
                if (job4 != null) jobs.add(job4);
                if (job5 != null) jobs.add(job5);
                if (job6 != null) jobs.add(job6);

                if (!jobs.isEmpty()) {
                    showJobSelectionDialog(jobs, sanitizedEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void showJobSelectionDialog(List<String> jobs, String sanitizedEmail) {
        CharSequence[] jobArray = jobs.toArray(new CharSequence[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Preferred Role");
        builder.setItems(jobArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedJob = jobArray[which].toString();
                Toast.makeText(MainActivity.this, "You have chosen: " + selectedJob, Toast.LENGTH_LONG).show();
                saveChosenRole(sanitizedEmail, selectedJob);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void saveChosenRole(String sanitizedEmail, String chosenRole) {
        databaseReference.child(sanitizedEmail).child(CHOSEN_ROLE).setValue(chosenRole)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Chosen role saved successfully: " + chosenRole);
                        goalText.setText("Preferred Role: " + chosenRole);
                        Toast.makeText(MainActivity.this, "Chosen role saved successfully and it is unchangeable.", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Error saving chosen role", task.getException());
                    }
                });
    }

    private void navigateToActivity(Class<?> activityClass) {
        startActivity(new Intent(MainActivity.this, activityClass));
    }

    private void handleMenuClick(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        Log.d(TAG, "Menu item clicked: " + itemId);

        if (itemId == R.id.nav_home) {
            Log.d(TAG, "Navigating to Home");
            navigateToActivity(MainActivity.class);
        } else if (itemId == R.id.nav_profile) {
            Log.d(TAG, "Navigating to Profile");
            navigateToActivity(ProfileActivity.class);
        } else if (itemId == R.id.antidistraction) {
            Log.d(TAG, "Opening Anti Distraction Mode dialog");
            showAntiDistractionDialog();
        } else if (itemId == R.id.nav_help) {
            Log.d(TAG, "Opening Help dialog");
            showHelpDialog();
        } else if (itemId == R.id.shareus) {
            Log.d(TAG, "Sharing the app");
            shareApp();
        } else if (itemId == R.id.nav_settings){
            Log.d(TAG, "Navigating to Settings");
            navigateToActivity(SettingsActivity.class);
        } else if (itemId == R.id.nav_sign_out) {
            Log.d(TAG, "Showing sign out confirmation dialog");
            showSignOutConfirmationDialog();
        } else {
            Log.w(TAG, "Unknown menu item clicked");
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Help Options")
                .setItems(new String[]{"FAQs", "Write to Us"}, (dialog, which) -> {
                    if (which == 0) {
                        navigateToActivity(FAQActivity.class);
                    } else if (which == 1) {
                        composeEmail();
                    }
                })
                .show();
    }



    private void showAntiDistractionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Anti Distraction Mode");

        // Add a switch to the dialog
        Switch antiDistractionSwitch = new Switch(this);
        builder.setView(antiDistractionSwitch);

        boolean isPinningEnabled = sharedPreferences1.getBoolean(KEY_PINNING_ENABLED, false);
        antiDistractionSwitch.setChecked(isPinningEnabled);

        antiDistractionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences1.edit().putBoolean(KEY_PINNING_ENABLED, isChecked).apply();

                if (isChecked) {
                    activateAntiDistractionMode();
                }
                else {
                    deactivateAntiDistractionMode();
                }
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void activateAntiDistractionMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "Anti Distraction Mode Activated", Toast.LENGTH_SHORT).show();
            startLockTask();
        }
    }

    private void deactivateAntiDistractionMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "Anti Distraction Mode Deactivated", Toast.LENGTH_SHORT).show();
            stopLockTask();
        }
    }

    private void composeEmail() {
        Log.d(TAG, "composeEmail called");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // Use email mime type
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"algowisetechnologies@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Careerguru");

        try {
            startActivity(Intent.createChooser(intent, "Choose an email client"));
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, "No email client installed.");
            Toast.makeText(this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }



    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this app at: [app link]");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void showSignOutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> signOut())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Overlay permission granted. Anti Distraction Mode can now be activated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Overlay permission not granted. Anti Distraction Mode cannot be activated.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
