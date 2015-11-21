package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import one.tribe.whatsnearme.Constants;

/**
 *
 */
public class BluetoothDiscoverySentinel {

    private Handler handler;

    private long discoveryTimeout;

    public BluetoothDiscoverySentinel(long discoveryTimeout) {
        this.discoveryTimeout = discoveryTimeout;
        handler = new Handler();
    }

    public void notifyDiscoveryStart(BluetoothAdapter bluetoothAdapter) {
        Log.i(Constants.TAG, "Bluetooth discovery started, scheduling discovery timeout task");
        handler.postDelayed(new ForceDiscoveryTask(bluetoothAdapter), discoveryTimeout);
    }

    public void notifyDiscoveryFinish() {
        Log.i(Constants.TAG, "Bluetooth discovery finished, cancelling timeout task");
        handler.removeCallbacksAndMessages(null);
    }

    private class ForceDiscoveryTask implements Runnable {

        private final BluetoothAdapter bluetoothAdapter;

        public ForceDiscoveryTask(BluetoothAdapter bluetoothAdapter) {
            this.bluetoothAdapter = bluetoothAdapter;
        }

        @Override
        public void run() {
            Log.w(Constants.TAG, "Bluetooth discovery time is up, forcing it's finish");
            BluetoothDeviceManager.getInstance().forceDiscoveryFinish(bluetoothAdapter);
        }
    }
}
