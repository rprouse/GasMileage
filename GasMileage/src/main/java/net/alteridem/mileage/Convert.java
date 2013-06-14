package net.alteridem.mileage;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class Convert {
    private static final double MILES_TO_KM = 0.621371192;
    private static final double US_GAL_TO_L = 0.264172052;
    private static final double IMP_GAL_TO_L = 0.219969157;

    public enum Gallons {
        US,
        Imperial
    }

    public static double kilometersToMiles(double kilometers) {
        return kilometers * MILES_TO_KM;
    }

    public static double milesToKilometers(double miles) {
        return miles / MILES_TO_KM;
    }

    public static double litersToGallons(double liters, Gallons units) {

        return liters * gallonsToLiters(units);
    }

    public static double gallonsToLiters(double gallons, Gallons units) {
        return gallons / gallonsToLiters(units);
    }

    public static double mileageFromMetric(double lp100km, Gallons units) {
        return (MILES_TO_KM / gallonsToLiters(units)) / lp100km * 100;
    }

    private static double gallonsToLiters(Gallons units) {
        return units == Gallons.US ? US_GAL_TO_L : IMP_GAL_TO_L;
    }

    public static double mileage(double mileage) {
        String units = MileageApplication.getSharedPreferences().getString("mileage_units", "");
        if (units.equalsIgnoreCase("kmpl"))
            return 100 / mileage;
        if (units.equalsIgnoreCase("mpg_us"))
            return mileageFromMetric(mileage, Gallons.US);
        if (units.equalsIgnoreCase("mpg_imp"))
            return mileageFromMetric(mileage, Gallons.Imperial);
        else
            return mileage;
    }

    public static double volume(double litres) {
        String units = MileageApplication.getSharedPreferences().getString("volume_units", "");
        if (units.equalsIgnoreCase("gal_us"))
            return litersToGallons(litres, Gallons.US);
        if (units.equalsIgnoreCase("gal_imp"))
            return litersToGallons(litres, Gallons.Imperial);
        return litres;
    }

    public static double distance(double kms) {
        String units = MileageApplication.getSharedPreferences().getString("distance_units", "");
        if (units.equalsIgnoreCase("m"))
            return kilometersToMiles(kms);
        return kms;
    }

    public static String getMileageUnitString() {
        String units = MileageApplication.getSharedPreferences().getString("mileage_units", "");
        if (units.equalsIgnoreCase("kmpl"))
            return "km/L";
        if (units.equalsIgnoreCase("mpg_us"))
            return "mpg (US)";
        if (units.equalsIgnoreCase("mpg_imp"))
            return "mpg (Imp)";
        return "L/100km";
    }

    public static String getVolumeUnitString() {
        String units = MileageApplication.getSharedPreferences().getString("volume_units", "");
        if (units.equalsIgnoreCase("gal_us"))
            return "Gal (US)";
        if (units.equalsIgnoreCase("gal_imp"))
            return "Gal (Imp)";
        return "L";
    }

    public static String getDistanceUnitString() {
        String units = MileageApplication.getSharedPreferences().getString("distance_units", "");
        if (units.equalsIgnoreCase("m"))
            return "Miles";
        return "km";
    }
}
