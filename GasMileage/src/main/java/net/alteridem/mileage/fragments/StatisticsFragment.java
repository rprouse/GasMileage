package net.alteridem.mileage.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.alteridem.mileage.Convert;
import net.alteridem.mileage.R;
import net.alteridem.mileage.VehicleActivity;
import net.alteridem.mileage.data.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class StatisticsFragment extends Fragment {
    //ListView vehicle_statistics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView vehicle_statistics = (ListView) getActivity().findViewById(R.id.vehicle_statistics);

        VehicleActivity activity = (VehicleActivity) getActivity();
        if (activity != null) {
            fillStatistics(activity.getCurrentVehicle());
        }
    }

    public void fillStatistics(Vehicle vehicle) {
        if (vehicle == null)
            return;

        if (getActivity() == null)
            return;

        ListView vehicle_statistics = (ListView) getActivity().findViewById(R.id.vehicle_statistics);

        // create the grid item mapping
        String[] from = new String[]{"label", "value"};
        int[] to = new int[]{R.id.statistic_label, R.id.statistic_value};

        // prepare the list of all records
        Resources res = getResources(); // Resource object to get strings
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        fillMaps.add(getStatisticMap(res.getString(R.string.stat_best_mileage), String.format("%.2f %s", vehicle.getBestMileage(), Convert.getMileageUnitString())));
        fillMaps.add(getStatisticMap(res.getString(R.string.stat_avg_mileage), String.format("%.2f %s", vehicle.getAverageMileage(), Convert.getMileageUnitString())));
        fillMaps.add(getStatisticMap(res.getString(R.string.stat_worst_mileage), String.format("%.2f %s", vehicle.getWorstMileage(), Convert.getMileageUnitString())));
        fillMaps.add(getStatisticMap(res.getString(R.string.stat_last_mileage), String.format("%.2f %s", vehicle.getLastMileage(), Convert.getMileageUnitString())));

        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.statistic, from, to);
        vehicle_statistics.setAdapter(adapter);
    }

    private HashMap<String, String> getStatisticMap(String label, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("label", label);
        map.put("value", value);
        return map;
    }
}
