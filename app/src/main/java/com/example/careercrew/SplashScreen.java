package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds delay
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d("SplashScreen", "User is logged in: " + currentUser.getEmail());
                    checkUserDataCompletion(currentUser.getEmail().replace(".", ","));
                } else {
                    Log.d("SplashScreen", "No user logged in, navigating to EntryPage");
                    startActivity(new Intent(SplashScreen.this, EntryPage.class));
                    finish();
                }
            }
        }, SPLASH_DELAY);
    }

    private void checkUserDataCompletion(String emailKey) {
        DatabaseReference userRef = databaseReference.child("users").child(emailKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean dreamRoleExists = dataSnapshot.hasChild("dreamRole");
                    boolean dreamCompanyExists = dataSnapshot.hasChild("dreamCompany");

                    if (dreamRoleExists && dreamCompanyExists) {
                        Log.d("SplashScreen", "All details including dreamRole and dreamCompany are present");
                        startActivity(new Intent(SplashScreen.this, CourseRecommendationActivity.class));
                    } else {
                        if (dataSnapshot.hasChild("lastChoice")) {
                            String lastChoice = dataSnapshot.child("lastChoice").getValue(String.class);
                            Log.d("SplashScreen", "Navigating to LastChoiceHandler with lastChoice: " + lastChoice);
                            Intent intent = new Intent(SplashScreen.this, com.example.careercrew.LastChoiceHandler.class);
                            intent.putExtra("lastChoice", lastChoice);
                            startActivity(intent);
                        } else {
                            Log.d("SplashScreen", "Registration complete but dream role/company is not provided, navigating to DreamRole");
                            startActivity(new Intent(SplashScreen.this, DreamRole.class));
                        }
                    }
                } else {
                    Log.d("SplashScreen", "No user data found, navigating to Register page");
                    startActivity(new Intent(SplashScreen.this, Register.class));
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SplashScreen", "Database error: " + databaseError.getMessage());
                startActivity(new Intent(SplashScreen.this, EntryPage.class));
                finish();
            }
        });
    }
}
