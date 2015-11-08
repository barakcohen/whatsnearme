package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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

    private ClassicBluetoothDiscovery classicBluetoothDiscovery;
    private BluetoothLEScan bluetoothLEScan;


    private List<Discoverable> availableClassicBluetoothDevices;
    private List<Discoverable> availableLEBluetoothDevices;

    private long discoverySequence;

    private BluetoothDeviceManager() {
        availableClassicBluetoothDevices = new ArrayList<>();
        availableLEBluetoothDevices = new ArrayList<>();
        discoverySequence = 0;
    }

    /**
     * Return the the devices visibility changes since the last discovery
     * @return the device changes
     */
    public NetworkChanges getBluetoothLEDevicesChanges() {
        Log.d(Constants.TAG, "Discovered Bluetooth LE devices: " + bluetoothLEScan.getDiscoveredDevices());
        Log.d(Constants.TAG, "Available Bluetooth LE devices before getChanges: " + availableLEBluetoothDevices);

        NetworkChanges networkChanges =
                getChanges(bluetoothLEScan.getDiscoveredDevices(), new ArrayList<>(availableLEBluetoothDevices));

        availableLEBluetoothDevices = new ArrayList<>(bluetoothLEScan.getDiscoveredDevices());
        Log.d(Constants.TAG, "Available Bluetooth LE devices after getChanges: " + availableLEBluetoothDevices);

        return networkChanges;
    }

    public NetworkChanges getBluetoothDevicesChanges() {
        List<Discoverable> discoveredDevices = new ArrayList<>();
        for(Discoverable device : classicBluetoothDiscovery.getDiscoveredDevices()) {
            discoveredDevices.add(device);
        }

        Log.d(Constants.TAG, "Discovered Bluetooth devices: " + discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices before getChanges: " + availableClassicBluetoothDevices);

        NetworkChanges networkChanges =
                getChanges(discoveredDevices, new ArrayList<>(availableClassicBluetoothDevices));

        availableClassicBluetoothDevices = new ArrayList<>(discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices after getChanges: " + availableClassicBluetoothDevices);

        return networkChanges;
    }


    /**
     * Starts the bluetooth device discovery
     * @param bluetoothAdapter
     */
    public void startDiscovery(Context context, BluetoothAdapter bluetoothAdapter, ResultReceiver resultReceiver) {
        classicBluetoothDiscovery = new ClassicBluetoothDiscovery(discoverySequence++);
        classicBluetoothDiscovery.start(bluetoothAdapter);

        bluetoothLEScan = new BluetoothLEScan(context, bluetoothAdapter, resultReceiver);
    }

    /**
     * Adds a discovered device to the discovered device list
     * @param device the discovered device
     */
    public boolean addDiscoveredClassicDevice(BluetoothDevice device) {
        Log.i(Constants.TAG, "Device discovered: " + device.getName() + ": " + device.getAddress());
        DiscoverableBluetoothDevice discoverableBluetoothDevice =
                new DiscoverableBluetoothDevice(device);
        classicBluetoothDiscovery.addDiscoveredDevice(discoverableBluetoothDevice);

        boolean contains = availableClassicBluetoothDevices.contains(discoverableBluetoothDevice);
        if(!contains) {
            availableClassicBluetoothDevices.add(discoverableBluetoothDevice);
        }

        return contains;
    }

    /**
     * Adds a discovered Bluetooth LW device to the discovered device list
     * @param device the discovered device
     */
    public void addDiscoveredLEDevice(BluetoothDevice device) {
        DiscoverableBluetoothDevice discoverableBluetoothDevice =
                new DiscoverableBluetoothDevice(device);
        if(!availableLEBluetoothDevices.contains(discoverableBluetoothDevice)) {
            availableLEBluetoothDevices.add(discoverableBluetoothDevice);
        }
    }

    /**
     * Returns a list of discovered devices
     */
    public List<DiscoverableBluetoothDevice> getDiscoveredClassicDevices() {
        if(classicBluetoothDiscovery != null) {
            return classicBluetoothDiscovery.getDiscoveredDevices();
        }

        return null;
    }

    /**
     * Stops the bluetooth device discovery
     */
    public void finishClassicBluetoothDiscovery() {
        if(classicBluetoothDiscovery != null) {
            classicBluetoothDiscovery.finish();
            bluetoothLEScan.scanLeDevices();
        }
    }

    /**
     * Returns true if any discovery has already started
     */
    public boolean hasClassicBluetoothDiscovery() {
        return classicBluetoothDiscovery != null;
    }

    public List<Discoverable> getAvailableClassicBluetoothDevices() {
        return availableClassicBluetoothDevices;
    }

    public List<Discoverable> getAvailableLEBluetoothDevices() {
        return availableLEBluetoothDevices;
    }

    public List<Discoverable> getAvailableBluetoothDevices() {
        List<Discoverable> bluetoothDevices = new ArrayList<>(availableClassicBluetoothDevices);
        bluetoothDevices.addAll(availableLEBluetoothDevices);

        return bluetoothDevices;
    }
}
