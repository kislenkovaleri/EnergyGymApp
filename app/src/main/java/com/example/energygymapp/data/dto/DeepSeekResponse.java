package com.example.energygymapp.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeepSeekResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("choices")
    private List<Choice> choices;

    public String getId() {
        return id;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {

        @SerializedName("index")
        private int index;

        @SerializedName("message")
        private DeepSeekMessage message;

        @SerializedName("finish_reason")
        private String finishReason;

        public int getIndex() {
            return index;
        }

        public DeepSeekMessage getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finishReason;
        }
    }
}
