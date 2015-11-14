package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import one.tribe.whatsnearme.AppPreferences;
import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkManager;

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


    private Set<Discoverable> availableClassicBluetoothDevices;
    private Set<Discoverable> availableLEBluetoothDevices;

    private long discoverySequence;

    private AppPreferences preferences;

    private BluetoothDeviceManager() {
        availableClassicBluetoothDevices = new HashSet<>();
        availableLEBluetoothDevices = new HashSet<>();
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
                getChanges(bluetoothLEScan.getDiscoveredDevices(), new HashSet<>(availableLEBluetoothDevices));

        availableLEBluetoothDevices = new HashSet<>(bluetoothLEScan.getDiscoveredDevices());
        Log.d(Constants.TAG, "Available Bluetooth LE devices after getChanges: " + availableLEBluetoothDevices);

        return networkChanges;
    }

    public NetworkChanges getBluetoothDevicesChanges() {
        Set<Discoverable> discoveredDevices = new HashSet<>();
        for(Discoverable device : classicBluetoothDiscovery.getDiscoveredDevices()) {
            discoveredDevices.add(device);
        }

        Log.d(Constants.TAG, "Discovered Bluetooth devices: " + discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices before getChanges: " + availableClassicBluetoothDevices);

        NetworkChanges networkChanges =
                getChanges(discoveredDevices, new HashSet<>(availableClassicBluetoothDevices));

        availableClassicBluetoothDevices = new HashSet<>(discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices after getChanges: " + availableClassicBluetoothDevices);

        return networkChanges;
    }


    /**
     * Starts the bluetooth device discovery
     * @param bluetoothAdapter
     */
    public void startDiscovery(Context context, BluetoothAdapter bluetoothAdapter, ResultReceiver resultReceiver) {
        if(preferences == null) {
            preferences = new AppPreferences(context);
        }

        classicBluetoothDiscovery = new ClassicBluetoothDiscovery(discoverySequence++);
        classicBluetoothDiscovery.start(bluetoothAdapter, resultReceiver);

        if (shouldStartLeScan(context)) {
            bluetoothLEScan = new BluetoothLEScan(context, bluetoothAdapter, resultReceiver);
        } else {
            bluetoothLEScan = null;
            Log.i(Constants.TAG, "Bluetooth Low Energy is not going to be performed");
        }

    }

    private boolean shouldStartLeScan(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
                && preferences.isBluetoothLEOn();
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
    public Set<DiscoverableBluetoothDevice> getDiscoveredClassicDevices() {
        if(classicBluetoothDiscovery != null) {
            return classicBluetoothDiscovery.getDiscoveredDevices();
        }

        return null;
    }

    /**
     * Stops the bluetooth device discovery
     */
    public void finishClassicBluetoothDiscovery() {
        if(bluetoothLEScan != null) {
            finishClassicDiscovery(Boolean.FALSE);
            bluetoothLEScan.scanLeDevices();
        } else {
            finishClassicDiscovery(Boolean.TRUE);
        }
    }

    private void finishClassicDiscovery(boolean notify) {
        if(classicBluetoothDiscovery == null) {
            Log.w(Constants.TAG, "There is no discoveries, ignoring finish discovery request");
            return;
        }

        if(notify) {
            classicBluetoothDiscovery.finishAndNotify();
        } else {
            classicBluetoothDiscovery.finish();
        }
    }

    /**
     * This method forces a discovery to finish (does not wait for the discovery
     * finished broadcast) and clear all the discovered devices
     */
    public void forceDiscoveryFinish() {
        Log.i(Constants.TAG, "Forcing discovery to finish");
        availableClassicBluetoothDevices = new HashSet<>();
        availableLEBluetoothDevices = new HashSet<>();
        finishClassicDiscovery(true);
    }

    /**
     * Returns true if any discovery has already started
     */
    public boolean hasClassicBluetoothDiscovery() {
        return classicBluetoothDiscovery != null;
    }

    public Set<Discoverable> getAvailableClassicBluetoothDevices() {
        return availableClassicBluetoothDevices;
    }

    public Set<Discoverable> getAvailableLEBluetoothDevices() {
        return availableLEBluetoothDevices;
    }

    public Set<Discoverable> getAvailableBluetoothDevices() {
        Set<Discoverable> bluetoothDevices = new HashSet<>(availableClassicBluetoothDevices);
        bluetoothDevices.addAll(availableLEBluetoothDevices);

        return bluetoothDevices;
    }
}
