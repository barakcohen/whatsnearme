package one.tribe.whatsnearme.network;

/**
 *
 */
public enum NetworkType {
    WIFI("wi-fi network"),
    BLUETOOTH("bluetooth device"),
    PHONE_WITH_APP("phone with app");

    private String name;

    NetworkType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static NetworkType getInstance(String name) {
        for (NetworkType networkType : values()) {
            if(networkType.name().equals(name)) {
                return networkType;
            }
        }

        return null;
    }

    public String serialize() {
        return this.name();
    }
}
