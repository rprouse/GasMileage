package net.alteridem.mileage.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import net.alteridem.mileage.data.Vehicle;

import java.util.List;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class VehicleSpinnerAdapter extends ArrayAdapter<Vehicle> implements SpinnerAdapter {
    public VehicleSpinnerAdapter(Context context, List<Vehicle> vehicles) {
        super(context, android.R.layout.simple_spinner_item, vehicles);
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
