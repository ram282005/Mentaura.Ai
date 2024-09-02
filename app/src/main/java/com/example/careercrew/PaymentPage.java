package com.example.careercrew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentPage extends AppCompatActivity {

    private Spinner monthSpinner;
    private TextView totalCostTextView;
    private TextView costWithTaxesTextView;
    private Button paymentButton;
    private ProgressBar progressBar;
    private ImageView imageViewBack;

    private final int premiumCost = 399;
    private final double gstRate = 0.18; // 18% GST

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        Log.d("PaymentPage", "onCreate called");

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("subscriptions");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        monthSpinner = findViewById(R.id.monthSpinner);
        totalCostTextView = findViewById(R.id.totalCostTextView);
        costWithTaxesTextView = findViewById(R.id.costWithTaxesTextView);
        paymentButton = findViewById(R.id.paymentbutton);
        progressBar = findViewById(R.id.progressBar);
        imageViewBack = findViewById(R.id.imageView);

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.month_options, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTotalCost();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSubscriptionDetails();
            }
        });
    }

    private void calculateTotalCost() {
        int selectedMonths = Integer.parseInt(monthSpinner.getSelectedItem().toString());
        int totalCost = premiumCost * selectedMonths;
        double totalCostWithGST = totalCost + (totalCost * gstRate);

        totalCostTextView.setText("Total Cost: " + totalCost);
        costWithTaxesTextView.setText("Cost: " + totalCost + " + GST/Taxes: " + String.format("%.2f", totalCostWithGST));
    }

    private void saveSubscriptionDetails() {
        int selectedMonths = Integer.parseInt(monthSpinner.getSelectedItem().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentPage.this);
        builder.setTitle("Updating Subscription Details");
        builder.setMessage("Please wait...");
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Save to Firebase
        if (currentUser != null) {
            String email = currentUser.getEmail().replace(".", ",");
            Subscription subscription = new Subscription("Premium", selectedMonths, true);
            databaseReference.child(email).setValue(subscription).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    Log.d("PaymentPage", "Subscription details saved successfully");

                    // Redirect to MainActivity
                    Intent intent = new Intent(PaymentPage.this, MainActivity.class);
                    intent.putExtra("fromPaymentPage", true);
                    startActivity(intent);
                    Log.d("PaymentPage", "Navigating to MainActivity");
                    finish();
                } else {
                    Log.e("PaymentPage", "Failed to save subscription details", task.getException());

                    // Handle failure
                    AlertDialog.Builder failureBuilder = new AlertDialog.Builder(PaymentPage.this);
                    failureBuilder.setTitle("Error");
                    failureBuilder.setMessage("Failed to update subscription details. Please try again.");
                    failureBuilder.setPositiveButton("OK", null);
                    failureBuilder.show();
                }
            });
        } else {
            dialog.dismiss();
            // Handle the case where user is not logged in
            AlertDialog.Builder builderNotLoggedIn = new AlertDialog.Builder(PaymentPage.this);
            builderNotLoggedIn.setTitle("Error");
            builderNotLoggedIn.setMessage("User not logged in. Please log in and try again.");
            builderNotLoggedIn.setPositiveButton("OK", null);
            builderNotLoggedIn.show();
        }
    }

    // Subscription class to represent subscription details
    public static class Subscription {
        public String model;
        public int months;
        public boolean premium;

        public Subscription() {
            // Default constructor required for calls to DataSnapshot.getValue(Subscription.class)
        }

        public Subscription(String model, int months, boolean premium) {
            this.model = model;
            this.months = months;
            this.premium = premium;
        }
    }
}
