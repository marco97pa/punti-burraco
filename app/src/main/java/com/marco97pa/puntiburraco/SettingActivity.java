package com.marco97pa.puntiburraco;


import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Opt into true edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 2. Make status & nav bars transparent
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // 3. Toggle icon color: true=dark icons, false=light icons
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

        // Check if the current theme is a dark theme
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            // Dark theme: Use light status bar icons
            controller.setAppearanceLightStatusBars(false);
        } else {
            // Light theme: Use dark status bar icons
            controller.setAppearanceLightStatusBars(true);
        }

        // Get the root content view where the fragment will be placed
        // android.R.id.content is a FrameLayout that is the root of the content area.
        View contentRoot = findViewById(android.R.id.content);
        if (contentRoot != null) {
            ViewCompat.setOnApplyWindowInsetsListener(contentRoot, (v, windowInsets) -> {
                Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Apply padding to account for status bar (top) and navigation bar (bottom)
                // The fragment's view will then be laid out within these paddings.
                v.setPadding(0, 0, 0, systemBars.bottom);

                // If your custom toolbar is inside android.R.id.content (which it isn't based on your setup),
                // you'd adjust its top margin here. But your toolbar setup is separate.

                return WindowInsetsCompat.CONSUMED; // Consume the insets
            });
        }

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

            // 5. Apply insets manually
            applyWindowInsets(toolbar, windowInsets -> {
                Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                lp.topMargin = bars.top;
                toolbar.setLayoutParams(lp);
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public void setRandomColor(){
        String mColors[] =  getResources().getStringArray(R.array.easteregg_colors_light);
        String mColorsDark[] =  getResources().getStringArray(R.array.easteregg_colors_dark);

        // Randomly select a fact
        Random randomGenerator = new Random(); // Construct a new Random number generator
        int randomNumber = randomGenerator.nextInt(mColors.length);

        //Primary color
        int color = Color.parseColor(mColors[randomNumber]);
        //Dark color
        int colorDark = Color.parseColor(mColorsDark[randomNumber]);

        //set that color as Action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        //set that color as Navigation bar color and Status bar color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(colorDark);
            getWindow().setStatusBarColor(colorDark);
        }

    }

    /**
     * Helper to listen for insets on any view and invoke a callback
     */
    private void applyWindowInsets(View view, SettingActivity.InsetCallback callback) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            callback.onApply(windowInsets);
            return windowInsets;
        });
    }


    /**
     * Functional interface for inset callbacks
     */
    private interface InsetCallback {
        void onApply(WindowInsetsCompat insets);
    }

}
