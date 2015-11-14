package one.tribe.whatsnearme.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 */
@DatabaseTable(tableName = "known_devices")
public class KnownDevice {

    @DatabaseField(id = true) private String id;
    @DatabaseField(canBeNull = false) private String macAddress;

    public KnownDevice(String id, String macAddress) {
        this.id = id;
        this.macAddress = macAddress;
    }

    public String getId() {
        return id;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
