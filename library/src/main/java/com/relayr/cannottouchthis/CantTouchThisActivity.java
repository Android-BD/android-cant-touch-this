package com.relayr.cannottouchthis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class CantTouchThisActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors_list);
    }

    /** Called from xml */
    public void onSensorClicked(View view) {
        startActivity(new Intent(this, SafeScreenActivity.class));
    }
}
