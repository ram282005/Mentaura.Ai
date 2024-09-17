package com.example.careercrew;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DoubtsChatActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    RecyclerView recyclerView;
    TextView name;
    EditText messageEditText;
    ImageButton sendButton;
    ImageView back, cameraButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String PERSONA_ID = "e149c5f5-4fc8-491a-a4bd-879a06431d57";
    private static final String CONVERSATION_ID = "ccd31043-81a7-42e9-b993-3595d9acb533";
    private static final String API_KEY = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=";
    private static final String PREFS_NAME = "ChatPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts_chat);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        back = findViewById(R.id.backbutton);
        cameraButton = findViewById(R.id.camera_icon);
        name = findViewById(R.id.community);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Fetch the user's joined community from Firebase and send the welcome message
        fetchUserCommunity();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                } else {
                    Toast.makeText(DoubtsChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(DoubtsChatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        cameraButton.setOnClickListener(v -> {
            selectImage();
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Upload from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DoubtsChatActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, which) -> {
            if (items[which].equals("Camera")) {
                if (ContextCompat.checkSelfPermission(DoubtsChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DoubtsChatActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    openCamera();
                }
            } else if (items[which].equals("Upload from Gallery")) {
                openGallery();
            } else if (items[which].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Uri imageUri = data.getData();
                handleImageResponse(imageUri);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                handleImageResponse(selectedImageUri);
            }
        }
    }

    private void handleImageResponse(Uri imageUri) {
        sendMessage("\"Our mentor would reach you in a few minutes.\"");
    }

    private void fetchUserCommunity() {
        // Get the current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmailKey = currentUser.getEmail().replace(".", ",");
            DatabaseReference userRef = mDatabase.child("users").child(userEmailKey);

            userRef.child("joined_community").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String communityName = snapshot.getValue(String.class);
                        if (communityName != null) {
                            String welcomeMessage = "Hello, welcome to the \"" + communityName + "\" Doubts Section. Please feel free to ask your questions, and they will be addressed within a few minutes.";

                            // Add the welcome message to the messageList but NOT the database
                            Message welcomeMsg = new Message(welcomeMessage, false, new Date());  // 'false' indicates bot message
                            messageList.add(welcomeMsg);

                            // Notify adapter to update RecyclerView UI without saving to Firebase
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to fetch community data: " + error.getMessage());
                }
            });
        }
    }


    private void sendMessage(String messageText) {
        // Add message to list and update adapter
        Message message = new Message(messageText, true, new Date());
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // Clear input field
        messageEditText.setText("");

        showTypingIndicator();

        // Save messages to shared preferences
        saveChatsToFirebase();

        // Send message to AI and get response
        new SendMessageToAI().execute(messageText);
    }

    private void showTypingIndicator() {
        Message typingMessage = new Message(true);
        messageList.add(typingMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void hideTypingIndicator() {
        if (messageList.size() > 0) {
            Message lastMessage = messageList.get(messageList.size() - 1);
            if (lastMessage.isTyping()) {
                messageList.remove(messageList.size() - 1);
                messageAdapter.notifyItemRemoved(messageList.size());
            }
        }
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
                Toast.makeText(DoubtsChatActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                hideTypingIndicator();

                String assistant = parseAIResponse(result);
                // Process AI response and display it in chat
                Message message = new Message(assistant, false, new Date());
                messageList.add(message);
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        }
    }

    private String parseAIResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choicesArray = jsonResponse.getJSONArray("choices");
            if (choicesArray.length() > 0) {
                JSONObject choice = choicesArray.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                return message.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error parsing response";
    }

    private void saveChatsToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmailKey = currentUser.getEmail().replace(".", ",");  // Firebase doesn't allow '.' in keys
            DatabaseReference chatRef = mDatabase.child("users").child(userEmailKey).child("Doubts");

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long conversationCount = snapshot.getChildrenCount();

                    String newConversationCount = "conversation" + (conversationCount + 1);

                    List<Map<String, Object>> conversation = new ArrayList<>();
                    for (Message message : messageList) {
                        Map<String, Object> chatMap = new HashMap<>();
                        if (message.isUser()) {
                            chatMap.put("user", message.getText());
                        } else {
                            chatMap.put("bot", message.getText());
                        }
                        chatMap.put("timestamp", message.getTimestamp().getTime());
                        conversation.add(chatMap);
                    }

                    chatRef.child(newConversationCount).setValue(conversation)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Chat history saved successfully under " + newConversationCount);
                                } else {
                                    Log.e("Firebase", "Failed to save chat history.", task.getException());
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to read conversations: " + error.getMessage());
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}