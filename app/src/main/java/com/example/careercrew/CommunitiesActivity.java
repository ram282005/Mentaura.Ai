package com.example.careercrew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunitiesActivity extends AppCompatActivity {

    private ListView individualRanksListView;
    private ListView communityRanksListView;
    private FirebaseAuth mAuth;
    private static final String TAG = "CommunitiesActivity";
    private String userCommunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize TabHost
        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        Button doubtsButton = findViewById(R.id.button_doubts);
        Button weeklyAssignmentButton = findViewById(R.id.button_weekly_assignment);

        TabHost.TabSpec spec = tabHost.newTabSpec("CommunityRanks");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Community Ranks");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("IndividualRanks");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Individual Ranks");
        tabHost.addTab(spec);

        // Initialize ListViews
        individualRanksListView = findViewById(R.id.individual_ranks_list);
        communityRanksListView = findViewById(R.id.community_ranks_list);

        // Fetch and display community data
        checkAndJoinCommunity();

        // Fetch and display individual ranks
        loadIndividualRanks();

        // Set click listeners
        doubtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to DoubtsChat page
                Intent intent = new Intent(CommunitiesActivity.this, DoubtsChatActivity.class);
                startActivity(intent);
            }
        });

        weeklyAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WeeklyAssignment page
                Intent intent = new Intent(CommunitiesActivity.this, WeeklyAssignmentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkAndJoinCommunity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                // Show progress dialog
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait while we join you to the community...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Ensure the user is part of the "Jobs" community
                addUserToJobsCommunity(sanitizedEmail, progressDialog, databaseReference);

                // Fetch the user's chosen role and check if already joined
                databaseReference.child("users").child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String chosenRole = dataSnapshot.child("chosen_role").getValue(String.class);
                        String dreamRole = dataSnapshot.child("dream_role").getValue(String.class);
                        String joinedCommunity = dataSnapshot.child("joined_community").getValue(String.class);

                        if (joinedCommunity != null) {
                            userCommunity = joinedCommunity;
                            Log.d(TAG, "Already joined community: " + joinedCommunity);
                            loadAllCommunities(joinedCommunity, progressDialog, databaseReference);
                        } else if (chosenRole != null) {
                            Log.d(TAG, "Chosen role: " + chosenRole);
                            addUserToCommunity(chosenRole, sanitizedEmail, progressDialog, databaseReference);
                        } else if (dreamRole != null) {
                            Log.d(TAG, "No chosen role found, using dream role: " + dreamRole);
                            addUserToCommunity(dreamRole, sanitizedEmail, progressDialog, databaseReference);
                        } else {
                            Log.d(TAG, "No chosen or dream role found for the user.");
                            Toast.makeText(CommunitiesActivity.this, "No chosen or dream role found for the user.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                        Log.e(TAG, "Failed to fetch user data: " + databaseError.getMessage());
                        Toast.makeText(CommunitiesActivity.this, "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        } else {
            // Handle the case where there is no logged-in user
            Log.d(TAG, "No user is logged in.");
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserToJobsCommunity(String sanitizedEmail, ProgressDialog progressDialog, DatabaseReference databaseReference) {
        String jobsCommunity = "Jobs Community";

        databaseReference.child("communities").child(jobsCommunity).child("members").child(sanitizedEmail)
                .setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseReference.child("users").child(sanitizedEmail).child("joined_jobs_community").setValue(jobsCommunity)
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful()) {
                                        Log.d(TAG, "User added to Jobs community.");
                                    } else {
                                        Log.e(TAG, "Failed to update Jobs community for user.");
                                        Toast.makeText(CommunitiesActivity.this, "Failed to update Jobs community for user.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e(TAG, "Failed to add user to Jobs community.");
                        Toast.makeText(CommunitiesActivity.this, "Failed to join Jobs community.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToCommunity(String role, String sanitizedEmail, ProgressDialog progressDialog, DatabaseReference databaseReference) {
        String communityName = role + " Community";

        // Add user to the chosen role's community and update user node
        databaseReference.child("communities").child(communityName).child("members").child(sanitizedEmail)
                .setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseReference.child("users").child(sanitizedEmail).child("joined_community").setValue(communityName)
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful()) {
                                        userCommunity = communityName;
                                        Log.d(TAG, "User added to community and user node updated.");
                                        loadAllCommunities(communityName, progressDialog, databaseReference);
                                    } else {
                                        Log.e(TAG, "Failed to update user node.");
                                        Toast.makeText(CommunitiesActivity.this, "Failed to update user node.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        Log.e(TAG, "Failed to add user to community.");
                        Toast.makeText(CommunitiesActivity.this, "Failed to join community.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void loadAllCommunities(String userCommunity, ProgressDialog progressDialog, DatabaseReference databaseReference) {
        databaseReference.child("communities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> communityRanksData = new ArrayList<>();

                // Add the "Jobs" community to the top
                DataSnapshot jobsCommunitySnapshot = snapshot.child("Jobs Community");
                if (jobsCommunitySnapshot.exists()) {
                    long memberCount = jobsCommunitySnapshot.child("members").getChildrenCount();
                    long communityPoints = jobsCommunitySnapshot.child("points").getValue(Long.class) != null
                            ? jobsCommunitySnapshot.child("points").getValue(Long.class)
                            : 0;

                    Map<String, String> jobsItem = new HashMap<>();
                    jobsItem.put("title", "Jobs Community ⭐");
                    jobsItem.put("description", "Members: " + memberCount + " - Points: " + communityPoints);
                    communityRanksData.add(jobsItem);
                }

                // Add other communities with rank 1
                for (DataSnapshot communitySnapshot : snapshot.getChildren()) {
                    String communityName = communitySnapshot.getKey();
                    if (communityName != null && !communityName.equals("Jobs Community")) {
                        // Count the number of members in the community
                        long memberCount = communitySnapshot.child("members").getChildrenCount();

                        // Assuming you have stored community points in the database under each community
                        long communityPoints = communitySnapshot.child("points").getValue(Long.class) != null
                                ? communitySnapshot.child("points").getValue(Long.class)
                                : 0;

                        Map<String, String> item = new HashMap<>();
                        item.put("title", "1. " + communityName + (communityName.equals(userCommunity) ? " ⭐" : ""));
                        item.put("description", "Members: " + memberCount + " - Points: " + communityPoints);
                        communityRanksData.add(item);
                    }
                }

                // Create and set the adapter for community ranks
                SimpleAdapter communityAdapter = new SimpleAdapter(
                        CommunitiesActivity.this,
                        communityRanksData,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "description"},
                        new int[]{android.R.id.text1, android.R.id.text2}
                );
                communityRanksListView.setAdapter(communityAdapter);

                communityRanksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map<String, String> selectedCommunity = communityRanksData.get(position);
                        String communityTitle = selectedCommunity.get("title");

                        if (communityTitle.contains("Jobs Community")) {
                            showCommunityDialog("Jobs Community");
                        } else if (communityTitle.contains(userCommunity)) {
                            showCommunityDialog(userCommunity);
                        }
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load communities: " + error.getMessage());
                Toast.makeText(CommunitiesActivity.this, "Failed to load communities.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void showCommunityDialog(String communityName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Community Options");
        builder.setMessage("Welcome to " + communityName + " !");

        builder.setPositiveButton("Enter Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Start ChatActivity or JobCommunity activity
                Intent intent;
                if (communityName.equals("Jobs Community")) {
                    intent = new Intent(CommunitiesActivity.this, JobCommunity.class);
                } else {
                    intent = new Intent(CommunitiesActivity.this, ChatActivity.class);
                    intent.putExtra("communityName", communityName);
                }
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadIndividualRanks() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> individualRanksData = new ArrayList<>();
                List<UserPoints> userPointsList = new ArrayList<>();

                // Fetch user data and assign random points
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.child("name").getValue(String.class);
                    if (userName != null) {
                        int randomPoints = (int) (Math.random() * 1000); // Assign random points
                        userPointsList.add(new UserPoints(userName, randomPoints));
                    }
                }

                // Sort users by points in descending order
                Collections.sort(userPointsList, new Comparator<UserPoints>() {
                    @Override
                    public int compare(UserPoints o1, UserPoints o2) {
                        return Integer.compare(o2.getPoints(), o1.getPoints());
                    }
                });

                int rank = 1;
                for (UserPoints userPoints : userPointsList) {
                    Map<String, String> item = new HashMap<>();
                    item.put("title", rank + ". " + userPoints.getUserName());
                    item.put("description", "Points: " + userPoints.getPoints());
                    individualRanksData.add(item);
                    rank++;
                }

                // Create and set the adapter for individual ranks
                SimpleAdapter individualAdapter = new SimpleAdapter(
                        CommunitiesActivity.this,
                        individualRanksData,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "description"},
                        new int[]{android.R.id.text1, android.R.id.text2}
                );
                individualRanksListView.setAdapter(individualAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load individual ranks: " + error.getMessage());
                Toast.makeText(CommunitiesActivity.this, "Failed to load individual ranks.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class UserPoints {
        private String userName;
        private int points;

        public UserPoints(String userName, int points) {
            this.userName = userName;
            this.points = points;
        }

        public String getUserName() {
            return userName;
        }

        public int getPoints() {
            return points;
        }
    }
}
