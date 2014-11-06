package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

public class SensorNameActivity extends Activity {

    private /* final */ Database mDatabase;

    private EditText mSensorNameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new Database(this);

        setContentView(R.layout.sensor_name_activity);

        mSensorNameET = (EditText) findViewById(R.id.sna_sensor_name_et);
        mSensorNameET.setText(mDatabase.getCurrentObjectName());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    public void onDoneClicked(View v) {
        if (mSensorNameET.getText().toString().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.sna_dialog_title))
                    .setMessage(getString(R.string.sna_dialog_message))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        } else {
            mDatabase.changeSensorObjectName(mSensorNameET.getText().toString());

            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
