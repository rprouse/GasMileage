package net.alteridem.mileage;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EFragment
public class ResetOdometerDialog extends DialogFragment {
    CheckBox _checkBox;

    @Pref
    MileagePreferences_ _preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_odometer, container);
        if ( view == null ) return null;
        getDialog().setTitle(getString(R.string.reset_dialog_title));

        _checkBox = (CheckBox) view.findViewById(R.id.reset_dialog_checkBox);
        Button ok = (Button) view.findViewById(R.id.reset_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        return view;
    }

    private void closeDialog() {
        if ( _checkBox.isChecked() ) {
            _preferences.show_reset_odometer().put(false);
        }
        this.dismiss();
    }
}