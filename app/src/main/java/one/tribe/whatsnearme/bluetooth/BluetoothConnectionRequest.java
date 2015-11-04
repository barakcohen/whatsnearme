package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import one.tribe.whatsnearme.Constants;

import java.io.IOException;

/**
 *
 */
public class BluetoothConnectionRequest implements Runnable {

    private BluetoothDevice bluetoothDevice;

    public BluetoothConnectionRequest(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public void run() {
        try {
            Log.i(Constants.TAG, "Trying to connect to device: " + bluetoothDevice.getName());
            BluetoothSocket client = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(Constants.MY_UUID_INSECURE);

            Log.i(Constants.TAG, "Another device running WhatsNearMe is found: " + bluetoothDevice.getName());
            close(client);
        } catch (IOException e) {
            Log.w(Constants.TAG, "Failed to connect to device " + bluetoothDevice.getName(), e);
        }
    }

    private void close(BluetoothSocket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Erro closing client socket from remote device: " + socket.getRemoteDevice().getName());
        }
    }
}
