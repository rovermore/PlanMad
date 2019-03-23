package com.example.rovermore.planmad.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.activities.DetailActivity;
import com.example.rovermore.planmad.activities.MainActivity;
import com.example.rovermore.planmad.adapters.MainAdapter;
import com.example.rovermore.planmad.database.AppDatabase;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.threads.AppExecutors;
import com.example.rovermore.planmad.viewmodel.FavViewModel;

import java.util.List;

public class FavFragment extends Fragment implements MainAdapter.onEventClickListener {

    private static final String TAG = FavFragment.class.getSimpleName();

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter eventListAdapter;
    private OnDataPassFromFavFragment onDataPassFromFavFragment;
    private Parcelable mListState;
    private Event clickedEvent;
    private boolean mTwoPane;

    private AppDatabase mDb;

    public interface OnDataPassFromFavFragment {
        void onDataPassFromFavFragment(Parcelable mListState, Event event);
    }

    public FavFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPassFromFavFragment = (OnDataPassFromFavFragment) context;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav, container, false);

        mDb = AppDatabase.getInstance(getContext());

        recyclerView = rootView.findViewById(R.id.rv_fav_events);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        eventListAdapter = new MainAdapter(getContext(), null, this);

        setUpFavViewModel();

        if(getArguments()!=null) {
            mTwoPane = getArguments().getBoolean(MainActivity.TWO_PANE_KEY);
            mListState = getArguments().getParcelable(MainFragment.LIST_STATE_KEY);
            if (mListState!=null){
                layoutManager.onRestoreInstanceState(mListState);
            }
        }

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Event> events = eventListAdapter.getEventList();
                        mDb.eventDao().deleteEvent(events.get(position));
                    }
                });
                Toast.makeText(getContext(),"Evento borrado de favoritos", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        return rootView;
    }

    private void setUpFavViewModel() {
        FavViewModel favViewModel = ViewModelProviders.of(this).get(FavViewModel.class);
        favViewModel.getEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable List<Event> eventList) {
                Log.d(TAG,"Updating list of events from LiveData in ViewModel");
                if(eventList != null && !eventList.isEmpty()){
                    createUI(eventList);
                    clickedEvent = eventList.get(0);
                    onDataPassFromFavFragment.onDataPassFromFavFragment(mListState, clickedEvent);
                } else {
                    Toast.makeText(getContext(),"No se encontraron eventos favoritos",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUI(List<Event> eventList) {
        eventListAdapter.setEventList(eventList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(eventListAdapter);
    }

    @Override
    public void onEventClicked(Event event) {
        if(mTwoPane) {
            this.clickedEvent = event;
            onDataPassFromFavFragment.onDataPassFromFavFragment(mListState, clickedEvent);
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
        onDataPassFromFavFragment.onDataPassFromFavFragment(mListState, clickedEvent);
    }
}
