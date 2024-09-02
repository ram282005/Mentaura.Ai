package com.example.careercrew;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class DoubtsChatActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    RecyclerView recyclerView;
    TextView name;
    EditText messageEditText;
    ImageButton sendButton;
    ImageView back, cameraButton;
    List<Message1> messageList1;
    MessageAdapter1 messageAdapter1;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts_chat);

        messageList1 = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        back = findViewById(R.id.backbutton);
        cameraButton = findViewById(R.id.camera_icon);
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

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message1.SENT_BY_ME);
            messageEditText.setText(null);
            handleUserMessage(question);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(DoubtsChatActivity.this, CommunitiesActivity.class);
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
        addToChat("Our mentor would reach you in a few minutes.", Message1.SENT_BY_BOT);
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
                            addToChat(welcomeMessage, Message1.SENT_BY_BOT);
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
        } else {
            response = "Sorry, I don't have information on that. Can you ask something else?";
        }

        addToChat(response, Message1.SENT_BY_BOT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}
