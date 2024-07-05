package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText editTextAge, editTextGender, editTextCareerFocus;
    private Button buttonRegister;
    private ImageView imageViewBack;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        editTextAge = findViewById(R.id.editTextText);
        editTextGender = findViewById(R.id.editTextText1);
        editTextCareerFocus = findViewById(R.id.editTextText2);
        buttonRegister = findViewById(R.id.button);
        imageViewBack = findViewById(R.id.imageView);  // Find the ImageView

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {  // Set OnClickListener for ImageView
            @Override
            public void onClick(View v) {
                navigateToHomePage();
            }
        });
    }

    private void registerUser() {
        final String age = editTextAge.getText().toString().trim();
        final String gender = editTextGender.getText().toString().trim();
        final String careerFocus = editTextCareerFocus.getText().toString().trim();

        if (age.isEmpty() || gender.isEmpty() || careerFocus.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                // Create a new user object
                User user = new User(age, gender, careerFocus);

                // Save user data to Firebase
                dbRef.child("users").child(emailKey).setValue(user)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Registration successful, navigate to DreamRole activity
                                Intent intent = new Intent(Register.this, DreamRole.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Register.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(Register.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
