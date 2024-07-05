package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.editTextText);
        editTextPassword = findViewById(R.id.editTextText2);
        buttonLogin = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    loginUser(email, password);
                } else {
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar

                        if (task.isSuccessful()) {
                            // Sign in success, check user's data
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d("Login", "User logged in: " + user.getEmail());
                                checkUserDataCompletion(user.getEmail().replace(".", ","));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("Login", "Login failed: " + task.getException().getMessage());
                            Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserDataCompletion(String emailKey) {
        DatabaseReference userRef = databaseReference.child("users").child(emailKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean ageExists = dataSnapshot.hasChild("age");
                    boolean genderExists = dataSnapshot.hasChild("gender");
                    boolean careerFocusExists = dataSnapshot.hasChild("careerFocus");
                    boolean dreamRoleExists = dataSnapshot.hasChild("dreamRole");
                    boolean dreamCompanyExists = dataSnapshot.hasChild("dreamCompany");

                    if (ageExists && genderExists && careerFocusExists) {
                        if (dreamRoleExists && dreamCompanyExists) {
                            // All details including dreamRole and dreamCompany are present
                            Log.d("Login", "All details are present, navigating to CourseRecommendationActivity");
                            navigateToActivity(CourseRecommendationActivity.class);
                        } else {
                            checkLastChoice(emailKey);
                        }
                    } else {
                        // Registration is incomplete
                        Log.d("Login", "Registration is incomplete, navigating to Register");
                        navigateToActivity(Register.class);
                    }
                } else {
                    // No user data found, go to Register page
                    Log.d("Login", "No user data found, navigating to Register");
                    navigateToActivity(Register.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Login", "Database error: " + databaseError.getMessage());
                // Error fetching data, go to EntryPage
                navigateToActivity(EntryPage.class);
            }
        });
    }

    private void checkLastChoice(String emailKey) {
        DatabaseReference lastChoiceRef = databaseReference.child("users").child(emailKey).child("lastChoice");
        lastChoiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastChoice = dataSnapshot.getValue(String.class);
                    Log.d("Login", "Navigating to LastChoiceHandler with lastChoice: " + lastChoice);
                    Intent intent = new Intent(Login.this, LastChoiceHandler.class);
                    intent.putExtra("lastChoice", lastChoice);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("Login", "No last choice found, navigating to HomePage");
                    navigateToActivity(HomePage.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Login", "Database error: " + databaseError.getMessage());
                navigateToActivity(HomePage.class);
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Log.d("Login", "Navigating to activity: " + targetActivity.getSimpleName());
        Intent intent = new Intent(Login.this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish();
    }
}
