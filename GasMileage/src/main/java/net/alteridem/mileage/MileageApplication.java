package net.alteridem.mileage;

import android.app.Application;

import net.alteridem.mileage.data.DbHelper;

import org.androidannotations.annotations.EApplication;

@EApplication
public class MileageApplication extends Application {

    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DbHelper(this);
    }

    public DbHelper getDbHelper() {
        return dbHelper;
    }
}
