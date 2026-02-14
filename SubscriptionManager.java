package com.paras.generatedapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SubscriptionManager {

    private static final String PREF_NAME = "subscription_prefs";
    private static final String KEY_PREMIUM = "is_premium";

    private final SharedPreferences prefs;

    public interface StatusCallback {
        void onUpdated();
    }

    public SubscriptionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isPremium() {
        return prefs.getBoolean(KEY_PREMIUM, false);
    }

    // Mock purchases. In real app, integrate BillingClient here.
    public void mockPurchaseWeekly(StatusCallback callback) {
        setPremium(true);
        if (callback != null) callback.onUpdated();
    }

    public void mockPurchaseMonthly(StatusCallback callback) {
        setPremium(true);
        if (callback != null) callback.onUpdated();
    }

    public void mockPurchaseQuarterly(StatusCallback callback) {
        setPremium(true);
        if (callback != null) callback.onUpdated();
    }

    private void setPremium(boolean value) {
        prefs.edit().putBoolean(KEY_PREMIUM, value).apply();
    }
}
