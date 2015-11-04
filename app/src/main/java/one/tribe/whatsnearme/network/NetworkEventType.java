package one.tribe.whatsnearme.network;

/**
 *
 */
public enum NetworkEventType {

    NEW("in range", "New "),
    GONE("out of range", " gone");

    String desc;
    String shortDesc;

    NetworkEventType(String desc, String shortDesc) {
        this.desc = desc;
        this.shortDesc = shortDesc;
    }

    public String getDesc() {
        return desc;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public static NetworkEventType getInstance(String name) {
        for (NetworkEventType networkEventType : values()) {
            if(networkEventType.name().equals(name)) {
                return networkEventType;
            }
        }

        return null;
    }

    public String serialize() {
        return this.name();
    }
}
