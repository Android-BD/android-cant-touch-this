package com.relayr.cannottouchthis.util;

import com.relayr.cannottouchthis.storage.Database;

import io.relayr.model.Reading;

public class SensitivityUtil {

    private static float mPrevValue = -1f;
    private static int mThreshold;

    static {
       thresholdChanged();
    }

    public static void thresholdChanged(){
        mThreshold = Database.getThreshold();
        if (mThreshold == Database.MAX_THRESHOLD) {
            mThreshold = 1;
        } else {
            mThreshold = Database.MAX_THRESHOLD - mThreshold;
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

        return isAlarm;
    }
}
