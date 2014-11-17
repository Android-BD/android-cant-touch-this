package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

import java.util.Calendar;

public class AlarmActivity extends Activity {

    private TextView mObjectNameTv;
    private TextView mDateTimeTv;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_activity);

        mObjectNameTv = (TextView) findViewById(R.id.aa_object_name_tv);
        mDateTimeTv = (TextView) findViewById(R.id.aa_date_time_tv);

        if (Database.isSoundAlarm()) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hammer_alarm);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mObjectNameTv.setText(Database.getObjectName());

        mDateTimeTv.setText(String.format("ON %1$tD AT %1$tI:%1$tM", Calendar.getInstance()));

        if (Database.isSoundAlarm()) {
            mMediaPlayer.setVolume(Database.getVolume(), Database.getVolume());
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    /**
     * Called from the xml
     */
    public void onDismissClicked(View view) {
        finish();
    }

    /**
     * Called from the xml
     */
    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }
}
