package com.relayr.cannottouchthis.util;

import android.util.Log;

import com.relayr.cannottouchthis.storage.Database;

import io.relayr.model.Reading;

public class SensitivityUtil {

    private static float mPrevValue = -1f;
    private static int mThreshold;

    static {
        mThreshold = Database.getThreshold();
    }

    public static boolean isReadingChanged(Reading reading) {
        float value = Math.abs(reading.accel.x) * 10 +
                Math.abs(reading.accel.y * 10) +
                Math.abs(reading.accel.z * 10);

        if (mPrevValue == -1f) {
            mPrevValue = value;
        }

        Log.e("READING", "C: " + value + " P: " + mPrevValue);

        if (value < mPrevValue - mThreshold || value > mPrevValue + mThreshold) {
            return true;
        } else {
            mPrevValue = value;
            return false;
        }
    }
}
