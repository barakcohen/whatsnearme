package one.tribe.whatsnearme.wifi;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.network.AbstractDiscoverable;
import one.tribe.whatsnearme.network.NetworkType;

/**
 * Abstraction of a a Wi-fi network
 */
public class DiscoverableWifiNetwork extends AbstractDiscoverable {

    private ScanResult scanResult;

    public DiscoverableWifiNetwork(ScanResult scanResult) {
        super(scanResult.SSID, scanResult.BSSID);
        this.scanResult = scanResult;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.WIFI;
    }

    @Override
    public String getExtras() {
        StringBuilder builder = new StringBuilder();
        builder.append("Level: " + scanResult.level);
        builder.append(", Frequency: " + scanResult.frequency);

        return builder.toString();
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
        Bundle b = in.readBundle();
        scanResult = b.getParcelable(Constants.WIFI_SCAN_RESULT_KEY);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        Bundle b = new Bundle();
        b.putParcelable(Constants.WIFI_SCAN_RESULT_KEY, scanResult);
        parcel.writeBundle(b);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name: ").append(getName());
        sb.append(", address: ").append(getAddress());
        sb.append(", level: " + scanResult.level);
        sb.append(", frequency: " + scanResult.frequency);
        sb.append('}');
        return sb.toString();
    }
}
