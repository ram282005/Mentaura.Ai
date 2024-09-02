package com.example.careercrew;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Message {
    public static final boolean RECEIVED_FROM_AI = false; // Use this constant for received messages

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

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("isUser", isUser);
            jsonObject.put("isTyping", isTyping);
            jsonObject.put("timestamp", timestamp.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Message fromJson(JSONObject jsonObject) {
        String text = jsonObject.optString("text", null);
        boolean isUser = jsonObject.optBoolean("isUser", false);
        boolean isTyping = jsonObject.optBoolean("isTyping", false);
        Date timestamp = new Date(jsonObject.optLong("timestamp", 0));
        if (isTyping) {
            return new Message(isTyping);
        } else {
            return new Message(text, isUser, timestamp);
        }
    }
}
