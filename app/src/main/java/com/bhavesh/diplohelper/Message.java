package com.bhavesh.diplohelper;

public class Message {
    private String text;
    private String senderId;
    private long timestamp;

    // Empty constructor needed for Firebase
    public Message() {
    }

    public Message(String text, String senderId, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

