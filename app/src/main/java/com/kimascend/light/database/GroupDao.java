package com.kimascend.light.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kimascend.light.home.entity.Group;
import com.kimascend.light.mesh.Mesh;
import com.kimascend.light.scene.Scene;
import com.kimascend.light.user.Profile;

import java.util.List;

@Dao
public interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Group group);


    @Query("select * from 'group'")
    LiveData<List<Group>> loadAllGroups();


    @Delete
    void deleteGroup(Group group);

    @Query("delete from `group`")
    void deleteAllGroups();

    @Query("select count(*) from `group` ")
    int getGroupNum();

    @Query("select  max(groupId)  from 'group' ")
    int getGroupRowId();

}
