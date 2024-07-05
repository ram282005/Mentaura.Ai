package com.example.careercrew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class CourseRecommendationActivity extends AppCompatActivity {

    private Button buttonPayUs;
    private TextView textViewCourses;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_recommendation);

        buttonPayUs = findViewById(R.id.button_pay_us);
        textViewCourses = findViewById(R.id.text_view_courses);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_activity", "CourseRecommendationActivity");
        editor.apply();

        saveCurrentActivity();
        fetchUserDetails();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }




    private void fetchUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                mDatabase.child("users").child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String dreamRole = dataSnapshot.child("dreamRole").getValue(String.class);
                            String dreamCompany = dataSnapshot.child("dreamCompany").getValue(String.class);

                            if (dreamRole != null && dreamCompany != null) {
                                displayCourseRecommendations(dreamRole, dreamCompany);
                            } else {
                                Toast.makeText(CourseRecommendationActivity.this, "Dream role or company not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CourseRecommendationActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CourseRecommendationActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error retrieving user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCourseRecommendations(String dreamRole, String dreamCompany) {
        String coursesText = "Courses according to your knowledge to crack your dream role in your dream company: "
                + dreamRole + " at " + dreamCompany + "\n\n"
                ;
        textViewCourses.setText(coursesText);
    }

    private void saveCurrentActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");
                mDatabase.child("users").child(emailKey).child("lastActivity").setValue("CourseRecommendationActivity");
            }

            buttonPayUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CourseRecommendationActivity.this, SubscriptionActivity.class);
                    startActivity(intent);
                }
            });
        }
        }

    }



