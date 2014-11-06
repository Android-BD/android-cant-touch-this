package com.relayr.cannottouchthis.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.relayr.cannottouchthis.util.SensitivityUtil;

import io.relayr.model.Device;

public class Database {

    private static final String NAME = "Database";
    private static final String SENSOR_ID = "sensor_id";
    private static final String OBJECT_NAME = "object_name";

    private static final String SETTINGS_ALARM_ON = "sett_alarm_on";
    private static final String SETTINGS_SOUND_ON = "sett_alarm_sound_on";
    private static final String SETTINGS_ALARM_THRESHOLD = "sett_alarm_threshold";
    private static final String SETTINGS_SOUND_VOLUME = "sett_sound_volume";

    public static final int MAX_THRESHOLD = 10;

    private static SharedPreferences mDatabase = null;

    public static void init(Context applicationContext) {
        new Database(applicationContext);
    }

    private Database(Context context) {
        mDatabase = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void setSensorData(Device device) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putString(SENSOR_ID, device.id);
        editor.putString(OBJECT_NAME, "Object");
        editor.apply();
    }

    public static String getCurrentObjectName() {
        return mDatabase.getString(OBJECT_NAME, "");
    }

    public static String getCurrentObjectId() {
        return mDatabase.getString(SENSOR_ID, "");
    }

    public static void changeSensorObjectName(String objectName) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putString(OBJECT_NAME, objectName);
        editor.apply();
    }

    public static boolean isWatchingObject() {
        return mDatabase.getString(SENSOR_ID, null) != null;
    }

    public static boolean isDeviceSaved(Device device) {
        return getCurrentObjectId().equals(device.id);
    }

    public static void setAlarm(boolean isOn) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putBoolean(SETTINGS_ALARM_ON, isOn);
        editor.apply();
    }

    public static boolean isAlarmOn() {
        return mDatabase.getBoolean(SETTINGS_ALARM_ON, true);
    }

    public static void setSoundAlarm(boolean isSoundOn) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putBoolean(SETTINGS_SOUND_ON, isSoundOn);
        editor.apply();
    }

    public static boolean isSoundAlarm() {
        return mDatabase.getBoolean(SETTINGS_SOUND_ON, false);
    }

    public static void setThreshold(int th) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putInt(SETTINGS_ALARM_THRESHOLD, th);
        editor.apply();

        SensitivityUtil.thresholdChanged();
    }

    public static int getThreshold() {
        return mDatabase.getInt(SETTINGS_ALARM_THRESHOLD, MAX_THRESHOLD / 2);
    }

    public static void setSoundVolume(int volume) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putInt(SETTINGS_SOUND_VOLUME, volume);
        editor.apply();
    }

    public static int getSoundVolume() {
        return mDatabase.getInt(SETTINGS_SOUND_VOLUME, 2);
    }

    public static float getVolume() {
        int volume = getSoundVolume();

        if (volume == 0) {
            return 0f;
        } else {
            return volume / 10f;
        }
    }
}
