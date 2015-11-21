package one.tribe.whatsnearme;

import java.util.UUID;

/**
 * Public constants of the app
 */
public class Constants {

    /**
     * Log tag. All classes shall used it to make debugging easier
     */
    public static final String TAG = "WhatsNearMe";

    /**
     * Broadcast sent when any network or devices where modified
     */
    public static final String NETWORK_CHANGED = "one.tribe.whatsnearme.INTENT_NETWORK_CHANGED";

    /**
     * Broadcast sent when wi-fi networks are changed (added or removed)
     * between two scannings
     */
    public static final String EXTRA_NETWORK_CHANGES = "one.tribe.whatsnearme.EXTRA_NETWORK_CHANGES";

    public static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    /**
     * Service priority used for the ordered broadcast receivers
     */
    public static final int SERVICE_PRIORITY = 10;

    /**
     * Activity priority used for the ordered broadcast receivers. The Activity
     * must receive the broadcasts first
     */
    public static final int ACTIVITY_PRIORITY = 20;

    /**
     * Result code, used to mark a ordered broadcast as "checked" by an activity
     */
    public static final int ACTIVITY_RESULT_CODE = 1001;

    /**
     * Bluetooth device extra key
     */
    public static final String BLUETOOTH_DEVICE_KEY = "BLUETOOTH_DEVICE";

    /**
     * Wifi scan result broadcast key
     */
    public static final String WIFI_SCAN_RESULT_KEY = "WIFI_SCAN_RESULT";

    /**
     * Maximum number of registered devices with the app
     */
    public static final int MAX_DEVICES_WITH_APP = 3;
}
