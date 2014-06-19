package com.relayr.cannottouchthis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
    }

    public void onBackClick(View view) {
        finish();
    }
}
