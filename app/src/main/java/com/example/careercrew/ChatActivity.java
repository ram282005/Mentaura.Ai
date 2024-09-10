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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final String CONVERSATION_ID = "f877a490-2d6c-49ef-8d26-d7ee1d111229"; // Replace with the actual conversation ID
    private static final String PERSONA_ID = "615328a9-d39a-4cd5-9440-c488110eac71"; // Replace with the persona ID

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
                // After loading, fetch new tips
                fetchPersonalizedTips(role);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load existing tips: " + error.getMessage());
            }
        });
    }

    // Fetch new tips from Hyperleap AI API based on the user's role, ensuring no duplicates
    private void fetchPersonalizedTips(String role) {
        String message = "Provide career tips for someone aspiring to be a " + role;

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
                                addToChat("Tip for " + role + ": " + tip, Message1.SENT_BY_BOT);  // Display the AI tip
                                saveTipToFirebase(role, tip);  // Save the new tip
                            } else {
                                addToChat("Received a duplicate tip. Requesting a new one...", Message1.SENT_BY_BOT);
                                fetchPersonalizedTips(role);  // Request a new tip
                            }

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

    // Save the AI-generated career tip to Firebase under the user's node
    private void saveTipToFirebase(String role, String tip) {
        DatabaseReference userTipsRef = mDatabase.child("users").child(userEmailKey).child("career_tips");
        userTipsRef.push().setValue(tip) // Use push() to ensure uniqueness in the database
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storedTipsSet.add(tip); // Add the new tip to the stored tips set
                        Log.d("Firebase", "Career tip successfully saved to Firebase.");
                    } else {
                        Log.e("Firebase", "Failed to save career tip: " + task.getException());
                    }
                });
    }

    // Helper function to add messages to the chat and update UI
    void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList1.add(new Message1(message, sentBy));
            messageAdapter1.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter1.getItemCount());  // Automatically scroll to the bottom
        });
    }
}
