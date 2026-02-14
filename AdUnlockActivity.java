package com.paras.generatedapp.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.paras.generatedapp.R;
import com.paras.generatedapp.util.SubscriptionManager;

public class AdUnlockActivity extends AppCompatActivity {

    private TextView txtInfo;
    private Button btnWatchAd;
    private Button btnSkipAd;

    private RewardedAd rewardedAd;
    private boolean rewardEarned = false;

    private String script;
    private int durationMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_unlock);

        txtInfo = findViewById(R.id.txtInfo);
        btnWatchAd = findViewById(R.id.btnWatchAd);
        btnSkipAd = findViewById(R.id.btnSkipAd);

        script = getIntent().getStringExtra("script");
        durationMinutes = getIntent().getIntExtra("duration", 5);

        SubscriptionManager subscriptionManager = new SubscriptionManager(this);
        if (subscriptionManager.isPremium()) {
            // Premium: no ads, go directly
            goToRendering(false);
            return;
        }

        if (isNetworkAvailable()) {
            loadRewardAd();
        } else {
            txtInfo.setText(R.string.no_internet_ad_info);
        }

        btnWatchAd.setOnClickListener(v -> {
            if (rewardedAd != null) {
                rewardedAd.show(AdUnlockActivity.this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(RewardItem rewardItem) {
                        rewardEarned = true;
                    }
                });
            } else {
                txtInfo.setText(R.string.ad_not_ready);
            }
        });

        btnSkipAd.setOnClickListener(v -> {
            // Free user cannot skip; require ad when internet is available, but
            // if ads are not available due to network, allow continuation.
            if (isNetworkAvailable() && rewardedAd != null && !rewardEarned) {
                txtInfo.setText(R.string.must_watch_ad);
            } else {
                goToRendering(true);
            }
        });
    }

    private void loadRewardAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.admob_reward_id), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                rewardedAd = null;
                txtInfo.setText(R.string.ad_load_failed);
            }

            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
                txtInfo.setText(R.string.ad_ready_info);
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        if (rewardEarned) {
                            goToRendering(true);
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        goToRendering(true);
                    }
                });
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
    }

    private void goToRendering(boolean watermark) {
        Intent intent = new Intent(AdUnlockActivity.this, RenderingActivity.class);
        intent.putExtra("script", script);
        intent.putExtra("duration", durationMinutes);
        intent.putExtra("watermark", watermark);
        startActivity(intent);
        finish();
    }
}
