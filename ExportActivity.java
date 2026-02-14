package com.paras.generatedapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

import java.io.File;

public class ExportActivity extends AppCompatActivity {

    private TextView txtSummary;
    private Button btnShare;
    private Button btnRemoveWatermark;

    private String videoPath;
    private boolean watermark;

    private RewardedAd rewardedAd;
    private boolean rewardEarned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        txtSummary = findViewById(R.id.txtSummary);
        btnShare = findViewById(R.id.btnShare);
        btnRemoveWatermark = findViewById(R.id.btnRemoveWatermark);

        videoPath = getIntent().getStringExtra("videoPath");
        watermark = getIntent().getBooleanExtra("watermark", true);

        SubscriptionManager subscriptionManager = new SubscriptionManager(this);
        if (subscriptionManager.isPremium()) {
            watermark = false; // Ensure no watermark for premium
        }

        updateSummary();

        btnShare.setOnClickListener(v -> shareVideo());

        if (!watermark) {
            btnRemoveWatermark.setEnabled(false);
            btnRemoveWatermark.setText(R.string.no_watermark_already);
        } else {
            btnRemoveWatermark.setOnClickListener(v -> loadRewardAd());
        }
    }

    private void updateSummary() {
        File f = videoPath == null ? null : new File(videoPath);
        String info = getString(R.string.export_summary_template,
                f != null ? f.getName() : getString(R.string.unknown),
                watermark ? getString(R.string.with_watermark) : getString(R.string.without_watermark));
        txtSummary.setText(info);
    }

    private void shareVideo() {
        if (videoPath == null) return;
        File f = new File(videoPath);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", f);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/mp4");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_video)));
    }

    private void loadRewardAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.admob_reward_id), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                txtSummary.setText(R.string.ad_load_failed);
            }

            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
                showRewardAd();
            }
        });
    }

    private void showRewardAd() {
        if (rewardedAd == null) return;
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                if (rewardEarned) {
                    watermark = false;
                    updateSummary();
                    btnRemoveWatermark.setEnabled(false);
                    btnRemoveWatermark.setText(R.string.no_watermark_already);
                }
            }
        });
        rewardedAd.show(this, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(RewardItem rewardItem) {
                rewardEarned = true;
            }
        });
    }
}
