package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.relayr.cannottouchthis.R;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_activity);

        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(IntroActivity.this, CantTouchThisActivity.class));
                finish();
            }
        }, 2, TimeUnit.SECONDS);
    }
}
