package com.relayr.cannottouchthis.util;

import com.relayr.cannottouchthis.RelayrSdkInitializer;
import com.relayr.cannottouchthis.storage.Database;

import io.relayr.java.model.AccelGyroscope;

public class SensitivityUtil {

    private static float mPrevValue = -1f;
    private static float mSensitivity;
    private static boolean mDebugAlarmsCounter = false;

    static {
        updateSensitivity();
    }

    public static void updateSensitivity() {
        mDebugAlarmsCounter = false;
        mSensitivity = Database.getSensitivity();

        if (mSensitivity == Database.MAX_SENSITIVITY) mSensitivity = 0.2f;
        else mSensitivity = Database.MAX_SENSITIVITY - mSensitivity;
    }

    public static boolean isReadingChanged(AccelGyroscope.Acceleration reading) {
        float value = Math.abs(reading.x) * 10 +
                Math.abs(reading.y * 10) +
                Math.abs(reading.z * 10);

        if (mPrevValue == -1f) mPrevValue = value;

        boolean isAlarm = value < mPrevValue - mSensitivity || value > mPrevValue + mSensitivity;

        mPrevValue = value;

        if (RelayrSdkInitializer.isDebug() && !mDebugAlarmsCounter) {
            mDebugAlarmsCounter = true;
            return true;
        }

        return isAlarm;
    }
}
