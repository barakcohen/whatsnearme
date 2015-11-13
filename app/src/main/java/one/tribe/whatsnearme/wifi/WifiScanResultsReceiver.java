package one.tribe.whatsnearme.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.NetworkChanges;

import java.util.HashSet;
import java.util.List;

/**
 * Receives a WifiManager.SCAN_RESULTS_AVAILABLE_ACTION broadcast
 *
 * Once the discovery is finished, the prior list of networks is compared to the
 * list of discovered networks and the changes are notified through broadcasts.
 *
 * The START_WIFI_SCANNING broadcast is sent to nofify that the scanning is done
 * and another one shall start.
 */
public class WifiScanResultsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG, "Broadcast SCAN_RESULTS_AVAILABLE_ACTION received");
        WifiManager wifiManager = getWifiManager(context);

        if(wifiManager == null) {
            Log.w(Constants.TAG, "Wifi manager is null, ignoring scan");
            return;
        }

        List<ScanResult> scanResults = wifiManager.getScanResults();

        NetworkChanges changes = WifiNetworkManager.getInstance().getNetworkChanges(new HashSet<>(scanResults));

        if(changes.hasChanges()) {

            Intent networkChangeIntent = new Intent(Constants.NETWORK_CHANGED);
            networkChangeIntent.putParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES, changes.getNetworkEvents());

            Log.d(Constants.TAG, "Added Wi-fi Networks: " + changes.getNewItems());
            Log.d(Constants.TAG, "Removed Wi-fi Networks: " + changes.getGoneItems());

            context.sendOrderedBroadcast(networkChangeIntent, null);
        } else {
            Log.i(Constants.TAG, "No changes in the Wifi networks");
        }
    }

    private WifiManager getWifiManager(Context context) {

        if(context != null && context.getApplicationContext() != null) {
            return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        return null;
    }
}