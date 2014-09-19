package net.alteridem.mileage;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MileagePreferencesActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
