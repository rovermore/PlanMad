package com.example.rovermore.planmad.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.utilities.NotificationUtils;

public class NotificationReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String EVENT_NOTIFICATION = "event-notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        Event event = intent.getParcelableExtra(EVENT_NOTIFICATION);
        NotificationUtils.eventNotification(context,event);
    }
}
