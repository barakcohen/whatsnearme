package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import one.tribe.whatsnearme.Constants;

import java.io.IOException;

/**
 * Bluetooth connection listener
 */
public class BluetoothConnectionListener implements Runnable {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket serverSocket;

    public BluetoothConnectionListener(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    private void createServerSocket() {
        Log.i(Constants.TAG, "Creating insecure RFCOMM server socket");

        try {
            serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    "WhatsNearMe", Constants.MY_UUID_INSECURE);

            Log.i(Constants.TAG, "Insecure RFCOMM server socket created");
        } catch (IOException e) {
            Log.e(Constants.TAG, "Creating insecure RFCOMM server Socket failed", e);
        }
    }

    @Override
    public void run() {
        createServerSocket();
    }


    private void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.w(Constants.TAG, "Error closing RFCOMM server socket", e);
        }
    }

    public void stop() {
        closeServerSocket();
    }
}
