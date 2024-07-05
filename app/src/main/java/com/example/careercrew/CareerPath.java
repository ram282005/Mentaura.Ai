package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CareerPath extends AppCompatActivity {

    private EditText editTextCareerGoal;
    private Button buttonSubmit;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career_path);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        editTextCareerGoal = findViewById(R.id.message_edit_text);
        buttonSubmit = findViewById(R.id.send_btn);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCareerGoalData();
            }
        });
    }

    private void submitCareerGoalData() {
        String careerGoal = editTextCareerGoal.getText().toString().trim();

        if (careerGoal.isEmpty()) {
            Toast.makeText(CareerPath.this, "Please fill in the career goal", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                // Update user data with Career Goal
                dbRef.child("users").child(emailKey).child("careerGoal").setValue(careerGoal);
                dbRef.child("users").child(emailKey).child("completed").setValue(true)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CareerPath.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CareerPath.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CareerPath.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(CareerPath.this, "Error retrieving user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CareerPath.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
