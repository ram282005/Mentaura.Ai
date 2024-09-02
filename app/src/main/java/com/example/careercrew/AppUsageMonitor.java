// AppUsageMonitor.java
package com.example.careercrew;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class AppUsageMonitor {

    private static final String TAG = "AppUsageMonitor";
    private Context context;

    public AppUsageMonitor(Context context) {
        this.context = context;
    }

    public void logAppUsage() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager != null) {
            long endTime = System.currentTimeMillis();
            long startTime = endTime - 1000 * 60 * 60; // Last hour

            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
            for (UsageStats usageStats : usageStatsList) {
                Log.d(TAG, "Package: " + usageStats.getPackageName() + ", Time: " + usageStats.getTotalTimeInForeground());
            }
        }
    }
}
