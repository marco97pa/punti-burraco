package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.marco97pa.puntiburraco.utils.FLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

public class DeveloperSettings extends SettingsFragment {

    public static final String TAG = "developer_fragment";
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FLog log = new FLog(TAG);

    public DeveloperSettings() {
    }

    public void onCreatePreferences(Bundle bundle, String rootKey) {

        addPreferencesFromResource(R.xml.developer_preference);

        //Firebase Remote Config initialization
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        String firebaseConfig = "";
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Fetch and activate succeeded");
                            Log.d(TAG, "Config params updated: " + updated);
                            //Generate Configuration String
                            firebaseConfig += "downloads: " + mFirebaseRemoteConfig.getLong("downloads") + "\n";
                            firebaseConfig += "nav_menu_feedback: " + mFirebaseRemoteConfig.getBoolean("nav_menu_feedback") + "\n";

                        } else {
                            Log.d(TAG, "Fetch failed");
                            firebaseConfig = "Fetch failed";
                        }

                        Preference remote_config = findPreference("remote_config");
                        remote_config.setSummary(firebaseConfig);
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

        //Sets intent to crash
        Preference crash_p = findPreference("crash");
        crash_p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                log.d("Forcing a manual crash");
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

        //Sets intent to launch Intro
        Preference intro_p = findPreference("intro");
        intro_p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                log.d( "Forcing launch of MainIntroActivity");
                Intent myIntent = new Intent(getActivity(), MainIntroActivity.class);
                startActivity(myIntent);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Setting activity title
        ((SettingActivity) getActivity()).setTitle(getString(R.string.setting_developer));
    }
}