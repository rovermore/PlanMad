package com.example.rovermore.planmad.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.database.AppDatabase;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.threads.AppExecutors;

import java.util.Calendar;

//import java.util.Calendar;
//import java.util.TimeZone;

public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getSimpleName();

    private AppDatabase mDb;

    private Event event;
    private TextView name;
    private TextView location;
    private TextView date;
    private TextView endDate;
    private TextView recurrenceDay;
    private TextView recurrenceFrequency;
    private TextView price;
    private TextView description;
    private TextView fav;

    private boolean isEventSavedInFav;
    private Event eventFromDatabase;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDb = AppDatabase.getInstance(getContext());

        getActivity().setTitle(R.string.title_top_fav);

        if(getArguments()!=null){
            event = getArguments().getParcelable(MainFragment.EVENT_KEY_NAME);
            Log.d(TAG,"Event retrieved successfully from parcelable");
        } else {
            event = null;
            Log.d(TAG,"Error retrieving Event from parcelable");
        }

        name = rootView.findViewById(R.id.tv_detail_name);
        location = rootView.findViewById(R.id.tv_detail_location);
        date = rootView.findViewById(R.id.tv_detail_date);
        endDate = rootView.findViewById(R.id.tv_detail_date_end);
        recurrenceDay = rootView.findViewById(R.id.tv_detail_recurrence_day);
        recurrenceFrequency = rootView.findViewById(R.id.tv_detail_recurrence_frequency);
        price = rootView.findViewById(R.id.tv_detail_price);
        description = rootView.findViewById(R.id.tv_detail_description);
        fav = rootView.findViewById(R.id.tv_detail_favoritos);

        name.setText(event.getTitle());
        location.setText(event.getEventLocation());
        date.setText(String.valueOf(event.getDtstart()));
        endDate.setText(String.valueOf(event.getDtend()));
        recurrenceDay.setText(event.getRecurrenceDays());
        recurrenceFrequency.setText(event.getRecurrenceFrequency());
        price.setText(String.valueOf(event.getPrice()));
        description.setText(event.getDescription());

        checkEventInDatabase();

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEventSavedInFav) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.eventDao().insertEvent(event);
                            isEventSavedInFav = true;
                        }
                    });
                    Log.d(TAG, "Event saved in favorites");
                    Toast.makeText(getContext(), "Evento guardado en favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "El evento ya está guardado en favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void checkEventInDatabase() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                eventFromDatabase = mDb.eventDao().loadEventByHash(event.getHash());

                if(eventFromDatabase!=null){
                    isEventSavedInFav = true;
                    Log.d(TAG,"The Event id in DDBB is: " + eventFromDatabase.getIdDatabase());
                } else {
                    isEventSavedInFav = false;
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(!isEventSavedInFav){
            MenuItem deleteMenuItem = menu.findItem(R.id.delete_event);
            deleteMenuItem.setVisible(false);
            getActivity().setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId){
            case R.id.delete_event:
                //delete event from database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.eventDao().deleteEvent(event);
                    }
                });
                Toast.makeText(getContext(),"Evento borrado de favoritos", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return true;
            case R.id.share_event:
                //create a share intent
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        event.getTitle() +
                                " - " + event.getDescription() +
                                " - " + event.getDtstart());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.add_to_calendar:
                addToCalendar();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addToCalendar() {

        long startMillis;
        long endMillis;

        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(event.getDtstart());
        startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(event.getDtend());
        endMillis = endTime.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getEventLocation());

        String recurString = "FREQ=WEEKLY;COUNT=10;WKST=SU;BYDAY=TU,TH”";
        intent.putExtra(CalendarContract.Events.RRULE, recurString);

        // Making it private and shown as busy
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        startActivity(intent);
    }
}
