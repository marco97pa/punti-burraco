package com.marco97pa.puntiburraco;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import ca.rmen.sunrisesunset.SunriseSunset;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

/**
 * SETTINGS FRAGMENT
 * Fragment of the settings
 *
 * @author Marco Fantauzzo
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_LOCATION = 100;
    private FusedLocationProviderClient fusedLocationClient;
    int taps = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //Sets version name programatically
        Preference version = findPreference("version");
        version.setSummary(BuildConfig.VERSION_NAME);


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
                String testo = getString(R.string.share_message)+ " https://puntiburraco.page.link/open"; //this is a Dynamic Link
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

        //Sets intent to redirect user to App Notification in Android Oreo
        PreferenceCategory pCategory = (PreferenceCategory) findPreference("interface_settings");
        Preference notification_p = findPreference("notification");
        Preference notification_old = findPreference("notification_old");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

        //Sets label of the background of stories
        ListPreference ListPref = (ListPreference) findPreference("background_stories");
        String v[] = getResources().getStringArray(R.array.listentries);
        String smy;
        switch (sp.getString("background_stories", "1")){
            case "1": ListPref.setIcon(R.drawable.gradient_1_circle); smy = v[0];  break;
            case "2": ListPref.setIcon(R.drawable.gradient_2_circle); smy = v[1]; break;
            case "3": ListPref.setIcon(R.drawable.gradient_3_circle); smy = v[2]; break;
            case "4": ListPref.setIcon(R.drawable.gradient_4_circle); smy = v[3]; break;
            case "5": ListPref.setIcon(R.drawable.gradient_5_circle); smy = v[4]; break;
            case "6": ListPref.setIcon(R.drawable.gradient_6_circle); smy = v[5]; break;
            default: ListPref.setIcon(R.drawable.gradient_1_circle); smy = v[0];
        }
        ListPref.setSummary(smy);

        //Sets the theme setting
        ListPreference ListPrefTheme = (ListPreference) findPreference("theme");
        ArrayList<String> theme_values = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.theme_values)));
        ArrayList<String> theme_entries = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.theme_entries)));
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            //remove follow-system-theme option on <= Android P
            theme_values.remove(3);
            theme_entries.remove(3);
        }
        if (!isPlayServicesAvailable()){
            //remove auto option on device without Google Play APIs available
            theme_values.remove(2);
            theme_entries.remove(2);
        }
        ListPrefTheme.setEntries(theme_entries.toArray(new CharSequence[theme_entries.size()]));
        ListPrefTheme.setEntryValues(theme_values.toArray(new CharSequence[theme_values.size()]));
        String theme[] = getResources().getStringArray(R.array.theme_entries);
        String summ;
        switch (sp.getString("theme", "light")){
            case "light": summ = theme[0]; break;
            case "dark": summ = theme[1]; break;
            case "auto": summ = theme[2]; break;
            case "system": summ = theme[3]; break;
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
        //check if is EditText to update summary
        if (pref == findPreference("background_stories")) {
            ListPreference lp = (ListPreference) pref;
            switch (lp.getValue()){
                case "1": lp.setIcon(R.drawable.gradient_1_circle); break;
                case "2": lp.setIcon(R.drawable.gradient_2_circle); break;
                case "3": lp.setIcon(R.drawable.gradient_3_circle); break;
                case "4": lp.setIcon(R.drawable.gradient_4_circle); break;
                case "5": lp.setIcon(R.drawable.gradient_5_circle); break;
                case "6": lp.setIcon(R.drawable.gradient_6_circle); break;
            }
            pref.setSummary(lp.getEntry());
        }

        //...Notify MainActivity about the change
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("setChange", true);
        editor.commit();
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
            case "auto":
                setAppThemeAuto();
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }


    public void setAppThemeAuto(){
        /* SETTING APP THEME AUTOMATICALLY
         * Here I set the app theme according to the current sun position
         * Based on time and location, I can determine if the sun is up.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                }
            else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Extract latitude and longitude
                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();
                                    if (SunriseSunset.isDay(latitude, longitude)) {
                                        //is DAY, so set the theme accordingly
                                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                                    } else {
                                        //is NIGHT, so set the theme accordingly
                                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                                    }
                                }
                            }
                        });
            }
        }
    }

    protected Boolean isPlayServicesAvailable() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (resultCode == ConnectionResult.SUCCESS){
            return true;
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), resultCode, 1).show();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAppThemeAuto();
                } else {
                    // permission denied, boo!
                    Toast.makeText(getActivity(),getString(R.string.error_location),Toast.LENGTH_LONG).show();
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("theme", "light");
                    editor.commit();
                    ListPreference ListPrefTheme = (ListPreference) findPreference("theme");
                    String theme[] = getResources().getStringArray(R.array.theme_entries);
                    ListPrefTheme.setSummary(theme[0]);
                }
            }
        }
    }

}
