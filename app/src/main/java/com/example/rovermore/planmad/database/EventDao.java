package com.example.rovermore.planmad.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.rovermore.planmad.datamodel.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM favEvents")
    LiveData<List<Event>> loadAllMovies();

    @Insert
    void insertEvent(Event Event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEvent(Event Event);

    @Delete
    void deleteEvent(Event Event);

    @Query("DELETE FROM favEvents WHERE id = :id")
    int deleteBydbId(int id);

    @Query("SELECT * FROM favEvents WHERE id = :id")
    LiveData<Event> loadEventById(int id);

}
