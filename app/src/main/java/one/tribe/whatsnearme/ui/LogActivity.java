package one.tribe.whatsnearme.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.notification.NotificationFormatter;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    TextView log;
    private NetworkChangedReceiver networkChangedReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        log = (TextView) findViewById(R.id.logTxt);

        networkChangedReceiver = new NetworkChangedReceiver();
        registerReceiver(networkChangedReceiver,
                new IntentFilter(Constants.NETWORK_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangedReceiver);
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<NetworkEvent> networkEvents =
                    intent.getParcelableArrayListExtra(Constants.EXTRA_NETWORK_CHANGES);

            for(NetworkEvent event : networkEvents) {
                Log.i(Constants.TAG, "Logging message for netowrk " + event.getNetworkName());
                log.append(NotificationFormatter.formatCompleteMessage(event) + "\n");
            }
        }
    }
}
