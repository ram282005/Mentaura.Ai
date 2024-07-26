package com.example.careercrew;

import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AIRecommendedJobsFragment extends Fragment {

    private static final String TAG = "AIRecommendedJobsFragment";
    private RadioGroup jobRadioGroup;
    private RadioButton job1RadioButton, job2RadioButton, job3RadioButton, selectedRadioButton;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String CHOSEN_ROLE = "chosen_role";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_recommended_jobs, container, false);

        jobRadioGroup = view.findViewById(R.id.job_radio_group);
        job1RadioButton = view.findViewById(R.id.job1_radio);
        job2RadioButton = view.findViewById(R.id.job2_radio);
        job3RadioButton = view.findViewById(R.id.job3_radio);
        saveButton = view.findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);

        fetchRecommendedJobs();

        saveButton.setOnClickListener(v -> saveSelectedJob());

        return view;
    }

    private void fetchRecommendedJobs() {
        String userEmail = mAuth.getCurrentUser().getEmail();
        if (userEmail != null) {
            String sanitizedEmail = userEmail.replace(".", ",");
            DatabaseReference userReference = databaseReference.child(sanitizedEmail);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String job1 = dataSnapshot.child("aijobrecommended1").getValue(String.class);
                        String job2 = dataSnapshot.child("aijobrecommended2").getValue(String.class);
                        String job3 = dataSnapshot.child("aijobrecommended3").getValue(String.class);

                        if (job1 != null) job1RadioButton.setText(job1);
                        if (job2 != null) job2RadioButton.setText(job2);
                        if (job3 != null) job3RadioButton.setText(job3);
                    } else {
                        Log.e(TAG, "No recommended jobs found for user");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read recommended jobs", databaseError.toException());
                }
            });
        } else {
            Log.e(TAG, "User email is null, cannot fetch jobs");
        }
    }

    private void saveSelectedJob() {
        int selectedId = jobRadioGroup.getCheckedRadioButtonId();

        if (selectedId != -1) {
            selectedRadioButton = getView().findViewById(selectedId);
            String selectedJob = selectedRadioButton.getText().toString();

            String userEmail = mAuth.getCurrentUser().getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(sanitizedEmail);
                userReference.child("chosenRole").setValue(selectedJob)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                sharedPreferences.edit().putString(CHOSEN_ROLE, selectedJob).apply();
                                Toast.makeText(getActivity(), "Role saved successfully", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction().remove(AIRecommendedJobsFragment.this).commit();
                            } else {
                                Toast.makeText(getActivity(), "Failed to save role", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "User email is null, cannot save role", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please select a job", Toast.LENGTH_SHORT).show();
        }
    }
}
