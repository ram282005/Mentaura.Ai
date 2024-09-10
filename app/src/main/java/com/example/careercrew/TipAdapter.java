package com.example.careercrew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.TipViewHolder> {

    private List<String> tipsList;

    // Constructor to initialize the list of tips
    public TipAdapter(List<String> tipsList) {
        this.tipsList = tipsList;
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each tip
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        // Bind the tip data to the view
        String tip = tipsList.get(position);
        holder.tipTextView.setText(tip);
    }

    @Override
    public int getItemCount() {
        return tipsList.size(); // Return the total number of tips
    }

    // ViewHolder class to hold the views for each tip
    static class TipViewHolder extends RecyclerView.ViewHolder {
        TextView tipTextView;

        public TipViewHolder(@NonNull View itemView) {
            super(itemView);
            tipTextView = itemView.findViewById(R.id.tip_text);
        }
    }
}
