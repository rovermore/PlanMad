package com.example.rovermore.planmad.sync;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.activities.DetailActivity;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.fragments.MainFragment;

public class NotificationUtils {

    public static final String EVENT_NOTIFICATION_CHANNEL_ID = "event-notification-channel";
    private static final int EVENT_NOTIFICATION_PENDING_INTENT_ID = 3003;

    public static Notification eventNotification (Context context, Event event){

        PendingIntent pendingIntent = eventContentIntent(context, event);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,EVENT_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(event.getTitle())
                .setContentText(event.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        event.getDescription()))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }*/
        return notificationBuilder.build();
    }

    private static PendingIntent eventContentIntent(Context context, Event event) {
        Intent startActivityIntent = new Intent(context, DetailActivity.class);
        startActivityIntent.putExtra(MainFragment.EVENT_KEY_NAME,event);
        return PendingIntent.getActivity(
                context,
                EVENT_NOTIFICATION_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
