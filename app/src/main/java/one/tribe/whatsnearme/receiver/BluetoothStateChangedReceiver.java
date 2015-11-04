package one.tribe.whatsnearme.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import one.tribe.whatsnearme.Constants;

/**
 * Receives a BluetoothAdapter.ACTION_STATE_CHANGED broadcast
 *
 * When the bluetooth is turned ON, start discovering devices
 */
public class BluetoothStateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

        if(BluetoothAdapter.STATE_ON == state) {
            Log.i(Constants.TAG, "Bluetooth is turned on, requesting discovery");
            Intent startDiscoveryIntent = new Intent(Constants.START_BLUETOOTH_DISCOVERY);
            context.sendBroadcast(startDiscoveryIntent);
        }
    }
}
