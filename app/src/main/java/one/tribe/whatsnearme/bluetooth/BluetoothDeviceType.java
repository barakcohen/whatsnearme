package one.tribe.whatsnearme.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 *
 */
public enum BluetoothDeviceType {

    CLASSIC(BluetoothDevice.DEVICE_TYPE_CLASSIC, "Classic"),
    LOW_ENERGY(BluetoothDevice.DEVICE_TYPE_LE, "Low Energy"),
    DUAL(BluetoothDevice.DEVICE_TYPE_DUAL, "Dual"),
    UNKNOWN(BluetoothDevice.DEVICE_TYPE_UNKNOWN, "Unknown");

    private int code;
    private String description;

    BluetoothDeviceType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static BluetoothDeviceType getInstance(int typeCode) {
        for(BluetoothDeviceType deviceType : values()) {
            if(deviceType.code == typeCode) {
                return deviceType;
            }
        }

        return UNKNOWN;
    }
}
