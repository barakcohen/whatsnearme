package one.tribe.whatsnearme.deviceswithapp.persistency;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import one.tribe.whatsnearme.database.DatabaseHelper;

/**
 *  Dao to manage device with app data
 */
public class DevicesWithAppDao {

    private RuntimeExceptionDao<DeviceWithApp, Integer> dao;

    public DevicesWithAppDao(Context context) {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        dao = databaseHelper.getDAO(DeviceWithApp.class);
    }

    public void insertDevice(DeviceWithApp device) {
        dao.createIfNotExists(device);
    }

    /**
     * Updates a device in the database
     */
    public void updateDevice(DeviceWithApp device) {
        dao.update(device);
    }

    /**
     * Deletes a device with app in the database
     */
    public void deleteDevice(DeviceWithApp device) {
        dao.delete(device);
    }

    /**
     * Returns all devices with app in the database
     */
    public List<DeviceWithApp> getDevicesWithApp() {
        return dao.queryForAll();
    }

    /**
     * Closes the DAO an releases the connection
     */
    public void close() {
        OpenHelperManager.releaseHelper();
    }
}
