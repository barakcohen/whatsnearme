package one.tribe.whatsnearme.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.Discoverable;
import one.tribe.whatsnearme.network.NetworkChanges;
import one.tribe.whatsnearme.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Wi-fi networks that are within the range of the device.
 *
 * It's a singleton to keep the state of the discovered networks
 */
public class WifiNetworkManager extends NetworkManager {

    private static final WifiNetworkManager instance = new WifiNetworkManager();

    private List<Discoverable> availableNetworks;

    private WifiNetworkManager() {
        availableNetworks = new ArrayList<>();
    }

    public static WifiNetworkManager getInstance() {
        return instance;
    }

    /**
     * Returns the network changes since the last scan
     * @param newScanResults the new scan results to be compared with the last one
     * @return the networks changes
     */
    public NetworkChanges getNetworkChanges(List<ScanResult> newScanResults) {
        Log.i(Constants.TAG, "Merging wi-fi scans");

        List<Discoverable> newWifiNetworks = fromScanResult(newScanResults);

        Log.d(Constants.TAG, "Scanned Wi-fi networks: " + newWifiNetworks);
        Log.d(Constants.TAG, "Available Wi-fi networks before getChanges: " + newWifiNetworks);
        NetworkChanges changes = getChanges(newWifiNetworks, new ArrayList<>(availableNetworks));

        availableNetworks = new ArrayList<>(newWifiNetworks);
        Log.d(Constants.TAG, "Available Wi-fi networks after getChanges: " + newWifiNetworks);

        return changes;
    }

    private List<Discoverable> fromScanResult(List<ScanResult> newScanResults) {
        List<Discoverable> newConnections = new ArrayList<>();

        for(ScanResult newScanResult : newScanResults) {
            newConnections.add(fromScanResult(newScanResult));
        }

        return newConnections;
    }

    private Discoverable fromScanResult(ScanResult scanResult) {
        Discoverable network = new DiscoverableWifiNetwork(scanResult);

        return network;
    }

    public List<Discoverable> getAvailableNetworks() {
        return availableNetworks;
    }
}
