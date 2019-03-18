package com.example.rovermore.planmad.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.activities.MainActivity;
import com.example.rovermore.planmad.datamodel.Event;

public class NotificationUtils {

    private static final String TODAY_EVENTS_NOTIFICATION_CHANNEL_ID = "today-notification-channel";
    private static final int TODAY_NOTIFICATION_PENDING_INTENT_ID = 3001;
    private static final int TODAY_REMINDER_NOTIFICATION_ID = 3002;

    private static final String EVENT_NOTIFICATION_CHANNEL_ID = "event-notification-channel";
    private static final int EVENT_NOTIFICATION_PENDING_INTENT_ID = 3003;
    private static final int EVENT_REMINDER_NOTIFICATION_ID = 3002;


    public static void todayUserNotification (Context context){

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    TODAY_EVENTS_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,TODAY_EVENTS_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.today_reminder_notification_title))
                .setContentText(context.getString(R.string.today_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.today_reminder_notification_body)))
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(TODAY_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.putExtra("NotificationFragment", "todayNotificationFragment");
        return PendingIntent.getActivity(
                context,
                TODAY_NOTIFICATION_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void eventNotification (Context context, Event event){

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    EVENT_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,EVENT_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(event.getTitle())
                .setContentText(event.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        event.getDescription()))
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(EVENT_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }
}
