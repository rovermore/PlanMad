package com.awesome.rovermore.planmad.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.awesome.rovermore.planmad.R;
import com.awesome.rovermore.planmad.activities.MainActivity;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int i=0; i<appWidgetIds.length; i++){

            //this intent starts the service
            Intent widgetServiceIntent=new Intent(context, WidgetService.class);

            RemoteViews widget=new RemoteViews(context.getPackageName(),
                    R.layout.event_widget_provider);

            //this creates the widget via widget service
            widget.setRemoteAdapter( R.id.listViewWidget,
                    widgetServiceIntent);


            Intent clickIntent=new Intent(context, MainActivity.class);
            clickIntent.putExtra("NotificationFragment", "todayNotificationFragment");
            PendingIntent clickPI=PendingIntent
                    .getActivity(context, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            //this pending intent launches the activity with the id of the row selected
            widget.setPendingIntentTemplate(R.id.listViewWidget, clickPI);

            //notify the data has changed in the widget
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.listViewWidget);

            //updates the widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}