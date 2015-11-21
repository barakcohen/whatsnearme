package one.tribe.whatsnearme.deviceswithapp.helper;

import android.content.Context;
import android.util.Log;

import java.util.List;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;
import one.tribe.whatsnearme.deviceswithapp.persistency.DevicesWithAppDao;

/**
 * Handles the device with app. All the CRUD operations regarding the devices
 * with ap list must be delegated to this class
 */
public class DeviceWithAppListHandler implements DeviceWithAppManager {
    private DevicesWithAppDao devicesWithAppDao;

    private List<DeviceWithApp> devicesWithApp;

    public DeviceWithAppListHandler(Context context) {
        Log.i(Constants.TAG, "Initializing DevicesWithAppDao");
        devicesWithAppDao = new DevicesWithAppDao(context);

        devicesWithApp = devicesWithAppDao.getDevicesWithApp();
        Log.i(Constants.TAG, "Devices with App: " + devicesWithApp);
    }

    public void deleteDevice(DeviceWithApp device) {
        Log.i(Constants.TAG, "Removing device from the database: " + device);
        devicesWithAppDao.deleteDevice(device);
        devicesWithApp.remove(device);
    }

    public void updateDevice(DeviceWithApp device) {
        Log.i(Constants.TAG, "Updating device in database: " + device);
        devicesWithAppDao.updateDevice(device);
    }

    public List<DeviceWithApp> getDevicesWithApp() {
        return devicesWithApp;
    }

    public void insertDevice(DeviceWithApp device) {
        devicesWithAppDao.insertDevice(device);
        Log.i(Constants.TAG, "Device added to database: " + device);

        devicesWithApp = devicesWithAppDao.getDevicesWithApp();
    }

    public void close() {
        devicesWithAppDao.close();
    }
}
