package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

import io.relayr.android.RelayrSdk;

public class SettingsActivity extends Activity {

    private final int SENSOR_REASSIGN_RESULT = 12;

    private SeekBar mVolumeSeek;
    private TextView mSensitivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        mVolumeSeek = (SeekBar) findViewById(R.id.sa_volume_seek_bar);
        mSensitivity = (TextView) findViewById(R.id.sa_alarm_sensitivity_value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initiateView();
    }

    private void initiateView() {
        Switch mAlarmSwitch = (Switch) findViewById(R.id.sa_alarm_switch);
        mAlarmSwitch.setChecked(Database.isAlarmOn());

        Switch mSoundSwitch = (Switch) findViewById(R.id.sa_sound_switch);
        mSoundSwitch.setChecked(Database.isSoundAlarm());

        mVolumeSeek.setProgress(Database.getSoundVolume());
        mVolumeSeek.setEnabled(Database.isSoundAlarm());
        mVolumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Database.setSoundVolume(seekBar.getProgress());
            }
        });

        int sensitivity = (int) (Database.getSensitivity() * 2f);

        mSensitivity.setText(sensitivity + "");

        SeekBar mThresholdSeek = (SeekBar) findViewById(R.id.sa_threshold_seek_bar);
        mThresholdSeek.setProgress(sensitivity);
        mThresholdSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSensitivity.setText(seekBar.getProgress() + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Database.setSensitivity(seekBar.getProgress() / 2f);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (SENSOR_REASSIGN_RESULT == requestCode && RESULT_OK == resultCode) finish();
    }

    /** Called from the xml */
    public void onAlarmSwitchClick(View view) {
        Database.setAlarm(((Switch) view).isChecked());
    }

    /** Called from the xml */
    public void onSoundSwitchClick(View view) {
        boolean checked = ((Switch) view).isChecked();
        Database.setSoundAlarm(checked);
        mVolumeSeek.setEnabled(checked);
    }

    /** Called from the xml */
    public void onBackClick(View view) {
        finish();
    }

    /** Called from the xml */
    public void onReAssignSensorClick(View view) {
        startActivityForResult(new Intent(SettingsActivity.this, DeviceNameActivity.class),
                SENSOR_REASSIGN_RESULT);
    }

    /** Called from the xml */
    public void onLogOutClick(View view) {
        RelayrSdk.logOut();
        startActivity(new Intent(SettingsActivity.this, CantTouchThisActivity.class));
        finish();
    }
}
