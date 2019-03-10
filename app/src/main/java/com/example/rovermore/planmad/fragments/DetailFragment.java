package com.example.rovermore.planmad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.database.AppDatabase;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.threads.AppExecutors;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDb = AppDatabase.getInstance(getContext());

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
                    Log.d(TAG,"EL ID EN BBDD DEL EVENTO ES " + eventFromDatabase.getIdDatabase());
                } else {
                    isEventSavedInFav = false;
                }
            }
        });

    }


}
