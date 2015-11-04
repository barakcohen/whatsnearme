package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ResultReceiver;
import android.util.Log;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the bluetooth devices that are within the range of the device.
 *
 * It's a singleton to keep the state of the discovered devices
 */
public class BluetoothDeviceManager extends NetworkManager {
    private static BluetoothDeviceManager instance = new BluetoothDeviceManager();

    public static BluetoothDeviceManager getInstance() {
        return instance;
    }

    private BluetoothDiscovery bluetoothDiscovery;

    private List<Discoverable> availableDevices;

    private long discoverySequence;

    private BluetoothDeviceManager() {
        availableDevices = new ArrayList<>();
        discoverySequence = 0;
    }

    /**
     * Return the the devices visibility changes since the last discovery
     * @return the device changes
     */
    public NetworkChanges getDevicesChanges() {

        List<Discoverable> discoveredDevices = new ArrayList<>();
        for(Discoverable device : bluetoothDiscovery.getDiscoveredDevices()) {
            discoveredDevices.add(device);
        }

        Log.d(Constants.TAG, "Discovered Bluetooth devices: " + discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices before getChanges: " + availableDevices);

        NetworkChanges networkChanges =
                getChanges(discoveredDevices, new ArrayList<>(availableDevices));

        availableDevices  = new ArrayList<>(discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices after getChanges: " + availableDevices);

        return networkChanges;
    }


    /**
     * Starts the bluetooth device discovery
     * @param bluetoothAdapter
     */
    public void startDiscovery(BluetoothAdapter bluetoothAdapter, ResultReceiver resultReceiver) {
        bluetoothDiscovery = new BluetoothDiscovery(discoverySequence++);
        bluetoothDiscovery.start(bluetoothAdapter, resultReceiver);
    }

    /**
     * Adds a discovered device to the discovered device list
     * @param device the discovered device
     */
    public void addDiscoveredDevice(BluetoothDevice device) {
        Log.i(Constants.TAG, "Device discovered: " + device.getName() + ": " + device.getAddress());
        bluetoothDiscovery.addDiscoveredDevice(device);
    }

    /**
     * Returns a list of discovered devices
     */
    public List<DiscoverableBluetoothDevice> getDiscoveredDevices() {
        if(bluetoothDiscovery != null) {
            return bluetoothDiscovery.getDiscoveredDevices();
        }

        return null;
    }

    /**
     * Stops the bluetooth device discovery
     */
    public void finishDiscovery() {
        if(bluetoothDiscovery != null) {
            bluetoothDiscovery.finish();
        }
    }

    /**
     * Returns true if any discovery has already started
     */
    public boolean hasDiscovery() {
        return bluetoothDiscovery != null;
    }

    public List<Discoverable> getAvailableDevices() {
        return availableDevices;
    }
}
