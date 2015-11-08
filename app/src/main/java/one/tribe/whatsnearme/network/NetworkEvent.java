package one.tribe.whatsnearme.network;

import android.os.Parcel;
import android.os.Parcelable;

import one.tribe.whatsnearme.bluetooth.Util;

/**
 *
 */
public class NetworkEvent implements Parcelable {

    private NetworkType type;
    private NetworkEventType eventType;
    private String networkName;
    private String networkAddress;
    private String timestamp;

    public NetworkEvent(Discoverable discoverable, NetworkEventType eventType) {
        this.type = discoverable.getNetworkType();
        this.networkName = discoverable.getName();
        this.networkAddress = discoverable.getAddress();
        this.eventType = eventType;
        this.timestamp = Util.getDate();
    }

    protected NetworkEvent(Parcel in) {
        type = NetworkType.getInstance(in.readString());
        eventType = NetworkEventType.getInstance(in.readString());
        networkName = in.readString();
        networkAddress = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<NetworkEvent> CREATOR = new Creator<NetworkEvent>() {
        @Override
        public NetworkEvent createFromParcel(Parcel in) {
            return new NetworkEvent(in);
        }

        @Override
        public NetworkEvent[] newArray(int size) {
            return new NetworkEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type.serialize());
        parcel.writeString(eventType.serialize());
        parcel.writeString(networkName);
        parcel.writeString(networkAddress);
        parcel.writeString(timestamp);
    }

    public NetworkType getType() {
        return type;
    }

    public NetworkEventType getEventType() {
        return eventType;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
