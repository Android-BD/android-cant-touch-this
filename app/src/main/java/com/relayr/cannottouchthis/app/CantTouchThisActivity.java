package com.relayr.cannottouchthis.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private static final String TAG = CantTouchThisActivity.class.getSimpleName();

    static final int REQUEST_OBJECT_NAME = 0;
    static final int SENSOR_NAME_RESULT = 11;

    private /* final */ Database mDatabase;

    private List<Device> accelerometers = new ArrayList<Device>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new Database(this);

        setContentView(R.layout.cant_touch_this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListView sensorList = (ListView) findViewById(R.id.ctta_sensor_list);
        setSensorListLayout(sensorList);

        //we use the relayr SDK to see if the user is logged in
        if (RelayrSdk.isUserLoggedIn()) {
            loadUserInfo();

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(new Runnable() {
                @Override
                public void run() {
                    if (mDatabase.isAlarmOn()) {
                        Intent i = new Intent(CantTouchThisActivity.this, AlarmActivity.class);
                        startActivity(i);
                    }
                }
            }, 10, TimeUnit.SECONDS);
        } else {
            //if the user isn't logged in, we call the logIn method
            RelayrSdk.logIn(this, this);
        }
    }

    public static void requestObjectName(Activity a) {
        a.startActivityForResult(new Intent(a, CantTouchThisActivity.class), REQUEST_OBJECT_NAME);
    }

    private void setSensorListLayout(ListView sensorList) {
        sensorList.setAdapter(new DeviceAdapter(this, accelerometers));
        sensorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDatabase.getCurrentObjectId().equals(accelerometers.get(i).id)) {
                    Intent changeName = new Intent(CantTouchThisActivity.this, SafeScreenActivity.class);
                    startActivity(changeName);
                } else {
                    mDatabase.setObjectNameForSensor("Sensor " + i, accelerometers.get(i).id);

                    Intent changeName = new Intent(CantTouchThisActivity.this, SensorNameActivity.class);
                    startActivityForResult(changeName, SENSOR_NAME_RESULT);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SENSOR_NAME_RESULT) {
            Intent protect = new Intent(this, SafeScreenActivity.class);
            startActivity(protect);
        }
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        }

        back_pressed = System.currentTimeMillis();
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
                        Log.e(TAG, "User observable - data generation failed");
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
                        Log.e(TAG, "Devices observable - data generation failed.");
                        Toast.makeText(CantTouchThisActivity.this, R.string.something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Device> devices) {
                        accelerometers.clear();

                        for (Device device : devices) {
                            if (device.getModel().getId().equals(DeviceModel.ACCELEROMETER_GYROSCOPE.getId())) {
                                accelerometers.add(device);
                                subscribeForAccelerometerUpdates(device);
                            }
                        }

                        if (accelerometers.isEmpty()) {
                            showSensorLayout(false);
                        } else {
                            showSensorLayout(true);
                        }
                    }
                });
    }

    private void showSensorLayout(boolean show) {
        findViewById(R.id.ctta_sensor_list).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.ctta_empty_sensor_label).setVisibility(show ? View.GONE : View.VISIBLE);
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
                        Reading reading = new Gson().fromJson(o.toString(), Reading.class);
                    }
                });
    }

    private class DeviceAdapter extends ArrayAdapter<Device> {

        public DeviceAdapter(Context context, List<Device> devices) {
            super(context, R.layout.cant_touch_this_list_device, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) View.inflate(getBaseContext(), R.layout.cant_touch_this_list_device, null);

            if (accelerometers.get(position).id.equals(mDatabase.getCurrentObjectId())) {
                view.setText(mDatabase.getCurrentObjectName());
            } else {
                view.setText("Sensor " + position);
            }
            return view;
        }
    }
}
