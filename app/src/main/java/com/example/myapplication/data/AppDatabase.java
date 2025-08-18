package com.example.myapplication.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {Question.class, DailyProgress.class, StreakMeta.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract QuestionDao questionDao();
    public abstract ProgressDao progressDao();
    public abstract StreakMetaDao streakMetaDao();

    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    "streak_quiz.db"
                            ).allowMainThreadQueries() // для простоты демо; в проде — через background/Coroutines
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}