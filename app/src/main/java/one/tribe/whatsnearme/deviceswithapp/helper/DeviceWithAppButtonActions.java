package one.tribe.whatsnearme.deviceswithapp.helper;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import one.tribe.whatsnearme.Constants;
import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.deviceswithapp.DevicesWithAppListAdapter;
import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;
import one.tribe.whatsnearme.deviceswithapp.EditDeviceWithAppDialog;

/**
 * Handles the action of the buttons
 */
public class DeviceWithAppButtonActions {

    private DeviceWithAppButtonActions() {}

    public static class EnableEditClickListener implements View.OnClickListener {
        private Activity parent;
        private DeviceWithApp data;

        public EnableEditClickListener(Activity parent, DeviceWithApp data) {
            this.parent = parent;
            this.data = data;
        }

        @Override
        public void onClick(View editButton) {
            Log.i(Constants.TAG, "Starting phone with app edition");

            EditDeviceWithAppDialog newFragment = new EditDeviceWithAppDialog();
            newFragment.setData(data);

            newFragment.show(parent.getFragmentManager(), "EditDeviceWithApp");
        }
    }

    public static class DeleteClickListener implements View.OnClickListener {
        private DevicesWithAppListAdapter listAdapter;
        private Activity parent;
        private DeviceWithApp data;

        public DeleteClickListener(DeviceWithApp data, DevicesWithAppListAdapter listAdapter, Activity parent) {
            this.data = data;
            this.listAdapter = listAdapter;
            this.parent = parent;
        }

        @Override
        public void onClick(View deleteButton) {
            Log.i(Constants.TAG, "Deleting device with app: " + data);
            listAdapter.deleteDevice(data);

            setVisibility(parent, R.id.addNewDeviceWithAppLayout, View.VISIBLE);
        }
    }

    private static void setVisibility(Activity parent, int resourceId, int visible) {
        parent.findViewById(resourceId).setVisibility(visible);
    }
}
