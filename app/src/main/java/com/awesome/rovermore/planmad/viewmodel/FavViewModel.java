package com.awesome.rovermore.planmad.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.awesome.rovermore.planmad.database.AppDatabase;
import com.awesome.rovermore.planmad.datamodel.Event;

import java.util.List;

public class FavViewModel extends AndroidViewModel {

    private static final String TAG = FavViewModel.class.getSimpleName();

    private LiveData<List<Event>> events;

    public FavViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        events = database.eventDao().loadAllEvents();
    }

    public LiveData<List<Event>> getEvents(){return events;}

}
