package com.example.careercrew;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievementActivity extends AppCompatActivity {

    private List<Achievement> achievementList = new ArrayList<>();
    private AchievementAdapter achievementAdapter;
    private DatabaseReference databaseReference, database;
    private FirebaseAuth firebaseAuth;
    private CircleImageView dp;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        TextView userNameTextView = findViewById(R.id.userName);
        TextView communityListTextView = findViewById(R.id.communityList);
        TextView dailyAssignmentTextView = findViewById(R.id.dailyAssignmentsTextView);  // Updated ID
        ListView achievementListView = findViewById(R.id.achievementListView);
        dp = findViewById(R.id.profileImage);

        // Initialize Firebase reference and authentication
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        userEmail = firebaseAuth.getCurrentUser().getEmail();

        if (userEmail != null) {
            String sanitizedEmail = userEmail.replace(".", ",");
            database = FirebaseDatabase.getInstance().getReference("Profile").child(sanitizedEmail);

            fetchUserData1();
        } else {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
        }

        // Set up ListView and Adapter
        achievementAdapter = new AchievementAdapter(this, achievementList);
        achievementListView.setAdapter(achievementAdapter);

        // Fetch user data
        fetchUserData(userNameTextView, communityListTextView);

        // Fetch and display Daily Assignment Count
        fetchDailyAssignmentCount(dailyAssignmentTextView);

        // Add two assignments with a score of 100 each
        Achievement achievement1 = new Achievement("#1 Assignment", 100);
        Achievement achievement2 = new Achievement("#2 Assignment", 100);
        achievementList.add(achievement1);
        achievementList.add(achievement2);
        achievementAdapter.notifyDataSetChanged();
    }

    private void fetchUserData(TextView userNameTextView, TextView communityListTextView) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                String userEmailFormatted = userEmail.replace(".", ",");

                // Fetch user details from Firebase
                databaseReference.child("users").child(userEmailFormatted).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String joinedCommunity = dataSnapshot.child("joined_community").getValue(String.class);

                        userNameTextView.setText(userName);
                        communityListTextView.setText(joinedCommunity);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
            }
        }
    }

    private void fetchDailyAssignmentCount(TextView dailyAssignmentTextView) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                String userEmailFormatted = userEmail.replace(".", ",");

                // Fetch Daily Assignment Count from Firebase
                databaseReference.child("users").child(userEmailFormatted).child("DailyAssignments").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long dailyAssignmentCount = dataSnapshot.getValue(Long.class);
                        if (dailyAssignmentCount != null) {
                            dailyAssignmentTextView.setText("Daily Assignments: " + dailyAssignmentCount);
                        } else {
                            dailyAssignmentTextView.setText("Daily Assignments: 0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
            }
        }
    }

    private void fetchUserData1() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.child("image").getValue(String.class);

                    if (profileImageUrl != null) {
                        Picasso.get()
                                .load(profileImageUrl)
                                .placeholder(R.drawable.baseline_account_circle_24) // Optional placeholder image
                                .error(R.drawable.baseline_account_circle_24) // Optional error image
                                .into(dp);
                    } else {
                        Toast.makeText(AchievementActivity.this, "Profile image not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AchievementActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AchievementActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}