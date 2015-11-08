package one.tribe.whatsnearme.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.ScannerService;
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.notification.NotificationFormatter;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LogActivity extends AppCompatActivity {

    TextView logTxt;
    private NetworkChangedReceiver networkChangedReceiver;
    private boolean serviceBound;
    private ScannerService scannerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.TAG, "LogActivity: Creating");

        setContentView(R.layout.activity_log);

        logTxt = (TextView) findViewById(R.id.logTxt);
        logTxt.setMovementMethod(new ScrollingMovementMethod());
    }

    private void registerReceivers() {
        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter networkChangedFilter = new IntentFilter(Constants.NETWORK_CHANGED);
        networkChangedFilter.setPriority(Constants.ACTIVITY_PRIORITY);
        registerReceiver(networkChangedReceiver, networkChangedFilter);
    }

    private void bindScannerService() {
        Intent intent = new Intent(this, ScannerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "LogActivity: Resuming");
        registerReceivers();
        bindScannerService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constants.TAG, "LogActivity: Pausing");
        unregisterReceiver(networkChangedReceiver);
        unbindService(serviceConnection);
    }

    public void cleanLog(View view) {
        logTxt.setText("");
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<NetworkEvent> networkEvents =
                    intent.getParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES);

            for(NetworkEvent event : networkEvents) {
                logTxt.append(NotificationFormatter.formatCompleteMessage(event) + "\n");
            }

            setResultCode(Constants.ACTIVITY_RESULT_CODE);
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            ScannerService.LocalBinder binder = (ScannerService.LocalBinder) service;
            scannerService = binder.getService();
            serviceBound = true;

            if(scannerService != null) {
                BlockingQueue<NetworkEvent> log = scannerService.getLog();
                for(NetworkEvent event : log) {
                    logTxt.append(NotificationFormatter.formatCompleteMessage(event) + "\n");
                }
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            scannerService = null;
            serviceBound = false;
        }
    };
}
