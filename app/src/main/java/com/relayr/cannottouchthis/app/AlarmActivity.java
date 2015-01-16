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

import java.io.IOException;
import java.util.Calendar;

public class AlarmActivity extends Activity {

    private TextView mObjectName;
    private TextView mDateTime;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_activity);

        mObjectName = (TextView) findViewById(R.id.aa_object_name_tv);
        mDateTime = (TextView) findViewById(R.id.aa_date_time_tv);

        if (Database.isSoundAlarm()) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.hammer_alarm);
            mMediaPlayer.setVolume(Database.getVolume(), Database.getVolume());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mObjectName.setText(Database.getObjectName());

        mDateTime.setText(String.format("ON %1$tD AT %1$tI:%1$tM", Calendar.getInstance()));

        if (Database.isSoundAlarm()) {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMediaPlayer != null) mMediaPlayer.reset();
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
