package com.relayr.cannottouchthis;

import android.content.Context;

import io.relayr.RelayrSdk;

abstract class RelayrSdkInitializer {

    static void initSdk(Context context) {
        RelayrSdk.initInMockMode(context);
    }

}
