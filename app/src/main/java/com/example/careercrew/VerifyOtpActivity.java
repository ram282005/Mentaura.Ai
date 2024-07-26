package com.example.careercrew;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private TextView resendOtp;
    private Button verifyButton;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otp1 = findViewById(R.id.forOTP1);
        otp2 = findViewById(R.id.forOTP2);
        otp3 = findViewById(R.id.forOTP3);
        otp4 = findViewById(R.id.forOTP4);
        otp5 = findViewById(R.id.forOTP5);
        otp6 = findViewById(R.id.forOTP6);
        resendOtp = findViewById(R.id.resend);
        verifyButton = findViewById(R.id.verify);

        mAuth = FirebaseAuth.getInstance();

        // Get the verification ID from intent extras
        if (getIntent().hasExtra("verificationId")) {
            verificationId = getIntent().getStringExtra("verificationId");
        } else {
            Toast.makeText(this, "Verification ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Automatically move to next EditText and handle paste event
        otp1.addTextChangedListener(new GenericTextWatcher(otp1));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4));
        otp5.addTextChangedListener(new GenericTextWatcher(otp5));
        otp6.addTextChangedListener(new GenericTextWatcher(otp6));

        verifyButton.setOnClickListener(v -> verifyOtp());
        resendOtp.setOnClickListener(v -> resendOtp());
    }

    private void verifyOtp() {
        String otp = otp1.getText().toString() + otp2.getText().toString() +
                otp3.getText().toString() + otp4.getText().toString() +
                otp5.getText().toString() + otp6.getText().toString();

        if (otp.length() == 6) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign-in success
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to the next screen or main activity
                            // Example: startActivity(new Intent(this, MainActivity.class));
                            finish(); // Close this activity
                        } else {
                            // Sign-in failed
                            Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void resendOtp() {
        // Your logic to resend OTP
        // Use the phone number and resendingToken to resend OTP
        Toast.makeText(this, "OTP Resent", Toast.LENGTH_SHORT).show();
    }

    private class GenericTextWatcher implements TextWatcher {

        private final EditText currentView;

        private GenericTextWatcher(EditText currentView) {
            this.currentView = currentView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();

            // Check if the user pasted a complete OTP
            if (text.length() == 6) {
                otp1.setText(String.valueOf(text.charAt(0)));
                otp2.setText(String.valueOf(text.charAt(1)));
                otp3.setText(String.valueOf(text.charAt(2)));
                otp4.setText(String.valueOf(text.charAt(3)));
                otp5.setText(String.valueOf(text.charAt(4)));
                otp6.setText(String.valueOf(text.charAt(5)));
            } else if (text.length() == 1) {
                // Move focus to next EditText
                if (currentView.getId() == R.id.forOTP1) {
                    otp2.requestFocus();
                } else if (currentView.getId() == R.id.forOTP2) {
                    otp3.requestFocus();
                } else if (currentView.getId() == R.id.forOTP3) {
                    otp4.requestFocus();
                } else if (currentView.getId() == R.id.forOTP4) {
                    otp5.requestFocus();
                } else if (currentView.getId() == R.id.forOTP5) {
                    otp6.requestFocus();
                }
            }
        }
    }
}