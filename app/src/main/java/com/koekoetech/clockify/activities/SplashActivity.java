package com.koekoetech.clockify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.koekoetech.clockify.BuildConfig;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.helpers.SharePreferenceHelper;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.activity_splash_tv_version)
    AppCompatTextView tvVersion;

    @BindString(R.string.lbl_version)
    String version;

    private Unbinder unbinder;
    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);
        init();
    }

    private void init() {
        sharePreferenceHelper = SharePreferenceHelper.getHelper(this);
        String versionName = version + BuildConfig.VERSION_NAME;
        tvVersion.setText(versionName);

        new Handler().postDelayed(this::intentProcess, 700);
    }

    private void intentProcess() {
        Intent intent;
        if (sharePreferenceHelper.isLogIn()) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), ApiKeyActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
