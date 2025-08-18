package com.example.myapplication.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Question;
import com.example.myapplication.databinding.ActivityQuestionListBinding;

import java.util.List;

public class QuestionListActivity extends AppCompatActivity {
    private ActivityQuestionListBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();

        super.onCreate(savedInstanceState);
        b = ActivityQuestionListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        AppDatabase db = AppDatabase.get(this);
        List<Question> all = db.questionDao().getAll();

        b.rv.setLayoutManager(new LinearLayoutManager(this));
        b.rv.setAdapter(new QuestionAdapter(db, all));
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int nightMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
