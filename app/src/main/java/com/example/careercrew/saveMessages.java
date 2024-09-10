package com.example.careercrew;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class saveMessages extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TipAdapter tipAdapter;
    private List<String> tipsList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userEmailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_messages);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_tips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tipsList = new ArrayList<>();
        tipAdapter = new TipAdapter(tipsList);
        recyclerView.setAdapter(tipAdapter);

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Fetch the user's career tips from Firebase
        fetchCareerTips();

        // Set up window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method to fetch career tips from Firebase
    private void fetchCareerTips() {
        if (mAuth.getCurrentUser() != null) {
            userEmailKey = mAuth.getCurrentUser().getEmail().replace(".", ",");
            DatabaseReference userTipsRef = mDatabase.child("users").child(userEmailKey).child("career_tips");

            // Listen for career tips in Firebase
            userTipsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Loop through all the tips and add them to the list
                        for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                            String tip = tipSnapshot.getValue(String.class);
                            tipsList.add(tip); // Add tip to the list
                        }
                        // Notify the adapter that data has changed
                        tipAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firebase", "No career tips found for this user.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Firebase", "Failed to fetch career tips: " + error.getMessage());
                }
            });
        }
    }
}
