package com.relayr.cannottouchthis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SafeScreenActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.safe_screen, null);
        setContentView(view);
        showAlarm(view);
    }

    private void showAlarm(View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SafeScreenActivity.this, AlarmActivity.class));
            }
        }, 5000);
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
