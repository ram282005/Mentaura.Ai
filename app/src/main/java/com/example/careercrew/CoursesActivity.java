package com.example.careercrew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class CoursesActivity extends AppCompatActivity {

    ImageView back;
    private LinearLayout coursesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // Get reference to the LinearLayout that will contain the course items
        coursesContainer = findViewById(R.id.courses_container);

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Load courses from JSON file
        loadCoursesFromJson();
    }

    private void loadCoursesFromJson() {
        try {
            InputStream inputStream = getAssets().open("courses.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            // Parse JSON data
            JSONArray coursesArray = new JSONArray(json);

            for (int i = 0; i < coursesArray.length(); i++) {
                JSONObject courseObject = coursesArray.getJSONObject(i);

                // Retrieve required fields from JSON
                String title = courseObject.getString("title");
                String url = courseObject.getString("url");
                JSONObject priceDetail = courseObject.getJSONObject("price_detail");
                String priceString = priceDetail.getString("price_string");
                String locale = courseObject.getJSONObject("locale").getString("title");
                String description = courseObject.getString("description");
                String headline = courseObject.getString("headline");
                double rating = courseObject.getDouble("rating");
                int numReviews = courseObject.getInt("num_reviews");
                int numQuizzes = courseObject.getInt("num_quizzes");
                int numLectures = courseObject.getInt("num_lectures");
                int numCurriculumItems = courseObject.getInt("num_curriculum_items");
                String image = courseObject.getString("image");
                JSONArray requirementsArray = courseObject.getJSONArray("requirements_data");
                JSONArray whatYouWillLearnArray = courseObject.getJSONArray("what_you_will_learn_data");
                JSONArray targetAudiencesArray = courseObject.getJSONArray("target_audiences");
                int estimatedContentLength = courseObject.getInt("estimated_content_length");
                JSONArray objectivesArray = courseObject.getJSONArray("objectives");
                boolean hasCertificate = courseObject.getBoolean("has_certificate");

                // Convert JSON arrays to string arrays for ease of use
                String[] requirements = new String[requirementsArray.length()];
                for (int j = 0; j < requirementsArray.length(); j++) {
                    requirements[j] = requirementsArray.getString(j);
                }

                String[] whatYouWillLearn = new String[whatYouWillLearnArray.length()];
                for (int j = 0; j < whatYouWillLearnArray.length(); j++) {
                    whatYouWillLearn[j] = whatYouWillLearnArray.getString(j);
                }

                String[] targetAudiences = new String[targetAudiencesArray.length()];
                for (int j = 0; j < targetAudiencesArray.length(); j++) {
                    targetAudiences[j] = targetAudiencesArray.getString(j);
                }

                String[] objectives = new String[objectivesArray.length()];
                for (int j = 0; j < objectivesArray.length(); j++) {
                    objectives[j] = objectivesArray.getString(j);
                }

                // Inflate course item layout and set data
                View courseView = LayoutInflater.from(this).inflate(R.layout.course_item, null);
                TextView courseTextView = courseView.findViewById(R.id.courseTextView);
                courseTextView.setText(title + "\n" + "Price: " + priceString + "\n" + "Number of hours: " + String.valueOf(estimatedContentLength/60) + "\n" + "Rating: " + String.valueOf(rating));
                courseView.setTag(R.id.courseTextView, new CourseDetails(title, priceString, url, locale, description, headline, rating, numReviews, numQuizzes, numLectures, numCurriculumItems, image, requirements, whatYouWillLearn, targetAudiences, estimatedContentLength, objectives, hasCertificate));


                // Set layout parameters with margin
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 24);
                courseView.setLayoutParams(layoutParams);

                // Set click listener to open CourseDetailActivity
                courseView.setOnClickListener(v -> {
                    CourseDetails courseDetails = (CourseDetails) v.getTag(R.id.courseTextView);
                    Intent intent = new Intent(CoursesActivity.this, CourseDetailActivity.class);

                    intent.putExtra("title", courseDetails.getTitle());
                    intent.putExtra("price", courseDetails.getPrice());
                    intent.putExtra("url", courseDetails.getUrl());
                    intent.putExtra("locale", courseDetails.getLocale());
                    intent.putExtra("description", courseDetails.getDescription());
                    intent.putExtra("headline", courseDetails.getHeadline());
                    intent.putExtra("rating", courseDetails.getRating());
                    intent.putExtra("numReviews", courseDetails.getNumReviews());
                    intent.putExtra("numQuizzes", courseDetails.getNumQuizzes());
                    intent.putExtra("numLectures", courseDetails.getNumLectures());
                    intent.putExtra("numCurriculumItems", courseDetails.getNumCurriculumItems());
                    intent.putExtra("image", courseDetails.getImage());
                    intent.putExtra("requirements", courseDetails.getRequirements());
                    intent.putExtra("whatYouWillLearn", courseDetails.getWhatYouWillLearn());
                    intent.putExtra("targetAudiences", courseDetails.getTargetAudiences());
                    intent.putExtra("estimatedContentLength", courseDetails.getEstimatedContentLength());
                    intent.putExtra("objectives", courseDetails.getObjectives());
                    intent.putExtra("hasCertificate", courseDetails.isHasCertificate());

                    startActivity(intent);
                });


                // Add the course view to the container
                coursesContainer.addView(courseView);
            }

        } catch (Exception e) {
            Log.e("CoursesActivity", "Error loading courses from JSON", e);
        }
    }

    public static class CourseDetails {
        // All the fields (as defined previously)
        private final String title;
        private final String price;
        private final String url;
        private final String locale;
        private final String description;
        private final String headline;
        private final double rating;
        private final int numReviews;
        private final int numQuizzes;
        private final int numLectures;
        private final int numCurriculumItems;
        private final String image;
        private final String[] requirements;
        private final String[] whatYouWillLearn;
        private final String[] targetAudiences;
        private final int estimatedContentLength;
        private final String[] objectives;
        private final boolean hasCertificate;

        // Constructor (as defined previously)
        public CourseDetails(
                String title, String price, String url, String locale, String description,
                String headline, double rating, int numReviews, int numQuizzes, int numLectures,
                int numCurriculumItems, String image, String[] requirements, String[] whatYouWillLearn,
                String[] targetAudiences, int estimatedContentLength, String[] objectives, boolean hasCertificate) {
            this.title = title;
            this.price = price;
            this.url = url;
            this.locale = locale;
            this.description = description;
            this.headline = headline;
            this.rating = rating;
            this.numReviews = numReviews;
            this.numQuizzes = numQuizzes;
            this.numLectures = numLectures;
            this.numCurriculumItems = numCurriculumItems;
            this.image = image;
            this.requirements = requirements;
            this.whatYouWillLearn = whatYouWillLearn;
            this.targetAudiences = targetAudiences;
            this.estimatedContentLength = estimatedContentLength;
            this.objectives = objectives;
            this.hasCertificate = hasCertificate;
        }

        // Getters for all fields
        public String getTitle() {
            return title;
        }

        public String getPrice() {
            return price;
        }

        public String getUrl() {
            return url;
        }

        public String getLocale() {
            return locale;
        }

        public String getDescription() {
            return description;
        }

        public String getHeadline() {
            return headline;
        }

        public double getRating() {
            return rating;
        }

        public int getNumReviews() {
            return numReviews;
        }

        public int getNumQuizzes() {
            return numQuizzes;
        }

        public int getNumLectures() {
            return numLectures;
        }

        public int getNumCurriculumItems() {
            return numCurriculumItems;
        }

        public String getImage() {
            return image;
        }

        public String[] getRequirements() {
            return requirements;
        }

        public String[] getWhatYouWillLearn() {
            return whatYouWillLearn;
        }

        public String[] getTargetAudiences() {
            return targetAudiences;
        }

        public int getEstimatedContentLength() {
            return estimatedContentLength;
        }

        public String[] getObjectives() {
            return objectives;
        }

        public boolean isHasCertificate() {
            return hasCertificate;
        }
    }
}