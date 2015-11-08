package one.tribe.whatsnearme;

import android.app.Application;
import android.content.Intent;

/**
 * Application class. It's extended to start the service when starts the
 * application
 */
public class WhastNearMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, ScannerService.class);
        startService(intent);
    }
}
