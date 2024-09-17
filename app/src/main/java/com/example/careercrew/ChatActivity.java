package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView name;
    ImageView back;
    List<Message1> messageList1;
    MessageAdapter1 messageAdapter1;
    OkHttpClient client = new OkHttpClient();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String chosenRole; // Store the fetched chosen role
    private String userEmailKey; // Store the user's email key for database reference
    private Set<String> storedTipsSet; // To store and check duplicates

    // Hyperleap API constants
    private static final String API_URL = "https://api.hyperleap.ai/conversations/";
    private static final String API_KEY = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE="; // Replace with your Hyperleap API Key
    private static final String CONVERSATION_ID = "7bcf7e1d-2cd9-4c8e-aa5f-408cc6de3d76"; // Replace with the actual conversation ID
    private static final String PERSONA_ID = "3d7cab73-cf2c-4595-8638-e6c380fa2847"; // Replace with the persona ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageList1 = new ArrayList<>();
        storedTipsSet = new HashSet<>(); // Initialize the set to store unique tips

        recyclerView = findViewById(R.id.recycler_view);
        back = findViewById(R.id.backbutton);
        name = findViewById(R.id.community);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup recycler view
        messageAdapter1 = new MessageAdapter1(messageList1);
        recyclerView.setAdapter(messageAdapter1);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true); // Ensure the latest message is visible
        recyclerView.setLayoutManager(llm);

        // Fetch the user's chosen role and community, and display the AI tips automatically
        fetchUserRoleAndCommunity();

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, CommunitiesActivity.class);
            startActivity(intent);
        });
    }

    // Fetch the user's role and community from Firebase
    private void fetchUserRoleAndCommunity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmailKey = currentUser.getEmail().replace(".", ",");
            DatabaseReference userRef = mDatabase.child("users").child(userEmailKey);

            // Fetch both role and community
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String communityName = snapshot.child("joined_community").getValue(String.class);
                        chosenRole = snapshot.child("chosen_role").getValue(String.class);

                        // Display the welcome message immediately
                        if (communityName != null) {
                            name.setText(communityName);
                            addToChat("Hello, welcome to the \"" + communityName + "\"! Success is not the result of spontaneous combustion. You must set yourself on fire. Every job you take, every skill you acquire, every step you take, brings you closer to your dream career. Embrace the journey, learn from every experience, and never stop striving for excellence. We will help you in that from now.", Message1.SENT_BY_BOT);
                        }

                        // If a role is found, load existing tips and fetch new ones
                        if (chosenRole != null) {
                            loadExistingTipsAndFetchNew(chosenRole); // Load existing tips first
                            onRoleFetched(chosenRole);  // Schedule the daily work
                        }
                    } else {
                        Log.e("Firebase", "User does not have role or community data.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to fetch data: " + error.getMessage());
                }
            });
        }
    }

    // Load existing tips from Firebase and fetch new tips
    private void loadExistingTipsAndFetchNew(String role) {
        DatabaseReference userTipsRef = mDatabase.child("users").child(userEmailKey).child("career_tips");

        // Fetch existing tips from Firebase to check for duplicates
        userTipsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                        String tip = tipSnapshot.getValue(String.class);
                        storedTipsSet.add(tip); // Add existing tips to the set
                    }
                }
                // After loading, fetch new tips and ensure the correct starting index is passed
                fetchPersonalizedTips(role, 0);  // Start with index 0
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load existing tips: " + error.getMessage());
            }
        });
    }

    // Fetch new tips from Hyperleap AI API based on the user's role, ensuring no duplicates
    private void fetchPersonalizedTips(String role, int messageIndex) {
        String[] messages = {
                "Provide career tips for someone aspiring to be a " + role,
                "Provide motivation for someone aspiring to be a " + role,
                "Provide latest updates for someone aspiring to be a " + role
        };

        if (messageIndex >= messages.length) {
            // Stop when all messages have been sent
            return;
        }

        String message = messages[messageIndex];

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("personaId", PERSONA_ID);
            jsonBody.put("message", message);

            String apiUrl = API_URL + CONVERSATION_ID + "/continue-sync";
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("x-hl-api-key", API_KEY)
                    .patch(body)
                    .build();

            // Send request to Hyperleap API and handle the response
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("API Request", "Failed to load response", e);
                    addToChat("Failed to get tips: " + e.getMessage(), Message1.SENT_BY_BOT);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(response.body().string());
                            String tip = extractReply(responseObject);

                            // Check if the tip is a duplicate
                            if (!storedTipsSet.contains(tip)) {
                                addToChat("The role  " + role + ": " + tip, Message1.SENT_BY_BOT);  // Display the AI tip
                                saveTipToFirebase(role, tip);  // Save the new tip
                            } else {
                                addToChat("Received a duplicate tip. Requesting a new one...", Message1.SENT_BY_BOT);
                            }

                            // Proceed to the next message after processing the current one
                            fetchPersonalizedTips(role, messageIndex + 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        addToChat("Failed to get tips. Response error: " + response.code(), Message1.SENT_BY_BOT);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Extract the reply from the Hyperleap API response
    private String extractReply(JSONObject responseObject) {
        try {
            if (responseObject.has("choices") && responseObject.getJSONArray("choices").length() > 0) {
                JSONObject choice = responseObject.getJSONArray("choices").getJSONObject(0);
                return choice.getJSONObject("message").getString("content");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No reply found in the response.";
    }

    // Save the new tip to Firebase to avoid duplicates
    private void saveTipToFirebase(String role, String tip) {
        DatabaseReference userTipsRef = mDatabase.child("users").child(userEmailKey).child("career_tips").push();
        userTipsRef.setValue(tip);
        storedTipsSet.add(tip); // Add the tip to the set
    }

    // Add message to chat recycler view
    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList1.add(new Message1(message, sentBy));
            messageAdapter1.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter1.getItemCount() - 1);
        });
    }

    // Schedule daily work at specific times (9 AM, 2 PM, 8 PM)
    private void scheduleWork(String role) {
        Data morningData = new Data.Builder().putString("role", role).putInt("messageIndex", 0).build();
        Data afternoonData = new Data.Builder().putString("role", role).putInt("messageIndex", 1).build();
        Data eveningData = new Data.Builder().putString("role", role).putInt("messageIndex", 2).build();

        // Schedule the work requests for the morning (9-10 AM), afternoon (2-3 PM), and evening (8-9 PM)
        scheduleDailyWork(morningData, 9);
        scheduleDailyWork(afternoonData, 14);
        scheduleDailyWork(eveningData, 20);
    }

    // Schedule a daily work at a specific hour
    private void scheduleDailyWork(Data data, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (delay < 0) {
            // If the time has already passed for today, schedule for tomorrow
            delay += TimeUnit.DAYS.toMillis(1);
        }

        // Schedule the work with the calculated delay
        WorkRequest dailyWork = new OneTimeWorkRequest.Builder(SendTipsWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(this).enqueue(dailyWork);
    }

    // Call this method after fetching the role to schedule the work
    private void onRoleFetched(String role) {
        scheduleWork(role);
    }
}