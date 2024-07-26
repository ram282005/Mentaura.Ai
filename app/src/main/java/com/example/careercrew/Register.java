package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    private EditText editTextAge, editTextCareerFocus;
    private Spinner spinnerGender;
    private Button buttonRegister;
    private ImageView imageViewBack;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String nextActivityClass;  // To hold the next activity class
    private String selectedGender;  // To hold the selected gender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        editTextAge = findViewById(R.id.editTextText);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextCareerFocus = findViewById(R.id.editTextText2);
        buttonRegister = findViewById(R.id.button);
        imageViewBack = findViewById(R.id.imageView);  // Find the ImageView

        // Get the next activity class from the intent extras
        nextActivityClass = getIntent().getStringExtra("nextActivityClass");

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = null;
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserDetails();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {  // Set OnClickListener for ImageView
            @Override
            public void onClick(View v) {
                navigateToHomePage();
            }
        });
    }

    private void registerUserDetails() {
        final String age = editTextAge.getText().toString().trim();
        final String careerFocus = editTextCareerFocus.getText().toString().trim();

        if (age.isEmpty() || selectedGender == null || selectedGender.isEmpty() || careerFocus.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                // Save user details to Firebase
                dbRef.child("users").child(emailKey).child("age").setValue(age);
                dbRef.child("users").child(emailKey).child("gender").setValue(selectedGender);
                dbRef.child("users").child(emailKey).child("careerFocus").setValue(careerFocus);

                // Registration successful, check for last choice
                checkLastChoice(emailKey);
            }
        }
    }

    private void checkLastChoice(String emailKey) {
        DatabaseReference lastChoiceRef = dbRef.child("users").child(emailKey).child("lastChoice");
        lastChoiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastChoice = dataSnapshot.getValue(String.class);
                    Log.d("Register", "Navigating to LastChoiceHandler with lastChoice: " + lastChoice);
                    navigateToActivity(LastChoiceHandler.class, lastChoice);
                } else {
                    Log.d("Register", "No last choice found, navigating to HomePage");
                    navigateToActivity(HomePage.class, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Register", "Database error: " + databaseError.getMessage());
                navigateToActivity(HomePage.class, null);
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity, String lastChoice) {
        Log.d("Register", "Navigating to activity: " + targetActivity.getSimpleName());
        Intent intent = new Intent(Register.this, targetActivity);
        if (lastChoice != null) {
            intent.putExtra("lastChoice", lastChoice);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish();
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(Register.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
