package com.relayr.cannottouchthis.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.relayr.cannottouchthis.R;
import com.relayr.cannottouchthis.storage.Database;

import java.util.List;

import io.relayr.model.Device;

public class DeviceAdapter extends ArrayAdapter<Device> {

    private final List<Device> devices;
    private final Context context;

    public DeviceAdapter(Context context, List<Device> devices) {
        super(context, R.layout.cant_touch_this_list_device, devices);

        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) View.inflate(context, R.layout.cant_touch_this_list_device, null);

        if (devices.get(position).id.equals(Database.getObjectId())) {
            view.setText(Database.getObjectName());
        } else {
            view.setText(context.getString(R.string.sensor_name_prefix) + position);
        }

        return view;
    }
}
