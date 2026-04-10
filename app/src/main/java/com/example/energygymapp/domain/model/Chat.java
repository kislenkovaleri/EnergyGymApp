package com.example.energygymapp.domain.model;

public class Chat {
    private String chatId;
    private String uid1;
    private String uid2;
    private String lastMessage;
    private String otherPersonName;
    private String otherPersonImageUrl;

    public Chat() {
    }

    public Chat(String chatId, String uid1, String uid2, String lastMessage) {
        this.chatId = chatId;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.lastMessage = lastMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getOtherPersonName() {
        return otherPersonName;
    }

    public void setOtherPersonName(String otherPersonName) {
        this.otherPersonName = otherPersonName;
    }

    public String getOtherPersonImageUrl() {
        return otherPersonImageUrl;
    }

    public void setOtherPersonImageUrl(String otherPersonImageUrl) {
        this.otherPersonImageUrl = otherPersonImageUrl;
    }
}
