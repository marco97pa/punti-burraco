package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;

public class DeveloperSettings extends SettingsFragment {

    public static final String FRAGMENT_TAG = "developer_fragment";

    public DeveloperSettings() {
    }

    public void onCreatePreferences(Bundle bundle, String rootKey) {
        addPreferencesFromResource(R.xml.developer_preference);

        //Sets intent to redirect user to App Settings in Android
        Preference advanced_p = findPreference("advanced");
        advanced_p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                //redirect user to app Settings
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + appPackageName));
                startActivity(i);
                return true;
            }
        });
    }
}