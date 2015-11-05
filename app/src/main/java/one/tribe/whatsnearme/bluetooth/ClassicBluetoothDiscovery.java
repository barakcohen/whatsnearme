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
public class ClassicBluetoothDiscovery {

    private List<DiscoverableBluetoothDevice> discoveredDevices;

    private boolean started = false;

    private long id;

    public ClassicBluetoothDiscovery(long id) {
        this.id = id;
    }

    public boolean start(BluetoothAdapter bluetoothAdapter) {
        Log.i(Constants.TAG, "Starting bluetooth discovery " + id);
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
        Log.i(Constants.TAG, "Classic Bluetooth discovery "+id+" finished");
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
