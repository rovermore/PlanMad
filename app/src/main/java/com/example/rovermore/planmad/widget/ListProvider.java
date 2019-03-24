package com.example.rovermore.planmad.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.network.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Event> todayEventList;
    private int appWidgetId;

    public ListProvider(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    //You can execute asynchronous task inside this method without asynctask needed
    @Override
    public void onDataSetChanged() {
        List<Event> eventList = new ArrayList<>();

        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl();
            eventList = NetworkUtils.parseJson(jsonResponse);
            todayEventList = NetworkUtils.getTodayList(eventList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return todayEventList == null ? 0 : todayEventList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Event event = todayEventList.get(position);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_row);
        rv.setTextViewText(R.id.widget_row_tv_event, event.getTitle() );

        Intent intent=new Intent();
        /*Bundle extras=new Bundle();

        //Here you set the ID of the item clicked in the list that will be added later
        //in the pending intent without needing any extra
        //so will open directly the selected recipe ingredients list
        extras.putInt(MainActivity.RECIPE_ID, position);
        intent.putExtras(extras);*/
        rv.setOnClickFillInIntent(R.id.widget_row_tv_event, intent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
