package com.example.careercrew;

public class Message {
    public static final int SENT_BY_ME = 0;
    public static final int SENT_BY_BOT = 1;

    private String content;
    private int sentBy;

    public Message(String content, int sentBy) {
        this.content = content;
        this.sentBy = sentBy;
    }

    public String getContent() {
        return content;
    }

    public int getSentBy() {
        return sentBy;
    }
}
