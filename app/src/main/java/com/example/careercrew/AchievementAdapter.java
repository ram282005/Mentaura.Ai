package com.example.careercrew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AchievementAdapter extends BaseAdapter {
    private Context context;
    private List<Achievement> achievementList;

    public AchievementAdapter(Context context, List<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @Override
    public int getCount() {
        return achievementList.size();
    }

    @Override
    public Object getItem(int position) {
        return achievementList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.achievement_item, parent, false);
        }

        Achievement achievement = achievementList.get(position);

        TextView assessmentTextView = convertView.findViewById(R.id.assessmentTextView);
        TextView scoreTextView = convertView.findViewById(R.id.scoreTextView);

        assessmentTextView.setText(achievement.getAssessment());
        scoreTextView.setText(String.valueOf(achievement.getScore()));

        return convertView;
    }
}
