package net.alteridem.mileage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import net.alteridem.mileage.R;
import net.alteridem.mileage.data.Vehicle;

import java.util.List;

public class VehicleSpinnerAdapter extends ArrayAdapter<Vehicle> implements SpinnerAdapter {

    private LayoutInflater _inflater;

    public VehicleSpinnerAdapter(Context context, List<Vehicle> vehicles) {
        super(context, android.R.layout.simple_spinner_item, vehicles);
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if ( view == null ) {
            view = _inflater.inflate(R.layout.vehicle_spinner_item, parent, false);
        }
        Vehicle vehicle = getItem(position);
        if ( view != null && vehicle != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.spinner_item_vehicle_icon);
            TextView name = (TextView) view.findViewById(R.id.spinner_item_vehicle_name);

            name.setText( vehicle.getName() );
        }
        return view;
    }

    public View getDropDownView(int position, View view, ViewGroup parent) {
        if ( view == null ) {
            view = _inflater.inflate(R.layout.vehicle_spinner_dropdown_item, parent, false);
        }
        Vehicle vehicle = getItem(position);
        if ( view != null && vehicle != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.spinner_dropdown_vehicle_icon);
            TextView name = (TextView) view.findViewById(R.id.spinner_dropdown_vehicle_name);

            name.setText( vehicle.getName() );
        }
        return view;
    }
}
