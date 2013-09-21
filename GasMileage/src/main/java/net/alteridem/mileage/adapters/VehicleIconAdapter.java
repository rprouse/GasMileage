package net.alteridem.mileage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import net.alteridem.mileage.R;
import net.alteridem.mileage.data.VehicleIcon;

public class VehicleIconAdapter extends BaseAdapter implements SpinnerAdapter {

    private LayoutInflater _inflater;

    public VehicleIconAdapter(Context context) {
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return VehicleIcon.getNumberOfIcons();
    }

    @Override
    public Object getItem(int i) {
        return VehicleIcon.getDrawableForId(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if ( view == null ) {
            view = _inflater.inflate(R.layout.vehicle_icon_spinner, viewGroup, false);
        }

        int drawable = VehicleIcon.getDrawableForId(i);
        if ( view != null ) {
            ImageView icon = (ImageView) view.findViewById(R.id.spinner_vehicle_icon);
            icon.setImageResource(drawable);
        }
        return view;
    }
}
