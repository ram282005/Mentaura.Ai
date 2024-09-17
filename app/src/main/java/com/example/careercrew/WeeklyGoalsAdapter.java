package com.example.careercrew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeeklyGoalsAdapter extends RecyclerView.Adapter<WeeklyGoalsAdapter.ViewHolder> {
    private List<String> weeklyGoalsItems;

    public WeeklyGoalsAdapter(List<String> weeklyGoalsItems) {
        this.weeklyGoalsItems = weeklyGoalsItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = weeklyGoalsItems.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return weeklyGoalsItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.weekly_goal_item_text);
        }
    }
}
