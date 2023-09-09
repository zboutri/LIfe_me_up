package com.example.lifemeup;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// Connect the app with the messaging service in firebase
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // Get the title and the body from the notification
        String title = message.getNotification().getTitle();
        String body = message.getNotification().getBody();
        final String channelId = "HEADS_UP_NOTIFICATION";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId,
                    "Heads Up Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the channel to access the notification
                getSystemService(NotificationManager.class).createNotificationChannel(channel);
            }
        }
        Notification.Builder notification = null;

        // Build the notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true);
        }
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(message);
    }
}
