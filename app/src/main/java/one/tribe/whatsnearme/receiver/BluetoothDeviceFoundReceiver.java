package one.tribe.whatsnearme.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;
import one.tribe.whatsnearme.bluetooth.DiscoverableBluetoothDevice;
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.network.NetworkEventType;

/**
 * Receives a BluetoothDevice.ACTION_FOUND broadcast. It's simply adds the
 * discovered device to the discovery devices list
 */
public class BluetoothDeviceFoundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        boolean isDiscovered = BluetoothDeviceManager.getInstance().addDiscoveredClassicDevice(device);

        // If the device is new, notify immediately
        if(!isDiscovered) {
            NetworkEvent newDevice = new NetworkEvent(new DiscoverableBluetoothDevice(device), NetworkEventType.NEW);
            ArrayList<NetworkEvent> list = new ArrayList<>(1);
            list.add(newDevice);

            Intent networkChangeIntent = new Intent(Constants.NETWORK_CHANGED);
            networkChangeIntent.putParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES, list);

            context.sendOrderedBroadcast(networkChangeIntent, null);
        }


    }
}
