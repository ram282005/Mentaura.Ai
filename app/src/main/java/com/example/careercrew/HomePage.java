package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private String chosenOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        btnCareerPath = findViewById(R.id.buttonDiscoverCareer);
        btnDreamRole = findViewById(R.id.buttonDreamRole);
        buttonEnterDetails = findViewById(R.id.buttonenterdetails);

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
                                navigateToRegister(emailKey);
                            }
                        } else {
                            // User data is not present, navigate to Register activity
                            navigateToRegister(emailKey);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(HomePage.this, "Failed to read user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error retrieving user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLastChoiceAndNavigate(String emailKey, String choice, Class<?> destinationClass) {
        dbRef.child("users").child(emailKey).child("lastChoice").setValue(choice).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(HomePage.this, destinationClass);
                startActivity(intent);
            } else {
                Toast.makeText(HomePage.this, "Failed to update choice: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRegister(String emailKey) {
        dbRef.child("users").child(emailKey).child("lastChoice").setValue(chosenOption).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(HomePage.this, Register.class);
                intent.putExtra("nextActivityClass", chosenOption.equals("CareerPath") ? "com.example.careercrew.CareerPath" : "com.example.careercrew.DreamRole");
                startActivity(intent);
            } else {
                Toast.makeText(HomePage.this, "Failed to save choice: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRegister() {
        Intent intent = new Intent(HomePage.this, Register.class);
        startActivity(intent);
    }
}
