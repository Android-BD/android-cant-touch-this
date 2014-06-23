package com.relayr.cannottouchthis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.relayr.cannottouchthis.storage.Database;

public class SafeScreenActivity extends Activity {

    private /* final */ Database mDatabase;
    private TextView mObjectNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = new Database(this);
        if (mDatabase.isWatchingObject()) {
            setContentView(mDatabase.getCurrentObjectName());
        } else {
            CantTouchThisActivity.requestObjectName(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Database database = new Database(this);
        mObjectNameTextView.setText(database.getCurrentObjectName());
    }

    private void setContentView(String name) {
        View view = View.inflate(this, R.layout.safe_screen, null);
        mObjectNameTextView = (TextView) view.findViewById(R.id.txt_object_name);
        mObjectNameTextView.setText(name);
        setContentView(view);
        showAlarm(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CantTouchThisActivity.REQUEST_OBJECT_NAME == requestCode) {
            if (RESULT_CANCELED == resultCode) {
                finish();
            } else {
                setContentView(mDatabase.getCurrentObjectName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showAlarm(View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SafeScreenActivity.this, AlarmActivity.class));
            }
        }, 5000);
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
