package net.alteridem.mileage;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EBean
public class Convert {

    @Pref
    MileagePreferences_ _preferences;

    private static final double MILES_TO_KM = 0.621371192;
    private static final double US_GAL_TO_L = 0.264172052;
    private static final double IMP_GAL_TO_L = 0.219969157;

    public enum Gallons {
        US,
        Imperial
    }

    public double kilometersToMiles(double kilometers) {
        return kilometers * MILES_TO_KM;
    }

    public double milesToKilometers(double miles) {
        return miles / MILES_TO_KM;
    }

    public double litersToGallons(double liters, Gallons units) {

        return liters * gallonsToLiters(units);
    }

    public double gallonsToLiters(double gallons, Gallons units) {
        return gallons / gallonsToLiters(units);
    }

    public double mileageFromMetric(double lp100km, Gallons units) {
        return (MILES_TO_KM / gallonsToLiters(units)) / lp100km * 100;
    }

    private double gallonsToLiters(Gallons units) {
        return units == Gallons.US ? US_GAL_TO_L : IMP_GAL_TO_L;
    }

    public double mileage(double mileage) {
        String units =_preferences.mileage_units().get();
        if (units.equalsIgnoreCase("kmpl"))
            return 100 / mileage;
        if (units.equalsIgnoreCase("mpg_us"))
            return mileageFromMetric(mileage, Gallons.US);
        if (units.equalsIgnoreCase("mpg_imp"))
            return mileageFromMetric(mileage, Gallons.Imperial);
        else
            return mileage;
    }

    public double volume(double litres) {
        String units = _preferences.volume_units().get();
        if (units.equalsIgnoreCase("gal_us"))
            return litersToGallons(litres, Gallons.US);
        if (units.equalsIgnoreCase("gal_imp"))
            return litersToGallons(litres, Gallons.Imperial);
        return litres;
    }

    public double distance(double kms) {
        String units = _preferences.distance_units().get();
        if (units.equalsIgnoreCase("m"))
            return kilometersToMiles(kms);
        return kms;
    }

    public String getMileageUnitString() {
        String units = _preferences.mileage_units().get();
        if (units.equalsIgnoreCase("kmpl"))
            return "km/L";
        if (units.equalsIgnoreCase("mpg_us"))
            return "mpg (US)";
        if (units.equalsIgnoreCase("mpg_imp"))
            return "mpg (Imp)";
        return "L/100km";
    }

    public String getVolumeUnitString() {
        String units = _preferences.volume_units().get();
        if (units.equalsIgnoreCase("gal_us"))
            return "Gal (US)";
        if (units.equalsIgnoreCase("gal_imp"))
            return "Gal (Imp)";
        return "L";
    }

    public String getDistanceUnitString() {
        String units = _preferences.distance_units().get();
        if (units.equalsIgnoreCase("m"))
            return "Miles";
        return "km";
    }
}
