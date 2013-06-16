package net.alteridem.mileage;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Robert Prouse on 13/06/13.
 */
public class MileagePreferencesActivity extends PreferenceActivity {

    private static final String TAG = MileagePreferencesActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
