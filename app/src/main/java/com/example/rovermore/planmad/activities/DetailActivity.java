package com.example.rovermore.planmad.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.fragments.DetailFragment;
import com.example.rovermore.planmad.fragments.MainFragment;

public class DetailActivity extends AppCompatActivity {

    private Event event;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        event = getIntent().getParcelableExtra(MainFragment.EVENT_KEY_NAME);

        fragmentManager = getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putParcelable(MainFragment.EVENT_KEY_NAME, event);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.detail_frame_layout, detailFragment)
                .commit();
    }
}
