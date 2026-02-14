package com.paras.generatedapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paras.generatedapp.R;
import com.paras.generatedapp.util.DailyLimitManager;
import com.paras.generatedapp.util.VideoRenderer;

import java.io.File;

public class RenderingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView txtStatus;

    private String script;
    private int durationMinutes;
    private boolean watermark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendering);

        progressBar = findViewById(R.id.progressBar);
        txtStatus = findViewById(R.id.txtStatus);

        script = getIntent().getStringExtra("script");
        durationMinutes = getIntent().getIntExtra("duration", 5);
        watermark = getIntent().getBooleanExtra("watermark", true);

        new RenderTask().execute();
    }

    private class RenderTask extends AsyncTask<Void, Integer, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtStatus.setText(R.string.rendering_started);
            progressBar.setProgress(0);
        }

        @Override
        protected File doInBackground(Void... voids) {
            VideoRenderer renderer = new VideoRenderer(RenderingActivity.this);
            renderer.setListener(progress -> publishProgress(progress));
            return renderer.render(script, durationMinutes, watermark);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int p = values[0];
            progressBar.setProgress(p);
            txtStatus.setText(getString(R.string.rendering_progress, p));
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            new DailyLimitManager(RenderingActivity.this).incrementCount();
            Intent intent = new Intent(RenderingActivity.this, ExportActivity.class);
            intent.putExtra("videoPath", file != null ? file.getAbsolutePath() : "");
            intent.putExtra("watermark", watermark);
            startActivity(intent);
            finish();
        }
    }
}
