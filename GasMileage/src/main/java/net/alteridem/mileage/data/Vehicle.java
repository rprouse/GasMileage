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
    static final String[] COLUMNS = {C_ID, C_NAME};

    static final String QUERY = "SELECT v.id, v.name FROM vehicle v INNER JOIN entry e on e.vehicle_id=v.id WHERE v.id=?";

    private long id;
    private String name;
    private List<Entry> entries;

    @Override
    public String toString() {
        return name;
    }

    public Vehicle(String name) {
        this.id = -1;
        this.name = name;
        this.entries = null;
    }

    public Vehicle(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.entries = null;
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

    public List<Entry> getEntries() {
        if (this.id < 0) {
            entries = new ArrayList<Entry>();
        } else {
            entries = Entry.fetchAll(this.id);
        }
        return this.entries;
    }

    public double getBestMileage() {
        if (entries.size() == 0) {
            return 0;
        }
        double bestMileage = Double.MAX_VALUE;
        for (Entry entry : getEntries()) {
            double mileage = entry.getMileage();
            if (mileage < bestMileage) {
                bestMileage = mileage;
            }
        }
        return Convert.mileage(bestMileage);
    }

    public double getWorstMileage() {
        if (entries.size() == 0) {
            return 0;
        }
        double worstMileage = Double.MIN_VALUE;
        for (Entry entry : getEntries()) {
            double mileage = entry.getMileage();
            if (mileage > worstMileage) {
                worstMileage = mileage;
            }
        }
        return Convert.mileage(worstMileage);
    }

    public double getAverageMileage() {
        if (entries.size() == 0) {
            return 0;
        }
        int count = 0;
        double mileageTotal = 0;
        for (Entry entry : getEntries()) {
            count++;
            mileageTotal += entry.getMileage();
        }
        return Convert.mileage(mileageTotal / count);
    }

    public double getLastMileage() {
        if (entries.size() == 0) {
            return 0;
        }
        return Convert.mileage(entries.get(0).getMileage());
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
        if (id < 0) {
            id = db.insertOrThrow(TABLE, null, values);
            Log.d(TAG, String.format("Inserted vehicle %s with id %d", name, id));
        } else {
            db.update(TABLE, values, "id=" + id, null);
            Log.d(TAG, String.format("Updated vehicle %s with id %d", name, id));
        }
    }

    public static List<Vehicle> fetchAll() {
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            Cursor cursor = db.query(TABLE, COLUMNS, null, null, null, null, null);
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
        Vehicle vehicle = null;
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery( QUERY, new String[] { String.valueOf( id ) } );
            try {
                if (cursor.moveToFirst()) {
                    vehicle = new Vehicle(cursor);
                }
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
        return vehicle;
    }

    static void createTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE +
                " ( " + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_NAME + " TEXT NOT NULL )";
        db.execSQL(sql);
        Log.d(TAG, "Created vehicle table");
    }

    static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        createTable(db);
    }
}
