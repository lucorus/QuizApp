package com.example.myapplication.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Question;
import com.example.myapplication.databinding.ActivityAddQuestionBinding;

public class AddQuestionActivity extends AppCompatActivity {
    private ActivityAddQuestionBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();

        super.onCreate(savedInstanceState);
        b = ActivityAddQuestionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnSave.setOnClickListener(v -> {
            String q = b.etQ.getText().toString().trim();
            String a = b.etA.getText().toString().trim();
            if (TextUtils.isEmpty(q) || TextUtils.isEmpty(a)) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }
            Question entity = new Question();
            entity.text = q;
            entity.answer = a;
            AppDatabase.get(this).questionDao().insert(entity);
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int nightMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
