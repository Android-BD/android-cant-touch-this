package com.relayr.cannottouchthis;

import android.content.Context;

import io.relayr.android.RelayrSdk;

public abstract class RelayrSdkInitializer {

    static void initSdk(Context context) {
        new RelayrSdk.Builder(context).inMockMode(true).build();
    }

    public static boolean isDebug(){
        return true;
    }
}
