package com.example.careercrew;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String targetActivity = remoteMessage.getData().get("targetActivity");

            // Log the targetActivity value
            Log.d("FCM", "Target Activity: " + targetActivity);

            // Show notification with specific Intent to open an Activity
            showNotification(title, body, targetActivity);
        }
    }


    private void showNotification(String title, String message, String targetActivity) {
        Intent intent;

        if ("ActivityA".equals(targetActivity)) {
            intent = new Intent(this, JobCommunity.class);
        } else if ("ActivityB".equals(targetActivity)) {
            intent = new Intent(this, ChatActivity.class);
        } else if ("ActivityC".equals(targetActivity)) {
            intent = new Intent(this, WeeklyAssignmentActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel")
                .setSmallIcon(R.drawable.entrypage2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, builder.build());
    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Handle the new token here if needed.
    }
}
