package one.tribe.whatsnearme.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.WhastNearMeApplication;
import one.tribe.whatsnearme.bluetooth.BluetoothDeviceManager;
import one.tribe.whatsnearme.bluetooth.WhatsNearMeDeviceManager;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.wifi.WifiNetworkManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NetworkChangedReceiver networkChangeReceiver;

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        networkChangeReceiver = new NetworkChangedReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(Constants.NETWORK_CHANGED));

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.networkListView);

        listAdapter = new ExpandableListAdapter();

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listAdapter.setLayoutInflater(inflater);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WhastNearMeApplication.activityResumed();
        updateGui();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WhastNearMeApplication.activityResumed();
        updateGui();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WhastNearMeApplication.activityPaused();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        WhastNearMeApplication.activityPaused();
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
            return true;
        }

        if (id == R.id.action_log) {

            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                updateGui();
            } catch(RuntimeException e) {
                Log.e(Constants.TAG, "Erro ao atualizar a interface", e);
            }
        }

    }

    private void updateGui() {

        List<Discoverable> wifiNetworkList =
                WifiNetworkManager.getInstance().getAvailableNetworks();
        List<Discoverable> bluetoothDeviceList =
                BluetoothDeviceManager.getInstance().getAvailableDevices();
        List<Discoverable> phoneWithAppList =
                WhatsNearMeDeviceManager.getInstance().getAvailableDevices();

        listAdapter.updateWifiNetworks(toStringList(wifiNetworkList));
        listAdapter.updateBlueToothDevices(toStringList(bluetoothDeviceList));
        listAdapter.updatePhoneWithApp(toStringList(phoneWithAppList));

    };

    private List<String>  toStringList(List<Discoverable> discoverables) {
        List<String> stringList = new ArrayList<>();

        for(Discoverable discoverable : discoverables) {
            stringList.add(discoverable.getName() + ": " +discoverable.getAddress());
        }

        return stringList;
    }


}
