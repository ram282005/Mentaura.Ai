package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignUp extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText;
    Button signUpButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

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
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            // Get the email of the registered user
                            String emailKey = email.replace(".", ","); // Replace "." to avoid conflicts in Firebase keys

                            // Create a new User object
                            User user = new User(name, email, password);

                            // Save user details to Firebase Realtime Database
                            mDatabase.child("users").child(emailKey).setValue(user);

                            // Move to the home page or any other activity
                            Intent intent = new Intent(SignUp.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
