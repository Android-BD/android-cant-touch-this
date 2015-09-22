package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.app.adapter.DeviceAdapter;
import com.relayr.cannottouchthis.storage.Database;
import com.relayr.cannottouchthis.util.AppLauncher;
import com.relayr.cannottouchthis.util.SensitivityUtil;

import java.util.ArrayList;
import java.util.List;

import io.relayr.android.RelayrSdk;
import io.relayr.java.model.AccelGyroscope;
import io.relayr.java.model.Device;
import io.relayr.java.model.User;
import io.relayr.java.model.action.Reading;
import io.relayr.java.model.models.error.DeviceModelsCacheException;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class CantTouchThisActivity extends Activity {

    private final int SENSOR_NAME_RESULT = 11;

    private ListView mSensorList;
    private List<Device> mAccelerometers = new ArrayList<>();
    private int mSelectedSensor = -1;

    private Subscription mUserInfoSubscription = Subscriptions.empty();
    private Subscription mDeviceSubscription = Subscriptions.empty();
    private Device mDevice;

    private AlertDialog mNetworkDialog;
    private AlertDialog mLauncherDialog;

    private boolean logInStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cant_touch_this_activity);

        mSensorList = (ListView) findViewById(R.id.ctta_sensor_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkWiFi();
        setSensorListLayout(mSensorList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unSubscribeToUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (SENSOR_NAME_RESULT == requestCode && RESULT_OK == resultCode) {
            Database.setObjectId(mAccelerometers.get(mSelectedSensor).getId());

            startActivity(new Intent(this, SafeDeviceActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mNetworkDialog != null) mNetworkDialog.dismiss();
        if (mLauncherDialog != null) mLauncherDialog.dismiss();
    }

    private void checkWiFi() {
        if (isConnected()) checkUserState();
        else showNetworkDialog();
    }

    private void checkUserState() {
        if (RelayrSdk.isUserLoggedIn()) {
            loadUserInfo();
        } else {
            if (logInStarted) {
                onBackPressed();
            } else {
                logInStarted = true;
                RelayrSdk.logIn(this).subscribe(new Observer<User>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        logInStarted = false;
                        Toast.makeText(CantTouchThisActivity.this,
                                R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onNext(User user) {
                        logInStarted = false;
                        Toast.makeText(CantTouchThisActivity.this,
                                R.string.successfully_logged_in, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showNetworkDialog() {
        mNetworkDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.please_connect_to_wifi))
                .setPositiveButton(getString(R.string.connect), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    private void setSensorListLayout(ListView sensorList) {
        sensorList.setAdapter(new DeviceAdapter(this, mAccelerometers));
        sensorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Database.isDeviceSaved(mAccelerometers.get(i).getId())) {
                    subscribeForAccelerometerUpdates(mAccelerometers.get(i));

                    startActivity(new Intent(CantTouchThisActivity.this, SafeDeviceActivity.class));
                } else {
                    mSelectedSensor = i;

                    subscribeForAccelerometerUpdates(mAccelerometers.get(mSelectedSensor));

                    startActivityForResult(new Intent(CantTouchThisActivity.this,
                            DeviceNameActivity.class), SENSOR_NAME_RESULT);
                }
            }
        });
    }

    private void refreshSensorLayout(boolean sensorsEmpty) {
        findViewById(R.id.ctta_sensor_list).setVisibility(sensorsEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.ctta_empty_sensor_label).setVisibility(sensorsEmpty ? View.VISIBLE : View.GONE);

        if (sensorsEmpty) {
            mLauncherDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.ctta_empty_sensor_list))
                    .setMessage(R.string.ctta_empty_sensor_list_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            AppLauncher.openApp(CantTouchThisActivity.this, "io.relayr.wunderbar");
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        }
    }

    private void loadUserInfo() {
        mUserInfoSubscription = RelayrSdk.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.err_loading_user_data,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onNext(User user) {
                        loadAccelerometerDevices(user);
                    }
                });
    }

    private void loadAccelerometerDevices(User user) {
        mDeviceSubscription = user.getDevices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Device>>() {

                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.err_loading_devices,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onNext(List<Device> devices) {
                        filterAccelerometers(devices);
                    }
                });
    }

    private void filterAccelerometers(List<Device> devices) {
        mAccelerometers.clear();

        String modelId = null;
        try {
            modelId = RelayrSdk.getDeviceModelsCache().getModelByName("Wunderbar Accelerometer", false, true).getId();
        } catch (DeviceModelsCacheException e) {
            e.printStackTrace();
        }

        for (Device device : devices)
            if (device.getModelId() != null && device.getModelId().equals(modelId))
                mAccelerometers.add(device);

        refreshSensorLayout(mAccelerometers.isEmpty());
    }

    private void subscribeForAccelerometerUpdates(Device device) {
        mDevice = device;
        device.subscribeToCloudReadings()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Reading>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.err_socket_problem,
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override public void onNext(Reading reading) {
                        if (reading.meaning.equals("acceleration")) {
                            AccelGyroscope.Acceleration acc = new Gson().fromJson(reading.value.toString(),
                                    AccelGyroscope.Acceleration.class);
                            checkData(acc);
                        }
                    }
                });
    }

    private void checkData(AccelGyroscope.Acceleration acceleration) {
        if (!Database.isAlarmOn() || !Database.isWatchingObject()) return;

        if (SensitivityUtil.isReadingChanged(acceleration))
            startActivity(new Intent(CantTouchThisActivity.this, AlarmActivity.class));
    }

    private void unSubscribeToUpdates() {
        mUserInfoSubscription.unsubscribe();
        mDeviceSubscription.unsubscribe();

        if (mDevice != null) mDevice.unSubscribeToCloudReadings();
    }
}
