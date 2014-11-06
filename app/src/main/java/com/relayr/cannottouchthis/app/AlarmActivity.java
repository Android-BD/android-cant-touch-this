package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

import java.io.IOException;

public class AlarmActivity extends Activity {

    private TextView mObjectNameTextView;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_activity);

        mObjectNameTextView = (TextView) findViewById(R.id.txt_object_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mObjectNameTextView.setText(Database.getCurrentObjectName());

        if (Database.isSoundAlarm()) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(0.1f, 0.1f);
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
