package one.tribe.whatsnearme.deviceswithapp.persistency;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a device with app
 */
@DatabaseTable(tableName = "devices_with_app")
public class DeviceWithApp {

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField(canBeNull = false) private String phoneId;
    @DatabaseField(canBeNull = false) private String macAddress;

    public DeviceWithApp(String phoneId, String macAddress) {
        this.phoneId = phoneId;
        this.macAddress = macAddress;
    }

    public DeviceWithApp() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("id: ").append(id);
        sb.append(", phoneId: \"").append(phoneId);
        sb.append("\", macAddress: \"").append(macAddress);
        sb.append("\"}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceWithApp that = (DeviceWithApp) o;

        return macAddress.equals(that.macAddress);

    }

    @Override
    public int hashCode() {
        return macAddress.hashCode();
    }
}
