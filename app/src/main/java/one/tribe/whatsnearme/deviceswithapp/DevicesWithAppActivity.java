package one.tribe.whatsnearme.deviceswithapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.deviceswithapp.helper.DeviceWithAppListHandler;
import one.tribe.whatsnearme.deviceswithapp.helper.MacAddressText;
import one.tribe.whatsnearme.deviceswithapp.helper.PhoneIdValidator;
import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;

/**
 * Device with app Activity. Accessed by the application menu.
 */
public class DevicesWithAppActivity extends AppCompatActivity implements EditDeviceWithAppDialog.EditDeviceDialogListener {

    private DevicesWithAppListAdapter listAdapter;
    private DeviceWithAppListHandler handler;
    private MacAddressText macAddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, getClass().getSimpleName() + " onCreate");

        setContentView(R.layout.activity_devices_with_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(Constants.TAG, getClass().getSimpleName() + " onResume");

        TextView myMacAddress = (TextView) findViewById(R.id.myMacAddress);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null) {
            myMacAddress.setText(bluetoothAdapter.getAddress());
        } else {
            myMacAddress.setText("-");
        }

        macAddressText = new MacAddressText(this);
        initializeListAdapter();
    }

    private void initializeListAdapter() {
        handler = new DeviceWithAppListHandler(this);

        List<DeviceWithApp> devicesWithApp = handler.getDevicesWithApp();

        listAdapter = new DevicesWithAppListAdapter(handler, this);
        ListView devicesWithAppList = (ListView) findViewById(R.id.devicesWithAppList);
        devicesWithAppList.setAdapter(listAdapter);

        if(devicesWithApp.size() == Constants.MAX_DEVICES_WITH_APP) {
            setVisibility(R.id.addNewDeviceWithAppLayout, View.GONE);
        }

        TextView labelDeviceWithAppTxt = (TextView) findViewById(R.id.labelDeviceWithAppTxt);
        if(devicesWithApp.isEmpty()) {
            labelDeviceWithAppTxt.setText(getString(R.string.action_add_device_with_app));
            devicesWithAppList.setVisibility(View.GONE);
            setVisibility(R.id.addNewDeviceWithAppLayout, View.VISIBLE);
        } else {
            labelDeviceWithAppTxt.setText(getString(R.string.action_device_with_app_list_label));
            devicesWithAppList.setVisibility(View.VISIBLE);
        }
    }

    private void setVisibility(int resourceId, int visible) {
        findViewById(resourceId).setVisibility(visible);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler != null) {
            handler.close();
        }
    }

    public void addDeviceWithApp(View view) {
        EditText deviceWithAppIdTxt = (EditText) findViewById(R.id.newDeviceWithAppId);

        PhoneIdValidator phoneIdValidator = new PhoneIdValidator();

        if(!phoneIdValidator.validate(deviceWithAppIdTxt) || !macAddressText.validate()) {
            Log.i(Constants.TAG, "Cannot add devices with app, fields are invalid");
            return;
        }

        String macAddress = macAddressText.getMacAddress();
        String deviceWithAppId = deviceWithAppIdTxt.getText().toString();

        Log.i(Constants.TAG, "Adding new device with app: " + deviceWithAppId + ": " + macAddress);

        DeviceWithApp device = new DeviceWithApp(deviceWithAppId, macAddress);

        listAdapter.insertDevice(device);
        Log.i(Constants.TAG, "Device added, updating list. Count: " + listAdapter.getCount());

        // Set the list visible
        setVisibility(R.id.devicesWithAppList, View.VISIBLE);
        setVisibility(R.id.devicesWithAppSeparator, View.VISIBLE);
        setVisibility(R.id.newDeviceWithAppIdLabel, View.VISIBLE);

        if(listAdapter.getCount() == Constants.MAX_DEVICES_WITH_APP) {
            Log.d(Constants.TAG, "Max number of devices with app reached!");
            setVisibility(R.id.addNewDeviceWithAppLayout, View.GONE);
            findViewById(R.id.addNewDeviceWithAppLayout).invalidate();
        }

        deviceWithAppIdTxt.clearFocus();
        deviceWithAppIdTxt.setText(null);

        // Clear fields
        macAddressText.clearFocus();
        macAddressText.clear();

        // Hides input
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(deviceWithAppIdTxt.getWindowToken(), 0);
    }

    @Override
    public void onDialogSave(EditDeviceWithAppDialog dialog) {
        Log.i(Constants.TAG, "Saving devices modifications");
        listAdapter.updateDevice(dialog.getData());
    }
}
