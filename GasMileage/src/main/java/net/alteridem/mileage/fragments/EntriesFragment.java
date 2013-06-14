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
import android.widget.SimpleAdapter;

import net.alteridem.mileage.R;
import net.alteridem.mileage.VehicleActivity;
import net.alteridem.mileage.data.Entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class EntriesFragment extends Fragment
{
    ListView vehicle_entries;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        return inflater.inflate( R.layout.fragment_entries, container, false );
    }

    @Override
    public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo )
    {
        super.onCreateContextMenu( menu, v, menuInfo );
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate( R.menu.entries_menu, menu );
    }

    @Override
    public boolean onContextItemSelected( MenuItem item )
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch ( item.getItemId() )
        {
            case R.id.entry_menu_edit:
                editEntry( info.id );
                return true;
            case R.id.entry_menu_delete:
                deleteEntry( info.id );
                return true;
        }
        return super.onContextItemSelected( item );
    }

    private void editEntry( long id )
    {

    }

    private void deleteEntry( long id )
    {

    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );
        vehicle_entries = (ListView) getActivity().findViewById( R.id.vehicle_entries );
        registerForContextMenu( vehicle_entries );
        fillEntries();
    }

    private void fillEntries()
    {
        VehicleActivity activity = (VehicleActivity)getActivity();
        if ( activity != null )
        {
            fillEntries( activity.getEntries() );
        }
    }

    public void fillEntries( List<Entry> entries )
    {
        if( entries == null )
            return;

        if ( getActivity() == null )
            return;

        // create the grid item mapping
        String[] from = new String[]{ "date", "kilometers", "liters", "mileage" };
        int[] to = new int[]{ R.id.entry_date, R.id.entry_kilometers, R.id.entry_liters, R.id.entry_mileage };

        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

        for ( Entry entry : entries )
        {
            fillMaps.add( getEntry( entry ) );
        }

        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter( getActivity(), fillMaps, R.layout.entry, from, to );
        vehicle_entries.setAdapter( adapter );
    }

    private HashMap<String, String> getEntry( Entry entry )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        Date date = entry.getFillup_date();
        // TODO: Use user pref date format
        String dateStr = String.format( "%d/%d/%d", date.getDate(), date.getMonth() + 1, date.getYear() );
        map.put( "date", dateStr );
        map.put( "kilometers", entry.getDistanceString() );
        map.put( "liters", entry.getVolumeString() );
        map.put( "mileage", entry.getMileageString() );
        return map;
    }
}
