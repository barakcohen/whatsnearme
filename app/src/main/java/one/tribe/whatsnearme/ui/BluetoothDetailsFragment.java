package one.tribe.whatsnearme.ui;

import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.bluetooth.BluetoothDeviceType;
import one.tribe.whatsnearme.bluetooth.DiscoverableBluetoothDevice;

/**
 *
 */
public class BluetoothDetailsFragment extends DialogFragment {

    private DiscoverableBluetoothDevice bluetoothDevice;

    /**
     * Create a new instance of DetailsFragment
     */
    static BluetoothDetailsFragment newInstance(DiscoverableBluetoothDevice bluetoothDevice) {
        BluetoothDetailsFragment f = new BluetoothDetailsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("device", bluetoothDevice);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothDevice = getArguments().getParcelable("device");

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bluetooth_details, container, false);

        TextView deviceName = (TextView) v.findViewById(R.id.deviceName);
        TextView macAddress = (TextView) v.findViewById(R.id.macAddress);
        TextView deviceType = (TextView) v.findViewById(R.id.deviceType);
        TextView deviceClass = (TextView) v.findViewById(R.id.deviceClass);

        BluetoothDevice device = bluetoothDevice.getBluetoothDevice();

        if(device.getName() == null || device.getName().isEmpty()) {
            deviceName.setText("(Unnamed Device)");
        } else {
            deviceName.setText(device.getName());
        }

        macAddress.setText(device.getAddress());
        deviceType.setText(BluetoothDeviceType.getInstance(device.getType()).getDescription());
        deviceClass.setText("-");

        return v;
    }
}
