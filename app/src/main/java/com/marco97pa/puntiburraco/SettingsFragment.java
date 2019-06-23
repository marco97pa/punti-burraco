package com.marco97pa.puntiburraco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;

import android.widget.Toast;

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
        //check if is night to update theme
        Preference night = findPreference("night");
        if(night == pref){
            getActivity().recreate();
        }
        //check if is EditText to update summary
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }
        //check if is EditText to update summary
        if (pref instanceof ListPreference) {
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

}
