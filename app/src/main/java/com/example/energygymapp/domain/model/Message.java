package com.example.energygymapp.domain.model;

public class Message {
    private String id;
    private String sentBy;
    private long sentAt;
    private String content;

    public Message() {
    }

    public Message(String id, String sentBy, long sentAt, String content) {
        this.id = id;
        this.sentBy = sentBy;
        this.sentAt = sentAt;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
