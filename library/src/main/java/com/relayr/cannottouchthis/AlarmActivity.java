package com.relayr.cannottouchthis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AlarmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_screen);
    }

    /** Called from the xml */
    public void onBackClick(View view) {
        finish();
    }

    /** Called from the xml */
    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
