package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    EditText messageEditText;
    ImageButton sendButton;
    ImageView back;
    List<Message1> messageList1;
    MessageAdapter1 messageAdapter1;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageList1 = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        back = findViewById(R.id.backbutton);
        name = findViewById(R.id.community);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup recycler view
        messageAdapter1 = new MessageAdapter1(messageList1);
        recyclerView.setAdapter(messageAdapter1);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Fetch the user's joined community from Firebase and send the welcome message
        fetchUserCommunity();



        back.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, CommunitiesActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserCommunity() {
        // Get the current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Replace periods in the email to match Firebase key format
            String userEmailKey = currentUser.getEmail().replace(".", ","); // Matches your path structure
            DatabaseReference userRef = mDatabase.child("users").child(userEmailKey);

            // Fetch the community name
            userRef.child("joined_community").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String communityName = snapshot.getValue(String.class);
                        if (communityName != null) {
                            // Update the UI with community name
                            name.setText(communityName);

                            // Send welcome message
                            String welcomeMessage = "Hello, welcome to the \"" + communityName + "\"! Success is not the result of spontaneous combustion. You must set yourself on fire. Every job you take, every skill you acquire, every step you take, brings you closer to your dream career. Embrace the journey, learn from every experience, and never stop striving for excellence. We will help you in that from now.";
                            addToChat(welcomeMessage, Message1.SENT_BY_BOT);

                            Log.d("WelcomeMessage", "Welcome message sent: " + welcomeMessage);
                        } else {
                            Log.e("WelcomeMessage", "Community name is null");
                        }
                    } else {
                        Log.e("WelcomeMessage", "User does not have a joined community");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to fetch community data: " + error.getMessage());
                }
            });
        }
    }



    void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList1.add(new Message1(message, sentBy));
            messageAdapter1.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter1.getItemCount());
        });
    }

    void handleUserMessage(String userMessage) {
        String response;
        userMessage = userMessage.toLowerCase();

        if (userMessage.equals("hello") || userMessage.equals("hi") || userMessage.equals("hey")) {
            response = "Hi there! How can I help?";
        } else if (userMessage.equals("how are you")) {
            response = "I'm just a program, but thank you for asking!";
        } else if (userMessage.equals("bye")) {
            response = "Goodbye! Have a great day!";
        } else if (userMessage.contains("weather")) {
            response = "Sorry, I can't provide real-time weather updates.";
        } else if (userMessage.contains("joke")) {
            response = tellJoke();
        } else if (userMessage.contains("fact")) {
            response = tellFact();
        } else if (userMessage.contains("quote")) {
            response = tellQuote();
        } else {
            response = "Sorry, I don't have information on that. Can you ask something else?";
        }

        addToChat(response, Message1.SENT_BY_BOT);
    }

    private String tellJoke() {
        String[] jokes = {
                "Why don't scientists trust atoms? Because they make up everything!",
                "I told my wife she was drawing her eyebrows too high. She looked surprised.",
                "Parallel lines have so much in common. It’s a shame they’ll never meet.",
                "Why did the scarecrow win an award? Because he was outstanding in his field!",
                "I'm reading a book on anti-gravity. It's impossible to put down!"
        };
        return jokes[new Random().nextInt(jokes.length)];
    }

    private String tellFact() {
        String[] facts = {
                "The Earth's atmosphere is composed of approximately 78% nitrogen, 21% oxygen, and 1% other gases.",
                "A group of flamingos is called a 'flamboyance'.",
                "The shortest war in history was between Britain and Zanzibar on August 27, 1896. Zanzibar surrendered after 38 minutes.",
                "Honey never spoils. Archaeologists have found pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible.",
                "Octopuses have three hearts."
        };
        return facts[new Random().nextInt(facts.length)];
    }

    private String tellQuote() {
        String[] quotes = {
                "The only way to do great work is to love what you do. - Steve Jobs",
                "In the midst of winter, I found there was, within me, an invincible summer. - Albert Camus",
                "Life is what happens when you're busy making other plans. - John Lennon",
                "The greatest glory in living lies not in never falling, but in rising every time we fall. - Nelson Mandela",
                "Be yourself; everyone else is already taken. - Oscar Wilde"
        };
        return quotes[new Random().nextInt(quotes.length)];
    }

    void addResponse(String response) {
        messageList1.remove(messageList1.size() - 1);
        addToChat(response, Message1.SENT_BY_BOT);
    }

    void callAPI(String question) {
        // okhttp
        messageList1.add(new Message1("Typing... ", Message1.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo-instruct-0914");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer YOUR_API_KEY")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API Request", "Failed to load response", e);
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().toString());
                }
            }
        });
    }
}