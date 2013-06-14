package net.alteridem.mileage;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class TabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment _fragment;
    private final Activity _activity;
    private final String _mTag;
    private final Class<T> _class;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity The host Activity, used to instantiate the fragment
     * @param tag      The identifier tag for the fragment
     * @param clz      The fragment's Class, used to instantiate the fragment
     */
    public TabListener(Activity activity, String tag, Class<T> clz) {
        _activity = activity;
        _mTag = tag;
        _class = clz;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

        // Check if the fragment is already initialized
        if (_fragment == null) {
            // If not, instantiate and add it to the activity
            _fragment = Fragment.instantiate(_activity, _class.getName());
            fragmentTransaction.add(R.id.vehicle_fragment, _fragment, _mTag);
        } else {
            // If it exists, simply attach it in order to show it
            fragmentTransaction.attach(_fragment);
        }
        tab.setTag(_fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

        if (_fragment != null) {
            // Detach the fragment, because another one is being attached
            fragmentTransaction.detach(_fragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

        // User selected the already selected tab. Usually do nothing.
    }
}
