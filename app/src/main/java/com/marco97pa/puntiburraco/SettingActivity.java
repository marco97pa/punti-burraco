package com.marco97pa.puntiburraco;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import java.util.Random;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */

public class SettingActivity extends AppCompatActivity {

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String[] mColors = {
            "#F44336", //red
            "#E91E63", //pink
            "#9C27B0", //purple
            "#673AB7", //deep purple
            "#3F51B5", //indigo
            "#2196F3", //blue
            "#03A9F4", //light blue
            "#00BCD4", //cyan
            "#009688", //teal
            "#4CAF50", //green
            "#8BC34A", //light green
            "#CDDC39", //lime
            "#FFEB3B", //yellow
            "#FFC107", //amber
            "#FF5722" //deep orange
    };
    public String[] mColorsDark = {
            "#D32F2F", //red
            "#C2185B", //pink
            "#7B1FA2", //purple
            "#512DA8", //deep purple
            "#303F9F", //indigo
            "#1976D2", //blue
            "#0288D1", //light blue
            "#0097A7", //cyan
            "#00796B", //teal
            "#388E3C", //green
            "#689F38", //light green
            "#AFB42B", //lime
            "#FBC02D", //yellow
            "#FFA000", //amber
            "#E64A19" //deep orange
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        //setCustomToolbar
        setupToolbar();
    }

    private void setupToolbar() {
        ViewGroup rootView = (ViewGroup)findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.setting_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setRandomColor(){

        String color = "#fff";
        String colorDark = "#fff";
        // Randomly select a fact
        Random randomGenerator = new Random(); // Construct a new Random number generator
        int randomNumber = randomGenerator.nextInt(mColors.length);

        //Primary color
        color = mColors[randomNumber];
        int colorAsInt = Color.parseColor(color);
        //Dark color
        colorDark = mColorsDark[randomNumber];
        int colorDarkAsInt = Color.parseColor(colorDark);

        //set that color as Action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorAsInt));

        //set that color as Navigation bar color and Status bar color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(colorDarkAsInt);
            getWindow().setStatusBarColor(colorDarkAsInt);
        }

    }

}
