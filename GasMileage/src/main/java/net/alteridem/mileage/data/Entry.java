package net.alteridem.mileage.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.alteridem.mileage.Convert;
import net.alteridem.mileage.MileageApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class Entry {
    static final String TAG = Entry.class.getSimpleName();
    static final String TABLE = "entry";

    static final String C_ID = "id";
    static final String C_VEHICLE_ID = "vehicle_id";
    static final String C_LITRES = "litres";
    static final String C_KILOMETERS = "kilometers";
    static final String C_FILLUP_DATE = "fillup_date";
    static final String C_MILEAGE = "mileage";
    static final String C_NOTE = "note";
    static final String[] COLUMNS = {C_ID, C_VEHICLE_ID, C_LITRES, C_KILOMETERS, C_FILLUP_DATE, C_MILEAGE, C_NOTE};
    static final String ORDER_BY = C_FILLUP_DATE + " DESC";

    private long id;
    private long vehicle_id;
    private double litres;
    private double kilometers;
    private Date fillup_date;
    private String note;

    public Entry(long vehicle_id, Date fillup_date, double kilometers, double litres, String note) {
        this.id = -1;
        this.fillup_date = fillup_date;
        this.kilometers = kilometers;
        this.litres = litres;
        this.note = note;
        this.vehicle_id = vehicle_id;
    }

    public Entry(Cursor cursor) {
        id = cursor.getInt(0);
        vehicle_id = cursor.getInt(1);
        litres = cursor.getDouble(2);
        kilometers = cursor.getDouble(3);
        fillup_date = new Date(cursor.getLong(4));
        note = cursor.getString(6);
    }

    public Date getFillup_date() {
        return fillup_date;
    }

    public void setFillup_date(Date fillup_date) {
        this.fillup_date = fillup_date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public double getMileage() {
        return litres / (kilometers / (double) 100);
    }

    public String getVolumeString() {
        double litres = Convert.volume(getLitres());
        return String.format("%.3f %s", litres, Convert.getVolumeUnitString());
    }

    public String getDistanceString() {
        double kms = Convert.distance(getKilometers());
        return String.format("%.1f %s", kms, Convert.getDistanceUnitString());
    }

    public String getMileageString() {
        double mileage = Convert.mileage(getMileage());
        return String.format("%.2f %s", mileage, Convert.getMileageUnitString());
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
        values.put(C_VEHICLE_ID, vehicle_id);
        values.put(C_LITRES, litres);
        values.put(C_KILOMETERS, kilometers);
        values.put(C_FILLUP_DATE, fillup_date.getTime());
        values.put(C_MILEAGE, getMileage());
        values.put(C_NOTE, note);
        if (id < 0) {
            id = db.insertOrThrow(TABLE, null, values);
            Log.d(TAG, String.format("Inserted entry %d", id));
        } else {
            db.update(TABLE, values, "id=" + id, null);
            Log.d(TAG, String.format("Updated entry %d", id));
        }
        Vehicle.updateLastMileage(db, vehicle_id, getMileage());
    }

    public static List<Entry> fetchAll(long vehicle_id) {
        SQLiteDatabase db = MileageApplication.getApplication().getDbHelper().getWritableDatabase();
        try {
            Cursor cursor = db.query(TABLE, COLUMNS, "vehicle_id=" + vehicle_id, null, null, null, ORDER_BY);
            return createEntryList(cursor);
        } finally {
            db.close();
        }
    }

    private static List<Entry> createEntryList(Cursor cursor) {
        List<Entry> entries = new ArrayList<Entry>();
        try {
            while (cursor.moveToNext()) {
                entries.add(new Entry(cursor));
            }
        } finally {
            cursor.close();
        }
        return entries;

    }

    static void createTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE +
                " ( " + C_ID + " INTEGER PRIMARY KEY, " +
                C_VEHICLE_ID + " INTEGER NOT NULL REFERENCES " + Vehicle.TABLE + " ( " + Vehicle.C_ID + " ) ON DELETE CASCADE, " +
                C_LITRES + " REAL NOT NULL DEFAULT 0, " +
                C_KILOMETERS + " REAL NOT NULL DEFAULT 0, " +
                C_FILLUP_DATE + " INTEGER NOT NULL DEFAULT CURRENT_DATE, " +
                C_MILEAGE + " REAL NOT NULL DEFAULT 0, " +
                C_NOTE + " TEXT )";
        db.execSQL(sql);
        Log.d(TAG, "Created " + TABLE + " table");
    }

    static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
