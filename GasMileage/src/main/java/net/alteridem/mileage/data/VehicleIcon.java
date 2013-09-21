package net.alteridem.mileage.data;

import net.alteridem.mileage.R;

public final class VehicleIcon {

    /**
     * Returns an R.drawable image for a given database id
     * @param id The database id of the icon
     * @return The R.drawable image
     */
    public static int getDrawableForId( int id ) {
        switch ( id ) {
            case 0:
                return R.drawable.vehicle_1;
            case 1:
                return R.drawable.vehicle_2;
            case 2:
                return R.drawable.vehicle_3;
            case 3:
                return R.drawable.vehicle_4;
            case 4:
                return R.drawable.vehicle_5;
            case 5:
                return R.drawable.vehicle_6;
            case 6:
                return R.drawable.vehicle_7;
            case 7:
                return R.drawable.vehicle_8;
            default:
                return R.drawable.vehicle_1;
        }
    }

    /**
     * Returns a database id for the given R.drawable image
     * @param drawable The R.drawable image
     * @return The database id of the icon
     */
    public static int getIdForDrawable( int drawable ) {
        switch ( drawable ) {
            case R.drawable.vehicle_1:
                return 0;
            case R.drawable.vehicle_2:
                return 1;
            case R.drawable.vehicle_3:
                return 2;
            case R.drawable.vehicle_4:
                return 3;
            case R.drawable.vehicle_5:
                return 4;
            case R.drawable.vehicle_6:
                return 5;
            case R.drawable.vehicle_7:
                return 6;
            case R.drawable.vehicle_8:
                return 7;
            default:
                return 0;
        }
    }

    public static int getNumberOfIcons() {
        return 8;
    }
}
