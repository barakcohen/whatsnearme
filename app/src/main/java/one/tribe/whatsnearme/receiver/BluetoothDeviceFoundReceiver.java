package one.tribe.whatsnearme.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;

/**
 * Receives a BluetoothDevice.ACTION_FOUND broadcast. It's simply adds the
 * discovered device to the discovery devices list
 */
public class BluetoothDeviceFoundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        BluetoothDeviceManager.getInstance().addDiscoveredDevice(device);

    }
}
