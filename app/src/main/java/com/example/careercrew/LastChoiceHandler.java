package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
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

public class LastChoiceHandler extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchLastChoice(currentUser.getEmail().replace(".", ","));
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LastChoiceHandler.this, EntryPage.class));
            finish();
        }
    }

    private void fetchLastChoice(String emailKey) {
        DatabaseReference userRef = databaseReference.child("users").child(emailKey).child("lastChoice");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastChoice = dataSnapshot.getValue(String.class);
                    redirectBasedOnLastChoice(lastChoice);
                } else {
                    Toast.makeText(LastChoiceHandler.this, "No last choice found", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LastChoiceHandler.this, HomePage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LastChoiceHandler.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LastChoiceHandler.this, Register.class));
                finish();
            }
        });
    }

    private void redirectBasedOnLastChoice(String lastChoice) {
        Intent newIntent;
        switch (lastChoice) {
            case "option1":
                newIntent = new Intent(this, CareerPath.class);
                break;
            case "option2":
                newIntent = new Intent(this, DreamRole.class);
                break;

        }

    }
}
