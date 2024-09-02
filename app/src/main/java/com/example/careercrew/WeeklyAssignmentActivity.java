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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class WeeklyAssignmentActivity extends AppCompatActivity {

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

    private int currentQuestionIndex = 0;
    private int totalScore = 0;

    private final String[] questions = {
            "Question 1: Basic Level\nQ1: What is the first step in critical thinking?\n\nA) Making a decision\nB) Gathering information\nC) Ignoring other perspectives\nD) Jumping to conclusions",
            "Question 2: Intermediate Level\nQ2: Which of the following best describes a logical fallacy?\n\nA) A statement that contradicts itself\nB) A conclusion based on emotional reasoning\nC) A flaw in reasoning that weakens an argument\nD) An argument that is strongly supported by evidence",
            "Question 3: Intermediate Level\nQ3: When analyzing a complex problem, which of the following is the most effective strategy?\n\nA) Solving the problem based on intuition\nB) Breaking the problem into smaller, manageable parts\nC) Avoiding the problem altogether\nD) Relying solely on others’ opinions",
            "Question 4: Advanced Level\nQ4: Which critical thinking technique involves considering multiple perspectives and alternative solutions before making a decision?\n\nA) Bias confirmation\nB) Divergent thinking\nC) Linear thinking\nD) Cognitive dissonance",
            "Question 5: Advanced Level\nQ5: In critical thinking, what is the significance of evaluating the credibility of sources before forming a conclusion?\n\nA) It ensures that conclusions are aligned with personal beliefs.\nB) It reduces the need for further analysis.\nC) It helps avoid bias and ensures conclusions are based on reliable information.\nD) It speeds up the decision-making process."
    };

    private final String[] correctAnswers = {"B", "C", "B", "B", "C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_assignment);
        messageList1 = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
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

        sendButton.setOnClickListener((v) -> {
            String userMessage = messageEditText.getText().toString().trim();
            addToChat(userMessage, Message1.SENT_BY_ME);
            messageEditText.setText(null);
            handleUserMessage(userMessage);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(WeeklyAssignmentActivity.this, CommunitiesActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserCommunity() {
        // Get the current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Replace periods in the email to match Firebase key format
            String userEmailKey = currentUser.getEmail().replace(".", ",");
            DatabaseReference userRef = mDatabase.child("users").child(userEmailKey);

            // Fetch the community name
            userRef.child("joined_community").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String communityName = snapshot.getValue(String.class);
                        if (communityName != null) {
                            // Send welcome message
                            String welcomeMessage = "Hello, welcome to the \"" + communityName + "\" Daily Assignment Section. Please complete today's daily assignment at your earliest convenience. Your prompt attention to this task is greatly appreciated. Please send 'Start' to start the assignment. Thank you.";
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
        userMessage = userMessage.trim().toUpperCase();

        if (userMessage.equals("START")) {
            currentQuestionIndex = 0;
            totalScore = 0;
            sendNextQuestion();
        } else if (currentQuestionIndex > 0 && currentQuestionIndex <= questions.length) {
            evaluateAnswer(userMessage);
            sendNextQuestion();
        } else {
            addToChat("Sorry, I don't understand that. Please type 'Start' to begin the assignment.", Message1.SENT_BY_BOT);
        }
    }

    private void sendNextQuestion() {
        if (currentQuestionIndex < questions.length) {
            addToChat(questions[currentQuestionIndex], Message1.SENT_BY_BOT);
            currentQuestionIndex++;
        } else {
            // All questions have been answered
            addToChat("You've completed the assignment! Your score is: " + totalScore + "/" + questions.length, Message1.SENT_BY_BOT);
            updateScoreInDatabase(totalScore);
        }
    }

    private void evaluateAnswer(String userAnswer) {
        if (userAnswer.equals(correctAnswers[currentQuestionIndex - 1])) {
            totalScore++;
            addToChat("Correct!", Message1.SENT_BY_BOT);
        } else {
            addToChat("Incorrect. The correct answer was: " + correctAnswers[currentQuestionIndex - 1], Message1.SENT_BY_BOT);
        }
    }

    private void updateScoreInDatabase(int score) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmailKey = currentUser.getEmail().replace(".", ",");
            DatabaseReference userRef = mDatabase.child("users").child(userEmailKey);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long dailyAssignments = 0;
                    if (snapshot.hasChild("DailyAssignments")) {
                        dailyAssignments = snapshot.child("DailyAssignments").getValue(Long.class);
                    }

                    dailyAssignments++;

                    if (dailyAssignments <= 5) {
                        String scoreKey = "DailyAssignmentScore" + dailyAssignments;
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(scoreKey, score);
                        updates.put("DailyAssignments", dailyAssignments);

                        userRef.updateChildren(updates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "Score and completion count updated successfully");
                            } else {
                                Log.e("Firebase", "Failed to update score and completion count: " + task.getException().getMessage());
                            }
                        });
                    } else {
                        Log.e("Firebase", "All 5 daily assignments for the week have already been completed.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to fetch user data: " + error.getMessage());
                }
            });
        }
    }
}
