package com.example.careercrew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isTyping()) {
            return 2; // Typing message
        } else if (message.isUser()) {
            return 0; // User message
        } else {
            return 1; // AI message
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_me, parent, false);
            return new UserMessageViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bot, parent, false);
            return new AIMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_typing, parent, false);
            return new TypingMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AIMessageViewHolder) {
            ((AIMessageViewHolder) holder).bind(message);
        } else if (holder instanceof TypingMessageViewHolder) {
            ((TypingMessageViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;

        UserMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timestampText = itemView.findViewById(R.id.timestamp_text);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timestampText.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    class AIMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;

        AIMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timestampText = itemView.findViewById(R.id.timestamp_text);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timestampText.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    class TypingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView typingText;

        TypingMessageViewHolder(View itemView) {
            super(itemView);
            typingText = itemView.findViewById(R.id.typing_text);
        }

        void bind() {
            typingText.setText("Typing...");
        }
    }

    private String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }
}
