package net.alteridem.mileage;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.alteridem.mileage.data.DbHelper;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class MileageApplication extends Application {
    private static final String TAG = MileageApplication.class.getSimpleName();

    private static MileageApplication application;
    private DbHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DbHelper(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        application = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public DbHelper getDbHelper() {
        return dbHelper;
    }

    public static MileageApplication getApplication() {
        return application;
    }

    public static SharedPreferences getSharedPreferences() {
        if (application == null)
            return null;

        return application.sharedPreferences;
    }
}
