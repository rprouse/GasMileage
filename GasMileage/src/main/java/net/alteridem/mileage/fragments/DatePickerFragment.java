package net.alteridem.mileage.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import net.alteridem.mileage.IDateReceiver;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Robert Prouse
 * Date: 1/29/13
 * Time: 8:50 PM
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private IDateReceiver _dateReceiver;

    public DatePickerFragment(IDateReceiver dateReceiver) {
        _dateReceiver = dateReceiver;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        _dateReceiver.setDate(year, month, day);
    }
}
