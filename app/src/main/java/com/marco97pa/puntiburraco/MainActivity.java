package com.marco97pa.puntiburraco;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.browser.customtabs.CustomTabsIntent;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


/**
 * MAIN ACTIVITY
 * The main activity contains a fragment corresponding to the actual mode selected by the user (2,3 or 4 players mode)
 * Mode can be changed by the navigation drawer
 * Specific comments will be provided near relative instruction
 *
 * @author Marco Fantauzzo
 */


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String TAG = this.getClass().getSimpleName();

    public static Context contextOfApplication;
    public static final int OPEN_SETTINGS = 9001;
    String CHANNEL_ID = "channel_suspended";
    //action bar settings
    Boolean ddp_visibility = true;
    Boolean newgame_show = false;
    double latitude, longitude;
    private boolean isDrawerFixed;
    private ConsentForm form;
    public boolean adsPersonalized = true;
    SharedPreferences sharedPreferences;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    Handler mHandler;
    Runnable runnableCode;

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    //CREATING ACTIVITY AND FAB BUTTON
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setAppTheme();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);


        isDrawerFixed = getResources().getBoolean(R.bool.isTablet);

        /* CREATING ACTIVITY
         * Creating activity and setting its contents, the toolbar, the fab and the first Fragment
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting first Fragment to display as DoubleFragment (aka 2 players mode)
        Fragment fragment = new DoubleFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "2").commit();

        isAppUpgraded();
        checkForConsent();

        //Firebase Remote Config initialization
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Fetch and activate succeeded");
                            Log.d(TAG, "Config params updated: " + updated);

                        } else {
                            Log.d(TAG, "Fetch failed");
                        }
                        setNavFeedback(mFirebaseRemoteConfig.getBoolean("nav_menu_feedback"));
                    }
                });

        //Setting the FAB button - It launches the openStart method of the active Fragment inside activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                String tag = currentFragment.getTag();
                switch (tag) {
                    case "2":
                        ((DoubleFragment) currentFragment).openStart();
                        break;
                    case "3":
                        ((TripleFragment) currentFragment).openStart();
                        break;
                    case "4":
                        ((QuadFragment) currentFragment).openStart();
                        break;
                }
            }
        });

        /* CREATING NAVIGATION DRAWER
         * Creating navigation drawer and setting its contents
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (!isDrawerFixed) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                //FIX The following methods close the (eventually) opened keyboard on opening the navigation bar
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
                }
            };
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        //Sets navigation drawer listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Notification Channel (as of Android 8.0 Oreo)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = CHANNEL_ID;
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.channel_name);
            // The user-visible description of the channel.
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(false);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.YELLOW);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        //Delete notification (if there is any)
        NotificationManager notifManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

        //If the user opens the app by clicking on a notification, set the Fragment according to the last used mode
        Intent intent = getIntent();
        int mode = intent.getIntExtra("mode", 0);
        if (mode == 2) {
            fragment = new DoubleFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "2").commit();
        } else if (mode == 3) {
            fragment = new TripleFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "3").commit();
        } else if (mode == 4) {
            fragment = new QuadFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "4").commit();
        }

        //Keep the screen always on if the user has set it true
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isWakeOn = sharedPreferences.getBoolean("wake", false);
        if (isWakeOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        //EMOJI: Imposta carattere e avvia il download in background
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);

    }


    /* CLOSING ACTIVITY
     * Press back two times to close the app
     */
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) && !isDrawerFixed) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                notifyIfGameSuspended();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
                System.exit(0); // finish activity
            } else {
                Toast.makeText(this, getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        //STOP Theme Handler if active
        if(mHandler != null) {
            mHandler.removeCallbacks(runnableCode);
        }

    }

    @Override
    public void onStop(){
        super.onStop();
    }


    /*NOTIFY USER ABOUT SUSPENDED MATCH
    * If the user closes the app before one of the players wins,
    * a notification will be shown on his Notification Drawer */
    public void notifyIfGameSuspended(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean shouldNotify = sharedPreferences.getBoolean("notification_old", true) ;
        if(shouldNotify) {
            int PDefault = 0;
            int tot1, tot2, tot3, status;
            final String gioc1Default = getString(R.string.gioc_1);
            final String gioc2Default = getString(R.string.gioc_2);
            final String sq1Default = getString(R.string.n1);
            final String sq2Default = getString(R.string.n2);
            final String p1Default = getString(R.string.g1);
            final String p2Default = getString(R.string.g2);
            final String p3Default = getString(R.string.g3);
            String gioc1, gioc2, gioc3, sq1, sq2;
            //Initialize description
            String description = "";
            //Check if the game was interrupted (if no one has already won)
            /*interrupted can be:
             * - 0: one of the players has won, so user doesn't need to be notified
             * - 2: game interrupted in 2 players mode
             * - 3: game interrupted in 3 players mode
             * - 4: game interrupted in 4 players mode*/
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            status = sharedPref.getInt("interrupted", 0);
            Log.i("STATO:", Integer.toString(status));
            //check if the score is 0 - 0: in that case do not notify
            boolean scoreNotZero = true;
            if (sharedPref.getInt("p1", PDefault) == 0 && sharedPref.getInt("p2", PDefault) == 0
                    && sharedPref.getInt("punti1", PDefault) == 0 && sharedPref.getInt("punti2", PDefault) == 0
                    && sharedPref.getInt("t1", PDefault) == 0 && sharedPref.getInt("t2", PDefault) == 0 && sharedPref.getInt("t3", PDefault) == 0) {
                scoreNotZero = false;
            }
            //if game was interrupted
            if (status != 0 && scoreNotZero) {
                //Generating notification text according to the mode selected
                switch (status) {
                    case 2:
                        tot1 = sharedPref.getInt("p1", PDefault);
                        tot2 = sharedPref.getInt("p2", PDefault);
                        gioc1 = sharedPref.getString("sq1", gioc1Default);
                        gioc2 = sharedPref.getString("sq2", gioc2Default);
                        description = gioc1 + " - " + gioc2 + "    " + Integer.toString(tot1) + " - " + Integer.toString(tot2);
                        break;
                    case 4:
                        tot1 = sharedPref.getInt("punti1", PDefault);
                        tot2 = sharedPref.getInt("punti2", PDefault);
                        sq1 = sharedPref.getString("squadra1", sq1Default);
                        sq2 = sharedPref.getString("squadra2", sq2Default);
                        description = sq1 + " - " + sq2 + "    " + Integer.toString(tot1) + " - " + Integer.toString(tot2);
                        break;
                    case 3:
                        tot1 = sharedPref.getInt("t1", PDefault);
                        tot2 = sharedPref.getInt("t2", PDefault);
                        tot3 = sharedPref.getInt("t3", PDefault);
                        gioc1 = sharedPref.getString("sqd1", p1Default);
                        gioc2 = sharedPref.getString("sqd2", p2Default);
                        gioc3 = sharedPref.getString("sqd3", p3Default);
                        description = gioc1 + " - " + gioc2 + " - " + gioc3 + "  " + Integer.toString(tot1) + " - " + Integer.toString(tot2) + " - " + Integer.toString(tot3);
                        break;

                }

                //and then, make the Notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(getString(R.string.notification))
                        .setContentText(description)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setChannelId(CHANNEL_ID);
                //Intent to open MainActivity (passing the actual player mode (fragment))
                Intent launch_app = new Intent(this, MainActivity.class);
                launch_app.putExtra("mode", status);
                mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, launch_app, PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }
        }

    }


    @Override
    public void onResume(){
        super.onResume();

        //Delete notification (if there is any)
        NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_dpp).setVisible(ddp_visibility);
        if(newgame_show) {
            menu.findItem(R.id.action_reset).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        else{
            menu.findItem(R.id.action_reset).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return true;
    }
    /* HANDLE NAVIGATION DRAWER SELECTION
     * It changes the fragment when changing mode
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();
        //reset menu
        setMenuAlternative(false);

        if (id == R.id.nav_2_player) {
            fragment = new DoubleFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"2").commit();
        } else if (id == R.id.nav_3_player) {
            fragment = new TripleFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"3").commit();

        } else if (id == R.id.nav_4_player) {
            fragment = new QuadFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "4").commit();


        } else if (id == R.id.nav_setting) {
            //Launches Settings Activity
            Intent myIntent = new Intent(this, SettingActivity.class);
            this.startActivityForResult(myIntent, OPEN_SETTINGS);

        } else if (id == R.id.nav_history) {
            //Launches History Activity
            Intent myIntent = new Intent(this, HistoryActivity.class);
            this.startActivity(myIntent);

        } else if (id == R.id.nav_guide) {
            //Launch the website in the default section (guide)
            launchWebsite(getString(R.string.nav_guide), null);

        } else if (id == R.id.nav_feedback) {
            //Show a feedback dialog
            sendFeedback();
        } else if (id == R.id.nav_send_app) {
            //Share app link with other apps
            shareAppLink();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!isDrawerFixed) {
                drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * This method handles resuming from other Activities
     * @param requestCode  [int]  if it is equal to OPEN_SETTING, it will invoke Activity recreate() method
     *                     because we are coming back from SettingActivity and we need to refresh the UI accordingly
     *                     to the new settings
     * @param resultCode  [int] if is RESULT_OK SettingsActivity passed to this Activity some Extra data
     * @param data   it contains extra values passed from SettingsActivity such as:
     *                    - askAds  [boolean]  if true, we have to show ad consent dialog calling requestConsent();
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTINGS) {
            recreate();
            if(resultCode == RESULT_OK){
                if(data.getBooleanExtra("askAds", false)){
                    requestConsent();
                }
            }
        }
    }


    //HANDLE SCREEN ORENTATION CHANGES
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        String tag = currentFragment.getTag();
        // Save custom values into the bundle
        savedInstanceState.putString("actual_mode", tag);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        String tag = savedInstanceState.getString("actual_mode");
        Fragment fragment;
        FragmentManager fragmentManager;
        if(tag == "2"){
            fragment = new DoubleFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"2").commit();
        }
        else if(tag == "3"){
            fragment = new TripleFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"3").commit();
        }
        else if(tag == "4"){
            fragment = new QuadFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"4").commit();
        }
    }

    //Simple Method to return the Context of App
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    public void setMenuAlternative(boolean flag){
        if(flag){
            ddp_visibility = false;
            newgame_show = true;
        }
        else{
            ddp_visibility = true;
            newgame_show = false;
        }
        invalidateOptionsMenu();
        //the method above invokes onPrepareOptionsMenu();
    }

    public void launchWebsite(String title, @Nullable String options){
        //Check if the device is connected
        boolean isConnected = checkConnectivity(this);

        if(isConnected) {
            //I am using CustomTabs
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            //BitmapFactory -> ImageDecoder per Android 9.0+ P fix
            Bitmap backButton = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                ImageDecoder.Source source = ImageDecoder.createSource(getResources(), R.drawable.ic_arrow_back);
                try {
                    backButton = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) { e.printStackTrace(); }
            } else{
                backButton = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back);
            }
            builder.setCloseButtonIcon(backButton);
            builder.setShowTitle(true);
            CustomTabsIntent customTabsIntent = builder.build();
            //Takes the user to the localized page of the user guide
            Locale lang;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                lang = getResources().getConfiguration().getLocales().get(0);
            } else{
                //noinspection deprecation
                lang = getResources().getConfiguration().locale;
            }
            String localeId = lang.toString();
            Log.i("LINGUA", localeId);
            //If lang is italian it will be redirected to guide-it.html., else to guide-en.html
            String URLtoLaunch;
            if(localeId.equals("it_IT")) {
                URLtoLaunch = "https://marco97pa.github.io/punti-burraco/guide-it.html";
                Log.i("language", "IT");
            }
            else{
                URLtoLaunch = "https://marco97pa.github.io/punti-burraco/guide-en.html";
                Log.i("language", "EN");
            }

            //Add parameters that a javascript script on the webpage can detect
            int nightModeflag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if(nightModeflag == Configuration.UI_MODE_NIGHT_YES || options != null){
                URLtoLaunch = URLtoLaunch+"?";
            }
            //Set theme
            if(nightModeflag == Configuration.UI_MODE_NIGHT_YES){
                URLtoLaunch = URLtoLaunch + "dark";
                if(options != null){
                    URLtoLaunch = URLtoLaunch + "&";
                }
            }
            //Add options to skip to a certain section
            if(options != null) {
                URLtoLaunch = URLtoLaunch + options;
            }

            //Launch custom tabs
            customTabsIntent.launchUrl(this, Uri.parse(URLtoLaunch));
        }
        else{
            //If user is not connected, shows an alert
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AppTheme_Dialog);
            builder .setTitle(title)
                    .setMessage(getString(R.string.errore_connection))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void shareAppLink(){
        String text = String.format(getString(R.string.share_message), getString(R.string.app_name), getString(R.string.link));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        // Add data to the intent, the receiving app will decide what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(share, getString(R.string.share_hint)));
    }

    private void setNavFeedback(Boolean set){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem nav_feedback = menu.findItem(R.id.nav_feedback);
        nav_feedback.setVisible(set);
    }

    private void sendFeedback(){
        final Context context = this;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogRateTheme);
        builder .setTitle(getString(R.string.setting_rate))
                .setMessage(getString(R.string.setting_rate_d))
                .setPositiveButton(getString(R.string.like), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
                        context.startActivity(webIntent);
                    }
                })
                .setNeutralButton(getString(R.string.dislike), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog);
                        builder2.setTitle(R.string.send_bug_report)
                                .setMessage(getString(R.string.send_bug_report_question))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Launch the website in the feedback section (called bug)
                                        launchWebsite(getString(R.string.nav_guide), "bug");
                                    }
                                })
                                .setNegativeButton(getString(R.string.ko), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert2 = builder2.create();
                        alert2.show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Checks if app has been updated to do things like upgrade databases or show a message to the user
     * <p>It checks if <b>Build VERSION_CODE</b> differs from last runtime and saves it</p>
     */
    public void isAppUpgraded(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int actualVersion = BuildConfig.VERSION_CODE;
        int lastVersion = sharedPreferences.getInt("last_version", 0);
        if(actualVersion != lastVersion){
            //App has been updated, do something like upgrade or show a message to user
            switch (actualVersion){
                case 5040:
                    break;
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_version", actualVersion);
        editor.apply();
    }

    private void checkForConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {getString(R.string.admob_pub_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d(TAG, "Showing Personalized ads");
                        adsPersonalized = true;
                        ConsentInformation.getInstance(getApplicationContext()).setConsentStatus(ConsentStatus.PERSONALIZED);
                        break;
                    case NON_PERSONALIZED:
                        Log.d(TAG, "Showing Non-Personalized ads");
                        adsPersonalized = false;
                        ConsentInformation.getInstance(getApplicationContext()).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        break;
                    case UNKNOWN:
                        Log.d(TAG, "Requesting Consent");
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            adsPersonalized = true;
                            ConsentInformation.getInstance(getApplicationContext()).setConsentStatus(ConsentStatus.PERSONALIZED);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    private void requestConsent() {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL("https://marco97pa.github.io/punti-burraco/privacy_policy");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error
            Log.d(TAG, "Requesting Consent: Bad or missing privacy policy URL");
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "Requesting Consent: onConsentFormLoaded");
                        showForm();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "Requesting Consent: onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d(TAG, "Requesting Consent: onConsentFormClosed");
                            switch (consentStatus) {
                                case PERSONALIZED:
                                    adsPersonalized = true;
                                    ConsentInformation.getInstance(getApplicationContext()).setConsentStatus(ConsentStatus.PERSONALIZED);
                                    break;
                                case NON_PERSONALIZED:
                                    adsPersonalized = false;
                                    ConsentInformation.getInstance(getApplicationContext()).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                    break;
                                case UNKNOWN:
                                    ConsentInformation.getInstance(getApplicationContext()).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                    adsPersonalized = false;
                                    break;
                            }

                        }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d(TAG, "Requesting Consent: onConsentFormError. Error - " + errorDescription);
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form == null) {
            Log.d(TAG, "Consent form is null");
        }
        if (form != null) {
            Log.d(TAG, "Showing consent form");
            form.show();
        } else {
            Log.d(TAG, "Not Showing consent form");
        }
    }

    public void setAppTheme() {
        /* SETTING APP THEME
         * Here I set the app theme according to the user choice
         * This method MUST be called before the "setContentView(...)"
         */
        String default_theme = "light";
        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            default_theme = "system";
        }
        switch (sharedPreferences.getString("theme", default_theme)) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @SuppressLint("MissingPermission") //Lint asks for a ACCESS_NETWORK_STATE PERMISSION that is already in the Manifest
    public static boolean checkConnectivity(Context mContext) {
        if (mContext == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            nc.hasTransport((NetworkCapabilities.TRANSPORT_ETHERNET)));
                }
            } else {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo tempNetworkInfo : networkInfos) {
                    if (tempNetworkInfo.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
