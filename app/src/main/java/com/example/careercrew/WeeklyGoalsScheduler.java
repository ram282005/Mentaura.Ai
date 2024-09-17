package com.example.careercrew;

import android.content.Context;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class WeeklyGoalsScheduler {

    public static void scheduleWeeklyGoalsUpdate(Context context) {
        PeriodicWorkRequest weeklyGoalsRequest = new PeriodicWorkRequest.Builder(WeeklyGoalsWorker.class, 7, TimeUnit.DAYS)
                .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context).enqueue(weeklyGoalsRequest);
    }

    private static long getInitialDelay() {
        long currentTimeMillis = System.currentTimeMillis();
        long targetTimeMillis = System.currentTimeMillis();  // Set the target time to Monday at 1 AM
        // Add logic to calculate the delay here
        return targetTimeMillis - currentTimeMillis;
    }
}
