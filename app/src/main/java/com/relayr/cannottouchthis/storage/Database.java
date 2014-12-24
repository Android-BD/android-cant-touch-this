package com.relayr.cannottouchthis.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.relayr.cannottouchthis.util.SensitivityUtil;

public class Database {

    public static final float MAX_SENSITIVITY = 5f;

    private static final String NAME = "Database";
    private static final String SENSOR_ID = "sensor_id";
    private static final String OBJECT_NAME = "object_name";

    private static final String ALARM_ON = "sett_alarm_on";
    private static final String SOUND_ON = "sett_alarm_sound_on";
    private static final String SENSITIVITY = "sett_alarm_threshold";
    private static final String VOLUME = "sett_sound_volume";

    private static SharedPreferences mDatabase = null;
    private static SharedPreferences.Editor mEditor = null;

    public static void init(Context applicationContext) {
        new Database(applicationContext);
    }

    private Database(Context context) {
        mDatabase = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        mEditor = mDatabase.edit();
    }

    public static void setObjectId(String deviceId) {
        mEditor.putString(SENSOR_ID, deviceId).apply();
    }

    public static String getObjectId() {
        return mDatabase.getString(SENSOR_ID, "");
    }

    public static void setObjectName(String objectName) {
        mEditor.putString(OBJECT_NAME, objectName).apply();
    }

    public static String getObjectName() {
        return mDatabase.getString(OBJECT_NAME, "");
    }

    public static boolean isWatchingObject() {
        return mDatabase.getString(SENSOR_ID, null) != null;
    }

    public static boolean isDeviceSaved(String deviceId) {
        return getObjectId().equals(deviceId);
    }

    public static void setAlarm(boolean isOn) {
        mEditor.putBoolean(ALARM_ON, isOn).apply();
    }

    public static boolean isAlarmOn() {
        return mDatabase.getBoolean(ALARM_ON, true);
    }

    public static void setSoundAlarm(boolean isSoundOn) {
        mEditor.putBoolean(SOUND_ON, isSoundOn).apply();
    }

    public static boolean isSoundAlarm() {
        return mDatabase.getBoolean(SOUND_ON, false);
    }

    public static void setSensitivity(float th) {
        mEditor.putFloat(SENSITIVITY, th).apply();

        SensitivityUtil.updateSensitivity();
    }

    public static float getSensitivity() {
        return mDatabase.getFloat(SENSITIVITY, MAX_SENSITIVITY - 1);
    }

    public static void setSoundVolume(int volume) {
        mEditor.putInt(VOLUME, volume).apply();
    }

    public static int getSoundVolume() {
        return mDatabase.getInt(VOLUME, 2);
    }

    public static float getVolume() {
        int volume = getSoundVolume();

        if (volume == 0) return 0f;
        else return volume / 10f;
    }
}
