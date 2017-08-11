package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * SETTINGS FRAGMENT
 * Fragment of the settings
 *
 * @author Marco Fantauzzo
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    int taps = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //Sets version name programatically
        Preference version = findPreference("version");
        version.setSummary(BuildConfig.VERSION_NAME);

        //Sets version name easter egg
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                if(taps > 7) {
                    Toast.makeText(getActivity(), getString(R.string.easter_egg), Toast.LENGTH_SHORT).show();
                    ((SettingActivity) getActivity()).setRandomColor();
                }

                taps++;
                return true;
            }
        });

        //Sets intent to share app with friends
        Preference share_p = findPreference("share");
        share_p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                String testo = getString(R.string.share_message)+ " https://hh29c.app.goo.gl/eaFH"; //this is a Dynamic Link
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, testo);
                startActivity(Intent.createChooser(share, getString(R.string.share_hint)));
                return true;
            }
        });

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

        //Sets label of the closing score
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference editTextPref = (EditTextPreference) findPreference("limite");
        editTextPref
                .setSummary(sp.getString("limite", "2005"));

    }


    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //WHEN A PREFERENCE IS CHANGED...
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }

        //...Notify MainActivity about the change
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("setChange", true);
        editor.commit();
    }

}
