package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

public class SafeDeviceActivity extends Activity {

    private TextView mObjectNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.safe_device_activity);

        mObjectNameTV = (TextView) findViewById(R.id.ss_object_name_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mObjectNameTV.setText(Database.getCurrentObjectName());
    }

    /** Called from the xml */
    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
