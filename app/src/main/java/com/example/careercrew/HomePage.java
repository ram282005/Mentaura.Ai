package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Button btnCareerPath, btnDreamRole, buttonEnterDetails;
    private ProgressBar progressBar;
    private String chosenOption;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        btnCareerPath = findViewById(R.id.home_career);
        btnDreamRole = findViewById(R.id.home_dreamrole);
        buttonEnterDetails = findViewById(R.id.home_register);
        progressBar = findViewById(R.id.home_progressBar);

        btnCareerPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenOption = "CareerPath";
                handleUserChoice();
            }
        });

        btnDreamRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenOption = "DreamRole";
                handleUserChoice();
            }
        });

        buttonEnterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
    }

    private void handleUserChoice() {
        progressBar.setVisibility(View.VISIBLE);  // Show progress bar
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");
                dbRef.child("users").child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null && user.getAge() != null && user.getGender() != null && user.getCareerFocus() != null) {
                                // User data is complete, update the choice and navigate to the selected activity
                                updateLastChoiceAndNavigate(emailKey, chosenOption, chosenOption.equals("CareerPath") ? CareerPath.class : DreamRole.class);
                            } else {
                                // User data is not complete, navigate to Register activity
                                navigateToRegister(emailKey, chosenOption);
                            }
                        } else {
                            // User data is not present, navigate to Register activity
                            navigateToRegister(emailKey, chosenOption);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);  // Hide progress bar
                        Toast.makeText(HomePage.this, "Failed to read user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);  // Hide progress bar
                Toast.makeText(this, "Error retrieving user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressBar.setVisibility(View.GONE);  // Hide progress bar
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLastChoiceAndNavigate(String emailKey, String choice, Class<?> destinationClass) {
        dbRef.child("users").child(emailKey).child("lastChoice").setValue(choice).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);  // Hide progress bar
            if (task.isSuccessful()) {
                Intent intent = new Intent(HomePage.this, destinationClass);
                startActivity(intent);
            } else {
                Toast.makeText(HomePage.this, "Please retry " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRegister(String emailKey, String choice) {
        dbRef.child("users").child(emailKey).child("lastChoice").setValue(choice).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);  // Hide progress bar
            if (task.isSuccessful()) {
                Intent intent = new Intent(HomePage.this, Register.class);
                intent.putExtra("nextActivityClass", choice.equals("CareerPath") ? "com.example.careercrew.CareerPath" : "com.example.careercrew.DreamRole");
                startActivity(intent);
            } else {
                Toast.makeText(HomePage.this, "Please retry " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRegister() {
        progressBar.setVisibility(View.VISIBLE);  // Show progress bar
        Intent intent = new Intent(HomePage.this, Register.class);
        startActivity(intent);
        progressBar.setVisibility(View.GONE);  // Hide progress bar
    }
}