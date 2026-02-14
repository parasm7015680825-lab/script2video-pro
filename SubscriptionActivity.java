package com.paras.generatedapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paras.generatedapp.R;
import com.paras.generatedapp.util.SubscriptionManager;

public class SubscriptionActivity extends AppCompatActivity {

    private TextView txtStatus;
    private Button btnWeekly;
    private Button btnMonthly;
    private Button btnQuarterly;

    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        txtStatus = findViewById(R.id.txtSubStatus);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);
        btnQuarterly = findViewById(R.id.btnQuarterly);

        subscriptionManager = new SubscriptionManager(this);

        updateStatus();

        btnWeekly.setOnClickListener(v -> subscriptionManager.mockPurchaseWeekly(this::updateStatus));
        btnMonthly.setOnClickListener(v -> subscriptionManager.mockPurchaseMonthly(this::updateStatus));
        btnQuarterly.setOnClickListener(v -> subscriptionManager.mockPurchaseQuarterly(this::updateStatus));
    }

    private void updateStatus() {
        boolean isPremium = subscriptionManager.isPremium();
        if (isPremium) {
            txtStatus.setText(R.string.sub_active);
        } else {
            txtStatus.setText(R.string.sub_not_active);
        }
    }
}
