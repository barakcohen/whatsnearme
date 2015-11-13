package one.tribe.whatsnearme;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.i(Constants.TAG, "Executing logcat command");
                    File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "whatsnearme.log");
                    Log.i(Constants.TAG, "Trying to redirect to file: " + outputFile.getPath());
                    Process exec = Runtime.getRuntime().exec("logcat -v time -f " + outputFile.getAbsolutePath() + " WhatsNearMe:D *:S");
                    exec.waitFor();
                    Log.i(Constants.TAG, "Command executed, returned: " + exec.exitValue());
                } catch(Exception e) {
                    Log.e(Constants.TAG, "Error redirecting logcat", e);
                }
            }
        }).start();
    }
}
