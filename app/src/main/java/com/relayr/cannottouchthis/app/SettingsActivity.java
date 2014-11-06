package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

public class SettingsActivity extends Activity {

    private static final int SENSOR_REASSIGN_RESULT = 12;

    private /* final */ Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new Database(this);

        setContentView(R.layout.settings_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initiateView();
    }

    private void initiateView() {
        Switch mAlarmSwitch = (Switch) findViewById(R.id.sa_alarm_switch);
        mAlarmSwitch.setChecked(mDatabase.isAlarmOn());

        SeekBar mThresholdSeek = (SeekBar) findViewById(R.id.sa_threshold_seek_bar);
        mThresholdSeek.setProgress(mDatabase.getThreshold());
        mThresholdSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDatabase.setThreshold(seekBar.getProgress());
            }
        });
    }

    public void onSwitchClick(View view) {
        mDatabase.setAlarm(((Switch) view).isChecked());
    }

    public void onBackClick(View view) {
        finish();
    }

    public void onReAssignSensorClick(View view) {
        Intent changeName = new Intent(SettingsActivity.this, SensorNameActivity.class);
        startActivityForResult(changeName, SENSOR_REASSIGN_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (CantTouchThisActivity.REQUEST_OBJECT_NAME == requestCode) {
            if (RESULT_CANCELED == resultCode) {
                finish();
            }
        }

        if (SENSOR_REASSIGN_RESULT == requestCode) {
            if (RESULT_OK == resultCode) {
                finish();
            }
        }
    }

}
