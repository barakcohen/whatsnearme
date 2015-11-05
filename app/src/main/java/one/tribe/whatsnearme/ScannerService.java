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
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
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

    private SharedPreferences sharedPref;
    private static final long WIFI_SCAN_DELAY= 10000;
    private static final long BLUETOOTH_SCAN_DELAY = 1000;
    private static final long BLUETOOTH_DISCOVERY_TIME = 12000;
    private static final int LOG_LIMIT = 100;

    private BlockingQueue<NetworkEvent> log;

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
        networkChangedReceiver = new NetworkChangedReceiver();


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
        registerReceiver(networkChangedReceiver,
                new IntentFilter(Constants.NETWORK_CHANGED));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.i(Constants.TAG, "Starting scanning wi-fi networks");
        wifiManager.startScan();

        if (mBluetoothAdapter == null) {
            Log.w(Constants.TAG, "Device does not support Bluetooth!");
        }

        bluetoothBroadcastHandler = new Handler();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Log.i(Constants.TAG, "LOG LIMIT: " + sharedPref.getString(getString(R.string.pref_log_limit), "NULL"));

        log = new ArrayBlockingQueue(100);


        Log.i(Constants.TAG, "Scanner service created!");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startConnectionListener();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startConnectionListener() {
        if(mBluetoothAdapter!= null && mBluetoothAdapter.isEnabled()) {
            Log.i(Constants.TAG, "Starting connection listener");
            connectionListener = new BluetoothConnectionListener(mBluetoothAdapter);
            Executors.newSingleThreadExecutor().execute(connectionListener);

            Log.i(Constants.TAG, "Connection started");

            startBluetoothDiscovery();
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
        if (mBluetoothAdapter.isDiscovering()) {
            Log.i(Constants.TAG, "A discovery is still running, skipping this discovery");
            scheduleBluetoothDiscovery(BLUETOOTH_DISCOVERY_TIME);
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

    private class BluetoothDiscoveryResultReceiver extends ResultReceiver {

        public BluetoothDiscoveryResultReceiver() {
            super(bluetoothBroadcastHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.i(Constants.TAG, "Bluetooth device discovery is done, scheduling next execution");
            scheduleBluetoothDiscovery(BLUETOOTH_SCAN_DELAY);
        }
    }

    private class WifiScanningStartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(Constants.TAG, "Received a broadcasts to start scanning Wi-fi networks");

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Log.i(Constants.TAG, "Start scanning Wi-fi networks");
                    wifiManager.startScan();
                }
            }, WIFI_SCAN_DELAY);
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

            if(BluetoothAdapter.STATE_ON == state) {
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

            if(!WhastNearMeApplication.isActivityVisible()) {
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
                .setSmallIcon(one.tribe.whatsnearme.R.drawable.radar)
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
