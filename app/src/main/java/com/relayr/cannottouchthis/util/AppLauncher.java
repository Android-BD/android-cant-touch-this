package com.relayr.cannottouchthis.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class AppLauncher {

    public static void openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();

        Intent startApp = manager.getLaunchIntentForPackage(packageName);
        if (startApp != null) {
            startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startApp.addCategory(Intent.CATEGORY_LAUNCHER);

            context.startActivity(startApp);
            return;
        }

        try {
            Uri storeUri = Uri.parse("market://details?id=" + packageName);
            startStoreActivity(context, storeUri);
        } catch (ActivityNotFoundException anfe) {
            Uri webUri = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
            startStoreActivity(context, webUri);
        }
    }

    private static void startStoreActivity(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
