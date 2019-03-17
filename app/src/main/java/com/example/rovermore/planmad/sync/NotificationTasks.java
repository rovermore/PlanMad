package com.example.rovermore.planmad.sync;

import android.content.Context;

import com.example.rovermore.planmad.utilities.NotificationUtils;

public class NotificationTasks {

    public static final String ACTION_TODAY_NOTIFICATION = "today-notification";

    public static void executeTask (Context context, String action){
        if (ACTION_TODAY_NOTIFICATION.equals(action)){
            issueTodayNotification(context);
        }
    }

    private static void issueTodayNotification(Context context) {
        NotificationUtils.todayUserNotification(context);
    }
}
