package net.alteridem.mileage;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.alteridem.mileage.data.Vehicle;

/**
 * Created with IntelliJ IDEA.
 * User: Robert Prouse
 * Date: 31/01/13
 * Time: 8:13 PM
 */
public class VehicleDialog extends DialogFragment implements TextView.OnEditorActionListener {
    public interface IVehicleDialogListener {
        void onFinishVehicleDialog(Vehicle vehicle);
    }

    private EditText _vehicleName;
    private Vehicle _vehicle;

    public VehicleDialog() {
        _vehicle = null;
    }

    public VehicleDialog(Vehicle vehicle) {
        _vehicle = vehicle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_vehicle_dialog, container);
        _vehicleName = (EditText) view.findViewById(R.id.vehicle_dialog_name);
        if (_vehicle == null) {
            getDialog().setTitle(getString(R.string.vehicle_dialog_title_add));
        } else {
            getDialog().setTitle(getString(R.string.vehicle_dialog_title_edit));
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
        String name = _vehicleName.getText().toString();
        if (_vehicle == null) {
            _vehicle = new Vehicle(name);
        } else {
            _vehicle.setName(name);
        }
        _vehicle.save();

        // Call back to the activity
        IVehicleDialogListener activity = (IVehicleDialogListener) getActivity();
        activity.onFinishVehicleDialog(_vehicle);
        this.dismiss();
    }
}
