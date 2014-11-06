package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

public class SafeScreenActivity extends Activity {

    private /* final */ Database mDatabase;
    private TextView mObjectNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new Database(this);

        setContentView(R.layout.safe_screen);

        mObjectNameTV = (TextView) findViewById(R.id.ss_object_name_tv);

        if (!mDatabase.isWatchingObject()) {
            CantTouchThisActivity.requestObjectName(this);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mObjectNameTV.setText(mDatabase.getCurrentObjectName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CantTouchThisActivity.REQUEST_OBJECT_NAME == requestCode) {
            if (RESULT_CANCELED == resultCode) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
