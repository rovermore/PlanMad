package com.example.rovermore.planmad.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.fragments.DetailFragment;
import com.example.rovermore.planmad.fragments.FavFragment;
import com.example.rovermore.planmad.fragments.MainFragment;
import com.example.rovermore.planmad.fragments.TodayFragment;
import com.example.rovermore.planmad.network.NetworkUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainFragment.OnDataPass,
        FavFragment.OnDataPassFromFavFragment,
        TodayFragment.OnDataPassFromTodayFragment,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private final String TAG = MainActivity.class.getName();

    private final static int ASYNC_TASK_INT = 103;

    public static final String TWO_PANE_KEY = "two-pane";
    public static final String DETAIL_FRAGMENT_ID = "detail-fragment";

    public static final String MONTH_POSITION_KEY = "month-position";

    private final String MAIN_LIST_STATE_KEY = "main-list-state";
    private final String FAV_LIST_STATE_KEY = "fav-list-state";
    private final String TODAY_LIST_STATE_KEY = "today-list-state";

    private final String MAIN_FRAGMENT_TAG = "MAIN-FRAGMENT";
    private final String TODAY_EVENT_FRAGMENT_TAG = "TODAY-FRAGMENT";

    private final String MAIN_EVENT = "main-event";
    private final String FAV_EVENT = "fav-event";
    private final String TODAY_EVENT = "today-event";

    private final String FRAGMENT_KEY = "fragment-key";

    private final int MAIN_FRAGMENT = 1;
    private final int FAV_FRAGMENT = 2;
    private final int TODAY_FRAGMENT = 3;
    private final int MAP_FRAGMENT = 4;

    private View linearLayoutMapView;
    private View placeHolderImage;
    private TextView welcomeTextView;

    private FragmentManager fragmentManager;
    private Parcelable mListState, mFavListState, mTodayListState;
    //lists for fetching in map fragment
    private List<Event> eventList, todayEventList;
    //events for tablet version
    private Event mainEvent, favEvent, todayEvent;
    private boolean isDataPassedFromMainFragment = false;
    private boolean isDataPassedFromFavFragment = false;
    private boolean isDataPassedFromTodayFragment = false;
    private boolean isSavedState = false;
    private int currentFragment;
    private int monthPosition;

    private MainFragment mainFragment;
    private TodayFragment todayFragment;
    private DetailFragment detailFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setUpHomeFragment();
                    currentFragment = MAIN_FRAGMENT;
                    return true;
                case R.id.navigation_fav:
                    setUpFavFragment();
                    currentFragment = FAV_FRAGMENT;
                    return true;
                case R.id.navigation_today:
                    setUpTodayFragment();
                    currentFragment = TODAY_FRAGMENT;
                    return true;
                case R.id.navigation_map:
                    if (eventList == null) {
                        if(NetworkUtils.isInternetAvailable(getApplicationContext())){
                        placeHolderImage.setVisibility(View.VISIBLE);
                        new FetchEvents().execute(ASYNC_TASK_INT);
                        } else {
                            Toast.makeText(getApplicationContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        setUpMapFragment();
                        currentFragment = MAP_FRAGMENT;
                    }
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null){
            mListState = savedInstanceState.getParcelable(MAIN_LIST_STATE_KEY);
            mFavListState = savedInstanceState.getParcelable(FAV_LIST_STATE_KEY);
            mTodayListState = savedInstanceState.getParcelable(TODAY_LIST_STATE_KEY);

            mainEvent = savedInstanceState.getParcelable(MAIN_EVENT);
            favEvent = savedInstanceState.getParcelable(FAV_EVENT);
            todayEvent = savedInstanceState.getParcelable(TODAY_EVENT);

            currentFragment = savedInstanceState.getInt(FRAGMENT_KEY);

            monthPosition = savedInstanceState.getInt(MONTH_POSITION_KEY);

            isSavedState = true;
        }

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_bar);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        linearLayoutMapView = (View) findViewById(R.id.map_linear_layout);

        placeHolderImage = (View) findViewById(R.id.place_holder_image);
        placeHolderImage.setVisibility(View.VISIBLE);

        String notificationFragment = getIntent().getStringExtra("NotificationFragment");

        //Checks if activity is started by a widget notification
        //and sets the corresponding fragment
        if (notificationFragment != null) {
            if (notificationFragment.equals("todayNotificationFragment")) {
                setUpTodayFragment();
            }
        } else if (isSavedState) {
            switch (currentFragment){
                case MAIN_FRAGMENT:
                    setUpHomeFragment();
                    break;
                case FAV_FRAGMENT:
                    setUpFavFragment();
                    break;
                case TODAY_FRAGMENT:
                    setUpTodayFragment();
                    break;
                case MAP_FRAGMENT:
                    setUpMapFragment();
                    break;
            }
        } else {
            if(NetworkUtils.isInternetAvailable(getApplicationContext())){
                setUpHomeFragment();
                currentFragment = MAIN_FRAGMENT;
            } else {
                welcomeTextView = (TextView) findViewById(R.id.tv_welcome_message);
                welcomeTextView.setText(R.string.network_error);
            }
        }
    }

    private void setUpHomeFragment() {
        linearLayoutMapView.setVisibility(View.INVISIBLE);
        setTitle(R.string.title_top_home);
        Fragment fragment =  fragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);
        if (fragment!=null){
            mainFragment = (MainFragment) fragment;
            Bundle bundle = new Bundle();
            bundle.putInt(MONTH_POSITION_KEY,monthPosition);
            if (findViewById(R.id.two_pane_linear_layout) != null) {
                bundle.putBoolean(TWO_PANE_KEY, true);
                if (isDataPassedFromMainFragment || isSavedState) {
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mListState);
                    //bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) eventList);
                    bundle.putParcelable(MainFragment.EVENT_KEY_NAME, mainEvent);
                }
                mainFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.two_pane_main_layout, mainFragment,MAIN_FRAGMENT_TAG)
                        .addToBackStack(MAIN_FRAGMENT_TAG)
                        .commit();
            } else {
                if (isDataPassedFromMainFragment || isSavedState) {
                    //Bundle bundle = new Bundle();
                    bundle.putBoolean(TWO_PANE_KEY, false);
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mListState);
                    //bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) eventList);
                    mainFragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, mainFragment, MAIN_FRAGMENT_TAG)
                        .addToBackStack(MAIN_FRAGMENT_TAG)
                        .commit();

            }
        } else {
            mainFragment = new MainFragment();
            Bundle bundle = new Bundle();

            if (findViewById(R.id.two_pane_linear_layout) != null) {
                bundle.putBoolean(TWO_PANE_KEY, true);
                if (isDataPassedFromMainFragment || isSavedState) {
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mListState);
                    //bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) eventList);
                    bundle.putParcelable(MainFragment.EVENT_KEY_NAME, mainEvent);
                }
                mainFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.two_pane_main_layout, mainFragment,MAIN_FRAGMENT_TAG)
                        .addToBackStack(MAIN_FRAGMENT_TAG)
                        .commit();
            } else {
                if (isDataPassedFromMainFragment || isSavedState) {
                    //Bundle bundle = new Bundle();
                    bundle.putBoolean(TWO_PANE_KEY, false);
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mListState);
                    //bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) eventList);
                    mainFragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, mainFragment, MAIN_FRAGMENT_TAG)
                        .addToBackStack(MAIN_FRAGMENT_TAG)
                        .commit();
            }
        }
    }


    private void setUpFavFragment() {
        linearLayoutMapView.setVisibility(View.INVISIBLE);
        setTitle(R.string.title_top_fav);
        FavFragment favFragment = new FavFragment();
        Bundle bundle = new Bundle();
        if (findViewById(R.id.two_pane_linear_layout) != null) {
            bundle.putBoolean(TWO_PANE_KEY, true);
            if (isDataPassedFromFavFragment || isSavedState) {
                bundle.putBoolean(TWO_PANE_KEY, true);
                bundle.putParcelable(MainFragment.LIST_STATE_KEY, mFavListState);
                bundle.putParcelable(MainFragment.EVENT_KEY_NAME, favEvent);
            }
            favFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.two_pane_main_layout, favFragment)
                    .commit();
        } else {
            bundle.putBoolean(TWO_PANE_KEY, false);
            if (isDataPassedFromFavFragment || isSavedState) {
                bundle.putParcelable(MainFragment.LIST_STATE_KEY, mFavListState);
                favFragment.setArguments(bundle);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, favFragment)
                    .commit();
        }
    }


    private void setUpTodayFragment() {
        linearLayoutMapView.setVisibility(View.INVISIBLE);
        setTitle(R.string.title_top_today);
        Fragment fragment = fragmentManager.findFragmentByTag(TODAY_EVENT_FRAGMENT_TAG);
        if (fragment != null) {
            todayFragment = (TodayFragment) fragment;
            Bundle bundle = new Bundle();
            if (findViewById(R.id.two_pane_linear_layout) != null) {
                bundle.putBoolean(TWO_PANE_KEY, true);
                if (isDataPassedFromTodayFragment || isSavedState) {
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mTodayListState);
                    bundle.putParcelable(MainFragment.EVENT_KEY_NAME, todayEvent);
                }
                todayFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.two_pane_main_layout, todayFragment,TODAY_EVENT_FRAGMENT_TAG)
                        .commit();
            } else {
                bundle.putBoolean(TWO_PANE_KEY, false);
                if (isDataPassedFromTodayFragment || isSavedState) {
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mTodayListState);
                    todayFragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, todayFragment,TODAY_EVENT_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            todayFragment = new TodayFragment();
            Bundle bundle = new Bundle();
            if (findViewById(R.id.two_pane_linear_layout) != null) {
                bundle.putBoolean(TWO_PANE_KEY, true);
                if (isDataPassedFromTodayFragment || isSavedState) {
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mTodayListState);
                    bundle.putParcelable(MainFragment.EVENT_KEY_NAME, todayEvent);
                }
                todayFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.two_pane_main_layout, todayFragment,TODAY_EVENT_FRAGMENT_TAG)
                        .commit();
            } else {
                bundle.putBoolean(TWO_PANE_KEY, false);
                if (isDataPassedFromTodayFragment || isSavedState) {
                    bundle.putParcelable(MainFragment.LIST_STATE_KEY, mTodayListState);
                    todayFragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, todayFragment,TODAY_EVENT_FRAGMENT_TAG)
                        .commit();
            }
        }

    }

    private void setUpMapFragment() {
        //linearLayoutMapView.setVisibility(View.VISIBLE);
        placeHolderImage.setVisibility(View.VISIBLE);
        setTitle(R.string.title_top_map);
        if (findViewById(R.id.two_pane_linear_layout) != null){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.two_pane_big_map);
            mapFragment.getMapAsync(this);
        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.big_map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onDataPass(Parcelable mListState, Event event, int monthPosition) {
        this.mListState = mListState;
        this.monthPosition = monthPosition;
        placeHolderImage.setVisibility(View.GONE);
        if (event != null && findViewById(R.id.two_pane_linear_layout) != null) {
            this.mainEvent = event;
            setUpTwoPaneDetailFragment(mainEvent);
        }
        isDataPassedFromMainFragment = true;
    }

    @Override
    public void onDataPassFromFavFragment(Parcelable mListState, Event event) {
        this.mFavListState = mListState;
        if (event != null && findViewById(R.id.two_pane_linear_layout) != null) {
            this.favEvent = event;
            setUpTwoPaneDetailFragment(favEvent);
        }
        isDataPassedFromFavFragment = true;
    }

    @Override
    public void onDataPassFromTodayFragment(Parcelable mListState, Event event) {
        this.mTodayListState = mListState;
        if (event != null && findViewById(R.id.two_pane_linear_layout) != null) {
            this.todayEvent = event;
            setUpTwoPaneDetailFragment(todayEvent);
        }
        isDataPassedFromTodayFragment = true;
    }

    private void setUpTwoPaneDetailFragment(Event event){
        detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainFragment.EVENT_KEY_NAME, event);

        detailFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.two_pane_detail_frame_layout, detailFragment, DETAIL_FRAGMENT_ID)
                .commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (todayEventList == null) {
            todayEventList = NetworkUtils.getTodayList(eventList);
        }
        if (todayEventList != null) {
            for (int i = 0; i < todayEventList.size(); i++) {
                Event event = todayEventList.get(i);
                LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(eventLocation)
                        .title(event.getTitle()));
                marker.setTag(event);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 10.0f));
            }
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);

            googleMap.setOnInfoWindowClickListener(this);
        }
        placeHolderImage.setVisibility(View.GONE);
        linearLayoutMapView.setVisibility(View.VISIBLE);
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        Event event = (Event) marker.getTag();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MainFragment.EVENT_KEY_NAME, event);
        startActivity(intent);
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
            eventList = events;
            setUpMapFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving the state of each fragment
        outState.putParcelable(MAIN_LIST_STATE_KEY, mListState);
        outState.putParcelable(MAIN_EVENT, mainEvent);
        outState.putInt(FRAGMENT_KEY, currentFragment);
        outState.putInt(MONTH_POSITION_KEY, monthPosition);

        outState.putParcelable(FAV_LIST_STATE_KEY, mFavListState);
        outState.putParcelable(FAV_EVENT, favEvent);
        outState.putInt(FRAGMENT_KEY, currentFragment);

        outState.putParcelable(TODAY_LIST_STATE_KEY, mTodayListState);
        outState.putParcelable(TODAY_EVENT, todayEvent);
        outState.putInt(FRAGMENT_KEY, currentFragment);

        outState.putInt(FRAGMENT_KEY, currentFragment);
    }
}
