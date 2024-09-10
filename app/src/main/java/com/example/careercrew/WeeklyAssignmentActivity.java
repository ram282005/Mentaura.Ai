package com.example.careercrew;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class WeeklyAssignmentActivity extends AppCompatActivity {

    private EditText editTextCareerGoal;
    private ImageButton buttonSubmit;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private ProgressDialog progressDialog;
    private RelativeLayout bottomLayout;

    private static final String PERSONA_ID = "2ef9447f-e4fd-46a3-9f4c-af404a9a759e";
    private static final String CONVERSATION_ID = "bb139998-6ccc-43bc-b000-74d298c7e5f8";
    private static final String API_KEY = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=";
    private static final String PREFS_NAME = "ChatPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_assignment);

        mAuth = FirebaseAuth.getInstance();

        editTextCareerGoal = findViewById(R.id.message_edit_text);
        buttonSubmit = findViewById(R.id.send_btn);
        ImageView homeImageView = findViewById(R.id.backbutton);
        bottomLayout = findViewById(R.id.bottom_layout);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextCareerGoal.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                } else {
                    Toast.makeText(WeeklyAssignmentActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String messageText) {
        // Add message to list and update adapter
        Message message = new Message(messageText, true, new Date());
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // Clear input field
        editTextCareerGoal.setText("");

        // Save messages to shared preferences
        saveChats();

        // Send message to AI and get response
        new SendMessageToAI().execute(messageText);
    }

    private class SendMessageToAI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String messageText = strings[0];
            try {
                URL url = new URL("https://api.hyperleap.ai/conversations/" + CONVERSATION_ID + "/continue-sync");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("x-hl-api-key", API_KEY);
                connection.setDoOutput(true);

                JSONObject jsonInput = new JSONObject();
                jsonInput.put("personaId", PERSONA_ID);
                jsonInput.put("message", messageText);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code == 200) {
                    try (Scanner scanner = new Scanner(connection.getInputStream())) {
                        scanner.useDelimiter("\\A");
                        return scanner.hasNext() ? scanner.next() : "";
                    }
                } else {
                    return "Error: " + code;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Error") || result.startsWith("Exception")) {
                Toast.makeText(WeeklyAssignmentActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                // Process AI response and display it in chat
                Message message = new Message(result, false, new Date());
                messageList.add(message);
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        }
    }

    private void saveChats() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // Code to save chat history in shared preferences
        // e.g., convert messageList to JSON and store it
        editor.apply();
    }
}
