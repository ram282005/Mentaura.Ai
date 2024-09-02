package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CourseDetailActivity extends AppCompatActivity {
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailActivity.this, CoursesActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String url = intent.getStringExtra("url");
        String locale = intent.getStringExtra("locale");
        String description = intent.getStringExtra("description");
        String headline = intent.getStringExtra("headline");
        double rating = intent.getDoubleExtra("rating", 0.0);
        int numReviews = intent.getIntExtra("numReviews", 0);
        int numQuizzes = intent.getIntExtra("numQuizzes", 0);
        int numLectures = intent.getIntExtra("numLectures", 0);
        int numCurriculumItems = intent.getIntExtra("numCurriculumItems", 0);
        String[] requirements = intent.getStringArrayExtra("requirements");
        String[] whatYouWillLearn = intent.getStringArrayExtra("whatYouWillLearn");
        String[] targetAudiences = intent.getStringArrayExtra("targetAudiences");
        int estimatedContentLength = intent.getIntExtra("estimatedContentLength", 0);
        String[] objectives = intent.getStringArrayExtra("objectives");
        boolean hasCertificate = intent.getBooleanExtra("hasCertificate", false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout courseDetailsLayout = findViewById(R.id.courseDetailsLayout);

        addCourseDetail(courseDetailsLayout, "Title:", title);
        addCourseDetail(courseDetailsLayout, "Price Detail:", price);
        addCourseDetail(courseDetailsLayout, "Locale:", locale);
        addCourseDetail(courseDetailsLayout, "Description:", description);
        addCourseDetail(courseDetailsLayout, "Headline:", headline);
        addCourseDetail(courseDetailsLayout, "Rating", String.valueOf(rating));
        addCourseDetail(courseDetailsLayout, "Number of Reviews:", String.valueOf(numReviews));
        addCourseDetail(courseDetailsLayout, "Number of Quizzes:", String.valueOf(numQuizzes));
        addCourseDetail(courseDetailsLayout, "Number of Lectures:", String.valueOf(numLectures));
        addCourseDetail(courseDetailsLayout, "Number of Curriculum Items:", String.valueOf(numCurriculumItems));
        addCourseDetail(courseDetailsLayout, "Requirements:", joinArray(requirements));
        addCourseDetail(courseDetailsLayout, "What You Will Learn:", joinArray(whatYouWillLearn));
        addCourseDetail(courseDetailsLayout, "Target Audiences:", joinArray(targetAudiences));
        addCourseDetail(courseDetailsLayout, "Estimated Content Length:", String.valueOf(estimatedContentLength));
        addCourseDetail(courseDetailsLayout, "Objectives:", joinArray(objectives));
        addCourseDetail(courseDetailsLayout, "Has Certificate:", hasCertificate ? "Yes" : "NO");


        // Set up register button to open the URL
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }

    private void addCourseDetail(LinearLayout layout, String heading, String content) {
        LinearLayout detailLayout = new LinearLayout(this);
        detailLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        detailLayout.setOrientation(LinearLayout.VERTICAL);
        detailLayout.setPadding(8, 8, 8, 8);

        // Create a TextView for the heading
        TextView headingTextView = new TextView(this);
        headingTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headingTextView.setText(heading);
        headingTextView.setTypeface(null, Typeface.BOLD); // Set text style to bold
        headingTextView.setPadding(0, 0, 0, 4);

        // Create a TextView for the content
        TextView contentTextView = new TextView(this);
        contentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Convert HTML to plain text
        Spanned plainText = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY); // For API 24 and above
        contentTextView.setText(plainText);
        contentTextView.setPadding(38, 0, 0, 0);

        // Check if heading and content can fit in one line
        if (heading.length() + plainText.length() < 50) { // Adjust the condition as needed
            detailLayout.setOrientation(LinearLayout.HORIZONTAL); // Use horizontal orientation if fits
            headingTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            contentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        }

        // Add the heading and content to the detail layout
        detailLayout.addView(headingTextView);
        detailLayout.addView(contentTextView);

        // Add the detail layout to the parent layout
        layout.addView(detailLayout);
    }

    // Helper method to join string arrays into a single string
    private String joinArray(String[] array) {
        if (array == null || array.length == 0) {
            return "No data";
        }
        StringBuilder sb = new StringBuilder();
        for (String item : array) {
            sb.append(item).append("\n");
        }
        return sb.toString().trim();
    }
}