package com.marco97pa.puntiburraco;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import static android.R.attr.id;
import static android.widget.Toast.LENGTH_SHORT;


/**
 * MAIN ACTIVITY
 * The main activity contains a fragment corresponding to the actual mode selected by the user (2,3 or 4 players mode)
 * Mode can be changed by the navigation drawer
 * Specific comments will be provided near relative instruction
 *
 * @author Marco Fantauzzo
 */


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context contextOfApplication;


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    //CREATING ACTIVITY AND FAB BUTTON
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* SETTING APP THEME
         * Here I set the app theme according to the user choice
         * I do it here because it MUST stay before the "setContentViev(...)"
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isNight = sharedPreferences.getBoolean("night", false) ;
        if(isNight){
            setTheme(R.style.DarkMode);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.barBlack));
            }
        }
        else{
            setTheme(R.style.AppTheme_NoActionBar);
        }

        /* CREATING ACTIVITY
         * Creating activity and setting its contents, the toolbar, the fab and the first Fragment
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting first Fragment to display as DoubleFragment (aka 2 players mode)
        Fragment fragment = new DoubleFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "2").commit();

        //Setting the FAB button - It launches the openStart method of the active Fragment inside activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            //FIX The following methods close the (eventually) opened keyboard on opening the navigation bar
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Sets navigation drawer listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Delete notification (if there is any)
        NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

        //If the user opens the app by clicking on a notification, set the Fragment according to the last used mode
        Intent intent = getIntent();
        int mode = intent.getIntExtra("mode", 0);
        if(mode == 2){
            fragment = new DoubleFragment();
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"2").commit();
        }
        else if(mode == 3){
            fragment = new TripleFragment();
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"3").commit();
        }
        else if(mode == 4){
            fragment = new QuadFragment();
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"4").commit();
        }

        //Keep the screen always on if the user has set it true
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isWakeOn = sharedPreferences.getBoolean("wake", false) ;
        if(isWakeOn){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this)
                .setReplaceAll(true);
        EmojiCompat.init(config);
    }


    /* CLOSING ACTIVITY
     * Press back two times to close the app
     */
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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
        notifyIfGameSuspended();
    }


    /*NOTIFY USER ABOUT SUSPENDED MATCH
    * If the user closes the app before one of the players wins,
    * a notification will be shown on his Notification Drawer */
    public void notifyIfGameSuspended(){
        int PDefault = 0;
        int tot1, tot2, tot3, status;
        final String gioc1Default=getString(R.string.s1);
        final String gioc2Default=getString(R.string.s2);
        final String sq1Default=getString(R.string.n1);
        final String sq2Default=getString(R.string.n2);
        final String p1Default=getString(R.string.g1);
        final String p2Default=getString(R.string.g2);
        final String p3Default=getString(R.string.g3);
        String gioc1, gioc2,gioc3, sq1, sq2;
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
        Log.i("STATO:",Integer.toString(status));
        //check if the score is 0 - 0: in that case do not notify
        boolean scoreNotZero = true;
        if(sharedPref.getInt("p1",PDefault) == 0 && sharedPref.getInt("p2",PDefault) == 0
                && sharedPref.getInt("punti1",PDefault) == 0 && sharedPref.getInt("punti2",PDefault) == 0
                && sharedPref.getInt("t1",PDefault) == 0 && sharedPref.getInt("t2",PDefault) == 0 && sharedPref.getInt("t3",PDefault) == 0){
            scoreNotZero = false;
        }
        //if game was interrupted
        if(status != 0 && scoreNotZero){
            //Generating notification text according to the mode selected
            switch (status){
                case 2:
                    tot1 = sharedPref.getInt("p1",PDefault);
                    tot2 = sharedPref.getInt("p2",PDefault);
                    gioc1 = sharedPref.getString("sq1",gioc1Default);
                    gioc2 = sharedPref.getString("sq2",gioc2Default);
                    description = gioc1+" - "+gioc2+"    "+Integer.toString(tot1)+" - "+Integer.toString(tot2);
                    break;
                case 4:
                    tot1 = sharedPref.getInt("punti1",PDefault);
                    tot2 = sharedPref.getInt("punti2",PDefault);
                    sq1 = sharedPref.getString("squadra1",sq1Default);
                    sq2 = sharedPref.getString("squadra2",sq2Default);
                    description = sq1+" - "+sq2+"    "+Integer.toString(tot1)+" - "+Integer.toString(tot2);
                    break;
                case 3:
                    tot1 = sharedPref.getInt("t1",PDefault);
                    tot2 = sharedPref.getInt("t2",PDefault);
                    tot3 = sharedPref.getInt("t3",PDefault);
                    gioc1 = sharedPref.getString("sqd1",p1Default);
                    gioc2 = sharedPref.getString("sqd2",p2Default);
                    gioc3 = sharedPref.getString("sqd3",p3Default);
                    description = gioc1+" - "+gioc2+" - "+gioc3+"  "+Integer.toString(tot1)+" - "+Integer.toString(tot2)+" - "+Integer.toString(tot3);
                    break;

            }

            //and then, make the Notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.notification))
                    .setContentText(description)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            //Intent to open MainActivity (passing the actual player mode (fragment))
            Intent launch_app = new Intent(this, MainActivity.class);
            launch_app.putExtra("mode", status);
            mBuilder.setContentIntent(PendingIntent.getActivity(this,0,launch_app , PendingIntent.FLAG_UPDATE_CURRENT));
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }

    }

    //On return from another activity
    @Override
    public void onResume(){
        super.onResume();
        //Check if there was a setting change
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean settingsChanged = sharedPreferences.getBoolean("setChange", false) ;
        if(settingsChanged){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("setChange", false);
            editor.commit();
            recreate();
        }

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

    /* HANDLE NAVIGATION DRAWER SELECTION
     * It changes the fragment when changing mode
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_2_player) {
            fragment = new DoubleFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"2").commit();
        } else if (id == R.id.nav_3_player) {
            fragment = new TripleFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,"3").commit();

        } else if (id == R.id.nav_4_player) {
            fragment = new QuadFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "4").commit();

        } else if (id == R.id.nav_setting) {
            //Launches Settings Activity
            Intent myIntent = new Intent(this, SettingActivity.class);
            this.startActivity(myIntent);

        } else if (id == R.id.nav_history) {
            //Launches History Activity
            Intent myIntent = new Intent(this, HistoryActivity.class);
            this.startActivity(myIntent);

        } else if (id == R.id.nav_guide) {
            //Check if the device is connected
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                //I am using CustomTabs
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                final Bitmap backButton = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back_black_24dp);
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
                if(localeId.equals("it_IT")) {
                    customTabsIntent.launchUrl(this, Uri.parse("https://punti-burraco.firebaseapp.com/guide-it.html"));
                    Log.i("PASSA", "true");
                }
                else{
                    customTabsIntent.launchUrl(this, Uri.parse("https://punti-burraco.firebaseapp.com/guide-en.html"));
                    Log.i("PASSA", "false");
                }
            }
            else{
                //If user is not connected, shows an alert
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder .setTitle(getString(R.string.nav_guide))
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

        } else if (id == R.id.nav_info) {
            //It opens Contributions Activity
            Intent myIntent = new Intent(this, Contributions.class);
            this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Simple Method to return the Context of App
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }





}
