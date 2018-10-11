package com.kimascend.light.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kimascend.light.clock.Clock;
import com.kimascend.light.scene.Scene;

import java.util.List;

@Dao
public interface ClockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Clock clock);


    @Query("select * from clock")
    LiveData<List<Clock>> loadAllClock();


    @Delete
    void delete(Clock clock);

    @Query("delete from clock")
    void deleteAllClock();


}
