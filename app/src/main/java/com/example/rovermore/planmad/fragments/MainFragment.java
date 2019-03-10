package com.example.rovermore.planmad.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.activities.DetailActivity;
import com.example.rovermore.planmad.adapters.MainAdapter;
import com.example.rovermore.planmad.database.AppDatabase;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.network.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements MainAdapter.onEventClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter eventListAdapter;
    private Parcelable mListState;
    private List<Event> eventList;
    private Context context;

    private OnDataPass onDataPass;

    private AppDatabase mDb;

    private final static int ASYNC_TASK_INT = 101;
    public final static String EVENT_KEY_NAME = "event_name";
    public static final String LIST_STATE_KEY = "recycler_view_state";
    public static final String EVENT_LIST_KEY = "data_state";

    public interface OnDataPass {
        void onDataPass(Parcelable mListState, List<Event> eventList);
    }

    public MainFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (OnDataPass) context;
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mDb = AppDatabase.getInstance(getContext());

        recyclerView = rootView.findViewById(R.id.rv_list_events);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        eventListAdapter = new MainAdapter(getContext(), null, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(eventListAdapter);

        if(getArguments()!=null){
            mListState = getArguments().getParcelable(LIST_STATE_KEY);
            layoutManager.onRestoreInstanceState(mListState);
            eventList = getArguments().getParcelableArrayList(EVENT_LIST_KEY);
            eventListAdapter.setEventList(eventList);

        } else {

            new FetchEvents().execute(ASYNC_TASK_INT);
        }

        return rootView;
    }

    @Override
    public void onEventClicked(Event event) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EVENT_KEY_NAME, event);
        startActivity(intent);
    }


    private class FetchEvents extends AsyncTask<Integer, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Integer... integers) {
            List<Event> eventList = new ArrayList<>();
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl();
                eventList = NetworkUtils.parseJson(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return eventList;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            if(eventListAdapter!=null)eventListAdapter.clearEventListAdapter();
            eventListAdapter.setEventList(events);
            eventList = events;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save list state
        mListState = layoutManager.onSaveInstanceState();
        onDataPass.onDataPass(mListState, eventList);

    }
}
