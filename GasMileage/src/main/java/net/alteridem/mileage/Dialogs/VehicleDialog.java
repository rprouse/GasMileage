package net.alteridem.mileage.dialogs;

import android.app.DialogFragment;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.alteridem.mileage.MileageApplication;
import net.alteridem.mileage.R;
import net.alteridem.mileage.adapters.VehicleIconAdapter;
import net.alteridem.mileage.data.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragement_vehicle_dialog)
public class VehicleDialog extends DialogFragment implements TextView.OnEditorActionListener {
    public interface IVehicleDialogListener {
        void onFinishVehicleDialog(Vehicle vehicle);
    }

    @App
    MileageApplication _app;

    @ViewById(R.id.vehicle_dialog_icon) Spinner  _vehicleIcon;
    @ViewById(R.id.vehicle_dialog_name) EditText _vehicleName;

    private Vehicle _vehicle;

    public VehicleDialog() {
        _vehicle = null;
    }

    public void setVehicle(Vehicle vehicle) {
        _vehicle = vehicle;
    }

    @AfterViews
    void initialize() {
        // Issue #29 - Show a spinner with vehicle icons
        _vehicleIcon.setAdapter(new VehicleIconAdapter(getActivity()));

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
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            closeDialog();
            return true;
        }
        return false;
    }

    @Click(R.id.vehicle_dialog_ok)
    void closeDialog() {
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

    @Click(R.id.vehicle_dialog_cancel)
    void cancel() {
        dismiss();
    }
}
