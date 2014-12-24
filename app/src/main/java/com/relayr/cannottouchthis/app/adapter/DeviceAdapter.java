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

    private final List<Device> mDevices;
    private final Context mContext;

    public DeviceAdapter(Context context, List<Device> devices) {
        super(context, R.layout.list_device, devices);

        this.mContext = context;
        this.mDevices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) View.inflate(mContext, R.layout.list_device, null);

        String id = mDevices.get(position).id;
        if (id.equals(Database.getObjectId())) view.setText(Database.getObjectName());
        else view.setText(mContext.getString(R.string.sensor_name_prefix) + position);

        return view;
    }
}
