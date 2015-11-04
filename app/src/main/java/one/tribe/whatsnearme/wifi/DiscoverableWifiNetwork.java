package one.tribe.whatsnearme.wifi;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import one.tribe.whatsnearme.network.AbstractDiscoverable;
import one.tribe.whatsnearme.network.NetworkType;

/**
 * Abstraction of a a Wi-fi network
 */
public class DiscoverableWifiNetwork extends AbstractDiscoverable {

    public DiscoverableWifiNetwork(ScanResult scanResult) {
        super(scanResult.SSID, scanResult.BSSID);
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.WIFI;
    }

    public static final Creator<DiscoverableWifiNetwork> CREATOR
            = new Creator<DiscoverableWifiNetwork>() {
        public DiscoverableWifiNetwork createFromParcel(Parcel in) {
            return new DiscoverableWifiNetwork(in);
        }

        public DiscoverableWifiNetwork[] newArray(int size) {
            return new DiscoverableWifiNetwork[size];
        }
    };

    private DiscoverableWifiNetwork(Parcel in) {
        super(in);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
