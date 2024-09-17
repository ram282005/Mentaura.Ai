package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private ImageView back, google;
    private boolean passwordVisible = false;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewForgotPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.login_email);
        editTextPassword = findViewById(R.id.login_password);
        back = findViewById(R.id.login_back);
        google = findViewById(R.id.login_google_sign_up);
        buttonLogin = findViewById(R.id.login_login);
        progressBar = findViewById(R.id.progressBar);
        textViewForgotPassword = findViewById(R.id.login_forgot_password);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Login.this, EntryPage.class);
                startActivity(intent1);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googlesignin();
            }
        });

        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2;  // For 'drawableEnd'
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                        if (passwordVisible) {
                            // Hide password
                            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(), R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.visibilityoff), null);
                        } else {
                            // Show password
                            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(), R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.removepasswordhide), null);
                        }
                        passwordVisible = !passwordVisible;
                        editTextPassword.setSelection(editTextPassword.getText().length());  // Move cursor to the end
                        return true;
                    }
                }
                return false;
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action required before text changes
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check if email format is correct
                if (isValidEmail(charSequence.toString())) {
                    // If email is valid, set the drawable to 'verified'
                    setDrawableEnd(R.drawable.baseline_email_24, R.drawable.verified);
                } else {
                    // If email is not valid, set the drawable to 'unverified'
                    setDrawableEnd(R.drawable.baseline_email_24, R.drawable.verified_right);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // No action required after text changes
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    loginUser(email, password);
                } else if (!isValidEmail(email)) {
                    // Show toast if email is not in the correct format
                    Toast.makeText(Login.this, "Wrong format email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    resetPassword(email);
                } else {
                    Toast.makeText(Login.this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to set the drawable end image of the EditText
    private void setDrawableEnd(int drawableRes1, int drawableRes) {
        Drawable drawable1 = ContextCompat.getDrawable(this, drawableRes1);
        Drawable drawable = ContextCompat.getDrawable(this, drawableRes);
        editTextEmail.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, drawable, null);
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
                            boolean isPremium = dataSnapshot.child("isPremium").getValue(Boolean.class);
                            if (isPremium) {
                                // User is premium, navigate to MainActivity
                                Log.d("Login", "Premium user, navigating to MainActivity");
                                navigateToActivity(MainActivity.class);
                            } else {
                                // User is free, navigate to HomePage
                                Log.d("Login", "Free user, navigating to HomePage");
                                navigateToActivity(HomePage.class);
                            }
                        } else {
                            checkLastActivity(emailKey);
                        }
                    } else {
                        // Registration is incomplete, check for last activity
                        Log.d("Login", "Registration is incomplete, checking last activity");
                        checkLastActivity(emailKey);
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

    private void checkLastActivity(String emailKey) {
        DatabaseReference lastActivityRef = databaseReference.child("users").child(emailKey).child("lastActivity");
        lastActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastActivity = dataSnapshot.getValue(String.class);
                    if (lastActivity != null && !lastActivity.isEmpty()) {
                        try {
                            Class<?> activityClass = Class.forName("com.example.careercrew." + lastActivity);
                            Log.d("Login", "Navigating to last activity: " + lastActivity);
                            Intent intent = new Intent(Login.this, activityClass);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
                            startActivity(intent);
                            finish();
                        } catch (ClassNotFoundException e) {
                            Log.e("Login", "ClassNotFoundException: " + e.getMessage());
                            navigateToActivity(HomePage.class);
                        }
                    } else {
                        Log.d("Login", "No last activity found, navigating to HomePage");
                        navigateToActivity(HomePage.class);
                    }
                } else {
                    Log.d("Login", "No last activity data found, navigating to HomePage");
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


    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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

    private void googlesignin() {

    }
}