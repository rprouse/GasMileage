package net.alteridem.mileage.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.alteridem.mileage.R;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class DbHelper extends SQLiteOpenHelper {
    static final String TAG = DbHelper.class.getSimpleName();
    static final String DB_NAME = "mileage.db";
    static final int DB_VERSION = 1;
    Context context;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Vehicle.createTable(db);
        Entry.createTable(db);

        // Insert some default data
        String name = context.getString(R.string.default_vehicle );
        Vehicle vehicle = new Vehicle(name);
        vehicle.save(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        Vehicle.upgradeTable(db, oldVersion, newVersion);
        Entry.upgradeTable(db, oldVersion, newVersion);
    }
}
