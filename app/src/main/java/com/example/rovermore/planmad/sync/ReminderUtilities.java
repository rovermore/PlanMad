package com.example.rovermore.planmad.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.rovermore.planmad.datamodel.Event;

import java.util.concurrent.TimeUnit;

public class ReminderUtilities {

    private final static String TAG = ReminderUtilities.class.getSimpleName();
    /*
     * Interval at which to remind the user to drink water. Use TimeUnit for convenience, rather
     * than writing out a bunch of multiplication ourselves and risk making a silly mistake.
     */
    private static final int HOUR_BEFORE_EVENT = 1;
    private static final int ONE_HOUR_IN_MILLISECONDS_BEFORE_EVENT = (int) (TimeUnit.HOURS.toMillis(HOUR_BEFORE_EVENT));

    public static void scheduleEventNotification (Context context, Event event){
        if(event!=null) {
            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_HASH_KEY, event.getHash());
            notificationIntent.putExtra(NotificationReceiver.EVENT_NOTIFICATION, NotificationUtils.eventNotification(context,event));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            long futureInMillis = event.getDtstart().getTime() - ONE_HOUR_IN_MILLISECONDS_BEFORE_EVENT;
            //long futureInMillis = System.currentTimeMillis() + 10000;
            Log.d(TAG, "The event date is: " + futureInMillis);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
                }
            }
        }

    }

}
