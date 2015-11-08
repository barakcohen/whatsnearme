package one.tribe.whatsnearme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;
import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.bluetooth.WhatsNearMeDeviceManager;

/**
 * Receives a BluetoothAdapter.ACTION_DISCOVERY_FINISHED broadcast
 *
 * Once the discovery is finished, the prior list of devices is compared to the
 * list of discovered devices and the changes are notified through broadcasts.
 *
 * For each device discovered, get the features UUIDs to search for the WhatsNearMe
 * UUID. If the UUID is not found, query the device for the UUID list.
 *
 * The START_BLUETOOTH_DISCOVERY broadcast is sent to notify that the discovery
 * is done and another one shall start.
 */
public class BluetoothDiscoveryFinishedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG, "Finished bluetooth discovery");

        if(BluetoothDeviceManager.getInstance().hasClassicBluetoothDiscovery()) {

            BluetoothDeviceManager.getInstance().finishClassicBluetoothDiscovery();
            WhatsNearMeDeviceManager.getInstance().onDiscoveryFinished();


            notifyChanges(BluetoothDeviceManager.getInstance().getBluetoothDevicesChanges(), context);
            notifyChanges(WhatsNearMeDeviceManager.getInstance().getDevicesChanges(), context);
        }

    }

    private void notifyChanges(NetworkChanges changes, Context context) {
        if (changes.hasChanges()) {
            Log.i(Constants.TAG, "Notifying bluetooth devices changes");
            Intent networkChangeIntent = new Intent(Constants.NETWORK_CHANGED);
            networkChangeIntent.putParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES, changes.getNetworkEvents());

            context.sendOrderedBroadcast(networkChangeIntent, null);
        }
    }

}
