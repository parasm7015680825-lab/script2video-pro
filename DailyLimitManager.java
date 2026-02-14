package com.paras.generatedapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class DailyLimitManager {

    public static final int FREE_MAX_PER_DAY = 2;
    public static final int PREMIUM_MAX_PER_DAY = 5;

    private static final String PREF_NAME = "daily_limits";
    private static final String KEY_LAST_RESET = "last_reset";
    private static final String KEY_COUNT = "count";

    private final SharedPreferences prefs;

    public DailyLimitManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        checkReset();
    }

    private void checkReset() {
        long lastReset = prefs.getLong(KEY_LAST_RESET, 0);
        long now = System.currentTimeMillis();
        if (now - lastReset >= 24L * 60L * 60L * 1000L) {
            prefs.edit().putInt(KEY_COUNT, 0).putLong(KEY_LAST_RESET, now).apply();
        }
    }

    public int getTodayCount() {
        checkReset();
        return prefs.getInt(KEY_COUNT, 0);
    }

    public void incrementCount() {
        checkReset();
        int c = prefs.getInt(KEY_COUNT, 0) + 1;
        prefs.edit().putInt(KEY_COUNT, c).apply();
    }
}
