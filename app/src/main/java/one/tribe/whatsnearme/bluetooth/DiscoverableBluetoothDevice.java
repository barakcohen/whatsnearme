package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import one.tribe.whatsnearme.network.AbstractDiscoverable;
import one.tribe.whatsnearme.network.NetworkType;

/**
 *
 */
public class DiscoverableBluetoothDevice extends AbstractDiscoverable {

    private BluetoothDevice bluetoothDevice;

    public DiscoverableBluetoothDevice(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice.getName(), bluetoothDevice.getAddress());
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.BLUETOOTH;
    }

    public static final Parcelable.Creator<DiscoverableBluetoothDevice> CREATOR
            = new Parcelable.Creator<DiscoverableBluetoothDevice>() {
        public DiscoverableBluetoothDevice createFromParcel(Parcel in) {
            return new DiscoverableBluetoothDevice(in);
        }

        public DiscoverableBluetoothDevice[] newArray(int size) {
            return new DiscoverableBluetoothDevice[size];
        }
    };

    private DiscoverableBluetoothDevice(Parcel in) {
       super(in);
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name: ").append(bluetoothDevice.getName());
        sb.append(", address:").append(bluetoothDevice.getAddress());
        sb.append('}');
        return sb.toString();
    }
}
