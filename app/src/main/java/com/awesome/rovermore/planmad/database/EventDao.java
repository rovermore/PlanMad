package com.awesome.rovermore.planmad.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.awesome.rovermore.planmad.datamodel.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM favEvents ORDER BY dtstart ASC")
    LiveData<List<Event>> loadAllEvents();

    @Insert
    void insertEvent(Event Event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEvent(Event Event);

    @Delete
    void deleteEvent(Event Event);

    @Query("DELETE FROM favEvents WHERE idDatabase = :idDatabase")
    int deleteBydbId(int idDatabase);

    @Query("SELECT * FROM favEvents WHERE idDatabase = :idDatabase")
    LiveData<Event> loadEventById(int idDatabase);

    @Query("SELECT * FROM favEvents WHERE hash = :hash")
    Event loadEventByHash(int hash);
}
