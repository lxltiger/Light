package com.kimascend.light.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kimascend.light.home.entity.Group;
import com.kimascend.light.scene.Scene;

import java.util.List;

@Dao
public interface SceneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Scene scene);


    @Query("select * from scene")
    LiveData<List<Scene>> loadAllScene();


    @Delete
    void delete(Scene scene);

    @Query("delete from scene")
    void deleteAllScenes();


    @Query("select  max(sceneId)  from scene ")
    int getMaxSceneId();

}
