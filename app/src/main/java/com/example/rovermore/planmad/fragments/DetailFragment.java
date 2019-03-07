package com.example.rovermore.planmad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rovermore.planmad.AppExecutors;
import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.database.AppDatabase;
import com.example.rovermore.planmad.datamodel.Event;


public class DetailFragment extends Fragment {

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
        } else {
            event = null;
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

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: SAVE EVENT IN DATABASE
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.eventDao().insertEvent(event);

                    }
                });

                Toast.makeText(getContext(),"Event saved in favorites",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }




}
