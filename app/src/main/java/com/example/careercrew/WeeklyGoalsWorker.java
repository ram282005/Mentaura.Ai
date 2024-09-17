package com.example.careercrew;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeeklyGoalsWorker extends Worker {

    private OkHttpClient client = new OkHttpClient();  // HTTP Client for API requests
    private String apiKey = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=";  // Replace with your Hyperleap.ai API key
    private String personaId = "7b53326e-8121-4936-b931-f1809fcf7f3e";  // Replace with your persona ID
    private String convoId = "c2dca827-5ddb-41b6-8963-dc1bb3443065";  // Replace with your conversation ID
    private DatabaseReference databaseReference;

    public WeeklyGoalsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        if (email != null) {
            String sanitizedEmail = email.replace(".", ",");
            databaseReference.child(sanitizedEmail).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String chosenRole = task.getResult().child("chosen_role").getValue(String.class);
                    if (chosenRole != null) {
                        fetchWeeklyGoalsFromAPI(chosenRole, sanitizedEmail);
                    }
                }
            });
        }
        return Result.success();
    }

    // Function to fetch weekly goals from Hyperleap.ai API
    private void fetchWeeklyGoalsFromAPI(String chosenRole, String sanitizedEmail) {
        String requestBody = "weekly goals + " + chosenRole + " + weekly assessment";
        Request request = new Request.Builder()
                .url("https://api.hyperleap.ai/weekly-goals")  // Example endpoint
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("persona-id", personaId)
                .addHeader("conversation-id", convoId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String weeklyGoals = jsonResponse.getString("weekly_goals");

                        // Save weekly goals to Firebase
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("weekly_goals", weeklyGoals);
                        databaseReference.child(sanitizedEmail).updateChildren(updates);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
