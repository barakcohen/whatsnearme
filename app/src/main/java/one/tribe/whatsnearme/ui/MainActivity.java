package one.tribe.whatsnearme.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.ScannerService;
import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;
import one.tribe.whatsnearme.bluetooth.WhatsNearMeDeviceManager;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.wifi.WifiNetworkManager;

public class MainActivity extends AppCompatActivity {

    private static final int DISCOVERABLE_REQUEST_CODE = 1311;

    private NetworkChangedReceiver networkChangeReceiver;
    private BluetoothStateChangedReceiver bluetoothStateChangedReceiver;

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;

    private ScannerService scannerService;
    private boolean serviceBound;
    private boolean deviceDiscoverableOption = Boolean.TRUE;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.TAG, "MainActivity: Creating");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.networkListView);

        listAdapter = new ExpandableListAdapter();

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listAdapter.setLayoutInflater(inflater);

        // setting list adapter
        expListView.setAdapter(listAdapter);

    }

    private void registerReceivers() {
        networkChangeReceiver = new NetworkChangedReceiver();
        IntentFilter networkChangedFilter = new IntentFilter(Constants.NETWORK_CHANGED);
        networkChangedFilter.setPriority(Constants.ACTIVITY_PRIORITY);
        registerReceiver(networkChangeReceiver, networkChangedFilter);

        bluetoothStateChangedReceiver = new BluetoothStateChangedReceiver();
        registerReceiver(bluetoothStateChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private void bindScannerService() {
        Intent intent = new Intent(this, ScannerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "MainActivity: Resuming");

        registerReceivers();
        updateGui();
        expListView.expandGroup(0);
        expListView.expandGroup(1);
        expListView.expandGroup(2);

        bindScannerService();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Log.i(Constants.TAG, "Device is not discoverable nor connectible");

                    if(deviceDiscoverableOption) {
                        Intent discoverableIntent = new
                                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST_CODE);
                    }
                }
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DISCOVERABLE_REQUEST_CODE) {
            if(resultCode == RESULT_CANCELED){
                this.deviceDiscoverableOption = Boolean.FALSE;
            } else {
                this.deviceDiscoverableOption = Boolean.TRUE;
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(Constants.TAG, "MainActivity: Pausing");
        unregisterReceiver(networkChangeReceiver);
        unregisterReceiver(bluetoothStateChangedReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.TAG, "MainActivity: Destroying");

        unbindService(serviceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_log) {

            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);

            return true;
        }

        if( id == R.id.action_stop_scanning) {
            if(item.getTitle().equals(getString(R.string.action_start_scanning))) {
                scannerService.startScanning();
                item.setTitle(getString(R.string.action_stop_scanning));
            } else {
                scannerService.stopScanning();
                item.setTitle(getString(R.string.action_start_scanning));
            }

            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_exit) {
            stopAndExit();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void stopAndExit() {
        scannerService.stop();
        finishAffinity();
    }

    private void updateGui() {

        Set<Discoverable> wifiNetworkList =
                WifiNetworkManager.getInstance().getAvailableNetworks();
        Set<Discoverable> bluetoothDeviceList =
                BluetoothDeviceManager.getInstance().getAvailableBluetoothDevices();
        Set<Discoverable> phoneWithAppList =
                WhatsNearMeDeviceManager.getInstance().getAvailableDevices();

        listAdapter.updateWifiNetworks(toStringList(wifiNetworkList));
        listAdapter.updateBlueToothDevices(toStringList(bluetoothDeviceList));
        listAdapter.updatePhoneWithApp(toStringList(phoneWithAppList));

    }

    private List<String>  toStringList(Set<Discoverable> discoverables) {
        List<String> stringList = new ArrayList<>();

        for(Discoverable discoverable : discoverables) {
            stringList.add(discoverable.getName() + ": " +discoverable.getAddress());
        }

        return stringList;
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(Constants.TAG, "MainActivity: Network Changed broadcast received");
                updateGui();
                setResultCode(Constants.ACTIVITY_RESULT_CODE);
            } catch(RuntimeException e) {
                Log.e(Constants.TAG, "Error updating interface", e);
            }
        }

    }

    /**
     * Receives a BluetoothAdapter.ACTION_STATE_CHANGED broadcast
     *
     * When the bluetooth is turned ON, start discovering devices
     */
    private class BluetoothStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            if(BluetoothAdapter.STATE_ON == state) {
                listAdapter.enableBluetoothIcon(Boolean.TRUE);
            } else if(BluetoothAdapter.STATE_OFF == state) {
                listAdapter.enableBluetoothIcon(Boolean.FALSE);
            }
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            ScannerService.LocalBinder binder = (ScannerService.LocalBinder) service;
            scannerService = binder.getService();
            serviceBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            scannerService = null;
            serviceBound = false;
        }
    };

}
