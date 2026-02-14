package com.paras.generatedapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paras.generatedapp.R;
import com.paras.generatedapp.util.DailyLimitManager;
import com.paras.generatedapp.util.SubscriptionManager;

public class MainActivity extends AppCompatActivity {

    private TextView txtStatus;
    private Button btnCreateVideo;
    private Button btnGoPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        btnCreateVideo = findViewById(R.id.btnCreateVideo);
        btnGoPremium = findViewById(R.id.btnGoPremium);

        updateStatus();

        btnCreateVideo.setOnClickListener(v -> {
            DailyLimitManager limitManager = new DailyLimitManager(this);
            SubscriptionManager subscriptionManager = new SubscriptionManager(this);
            boolean isPremium = subscriptionManager.isPremium();

            int used = limitManager.getTodayCount();
            int max = isPremium ? DailyLimitManager.PREMIUM_MAX_PER_DAY : DailyLimitManager.FREE_MAX_PER_DAY;
            if (used >= max) {
                txtStatus.setText(getString(R.string.daily_limit_reached, max));
                return;
            }

            Intent intent = new Intent(MainActivity.this, ScriptInputActivity.class);
            startActivity(intent);
        });

        btnGoPremium.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubscriptionActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        DailyLimitManager limitManager = new DailyLimitManager(this);
        SubscriptionManager subscriptionManager = new SubscriptionManager(this);
        boolean isPremium = subscriptionManager.isPremium();
        int used = limitManager.getTodayCount();
        int max = isPremium ? DailyLimitManager.PREMIUM_MAX_PER_DAY : DailyLimitManager.FREE_MAX_PER_DAY;
        String type = isPremium ? getString(R.string.premium_user) : getString(R.string.free_user);
        String text = getString(R.string.status_template, type, used, max);
        txtStatus.setText(text);
    }
}
