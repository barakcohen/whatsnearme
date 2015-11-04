package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;

import one.tribe.whatsnearme.network.NetworkType;

/**
 *
 */
public class DiscoverableDeviceWithApp extends DiscoverableBluetoothDevice {

    public DiscoverableDeviceWithApp(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice);
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.PHONE_WITH_APP;
    }
}
