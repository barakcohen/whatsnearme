package one.tribe.whatsnearme.deviceswithapp.helper;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import one.tribe.whatsnearme.R;

/**
 * Encapsulates the mac address text view logic. It aggregates 6 text views and
 * manages them.
 */
public class MacAddressText {

    private static final String SEPARATOR = ":";

    private EditText[] macAddressText;

    /**
     * Build a mac address field that are directly in a Activity
     * @param parent The activity where the mac address text views are located
     */
    public MacAddressText(Activity parent) {
        macAddressText = new EditText[] {
            (EditText) parent.findViewById(R.id.macAddressFirstPart),
            (EditText) parent.findViewById(R.id.macAddressSecondPart),
            (EditText) parent.findViewById(R.id.macAddressThirdPart),
            (EditText) parent.findViewById(R.id.macAddressFourthPart),
            (EditText) parent.findViewById(R.id.macAddressFifthPart),
            (EditText) parent.findViewById(R.id.macAddressSixthPart)
        };

        for(int i = 0; i < macAddressText.length - 1; i++) {
            macAddressText[i].addTextChangedListener(new MacAddressTextWatcher(macAddressText[i], macAddressText[i + 1]));
        }
    }

    /**
     * Build a mac address field that are in a view, not an Activity
     * @param parent The view where the mac address text views are located
     */
    public MacAddressText(View parent) {
        macAddressText = new EditText[] {
                (EditText) parent.findViewById(R.id.editMacAddressFirstPart),
                (EditText) parent.findViewById(R.id.editMacAddressSecondPart),
                (EditText) parent.findViewById(R.id.editMacAddressThirdPart),
                (EditText) parent.findViewById(R.id.editMacAddressFourthPart),
                (EditText) parent.findViewById(R.id.editMacAddressFifthPart),
                (EditText) parent.findViewById(R.id.editMacAddressSixthPart)
        };
    }

    /**
     * Fill each part of the mac address with data
     * @param macAddress
     */
    public void fillMacAddressText(String macAddress) {
        String[] splittedMacAddress = macAddress.split(":");

        for(int i = 0; i < macAddressText.length; i++) {
            macAddressText[i].setText(splittedMacAddress[i]);
        }
    }

    /**
     * Retrieves each part of the mac address from the fields and return a single
     * string with the mac address
     */
    public String getMacAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(macAddressText[0].getText().toString());

        for(int i = 1; i < macAddressText.length; i++) {
            builder.append(SEPARATOR).append(macAddressText[i].getText().toString());
        }

        return builder.toString();
    }

    /**
     * Validates the mac addres
     * @return true if the mac address is valid
     */
    public boolean validate() {
        for(EditText macAddressField : macAddressText) {
            if(macAddressField.getText().length() != 2) {
                macAddressField.requestFocus();
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Clears the focus of the fields
     */
    public void clearFocus() {
        for(EditText macAddressField : macAddressText) {
            macAddressField.clearFocus();
        }
    }

    /**
     * Clears all the fields
     */
    public void clear() {
        for(EditText macAddressField : macAddressText) {
            macAddressField.setText(null);
        }
    }

    /**
     * Set the text changed listeners for each text view. Those listeners check
     * if a mac address text field is filled and go to the next one
     */
    public void setTextChangedListeners() {
        for(int i = 0; i < macAddressText.length - 1; i++) {
            macAddressText[i].addTextChangedListener(new MacAddressTextWatcher(macAddressText[i], macAddressText[i + 1]));
        }
    }

    /**
     * Text Watcher that checks if a mac address text field is filled and change
     * the focus to the next field
     */
    private static class MacAddressTextWatcher implements TextWatcher {
        private EditText currentText;
        private EditText nextText;

        public MacAddressTextWatcher(EditText currentView, EditText nextText) {
            this.currentText = currentView;
            this.nextText = nextText;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(currentText.getText().toString().length() ==  2) {
                nextText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
