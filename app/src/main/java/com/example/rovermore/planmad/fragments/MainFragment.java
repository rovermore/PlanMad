package com.example.rovermore.planmad.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.adapters.MainAdapter;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.network.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter eventListAdapter;
    private final static int ASYNC_TASK_INT = 101;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.tv_text_main_fragment);

        recyclerView = rootView.findViewById(R.id.rv_list_events);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        eventListAdapter = new MainAdapter(getContext(), null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(eventListAdapter);

        new FetchEvents().execute(ASYNC_TASK_INT);

        return rootView;
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
        }
    }

}
