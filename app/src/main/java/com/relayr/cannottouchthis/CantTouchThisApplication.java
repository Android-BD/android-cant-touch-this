package com.relayr.cannottouchthis;

import android.app.Application;

import com.relayr.cannottouchthis.storage.Database;

import io.relayr.android.RelayrSdk;

public class CantTouchThisApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RelayrSdkInitializer.initSdk(this);
        Database.init(getApplicationContext());
    }
}
