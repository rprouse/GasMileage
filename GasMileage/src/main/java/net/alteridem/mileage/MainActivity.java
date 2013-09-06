package net.alteridem.mileage;

import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import net.alteridem.mileage.adapters.VehicleSpinnerAdapter;
import net.alteridem.mileage.data.Vehicle;
import net.alteridem.mileage.fragments.EntriesFragment;
import net.alteridem.mileage.fragments.StatisticsFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,  VehicleDialog.IVehicleDialogListener, EntryDialog.IEntryDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    MileageApplication _application;
    Spinner _spinner;
    List<Vehicle> _vehicleList;
    Vehicle _currentVehicle;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _application = (MileageApplication)getApplication();

        // Subscribe to the preferences changing
        MileageApplication.getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                loadData();
            }
        });

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setCustomView( R.layout.actionbar );
        actionBar.setDisplayShowCustomEnabled(true);

        _spinner = (Spinner) findViewById( R.id.actionbar_vehicles );
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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() )
        {
            case R.id.menu_fill_up:
                enterFillUp();
                break;
            case R.id.menu_add_vehicle:
                addVehicle();
                break;
            case R.id.menu_edit_vehicle:
                editVehicle();
                break;
            case R.id.menu_delete_vehicle:
                deleteVehicle();
                break;
            case R.id.menu_settings:
                startActivity( new Intent( this, MileagePreferencesActivity.class ) );
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StatisticsFragment();
                case 1:
                    return new EntriesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.vehicle_statistics).toUpperCase(l);
                case 1:
                    return getString(R.string.vehicle_entries).toUpperCase(l);
            }
            return null;
        }
    }


    public Vehicle getCurrentVehicle()
    {
        return _currentVehicle;
    }

    private void loadData()
    {
        loadVehicles();
        loadVehicle();
    }

    private void loadVehicle()
    {
        if ( _spinner == null ) return;

        _currentVehicle = (Vehicle) _spinner.getSelectedItem();

        // This will load the data into the current fragment
        // TODO: Notify the fragments to reload their data
    }

    private void loadVehicles()
    {
        if ( _spinner == null ) return;

        _vehicleList = Vehicle.fetchAll();
        ArrayAdapter adapter_veh = new VehicleSpinnerAdapter( this, _vehicleList );
        _spinner.setAdapter( adapter_veh );

        loadLastVehicle();
    }

    private void loadLastVehicle()
    {
        if ( _spinner == null ) return;

        long vehId = MileageApplication.getSharedPreferences().getLong( "last_vehicle", -1 );
        if ( vehId == -1 )
            _spinner.setSelection( 0 );
        else
            switchToVehicle(vehId);
    }

    public void switchToVehicle(long vehicle_id)
    {
        Vehicle v = null;
        for( Vehicle veh : _vehicleList )
        {
            if ( veh.getId() == vehicle_id )
            {
                veh.reload();
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

    private void enterFillUp()
    {
        Log.d(TAG, "enterFillUp");
        FragmentManager fm = getSupportFragmentManager();
        EntryDialog dlg = new EntryDialog( _currentVehicle );
        dlg.show(fm, "entry_dialog");
    }

    private void addVehicle()
    {
        Log.d( TAG, "addVehicle" );
        FragmentManager fm = getSupportFragmentManager();
        VehicleDialog dlg = new VehicleDialog();
        dlg.show(fm, "add_vehicle_dialog");
    }

    private void editVehicle() {
        Log.d( TAG, "editVehicle" );
        FragmentManager fm = getSupportFragmentManager();
        VehicleDialog dlg = new VehicleDialog( _currentVehicle );
        dlg.show( fm, "edit_vehicle_dialog" );

    }

    private void deleteVehicle() {
        // TODO: Delete the vehicle
        Log.d(TAG, "deleteVehicle");
    }

    @Override
    public void onFinishVehicleDialog( Vehicle vehicle )
    {
        // Save the vehicle to the last vehicle used so we can set the
        // spinner to it
        _currentVehicle = vehicle;
        saveLastVehicle();
        loadVehicles();
    }

    @Override
    public void onFinishEntryDialog( Vehicle vehicle )
    {
        switchToVehicle( vehicle.getId() );
    }

}
