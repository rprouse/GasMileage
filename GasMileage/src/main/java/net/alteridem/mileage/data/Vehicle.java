package net.alteridem.mileage.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.alteridem.mileage.Convert;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    static final String TAG = Vehicle.class.getSimpleName();
    static final String TABLE = "vehicle";
    static final String C_ID = "id";
    static final String C_ICON = "icon";
    static final String C_NAME = "name";
    static final String C_LAST_MILEAGE = "last_mileage";
    static final String[] COLUMNS = {C_ID, C_ICON, C_NAME, C_LAST_MILEAGE};

    static final String QUERY_START = "SELECT v.id, v.icon, v.name, MIN( e.mileage ), MAX( e.mileage ), AVG( e.mileage ), v.last_mileage FROM vehicle v LEFT OUTER JOIN entry e on e.vehicle_id=v.id ";
    static final String QUERY_GROUP_BY = "GROUP BY v.id";
    static final String QUERY_ALL = QUERY_START + QUERY_GROUP_BY;
    static final String QUERY_ONE = QUERY_START + " WHERE v.id=? " + QUERY_GROUP_BY;

    static final String QUERY_LAST_MILEAGE = "SELECT " + Entry.C_MILEAGE + " FROM " + Entry.TABLE + " WHERE " + Entry.C_VEHICLE_ID + "=? ORDER BY " + Entry.C_FILLUP_DATE + " DESC";

    private long id;
    private int icon;
    private String name;
    private List<Entry> entries;
    private double bestMileage;
    private double worstMileage;
    private double averageMileage;
    private double lastMileage;

    @Override
    public String toString() {
        return name;
    }

    public Vehicle(int icon, String name) {
        id = -1;
        this.icon = icon;
        this.name = name;
        entries = null;
        bestMileage = 0;
        worstMileage = 0;
        averageMileage = 0;
        lastMileage = 0;
    }

    private Vehicle(Cursor cursor) {
        loadFromCursor(cursor);
    }

    private void loadFromCursor(Cursor cursor) {
        id = cursor.getInt(0);
        icon = cursor.getInt(1);
        name = cursor.getString(2);
        entries = null;
        bestMileage = cursor.getDouble(3);
        worstMileage = cursor.getDouble(4);
        averageMileage = cursor.getDouble(5);
        lastMileage = cursor.getDouble(6);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the database id of the icon
     * @return The database id of the icon
     */
    public int getIconId() {
        return icon;
    }

    public void setIconId(int id) {
        icon = id;
    }

    /**
     * Gets the R.drawable image for the icon
     * @return The R.drawable image for the icon
     */
    public int getIcon() {
        return VehicleIcon.getDrawableForId(icon);
    }

    public List<Entry> getEntries(SQLiteDatabase db) {
        if (this.id < 0) {
            entries = new ArrayList<>();
        } else {
            entries = Entry.fetchAll(db, this.id);
        }
        return this.entries;
    }

    public double getBestMileageUnconverted() {
        return bestMileage;
    }

    public double getWorstMileageUnconverted() {
        return worstMileage;
    }

    public double getBestMileage(Convert convert) {
        return convert.mileage(bestMileage);
    }

    public double getWorstMileage(Convert convert) {
        return convert.mileage(worstMileage);
    }

    public double getAverageMileage(Convert convert) {
        return convert.mileage(averageMileage);
    }

    public double getLastMileage(Convert convert) {
        return convert.mileage(lastMileage);
    }

    public void save(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(C_ICON, icon);
        values.put(C_NAME, name);
        values.put(C_LAST_MILEAGE, lastMileage);
        if (id < 0) {
            id = db.insertOrThrow(TABLE, null, values);
            Log.d(TAG, String.format("Inserted vehicle %s with id %d", name, id));
        } else {
            db.update(TABLE, values, C_ID+"=?", new String[] { String.valueOf(id) });
            Log.d(TAG, String.format("Updated vehicle %s with id %d", name, id));
        }
    }


    /**
     * Updates the last mileage value for this vehicle
     */
    public void updateLastMileage(SQLiteDatabase db) {
        Vehicle.updateLastMileage(db, getId());
    }

    /**
     * Updates the last mileage value for a given vehicle
     * @param db The database to use
     * @param id The id of the vehicle to update
     */
    public static void updateLastMileage( SQLiteDatabase db, long id ) {
        // Issue 24 - Last mileage might not be
        // Calculate the last mileage
        double mileage = 0;
        Cursor cursor = db.rawQuery(QUERY_LAST_MILEAGE, new String[]{String.valueOf(id)});
        if (cursor.moveToNext()) {
            mileage = cursor.getDouble(0);
        }
        updateLastMileage( db, id, mileage );
    }

    private static void updateLastMileage(SQLiteDatabase db, long id, double mileage ) {
        ContentValues values = new ContentValues();
        values.put(C_LAST_MILEAGE, mileage);

        db.update(TABLE, values, C_ID+"=?", new String[] { String.valueOf(id) });
        Log.d(TAG, String.format("Updated last mileage for vehicle %d", id));
    }

    public static List<Vehicle> fetchAll(SQLiteDatabase db) {
        List<Vehicle> vehicles = new ArrayList<>();
        Cursor cursor = db.rawQuery(QUERY_ALL, new String[]{});
        while (cursor.moveToNext()) {
            vehicles.add(new Vehicle(cursor));
        }
        return vehicles;
    }

    public static Vehicle fetch(SQLiteDatabase db, long id) {
        Cursor cursor = db.rawQuery(QUERY_ONE, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            return new Vehicle(cursor);
        }
        return null;
    }
    
    public void delete(SQLiteDatabase db) {
        db.delete( TABLE, C_ID+"=?", new String[]{String.valueOf(id)});
        db.delete( Entry.TABLE, Entry.C_VEHICLE_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void reload(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(QUERY_ONE, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            loadFromCursor(cursor);
        }
    }

    static void createTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE +
                " ( " + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_NAME + " TEXT NOT NULL, " +
                C_LAST_MILEAGE + " REAL NOT NULL DEFAULT 0," +
                C_ICON + " INTEGER NOT NULL DEFAULT 0 )";
        db.execSQL(sql);
        Log.d(TAG, "Created vehicle table");
    }

    static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ( oldVersion < 2 ) {
            // Upgrade to version 2
            String sql = "ALTER TABLE " + TABLE + " ADD COLUMN " + C_ICON + " INTEGER NOT NULL DEFAULT 0";
            db.execSQL(sql);
            Log.d(TAG, "Updated vehicle table to version 2");
        }
    }
}
