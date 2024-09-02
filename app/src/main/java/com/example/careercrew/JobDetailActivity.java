package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JobDetailActivity extends AppCompatActivity {

    ImageView back;
    TextView job;
    private String applyUrl;
    private String companyUrl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        job = findViewById(R.id.job);
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobDetailActivity.this, jobportal.class);
                startActivity(intent);
            }
        });

        // Get the job details from the intent
        Intent intent = getIntent();
        String job1 = intent.getStringExtra("job1");
        String title = intent.getStringExtra("title");
        String companyName = intent.getStringExtra("companyName");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        applyUrl = intent.getStringExtra("applyUrl");
        companyUrl = intent.getStringExtra("companyUrl");
        String salary = intent.getStringExtra("salary");
        String publishedAt = intent.getStringExtra("publishedAt");
        String postedTime = intent.getStringExtra("postedTime");
        String applicationsCount = intent.getStringExtra("applicationsCount");
        String contractType = intent.getStringExtra("contractType");
        String experienceLevel = intent.getStringExtra("experienceLevel");
        String workType = intent.getStringExtra("workType");
        String sector = intent.getStringExtra("sector");

        job.setText(job1);

        // Layout to hold the job details
        LinearLayout jobDetailsLayout = findViewById(R.id.jobDetailsLayout);

        // Add job details programmatically
        addJobDetail(jobDetailsLayout, "Title:", title);
        addJobDetail(jobDetailsLayout, "Company:", companyName);
        addJobDetail(jobDetailsLayout, "Location:", location);
        addJobDetail(jobDetailsLayout, "Description:", description);
        addHyperlinkDetail(jobDetailsLayout, "Company:", companyUrl);
        addJobDetail(jobDetailsLayout, "Salary:", salary);
        addJobDetail(jobDetailsLayout, "Published At:", publishedAt);
        addJobDetail(jobDetailsLayout, "Posted Time:", postedTime);
        addJobDetail(jobDetailsLayout, "Applications Count:", applicationsCount);
        addJobDetail(jobDetailsLayout, "Contract Type:", contractType);
        addJobDetail(jobDetailsLayout, "Experience Level:", experienceLevel);
        addJobDetail(jobDetailsLayout, "Work Type:", workType);
        addJobDetail(jobDetailsLayout, "Sector:", sector);

        // Set up Apply Button
        Button applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(v -> openApplyUrl());
    }

    // Helper method to add job details
    private void addJobDetail(LinearLayout layout, String heading, String content) {
        // Create a container layout for the heading and content
        LinearLayout detailLayout = new LinearLayout(this);
        detailLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        detailLayout.setOrientation(LinearLayout.HORIZONTAL);
        detailLayout.setPadding(8, 8, 8, 8);

        // Create a TextView for the heading
        TextView headingTextView = new TextView(this);
        headingTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        headingTextView.setText(heading);
        headingTextView.setTypeface(null, Typeface.BOLD); // Set text style to bold

        // Create a TextView for the content
        TextView contentTextView = new TextView(this);
        contentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        contentTextView.setText(content);

        // Add the heading and content to the detail layout
        detailLayout.addView(headingTextView);
        detailLayout.addView(contentTextView);

        // Add the detail layout to the parent layout
        layout.addView(detailLayout);
    }

    // Helper method to add a hyperlink detail
    private void addHyperlinkDetail(LinearLayout layout, String heading, String url) {
        // Create a container layout for the heading and content
        LinearLayout detailLayout = new LinearLayout(this);
        detailLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        detailLayout.setOrientation(LinearLayout.HORIZONTAL);
        detailLayout.setPadding(8, 8, 8, 8);

        // Create a TextView for the heading
        TextView headingTextView = new TextView(this);
        headingTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        headingTextView.setText(heading);
        headingTextView.setTypeface(null, Typeface.BOLD); // Set text style to bold

        // Create a TextView for the hyperlink
        TextView linkTextView = new TextView(this);
        linkTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        // Use Html.fromHtml with Html.FROM_HTML_MODE_LEGACY
        Spanned linkText = Html.fromHtml("<a href=\"" + url + "\">Company</a>", Html.FROM_HTML_MODE_LEGACY);
        linkTextView.setText(linkText);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Make the link clickable

        // Add the heading and hyperlink to the detail layout
        detailLayout.addView(headingTextView);
        detailLayout.addView(linkTextView);

        // Add the detail layout to the parent layout
        layout.addView(detailLayout);
    }
    // Method to open the apply URL
    private void openApplyUrl() {
        if (applyUrl != null && !applyUrl.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(applyUrl));
            startActivity(browserIntent);
        }
    }
}