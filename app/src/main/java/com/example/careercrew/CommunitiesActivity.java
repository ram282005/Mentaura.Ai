package com.example.careercrew;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunitiesActivity extends AppCompatActivity {

    private ListView individualRanksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        // Initialize TabHost
        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("CommunityRanks");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Community Ranks");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("IndividualRanks");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Individual Ranks");
        tabHost.addTab(spec);

        // Initialize ListView
        individualRanksListView = findViewById(R.id.individual_ranks_list);

        // Create a list of maps, each map represents a list item
        List<Map<String, String>> individualRanksData = new ArrayList<>();

        // Sample data
        Map<String, String> item1 = new HashMap<>();
        item1.put("title", "John Doe");
        item1.put("description", "Rank 1: 500 points");
        individualRanksData.add(item1);

        Map<String, String> item2 = new HashMap<>();
        item2.put("title", "Jane Smith");
        item2.put("description", "Rank 2: 450 points");
        individualRanksData.add(item2);

        // Create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                individualRanksData,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "description"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        individualRanksListView.setAdapter(adapter);
    }
}
