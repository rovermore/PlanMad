package com.example.rovermore.planmad.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.fragments.FavFragment;
import com.example.rovermore.planmad.fragments.MainFragment;
import com.example.rovermore.planmad.fragments.TodayFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainFragment.OnDataPass,
        FavFragment.OnDataPassFromFavFragment,
        TodayFragment.OnDataPassFromTodayFragment{

    private TextView mTextMessage;

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
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUpHomeFragment();
    }

    private void setUpHomeFragment() {
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
        TodayFragment todayFragment = new TodayFragment();
        if (isDataPassedFromTodayFragment){
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainFragment.LIST_STATE_KEY, mTodayListState);
            bundle.putParcelableArrayList(MainFragment.EVENT_LIST_KEY, (ArrayList<? extends Parcelable>) todayEventList);
            todayFragment.setArguments(bundle);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, todayFragment)
                .commit();
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
}
