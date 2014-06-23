package com.relayr.cannottouchthis.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Database {

    private static final String NAME = "Database";
    private static final String SENSOR_ID = "sensor_id";
    private static final String OBJECT_NAME = "object_name";

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

}
