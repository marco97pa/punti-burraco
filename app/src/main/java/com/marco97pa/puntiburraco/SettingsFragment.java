package com.marco97pa.puntiburraco;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import android.provider.Settings;

import android.util.Log;
import android.view.View;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

/**
 * SETTINGS FRAGMENT
 * Fragment of the settings
 *
 * @author Marco Fantauzzo
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    int taps = 0;
    FragmentManager fragmentManager;
    public static final String LOG_TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        fragmentManager = getFragmentManager();

        //Sets version name programatically
        Preference version = findPreference("version");
        version.setSummary(BuildConfig.VERSION_NAME);

        //Set default value for img setting
        //Images are activated by default, except if isLowRamDevice is true
        Preference img = findPreference("img");
        ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            boolean isLowRamDevice = am.isLowRamDevice();
            img.setDefaultValue(!isLowRamDevice);
            Log.d(LOG_TAG, "isLowRamDevice? " + Boolean.toString(isLowRamDevice));
        }
        else{
            img.setDefaultValue(true);
        }


        //Sets sound and volume level
        final SwitchPreference sound = (SwitchPreference) findPreference("sound");
        sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(sound.isChecked()) {
                    sound.setChecked(false);
                }
                else{
                    sound.setChecked(true);
                    AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, AudioManager.FLAG_PLAY_SOUND);
                    Toast.makeText(getActivity(), getString(R.string.audio_up), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        //Sets version name easter egg
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                if (taps == 7) {
                    Toast.makeText(getActivity(), getString(R.string.easter_egg), Toast.LENGTH_SHORT).show();
                }
                if (taps >= 7) {
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
                String testo = getString(R.string.share_message)+ " " + getString(R.string.link); //this is a Dynamic Link
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

        //Sets intent to open input method chooser activity
        Preference input_method = findPreference("input_method");
        input_method.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, new InputMethodChooser())
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });

        //Sets intent to open ads settings
        Preference ads = findPreference("ads_choice");
        ads.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("askAds", true);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
                return true;
            }
        });

        //Show/hide developer options
        Preference developer = findPreference("developer");
        if(BuildConfig.DEBUG){
            developer.setVisible(true);
        }
        else {
            developer.setVisible(false);
        }

        //Sets intent to redirect user to App Notification in Android Oreo
        PreferenceCategory pCategory = (PreferenceCategory) findPreference("interface_settings");
        Preference notification_p = findPreference("notification");
        Preference notification_old = findPreference("notification_old");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pCategory.removePreference(notification_old);   // remove preference
            notification_p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference preference) {
                    String CHANNEL_ID = "channel_suspended";
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                    //redirect user to app Settings
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, appPackageName);
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID);
                    startActivity(intent);
                    return true;
                }
            });
        }
        else{
            pCategory.removePreference(notification_p);   // remove preference
        }

        //Sets label of the closing score
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference editTextPref = (EditTextPreference) findPreference("limite");
        editTextPref
                .setSummary(sp.getString("limite", "2005"));

        //Sets the theme setting
        ListPreference ListPrefTheme = (ListPreference) findPreference("theme");
        ArrayList<String> theme_values = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.theme_values)));
        ArrayList<String> theme_entries = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.theme_entries)));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            //remove follow-system-theme option on <= Android P
            theme_values.remove(2);
            theme_entries.remove(2);
        }
        String default_theme = "light";
        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            default_theme = "system";
        }
        ListPrefTheme.setEntries(theme_entries.toArray(new CharSequence[theme_entries.size()]));
        ListPrefTheme.setEntryValues(theme_values.toArray(new CharSequence[theme_values.size()]));
        ListPrefTheme.setDefaultValue(default_theme);
        String theme[] = getResources().getStringArray(R.array.theme_entries);
        String summ;
        switch (sp.getString("theme", default_theme)){
            case "light": summ = theme[0]; break;
            case "dark": summ = theme[1]; break;
            case "system": summ = theme[2]; break;
            default: summ = theme[0]; break;
        }
        ListPrefTheme.setSummary(summ);

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

        //check if is changed to update theme
        if(pref == findPreference("theme")){
            ListPreference th = (ListPreference) pref;
            pref.setSummary(th.getEntry());
            setAppTheme(th.getValue());
        }

        //check if is EditText to update summary
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }

    }


    public void setAppTheme(String selection) {
        /* SETTING APP THEME
         * Here I set the app theme according to the user choice
         * This method MUST be called before the "setContentView(...)"
         */
        switch (selection) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            default:
                if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Setting activity title
        ((SettingActivity) getActivity()).setTitle(getString(R.string.nav_settings));
    }
}
