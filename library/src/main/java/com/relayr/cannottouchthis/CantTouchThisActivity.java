package com.relayr.cannottouchthis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.relayr.cannottouchthis.storage.Database;


public class CantTouchThisActivity extends Activity {

    static final int REQUEST_OBJECT_NAME = 0;
    private /* final */ Database mDatabase;
    private FrameLayout mLayout;
    private int mCurrentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = new Database(this);
        mLayout = new FrameLayout(this);
        showSensorListLayout();
        setContentView(mLayout);
    }

    public static void requestObjectName(Activity a) {
        a.startActivityForResult(new Intent(a, CantTouchThisActivity.class), REQUEST_OBJECT_NAME);
    }

    /** Called from xml */
    public void onSensorClicked(View view) {
        showNameChangerLayout();
    }

    private void showSensorListLayout() {
        setLayout(R.layout.sensors_list);
    }

    private void showNameChangerLayout() {
        setLayout(R.layout.name_changer_layout);
    }

    private void setLayout(int layout) {
        mCurrentScreen = layout;
        mLayout.removeAllViews();
        View screen = View.inflate(this, mCurrentScreen, null);
        mLayout.addView(screen);
    }

    public void onDoneClicked(View view) {
        String name = ((EditText) mLayout.findViewById(R.id.etxt_object_name)).getText().toString();
        mDatabase.setObjectNameForSensor(name, "sensor_id_clicked");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentScreen == R.layout.sensors_list) {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        } else {
            showSensorListLayout();
        }
    }
}
