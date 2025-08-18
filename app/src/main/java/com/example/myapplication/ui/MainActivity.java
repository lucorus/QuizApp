package com.example.myapplication.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.DailyProgress;
import com.example.myapplication.data.Question;
import com.example.myapplication.data.StreakMeta;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.util.DateUtil;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;
    private AppDatabase db;
    private Question current;

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_THEME = "app_theme"; // MODE_NIGHT_NO или MODE_NIGHT_YES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Загружаем сохранённую тему до setContentView
        loadSavedTheme();

        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        db = AppDatabase.get(this);
        ensureMetaExistsAndMaybeReset();
        ensureTodayProgressExists();
        refreshUI();

        loadRandomQuestion();

        b.btnSubmit.setOnClickListener(v -> checkAnswer());
        b.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddQuestionActivity.class)));
        b.btnList.setOnClickListener(v ->
                startActivity(new Intent(this, QuestionListActivity.class)));

        // Переключение темы
        b.btnToggleTheme.setOnClickListener(v -> toggleTheme());
        updateThemeIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        boolean dark = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        menu.findItem(R.id.action_toggle_theme).setIcon(
                dark ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            boolean dark = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
            if (dark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate();
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putInt("night_mode", AppCompatDelegate.getDefaultNightMode())
                    .apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadSavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    private void saveTheme(int mode) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, mode).apply();
    }

    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveTheme(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveTheme(AppCompatDelegate.MODE_NIGHT_YES);
        }
        updateThemeIcon();
    }

    private void updateThemeIcon() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            b.btnToggleTheme.setImageResource(R.drawable.ic_sun);
        } else {
            b.btnToggleTheme.setImageResource(R.drawable.ic_moon);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureMetaExistsAndMaybeReset();
        ensureTodayProgressExists();
        refreshUI();
        if (current == null) loadRandomQuestion();
        updateThemeIcon();
    }

    private void ensureMetaExistsAndMaybeReset() {
        StreakMeta meta = db.streakMetaDao().get();
        if (meta == null) {
            meta = new StreakMeta();
            meta.currentStreak = 0;
            meta.lastAwardDate = null;
            db.streakMetaDao().insert(meta);
        } else {
            if (DateUtil.missedADay(meta.lastAwardDate)) {
                meta.currentStreak = 0; // сброс серии
                db.streakMetaDao().update(meta);
            }
        }
    }

    private void ensureTodayProgressExists() {
        String today = DateUtil.todayIso();
        DailyProgress dp = db.progressDao().getByDate(today);
        if (dp == null) {
            dp = new DailyProgress();
            dp.dateIso = today;
            dp.correctCount = 0;
            dp.awardGiven = false;
            db.progressDao().insert(dp);
        }
    }

    private void refreshUI() {
        StreakMeta meta = db.streakMetaDao().get();
        DailyProgress dp = db.progressDao().getByDate(DateUtil.todayIso());
        int cnt = dp != null ? dp.correctCount : 0;

        int oldProgress = b.dailyProgressBar.getProgress();
        animateProgressBar(oldProgress, cnt);
        b.tvStreak.setText("Серия: " + (meta != null ? meta.currentStreak : 0));
        b.tvDaily.setText("Сегодня правильных: " + cnt + "/10");
    }

    private void animateProgressBar(int from, int to) {
        ObjectAnimator animation = ObjectAnimator.ofInt(b.dailyProgressBar, "progress", from, to);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    private void loadRandomQuestion() {
        if (db.questionDao().getAll().isEmpty()) {
            Question q = new Question();
            q.text = "Столица Франции?";
            q.answer = "Париж";
            db.questionDao().insert(q);
        }
        current = db.questionDao().getRandom();
        animateQuestionChange(current.text);
        b.tvFeedback.setText("");
        b.etAnswer.setText("");
    }

    private void animateQuestionChange(String newQuestion) {
        b.tvQuestion.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    b.tvQuestion.setText(newQuestion);
                    b.tvQuestion.animate().alpha(1f).setDuration(200).start();
                }).start();
    }

    private void checkAnswer() {
        String userAns = b.etAnswer.getText().toString().trim();
        if (TextUtils.isEmpty(userAns)) return;

        if (userAns.equals(current.answer.trim())) {
            onCorrectAnswer();
            showFeedback("Верно!", true);
        } else {
            onWrongAnswer();
            showFeedback("Неверно. Правильный ответ: " + current.answer, false);
        }
        loadRandomQuestion();
    }

    private void showFeedback(String text, boolean correct) {
        b.tvFeedback.setText(text);
        b.tvFeedback.setTextColor(correct ? 0xFF006400 : 0xFF8B0000);

        b.tvFeedback.setAlpha(0f);
        b.tvFeedback.animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction(() -> b.tvFeedback.animate()
                        .alpha(1f)
                        .setDuration(1200) // держим дольше
                        .withEndAction(() -> b.tvFeedback.animate()
                                .alpha(0f)
                                .setDuration(500)))
                .start();
    }

    private void onCorrectAnswer() {
        String today = DateUtil.todayIso();
        DailyProgress dp = db.progressDao().getByDate(today);
        if (dp == null) return;
        if (dp.awardGiven) {
            dp.correctCount += 1;
            db.progressDao().update(dp);
            refreshUI();
            return;
        }

        dp.correctCount += 1;
        db.progressDao().update(dp);

        if (dp.correctCount >= 10) {
            StreakMeta meta = db.streakMetaDao().get();
            int oldStreak = meta.currentStreak;

            meta.currentStreak = (DateUtil.alreadyAwardedToday(meta.lastAwardDate)) ?
                    meta.currentStreak : meta.currentStreak + 1;
            meta.lastAwardDate = today;
            db.streakMetaDao().update(meta);
            dp.awardGiven = true;
            db.progressDao().update(dp);

            if (meta.currentStreak > oldStreak) {
                highlightStreak();
            }
        }

        animateCardColor(R.color.correctGreen);
        refreshUI();
    }

    private void onWrongAnswer() {
        animateCardColor(R.color.wrongRed);
        refreshUI();
    }

    private void animateCardColor(int targetColorRes) {
        int startColor = ((ColorDrawable) b.questionCard.getBackground()).getColor();
        int targetColor = ContextCompat.getColor(this, targetColorRes);
        int whiteColor = ContextCompat.getColor(this, android.R.color.white);

        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, targetColor, whiteColor);
        colorAnim.setDuration(800);
        colorAnim.addUpdateListener(animator ->
                b.questionCard.setCardBackgroundColor((int) animator.getAnimatedValue())
        );
        colorAnim.start();
    }

    private void highlightStreak() {
        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(),
                getResources().getColor(R.color.purple_700),
                getResources().getColor(R.color.teal_200),
                getResources().getColor(R.color.purple_700));
        colorAnim.setDuration(800);
        colorAnim.addUpdateListener(animator ->
                b.tvStreak.setTextColor((int) animator.getAnimatedValue()));
        colorAnim.start();

        b.tvStreak.animate()
                .scaleX(1.2f).scaleY(1.2f)
                .setDuration(200)
                .withEndAction(() -> b.tvStreak.animate()
                        .scaleX(1f).scaleY(1f).setDuration(200))
                .start();
    }
}
