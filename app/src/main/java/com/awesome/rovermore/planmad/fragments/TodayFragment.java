package com.awesome.rovermore.planmad.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.awesome.rovermore.planmad.R;
import com.awesome.rovermore.planmad.activities.DetailActivity;
import com.awesome.rovermore.planmad.activities.MainActivity;
import com.awesome.rovermore.planmad.adapters.MainAdapter;
import com.awesome.rovermore.planmad.database.AppDatabase;
import com.awesome.rovermore.planmad.datamodel.Event;
import com.awesome.rovermore.planmad.datamodel.EventFeed;
import com.awesome.rovermore.planmad.datamodel.Graph;
import com.awesome.rovermore.planmad.datamodel.MyLocation;
import com.awesome.rovermore.planmad.datamodel.Recurrence;
import com.awesome.rovermore.planmad.network.NetworkUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    private final String TAG = TodayFragment.class.getName();

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
                    //new FetchEvents().execute(ASYNC_TASK_INT);
                    getListFromRetrofit();
                } else {
                    Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            if(NetworkUtils.isInternetAvailable(getContext())){
                //new FetchEvents().execute(ASYNC_TASK_INT);
                getListFromRetrofit();
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


    @Override
    public void onPause() {
        super.onPause();
        mListState = layoutManager.onSaveInstanceState();
        onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, clickedEvent);
    }

    private void getListFromRetrofit(){

        Retrofit retrofit = NetworkUtils.connectWithRetrofit();
        NetworkUtils.AyuntamientoMadridService ayuntamientoMadridService = retrofit.create(NetworkUtils.AyuntamientoMadridService.class);
        Call<EventFeed> call = ayuntamientoMadridService.getEventFeed();
        call.enqueue(new Callback<EventFeed>() {
            @Override
            public void onResponse(Call<EventFeed> call, Response<EventFeed> response) {
                if(!response.isSuccessful()) {
                    Log.e(TAG,"Response code is: " + response.code());
                } else {
                    Log.e(TAG, "Response code is: " + response.code());
                    EventFeed eventFeed = response.body();
                    List<Graph> graphList = eventFeed.getGraphList();
                    List<Event> events = new ArrayList<>();
                    for (Graph graph : graphList) {

                        String stringHash = graph.getId();
                        int hash = Integer.parseInt(stringHash);
                        String type = graph.getTypeUrl();
                        String eventType = NetworkUtils.parseEventType(type);
                        String title = graph.getTitle();
                        String description = graph.getDescription();
                        int price = graph.getPrice();
                        String stringDtStart = graph.getDtstart();
                        Date dtstart = NetworkUtils.fromStringToDate(stringDtStart);
                        String stringDtEnd = graph.getDtend();
                        Date dtend = NetworkUtils.fromStringToDate(stringDtEnd);
                        String link = graph.getLink();
                        Recurrence jsonRecurrence = graph.getRecurrence();
                        String recurrenceDays = null;
                        String recurrenceFrequency = null;
                        if (jsonRecurrence != null) {
                            recurrenceDays = jsonRecurrence.getDays();
                            recurrenceFrequency = jsonRecurrence.getFrequency();
                        }
                        String eventLocation = graph.getEvent_location();
                        MyLocation jsonLocation = graph.getLocation();
                        double latitude = 0;
                        double longitude = 0;
                        if (jsonLocation != null) {
                            latitude = jsonLocation.getLatitude();
                            longitude = jsonLocation.getLongitude();
                        }

                        Event event = new Event(hash, title, description, price, dtstart, dtend, recurrenceDays,
                                recurrenceFrequency, eventLocation, latitude, longitude, eventType, false, link);

                        Log.v(TAG, "title " + title);
                        Log.v(TAG, "id " + hash);
                        Log.v(TAG, "RECURRENCE DAYS: " + recurrenceDays);
                        Log.v(TAG, "RECURRENCE FREQUENCY " + recurrenceFrequency);
                        Log.v(TAG, "LATITUDE " + latitude);
                        Log.v(TAG, "LONGITUDE " + longitude);

                        events.add(event);
                    }
                    NetworkUtils.sortEventList(events);
                    eventList = NetworkUtils.getTodayList(events);
                    if(eventListAdapter!=null)eventListAdapter.clearEventListAdapter();
                    progressBar.setVisibility(View.GONE);
                    eventListAdapter.setEventList(eventList);
                    swipeRefreshLayout.setRefreshing(false);
                    if(mListState!=null) layoutManager.onRestoreInstanceState(mListState);
                    onDataPassFromTodayFragment.onDataPassFromTodayFragment(mListState, clickedEvent);

                }
            }

            @Override
            public void onFailure(Call<EventFeed> call, Throwable t) {
                Log.d(TAG,"ERROR: " + t.toString());
            }
        });
    }
}
