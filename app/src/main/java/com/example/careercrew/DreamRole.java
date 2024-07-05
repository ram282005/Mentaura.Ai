package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DreamRole extends AppCompatActivity {

    private EditText etDreamRole, etDreamCompany;
    private Button buttonSubmit;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_role);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etDreamRole = findViewById(R.id.et_dream_role);
        etDreamCompany = findViewById(R.id.et_dream_company);
        buttonSubmit = findViewById(R.id.button);

        buttonSubmit.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        String dreamRole = etDreamRole.getText().toString().trim();
        String dreamCompany = etDreamCompany.getText().toString().trim();

        if (TextUtils.isEmpty(dreamRole) || TextUtils.isEmpty(dreamCompany)) {
            Toast.makeText(this, "Please enter both dream role and company", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                mDatabase.child("users").child(emailKey).child("dreamRole").setValue(dreamRole);
                mDatabase.child("users").child(emailKey).child("dreamCompany").setValue(dreamCompany);
                mDatabase.child("users").child(emailKey).child("lastActivity").setValue("CourseRecommendationActivity")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(DreamRole.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
                                navigateToCourseRecommendation();
                            } else {
                                Toast.makeText(DreamRole.this, "Failed to submit data", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Error retrieving user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCourseRecommendation() {
        Intent intent = new Intent(DreamRole.this, CourseRecommendationActivity.class);
        intent.putExtra("DREAM_ROLE", etDreamRole.getText().toString().trim());
        intent.putExtra("DREAM_COMPANY", etDreamCompany.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
