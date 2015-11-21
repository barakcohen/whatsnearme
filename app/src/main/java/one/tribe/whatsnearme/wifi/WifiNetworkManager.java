package one.tribe.whatsnearme.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import one.tribe.whatsnearme.AppPreferences;
import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkManager;

/**
 * Manages the Wi-fi networks that are within the range of the device.
 *
 * It's a singleton to keep the state of the discovered networks
 */
public class WifiNetworkManager extends NetworkManager {

    private static final WifiNetworkManager instance = new WifiNetworkManager();

    private Set<Discoverable> availableNetworks;

    private WifiNetworkManager() {
        availableNetworks = new HashSet<>();
    }

    private AppPreferences preferences;

    public static WifiNetworkManager getInstance() {
        return instance;
    }

    /**
     * Returns the network changes since the last scan
     * @param newScanResults the new scan results to be compared with the last one
     * @return the networks changes
     */
    public NetworkChanges getNetworkChanges(Set<ScanResult> newScanResults) {
        Log.i(Constants.TAG, "Merging wi-fi scans");

        Set<Discoverable> newWifiNetworks = fromScanResult(newScanResults);

        Log.d(Constants.TAG, "Scanned Wi-fi networks: " + newWifiNetworks);
        Log.d(Constants.TAG, "Available Wi-fi networks before getChanges: " + newWifiNetworks);
        NetworkChanges changes = getChanges(newWifiNetworks, new HashSet<>(availableNetworks));

        availableNetworks = new HashSet<>(newWifiNetworks);
        Log.d(Constants.TAG, "Available Wi-fi networks after getChanges: " + newWifiNetworks);

        return changes;
    }

    private Set<Discoverable> fromScanResult(Set<ScanResult> newScanResults) {
        Set<Discoverable> newConnections = new HashSet<>();

        for(ScanResult newScanResult : newScanResults) {
            filter(newConnections, newScanResult);
        }

        return newConnections;
    }

    private void filter(Set<Discoverable> newConnections, ScanResult newScanResult) {
        int minLevel = preferences.getWifiMinLevel();

        if(newScanResult.level > minLevel) {
            newConnections.add(fromScanResult(newScanResult));
        }
    }

    private Discoverable fromScanResult(ScanResult scanResult) {
        Discoverable network = new DiscoverableWifiNetwork(scanResult);

        return network;
    }

    public Set<Discoverable> getAvailableNetworks() {
        return new HashSet<>(availableNetworks);
    }

    public void setPreferences(AppPreferences preferences) {
        this.preferences = preferences;
    }

    public void stopScanning() {
        availableNetworks = new HashSet<>();
    }
}
