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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainFragment extends Fragment implements MainAdapter.onEventClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter eventListAdapter;
    private Parcelable mListState;
    private List<Event> eventList;
    private Event clickedEvent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner monthSpinner;
    private Button filterButton;
    private LinearLayout linearLayout;
    private boolean mTwoPane;
    private boolean setFirstEvent;
    private Context context;
    private List<Event> monthEventList;
    private int monthPosition;
    private ProgressBar progressBar;

    private OnDataPass onDataPass;

    private AppDatabase mDb;

    private final String TAG = MainFragment.class.getName();

    private final static int ASYNC_TASK_INT = 101;
    public final static String EVENT_KEY_NAME = "event_name";
    public static final String LIST_STATE_KEY = "recycler_view_state";
    public static final String EVENT_LIST_KEY = "data_state";

    public interface OnDataPass {
        void onDataPass(Parcelable mListState, Event event, int monthPosition);
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

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        progressBar = (ProgressBar) rootView.findViewById(R.id.main_progress_loader);
        progressBar.setVisibility(View.VISIBLE);

        linearLayout = (LinearLayout) rootView.findViewById(R.id.view_spinner_button);
        linearLayout.setVisibility(View.GONE);
        monthSpinner = rootView.findViewById(R.id.spinner_month);
        filterButton = rootView.findViewById(R.id.button_filter_month);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMonthList();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(NetworkUtils.isInternetAvailable(getContext())){
                    //new FetchEvents().execute(ASYNC_TASK_INT);
                    getListFromRetrofit();
                } else {
                    Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
                }
            }
        });

        setFirstEvent = true;

        if(getArguments()!=null){
            mTwoPane = getArguments().getBoolean(MainActivity.TWO_PANE_KEY);
            mListState = getArguments().getParcelable(LIST_STATE_KEY);
            monthPosition = getArguments().getInt(MainActivity.MONTH_POSITION_KEY,1001);
            if(mListState!=null && eventList!=null) {
                if(monthEventList!=null){
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    eventListAdapter.setEventList(monthEventList);
                } else {
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    eventListAdapter.setEventList(eventList);
                }
                layoutManager.onRestoreInstanceState(mListState);
                setFirstEvent = false;
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
        if(mTwoPane){
            this.clickedEvent = event;
            onDataPass.onDataPass(mListState, clickedEvent, monthPosition);
        } else {

            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(EVENT_KEY_NAME, event);
            startActivity(intent);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        // Save list state
        mListState = layoutManager.onSaveInstanceState();
        onDataPass.onDataPass(mListState, clickedEvent, monthPosition);

    }

    private void setMonthList(){
        monthEventList = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("MM");
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            Date eventDate = event.getDtstart();
            String eventDateText = df.format(eventDate);
            int eventDateInt = Integer.parseInt(eventDateText);
            monthPosition = monthSpinner.getSelectedItemPosition();
            if (eventDateInt == monthPosition + 1) {
                monthEventList.add(event);
            }
        }
        if(eventListAdapter!=null)eventListAdapter.clearEventListAdapter();
        eventListAdapter.setEventList(monthEventList);
    }

    private void setCurrentMonth(){
        List<Event> currentMonthEventList = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("MM");
        Date currentDate = Calendar.getInstance().getTime();
        String currentDateText = df.format(currentDate);
        int currentDateInt = Integer.parseInt(currentDateText);
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            Date eventDate = event.getDtstart();
            String eventDateText = df.format(eventDate);
            int eventDateInt = Integer.parseInt(eventDateText);
            //monthPosition = monthSpinner.getSelectedItemPosition();
            if (eventDateInt == currentDateInt) {
                currentMonthEventList.add(event);
            }
        }
        if(eventListAdapter!=null)eventListAdapter.clearEventListAdapter();
        eventListAdapter.setEventList(currentMonthEventList);
        monthSpinner.setSelection(currentDateInt - 1);
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
                    Log.e(TAG,"Response code is: " + response.code());
                    EventFeed eventFeed = response.body();
                    List<Graph> graphList = eventFeed.getGraphList();
                    List<Event> events = new ArrayList<>();
                    for(Graph graph : graphList){

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
                        if(jsonRecurrence!=null) {
                            recurrenceDays = jsonRecurrence.getDays();
                            recurrenceFrequency = jsonRecurrence.getFrequency();
                        }
                        String eventLocation = graph.getEvent_location();
                        MyLocation jsonLocation = graph.getLocation();
                        double latitude = 0;
                        double longitude = 0;
                        if(jsonLocation!=null) {
                            latitude = jsonLocation.getLatitude();
                            longitude = jsonLocation.getLongitude();
                        }

                        Event event = new Event(hash, title, description, price, dtstart, dtend,recurrenceDays,
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
                    eventList = events;
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    if (eventListAdapter != null) eventListAdapter.clearEventListAdapter();
                    if(mListState!=null && monthPosition >= 0 && monthPosition <= 11){
                        setMonthList();
                    } else {
                        setCurrentMonth();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    if(mListState!=null) layoutManager.onRestoreInstanceState(mListState);
                    if(mListState==null) clickedEvent = eventList.get(0);
                    onDataPass.onDataPass(mListState, clickedEvent, monthPosition);

                }
            }

            @Override
            public void onFailure(Call<EventFeed> call, Throwable t) {
                Log.d(TAG,"ERROR: " + t.toString());
            }
        });
    }
}


