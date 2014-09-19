package net.alteridem.mileage;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.alteridem.mileage.adapters.VehicleIconAdapter;
import net.alteridem.mileage.data.Vehicle;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Robert Prouse
 * Date: 31/01/13
 * Time: 8:13 PM
 */
@EFragment
public class VehicleDialog extends DialogFragment implements TextView.OnEditorActionListener {
    public interface IVehicleDialogListener {
        void onFinishVehicleDialog(Vehicle vehicle);
    }

    @App
    MileageApplication _app;

    private Spinner _vehicleIcon;
    private EditText _vehicleName;
    private Vehicle _vehicle;

    public VehicleDialog() {
        _vehicle = null;
    }

    public void setVehicle(Vehicle vehicle) {
        _vehicle = vehicle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_vehicle_dialog, container);

        // Issue #29 - Show a spinner with vehicle icons
        _vehicleIcon = (Spinner) view.findViewById(R.id.vehicle_dialog_icon);
        _vehicleIcon.setAdapter(new VehicleIconAdapter(getActivity()));

        _vehicleName = (EditText) view.findViewById(R.id.vehicle_dialog_name);
        if (_vehicle == null) {
            getDialog().setTitle(getString(R.string.vehicle_dialog_title_add));
        } else {
            getDialog().setTitle(getString(R.string.vehicle_dialog_title_edit));
            _vehicleIcon.setSelection(_vehicle.getIconId());
            _vehicleName.setText(_vehicle.getName());
        }

        // Show the soft keyboard automatically
        _vehicleName.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        _vehicleName.setOnEditorActionListener(this);

        Button ok = (Button) view.findViewById(R.id.vehicle_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        Button cancel = (Button) view.findViewById(R.id.vehicle_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            closeDialog();
            return true;
        }
        return false;
    }

    private void closeDialog() {
        // Save the vehicle
        // Issue #29 - Show a spinner with vehicle icons
        int icon = _vehicleIcon.getSelectedItemPosition();
        String name = _vehicleName.getText().toString();
        if ( name.isEmpty() ) {
            _vehicleName.setError(getString(R.string.string_error));
            return;
        }

        if (_vehicle == null) {
            _vehicle = new Vehicle(icon, name);
        } else {
            _vehicle.setIconId(icon);
            _vehicle.setName(name);
        }
        _vehicle.save(_app.getDbHelper().getWritableDatabase());

        // Call back to the activity
        IVehicleDialogListener activity = (IVehicleDialogListener) getActivity();
        activity.onFinishVehicleDialog(_vehicle);
        this.dismiss();
    }
}
