package com.example.myapplication.util;

import java.time.LocalDate;

public class DateUtil {
    public static String todayIso() {
        return LocalDate.now().toString(); // YYYY-MM-DD
    }

    public static boolean missedADay(String lastAwardDateIso) {
        if (lastAwardDateIso == null || lastAwardDateIso.isEmpty()) return false;
        LocalDate last = LocalDate.parse(lastAwardDateIso);
        LocalDate today = LocalDate.now();
        // Если между last и today >= 2 дней, значит пропуск хотя бы одного полного дня
        return today.isAfter(last.plusDays(1));
    }

    public static boolean alreadyAwardedToday(String lastAwardDateIso) {
        if (lastAwardDateIso == null) return false;
        return LocalDate.now().toString().equals(lastAwardDateIso);
    }

    public static boolean isNextDay(String lastAwardDateIso) {
        if (lastAwardDateIso == null) return true; // серия с нуля
        return LocalDate.now().equals(LocalDate.parse(lastAwardDateIso).plusDays(1));
    }
}