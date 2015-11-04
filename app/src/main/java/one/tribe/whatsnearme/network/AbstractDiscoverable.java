package one.tribe.whatsnearme.network;

import android.os.Parcel;

/**
 *
 */
public abstract class AbstractDiscoverable implements Discoverable {

    private String name;
    private String address;

    public AbstractDiscoverable(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public AbstractDiscoverable(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractDiscoverable that = (AbstractDiscoverable) o;

        return !(address != null ? !address.equals(that.address) : that.address != null);

    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name: ").append(name);
        sb.append(", address: ").append(address);
        sb.append('}');
        return sb.toString();
    }
}
