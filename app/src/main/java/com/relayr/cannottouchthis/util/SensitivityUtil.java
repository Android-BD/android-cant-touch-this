package com.relayr.cannottouchthis.util;

import com.relayr.cannottouchthis.RelayrSdkInitializer;
import com.relayr.cannottouchthis.storage.Database;

import io.relayr.model.Reading;

public class SensitivityUtil {

    private static float mPrevValue = -1f;
    private static float mThreshold;

    //Used for filtering mocked accelerometer values in debug mode
    private static int mDebugAlarmsCounter = 0;

    static {
        thresholdChanged();
    }

    public static void thresholdChanged() {
        mThreshold = Database.getSensitivity();
        if (mThreshold == Database.MAX_SENSITIVITY) {
            mThreshold = 0.2f;
        } else {
            mThreshold = Database.MAX_SENSITIVITY - mThreshold;
        }
    }

    public static boolean isReadingChanged(Reading reading) {
        float value = Math.abs(reading.accel.x) * 10 +
                Math.abs(reading.accel.y * 10) +
                Math.abs(reading.accel.z * 10);

        if (mPrevValue == -1f) {
            mPrevValue = value;
        }

        boolean isAlarm = value < mPrevValue - mThreshold || value > mPrevValue + mThreshold;

        mPrevValue = value;

        //Hack for debug mode
        //Mocked data is coming to fast for this demo so only every 50th alarm will be triggered
        //Alarm will activate approximately every 25 seconds
        if (RelayrSdkInitializer.isDebug() && isAlarm) {
            mDebugAlarmsCounter++;

            if (mDebugAlarmsCounter == 50) {
                mDebugAlarmsCounter = 0;
                return true;
            }

            return false;
        }

        return isAlarm;
    }
}
