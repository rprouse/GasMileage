package net.alteridem.mileage;

import android.app.DialogFragment;
import android.widget.CheckBox;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EFragment(R.layout.reset_odometer)
public class ResetOdometerDialog extends DialogFragment {

    @ViewById(R.id.reset_dialog_checkBox)
    CheckBox _checkBox;

    @Pref
    MileagePreferences_ _preferences;

    @AfterViews
    void initialize() {
        getDialog().setTitle(getString(R.string.reset_dialog_title));
    }

    @Click(R.id.reset_dialog_ok)
    private void closeDialog() {
        if ( _checkBox.isChecked() ) {
            _preferences.show_reset_odometer().put(false);
        }
        this.dismiss();
    }
}