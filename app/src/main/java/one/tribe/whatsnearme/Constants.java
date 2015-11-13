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

    /**
     * Broadcast sent when a Wi-fi network scanning in done and another one shall start
     */
    public static final String START_WIFI_SCANNING = "one.tribe.whatsnearme.INTENT_START_WIFI_SCANNING";


    /**
     * Broadcast sent when a Bluetooth discovery in done and another one shall start
     */
    public static final String START_BLUETOOTH_DISCOVERY = "one.tribe.whatsnearme.INTENT_START_BLUETOOTH_DISCOVERY";

    public static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public static final long BLUETOOTH_LE_SCAN_PERIOD = 10000L;

    public static final int SERVICE_PRIORITY = 10;

    public static final int ACTIVITY_PRIORITY = 20;

    public static final int ACTIVITY_RESULT_CODE = 1001;

    public static final int SERVICE_RESULT_CODE = 1002;
    public static final String BLUETOOTH_DEVICE_KEY = "BLUETOOTH_DEVICE";
    public static final String WIFI_SCAN_RESULT_KEY = "WIFI_SCAN_RESULT";
}
