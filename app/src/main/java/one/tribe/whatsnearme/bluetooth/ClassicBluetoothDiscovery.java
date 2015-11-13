package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import one.tribe.whatsnearme.Constants;

/**
 *
 */
public class ClassicBluetoothDiscovery {

    private Set<DiscoverableBluetoothDevice> discoveredDevices;

    private boolean started = false;

    private long id;

    private ResultReceiver resultReceiver;

    public ClassicBluetoothDiscovery(long id) {
        this.id = id;
    }

    public boolean start(BluetoothAdapter bluetoothAdapter, ResultReceiver resultReceiver) {
        Log.i(Constants.TAG, "Starting bluetooth discovery " + id);
        discoveredDevices = new HashSet<>();
        this.resultReceiver = resultReceiver;

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
        resultReceiver = null;
        Log.i(Constants.TAG, "Classic Bluetooth discovery "+id+" finished");
    }

    public void finishAndNotify() {
        Log.i(Constants.TAG, "Send a message to ScannerService that broadcast is over");
        resultReceiver.send(1, new Bundle());
        finish();
    }


    public void addDiscoveredDevice(DiscoverableBluetoothDevice device) {
        if(started) {
            Log.i(Constants.TAG, "Adding discovered device " + device + " to the devices list, discovery " + id);
            if(!discoveredDevices.contains(device)) {
                discoveredDevices.add(device);
            } else {
                Log.i(Constants.TAG, "Device " + device + " already listed in discovery " + id);
            }

        } else {
            Log.w(Constants.TAG, "Discovery is finished, device is ignored: "+ device);
        }
    }

    public Set<DiscoverableBluetoothDevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

}
