package com.rvm.rovermore.planmad;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnitRunner;
import android.support.v4.app.FragmentManager;

import com.rvm.rovermore.planmad.activities.DetailActivity;
import com.rvm.rovermore.planmad.activities.MainActivity;
import com.rvm.rovermore.planmad.fragments.TodayFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

public class TodayFragmentTest extends AndroidJUnitRunner {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void testIntentWhenClickRecyclerViewItemMainFragment(){

        FragmentManager fragmentManager = mActivityRule.getActivity().getSupportFragmentManager();
        TodayFragment todayFragment = new TodayFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.TWO_PANE_KEY, false);
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, todayFragment, MainActivity.TODAY_EVENT_FRAGMENT_TAG)
                .commit();

        // perform click on view at 5th position in RecyclerView
        onView(withId(R.id.rv_today_events))
                .perform(RecyclerViewActions.actionOnItemAtPosition(5, click()));

        intended(hasComponent(DetailActivity.class.getName()));
    }
}