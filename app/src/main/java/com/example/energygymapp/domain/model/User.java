package com.example.energygymapp.domain.model;

public class User {
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String subscriptionActiveUntil;
    private boolean isTrainer;

    public User() {
    }

    public User(String uid, String name, String email, String phoneNumber,
                String profileImageUrl, String subscriptionActiveUntil) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.subscriptionActiveUntil = subscriptionActiveUntil;
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

    public String getSubscriptionActiveUntil() {
        return subscriptionActiveUntil;
    }

    public void setSubscriptionActiveUntil(String subscriptionActiveUntil) {
        this.subscriptionActiveUntil = subscriptionActiveUntil;
    }

    public boolean isTrainer() {
        return isTrainer;
    }

    public void setTrainer(boolean trainer) {
        isTrainer = trainer;
    }
}
