package one.tribe.whatsnearme.deviceswithapp.helper;

import android.view.View;
import android.widget.EditText;

import one.tribe.whatsnearme.R;

/**
 * Validates the phone id field.
 */
public class PhoneIdValidator implements View.OnFocusChangeListener {

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(!hasFocus) {
            validate(view);
        }
    }

    public boolean validate(View view) {
        EditText editText = (EditText) view;

        if(editText.getText().length() != 0) {
            editText.setError(null);
            return Boolean.TRUE;
        } else {
            editText.setError(view.getResources().getString(R.string.invalid_phone_id_error_message));
            return Boolean.FALSE;
        }
    }
}
