package com.example.careercrew;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SubscriptionActivity extends AppCompatActivity {

    private Button buttonMonthly;
    private Button buttonYearly;
    private LinearLayout subscriptionDetails;
    private TextView subscriptionInfo;
    private ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        buttonMonthly = findViewById(R.id.button_monthly);
        buttonYearly = findViewById(R.id.button_yearly);
        subscriptionDetails = findViewById(R.id.subscription_details);
        subscriptionInfo = findViewById(R.id.subscription_info);
        imageViewBack = findViewById(R.id.imageView);

        buttonMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubscriptionDetails("Monthly Subscription:\n- Feature 1\n- Feature 2\n- Feature 3");
            }
        });

        buttonYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubscriptionDetails("Yearly Subscription:\n- Feature 1\n- Feature 2\n- Feature 3\n- Feature 4");
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
    }

    private void showSubscriptionDetails(String details) {
        subscriptionDetails.setVisibility(View.VISIBLE);
        subscriptionInfo.setText(details);
    }

    private void navigateBack() {
        finish(); // This will close the current activity and navigate back to the previous one
    }
}
