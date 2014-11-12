package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

public class DeviceNameActivity extends Activity {

    private EditText mSensorNameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_name_activity);

        mSensorNameET = (EditText) findViewById(R.id.dna_sensor_name_et);
        mSensorNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setObjectName();
                }
                return false;
            }
        });
        if (!Database.getObjectName().isEmpty())
            mSensorNameET.setText(Database.getObjectName());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    public void onDoneClicked(View v) {
        if (mSensorNameET.getText().toString().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dna_dialog_title))
                    .setMessage(getString(R.string.dna_dialog_message))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        } else {
            setObjectName();
        }
    }

    private void setObjectName() {
        Database.setObjectName(mSensorNameET.getText().toString());

        setResult(Activity.RESULT_OK);
        finish();
    }
}
