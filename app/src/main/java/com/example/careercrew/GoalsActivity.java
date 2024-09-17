package com.example.careercrew;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoalsActivity extends AppCompatActivity {

    private TextView preferredCareerpath;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ImageView backButton;
    private RecyclerView roadmapRecyclerView, weeklyGoalsRecyclerView;
    private RoadmapAdapter roadmapAdapter;
    private WeeklyGoalsAdapter weeklyGoalsAdapter;
    private OkHttpClient client = new OkHttpClient();

    // Persona and convo IDs
    private static final String ROADMAP_PERSONA_ID = "1afb55f7-df9f-4f97-bd44-3fe4ee09fb86";
    private static final String ROADMAP_CONVO_ID = "1f8d98fc-5c18-4bf3-8d8d-1d6398cd6739";
    private static final String GOALS_PERSONA_ID = "44200a69-9e94-458b-91fc-9a63628f377a";
    private static final String GOALS_CONVO_ID = "f005b9c5-8b5b-47e1-b2e7-a3cea195ac25";
    private static final String API_KEY = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=";

    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        preferredCareerpath = findViewById(R.id.preferred_careerpath);
        backButton = findViewById(R.id.backButton);
        roadmapRecyclerView = findViewById(R.id.roadmap_recycler_view);
        weeklyGoalsRecyclerView = findViewById(R.id.weekly_goals_recycler_view);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        roadmapAdapter = new RoadmapAdapter(new ArrayList<>());
        weeklyGoalsAdapter = new WeeklyGoalsAdapter(new ArrayList<>());

        roadmapRecyclerView.setAdapter(roadmapAdapter);
        weeklyGoalsRecyclerView.setAdapter(weeklyGoalsAdapter);

        roadmapRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weeklyGoalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUserData();

        //backButton.setOnClickListener(v -> onBackPressed());
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

                        // Show progress dialog while fetching data
                        showProgressDialog();

                        // Fetch roadmap and weekly goals from API
                        fetchRoadmapFromAPI(chosenRole, sanitizedEmail);
                        fetchWeeklyGoalsFromAPI(chosenRole, sanitizedEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
                }
            });
        }
    }

    private void fetchRoadmapFromAPI(String chosenRole, String sanitizedEmail) {
        String message = "Can you provide me with a detailed roadmap for becoming a successful " + chosenRole;
        sendApiRequest(ROADMAP_PERSONA_ID, ROADMAP_CONVO_ID, message, "roadmap", sanitizedEmail);
    }

    private void fetchWeeklyGoalsFromAPI(String chosenRole, String sanitizedEmail) {
        String message = "What are my weekly goals for becoming a successful " + chosenRole;
        sendApiRequest(GOALS_PERSONA_ID, GOALS_CONVO_ID, message, "weekly_goals", sanitizedEmail);
    }

    private void sendApiRequest(String personaId, String convoId, String message, String type, String sanitizedEmail) {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("personaId", personaId);
            jsonPayload.put("convoId", convoId);
            jsonPayload.put("message", message);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonPayload.toString()
            );

            Request request = new Request.Builder()
                    .url("https://api.hyperleap.ai/conversations/persona")
                    .addHeader("x-hl-api-key", API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("ApiRequest", "Network request failed: " + e.getMessage());
                    hideProgressDialog();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    hideProgressDialog();
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            if (jsonResponse.has("message")) {
                                String content = jsonResponse.getJSONObject("message").getString("content");
                                List<String> items = parseContent(content);

                                runOnUiThread(() -> {
                                    if (type.equals("roadmap")) {
                                        roadmapAdapter = new RoadmapAdapter(items);
                                        roadmapRecyclerView.setAdapter(roadmapAdapter);
                                    } else if (type.equals("weekly_goals")) {
                                        weeklyGoalsAdapter = new WeeklyGoalsAdapter(items);
                                        weeklyGoalsRecyclerView.setAdapter(weeklyGoalsAdapter);
                                    }
                                });

                                // Save to Firebase
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(type, content);
                                databaseReference.child(sanitizedEmail).updateChildren(updates);

                            } else {
                                Log.e("ApiRequest", type + " field is missing or empty.");
                            }

                        } catch (JSONException e) {
                            Log.e("ApiRequest", "JSON parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e("ApiRequest", "Response error: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            Log.e("ApiRequest", "JSON creation error: " + e.getMessage());
            hideProgressDialog();
        }
    }

    private List<String> parseContent(String content) {
        List<String> items = new ArrayList<>();
        String[] lines = content.split("\\n");
        for (String line : lines) {
            items.add(line);
        }
        return items;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we fetch your roadmap and goals...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
