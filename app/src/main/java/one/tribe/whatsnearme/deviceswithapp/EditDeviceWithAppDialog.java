package one.tribe.whatsnearme.deviceswithapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.deviceswithapp.persistency.DeviceWithApp;
import one.tribe.whatsnearme.deviceswithapp.helper.MacAddressText;

/**
 * Dialog for editing an device with app entry
 */
public class EditDeviceWithAppDialog extends DialogFragment {

    private DeviceWithApp data;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit device with app");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_device_with_app, null);

        // get the fields
        EditText editPhoneIdTxt = (EditText) view.findViewById(R.id.editPhoneIdTxt);
        editPhoneIdTxt.setText(data.getPhoneId());

        MacAddressText macAddress = new MacAddressText(view);
        macAddress.fillMacAddressText(data.getMacAddress());
        macAddress.setTextChangedListeners();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.dialog_save_button_txt, new OnClickListener(view))
                .setNegativeButton(R.string.dialog_cancel_button_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDeviceWithAppDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setData(DeviceWithApp data) {
        this.data = data;
    }

    public DeviceWithApp getData() {
        return this.data;
    }

    public interface EditDeviceDialogListener {
        void onDialogSave(EditDeviceWithAppDialog dialog);}

    /**
     * Listener for the save button. Delegates the control to the calling Activity
     */
    private class OnClickListener implements DialogInterface.OnClickListener {

        private View view;

        private OnClickListener(View view) {
            this.view = view;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            EditText editPhoneIdTxt = (EditText) view.findViewById(R.id.editPhoneIdTxt);
            MacAddressText macAddress = new MacAddressText(view);

            EditDeviceWithAppDialog.this.data.setPhoneId(editPhoneIdTxt.getText().toString());
            EditDeviceWithAppDialog.this.data.setMacAddress(macAddress.getMacAddress());

            EditDeviceDialogListener listener = (EditDeviceDialogListener) getActivity();
            listener.onDialogSave(EditDeviceWithAppDialog.this);

        }
    }
}
