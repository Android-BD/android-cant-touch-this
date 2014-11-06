package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CantTouchThisActivity extends Activity implements LoginEventListener {

    private final int SENSOR_NAME_RESULT = 11;

    private ListView mSensorList;
    private List<Device> mAccelerometers = new ArrayList<Device>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cant_touch_this_activity);

        mSensorList = (ListView) findViewById(R.id.ctta_sensor_list);

        if (RelayrSdk.isUserLoggedIn()) {
            loadUserInfo();
        } else {
            RelayrSdk.logIn(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setSensorListLayout(mSensorList);
        refreshSensorLayout(mAccelerometers.isEmpty());
    }

    private void setSensorListLayout(ListView sensorList) {
        sensorList.setAdapter(new DeviceAdapter(this, mAccelerometers));

        sensorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Database.isDeviceSaved(mAccelerometers.get(i))) {
                    startActivity(new Intent(CantTouchThisActivity.this, SafeDeviceActivity.class));
                } else {
                    Database.setSensorData(mAccelerometers.get(i));

                    startActivityForResult(new Intent(CantTouchThisActivity.this,
                            DeviceNameActivity.class), SENSOR_NAME_RESULT);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (SENSOR_NAME_RESULT == requestCode) {
            startActivity(new Intent(this, SafeDeviceActivity.class));
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

    private void loadUserInfo() {
        RelayrSdk.getRelayrApi()
                .getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(User user) {
                        loadAccelerometerDevices(user.id);
                    }
                });
    }

    private void loadAccelerometerDevices(String userId) {
        RelayrSdk.getRelayrApi()
                .getUserDevices(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Device>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.something_went_wrong,
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
        TransmitterDevice transmitterDevice = new TransmitterDevice(device.id, device.getSecret(),
                device.getOwner(), device.getName(), device.getModel().getId());

        RelayrSdk.getWebSocketClient()
                .subscribe(transmitterDevice, new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CantTouchThisActivity.this, R.string.something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.w("CTTA", "Reading");
                        Reading reading = new Gson().fromJson(o.toString(), Reading.class);
                        checkData(reading);
                    }
                });
    }

    private void checkData(Reading reading) {
        if (!Database.isAlarmOn() || !Database.isWatchingObject()) {
            return;
        }

        if(SensitivityUtil.isReadingChanged(reading)){
            startActivity(new Intent(CantTouchThisActivity.this, AlarmActivity.class));
        }
    }

    private void refreshSensorLayout(boolean sensorsEmpty) {
        findViewById(R.id.ctta_sensor_list).setVisibility(sensorsEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.ctta_empty_sensor_label).setVisibility(sensorsEmpty ? View.VISIBLE : View.GONE);
    }
}
