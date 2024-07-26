package com.example.careercrew;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AIRecommendedJobFragment extends Fragment {

    private static final String TAG = "AIRecommendedJobsFragment";
    private RadioGroup radioGroup;
    private Button submitButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_recommended_jobs, container, false);

        radioGroup = view.findViewById(R.id.radio_group);
        submitButton = view.findViewById(R.id.submit_button);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        fetchRecommendedJobs();

        submitButton.setOnClickListener(v -> submitChoice());

        return view;
    }

    private void fetchRecommendedJobs() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");
                databaseReference.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String job1 = dataSnapshot.child("airecommendedjob1").getValue(String.class);
                        String job2 = dataSnapshot.child("airecommendedjob2").getValue(String.class);
                        String job3 = dataSnapshot.child("airecommendedjob3").getValue(String.class);

                        if (job1 != null) {
                            radioGroup.addView(createRadioButton(job1));
                        }
                        if (job2 != null) {
                            radioGroup.addView(createRadioButton(job2));
                        }
                        if (job3 != null) {
                            radioGroup.addView(createRadioButton(job3));
                        }
                        Log.d(TAG, "Fetched recommended jobs successfully");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error: " + databaseError.getMessage());
                    }
                });
            } else {
                Log.e(TAG, "User email is null, cannot fetch recommended jobs.");
            }
        } else {
            Log.e(TAG, "Current user is null, cannot fetch recommended jobs.");
        }
    }


    private RadioButton createRadioButton(String job) {
        RadioButton radioButton = new RadioButton(getActivity());
        radioButton.setText(job);
        radioButton.setId(View.generateViewId());
        return radioButton;
    }

    private void submitChoice() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
            String selectedJob = selectedRadioButton.getText().toString();
            Log.d(TAG, "User selected job: " + selectedJob);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                if (userEmail != null) {
                    String sanitizedEmail = userEmail.replace(".", ",");
                    databaseReference.child(sanitizedEmail).child("chosenJob").setValue(selectedJob)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Job chosen successfully!", Toast.LENGTH_SHORT).show();
                                    navigateToHome();
                                } else {
                                    Log.e(TAG, "Error storing chosen job", task.getException());
                                }
                            });
                } else {
                    Log.e(TAG, "User email is null, cannot store chosen job.");
                }
            } else {
                Log.e(TAG, "Current user is null, cannot store chosen job.");
            }
        } else {
            Toast.makeText(getActivity(), "Please select a job", Toast.LENGTH_SHORT).show();
        }
    }


    private void navigateToHome() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
