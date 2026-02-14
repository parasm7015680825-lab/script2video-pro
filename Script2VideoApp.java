package com.paras.generatedapp;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;

public class Script2VideoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {});
    }
}
