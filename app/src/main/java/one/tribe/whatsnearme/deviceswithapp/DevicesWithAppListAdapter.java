package one.tribe.whatsnearme.deviceswithapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.deviceswithapp.helper.DeviceWithAppButtonActions;
import one.tribe.whatsnearme.deviceswithapp.helper.DeviceWithAppListHandler;
import one.tribe.whatsnearme.deviceswithapp.helper.DeviceWithAppManager;
import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;

/**
 * Adapter for the list of devices with app
 */
public class DevicesWithAppListAdapter extends BaseAdapter implements DeviceWithAppManager {

    private DeviceWithAppListHandler handler;
    private LayoutInflater inflater;
    private Activity parent;

    public DevicesWithAppListAdapter(DeviceWithAppListHandler handler, Activity parent) {
        this.handler = handler;
        this.parent = parent;
        this.inflater = (LayoutInflater) parent
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.i(Constants.TAG, "Creating DevicesWithAppListAdapter");
    }

    public void insertDevice(DeviceWithApp deviceWithApp) {
        handler.insertDevice(deviceWithApp);
        notifyDataSetChanged();
    }

    public void deleteDevice(DeviceWithApp data) {
        handler.deleteDevice(data);
        notifyDataSetChanged();
    }

    public void updateDevice(DeviceWithApp data) {
        handler.updateDevice(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return handler.getDevicesWithApp().size();
    }

    @Override
    public Object getItem(int position) {
        return handler.getDevicesWithApp().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_device_with_app, null);
        }

        EditText listDeviceWithAppId = (EditText) convertView.findViewById(R.id.listDeviceWithAppId);
        EditText listMacAddress = (EditText) convertView.findViewById(R.id.listMacAddress);

        ImageView editButton = (ImageView) convertView.findViewById(R.id.editButton);
        ImageView deleteButton = (ImageView) convertView.findViewById(R.id.deleteButton);

        if (handler.getDevicesWithApp().size() > position) {
            DeviceWithApp data = handler.getDevicesWithApp().get(position);

            // get data
            listDeviceWithAppId.setText(data.getPhoneId());
            listMacAddress.setText(data.getMacAddress());

            //set button handlers
            editButton.setOnClickListener(new DeviceWithAppButtonActions.EnableEditClickListener(this.parent, data));
            deleteButton.setOnClickListener(new DeviceWithAppButtonActions.DeleteClickListener(data, this, this.parent));
        }

        return convertView;
    }
}
