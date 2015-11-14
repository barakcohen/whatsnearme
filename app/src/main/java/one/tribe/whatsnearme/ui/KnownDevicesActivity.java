package one.tribe.whatsnearme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;

import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.model.KnownDevicesDao;

public class KnownDevicesActivity extends AppCompatActivity {

    private KnownDevicesDao knownDevicesDao;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_known_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void initializeList() {
        knownDevicesDao = new KnownDevicesDao(this);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listAdapter = new KnownDeviceListAdapter(knownDevicesDao.getKnownDevices(), inflater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        knownDevicesDao.close();
    }
}
