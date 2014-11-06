package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

public class AlarmActivity extends Activity {

    private TextView mObjectNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.alarm_screen, null);
        mObjectNameTextView = (TextView) view.findViewById(R.id.txt_object_name);
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Database database = new Database(this);
        mObjectNameTextView.setText(database.getCurrentObjectName());
    }

    /** Called from the xml */
    public void onBackClick(View view) {
        finish();
    }

    /** Called from the xml */
    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
