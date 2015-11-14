package one.tribe.whatsnearme.model;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

/**
 *
 */
public class KnownDevicesDao {

    private RuntimeExceptionDao<KnownDevice, Integer> dao;

    public KnownDevicesDao(Context context) {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        dao = databaseHelper.getDAO(KnownDevice.class);
    }

    public void insertDevice(String id, String macAddress) {
        KnownDevice device = new KnownDevice(id, macAddress);
        dao.createIfNotExists(device);
    }

    public void updateDevice(KnownDevice device) {
        dao.update(device);
    }

    public void deleteDevice(KnownDevice device) {
        dao.delete(device);
    }

    public List<KnownDevice> getKnownDevices() {
        return dao.queryForAll();
    }

    public void close() {
        OpenHelperManager.releaseHelper();
    }
}
