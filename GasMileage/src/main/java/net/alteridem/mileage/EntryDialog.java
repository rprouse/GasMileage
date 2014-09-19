package net.alteridem.mileage;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import net.alteridem.mileage.adapters.VehicleSpinnerAdapter;
import net.alteridem.mileage.fragments.DatePickerFragment;
import net.alteridem.mileage.data.Entry;
import net.alteridem.mileage.data.Vehicle;

import android.text.format.DateFormat;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Robert Prouse
 * Date: 1/27/13
 * Time: 4:55 PM
 */
@EFragment
public class EntryDialog extends DialogFragment implements IDateReceiver, TextView.OnEditorActionListener {
    public interface IEntryDialogListener {
        void onFinishEntryDialog(Vehicle vehicle);
    }

    @App
    MileageApplication _app;

    @Pref
    MileagePreferences_ _preferences;

    @Bean
    Convert _convert;

    List<Vehicle> _vehicleList;
    Vehicle _vehicle; // The current vehicle
    Entry _entry; // The current entry, this is null if new
    Spinner _vehicleSpinner;
    EditText _kilometers;
    Spinner _kilometersUnit;
    EditText _liters;
    Spinner _litersUnit;
    TextView _datePicker;
    TextView _note;
    int _year;
    int _month;
    int _day;

    public EntryDialog() {
        setDefaultDate();
    }

    public void setVehicle(Vehicle vehicle) {
        _vehicle = vehicle;
    }

    public void setEntry(Entry entry) {
        _entry = entry;
        _year = entry.getFillup_date().getYear() + 1900;
        _month = entry.getFillup_date().getMonth();
        _day = entry.getFillup_date().getDay() + 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_entry_dialog, container);

        _vehicleList = Vehicle.fetchAll(_app.getDbHelper().getWritableDatabase());
        getDialog().setTitle(R.string.entry_dialog_title);

        _vehicleSpinner = (Spinner) view.findViewById(R.id.entry_dialog_vehicle);
        ArrayAdapter adapter_veh = new VehicleSpinnerAdapter(getActivity(), _vehicleList);
        _vehicleSpinner.setAdapter(adapter_veh);

        _kilometers = (EditText) view.findViewById(R.id.entry_dialog_kilometers);
        _kilometersUnit = (Spinner) view.findViewById(R.id.entry_dialog_kilometers_unit);

        ArrayAdapter adapter_km = ArrayAdapter.createFromResource(getActivity(), R.array.distance_units, android.R.layout.simple_spinner_item);
        adapter_km.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _kilometersUnit.setAdapter(adapter_km);

        _liters = (EditText) view.findViewById(R.id.entry_dialog_liters);
        _litersUnit = (Spinner) view.findViewById(R.id.entry_dialog_liters_unit);

        ArrayAdapter adapter_l = ArrayAdapter.createFromResource(getActivity(), R.array.volume_units, android.R.layout.simple_spinner_item);
        adapter_l.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _litersUnit.setAdapter(adapter_l);

        _datePicker = (TextView) view.findViewById(R.id.entry_dialog_date);
        setDate();

        _datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        _note = (TextView) view.findViewById(R.id.entry_dialog_note);

        Button ok = (Button) view.findViewById(R.id.entry_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        Button cancel = (Button) view.findViewById(R.id.entry_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        switchToVehicle();
        setDefaultVolumeUnits();
        setDefaultDistanceUnits();

        // Show the soft keyboard automatically
        _kilometers.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        _liters.setOnEditorActionListener(this);

        // Set values when in edit mode
        if (_entry != null) {
            _liters.setText(String.valueOf(_convert.volume(_entry.getLitres())));
            _kilometers.setText(String.valueOf(_convert.distance(_entry.getKilometers())));
            _note.setText(_entry.getNote());
        }

        return view;
    }

    private void setDefaultVolumeUnits() {
        String units = _preferences.volume_units().get();
        int pos = 0;
        if (units.equalsIgnoreCase("gal_us"))
            pos = 1;
        if (units.equalsIgnoreCase("gal_imp"))
            pos = 2;
        _litersUnit.setSelection(pos);
    }

    private void setDefaultDistanceUnits() {
        String units = _preferences.distance_units().get();
        int pos = 0;
        if (units.equalsIgnoreCase("m"))
            pos = 1;
        _kilometersUnit.setSelection(pos);
    }

    private void switchToVehicle() {
        if (_vehicle == null)
            return;

        long id = _vehicle.getId();
        Vehicle v = null;
        for (Vehicle veh : _vehicleList) {
            if (veh.getId() == id) {
                v = veh;
                break;
            }
        }
        int pos = _vehicleList.indexOf(v);
        if (pos >= 0) {
            _vehicleSpinner.setSelection(pos);
        }
    }

    private void setDefaultDate() {
        final Calendar c = Calendar.getInstance();
        _year = c.get(Calendar.YEAR);
        _month = c.get(Calendar.MONTH);
        _day = c.get(Calendar.DAY_OF_MONTH);
    }

    private void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setReceiver(this);
        newFragment.show(getFragmentManager(), "datePicker");
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
        Vehicle v = (Vehicle) _vehicleSpinner.getSelectedItem();
        double km = 0;
        try {
            km = Double.parseDouble(_kilometers.getText().toString());
            if (_kilometersUnit.getSelectedItemPosition() == 1) {
                km = _convert.milesToKilometers(km);
            }
        } catch (NumberFormatException nfe) {
        }

        double l = 0;
        try {
            l = Double.parseDouble(_liters.getText().toString());
            if (_litersUnit.getSelectedItemPosition() == 1) {
                l = _convert.gallonsToLiters(l, Convert.Gallons.US);
            } else if (_litersUnit.getSelectedItemPosition() == 2) {
                l = _convert.gallonsToLiters(l, Convert.Gallons.Imperial);
            }
        } catch (NumberFormatException nfe) {
        }

        if (km <= 0 || l <= 0) {
            if (km <= 0) {
                _kilometers.setError(getString(R.string.number_error));
            }
            if (l <= 0) {
                _liters.setError(getString(R.string.number_error));
            }
            return;
        }

        Date d = new Date(_year - 1900, _month, _day);

        String note = _note.getText().toString();

        Entry entry = new Entry(v.getId(), d, km, l, note);
        if ( _entry != null ) {
            entry.setId(_entry.getId());
        }
        entry.save(_app.getDbHelper().getWritableDatabase());

        // Call back to the activity
        IEntryDialogListener activity = (IEntryDialogListener) getActivity();
        activity.onFinishEntryDialog(v);
        this.dismiss();
    }

    @Override
    public void setDate(int year, int month, int day) {
        _year = year;
        _month = month;
        _day = day;

        setDate();
    }

    private void setDate() {
        // Use the user's default date format
        Format df = DateFormat.getLongDateFormat(getActivity());
        Date d = new Date(_year - 1900, _month, _day);
        String dateStr = df.format(d);
        _datePicker.setText(dateStr);
    }
}
