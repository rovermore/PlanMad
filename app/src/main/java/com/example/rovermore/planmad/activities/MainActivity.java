package com.example.rovermore.planmad.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.datamodel.Event;
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

    private final static int ASYNC_TASK_INT = 103;

    private View linearLayoutMapView;
    private FragmentManager fragmentManager;
    private Parcelable mListState, mFavListState, mTodayListState;
    private List<Event> eventList, todayEventList;
    private boolean isDataPassedFromMainFragment = false;
    private boolean isDataPassedFromFavFragment = false;
    private boolean isDataPassedFromTodayFragment = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setUpHomeFragment();
                    return true;
                case R.id.navigation_fav:
                    setUpFavFragment();
                    return true;
                case R.id.navigation_today:
                    setUpTodayFragment();
                    return true;
                case R.id.navigation_map:
                    if (eventList == null){
                        new FetchEvents().execute(ASYNC_TASK_INT);
                    } else {
                        setUpMapFragment();
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

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        linearLayoutMapView = (View) findViewById(R.id.map_linear_layout);

        setUpHomeFragment();
    }

    private void setUpHomeFragment() {
        linearLayoutMapView.setVisibility(View.INVISIBLE);
        MainFragment mainFragment = new MainFragment();
        if (isDataPassedFromMainFragment) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainFragment.LIST_STATE_KEY, mListState);
            bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) eventList);
            mainFragment.setArguments(bundle);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, mainFragment)
                .commit();
    }

    private void setUpFavFragment(){
        linearLayoutMapView.setVisibility(View.INVISIBLE);
        FavFragment favFragment = new FavFragment();
        if (isDataPassedFromFavFragment){
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainFragment.LIST_STATE_KEY, mFavListState);
            favFragment.setArguments(bundle);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, favFragment)
                .commit();
    }

    private void setUpTodayFragment(){
        linearLayoutMapView.setVisibility(View.INVISIBLE);
        TodayFragment todayFragment = new TodayFragment();
        if (isDataPassedFromTodayFragment || todayEventList!=null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainFragment.LIST_STATE_KEY, mTodayListState);
            bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) todayEventList);
            todayFragment.setArguments(bundle);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, todayFragment)
                .commit();
    }

    private void setUpMapFragment() {
        linearLayoutMapView.setVisibility(View.VISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.big_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onDataPass(Parcelable mListState, List<Event> eventList) {
        this.mListState = mListState;
        this.eventList = eventList;
        isDataPassedFromMainFragment = true;
    }

    @Override
    public void onDataPassFromFavFragment(Parcelable mListState) {
        this.mFavListState = mListState;
        isDataPassedFromFavFragment = true;
    }

    @Override
    public void onDataPassFromTodayFragment(Parcelable mListState, List<Event> eventList) {
        this.mTodayListState = mListState;
        this.todayEventList = eventList;
        isDataPassedFromTodayFragment = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for(int i = 0;i<todayEventList.size();i++) {
            Event event = todayEventList.get(i);
            LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(eventLocation)
                    .title(event.getTitle()));
            marker.setTag(event);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation,10.0f));
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);

        googleMap.setOnInfoWindowClickListener(this);

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
            todayEventList = events;
            setUpMapFragment();
        }
    }
}
