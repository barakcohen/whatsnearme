package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WhatsNearMeDeviceManager extends NetworkManager {

    private static WhatsNearMeDeviceManager instance = new WhatsNearMeDeviceManager();

    public static WhatsNearMeDeviceManager getInstance() {
        return instance;
    }

    private List<Discoverable> availableDevices;
    private List<Discoverable> discoveredDevices;
    private List<String> devicesWithAppCache;

    private WhatsNearMeDeviceManager() {
        availableDevices = new ArrayList<>();
        discoveredDevices = new ArrayList<>();
        devicesWithAppCache = new ArrayList<>();
    }

    /**
     * Return the the devices visibility changes since the last discovery
     * @return the device changes
     */
    public NetworkChanges getDevicesChanges() {
        Log.d(Constants.TAG, "Discovered Bluetooth devices with App: " + discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices with App before getChanges: " + availableDevices);
        NetworkChanges networkChanges =
                getChanges(discoveredDevices, new ArrayList<>(availableDevices));


        availableDevices  = new ArrayList<>(discoveredDevices);
        Log.d(Constants.TAG, "Available Bluetooth devices with App after getChanges: " + availableDevices);

        discoveredDevices = new ArrayList<>();

        return networkChanges;
    }

    public void onDiscoveredDeviceUUIDFetched(BluetoothDevice bluetoothDevice, Parcelable[] parcels) {
        if(deviceHasApp(bluetoothDevice, parcels)) {
            addDeviceToCache(bluetoothDevice);
        }
    }

    public void onDiscoveryFinished() {
        List<DiscoverableBluetoothDevice> discoveredDevices =
                BluetoothDeviceManager.getInstance().getDiscoveredDevices();

        Log.i(Constants.TAG, "Bluetooth adapter discovery finished. Total discovered: " + discoveredDevices.size());
        for(DiscoverableBluetoothDevice device : discoveredDevices) {
            BluetoothDevice bluetoothDevice = device.getBluetoothDevice();

            // For all devices discovered, check if the app is installed
            if(deviceHasApp(bluetoothDevice)) {
                Log.i(Constants.TAG, "Another device with WhatsNearApp found: " + device);
                addDeviceToCache(bluetoothDevice);
                this.discoveredDevices.add(new DiscoverableDeviceWithApp(bluetoothDevice));
            }else if(bluetoothDevice.getUuids() == null) {
                Log.i(Constants.TAG, "Trying to fetch the WhatsNearMe UUID for device " + device);
                boolean started = bluetoothDevice.fetchUuidsWithSdp();
                Log.i(Constants.TAG, "Fetch started for device: " + started);
            }
        }
    }

    private boolean deviceHasApp(BluetoothDevice device) {
        return deviceHasApp(device, device.getUuids());
    }

    private boolean deviceHasApp(BluetoothDevice device, Parcelable[] parcels) {
        if(devicesWithAppCache.contains(device.getAddress())) {
            return Boolean.TRUE;
        }

        if(parcels == null) {
            return Boolean.FALSE;
        }

        Log.i(Constants.TAG, "Checking for the WhatsNearMe UUID for discovered device " + device);

        for(Parcelable parcelUuid : parcels) {
            ParcelUuid uuid = (ParcelUuid) parcelUuid;
            Log.d(Constants.TAG, "UUID "+ uuid + " found for device " +device);
            if(Constants.MY_UUID_INSECURE.equals(uuid.getUuid())) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }


    private void addDeviceToCache(BluetoothDevice bluetoothDevice) {
        if(!devicesWithAppCache.contains(bluetoothDevice.getAddress())) {
            Log.i(Constants.TAG, "Adding new device with the App to cache: " + bluetoothDevice.getAddress());
            devicesWithAppCache.add(bluetoothDevice.getAddress());
        }
    }

    public List<Discoverable> getAvailableDevices() {
        return availableDevices;
    }
}
