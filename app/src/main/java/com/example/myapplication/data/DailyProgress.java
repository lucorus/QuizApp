package com.example.myapplication.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Храним прогресс по датам: сколько правильных ответов и выдано ли очко
@Entity(tableName = "daily_progress")
public class DailyProgress {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String dateIso;     // "YYYY-MM-DD" (LocalDate.toString())
    public int correctCount;   // правильных за этот день
    public boolean awardGiven; // сегодня очко уже выдано?
}