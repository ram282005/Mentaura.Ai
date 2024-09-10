package com.example.careercrew;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("settings_preferences", MODE_PRIVATE);
        boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the toolbar with a back arrow
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;
        private SharedPreferences sharedPreferences;

        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            sharedPreferences = getActivity().getSharedPreferences("settings_preferences", MODE_PRIVATE);

            // Set click listeners for preferences
            Preference changePasswordPreference = findPreference("change_password");
            if (changePasswordPreference != null) {
                changePasswordPreference.setOnPreferenceClickListener(preference -> {
                    // Handle change password logic here
                    return true;
                });
            }

            Preference deleteAccountPreference = findPreference("delete_account");
            if (deleteAccountPreference != null) {
                deleteAccountPreference.setOnPreferenceClickListener(preference -> {
                    showDeleteAccountDialog();
                    return true;
                });
            }

            Preference verifyPhoneNumberPreference = findPreference("verify_phone_number");
            if (verifyPhoneNumberPreference != null) {
                verifyPhoneNumberPreference.setOnPreferenceClickListener(preference -> {
                    verifyPhoneNumber();
                    return true;
                });
            }

            Preference locationPreference = findPreference("request_location");
            if (locationPreference != null) {
                locationPreference.setOnPreferenceClickListener(preference -> {
                    requestLocation();
                    return true;
                });
            }

            Preference darkModePreference = findPreference("dark_mode");
            if (darkModePreference != null) {
                darkModePreference.setOnPreferenceClickListener(preference -> {
                    boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
                    toggleDarkMode(darkModeEnabled);
                    return true;
                });
            }

            Preference aboutPreference = findPreference("about");
            if (aboutPreference != null) {
                aboutPreference.setOnPreferenceClickListener(preference -> {
                    showAboutDialog();
                    return true;
                });
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("dark_mode")) {
                boolean darkModeEnabled = sharedPreferences.getBoolean(key, false);
                toggleDarkMode(darkModeEnabled);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private void toggleDarkMode(boolean darkModeEnabled) {
            sharedPreferences.edit().putBoolean("dark_mode", darkModeEnabled).apply();
            if (darkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        private void showDeleteAccountDialog() {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUserAccount();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void deleteUserAccount() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getContext(), EntryPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Account deletion failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        private void verifyPhoneNumber() {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String email = user.getEmail();
                if (email != null) {
                    String encodedEmail = email.replace(".", ",");

                    mDatabase.child("profiles").child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String phoneNumber = snapshot.child("phone_number").getValue(String.class);
                                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                                    sendOtpToPhoneNumber(phoneNumber);
                                } else {
                                    promptUserToCompleteProfile();
                                }
                            } else {
                                promptUserToCompleteProfile();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(getContext(), "Error fetching data. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No email associated with this account.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        }

        private void sendOtpToPhoneNumber(String phoneNumber) {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(getActivity())
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            // Auto-verification succeeded
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Toast.makeText(getContext(), "Verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                            Intent intent = new Intent(getContext(), VerifyOtpActivity.class);
                            intent.putExtra("verificationId", verificationId);
                            startActivity(intent);
                        }
                    })
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }

        private void requestLocation() {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Handle location logic
                Toast.makeText(getContext(), "Location access granted", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(getContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void promptUserToCompleteProfile() {
            Toast.makeText(getContext(), "Please update your profile with a valid phone number.", Toast.LENGTH_LONG).show();
        }

        private void showAboutDialog() {
            new AlertDialog.Builder(getContext())
                    .setTitle("About CareerCrew")
                    .setMessage("Mentaura AI is your companion for finding the right career path, preparing for job roles, and staying focused!")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
