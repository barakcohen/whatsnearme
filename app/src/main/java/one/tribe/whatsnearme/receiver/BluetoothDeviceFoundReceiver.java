package one.tribe.whatsnearme.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;
import one.tribe.whatsnearme.bluetooth.DiscoverableBluetoothDevice;
import one.tribe.whatsnearme.bluetooth.WhatsNearMeDeviceManager;
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

        Log.d(Constants.TAG, "BluetoothDevice.ACTION_FOUND broadcast received");

        boolean isDiscovered = BluetoothDeviceManager.getInstance().addDiscoveredClassicDevice(device);
        WhatsNearMeDeviceManager.getInstance().addDiscoveredDevice(device);

        // If the device is new, notify immediately
        if(!isDiscovered) {
            NetworkEvent newDevice = new NetworkEvent(new DiscoverableBluetoothDevice(device), NetworkEventType.NEW);
            ArrayList<NetworkEvent> list = new ArrayList<>(Collections.singletonList(newDevice));

            Intent networkChangeIntent = new Intent(Constants.NETWORK_CHANGED);
            networkChangeIntent.putParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES, list);

            context.sendOrderedBroadcast(networkChangeIntent, null);
        }


    }
}
