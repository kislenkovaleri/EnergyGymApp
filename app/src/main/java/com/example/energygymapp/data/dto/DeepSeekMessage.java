package com.example.energygymapp.data.dto;

import com.google.gson.annotations.SerializedName;

public class DeepSeekMessage {

    @SerializedName("role")
    private final String role;

    @SerializedName("content")
    private final String content;

    public DeepSeekMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
