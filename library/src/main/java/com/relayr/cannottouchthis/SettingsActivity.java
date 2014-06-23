package com.relayr.cannottouchthis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
    }

    public void onBackClick(View view) {
        finish();
    }

    public void onReAssignSensorClick(View view) {
        CantTouchThisActivity.requestObjectName(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CantTouchThisActivity.REQUEST_OBJECT_NAME == requestCode) {
            if (RESULT_CANCELED == resultCode) {
                // finish();
            } else {
                // TODO: setContentView(mDatabase.getCurrentObjectName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
