package one.tribe.whatsnearme;

import android.app.Application;
import android.content.Intent;

/**
 * Application class. It's extended to start the service when starts the
 * application
 */
public class WhastNearMeApplication extends Application {

    private static boolean activityVisible = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, ScannerService.class);
        startService(intent);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
