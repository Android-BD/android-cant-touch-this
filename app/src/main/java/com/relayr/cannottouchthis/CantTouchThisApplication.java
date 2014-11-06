package com.relayr.cannottouchthis;

import android.app.Application;

public class CantTouchThisApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RelayrSdkInitializer.initSdk(this);
    }
}
