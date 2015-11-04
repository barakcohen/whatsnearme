package one.tribe.whatsnearme.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.bluetooth.WhatsNearMeDeviceManager;

/**
 * Receives a BluetoothDevice.ACTION_UUID broadcast. It's simply adds the
 * discovered device to the discovery devices list
 */
public class BluetoothUUIDFetchReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        Parcelable[] parcels = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

        Log.i(Constants.TAG, "UUID for device " + device +" fetched");
        WhatsNearMeDeviceManager.getInstance().onDiscoveredDeviceUUIDFetched(device, parcels);

    }
}
