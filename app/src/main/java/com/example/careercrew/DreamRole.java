package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DreamRole extends AppCompatActivity {

    private EditText etDreamRole, etDreamCompany;
    private Button buttonSubmit;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView imageViewHome;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_role);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etDreamRole = findViewById(R.id.et_dream_role);
        etDreamCompany = findViewById(R.id.et_dream_company);
        buttonSubmit = findViewById(R.id.button);
        imageViewHome = findViewById(R.id.imageView1);
        progressBar = findViewById(R.id.progress_bar);

        buttonSubmit.setOnClickListener(v -> checkAndSubmitData());
        imageViewHome.setOnClickListener(v -> navigateToHomePage());
    }

    private void checkAndSubmitData() {
        String dreamRole = etDreamRole.getText().toString().trim();
        String dreamCompany = etDreamCompany.getText().toString().trim();

        if (TextUtils.isEmpty(dreamRole) || TextUtils.isEmpty(dreamCompany)) {
            showMessageDialog("Please enter both dream role and company", false);
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                DatabaseReference userRef = mDatabase.child("users").child(emailKey);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("dreamRole") && dataSnapshot.hasChild("dreamCompany")) {
                            showOverwriteDialog(dreamRole, dreamCompany, emailKey);
                        } else {
                            submitData(dreamRole, dreamCompany, emailKey);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showMessageDialog("Database error: " + databaseError.getMessage(), false);
                    }
                });
            } else {
                showMessageDialog("Error retrieving user email", false);
            }
        } else {
            showMessageDialog("User not logged in", false);
        }
    }

    private void showOverwriteDialog(String dreamRole, String dreamCompany, String emailKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There's already data for your dream role and company. Should we overwrite this data? This action is taken seriously by our app.")
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                positiveButton.setText("Confirm (" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                positiveButton.setText("Confirm");
                positiveButton.setEnabled(true);
            }
        }.start();

        positiveButton.setOnClickListener(v -> {
            submitData(dreamRole, dreamCompany, emailKey);
            dialog.dismiss();
        });
    }

    private void submitData(String dreamRole, String dreamCompany, String emailKey) {
        showProgressBar();
        mDatabase.child("users").child(emailKey).child("dreamRole").setValue(dreamRole);
        mDatabase.child("users").child(emailKey).child("dreamCompany").setValue(dreamCompany);
        mDatabase.child("users").child(emailKey).child("lastActivity").setValue("MainActivity")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        triggerHyperleapPrompt();
                    } else {
                        hideProgressBar();
                        showMessageDialog("Failed to submit data", false);
                    }
                });
    }

    private void triggerHyperleapPrompt() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"promptId\": \"2dae56f3-2967-48fb-b7ca-efc454c8103b\",\n    \"promptVersionId\": \"73ad2dbf-8438-4146-9a15-216f0c6073c3\"\n}");
        Request request = new Request.Builder()
                .url("https://api.hyperleap.ai/prompts")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-hl-api-key", "YzhlMGM1MzAyY2YwNDU3MDk2NmRiNDI0OWM5MTI4NTE=")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    hideProgressBar();
                    showMessageDialog("Failed to trigger AI prompt: " + e.getMessage(), false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    hideProgressBar();
                    if (!response.isSuccessful()) {
                        showMessageDialog("Failed to trigger AI prompt: " + response.message(), false);
                    } else {
                        showMessageDialog("AI prompt triggered successfully. Please visit our website to take the exam.", true);
                    }
                });
            }
        });
    }

    private void showMessageDialog(String message, boolean isSuccessful) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {
                    if (isSuccessful) {
                        checkExamScoreAndNavigate();
                    }
                })
                .setNegativeButton("Exit", (dialog, id) -> finishAffinity());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkExamScoreAndNavigate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");
                DatabaseReference scoreRef = mDatabase.child("users").child(emailKey).child("examScore");
                scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            navigateToMainactivity();
                        } else {
                            showMessageDialog("Please go to our website to write the exam at your suitable and wanted time.", false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showMessageDialog("Database error: " + databaseError.getMessage(), false);
                    }
                });
            } else {
                showMessageDialog("Error retrieving user email", false);
            }
        } else {
            showMessageDialog("User not logged in", false);
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(DreamRole.this, HomePage.class);
        startActivity(intent);
    }

    private void navigateToMainactivity() {
        Intent intent = new Intent(DreamRole.this, MainActivity.class);
        intent.putExtra("DREAM_ROLE", etDreamRole.getText().toString().trim());
        intent.putExtra("DREAM_COMPANY", etDreamCompany.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


}
