package com.example.careercrew;

public class User {
    private String name;
    private String email;
    private String password;
    private String age;
    private String gender;
    private String careerFocus;
    private String lastChoice; // New field

    // Fields for AI job recommendations
    private String aijobrecommended1;
    private String aijobrecommended2;
    private String aijobrecommended3;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, String age, String gender, String careerFocus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.careerFocus = careerFocus;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCareerFocus() {
        return careerFocus;
    }

    public void setCareerFocus(String careerFocus) {
        this.careerFocus = careerFocus;
    }

    public String getLastChoice() {
        return lastChoice;
    }

    public void setLastChoice(String lastChoice) {
        this.lastChoice = lastChoice;
    }

    public String getAijobrecommended1() {
        return aijobrecommended1;
    }

    public void setAijobrecommended1(String aijobrecommended1) {
        this.aijobrecommended1 = aijobrecommended1;
    }

    public String getAijobrecommended2() {
        return aijobrecommended2;
    }

    public void setAijobrecommended2(String aijobrecommended2) {
        this.aijobrecommended2 = aijobrecommended2;
    }

    public String getAijobrecommended3() {
        return aijobrecommended3;
    }

    public void setAijobrecommended3(String aijobrecommended3) {
        this.aijobrecommended3 = aijobrecommended3;
    }
}
