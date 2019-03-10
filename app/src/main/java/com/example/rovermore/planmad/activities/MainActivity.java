package com.example.rovermore.planmad.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.fragments.FavFragment;
import com.example.rovermore.planmad.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private FragmentManager fragmentManager;

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
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, mainFragment)
                .commit();
    }

    private void setUpFavFragment(){
        FavFragment favFragment = new FavFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, favFragment)
                .commit();
    }

}
