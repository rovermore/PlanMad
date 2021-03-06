package com.rvm.rovermore.planmad.fragments;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rvm.rovermore.planmad.R;
import com.rvm.rovermore.planmad.activities.DetailActivity;
import com.rvm.rovermore.planmad.activities.MainActivity;
import com.rvm.rovermore.planmad.adapters.MainAdapter;
import com.rvm.rovermore.planmad.database.AppDatabase;
import com.rvm.rovermore.planmad.datamodel.Event;
import com.rvm.rovermore.planmad.network.NetworkUtils;

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
    private ProgressBar progressBar;
    private AppDatabase mDb;

    private final static int ASYNC_TASK_INT = 102;

    public interface OnDataPassFromTodayFragment {
        void onDataPassFromTodayFragment(Parcelable mListState, Event event);
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
        progressBar = (ProgressBar) rootView.findViewById(R.id.today_progress_loader);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        if(getArguments()!=null){
            mTwoPane = getArguments().getBoolean(MainActivity.TWO_PANE_KEY);
            mListState = getArguments().getParcelable(MainFragment.LIST_STATE_KEY);
            if(mListState!=null && eventList!=null) {
                progressBar.setVisibility(View.GONE);
                eventListAdapter.setEventList(eventList);
                layoutManager.onRestoreInstanceState(mListState);
            } else {
                if(NetworkUtils.isInternetAvailable(getContext())){
                    new FetchEvents().execute(ASYNC_TASK_INT);
                } else {
                    Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            if(NetworkUtils.isInternetAvailable(getContext())){
                new FetchEvents().execute(ASYNC_TASK_INT);
            } else {
                Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }

    @Override
    public void onEventClicked(Event event) {
        if(mTwoPane) {
            this.clickedEvent = event;
            onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, clickedEvent);
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
            List<Event> jsonFileEventList = new ArrayList<>();
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl();
                eventList = NetworkUtils.parseJson(jsonResponse);
                if(eventList!=null) {
                    todayEventList = NetworkUtils.getTodayList(eventList);
                } else {
                    jsonFileEventList = NetworkUtils.parseJsonfromRawFile(getContext());
                    todayEventList = NetworkUtils.getTodayList(jsonFileEventList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return todayEventList;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            if (events != null) {
                eventList = events;
                super.onPostExecute(events);
                eventList = events;
                if (eventListAdapter != null) eventListAdapter.clearEventListAdapter();
                progressBar.setVisibility(View.GONE);
                eventListAdapter.setEventList(events);
                swipeRefreshLayout.setRefreshing(false);
                if (mListState != null) layoutManager.onRestoreInstanceState(mListState);
                onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, clickedEvent);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListState = layoutManager.onSaveInstanceState();
        onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, clickedEvent);
    }
}
