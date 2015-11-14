package one.tribe.whatsnearme.ui;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;

import java.util.List;

import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.model.KnownDevice;

/**
 *
 */
public class KnownDeviceListAdapter extends BaseAdapter {

    private List<KnownDevice> knownDevices;
    private LayoutInflater inflater;

    public KnownDeviceListAdapter(List<KnownDevice> knownDevices, LayoutInflater inflater) {
        this.knownDevices = knownDevices;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return knownDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return knownDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_known_device, null);
        }

        EditText macAddress = (EditText) convertView.findViewById(R.id.macAddress);
        EditText deviceId = (EditText)  convertView.findViewById(R.id.knownDeviceId);

        if(knownDevices.size() > position) {
            deviceId.setText(knownDevices.get(position).getId());
            macAddress.setText(knownDevices.get(position).getMacAddress());
        }


        return convertView;
    }
}
