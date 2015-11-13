package one.tribe.whatsnearme.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;

/**
 * Class that represents a Bluetooth Low Energy scan
 */
public class BluetoothLEScan {

    private Context serviceContext;

    private BluetoothAdapter bluetoothAdapter;

    private Set<Discoverable> discoveredDevices;

    private ResultReceiver receiver;

    private DefaultScanCallback scanCallback;

    public BluetoothLEScan(Context serviceContext, BluetoothAdapter bluetoothAdapter, ResultReceiver receiver) {
        this.serviceContext = serviceContext;
        this.bluetoothAdapter = bluetoothAdapter;
        discoveredDevices = new HashSet<>();
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.i(Constants.TAG, "Stop scanning for Bluetooth LE Devices (API 21+)");
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                } else {
                    Log.i(Constants.TAG, "Stop scanning for Bluetooth LE Devices (API 21-)");
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                }

                Log.d(Constants.TAG, "Total of Bluetooth LE Devices Discovered: " + discoveredDevices.size());

                broadcastDeviceChanges(BluetoothDeviceManager.getInstance().getBluetoothLEDevicesChanges());

                receiver.send(1, new Bundle());

            }
        }, Constants.BLUETOOTH_LE_SCAN_PERIOD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(Constants.TAG, "Starting scanning for Bluetooth LE Devices (API 21+)");
            scanCallback = new DefaultScanCallback();
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        } else {
            Log.i(Constants.TAG, "Starting scanning for Bluetooth LE Devices (API 21-)");
            bluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    private void broadcastDeviceChanges(NetworkChanges changes) {

        if(changes.hasChanges()) {
            Intent networkChangeIntent = new Intent(Constants.NETWORK_CHANGED);
            networkChangeIntent.putParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES, changes.getNetworkEvents());

            serviceContext.sendOrderedBroadcast(networkChangeIntent, null);
        }
    }

    private void addDiscoveredDevice(BluetoothDevice device) {
        Log.i(Constants.TAG, "Bluetooth LE Device found: " + device);
        Discoverable discoveredDevice = new DiscoverableBluetoothDevice(device);

        if(!discoveredDevices.contains(discoveredDevice)) {
            discoveredDevices.add(discoveredDevice);
        }

        // let Device Manager knows a new discovered LE device was found
        BluetoothDeviceManager.getInstance().addDiscoveredLEDevice(device);

        // broadcast the new device for all listening components
        NetworkChanges changes = new NetworkChanges(Collections.singleton(discoveredDevice), Collections.EMPTY_SET);
        broadcastDeviceChanges(changes);
    }

    public Set<Discoverable> getDiscoveredDevices() {
        return discoveredDevices;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    addDiscoveredDevice(device);

                }
            };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class DefaultScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(Constants.TAG, "Bluetooth LE Scan results are available");
            addDiscoveredDevice(result.getDevice());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(Constants.TAG, "Bluetooth LE Scan failed! Error code: " + errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(Constants.TAG, "Bluetooth LE Scan batch results are available");
            for(ScanResult scanResult : results){
                addDiscoveredDevice(scanResult.getDevice());
            }
        }
    }
}
