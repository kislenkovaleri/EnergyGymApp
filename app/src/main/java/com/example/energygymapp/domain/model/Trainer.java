package com.example.energygymapp.domain.model;

public class Trainer {
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String lessonPrice;
    private String workoutType;
    private String bio;

    public Trainer() {
    }

    public Trainer(String uid, String name, String email, String phoneNumber,
                   String profileImageUrl, String lessonPrice, String workoutType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.lessonPrice = lessonPrice;
        this.workoutType = workoutType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getLessonPrice() {
        return lessonPrice;
    }

    public void setLessonPrice(String lessonPrice) {
        this.lessonPrice = lessonPrice;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
