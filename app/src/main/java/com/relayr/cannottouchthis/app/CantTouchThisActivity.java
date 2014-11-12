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
import com.relayr.cannottouchthis.util.SensitivityUtil;

import java.util.ArrayList;
import java.util.List;

import io.relayr.LoginEventListener;
import io.relayr.RelayrSdk;
import io.relayr.model.Device;
import io.relayr.model.DeviceModel;
import io.relayr.model.Reading;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CantTouchThisActivity extends Activity implements LoginEventListener {

    private final int SENSOR_NAME_RESULT = 11;

    private ListView mSensorList;
    private List<Device> mAccelerometers = new ArrayList<Device>();
    private int mSelectedSensor = -1;

    private Subscription mUserInfoSubscription;
    private Subscription mAccelDeviceSubscription;
    private Subscription mWebSocketSubscription;
    private TransmitterDevice mDevice;

    private AlertDialog mNetworkdialog;

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
            Database.setSensorData(mAccelerometers.get(mSelectedSensor));

            startActivity(new Intent(this, SafeDeviceActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mNetworkdialog != null) {
            mNetworkdialog.dismiss();
        }
    }

    @Override
    public void onSuccessUserLogIn() {
        Toast.makeText(this, R.string.successfully_logged_in, Toast.LENGTH_SHORT).show();
        loadUserInfo();
    }

    @Override
    public void onErrorLogin(Throwable e) {
        Toast.makeText(this, R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
    }

    private void checkWiFi() {
        if (isConnected()) {
            if (RelayrSdk.isUserLoggedIn()) {
                loadUserInfo();
            } else {
                RelayrSdk.logIn(this, this);
            }
        } else {
            showNetworkDialog();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showNetworkDialog() {
        mNetworkdialog = new AlertDialog.Builder(this).setTitle(getString(R.string.please_connect_to_wifi))
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
                if (Database.isDeviceSaved(mAccelerometers.get(i))) {
                    startActivity(new Intent(CantTouchThisActivity.this, SafeDeviceActivity.class));
                } else {
                    mSelectedSensor = i;

                    startActivityForResult(new Intent(CantTouchThisActivity.this,
                            DeviceNameActivity.class), SENSOR_NAME_RESULT);
                }
            }
        });
    }

    private void refreshSensorLayout(boolean sensorsEmpty) {
        findViewById(R.id.ctta_sensor_list).setVisibility(sensorsEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.ctta_empty_sensor_label).setVisibility(sensorsEmpty ? View.VISIBLE : View.GONE);
    }

    private void loadUserInfo() {
        mUserInfoSubscription = RelayrSdk.getRelayrApi()
                .getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.err_loading_user_data,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(User user) {
                        loadAccelerometerDevices(user.id);
                    }
                });
    }

    private void loadAccelerometerDevices(String userId) {
        mAccelDeviceSubscription = RelayrSdk.getRelayrApi()
                .getUserDevices(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Device>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.err_loading_devices,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Device> devices) {
                        filterAccelerometers(devices);
                    }
                });
    }

    private void filterAccelerometers(List<Device> devices) {
        mAccelerometers.clear();

        for (Device device : devices) {
            if (device.getModel().getId().equals(DeviceModel.ACCELEROMETER_GYROSCOPE.getId())) {
                mAccelerometers.add(device);
                subscribeForAccelerometerUpdates(device);
            }
        }

        refreshSensorLayout(mAccelerometers.isEmpty());
    }

    private void subscribeForAccelerometerUpdates(Device device) {
        mDevice = new TransmitterDevice(device.id, device.getSecret(),
                device.getOwner(), device.getName(), device.getModel().getId());

        mWebSocketSubscription = RelayrSdk.getWebSocketClient()
                .subscribe(mDevice, new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.err_socket_problem,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        Reading reading = new Gson().fromJson(o.toString(), Reading.class);
                        checkData(reading);
                    }
                });
    }

    private void checkData(Reading reading) {
        if (!Database.isAlarmOn() || !Database.isWatchingObject()) {
            return;
        }

        if (SensitivityUtil.isReadingChanged(reading)) {
            startActivity(new Intent(CantTouchThisActivity.this, AlarmActivity.class));
        }
    }

    private void unSubscribeToUpdates() {
        if (isSubscribed(mUserInfoSubscription)) {
            mUserInfoSubscription.unsubscribe();
        }
        if (isSubscribed(mAccelDeviceSubscription)) {
            mAccelDeviceSubscription.unsubscribe();
        }
        if (isSubscribed(mWebSocketSubscription)) {
            mWebSocketSubscription.unsubscribe();
            RelayrSdk.getWebSocketClient().unSubscribe(mDevice.id);
        }
    }

    private static boolean isSubscribed(Subscription subscription) {
        return subscription != null && !subscription.isUnsubscribed();
    }
}
