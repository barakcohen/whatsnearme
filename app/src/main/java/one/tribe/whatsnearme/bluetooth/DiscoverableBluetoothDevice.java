package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import one.tribe.whatsnearme.Constants;
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

    @Override
    public String getExtras() {
        StringBuilder builder = new StringBuilder();

        builder.append("type: " + BluetoothDeviceType.getInstance(
                bluetoothDevice.getType()).getDescription());
        builder.append(", class: " + bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
        builder.append(", bonded: " + bluetoothDevice.getBondState());

        return builder.toString();
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
        Bundle b = in.readBundle();
        this.bluetoothDevice = b.getParcelable(Constants.BLUETOOTH_DEVICE_KEY);
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        Bundle b = new Bundle();
        b.putParcelable(Constants.BLUETOOTH_DEVICE_KEY, bluetoothDevice);
        parcel.writeBundle(b);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name: ").append(getName());
        sb.append(", address: ").append(getAddress());

        BluetoothDeviceType deviceType =
                BluetoothDeviceType.getInstance(bluetoothDevice.getType());
        sb.append(", type: " + deviceType.getDescription());
        sb.append(", class: " + bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
        sb.append(", bonded: " + bluetoothDevice.getBondState());
        return sb.toString();
    }
}
