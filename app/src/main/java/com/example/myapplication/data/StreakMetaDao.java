package com.example.myapplication.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StreakMetaDao {
    @Query("SELECT * FROM streak_meta WHERE singletonId = 1")
    StreakMeta get();

    @Insert
    void insert(StreakMeta m);

    @Update
    void update(StreakMeta m);
}