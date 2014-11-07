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

public class AlarmActivity extends Activity {

    private TextView mObjectNameTV;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_activity);

        mObjectNameTV = (TextView) findViewById(R.id.txt_object_name);

        if (Database.isSoundAlarm()) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mObjectNameTV.setText(Database.getObjectName());

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
