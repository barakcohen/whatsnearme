package one.tribe.whatsnearme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
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
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.notification.NotificationFormatter;
import one.tribe.whatsnearme.receiver.BluetoothDeviceFoundReceiver;
import one.tribe.whatsnearme.receiver.BluetoothDiscoveryFinishedReceiver;
import one.tribe.whatsnearme.receiver.BluetoothUUIDFetchReceiver;
import one.tribe.whatsnearme.ui.MainActivity;
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

    private WifiScanningStartReceiver wifiScanningStartReceiver;
    private BluetoothDeviceFoundReceiver deviceFoundReceiver;
    private BluetoothDiscoveryFinishedReceiver discoveryFinishedReceiver;
    private WifiScanResultsReceiver wifiScanResultsdReceiver;
    private BluetoothStateChangedReceiver bluetoothStateChangedReceiver;
    private BluetoothUUIDFetchReceiver bluetoothUUIDFetchReceiver;

    private BluetoothConnectionListener connectionListener;

    private NetworkChangedReceiver networkChangedReceiver;
    private Handler bluetoothBroadcastHandler;

    private AppPreferences preferences;

    private BlockingQueue<NetworkEvent> log;

    private boolean scanning = Boolean.TRUE;

    @Override
    public void onCreate() {
        super.onCreate();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiScanningStartReceiver = new WifiScanningStartReceiver();
        deviceFoundReceiver = new BluetoothDeviceFoundReceiver();
        discoveryFinishedReceiver = new BluetoothDiscoveryFinishedReceiver();
        wifiScanResultsdReceiver = new WifiScanResultsReceiver();
        bluetoothStateChangedReceiver = new BluetoothStateChangedReceiver();
        bluetoothUUIDFetchReceiver = new BluetoothUUIDFetchReceiver();

        registerReceiver(wifiScanningStartReceiver,
                new IntentFilter(Constants.START_WIFI_SCANNING));
        registerReceiver(deviceFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(wifiScanResultsdReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(bluetoothStateChangedReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(bluetoothUUIDFetchReceiver,
                new IntentFilter(BluetoothDevice.ACTION_UUID));

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

        log = new ArrayBlockingQueue(preferences.getLogLimit());

        Log.i(Constants.TAG, "Scanner service created!");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startConnectionListener();
        startBluetoothDiscovery();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startConnectionListener() {
        if(mBluetoothAdapter!= null && mBluetoothAdapter.isEnabled()) {
            Log.i(Constants.TAG, "Starting connection listener");
            connectionListener = new BluetoothConnectionListener(mBluetoothAdapter);
            Executors.newSingleThreadExecutor().execute(connectionListener);

            Log.i(Constants.TAG, "Connection started");
        }
    }

    public void stop() {
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanningStartReceiver);
        unregisterReceiver(deviceFoundReceiver);
        unregisterReceiver(discoveryFinishedReceiver);
        unregisterReceiver(wifiScanResultsdReceiver);
        unregisterReceiver(bluetoothStateChangedReceiver);
        unregisterReceiver(bluetoothUUIDFetchReceiver);
        unregisterReceiver(networkChangedReceiver);

        if(connectionListener != null) {
            connectionListener.stop();
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startBluetoothDiscovery() {
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
            BluetoothDeviceManager.getInstance().startDiscovery(this, mBluetoothAdapter, new BluetoothDiscoveryResultReceiver());
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

        Log.i(Constants.TAG, "Starting scanning wi-fi networks");
        wifiManager.startScan();

        startBluetoothDiscovery();
    }

    public void stopScanning() {
        scanning = false;
    }

    private class BluetoothDiscoveryResultReceiver extends ResultReceiver {

        public BluetoothDiscoveryResultReceiver() {
            super(bluetoothBroadcastHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(scanning) {
                Log.i(Constants.TAG, "Bluetooth device discovery is done, scheduling next execution");
                scheduleBluetoothDiscovery(preferences.getBluetoothDiscoveryInterval());
            }
        }
    }

    private class WifiScanningStartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(Constants.TAG, "Received a broadcasts to start scanning Wi-fi networks");

            if(scanning) {
                long scanInterval =  preferences.getWifiScanInterval();
                Log.d(Constants.TAG, "Scanning wi-fi again in: " + scanInterval);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Log.i(Constants.TAG, "Start scanning Wi-fi networks");
                        wifiManager.startScan();
                    }
                }, scanInterval);
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
            notifyEvent(NotificationFormatter.formatShortMessage(event),
                    NotificationFormatter.formatCompleteMessage(event));
        }
    }

    private void notifyEvent(String title, String text) {
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_whastnearme)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(12345, mBuilder.build());
    }

    public class LocalBinder extends Binder {
        public ScannerService getService() {
            return ScannerService.this;
        }
    }

}
