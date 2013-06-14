package net.alteridem.mileage.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

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

        // Insert some test data
        Vehicle mazda = new Vehicle("2005 Mazda 6");
        mazda.save(db);

        new Entry(mazda.getId(), new Date(2010, 10, 16), 406.9, 43.898, "").save(db);
        new Entry(mazda.getId(), new Date(2010, 11, 5), 550.3, 60.365, "").save(db);
        new Entry(mazda.getId(), new Date(2010, 11, 14), 490.7, 60.396, "").save(db);
        new Entry(mazda.getId(), new Date(2011, 1, 27), 502.5, 58.592, "").save(db);

        Vehicle rabbit = new Vehicle("2008 VW Rabbit");
        rabbit.save(db);

        new Entry(rabbit.getId(), new Date(2011, 9, 11), 524.6, 47.537, "").save(db);
        new Entry(rabbit.getId(), new Date(2011, 11, 14), 527.4, 44.324, "").save(db);
        new Entry(rabbit.getId(), new Date(2011, 11, 17), 521.4, 46.594, "").save(db);
        new Entry(rabbit.getId(), new Date(2011, 11, 22), 469.4, 40.968, "").save(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        Vehicle.upgradeTable(db, oldVersion, newVersion);
        Entry.upgradeTable(db, oldVersion, newVersion);
    }
}
