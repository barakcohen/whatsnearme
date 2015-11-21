package one.tribe.whatsnearme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * App preferences wrapper
 */
public class AppPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String WIFI_MIN_LEVEL;
    private final String BLUETOOTH_DISCOVERY_INTERVAL;
    private final String BLUETOOTH_DISCOVERABLE_TIME;
    private final String BLUETOOTH_LE_SCAN_TIME;
    private final String LOG_LIMIT;
    private final String NOTIFICATIONS_ON;
    private final String BLUETOOTH_LE_ON;
    private final String WIFI_MIN_LEVEL_DEFAULT_VALUE;
    private final String BLUETOOTH_DISCOVERY_INTERVAL_DEFAULT_VALUE;
    private final String BLUETOOTH_DISCOVERABLE_TIME_DEFAULT_VALUE;
    private final String BLUETOOTH_LE_SCAN_TIME_DEFAULT_VALUE;
    private final String LOG_LIMIT_DEFAULT_VALUE;
    private final boolean NOTIFICATIONS_ON_DEFAULT_VALUE;
    private final boolean BLUETOOTH_LE_ON_DEFAULT_VALUE;

    private SharedPreferences preferences;



    public AppPreferences(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferences.registerOnSharedPreferenceChangeListener(this);

        Resources r = context.getResources();

        WIFI_MIN_LEVEL = r.getString(R.string.pref_wifi_min_level_key);
        BLUETOOTH_DISCOVERY_INTERVAL = r.getString(R.string.pref_bluetooth_interval_key);
        BLUETOOTH_DISCOVERABLE_TIME = r.getString(R.string.pref_bluetooth_discoverable_time_key);
        BLUETOOTH_LE_SCAN_TIME = r.getString(R.string.pref_bluetooth_le_scan_time_key);
        LOG_LIMIT = r.getString(R.string.pref_log_limit_key);
        NOTIFICATIONS_ON = r.getString(R.string.pref_notifications_on_key);
        BLUETOOTH_LE_ON = r.getString(R.string.pref_bluetooth_le_enable_key);
        WIFI_MIN_LEVEL_DEFAULT_VALUE = r.getString(R.string.pref_default_wifi_min_level);
        BLUETOOTH_DISCOVERY_INTERVAL_DEFAULT_VALUE = r.getString(R.string.pref_default_bluetooth_interval);
        BLUETOOTH_DISCOVERABLE_TIME_DEFAULT_VALUE = r.getString(R.string.pref_default_bluetooth_discoverable_time);
        BLUETOOTH_LE_SCAN_TIME_DEFAULT_VALUE = r.getString(R.string.pref_default_bluetooth_le_scan_time);
        LOG_LIMIT_DEFAULT_VALUE = r.getString(R.string.pref_default_log_limit);
        NOTIFICATIONS_ON_DEFAULT_VALUE = r.getBoolean(R.bool.pref_default_notifications_on);
        BLUETOOTH_LE_ON_DEFAULT_VALUE = r.getBoolean(R.bool.pref_default_bluetooth_le_enable);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.preferences = sharedPreferences;
        Log.d(Constants.TAG, key + " changed");
    }

    public int getWifiMinLevel() {
        return getInt(WIFI_MIN_LEVEL, WIFI_MIN_LEVEL_DEFAULT_VALUE);
    }

    public long getBluetoothDiscoveryInterval() {
        int value = getInt(BLUETOOTH_DISCOVERY_INTERVAL, BLUETOOTH_DISCOVERY_INTERVAL_DEFAULT_VALUE);
        return TimeUnit.MILLISECONDS.convert(value, TimeUnit.SECONDS);
    }

    public int getBluetoothDiscoverableTime() {
        return getInt(BLUETOOTH_DISCOVERABLE_TIME, BLUETOOTH_DISCOVERABLE_TIME_DEFAULT_VALUE);
    }

    public long getBluetoothLEScanTime() {
        int value = getInt(BLUETOOTH_LE_SCAN_TIME, BLUETOOTH_LE_SCAN_TIME_DEFAULT_VALUE);
        return TimeUnit.MILLISECONDS.convert(value, TimeUnit.SECONDS);
    }

    public int getLogLimit() {
        Log.d(Constants.TAG, LOG_LIMIT + ": " + LOG_LIMIT_DEFAULT_VALUE);

        return getInt(LOG_LIMIT, LOG_LIMIT_DEFAULT_VALUE);
    }

    public boolean isNotificationsOn() {
        return preferences.getBoolean(NOTIFICATIONS_ON, NOTIFICATIONS_ON_DEFAULT_VALUE);
    }

    public boolean isBluetoothLEOn() {
        return preferences.getBoolean(BLUETOOTH_LE_ON, BLUETOOTH_LE_ON_DEFAULT_VALUE);
    }

    private int getInt(String key, String defaultValue) {
        String value = preferences.getString(key, defaultValue);

        return Integer.parseInt(value);
    }

}
