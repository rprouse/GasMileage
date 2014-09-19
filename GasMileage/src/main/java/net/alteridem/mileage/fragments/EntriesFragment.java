package net.alteridem.mileage.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.alteridem.mileage.Convert;
import net.alteridem.mileage.R;
import net.alteridem.mileage.VehicleActivity;
import net.alteridem.mileage.adapters.EntriesAdapter;
import net.alteridem.mileage.data.Entry;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;

import java.util.List;

@EFragment(R.layout.fragment_entries)
public class EntriesFragment extends Fragment {
    //ListView _vehicleEntries;
    EntriesAdapter _adapter;

    @Bean
    Convert _convert;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.entries_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if ( info != null ) {
            long entry_id = _adapter.getItemId(info.position);
            if ( entry_id >= 0 ) {
                switch (item.getItemId()) {
                    case R.id.entry_menu_edit:
                        editEntry(entry_id);
                        return true;
                    case R.id.entry_menu_delete:
                        deleteEntry(entry_id);
                        return true;
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    private void editEntry(long id) {
        VehicleActivity activity = (VehicleActivity) getActivity();
        if (activity != null) {
            activity.editFillUp(id);
        }
    }

    private void deleteEntry(long id) {
        VehicleActivity activity = (VehicleActivity) getActivity();
        if (activity != null) {
            activity.deleteFillUp(id);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView vehicleEntries = (ListView) getActivity().findViewById(R.id.vehicle_entries);
        registerForContextMenu(vehicleEntries);
        fillEntries();
    }

    private void fillEntries() {
        VehicleActivity activity = (VehicleActivity) getActivity();
        if (activity != null) {
            fillEntries(activity.getEntries());
        }
    }

    public void fillEntries(List<Entry> entries) {
        if (entries == null || getActivity() == null )
            return;

        ListView vehicleEntries = (ListView) getActivity().findViewById(R.id.vehicle_entries);

        // fill in the grid_item layout
        _adapter = new EntriesAdapter(this, entries, _convert);
        vehicleEntries.setAdapter(_adapter);
    }
}
