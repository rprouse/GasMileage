package net.alteridem.mileage.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.alteridem.mileage.Convert;
import net.alteridem.mileage.MileageApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class Vehicle {

    static final String TAG = Vehicle.class.getSimpleName();
    static final String TABLE = "vehicle";
    static final String C_ID = "id";
    static final String C_NAME = "name";
    static final String C_LAST_MILEAGE = "last_mileage";
    static final String[] COLUMNS = {C_ID, C_NAME, C_LAST_MILEAGE};

    static final String QUERY_START = "SELECT v.id, v.name, MIN( e.mileage ), MAX( e.mileage ), AVG( e.mileage ), v.last_mileage FROM vehicle v LEFT OUTER JOIN entry e on e.vehicle_id=v.id ";
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
        icon = 0;
        name = cursor.getString(1);
        entries = null;
        bestMileage = cursor.getDouble(2);
        worstMileage = cursor.getDouble(3);
        averageMileage = cursor.getDouble(4);
        lastMileage = cursor.getDouble(5);
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
     * @return
     */
    public int getIconId() {
        return icon;
    }

    public void setIconId(int id) {
        icon = id;
    }

    /**
     * Gets the R.drawable image for the icon
     * @return
     */
    public int getIcon() {
        return VehicleIcon.getDrawableForId(icon);
    }

    public List<Entry> getEntries() {
        if (this.id < 0) {
            entries = new ArrayList<Entry>();
        } else {
            entries = Entry.fetchAll(this.id);
        }
        return this.entries;
    }

    public double getBestMileageUnconverted() {
        return bestMileage;
    }

    public double getWorstMileageUnconverted() {
        return worstMileage;
    }

    public double getBestMileage() {
        return Convert.mileage(bestMileage);
    }

    public double getWorstMileage() {
        return Convert.mileage(worstMileage);
    }

    public double getAverageMileage() {
        return Convert.mileage(averageMileage);
    }

    public double getLastMileage() {
        return Convert.mileage(lastMileage);
    }

    public void save() {
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            save(db);
        } finally {
            db.close();
        }
    }

    public void save(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
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
    public void updateLastMileage() {
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            Vehicle.updateLastMileage(db, getId());
        } finally {
            db.close();
        }
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
        try {
            if (cursor.moveToNext()) {
                mileage = cursor.getDouble(0);
            }
        } finally {
            cursor.close();
        }
        updateLastMileage( db, id, mileage );
    }

    private static void updateLastMileage(SQLiteDatabase db, long id, double mileage ) {
        ContentValues values = new ContentValues();
        values.put(C_LAST_MILEAGE, mileage);

        db.update(TABLE, values, C_ID+"=?", new String[] { String.valueOf(id) });
        Log.d(TAG, String.format("Updated last mileage for vehicle %d", id));
    }

    public static List<Vehicle> fetchAll() {
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(QUERY_ALL, new String[]{});
            try {
                while (cursor.moveToNext()) {
                    vehicles.add(new Vehicle(cursor));
                }
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
        return vehicles;
    }

    public static Vehicle fetch(long id) {
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            return fetch(db, id);
        } finally {
            db.close();
        }
    }

    public static Vehicle fetch(SQLiteDatabase db, long id) {
        Cursor cursor = db.rawQuery(QUERY_ONE, new String[]{String.valueOf(id)});
        try {
            if (cursor.moveToFirst()) {
                return new Vehicle(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }
    
    public void delete() {
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            db.delete( TABLE, C_ID+"=?", new String[]{String.valueOf(id)});
            db.delete( Entry.TABLE, Entry.C_VEHICLE_ID + "=?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }        
    }

    public void reload() {
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(QUERY_ONE, new String[]{String.valueOf(id)});
            try {
                if (cursor.moveToFirst()) {
                    loadFromCursor(cursor);
                }
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    static void createTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE +
                " ( " + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_NAME + " TEXT NOT NULL, " +
                C_LAST_MILEAGE + " REAL NOT NULL DEFAULT 0 )";
        db.execSQL(sql);
        Log.d(TAG, "Created vehicle table");
    }

    static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
