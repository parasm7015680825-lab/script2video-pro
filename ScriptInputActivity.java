package com.paras.generatedapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paras.generatedapp.R;

public class ScriptInputActivity extends AppCompatActivity {

    private EditText edtScript;
    private SeekBar seekDuration;
    private TextView txtDurationValue;
    private Button btnNext;

    private int selectedDurationMinutes = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_input);

        edtScript = findViewById(R.id.edtScript);
        seekDuration = findViewById(R.id.seekDuration);
        txtDurationValue = findViewById(R.id.txtDurationValue);
        btnNext = findViewById(R.id.btnNext);

        seekDuration.setMax(10); // 5 to 15 (5 + progress)
        seekDuration.setProgress(0);
        txtDurationValue.setText(getString(R.string.duration_template, selectedDurationMinutes));

        seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedDurationMinutes = 5 + progress;
                txtDurationValue.setText(getString(R.string.duration_template, selectedDurationMinutes));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnNext.setOnClickListener(v -> {
            String script = edtScript.getText().toString().trim();
            if (TextUtils.isEmpty(script)) {
                edtScript.setError(getString(R.string.error_script_required));
                return;
            }

            Intent intent = new Intent(ScriptInputActivity.this, AdUnlockActivity.class);
            intent.putExtra("script", script);
            intent.putExtra("duration", selectedDurationMinutes);
            startActivity(intent);
        });
    }
}
