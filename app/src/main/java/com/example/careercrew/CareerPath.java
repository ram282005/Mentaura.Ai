package com.example.careercrew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.annotations.NonNull;

public class CareerPath extends AppCompatActivity {

    private EditText editTextCareerGoal;
    private ImageButton buttonSubmit;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private ProgressDialog progressDialog;
    private Button navigateButton;
    private RelativeLayout bottomLayout;
    private LinearLayout chatLayout;

    private static final String PERSONA_ID = "c216cde8-3b5d-41fc-8340-2874871db439";
    private static final String CONVERSATION_ID = "5d85c664-9569-41af-ba36-a349311f6fde";
    private static final String API_KEY = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=";
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String PREFS_KEY = "ChatMessages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career_path);

        mAuth = FirebaseAuth.getInstance();

        editTextCareerGoal = findViewById(R.id.message_edit_text);
        buttonSubmit = findViewById(R.id.send_btn);
        ImageView homeImageView = findViewById(R.id.imageView1);
        bottomLayout = findViewById(R.id.bottom_layout);
        chatLayout = findViewById(R.id.chat_layout);

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
                    Toast.makeText(CareerPath.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomepage();
            }
        });




        showLoadingDialog();
        loadChats();
    }

    private void navigateToHomepage() {
        Intent intent = new Intent(CareerPath.this, HomePage.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Log.d("CareerPath", "Navigating to MainActivity");
        Intent intent = new Intent(CareerPath.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while chats are loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void promptInitialMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start Conversation");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter 'Hello' or 'Hi'");
        builder.setView(input);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String messageText = input.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                } else {
                    Toast.makeText(CareerPath.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendMessage(String messageText) {
        // Add message to list and update adapter
        Message message = new Message(messageText, true, new Date());
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // Clear input field
        editTextCareerGoal.setText("");

        // Show typing indicator
        showTypingIndicator();

        // Save messages to shared preferences
        saveChats();

        // Send message to AI and get response
        fetchUserDetailsAndSendToAI(messageText);
        Log.d("CareerPath", "sendMessage: Message sent and saved.");
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

    private void saveChats() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();
        for (Message message : messageList) {
            if (!message.isTyping()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("text", message.getText());
                    jsonObject.put("isUser", message.isUser());
                    jsonObject.put("timestamp", message.getTimestamp().getTime());
                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        editor.putString(PREFS_KEY, jsonArray.toString());
        editor.apply();
    }

    private void loadChats() {
        new LoadChatsTask().execute();
    }

    private class LoadChatsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String chatString = prefs.getString(PREFS_KEY, null);

            if (chatString != null) {
                try {
                    JSONArray jsonArray = new JSONArray(chatString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String text = jsonObject.getString("text");
                        boolean isUser = jsonObject.getBoolean("isUser");
                        long timestamp = jsonObject.getLong("timestamp");
                        Message message = new Message(text, isUser, new Date(timestamp));
                        messageList.add(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
            progressDialog.dismiss();
            promptInitialMessage();
        }
    }

    private void fetchUserDetailsAndSendToAI(String messageText) {
        String email = mAuth.getCurrentUser().getEmail();
        if (email != null) {
            String emailKey = email.replace(".", ",");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(emailKey);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Fetch user details
                        String age = snapshot.child("age").getValue(String.class);
                        String gender = snapshot.child("gender").getValue(String.class);
                        String education = snapshot.child("education").getValue(String.class);
                        String hobby = snapshot.child("hobby").getValue(String.class);
                        String interest = snapshot.child("interest").getValue(String.class);
                        String subject = snapshot.child("subject").getValue(String.class);
                        String stream = snapshot.child("stream").getValue(String.class);

                        // Send user details along with the message to AI
                        sendMessageToAIWithDetails(messageText, age, gender, education, hobby, interest, subject, stream);
                    } else {
                        Toast.makeText(CareerPath.this, "User details not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CareerPath.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendMessageToAIWithDetails(String messageText, String age, String gender, String education, String hobby, String interest, String subject, String stream) {
        // Since the messageText is already checked before calling this method, no need to check again
        new SendMessageToAI().execute(messageText, age, gender,  education, hobby, interest, subject, stream);  // Pass additional details
    }




    private class SendMessageToAI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String messageText = strings[0];
            String age = strings[1];
            String gender = strings[2];
            String education = strings[3];
            String hobby = strings[4];
            String interest = strings[5];
            String subject = strings[6];
            String stream = strings[7];

            try {
                URL url = new URL("https://api.hyperleap.ai/conversations/" + CONVERSATION_ID + "/continue-sync");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("x-hl-api-key", API_KEY);
                connection.setDoOutput(true);

                Log.d("SendMessageToAI", "doInBackground: Opened connection to API.");

                // Construct JSON input
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("personaId", PERSONA_ID);
                jsonInput.put("message", messageText);
                jsonInput.put("age", age);
                jsonInput.put("gender", gender);
                jsonInput.put("education", education);
                jsonInput.put("hobby", hobby);
                jsonInput.put("interest", interest);
                jsonInput.put("subject", subject);
                jsonInput.put("stream", stream);


                Log.d("SendMessageToAI", "doInBackground: Sending JSON data to AI: " + jsonInput);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                Log.d("SendMessageToAI", "doInBackground: Response code from API: " + code);

                if (code == 200) {
                    Log.d("SendMessageToAI", "doInBackground: API responded successfully.");
                    try (Scanner scanner = new Scanner(connection.getInputStream())) {
                        scanner.useDelimiter("\\A");
                        return scanner.hasNext() ? scanner.next() : "";
                    }
                } else {
                    Log.e("SendMessageToAI", "doInBackground: Error response from API: " + code);
                    return "Error: " + code;
                }
            } catch (Exception e) {
                Log.e("SendMessageToAI", "doInBackground: Exception occurred - " + e.getMessage());
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.d("SendMessageToAI", "onPostExecute: Response from AI: " + response);

            // Remove typing indicator
            hideTypingIndicator();

            // Parse the JSON response to extract the assistant's message content
            String assistantMessage = parseAIResponse(response);

            Log.d("SendMessageToAI", "onPostExecute: Parsed AI message: " + assistantMessage);

            // Add AI response to message list and update adapter
            Message aiMessage = new Message(assistantMessage, false, new Date());
            messageList.add(aiMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            saveChats();

            if (assistantMessage.contains("Thank you for using Mentaura-AI. If you have any queries, contact us. All the best for your career journey!")) {
                promptUserWithDialog();
            }

            saveJobRecommendations(assistantMessage);
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
                Log.e("ParseAIResponse", "Error parsing AI response", e);
            }
            return "Sorry, I couldn't understand your request.";
        }


        private void promptUserWithDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(CareerPath.this);
            builder.setMessage("Do you want to continue to the Next Page?");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    navigateToMainActivity();
                }
            });
            builder.setNegativeButton("Wait!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }


        private void saveJobRecommendations(String assistantMessage) {
            Log.d("CareerPath", "saveJobRecommendations called with message: " + assistantMessage);

            String triggerPattern = "Thank you for your responses\\. Based on your answers, here are the top (\\d+) career options that align with your interests and strengths:(.*?)Thank you for using Mentaura-AI\\.";
            Pattern pattern = Pattern.compile(triggerPattern, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(assistantMessage);

            if (matcher.find()) {
                String recommendations = matcher.group(2).trim();
                String[] jobs = recommendations.split("\\d+\\. ");

                Log.d("CareerPath", "Recommendations: " + recommendations);
                Log.d("CareerPath", "Job Parts: " + Arrays.toString(jobs));

                // Remove any empty strings from the jobs array
                List<String> jobList = new ArrayList<>();
                for (String job : jobs) {
                    if (!job.trim().isEmpty()) {
                        jobList.add(job.split(":")[0].trim());
                    }
                }

                Log.d("CareerPath", "Filtered Jobs: " + jobList);

                // Call saveJobsToDatabase with the appropriate number of job recommendations
                if (jobList.size() > 0) {
                    saveJobsToDatabase(jobList);
                } else {
                    Log.e("CareerPath", "Failed to parse job recommendations. Job parts length: " + jobList.size());
                }
            } else {
                Log.e("CareerPath", "Assistant message does not match the expected pattern.");
            }
        }


        private void saveJobsToDatabase(List<String> jobList) {
            String email = mAuth.getCurrentUser().getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                Log.d("CareerPath", "User Email: " + email);
                Log.d("CareerPath", "User Email Key: " + emailKey);

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(emailKey);

                // Iterate through the job list and save each job to the database
                for (int i = 0; i < jobList.size(); i++) {
                    String jobKey = "aijobrecommended" + (i + 1);
                    String job = jobList.get(i);

                    userRef.child(jobKey).setValue(job)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("CareerPath", jobKey + " saved successfully.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("CareerPath", "Failed to save " + jobKey + ": " + e.getMessage());
                                }
                            });
                }
            } else {
                Log.e("CareerPath", "Failed to get user email.");
            }
        }
    }
}