package com.example.careercrew;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {

    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        text = findViewById(R.id.textView);
        String data = getIntent().getStringExtra("data");
        text.setText(data);
    }
}