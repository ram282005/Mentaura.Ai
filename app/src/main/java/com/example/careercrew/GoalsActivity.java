package com.example.careercrew;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GoalsActivity extends AppCompatActivity {

    private TextView preferredCareerpath, roadmapContent, weeklyGoalsContent, anotherBox;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ImageButton backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        preferredCareerpath = findViewById(R.id.preferred_careerpath);
        roadmapContent = findViewById(R.id.roadmap_content);
        weeklyGoalsContent = findViewById(R.id.weekly_goals_content);
        anotherBox = findViewById(R.id.another_box);
        backButton = findViewById(R.id.backButton);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        loadUserData();

        // Set up the back button listener
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        String email = mAuth.getCurrentUser().getEmail();
        if (email != null) {
            String sanitizedEmail = email.replace(".", ",");
            databaseReference.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String chosenRole = dataSnapshot.child("chosen_role").getValue(String.class);
                    if (chosenRole != null) {
                        preferredCareerpath.setText(chosenRole);
                    }
                    roadmapContent.setText("• Start Learning Basics:\n  - Learn the basics of programming with C language.\n  - Get familiar with operating systems like Windows and Ubuntu.\n\n" +
                            "• Intermediate Skills:\n  - Master data structures and algorithms.\n  - Develop problem-solving skills by participating in coding challenges.\n\n" +
                            "• Advanced Skills:\n  - Learn advanced topics such as multithreading, network programming, and system design.\n  - Contribute to open-source projects to gain real-world \n\n" );
                    weeklyGoalsContent.setText("• Week 1:\n  - Complete the first three modules of the C programming course.\n  - Set up a dual-boot system with Windows and Ubuntu.\n  - Solve at least 5 problems on LeetCode.\n  - Read chapters 1-2 of 'Introduction to Algorithms'.\n  - Build a simple calculator app.\n  - Attend a local networking event.");
                    anotherBox.setText(" Update resume.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
}