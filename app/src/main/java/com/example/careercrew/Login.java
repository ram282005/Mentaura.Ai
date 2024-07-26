package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewForgotPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.editTextText);
        editTextPassword = findViewById(R.id.editTextText2);
        buttonLogin = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    loginUser(email, password);
                } else {
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    resetPassword(email);
                } else {
                    Toast.makeText(Login.this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar

                        if (task.isSuccessful()) {
                            // Sign in success, check user's data
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d("Login", "User logged in: " + user.getEmail());
                                checkUserDataCompletion(user.getEmail().replace(".", ","));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("Login", "Login failed: " + task.getException().getMessage());
                            Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserDataCompletion(String emailKey) {
        DatabaseReference userRef = databaseReference.child("users").child(emailKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean ageExists = dataSnapshot.hasChild("age");
                    boolean genderExists = dataSnapshot.hasChild("gender");
                    boolean careerFocusExists = dataSnapshot.hasChild("careerFocus");
                    boolean dreamRoleExists = dataSnapshot.hasChild("dreamRole");
                    boolean dreamCompanyExists = dataSnapshot.hasChild("dreamCompany");

                    if (ageExists && genderExists && careerFocusExists) {
                        if (dreamRoleExists && dreamCompanyExists) {
                            boolean isPremium = dataSnapshot.child("isPremium").getValue(Boolean.class);
                            if (isPremium) {
                                // User is premium, navigate to MainActivity
                                Log.d("Login", "Premium user, navigating to MainActivity");
                                navigateToActivity(MainActivity.class);
                            } else {
                                // User is free, navigate to HomePage
                                Log.d("Login", "Free user, navigating to HomePage");
                                navigateToActivity(HomePage.class);
                            }
                        } else {
                            checkLastActivity(emailKey);
                        }
                    } else {
                        // Registration is incomplete, check for last activity
                        Log.d("Login", "Registration is incomplete, checking last activity");
                        checkLastActivity(emailKey);
                    }
                } else {
                    // No user data found, go to Register page
                    Log.d("Login", "No user data found, navigating to Register");
                    navigateToActivity(Register.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Login", "Database error: " + databaseError.getMessage());
                // Error fetching data, go to EntryPage
                navigateToActivity(EntryPage.class);
            }
        });
    }

    private void checkLastActivity(String emailKey) {
        DatabaseReference lastActivityRef = databaseReference.child("users").child(emailKey).child("lastActivity");
        lastActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastActivity = dataSnapshot.getValue(String.class);
                    if (lastActivity != null && !lastActivity.isEmpty()) {
                        try {
                            Class<?> activityClass = Class.forName("com.example.careercrew." + lastActivity);
                            Log.d("Login", "Navigating to last activity: " + lastActivity);
                            Intent intent = new Intent(Login.this, activityClass);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
                            startActivity(intent);
                            finish();
                        } catch (ClassNotFoundException e) {
                            Log.e("Login", "ClassNotFoundException: " + e.getMessage());
                            navigateToActivity(HomePage.class);
                        }
                    } else {
                        Log.d("Login", "No last activity found, navigating to HomePage");
                        navigateToActivity(HomePage.class);
                    }
                } else {
                    Log.d("Login", "No last activity data found, navigating to HomePage");
                    navigateToActivity(HomePage.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Login", "Database error: " + databaseError.getMessage());
                navigateToActivity(HomePage.class);
            }
        });
    }


    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Log.d("Login", "Navigating to activity: " + targetActivity.getSimpleName());
        Intent intent = new Intent(Login.this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish();
    }
}