package com.koekoetech.clockify.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ClockifyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupRealm();
    }

    private void setupRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("clockify.realm")
                .schemaVersion(1)
                .compactOnLaunch()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
