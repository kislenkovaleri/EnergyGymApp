package com.example.energygymapp.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// класс сообщения переписки пользователь-AI, которая сохраняется в локальной БД
@Entity(tableName = "ai_messages")
public class AiMessageEntity {

    @PrimaryKey
    @NonNull
    private String id;

    private String userId;
    private String sentBy;
    private long sentAt;
    private String content;

    public AiMessageEntity(@NonNull String id, String userId, String sentBy, long sentAt, String content) {
        this.id = id;
        this.userId = userId;
        this.sentBy = sentBy;
        this.sentAt = sentAt;
        this.content = content;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
