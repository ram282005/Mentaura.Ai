package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText;
    Button signUpButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nameEditText = findViewById(R.id.editTextText);
        emailEditText = findViewById(R.id.editTextText1);
        passwordEditText = findViewById(R.id.editTextText2);
        signUpButton = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

        handler = new Handler();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String pwd = passwordEditText.getText().toString().trim();
                final String name = nameEditText.getText().toString().trim();

                if (!email.isEmpty() && !pwd.isEmpty() && !name.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    registerUser(email, pwd, name);
                } else {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar

                        if (task.isSuccessful()) {
                            // Sign in success, send verification email
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUp.this, "Registration successful! Please check your email for verification.", Toast.LENGTH_SHORT).show();

                                                    // Save user details to Firebase Realtime Database
                                                    String emailKey = email.replace(".", ",");
                                                    mDatabase.child("users").child(emailKey).child("name").setValue(name);
                                                    mDatabase.child("users").child(emailKey).child("password").setValue(password);

                                                    // Open Gmail app
                                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    try {
                                                        startActivity(intent);
                                                    } catch (android.content.ActivityNotFoundException ex) {
                                                        Toast.makeText(SignUp.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                                                    }

                                                    // Start a runnable to check email verification status
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            checkEmailVerification(user);
                                                        }
                                                    }, 2000);
                                                } else {
                                                    Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkEmailVerification(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar while checking verification
        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE); // Hide the progress bar

                if (user.isEmailVerified()) {
                    // Email is verified, navigate to Register activity
                    Intent intent = new Intent(SignUp.this, HomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Email not verified, check again after some time
                    Toast.makeText(SignUp.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkEmailVerification(user);
                        }
                    }, 2000);
                }
            }
        });
    }
}
