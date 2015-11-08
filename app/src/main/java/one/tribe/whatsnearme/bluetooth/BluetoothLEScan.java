package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.network.NetworkEventType;

/**
 *
 */
public class BluetoothLEScan {

    private Context serviceContext;

    private BluetoothAdapter bluetoothAdapter;

    private List<Discoverable> discoveredDevices;

    private ResultReceiver receiver;

    public BluetoothLEScan(Context serviceContext, BluetoothAdapter bluetoothAdapter, ResultReceiver receiver) {
        this.serviceContext = serviceContext;
        this.bluetoothAdapter = bluetoothAdapter;
        discoveredDevices = new ArrayList<>();
        this.receiver = receiver;
    }

    public void scanLeDevices() {
        Log.i(Constants.TAG, "Starting scan for Bluetooth LE devices");
        Handler handler = new Handler();

        // Stops scanning after a pre-defined scan period.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(Constants.TAG, "Stop scanning for Bluetooth LE Devices");
                bluetoothAdapter.stopLeScan(mLeScanCallback);

                Log.d(Constants.TAG, "Total of Bluetooth LE Devices Discovered: " + discoveredDevices.size());

                broadcastDeviceChanges(BluetoothDeviceManager.getInstance().getBluetoothLEDevicesChanges());

                receiver.send(1, new Bundle());

            }
        }, Constants.BLUETOOTH_LE_SCAN_PERIOD);

        bluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private void broadcastDeviceChanges(NetworkChanges changes) {

        if(changes.hasChanges()) {
            Intent networkChangeIntent = new Intent(Constants.NETWORK_CHANGED);
            networkChangeIntent.putParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES, changes.getNetworkEvents());

            serviceContext.sendOrderedBroadcast(networkChangeIntent, null);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    Log.i(Constants.TAG, "Bluetooth LE Device found: " + device);
                    Discoverable discoveredDevice = new DiscoverableBluetoothDevice(device);
                    discoveredDevices.add(discoveredDevice);

                    // let Device Manager knows a new discovered le device was found
                    BluetoothDeviceManager.getInstance().addDiscoveredLEDevice(device);

                    // broadcast the new device for all listening components
                    NetworkChanges changes = new NetworkChanges(Collections.singletonList(discoveredDevice), Collections.EMPTY_LIST);
                    broadcastDeviceChanges(changes);
                }
            };

    public List<Discoverable> getDiscoveredDevices() {
        return discoveredDevices;
    }
}
