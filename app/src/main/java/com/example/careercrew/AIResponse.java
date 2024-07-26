package com.example.careercrew;

import java.util.List;

public class AIResponse {
    private String conversationId;
    private List<ChatMessage> chatMessages;

    // Getters and Setters

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public static class ChatMessage {
        private String message;

        // Getters and Setters

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
