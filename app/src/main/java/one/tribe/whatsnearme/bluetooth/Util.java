package one.tribe.whatsnearme.bluetooth;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Util {

    private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("hh:mm:ss a");

    /**
     * Returns the current date in format hh:mm:ss a, e.g. 02:32:11 AM
     * @return
     */
    public static String getDate() {
        return HOUR_FORMAT.format(new Date());
    }

}
