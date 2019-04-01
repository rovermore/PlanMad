package com.example.rovermore.planmad.sync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.rovermore.planmad.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_HASH_KEY = "notification-hash";
    public static String EVENT_NOTIFICATION = "event-notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification eventNotification = intent.getParcelableExtra(EVENT_NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_HASH_KEY, 1);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NotificationUtils.EVENT_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(notificationId,eventNotification);

    }
}
