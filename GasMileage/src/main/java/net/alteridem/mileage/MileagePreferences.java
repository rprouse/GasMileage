package net.alteridem.mileage;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Author: Rob.Prouse
 * Date: 2014-09-19.
 */
@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface MileagePreferences {

    @DefaultString("lp100km")
    String mileage_units();

    @DefaultString("l")
    String volume_units();

    @DefaultString("km")
    String distance_units();

    @DefaultBoolean(true)
    boolean show_reset_odometer();

    @DefaultLong(-1)
    long last_vehicle();
}
