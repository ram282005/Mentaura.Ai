package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class jobportal extends AppCompatActivity {

    ImageView back;
    private LinearLayout fullStackSection, hr, marketresearch, softwareDeveloperSection, product, event, graphic, architecture, datamanagement, socialmedia, content, industrial, administration, mba, service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobportal);

        // Get references to the sections in the layout
        fullStackSection = findViewById(R.id.full_stack_developer_section);
        hr = findViewById(R.id.hr_section);
        marketresearch = findViewById(R.id.market_research_section);
        softwareDeveloperSection = findViewById(R.id.software_developer_engineer_section);
        product = findViewById(R.id.product_logistics_analyst_section);
        event = findViewById(R.id.event_management_section);
        graphic = findViewById(R.id.graphic_design_section);
        architecture = findViewById(R.id.architecture_section);
        datamanagement = findViewById(R.id.data_analyst_management_section);
        socialmedia = findViewById(R.id. social_media_marketing_section);
        content = findViewById(R.id.content_creator_section);
        industrial = findViewById(R.id.industrial_section);
        administration = findViewById(R.id.administration_section);
        mba = findViewById(R.id.mba_section);
        service = findViewById(R.id.service_section);

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(jobportal.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Load jobs from JSON
        loadJobsFromJson();
    }

    private void loadJobsFromJson() {
        try {
            InputStream inputStream = getAssets().open("jobs.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jobsArray = new JSONArray(json);

            Map<String, LinearLayout> sectionsMap = new HashMap<>();
            sectionsMap.put("Full Stack Developer", fullStackSection);
            sectionsMap.put("Human Resources (HR)", hr);
            sectionsMap.put("Market Research", marketresearch);
            sectionsMap.put("Software Developer Engineer", softwareDeveloperSection);
            sectionsMap.put("Product and Logistics Analyst", product);
            sectionsMap.put("Event Management", event);
            sectionsMap.put("Graphic Design", graphic);
            sectionsMap.put("Architecture", architecture);
            sectionsMap.put("Data Analyst and Management", datamanagement);
            sectionsMap.put("Social Media Marketing", socialmedia);
            sectionsMap.put("Content", content);
            sectionsMap.put("Industrial", industrial);
            sectionsMap.put("Administration", administration);
            sectionsMap.put("MBA", mba);
            sectionsMap.put("Service", service);

            for (int i = 0; i < jobsArray.length(); i++) {
                JSONObject jobObject = jobsArray.getJSONObject(i);

                String title = jobObject.getString("title");
                String company = jobObject.getString("companyName");
                String experience = jobObject.getString("experienceLevel");
                String location = jobObject.getString("location");
                String description = jobObject.getString("description");
                String applyUrl = jobObject.getString("applyUrl");
                String companyUrl = jobObject.getString("companyUrl");
                String salary = jobObject.getString("salary");
                String publishedAt = jobObject.getString("publishedAt");
                String postedTime = jobObject.getString("postedTime");
                String applicationsCount = jobObject.getString("applicationsCount");
                String contractType = jobObject.getString("contractType");
                String workType = jobObject.getString("workType");
                String sector = jobObject.getString("sector");

                Pair<String, LinearLayout> bestSectionPair = findBestMatchingSection(title, sectionsMap);
                String bestSectionName = bestSectionPair.first;
                LinearLayout bestSection = bestSectionPair.second;

                View jobView = LayoutInflater.from(this).inflate(R.layout.job_item, null);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView jobTextView = jobView.findViewById(R.id.jobTextView);
                jobTextView.setText(title + "\n" + company + "\n" + "Location: " + location + "\n" + "Published at: " + publishedAt);
                jobView.setTag(R.id.jobTextView, new JobDetails(title, company, location, description, applyUrl, companyUrl, salary, publishedAt, postedTime, applicationsCount, contractType, experience, workType, sector));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0,0,24,0);
                jobTextView.setLayoutParams(layoutParams);

                // Set click listener to open JobDetailActivity
                jobView.setOnClickListener(v -> {
                    JobDetails jobDetails = (JobDetails) v.getTag(R.id.jobTextView);
                    Intent intent = new Intent(jobportal.this, JobDetailActivity.class);
                    intent.putExtra("job1", bestSectionName);
                    intent.putExtra("title", jobDetails.getTitle());
                    intent.putExtra("companyName", jobDetails.getCompanyName());
                    intent.putExtra("location", jobDetails.getLocation());
                    intent.putExtra("description", jobDetails.getDescription());
                    intent.putExtra("applyUrl", jobDetails.getApplyUrl());
                    intent.putExtra("companyUrl", jobDetails.getCompanyUrl());
                    intent.putExtra("salary", jobDetails.getSalary());
                    intent.putExtra("publishedAt", jobDetails.getPublishedAt());
                    intent.putExtra("postedTime", jobDetails.getPostedTime());
                    intent.putExtra("applicationsCount", jobDetails.getApplicationsCount());
                    intent.putExtra("contractType", jobDetails.getContractType());
                    intent.putExtra("experienceLevel", jobDetails.getExperienceLevel());
                    intent.putExtra("workType", jobDetails.getWorkType());
                    intent.putExtra("sector", jobDetails.getSector());
                    startActivity(intent);
                });

                if (bestSection != null) {
                    bestSection.addView(jobView);
                }
            }

        } catch (Exception e) {
            Log.e("MainActivity", "Error loading jobs from JSON", e);
        }
    }

    private Pair<String, LinearLayout> findBestMatchingSection(String title, Map<String, LinearLayout> sectionsMap) {
        String bestSectionName = null;
        LinearLayout bestSection = null;
        int maxMatchCount = 0;

        for (Map.Entry<String, LinearLayout> entry : sectionsMap.entrySet()) {
            String sectionName = entry.getKey();
            int matchCount = getWordMatchCount(title, sectionName);

            if (matchCount > maxMatchCount) {
                maxMatchCount = matchCount;
                bestSection = entry.getValue();
                bestSectionName = sectionName;
            }
        }

        return new Pair<>(bestSectionName, bestSection);
    }

    private int getWordMatchCount(String title, String sectionName) {
        String[] titleWords = title.toLowerCase(Locale.ROOT).split(" ");
        String[] sectionWords = sectionName.toLowerCase(Locale.ROOT).split(" ");
        int matchCount = 0;

        for (String titleWord : titleWords) {
            for (String sectionWord : sectionWords) {
                if (titleWord.equals(sectionWord)) {
                    matchCount++;
                }
            }
        }

        return matchCount;
    }

    private static class JobDetails {
        private final String title;
        private final String companyName;
        private final String location;
        private final String description;
        private final String applyUrl;
        private final String companyUrl;
        private final String salary;
        private final String publishedAt;
        private final String postedTime;
        private final String applicationsCount;
        private final String contractType;
        private final String experienceLevel;
        private final String workType;
        private final String sector;

        public JobDetails(String title, String companyName, String location, String description, String applyUrl, String companyUrl, String salary, String publishedAt, String postedTime, String applicationsCount, String contractType, String experienceLevel, String workType, String sector) {
            this.title = title;
            this.companyName = companyName;
            this.location = location;
            this.description = description;
            this.applyUrl = applyUrl;
            this.companyUrl = companyUrl;
            this.salary = salary;
            this.publishedAt = publishedAt;
            this.postedTime = postedTime;
            this.applicationsCount = applicationsCount;
            this.contractType = contractType;
            this.experienceLevel = experienceLevel;
            this.workType = workType;
            this.sector = sector;
        }

        public String getTitle() {
            return title;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getLocation() {
            return location;
        }

        public String getDescription() {
            return description;
        }

        public String getApplyUrl() {
            return applyUrl;
        }

        public String getCompanyUrl() {
            return companyUrl;
        }

        public String getSalary() {
            return salary;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public String getPostedTime() {
            return postedTime;
        }

        public String getApplicationsCount() {
            return applicationsCount;
        }

        public String getContractType() {
            return contractType;
        }

        public String getExperienceLevel() {
            return experienceLevel;
        }

        public String getWorkType() {
            return workType;
        }

        public String getSector() {
            return sector;
        }
    }
}