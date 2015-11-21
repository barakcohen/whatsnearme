package one.tribe.whatsnearme;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import one.tribe.whatsnearme.bluetooth.BluetoothConnectionListener;
import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;
import one.tribe.whatsnearme.bluetooth.BluetoothDiscoverySentinel;
import one.tribe.whatsnearme.bluetooth.WhatsNearMeDeviceManager;
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.notification.NotificationManager;
import one.tribe.whatsnearme.receiver.BluetoothDeviceFoundReceiver;
import one.tribe.whatsnearme.receiver.BluetoothDiscoveryFinishedReceiver;
import one.tribe.whatsnearme.wifi.WifiNetworkManager;
import one.tribe.whatsnearme.wifi.WifiScanResultsReceiver;

/**
 * Service that start scanning for Wi-fi networks, bluetooth devices and phone
 * with the app running and manage the scanning events.
 *
 * This service starts with application and runs in background until the
 * application is alive.
 */
public class ScannerService extends Service {

    private WifiManager wifiManager;
    private BluetoothAdapter mBluetoothAdapter;

    private final IBinder mBinder = new LocalBinder();

    private BluetoothDeviceFoundReceiver deviceFoundReceiver;
    private BluetoothDiscoveryFinishedReceiver discoveryFinishedReceiver;
    private WifiScanResultsReceiver wifiScanResultsReceiver;
    private BluetoothStateChangedReceiver bluetoothStateChangedReceiver;

    private BluetoothConnectionListener connectionListener;

    private NetworkChangedReceiver networkChangedReceiver;
    private Handler bluetoothBroadcastHandler;

    private AppPreferences preferences;

    private BlockingQueue<NetworkEvent> log;

    private boolean scanning = Boolean.TRUE;

    private BluetoothDiscoverySentinel discoverySentinel;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.TAG, "Creating ScannerService");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        deviceFoundReceiver = new BluetoothDeviceFoundReceiver();
        discoveryFinishedReceiver = new BluetoothDiscoveryFinishedReceiver();
        wifiScanResultsReceiver = new WifiScanResultsReceiver();
        bluetoothStateChangedReceiver = new BluetoothStateChangedReceiver();

        registerReceiver(deviceFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(wifiScanResultsReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(bluetoothStateChangedReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter networkChangedFilter = new IntentFilter(Constants.NETWORK_CHANGED);
        networkChangedFilter.setPriority(Constants.SERVICE_PRIORITY);
        registerReceiver(networkChangedReceiver, networkChangedFilter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.i(Constants.TAG, "Starting scanning wi-fi networks");
        wifiManager.startScan();

        if (mBluetoothAdapter == null) {
            Log.w(Constants.TAG, "Device does not support Bluetooth!");
        }

        bluetoothBroadcastHandler = new Handler();

        preferences = new AppPreferences(this);

        WifiNetworkManager.getInstance().setPreferences(preferences);

        log = new ArrayBlockingQueue(preferences.getLogLimit());

        WhatsNearMeDeviceManager.getInstance().initDAO(this);

        long bluetoothDiscoveryTimeout = 60000;
        discoverySentinel = new BluetoothDiscoverySentinel(bluetoothDiscoveryTimeout);

        startBluetoothDiscovery();

        Log.i(Constants.TAG, "Scanner service created!");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Stops the service
     */
    public void stop() {
        Log.i(Constants.TAG, "Stopping ScannerService");
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(Constants.TAG, "ScannerService onDestroy");
        unregisterReceiver(deviceFoundReceiver);
        unregisterReceiver(discoveryFinishedReceiver);
        unregisterReceiver(wifiScanResultsReceiver);
        unregisterReceiver(bluetoothStateChangedReceiver);
        unregisterReceiver(networkChangedReceiver);

        if(connectionListener != null) {
            connectionListener.stop();
        }

        discoverySentinel.notifyDiscoveryFinish();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startBluetoothDiscovery() {
        if(mBluetoothAdapter == null) {
            Log.w(Constants.TAG, "[ScannerService] Device does not support Bluetooth!");
            return;
        }

        if(!scanning) {
            Log.i(Constants.TAG, "Scan is off. Skipping bluetooth discovery");
            return;
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Log.i(Constants.TAG, "Bluetooth is off, turning it on...");
            boolean enable = mBluetoothAdapter.enable();
            Log.i(Constants.TAG, "Bluetooth adapter startup begun: " + enable);
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            Log.i(Constants.TAG, "A discovery is still running, skipping this discovery");
            scheduleBluetoothDiscovery(preferences.getBluetoothDiscoveryInterval());
        } else {
            Log.i(Constants.TAG, "Starting another Bluetooth discovery");
            BluetoothDeviceManager.getInstance().
                    startDiscovery(this, mBluetoothAdapter, new BluetoothDiscoveryResultReceiver());
            discoverySentinel.notifyDiscoveryStart(mBluetoothAdapter);
        }
    }

    private void scheduleBluetoothDiscovery(long delay) {
        bluetoothBroadcastHandler.postDelayed(new Runnable() {
            public void run() {
                startBluetoothDiscovery();
            }
        }, delay);
    }

    public BlockingQueue<NetworkEvent> getLog() {
        return log;
    }

    public void startScanning() {
        scanning = true;

        registerReceiver(wifiScanResultsReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        Log.i(Constants.TAG, "Starting scanning wi-fi networks");
        wifiManager.startScan();

        startBluetoothDiscovery();
    }

    public void stopScanning() {
        scanning = false;

        Log.i(Constants.TAG, "Stop scanning for bluetooth devices");
        BluetoothDeviceManager.getInstance().stopScanning();

        Log.i(Constants.TAG, "Stop receiving wi-fi scan results");
        unregisterReceiver(wifiScanResultsReceiver);
        WifiNetworkManager.getInstance().stopScanning();
    }

    private class BluetoothDiscoveryResultReceiver extends ResultReceiver {

        public BluetoothDiscoveryResultReceiver() {
            super(bluetoothBroadcastHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(scanning) {
                discoverySentinel.notifyDiscoveryFinish();

                Log.i(Constants.TAG, "Bluetooth device discovery is done, scheduling next execution");
                scheduleBluetoothDiscovery(preferences.getBluetoothDiscoveryInterval());

            }
        }
    }

    /**
     * Receives a BluetoothAdapter.ACTION_STATE_CHANGED broadcast
     *
     * When the bluetooth is turned ON, start discovering devices
     */
    public class BluetoothStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            if(BluetoothAdapter.STATE_ON == state && scanning) {
                Log.i(Constants.TAG, "Bluetooth is turned on, requesting discovery");
                startBluetoothDiscovery();
            }
        }
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<NetworkEvent> networkEvents =
                    intent.getParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES);
            logEvents(networkEvents);

            int resultCode = getResultCode();

            Log.d(Constants.TAG, "Result code: " + resultCode + ", notifications on: " + preferences.isNotificationsOn());
            if(Constants.ACTIVITY_RESULT_CODE != resultCode && preferences.isNotificationsOn()) {
                showNotifications(networkEvents);
            }
        }

    }

    private void logEvents(List<NetworkEvent> networkEvents) {
        for(NetworkEvent event : networkEvents) {
            if(log.remainingCapacity() == 0) {
                log.poll();
            }

            log.add(event);
        }
    }

    private void showNotifications(List<NetworkEvent> networkEvents) {

        for(NetworkEvent event : networkEvents) {
            NotificationManager.getInstance().notifyEvent(this, event);
        }
    }

    public class LocalBinder extends Binder {
        public ScannerService getService() {
            return ScannerService.this;
        }
    }

}
