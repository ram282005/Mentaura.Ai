package com.example.careercrew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AIRecommendedJobFragment extends Fragment {

    private OnJobSelectedListener listener;

    public AIRecommendedJobFragment() {
        // Required empty public constructor
    }

    public interface OnJobSelectedListener {
        void onJobSelected(String job);
    }

    public void setOnJobSelectedListener(OnJobSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_a_i_recommended_job, container, false);

        if (getArguments() != null) {
            String job1 = getArguments().getString("job1", "No Job");
            String job2 = getArguments().getString("job2", "No Job");
            String job3 = getArguments().getString("job3", "No Job");

            // Here, display the jobs or do something with them
            Toast.makeText(getContext(), "Jobs: " + job1 + ", " + job2 + ", " + job3, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No jobs found", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
