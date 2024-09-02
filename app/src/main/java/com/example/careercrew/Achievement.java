package com.example.careercrew;

public class Achievement {
    private String assessment;
    private int score;

    public Achievement() {
        // Default constructor required for calls to DataSnapshot.getValue(Achievement.class)
    }

    public Achievement(String assessment, int score) {
        this.assessment = assessment;
        this.score = score;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
