package com.relayr.cannottouchthis.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Database {

    private static final String NAME = "Database";
    private static final String SENSOR_ID = "sensor_id";
    private static final String OBJECT_NAME = "object_name";

    private static final String SETTINGS_ALARM_ON = "sett_alarm_on";
    private static final String SETTINGS_ALARM_TRESHOLD = "sett_alarm_threshold";


    private final SharedPreferences mDatabase;

    public Database(Context context) {
        mDatabase = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public boolean isWatchingObject() {
        return mDatabase.contains(OBJECT_NAME);
    }

    public void setObjectNameForSensor(String objectName, String sensorId) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putString(SENSOR_ID, sensorId);
        editor.putString(OBJECT_NAME, objectName);
        editor.apply();
    }

    public String getCurrentObjectName() {
        return mDatabase.getString(OBJECT_NAME, "");
    }

    public String getCurrentObjectId() {
        return mDatabase.getString(SENSOR_ID, "");
    }

    public void changeSensorObjectName(String objectName) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putString(OBJECT_NAME, objectName);
        editor.apply();
    }

    public void setAlarm(boolean isOn) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putBoolean(SETTINGS_ALARM_ON, isOn);
        editor.apply();
    }

    public boolean isAlarmOn() {
        return mDatabase.getBoolean(SETTINGS_ALARM_ON, true);
    }

    public void setThreshold(int th) {
        SharedPreferences.Editor editor = mDatabase.edit();
        editor.putInt(SETTINGS_ALARM_TRESHOLD, th);
        editor.apply();
    }

    public int getThreshold() {
        return mDatabase.getInt(SETTINGS_ALARM_TRESHOLD, 0);
    }
}
