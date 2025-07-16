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

import androidx.activity.result.contract.ActivityResultContracts;
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
                            log.d( "Fetch and activate succeeded");
                            log.d( "Config params updated: " + updated);
                            //Generate Configuration String
                            firebaseConfig += "downloads: " + mFirebaseRemoteConfig.getLong("downloads") + "\n";
                            firebaseConfig += "nav_menu_feedback: " + mFirebaseRemoteConfig.getBoolean("nav_menu_feedback") + "\n";

                        } else {
                            log.d( "Fetch failed");
                            firebaseConfig = "Fetch failed";
                        }

                        Preference remote_config = findPreference("remote_config");
                        remote_config.setSummary(firebaseConfig);
                    }
                });


        //Sets version code programatically
        Preference version_code = findPreference("version_code");
        version_code.setSummary(Integer.toString(BuildConfig.VERSION_CODE));

        //Sets sdk version programatically
        Preference version_sdk = findPreference("version_sdk");
        int sdk_app = this.getActivity().getApplicationInfo().targetSdkVersion;
        version_sdk.setSummary(
                Integer.toString(sdk_app) + " (" + getAndroidVersionName(sdk_app) + ")"
        );
        Preference version_os = findPreference("version_os");
        int sdk_os = android.os.Build.VERSION.SDK_INT;
        version_os.setSummary(
                Integer.toString(sdk_os) + " (" + getAndroidVersionName(sdk_os) + ")"
        );

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

        //Sets intent to launch Intro
        Preference pro_p = findPreference("pro");
        pro_p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                log.d( "Forcing launch of UpgradeActivity");
                Intent myIntent = new Intent(getActivity(), UpgradeActivity.class);
                startActivity(myIntent);
                return true;
            }
        });

        Preference photo_picker = findPreference("photo_picker");
        Boolean isPhotoPickerAvailable = ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable();
        photo_picker.setSummary(isPhotoPickerAvailable.toString());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Setting activity title
        ((SettingActivity) getActivity()).setTitle(getString(R.string.setting_developer));
    }

    public String getAndroidVersionName(int apiLevel) {
        if(apiLevel >= 33){
            return "Android " + (apiLevel - 20);
        }
        else {
            switch (apiLevel) {
                case 32:
                    return "Android 12L";
                case 31:
                    return "Android 12";
                case 30:
                    return "Android 11";
                case 29:
                    return "Android 10";
                case 28:
                    return "Android 9";
                case 27:
                    return "Android 8.1";
                case 26:
                    return "Android 8.0";
                case 25:
                    return "Android 7.1";
                case 24:
                    return "Android 7.0";
                case 23:
                    return "Android 6.0";
                case 22:
                    return "Android 5.1";
                case 21:
                    return "Android 5.0";
                default:
                    return "Unknown or unsupported API level";
            }
        }
    }

}