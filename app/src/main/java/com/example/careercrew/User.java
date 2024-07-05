package com.example.careercrew;

public class User {
    private String name;
    private String email;
    private String password;
    private String age;
    private String gender;
    private String careerFocus;
    private String lastChoice;  // New field

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
}
