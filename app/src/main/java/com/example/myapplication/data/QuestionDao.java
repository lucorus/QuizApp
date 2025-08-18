package com.example.myapplication.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    long insert(Question q);

    @Delete
    void delete(Question q);

    @Query("SELECT * FROM questions ORDER BY id DESC")
    List<Question> getAll();

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT 1")
    Question getRandom();
}