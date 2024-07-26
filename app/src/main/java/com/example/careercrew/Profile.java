package com.example.careercrew;

public class Profile {
    private String name;
    private String age;
    private String gender;
    private String email;
    private String phone;
    private String about;
    private String education;
    private String DOB;
    private String profilePicUrl;

    // No-argument constructor required for Firebase
    public Profile() {
    }

    // Constructor with parameters
    public Profile(String name, String age, String gender, String email, String phone, String about, String education, String DOB, String profilePicUrl) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.about = about;
        this.education = education;
        this.DOB = DOB;
        this.profilePicUrl = profilePicUrl;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAbout() {
        return about;
    }

    public String getEducation() {
        return education;
    }

    public String getDOB() {
        return DOB;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
