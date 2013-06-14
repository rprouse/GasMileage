package net.alteridem.mileage;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import net.alteridem.mileage.adapters.VehicleSpinnerAdapter;
import net.alteridem.mileage.data.Entry;
import net.alteridem.mileage.data.Vehicle;
import net.alteridem.mileage.fragments.EntriesFragment;
import net.alteridem.mileage.fragments.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

public class VehicleActivity extends Activity {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final String TAG = VehicleActivity.class.getSimpleName();

    MileageApplication _application;
    Spinner _spinner;
    List<Vehicle> _vehicleList;
    Vehicle _currentVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        _application = (MileageApplication)getApplication();

        // Subscribe to the preferences changing
        MileageApplication.getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                loadData();
            }
        });

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = actionBar.newTab()
                .setText( R.string.vehicle_statistics )
                /*.setIcon( R.drawable.ic_tab_statistics )*/
                .setTabListener( new TabListener<StatisticsFragment>( this, "statistics", StatisticsFragment.class ) );
        actionBar.addTab( tab );

        tab = actionBar.newTab()
                .setText( R.string.vehicle_entries )
                /*.setIcon( R.drawable.ic_tab_entries )*/
                .setTabListener( new TabListener<EntriesFragment>( this, "entries", EntriesFragment.class ) );
        actionBar.addTab( tab );

        _spinner = (Spinner) findViewById( R.id.vehicle_name );
        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _currentVehicle = (Vehicle) _spinner.getSelectedItem();
                saveLastVehicle();
                loadVehicle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadData();
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
//        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
//            getActionBar().setSelectedNavigationItem(
//                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
//        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
//                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vehicle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() )
        {
            case R.id.menu_fill_up:
//                enterFillUp();
                break;
            case R.id.menu_add_vehicle:
//                addVehicle();
                break;
            case R.id.menu_edit_vehicle:
//                editVehicle();
                break;
            case R.id.menu_delete_vehicle:
//                deleteVehicle();
                break;
            case R.id.menu_settings:
                startActivity( new Intent( this, MileagePreferencesActivity.class ) );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Vehicle getCurrentVehicle()
    {
        return _currentVehicle;
    }

    public List<Entry> getEntries()
    {
        List<Entry> entries;
        if ( _currentVehicle != null )
        {
            entries = _currentVehicle.getEntries();
        }
        else
        {
            entries = new ArrayList<Entry>();
        }
        return entries;
    }

    private void loadData()
    {
        loadVehicles();
        loadVehicle();
    }

    private void loadVehicles()
    {
        _vehicleList = Vehicle.fetchAll();
        ArrayAdapter adapter_veh = new VehicleSpinnerAdapter( this, _vehicleList );
        _spinner.setAdapter( adapter_veh );

        loadLastVehicle();
    }

    private void loadLastVehicle()
    {
        long vehId = MileageApplication.getSharedPreferences().getLong( "last_vehicle", -1 );
        if ( vehId == -1 )
            _spinner.setSelection( 0 );
        else
            switchToVehicle(vehId);
    }

    private void saveLastVehicle()
    {
        long vehId = -1;
        if ( _currentVehicle != null )
        {
            vehId = _currentVehicle.getId();
        }
        SharedPreferences.Editor edit = MileageApplication.getSharedPreferences().edit();
        edit.putLong( "last_vehicle", vehId );
        edit.commit();
    }

    public void switchToVehicle(long vehicle_id)
    {
        Vehicle v = null;
        for( Vehicle veh : _vehicleList )
        {
            if ( veh.getId() == vehicle_id )
            {
                v = veh;
                break;
            }
        }
        int pos = _vehicleList.indexOf( v );
        if ( pos >= 0 )
        {
            _spinner.setSelection( pos );
        }
        loadVehicle();
    }

    private void loadVehicle()
    {
        _currentVehicle = (Vehicle) _spinner.getSelectedItem();
        _currentVehicle.getEntries();

        // This will load the data into the current fragment
        setDataToFragment( (Fragment) getActionBar().getSelectedTab().getTag() );
    }

    public void setDataToFragment( Fragment fragment )
    {
        if ( fragment == null )
            return;

        if ( fragment.getClass() == StatisticsFragment.class )
        {
            StatisticsFragment sf = (StatisticsFragment)fragment;
            sf.fillStatistics( _currentVehicle );
        }
        else if ( fragment.getClass() == EntriesFragment.class )
        {
            EntriesFragment ef = (EntriesFragment)fragment;
            ef.fillEntries( getEntries() );
        }
    }
}
