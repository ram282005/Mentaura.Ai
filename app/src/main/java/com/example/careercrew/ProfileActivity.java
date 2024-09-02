package com.example.careercrew;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, ageTextView, genderTextView, emailTextView;
    private EditText phoneEditText, bioEditText, educationEditText, dobEditText;
    private ImageView editImageView;
    private Button submitButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, newdata;
    private String userEmail, name2, age2, gender2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        nameTextView = findViewById(R.id.name);
        ageTextView = findViewById(R.id.age);
        genderTextView = findViewById(R.id.gender);
        emailTextView = findViewById(R.id.email);

        phoneEditText = findViewById(R.id.phone);
        bioEditText = findViewById(R.id.about);
        educationEditText = findViewById(R.id.education);
        dobEditText = findViewById(R.id.DOB);

        editImageView = findViewById(R.id.edit);
        submitButton = findViewById(R.id.submitBtn);

        // Disable editing initially
        disableEditing();

        FirebaseApp.initializeApp(this);

        // Set onClickListener for edit button
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(sanitizedEmail);
                newdata = FirebaseDatabase.getInstance().getReference("Profile").child(sanitizedEmail);

                fetchUserData();

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitUserData(userEmail);
                    }
                });
            }
        } else {
            Toast.makeText(this, "User not found :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name2 = dataSnapshot.child("name").getValue(String.class);
                    age2 = dataSnapshot.child("age").getValue(String.class);
                    gender2 = dataSnapshot.child("gender").getValue(String.class);

                    nameTextView.setText(name2);
                    ageTextView.setText(age2);
                    genderTextView.setText(gender2);
                    emailTextView.setText(userEmail);
                } else {
                    Toast.makeText(ProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing() {
        phoneEditText.setVisibility(View.VISIBLE);
        bioEditText.setVisibility(View.VISIBLE);
        educationEditText.setVisibility(View.VISIBLE);
        dobEditText.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        phoneEditText.setEnabled(true);
        bioEditText.setEnabled(true);
        educationEditText.setEnabled(true);
        dobEditText.setEnabled(true);
    }

    private void disableEditing() {
        phoneEditText.setVisibility(View.GONE);
        bioEditText.setVisibility(View.GONE);
        educationEditText.setVisibility(View.GONE);
        dobEditText.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private void submitUserData(String userEmail) {
        String phone = phoneEditText.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();
        String education = educationEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        if (!phone.isEmpty() && !bio.isEmpty() && !education.isEmpty() && !dob.isEmpty()) {

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("name", name2);
            additionalData.put("age", age2);
            additionalData.put("gender", gender2);
            additionalData.put("email", userEmail);
            additionalData.put("phone", phone);
            additionalData.put("bio", bio);
            additionalData.put("education", education);
            additionalData.put("dob", dob);

            newdata.updateChildren(additionalData);

            Toast.makeText(ProfileActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();

            // Disable editing again
            disableEditing();
        } else {
            Toast.makeText(ProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }
}