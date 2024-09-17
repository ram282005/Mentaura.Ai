package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private ProgressBar progressBar;
    private ImageView back;
    private boolean passwordVisible = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Handler handler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nameEditText = findViewById(R.id.signup_name);
        emailEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        signUpButton = findViewById(R.id.signup_signup);
        progressBar = findViewById(R.id.signup_progressBar);
        back = findViewById(R.id.signup_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_back = new Intent(SignUp.this, EntryPage.class);
                startActivity(intent_back);
            }
        });

        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2;  // For 'drawableEnd'
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                        if (passwordVisible) {
                            // Hide password
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(), R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.visibilityoff), null);
                        } else {
                            // Show password
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(), R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.removepasswordhide), null);
                        }
                        passwordVisible = !passwordVisible;
                        passwordEditText.setSelection(passwordEditText.getText().length());  // Move cursor to the end
                        return true;
                    }
                }
                return false;
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
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

        handler = new Handler();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String pwd = passwordEditText.getText().toString().trim();
                final String name = nameEditText.getText().toString().trim();

                if (!email.isEmpty() && !pwd.isEmpty() && !name.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                    registerUser(email, pwd, name);
                } else if (!isValidEmail(email)) {
                    // Show toast if email is not in the correct format
                    Toast.makeText(SignUp.this, "Wrong format email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, drawable, null);
    }

    private void registerUser(String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar

                        if (task.isSuccessful()) {
                            // Sign in success, send verification email
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUp.this, "Registration successful! Please check your email for verification.", Toast.LENGTH_SHORT).show();

                                                    // Save user details to Firebase Realtime Database
                                                    String emailKey = email.replace(".", ",");
                                                    mDatabase.child("users").child(emailKey).child("name").setValue(name);
                                                    mDatabase.child("users").child(emailKey).child("password").setValue(password);

                                                    // Open Gmail app
                                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    try {
                                                        startActivity(intent);
                                                    } catch (android.content.ActivityNotFoundException ex) {
                                                        Toast.makeText(SignUp.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                                                    }

                                                    // Start a runnable to check email verification status
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            checkEmailVerification(user);
                                                        }
                                                    }, 2000);
                                                } else {
                                                    Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkEmailVerification(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar while checking verification
        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE); // Hide the progress bar

                if (user.isEmailVerified()) {
                    // Email is verified, navigate to Register activity
                    Intent intent = new Intent(SignUp.this, HomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Email not verified, check again after some time
                    Toast.makeText(SignUp.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkEmailVerification(user);
                        }
                    }, 2000);
                }
            }
        });
    }
}