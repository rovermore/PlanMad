package com.example.rovermore.planmad.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.activities.DetailActivity;
import com.example.rovermore.planmad.activities.MainActivity;
import com.example.rovermore.planmad.adapters.MainAdapter;
import com.example.rovermore.planmad.database.AppDatabase;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.network.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment implements MainAdapter.onEventClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter eventListAdapter;
    private Context context;
    private List<Event> eventList;
    private Parcelable mListState;
    private Event clickedEvent;
    private boolean mTwoPane;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnDataPassFromTodayFragment onDataPassFromTodayFragment;

    private AppDatabase mDb;

    private final static int ASYNC_TASK_INT = 102;

    public interface OnDataPassFromTodayFragment {
        void onDataPassFromTodayFragment(Parcelable mListState, List<Event> eventList, Event event);
    }

    public TodayFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPassFromTodayFragment = (OnDataPassFromTodayFragment) context;
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        mDb = AppDatabase.getInstance(getContext());

        recyclerView = rootView.findViewById(R.id.rv_today_events);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        eventListAdapter = new MainAdapter(getContext(), null, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(eventListAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        if(getArguments()!=null){
            mTwoPane = getArguments().getBoolean(MainActivity.TWO_PANE_KEY);
            mListState = getArguments().getParcelable(MainFragment.LIST_STATE_KEY);
            eventList = getArguments().getParcelableArrayList(MainFragment.EVENT_LIST_KEY);
            if(mListState!=null && eventList!=null) {
                eventListAdapter.setEventList(eventList);
                layoutManager.onRestoreInstanceState(mListState);
            } else {
                new FetchEvents().execute(ASYNC_TASK_INT);
            }

        } else {

            new FetchEvents().execute(ASYNC_TASK_INT);
        }

        return rootView;
    }

    @Override
    public void onEventClicked(Event event) {
        if(mTwoPane) {
            this.clickedEvent = event;
            onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, eventList, clickedEvent);
        } else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(MainFragment.EVENT_KEY_NAME, event);
            startActivity(intent);
        }
    }


    private class FetchEvents extends AsyncTask<Integer, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Integer... integers) {
            List<Event> eventList = new ArrayList<>();
            List<Event> todayEventList = new ArrayList<>();
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl();
                eventList = NetworkUtils.parseJson(jsonResponse);
                todayEventList = NetworkUtils.getTodayList(eventList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return todayEventList;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            if(eventListAdapter!=null)eventListAdapter.clearEventListAdapter();
            eventListAdapter.setEventList(events);
            eventList = events;
            swipeRefreshLayout.setRefreshing(false);
            onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, eventList, clickedEvent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListState = layoutManager.onSaveInstanceState();
        onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, eventList, clickedEvent);
    }
}
