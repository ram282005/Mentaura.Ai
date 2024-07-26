package com.example.careercrew;

import java.util.Date;

public class Message {
    private String text;
    private boolean isUser;
    private boolean isTyping;
    private Date timestamp;

    public Message(String text, boolean isUser, Date timestamp) {
        this.text = text;
        this.isUser = isUser;
        this.isTyping = false;
        this.timestamp = timestamp;
    }

    public Message(boolean isTyping) {
        this.text = null;
        this.isUser = false;
        this.isTyping = isTyping;
        this.timestamp = new Date();
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}