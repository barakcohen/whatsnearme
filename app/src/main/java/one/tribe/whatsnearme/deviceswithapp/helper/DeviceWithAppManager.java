package one.tribe.whatsnearme.deviceswithapp.helper;

import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;

/**
 * Manages the devices with app
 */
public interface DeviceWithAppManager {

    /**
     * Inserts a new device
     */
    void insertDevice(DeviceWithApp device);

    /**
     * Delete a device
     */
    void deleteDevice(DeviceWithApp device);

    /**
     * Updates a devices
     */
    void updateDevice(DeviceWithApp device);
}
