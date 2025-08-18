package com.example.myapplication.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ProgressDao {
    @Query("SELECT * FROM daily_progress WHERE dateIso = :date LIMIT 1")
    DailyProgress getByDate(String date);

    @Insert
    long insert(DailyProgress dp);

    @Update
    void update(DailyProgress dp);
}