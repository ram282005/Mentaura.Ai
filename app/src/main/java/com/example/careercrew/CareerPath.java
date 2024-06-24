package com.example.careercrew;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CareerPath extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    String apiKey = "YjU2NjM5YTdmZTMwNDBhNDk0MDg1OWJjM2VlZmQ5YTA=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career_path);

        // Initialize views
        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        // Setup RecyclerView
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Set send button click listener
        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            if (!question.isEmpty()) {
                addToChat(question, Message.SENT_BY_ME);
                messageEditText.setText("");
                welcomeTextView.setVisibility(View.GONE);
                callAPI(question);
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addToChat(String message, int sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void callAPI(String question) {
        JSONObject jsonBody1 = new JSONObject();
        try {
            jsonBody1.put("personaId", "33ff52bf-9969-4edd-9ca1-76b1c4843188");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody2 = new JSONObject();
        try {
            jsonBody2.put("message", question);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString1 = jsonBody1.toString();
        RequestBody body1 = RequestBody.create(jsonString1, JSON);
        Request request1 = new Request.Builder()
                .url("https://api.hyperleap.ai/conversations/persona")
                .addHeader("appId", "cid-v1:e0dbebc6-6232-44bb-bfc6-223d5cdb4881")
                .addHeader("x-hl-api-key", apiKey)
                .post(body1)
                .build();

        String jsonString2 = jsonBody2.toString();
        RequestBody body2 = RequestBody.create(jsonString2, JSON);
        Request request2 = new Request.Builder()
                .url("https://api.hyperleap.ai/conversations/{conversationId}/continue-sync")
                .addHeader("appId", "cid-v1:e0dbebc6-6232-44bb-bfc6-223d5cdb4881")
                .addHeader("x-hl-api-key", apiKey)
                .post(body2)
                .build();

        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CareerPath.this, "API request failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String conversationId = jsonResponse.getString("conversationId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(CareerPath.this, "API response error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
