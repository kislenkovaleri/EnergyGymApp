package com.example.energygymapp.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeepSeekRequest {

    @SerializedName("model")
    private final String model;

    @SerializedName("messages")
    private final List<DeepSeekMessage> messages;

    public DeepSeekRequest(String model, List<DeepSeekMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }
}
