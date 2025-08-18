package com.example.myapplication.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Единственная запись с состоянием серии
@Entity(tableName = "streak_meta")
public class StreakMeta {
    @PrimaryKey
    public int singletonId = 1;

    public int currentStreak;     // текущая серия
    public String lastAwardDate;  // "YYYY-MM-DD" дата, когда последний раз получали очко
}