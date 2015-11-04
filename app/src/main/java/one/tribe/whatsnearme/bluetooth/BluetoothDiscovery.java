package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import one.tribe.whatsnearme.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BluetoothDiscovery {

    private List<DiscoverableBluetoothDevice> discoveredDevices;

    private ResultReceiver resultReceiver;

    private boolean started = false;

    private long id;

    public BluetoothDiscovery(long id) {
        this.id = id;
    }

    public boolean start(BluetoothAdapter bluetoothAdapter, ResultReceiver resultReceiver) {
        Log.i(Constants.TAG, "Starting bluetooth discovery " + id);
        this.resultReceiver = resultReceiver;
        discoveredDevices = new ArrayList<>();

        Log.i(Constants.TAG, "Starting bluetooth discovery on adapter: " + id);
        started = bluetoothAdapter.startDiscovery();
        Log.i(Constants.TAG, "Bluetooth discovery " + id + " started: " + started);

        return started;
    }

    /**
     * Finish the bluetooth discovery
     */
    public void finish() {
        started = false;
        Log.i(Constants.TAG, "Bluetooth discovery "+id+" finished, sending message to service");

        Bundle bundle = new Bundle();
        bundle.putInt("discoveredDevices", discoveredDevices.size());
        bundle.putLong("discoveryId", id);

        resultReceiver.send(1, bundle);
    }

    public void addDiscoveredDevice(BluetoothDevice device) {
        if(started) {
            Log.i(Constants.TAG, "Adding discovered device "+device+" to the devices list, discovery " + id);
            discoveredDevices.add(new DiscoverableBluetoothDevice(device));
        } else {
            Log.w(Constants.TAG, "Discovery is finished, device is ignored: "+ device);
        }
    }

    public List<DiscoverableBluetoothDevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

}
