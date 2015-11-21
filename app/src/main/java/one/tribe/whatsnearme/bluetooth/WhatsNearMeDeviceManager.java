package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;
import one.tribe.whatsnearme.deviceswithapp.persistency.DevicesWithAppDao;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkManager;

/**
 * Manages the nearby devices with the app running
 */
public class WhatsNearMeDeviceManager extends NetworkManager {

    private static WhatsNearMeDeviceManager instance = new WhatsNearMeDeviceManager();

    public static WhatsNearMeDeviceManager getInstance() {
        return instance;
    }

    private Set<Discoverable> availableDevices;
    private Set<Discoverable> discoveredDevices;

    private DevicesWithAppDao devicesWithAppDao;

    private WhatsNearMeDeviceManager() {
        availableDevices = new HashSet<>();
        discoveredDevices = new HashSet<>();
    }

    /**
     * Initialize the DAO objects with the app Context
     */
    public void initDAO(Context context) {
        devicesWithAppDao = new DevicesWithAppDao(context);
    }

    /**
     * Return the devices visibility changes since the last discovery
     */
    public NetworkChanges getDevicesChanges() {
        Log.d(Constants.TAG, "Discovered Bluetooth devices with App: " + discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices with App before getChanges: " + availableDevices);
        NetworkChanges networkChanges =
                getChanges(discoveredDevices, new HashSet<>(availableDevices));


        availableDevices  = new HashSet<>(discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices with App after getChanges: " + availableDevices);

        discoveredDevices = new HashSet<>();

        return networkChanges;
    }

    /**
     * Adds a discovered device with the app running to the device list
     * @param discoveredDevice a discovered device through the bluetooth discovery
     */
    public void addDiscoveredDevice(BluetoothDevice discoveredDevice) {
        DiscoverableBluetoothDevice device =
                new DiscoverableBluetoothDevice(discoveredDevice);

        if(!discoveredDevices.contains(device) && deviceHasApp(device)) {
            Log.i(Constants.TAG, "Device with app found: " + device);
            discoveredDevices.add(device);
        }
    }

    private boolean deviceHasApp(DiscoverableBluetoothDevice device) {

        List<DeviceWithApp> devicesWithApp = devicesWithAppDao.getDevicesWithApp();

        for (DeviceWithApp deviceWithApp : devicesWithApp) {
            if(deviceWithApp.getMacAddress().equalsIgnoreCase(device.getAddress())) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Returns a set of the available devices
     */
    public Set<Discoverable> getAvailableDevices() {
        return new HashSet<>(availableDevices);
    }
}
