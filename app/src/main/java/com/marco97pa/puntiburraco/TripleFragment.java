package com.marco97pa.puntiburraco;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yalantis.ucrop.UCrop;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * TRIPLE FRAGMENT
 * Fragment of the 3 players mode
 * Some parts of this code was written by me at the age of 16, so it can be written better
 * It is really similar to DoubleFragment and QuadFragment, except for some calculation and error checks difference
 * So I will not provide comments of this Fragment
 *
 * @author Marco Fantauzzo
 */

public class TripleFragment extends Fragment {

    public static final String TAG = "3PlayersFragment";
    int bp1,bp2,bp3, bi1, bi2,bi3, bs1, bs2,bs3,pn1,pn2,pn3,tot1,tot2,tot3,pm1,pm2,pm3, pb1, pb2, pb3;
    int tot=0;
    private TextView textNome1, textNome2, textNome3;
    private TextView punti1, punti2, punti3;
    private EditText BP1, BP2, BP3;
    private EditText BI1, BI2, BI3;
    private EditText BS1, BS2, BS3;
    private EditText PN1, PN2, PN3;
    private EditText PB1, PB2, PB3;
    private EditText PM1, PM2, PM3;
    private CheckBox CH1, CH2, CH3;
    private CheckBox RB1, RB2, RB3;
    private CheckBox PZ1, PZ2, PZ3;
    private ImageView IMG1, IMG2, IMG3; //Images of players
    boolean check1=false, check2=false, check3=false;
    int SET, input_method;
    private AdView mAdView;
    final int PDefault=0;
    boolean win=false;
    String winner,loser1,loser2;
    //Constants response to import images in app
    private static int REQUEST_PICTURE_3 = 13;
    private static int REQUEST_CROP_PICTURE_3 = 23;
    private static int REQUEST_PICTURE_2 = 12;
    private static int REQUEST_CROP_PICTURE_2 = 22;
    private static int REQUEST_PICTURE_1 = 11;
    private static int REQUEST_CROP_PICTURE_1 = 21;

    //Constants response to permission request (Android 6.0+)
    private final static int STORAGE_PERMISSION_PICTURE_1 = 13;
    private final static int STORAGE_PERMISSION_PICTURE_2 = 23;
    private final static int STORAGE_PERMISSION_PICTURE_3 = 33;
    private final static int STORAGE_PERMISSION_SCREENSHOT = 30;
    private final static int LOCATION_PERMISSION_NEARBY = 40;

    public int old_tot1;
    public int old_tot2;
    public int old_tot3;

    String bgColor, txtColor, colors;
    boolean bypass = false;
    Boolean isManoModeActivated;
    MediaPlayer sound;
    private FirebaseAnalytics mFirebaseAnalytics;
    private NearbyAdvertise advertise;

    public TripleFragment() {
        // Empty constructor required for fragment subclasses
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_triple, container, false);

        tot1=0;
        tot2=0;
        tot3=0;
        textNome1 = (TextView) rootView.findViewById(R.id.editNome1);
        textNome2 = (TextView) rootView.findViewById(R.id.editNome2);
        textNome3 = (TextView) rootView.findViewById(R.id.editNome3);
        punti1 = (TextView) rootView.findViewById(R.id.Punti1);
        BP1 = (EditText) rootView.findViewById(R.id.editBP1);
        BI1 = (EditText) rootView.findViewById(R.id.editBI1);
        BS1 = (EditText) rootView.findViewById(R.id.editBS1);
        PN1 = (EditText) rootView.findViewById(R.id.editP1);
        PM1 = (EditText) rootView.findViewById(R.id.editPM1);
        punti2 = (TextView) rootView.findViewById(R.id.Punti2);
        BP2 = (EditText) rootView.findViewById(R.id.editBP2);
        BI2 = (EditText) rootView.findViewById(R.id.editBI2);
        BS2 = (EditText) rootView.findViewById(R.id.editBS2);
        PN2 = (EditText) rootView.findViewById(R.id.editP2);
        PM2 = (EditText) rootView.findViewById(R.id.editPM2);
        punti3 = (TextView) rootView.findViewById(R.id.Punti3);
        BP3 = (EditText) rootView.findViewById(R.id.editBP3);
        BI3 = (EditText) rootView.findViewById(R.id.editBI3);
        BS3 = (EditText) rootView.findViewById(R.id.editBS3);
        PN3 = (EditText) rootView.findViewById(R.id.editP3);
        PM3 = (EditText) rootView.findViewById(R.id.editPM3);
        CH1 = (CheckBox) rootView.findViewById(R.id.chiusura1);
        CH2 = (CheckBox) rootView.findViewById(R.id.chiusura2);
        CH3 = (CheckBox) rootView.findViewById(R.id.chiusura3);
        PZ1 = (CheckBox) rootView.findViewById(R.id.pozzetto1);
        PZ2 = (CheckBox) rootView.findViewById(R.id.pozzetto2);
        PZ3 = (CheckBox) rootView.findViewById(R.id.pozzetto3);
        PB1 = (EditText) rootView.findViewById(R.id.editPB1);
        PB2 = (EditText) rootView.findViewById(R.id.editPB2);
        PB3 = (EditText) rootView.findViewById(R.id.editPB3);
        IMG1= (ImageView) rootView.findViewById(R.id.image1);
        IMG2= (ImageView) rootView.findViewById(R.id.image2);
        IMG3= (ImageView) rootView.findViewById(R.id.image3); 

        sound = MediaPlayer.create(getActivity(), R.raw.fischio);

        mAdView = rootView.findViewById(R.id.adView);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Boolean isPro = sharedPref.getBoolean("pro_user", false) ;
        if(!isPro) {
            MobileAds.initialize(getActivity(), getString(R.string.admob_app_id));
            showAds();
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        //get Actual Theme Colors
        bgColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getActivity(),R.color.dialogBackground)));
        txtColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getActivity(),R.color.dialogText)));
        colors = "<html><body bgcolor='"+ bgColor +"' style='color: " + txtColor + "'>";

        /**
         * PUNTI DIRETTI e PUNTI IN MANO NASCOSTI
         *
         * */
        isManoModeActivated = sharedPref.getBoolean("input_puntimano", true) ;
        if(!isManoModeActivated){
            PM1.setVisibility(View.GONE);
            PM2.setVisibility(View.GONE);
            PM3.setVisibility(View.GONE);
            bypass = true; //PERMETTI DI CHIUDERE SENZA POZZETTO E SENZA PUNTI IN MANO
        }
        input_method = sharedPref.getInt("input_method", 1) ;
        switch(input_method){
            case 1:
                PB1.setVisibility(View.GONE);
                PB2.setVisibility(View.GONE);
                PB3.setVisibility(View.GONE);
                break;
            case 2:
                PM1.setVisibility(View.GONE);
                BP1.setVisibility(View.GONE);
                BI1.setVisibility(View.GONE);
                BS1.setVisibility(View.GONE);
                PM2.setVisibility(View.GONE);
                BP2.setVisibility(View.GONE);
                BI2.setVisibility(View.GONE);
                BS2.setVisibility(View.GONE);
                CH1.setVisibility(View.GONE);
                CH2.setVisibility(View.GONE);
                PZ1.setVisibility(View.GONE);
                PZ2.setVisibility(View.GONE);
                PZ3.setVisibility(View.GONE);
                CH3.setVisibility(View.GONE);
                PM3.setVisibility(View.GONE);
                BP3.setVisibility(View.GONE);
                BI3.setVisibility(View.GONE);
                BS3.setVisibility(View.GONE);
                break;
            case 3:
                PM1.setVisibility(View.GONE);
                BP1.setVisibility(View.GONE);
                BI1.setVisibility(View.GONE);
                BS1.setVisibility(View.GONE);
                PM2.setVisibility(View.GONE);
                BP2.setVisibility(View.GONE);
                BI2.setVisibility(View.GONE);
                BS2.setVisibility(View.GONE);
                CH1.setVisibility(View.GONE);
                CH2.setVisibility(View.GONE);
                PZ1.setVisibility(View.GONE);
                PZ2.setVisibility(View.GONE);
                PZ3.setVisibility(View.GONE);
                CH3.setVisibility(View.GONE);
                PM3.setVisibility(View.GONE);
                BP3.setVisibility(View.GONE);
                BI3.setVisibility(View.GONE);
                BS3.setVisibility(View.GONE);

                PB1.setVisibility(View.GONE);
                PB2.setVisibility(View.GONE);
                PB3.setVisibility(View.GONE);
                break;
        }


        //SET POZZETTO - Imposta squadre
        RB1 = (CheckBox) rootView.findViewById(R.id.rB1);
        RB1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                if(RB2.isChecked()||RB3.isChecked()){
                    Snackbar.make(getView(), R.string.error01, Snackbar.LENGTH_SHORT).show();
                    RB1.setChecked(false);
                }
                else{
                    if(PN3.getVisibility()==View.VISIBLE){
                        SET=View.INVISIBLE;
                    }
                    else{
                        SET=View.VISIBLE;
                    }
                    PN3.setVisibility(SET);
                    if(input_method == 1) {
                        BP3.setVisibility(SET);
                        BI3.setVisibility(SET);
                        if(isManoModeActivated) {
                            PM3.setVisibility(SET);
                        }
                        CH3.setVisibility(SET);
                        PZ3.setVisibility(SET);
                        BS3.setVisibility(SET);
                    }
                    if(input_method == 2){
                        PB3.setVisibility(SET);
                    }
                }
            }

        });
        RB2 = (CheckBox) rootView.findViewById(R.id.rB2);
        RB2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                if(RB1.isChecked()||RB3.isChecked()){
                    Snackbar.make(getView(), R.string.error01, Snackbar.LENGTH_SHORT).show();
                    RB2.setChecked(false);
                }
                else{
                    if(PN1.getVisibility()==View.VISIBLE){
                        SET=View.INVISIBLE;
                    }
                    else{
                        SET=View.VISIBLE;
                    }
                    PN1.setVisibility(SET);
                    if(input_method == 1) {
                        BP1.setVisibility(SET);
                        BI1.setVisibility(SET);
                        if(isManoModeActivated) {
                            PM1.setVisibility(SET);
                        }
                        CH1.setVisibility(SET);
                        PZ1.setVisibility(SET);
                        BS1.setVisibility(SET);
                    }
                    if(input_method == 2){
                        PB1.setVisibility(SET);
                    }
                }
            }
        });
        RB3 = (CheckBox) rootView.findViewById(R.id.rB3);
        RB3.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v){
                if(RB1.isChecked()||RB2.isChecked()){
                    Snackbar.make(getView(), R.string.error01, Snackbar.LENGTH_SHORT).show();
                    RB3.setChecked(false);
                }
                else{
                    if(PN2.getVisibility()==View.VISIBLE){
                        SET=View.INVISIBLE;
                    }
                    else{
                        SET=View.VISIBLE;
                    }
                    PN2.setVisibility(SET);
                    if(input_method == 1) {
                        BP2.setVisibility(SET);
                        BI2.setVisibility(SET);
                        if(isManoModeActivated) {
                            PM2.setVisibility(SET);
                        }
                        CH2.setVisibility(SET);
                        PZ2.setVisibility(SET);
                        BS2.setVisibility(SET);
                    }
                    if(input_method == 2){
                        PB2.setVisibility(SET);
                    }
                }
            }
        });
        
        Restore();
        
        textNome1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialogGioc1();
                onSave();
            }
        });
        textNome2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialogGioc2();
                onSave();
            }
        });
        textNome3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialogGioc3();
                onSave();
            }
        });
        
        IMG1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Animation
                IMG1.bringToFront();
                IMG1.invalidate();
                IMG1.animate()
                        .scaleX(10)
                        .scaleY(10)
                        .setDuration(500)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator){
                                IMG1.setScaleX(1);
                                IMG1.setScaleY(1);
                            }
                        });
                //Pick image from user Gallery
                // First, request permission (Android 6.0+ only)
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_PICTURE_1);
                }
                else {
                    startActivityForResult(MediaStoreUtils.getPickImageIntent(getActivity()), REQUEST_PICTURE_1);
                }

            }
        });

        IMG1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeVibration();
                PopupMenu popup = new PopupMenu(getActivity(), view);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeText:
                                textNome1.setText(getString(R.string.g1));
                                onSave();
                                //advertise
                                if(advertise != null && advertise.isRunning()) {
                                    Log.d(TAG, "Advertising: " + getMatchState());
                                    advertise.update(getMatchState());
                                }
                                return true;
                            case R.id.removeImage:
                                File file1 = new File(getActivity().getFilesDir(), "img_m3_1.jpg");
                                file1.delete();
                                IMG1.setImageResource(R.drawable.circle_placeholder);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.actions);
                popup.show();
                return false;
            }

        });

        IMG2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Animation
                IMG2.bringToFront();
                IMG2.invalidate();
                IMG2.animate()
                        .scaleX(10)
                        .scaleY(10)
                        .setDuration(500)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator){
                                IMG2.setScaleX(1);
                                IMG2.setScaleY(1);
                            }
                        });
                //Pick image from user Gallery
                // First, request permission
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_PICTURE_2);
                }
                else {
                    startActivityForResult(MediaStoreUtils.getPickImageIntent(getActivity()), REQUEST_PICTURE_2);
                }

            }
        });

        IMG2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeVibration();
                PopupMenu popup = new PopupMenu(getActivity(), view);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeText:
                                textNome2.setText(getString(R.string.g2));
                                onSave();
                                //advertise
                                if(advertise != null && advertise.isRunning()) {
                                    Log.d(TAG, "Advertising: " + getMatchState());
                                    advertise.update(getMatchState());
                                }
                                return true;
                            case R.id.removeImage:
                                File file2 = new File(getActivity().getFilesDir(), "img_m3_2.jpg");
                                file2.delete();
                                IMG2.setImageResource(R.drawable.circle_placeholder);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.actions);
                popup.show();
                return false;
            }

        });

        IMG3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Animation
                IMG3.bringToFront();
                IMG3.invalidate();
                IMG3.animate()
                        .scaleX(10)
                        .scaleY(10)
                        .setDuration(500)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator){
                                IMG3.setScaleX(1);
                                IMG3.setScaleY(1);
                            }
                        });
                //Pick image from user Gallery
                // First, request permission
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_PICTURE_3);
                }
                else {
                    startActivityForResult(MediaStoreUtils.getPickImageIntent(getActivity()), REQUEST_PICTURE_3);
                }

            }
        });

        IMG3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeVibration();
                PopupMenu popup = new PopupMenu(getActivity(), view);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeText:
                                textNome3.setText(getString(R.string.g3));
                                onSave();
                                //advertise
                                if(advertise != null && advertise.isRunning()) {
                                    Log.d(TAG, "Advertising: " + getMatchState());
                                    advertise.update(getMatchState());
                                }
                                return true;
                            case R.id.removeImage:
                                File file3 = new File(getActivity().getFilesDir(), "img_m3_3.jpg");
                                file3.delete();
                                IMG3.setImageResource(R.drawable.circle_placeholder);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.actions);
                popup.show();
                return false;
            }

        });


        //improves usability in Android N with MultiWindow and avoids bugs
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (getActivity().isInMultiWindowMode()) {
                IMG1.setVisibility(View.GONE);
                IMG2.setVisibility(View.GONE);
                IMG3.setVisibility(View.GONE);
                Log.d(TAG, "images disabled: isInMultiWindowMode = true");
            }
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreImages();
        restoreEditedViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveEditedViews();
    }

    protected void dialogGioc1() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_gioc, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        textNome1.setText(editText.getText().toString());
                        onSave();
                        //advertise
                        if(advertise != null && advertise.isRunning()) {
                            Log.d(TAG, "Advertising: " + getMatchState());
                            advertise.update(getMatchState());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.ko),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    protected void dialogGioc2() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_gioc, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        textNome2.setText(editText.getText().toString());
                        onSave();
                        //advertise
                        if(advertise != null && advertise.isRunning()) {
                            Log.d(TAG, "Advertising: " + getMatchState());
                            advertise.update(getMatchState());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.ko),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    protected void dialogGioc3() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_gioc, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        textNome3.setText(editText.getText().toString());
                        onSave();
                        //advertise
                        if(advertise != null && advertise.isRunning()) {
                            Log.d(TAG, "Advertising: " + getMatchState());
                            advertise.update(getMatchState());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.ko),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_share:
                //FIX CHIUDI TASTIERA
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(textNome1.getWindowToken(), 0);

                // First, request permission
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_SCREENSHOT);
                }
                else {
                    openScreen();
                }
                return true;

            case R.id.action_set:
                showNoticeDialog();
                return true;
            case R.id.action_reset:
                openReset();
                return true;
            case R.id.action_nomi:
                openNomi();
                return true;
            case R.id.action_dpp:
                showDettPuntParz();
                return true;
            case R.id.action_advertise:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                Boolean isPro = sharedPref.getBoolean("pro_user", false) ;
                if(isPro) {
                    //Start Advertising using Nearby library
                    //First ask the permission in Android 6.0+
                    Log.d(TAG,"Option selected: Advertise");
                    if(advertise == null || !advertise.isRunning()){
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_NEARBY);
                        }
                        else {
                            advertise = new NearbyAdvertise(getContext(), getMatchState());
                            advertise.start();
                        }
                    }
                    else{
                        advertise.stop();
                        item.setTitle(getString(R.string.join));
                    }
                }
                else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
                    builder .setTitle(getString(R.string.join))
                            .setMessage(getString(R.string.only_for_pro))
                            .setIcon(R.drawable.ic_warning_24dp)
                            .setPositiveButton(getString(R.string.upgrade), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Launches Upgrade Activity
                                    Intent myIntent = new Intent(getActivity(), UpgradeActivity.class);
                                    startActivity(myIntent);
                                }
                            })
                            .setNegativeButton(getString(R.string.ko), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);}
    }
    //Ripristino Save
    public void Restore(){
        final String sq1Default=getString(R.string.g1);
        final String sq2Default=getString(R.string.g2);
        final String sq3Default=getString(R.string.g3);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        tot1 = sharedPref.getInt("t1",PDefault);
        tot2 = sharedPref.getInt("t2",PDefault);
        tot3 = sharedPref.getInt("t3",PDefault);
        punti1.setText(Integer.toString(tot1));
        punti2.setText(Integer.toString(tot2));
        punti3.setText(Integer.toString(tot3));
        textNome1.setText(sharedPref.getString("sqd1",sq1Default));
        textNome2.setText(sharedPref.getString("sqd2",sq2Default));
        textNome3.setText(sharedPref.getString("sqd3",sq3Default));
    }

    /**
     * RECOVER IMAGES OF LAST GAME
     * Images are showed only if user has choose it in settings
     */
    public void restoreImages(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        //On lowRamDevice images are disabled par default
        boolean isLowRamDevice = false;
        ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isLowRamDevice = am.isLowRamDevice();
            Log.d(TAG, "isLowRamDevice? " + Boolean.toString(isLowRamDevice));
        }

        Boolean isImgActivated = sharedPref.getBoolean("img", !isLowRamDevice) ;
        if(isImgActivated) {
            //BitmapFactory -> ImageDecoder per Android 9.0+ P fix
            Bitmap bitmap1 = null, bitmap2 = null, bitmap3 = null;
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                try {
                    File bmp1 = new File(getActivity().getFilesDir() + "/img_m3_1.jpg");
                    ImageDecoder.Source source1 = ImageDecoder.createSource(bmp1);
                    bitmap1 = ImageDecoder.decodeBitmap(source1);
                } catch (IOException e) { e.printStackTrace(); }
                try {
                    File bmp2 = new File(getActivity().getFilesDir() + "/img_m3_2.jpg");
                    ImageDecoder.Source source2 = ImageDecoder.createSource(bmp2);
                    bitmap2 = ImageDecoder.decodeBitmap(source2);
                } catch (IOException e) { e.printStackTrace(); }
                try {
                    File bmp3 = new File(getActivity().getFilesDir() + "/img_m3_3.jpg");
                    ImageDecoder.Source source3 = ImageDecoder.createSource(bmp3);
                    bitmap3 = ImageDecoder.decodeBitmap(source3);
                } catch (IOException e) { e.printStackTrace(); }
            }
            else{
                bitmap1 = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/img_m3_1.jpg");
                bitmap2 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m3_2.jpg");
                bitmap3 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m3_3.jpg");
            }
            //Set Bitmap to Image if not null
            if(bitmap1 != null) {
                IMG1.setImageBitmap(bitmap1);
            }
            if(bitmap2 != null) {
                IMG2.setImageBitmap(bitmap2);
            }
            if(bitmap3 != null) {
                IMG3.setImageBitmap(bitmap3);
            }
        }
        else{
            IMG1.setVisibility(View.GONE);
            IMG2.setVisibility(View.GONE);
            IMG3.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_advertise);
        if(advertise != null && advertise.isRunning()) {
            item.setTitle(getString(R.string.stop));
        }
    }

    private void saveEditedViews() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Here we handle saving all inserted values to prevent losing them in a configuration change
        editor.putString("BP1",BP1.getText().toString());
        editor.putString("BI1",BI1.getText().toString());
        editor.putString("BS1",BS1.getText().toString());
        editor.putString("PN1",PN1.getText().toString());
        editor.putString("PM1",PM1.getText().toString());
        editor.putString("BP2",BP2.getText().toString());
        editor.putString("BI2",BI2.getText().toString());
        editor.putString("BS2",BS2.getText().toString());
        editor.putString("PN2",PN2.getText().toString());
        editor.putString("PM2",PM2.getText().toString());
        editor.putString("BP3",BP3.getText().toString());
        editor.putString("BI3",BI3.getText().toString());
        editor.putString("BS3",BS3.getText().toString());
        editor.putString("PN3",PN3.getText().toString());
        editor.putString("PM3",PM3.getText().toString());
        editor.putString("PB1",PB1.getText().toString());
        editor.putString("PB2",PB2.getText().toString());
        editor.putString("PB3",PB3.getText().toString());
        editor.putBoolean("CH1",CH1.isChecked());
        editor.putBoolean("CH2",CH2.isChecked());
        editor.putBoolean("CH3",CH3.isChecked());
        editor.putBoolean("PZ1",PZ1.isChecked());
        editor.putBoolean("PZ2",PZ2.isChecked());
        editor.putBoolean("PZ3",PZ3.isChecked());
        editor.putBoolean("RB1",RB1.isChecked());
        editor.putBoolean("RB2",RB2.isChecked());
        editor.putBoolean("RB3",RB3.isChecked());

        editor.apply();
    }

    private void restoreEditedViews(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        //Restore EditTexts and Checkboxes state after a configuration change
        BP1.setText(sharedPref.getString("BP1",""));
        BI1.setText(sharedPref.getString("BI1",""));
        BS1.setText(sharedPref.getString("BS1",""));
        PN1.setText(sharedPref.getString("PN1",""));
        PM1.setText(sharedPref.getString("PM1",""));
        BP2.setText(sharedPref.getString("BP2",""));
        BI2.setText(sharedPref.getString("BI2",""));
        BS2.setText(sharedPref.getString("BS2",""));
        PN2.setText(sharedPref.getString("PN2",""));
        PM2.setText(sharedPref.getString("PM2",""));
        BP3.setText(sharedPref.getString("BP3",""));
        BI3.setText(sharedPref.getString("BI3",""));
        BS3.setText(sharedPref.getString("BS3",""));
        PN3.setText(sharedPref.getString("PN3",""));
        PM3.setText(sharedPref.getString("PM3",""));
        PB1.setText(sharedPref.getString("PB1",""));
        PB2.setText(sharedPref.getString("PB2",""));
        PB3.setText(sharedPref.getString("PB3",""));
        CH1.setChecked(sharedPref.getBoolean("CH1", false));
        CH2.setChecked(sharedPref.getBoolean("CH2", false));
        CH3.setChecked(sharedPref.getBoolean("CH3", false));
        PZ1.setChecked(sharedPref.getBoolean("PZ1", false));
        PZ2.setChecked(sharedPref.getBoolean("PZ2", false));
        PZ3.setChecked(sharedPref.getBoolean("PZ3", false));

        if(sharedPref.getBoolean("RB1", false)){
            RB1.performClick();
        }
        if(sharedPref.getBoolean("RB2", false)){
            RB2.performClick();
        }
        if(sharedPref.getBoolean("RB3", false)){
            RB3.performClick();
        }
    }
    
    //RIPRISTINO NOMI
    public void openNomi(){
        textNome1.setText(getString(R.string.g1));
        textNome2.setText(getString(R.string.g2));
        textNome3.setText(getString(R.string.g3));
        onSave();
        //reset Immagini
        File file1 = new File(getActivity().getFilesDir(), "img_m3_1.jpg");
        file1.delete();
        File file2 = new File(getActivity().getFilesDir(), "img_m3_2.jpg");
        file2.delete();
        File file3 = new File(getActivity().getFilesDir(), "img_m3_3.jpg");
        file3.delete();
        IMG1.setImageResource(R.drawable.circle_placeholder);
        IMG2.setImageResource(R.drawable.circle_placeholder);
        IMG3.setImageResource(R.drawable.circle_placeholder);
        //advertise
        if(advertise != null && advertise.isRunning()) {
            Log.d(TAG, "Advertising: " + getMatchState());
            advertise.update(getMatchState());
        }
    }

    //AVVIA RESET
    public void openReset(){
        BP1.setText("");
        BI1.setText("");
        PN1.setText("");
        BS1.setText("");
        PM1.setText("");
        BP2.setText("");
        BI2.setText("");
        PN2.setText("");
        BS2.setText("");
        PM2.setText("");
        BP3.setText("");
        BI3.setText("");
        PN3.setText("");
        PM3.setText("");
        BS3.setText("");
        PB1.setText("");
        PB2.setText("");
        PB3.setText("");
        CH1.setChecked(false);
        CH2.setChecked(false);
        CH3.setChecked(false);
        PZ1.setChecked(false);
        PZ2.setChecked(false);
        PZ3.setChecked(false);
        RB1.setChecked(false);
        RB2.setChecked(false);
        RB3.setChecked(false);
        tot1=0;
        tot2=0;
        tot3=0;
        punti1.setText(Integer.toString(tot1));
        punti2.setText(Integer.toString(tot2));
        punti3.setText(Integer.toString(tot3));
        PN3.setVisibility(View.VISIBLE);
        PN1.setVisibility(View.VISIBLE);
        PN2.setVisibility(View.VISIBLE);
        if(input_method == 1) {
            BP3.setVisibility(View.VISIBLE);
            BI3.setVisibility(View.VISIBLE);
            BS3.setVisibility(View.VISIBLE);
            CH3.setVisibility(View.VISIBLE);
            BP1.setVisibility(View.VISIBLE);
            BI1.setVisibility(View.VISIBLE);
            BS1.setVisibility(View.VISIBLE);
            CH1.setVisibility(View.VISIBLE);
            BP2.setVisibility(View.VISIBLE);
            BI2.setVisibility(View.VISIBLE);
            BS2.setVisibility(View.VISIBLE);
            CH2.setVisibility(View.VISIBLE);
            PZ1.setVisibility(View.VISIBLE);
            PZ2.setVisibility(View.VISIBLE);
            PZ3.setVisibility(View.VISIBLE);
            if(isManoModeActivated) {
                PM3.setVisibility(View.VISIBLE);
                PM1.setVisibility(View.VISIBLE);
                PM2.setVisibility(View.VISIBLE);
            }
        }
        win=false;
        //salva tutto
        onSave();
        saveEditedViews();
        //advertise
        if(advertise != null && advertise.isRunning()) {
            Log.d(TAG, "Advertising: " + getMatchState());
            advertise.update(getMatchState());
        }
        //reset dpp
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dpp3", "");
        editor.putInt("interrupted", 0);
        editor.apply();
        //make action bar standard again
        ((MainActivity)getActivity()).setMenuAlternative(false);
        Snackbar.make(getView(), R.string.reset, Snackbar.LENGTH_SHORT).show();
    }



    //AVVIA CALCOLO PUNTI
    public void openStart(){
        try{
        boolean ciSonoErrori=checkErrors();
        if(ciSonoErrori==false){
            if (RB1.isChecked()==false) {
                if(BP3.getText().toString().matches("")){bp3=0;}
                else{bp3 = Integer.parseInt(BP3.getText().toString());}
                if(BI3.getText().toString().matches("")){bi3=0;}
                else{bi3 = Integer.parseInt(BI3.getText().toString());}
                if(BS3.getText().toString().matches("")){bs3=0;}
                else{bs3 = Integer.parseInt(BS3.getText().toString());}
                if(PN3.getText().toString().matches("")){pn3=0;}
                else{pn3 = Integer.parseInt(PN3.getText().toString());}
                if(PM3.getText().toString().matches("")){pm3=0;}
                else{pm3 = Integer.parseInt(PM3.getText().toString());}
                if (PB3.getText().toString().matches("")) {
                    pb3 = 0;
                } else {
                    pb3 = Integer.parseInt(PB3.getText().toString());
                }
            }
            if (RB2.isChecked()==false) {
                if(BP1.getText().toString().matches("")){bp1=0;}
                else{bp1 = Integer.parseInt(BP1.getText().toString());}
                if(BI1.getText().toString().matches("")){bi1=0;}
                else{bi1 = Integer.parseInt(BI1.getText().toString());}
                if(BS1.getText().toString().matches("")){bs1=0;}
                else{bs1 = Integer.parseInt(BS1.getText().toString());}
                if(PN1.getText().toString().matches("")){pn1=0;}
                else{pn1 = Integer.parseInt(PN1.getText().toString());}
                if(PM1.getText().toString().matches("")){pm1=0;}
                else{pm1 = Integer.parseInt(PM1.getText().toString());}
                if (PB1.getText().toString().matches("")) {
                    pb1 = 0;
                } else {
                    pb1 = Integer.parseInt(PB1.getText().toString());
                }
            }
            if (RB3.isChecked()==false) {
                if(BP2.getText().toString().matches("")){bp2=0;}
                else{bp2 = Integer.parseInt(BP2.getText().toString());}
                if(BI2.getText().toString().matches("")){bi2=0;}
                else{bi2 = Integer.parseInt(BI2.getText().toString());}
                if(BS2.getText().toString().matches("")){bs2=0;}
                else{bs2 = Integer.parseInt(BS2.getText().toString());}
                if(PN2.getText().toString().matches("")){pn2=0;}
                else{pn2 = Integer.parseInt(PN2.getText().toString());}
                if(PM2.getText().toString().matches("")){pm2=0;}
                else{pm2 = Integer.parseInt(PM2.getText().toString());}
                if (PB2.getText().toString().matches("")) {
                    pb2 = 0;
                } else {
                    pb2 = Integer.parseInt(PB2.getText().toString());
                }
            }

            old_tot1 = tot1;
            old_tot2 = tot2;
            old_tot3 = tot3;

            if(RB1.isChecked()){
                tot1=tot1+( ((bp1*200)+(bi1*100)+(bs1*150)+pn1+pb1)-pm1);
                if (CH1.isChecked()) {
                    tot1=tot1+100;
                }
                if (PZ1.isChecked()) {
                    tot1=tot1-100;
                }
                punti1.setText(Integer.toString(tot1));

                tot=( ((bp2*200)+(bi2*100)+(bs2*150)+pn2+pb2)-pm2);
                if (CH2.isChecked()) {
                    tot=tot+100;
                }
                if (PZ2.isChecked()) {
                    tot=tot-100;
                }
                tot2=tot2+(tot/2);
                tot3=tot3+(tot/2);
                punti2.setText(Integer.toString(tot2));
                punti3.setText(Integer.toString(tot3));
            }

            if(RB2.isChecked()){
                tot2=tot2+( ((bp2*200)+(bi2*100)+(bs2*150)+pn2+pb2)-pm2);
                if (CH2.isChecked()) {
                    tot2=tot2+100;
                }
                if (PZ2.isChecked()) {
                    tot2=tot2-100;
                }
                punti2.setText(Integer.toString(tot2));

                tot=( ((bp3*200)+(bi3*100)+(bs3*150)+pn3+pb3)-pm3);
                if (CH3.isChecked()) {
                    tot=tot+100;
                }
                if (PZ3.isChecked()) {
                    tot=tot-100;
                }
                tot1=tot1+(tot/2);
                tot3=tot3+(tot/2);
                punti1.setText(Integer.toString(tot1));
                punti3.setText(Integer.toString(tot3));
            }

            if(RB3.isChecked()){
                tot3=tot3+( ((bp3*200)+(bi3*100)+(bs3*150)+pn3+pb3)-pm3);
                if (CH3.isChecked()) {
                    tot3=tot3+100;
                }
                if (PZ3.isChecked()) {
                    tot3=tot3-100;
                }
                punti3.setText(Integer.toString(tot3));

                tot=( ((bp1*200)+(bi1*100)+(bs1*150)+pn1+pb1)-pm1);
                if (CH1.isChecked()) {
                    tot=tot+100;
                }
                if (PZ1.isChecked()) {
                    tot=tot-100;
                }
                tot1=tot1+(tot/2);
                tot2=tot2+(tot/2);
                punti1.setText(Integer.toString(tot1));
                punti2.setText(Integer.toString(tot2));
            }
            if(RB1.isChecked()==false&&RB2.isChecked()==false&&RB3.isChecked()==false){
                tot1=tot1+( ((bp1*200)+(bi1*100)+(bs1*150)+pn1+pb1)-pm1);
                if (CH1.isChecked()) {
                    tot1=tot1+100;
                }
                if (PZ1.isChecked()) {
                    tot1=tot1-100;
                }
                tot2=tot2+( ((bp2*200)+(bi2*100)+(bs2*150)+pn2+pb2)-pm2);
                if (CH2.isChecked()) {
                    tot2=tot2+100;
                }
                if (PZ2.isChecked()) {
                    tot2=tot2-100;
                }
                tot3=tot3+( ((bp3*200)+(bi3*100)+(bs3*150)+pn3+pb3)-pm3);
                if (CH3.isChecked()) {
                    tot3=tot3+100;
                }
                if (PZ3.isChecked()) {
                    tot3=tot3-100;
                }
                punti1.setText(Integer.toString(tot1));
                punti2.setText(Integer.toString(tot2));
                punti3.setText(Integer.toString(tot3));
            }
            //genera il dpp
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String html=sharedPref.getString("dpp3", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("dpp3",html+"<tr><td>"+tot1+"</td><td>"+tot2+"</td><td>"+tot3+"</td></tr>");
            editor.apply();
            //reset (no tot)
            BP1.setText("");
            BI1.setText("");
            BS1.setText("");
            PN1.setText("");
            PM1.setText("");
            BP2.setText("");
            BI2.setText("");
            BS2.setText("");
            PN2.setText("");
            PM2.setText("");
            BP3.setText("");
            BI3.setText("");
            BS3.setText("");
            PN3.setText("");
            PM3.setText("");
            PB1.setText("");
            PB2.setText("");
            PB3.setText("");
            CH1.setChecked(false);
            CH2.setChecked(false);
            CH3.setChecked(false);
            PZ1.setChecked(false);
            PZ2.setChecked(false);
            PZ3.setChecked(false);
            RB1.setChecked(false);
            RB2.setChecked(false);
            RB3.setChecked(false);

            PN3.setVisibility(View.VISIBLE);
            PN1.setVisibility(View.VISIBLE);
            PN2.setVisibility(View.VISIBLE);
            if(input_method == 1) {
                BP3.setVisibility(View.VISIBLE);
                BI3.setVisibility(View.VISIBLE);
                BS3.setVisibility(View.VISIBLE);
                CH3.setVisibility(View.VISIBLE);
                BP1.setVisibility(View.VISIBLE);
                BI1.setVisibility(View.VISIBLE);
                BS1.setVisibility(View.VISIBLE);
                CH1.setVisibility(View.VISIBLE);
                BP2.setVisibility(View.VISIBLE);
                BI2.setVisibility(View.VISIBLE);
                BS2.setVisibility(View.VISIBLE);
                CH2.setVisibility(View.VISIBLE);
                PZ1.setVisibility(View.VISIBLE);
                PZ2.setVisibility(View.VISIBLE);
                PZ3.setVisibility(View.VISIBLE);
                if(isManoModeActivated) {
                    PM3.setVisibility(View.VISIBLE);
                    PM1.setVisibility(View.VISIBLE);
                    PM2.setVisibility(View.VISIBLE);
                }
            }
            //SnackBar con annulla
            Snackbar.make(getView(), getString(R.string.add_point), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ko), new annullaPunti())  // action text on the right side
                    .setDuration(10000).show();
            //vincita
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            int limite = Integer.parseInt(sharedPreferences.getString("limite", "2005"));;
            String winText = getString(R.string.win);
            String points="";
            if(tot1>=limite && tot1>tot2 && tot1>tot3){
                win=true;
                winner=textNome1.getText().toString();
                loser1=textNome2.getText().toString();
                loser2=textNome3.getText().toString();
                //save score to DB
                saveScoreToDB(textNome1.getText().toString(), textNome2.getText().toString(), textNome3.getText().toString(), tot1, tot2, tot3);
                //save event
                saveScoreToFirebase(textNome1.getText().toString(), textNome2.getText().toString(), textNome3.getText().toString(), tot1, tot2, tot3);
                //make alert
                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);

                String out=textNome1.getText().toString();
                out = out +" ";
                out=out.concat(winText);
                builder.setTitle(out);
                points=punti1.getText().toString();
                points=points.concat(" - ");
                points=points.concat(punti2.getText().toString());
                points=points.concat(" - ");
                points=points.concat(punti3.getText().toString());
                //Play sound
                Boolean soundActive = sharedPreferences.getBoolean("sound", true) ;
                if(soundActive) {
                    sound.start();
                }
                WebView webview = generateWebView();
                builder.setView(webview);
                builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            if(tot2>=limite && tot2>tot3 && tot2>tot1){
                winner=textNome2.getText().toString();
                loser1=textNome1.getText().toString();
                loser2=textNome3.getText().toString();
                //save score to DB
                saveScoreToDB(textNome1.getText().toString(), textNome2.getText().toString(), textNome3.getText().toString(), tot1, tot2, tot3);
                //save event
                saveScoreToFirebase(textNome1.getText().toString(), textNome2.getText().toString(), textNome3.getText().toString(), tot1, tot2, tot3);
                //make alert
                win=true;
                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
                String out=textNome2.getText().toString();
                out = out +" ";
                out=out.concat(winText);
                builder.setTitle(out);
                points=punti1.getText().toString();
                points=points.concat(" - ");
                points=points.concat(punti2.getText().toString());
                points=points.concat(" - ");
                points=points.concat(punti3.getText().toString());
                //Play sound
                Boolean soundActive = sharedPreferences.getBoolean("sound", true) ;
                if(soundActive) {
                    sound.start();
                }
                WebView webview = generateWebView();
                builder.setView(webview);
                builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            if(tot3>=limite && tot3>tot2 && tot3>tot1){
                winner=textNome3.getText().toString();
                loser1=textNome2.getText().toString();
                loser2=textNome1.getText().toString();

                //save score to DB
                saveScoreToDB(textNome1.getText().toString(), textNome2.getText().toString(), textNome3.getText().toString(), tot1, tot2, tot3);
                //save event
                saveScoreToFirebase(textNome1.getText().toString(), textNome2.getText().toString(), textNome3.getText().toString(), tot1, tot2, tot3);

                //make alert
                win=true;
                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
                String out=textNome3.getText().toString();
                out = out +" ";
                out=out.concat(winText);
                builder.setTitle(out);
                points=punti1.getText().toString();
                points=points.concat(" - ");
                points=points.concat(punti2.getText().toString());
                points=points.concat(" - ");
                points=points.concat(punti3.getText().toString());
                //Play sound
                Boolean soundActive = sharedPreferences.getBoolean("sound", true) ;
                if(soundActive) {
                    sound.start();
                }
                WebView webview = generateWebView();
                builder.setView(webview);
                builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            //salva
            onSave();
            saveEditedViews();
            //salvataggiSeVince
            //advertise
            if(advertise != null && advertise.isRunning()) {
                Log.d(TAG, "Advertising: " + getMatchState());
                advertise.update(getMatchState());
            }
        }
        }
        /*
        catch (NullPointerException e){
            Snackbar.make(getView(),getString(R.string.error00), BaseTransientBottomBar.LENGTH_LONG).show();
        }
        */
        catch (NumberFormatException e){
            Snackbar.make(getView(),getString(R.string.error04), BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }

    public class annullaPunti implements View.OnClickListener{

        public void onClick(View v) {
            // Code to undo the user's last action
            tot1=old_tot1;
            tot2=old_tot2;
            tot3=old_tot3;
            punti1.setText(Integer.toString(tot1));
            punti2.setText(Integer.toString(tot2));
            punti3.setText(Integer.toString(tot3));
            onSave();
            //advertise
            if(advertise != null && advertise.isRunning()) {
                Log.d(TAG, "Advertising: " + getMatchState());
                advertise.update(getMatchState());
            }
        }
    }

    //SALVATAGGIO AUTOMATICO
    public void onSave(){
        final String sq1Default=getString(R.string.g1);
        final String sq2Default=getString(R.string.g2);
        final String sq3Default=getString(R.string.g3);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(win==false){
            editor.putInt("t1", tot1);
            editor.putInt("t2", tot2);
            editor.putInt("t3", tot3);
            editor.putString("sqd1",textNome1.getText().toString());
            editor.putString("sqd2",textNome2.getText().toString());
            editor.putString("sqd3",textNome3.getText().toString());
            editor.putInt("interrupted", 3);
            editor.apply();
        }
        else{
            editor.putInt("t1", PDefault);
            editor.putInt("t2", PDefault);
            editor.putInt("t3", PDefault);
            editor.putString("sqd1", sq1Default);
            editor.putString("sqd2",sq2Default);
            editor.putString("sqd3",sq3Default);
            editor.putInt("interrupted", 0);
            editor.putString("dpp3", "");
            //set alternative bar
            ((MainActivity)getActivity()).setMenuAlternative(true);
            editor.apply();
        }
    }

    private String getMatchState(){
        String num_players = "3";
        String name_player1 = textNome1.getText().toString();
        String name_player2 = textNome2.getText().toString();
        String name_player3 = textNome3.getText().toString();
        String points_player1 = Integer.toString(tot1);
        String points_player2 = Integer.toString(tot2);
        String points_player3 = Integer.toString(tot3);
        String out = num_players + ";" + name_player1 + ";" + name_player2 + ";" + name_player3 + ";" +
                points_player1 +  ";" + points_player2 + ";" + points_player3 + ";";
        return out;
    }

    //RICHIESTA PERMESSI Android 6.0+ (Marshmallow)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_PICTURE_1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    startActivityForResult(MediaStoreUtils.getPickImageIntent(getActivity()), REQUEST_PICTURE_1);

                } else {

                    // permission denied, boo!
                    Toast.makeText(getActivity(), getString(R.string.marshmallow_alert), Toast.LENGTH_LONG).show();

                }
                return;
            }

            case STORAGE_PERMISSION_PICTURE_2:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    startActivityForResult(MediaStoreUtils.getPickImageIntent(getActivity()), REQUEST_PICTURE_2);

                } else {

                    // permission denied, boo!
                    Toast.makeText(getActivity(), getString(R.string.marshmallow_alert), Toast.LENGTH_LONG).show();

                }
                return;
            }

            case STORAGE_PERMISSION_PICTURE_3:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    startActivityForResult(MediaStoreUtils.getPickImageIntent(getActivity()), REQUEST_PICTURE_3);

                } else {

                    // permission denied, boo!
                    Toast.makeText(getActivity(), getString(R.string.marshmallow_alert), Toast.LENGTH_LONG).show();

                }
                return;
            }

            case STORAGE_PERMISSION_SCREENSHOT:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    openScreen();

                } else {

                    // permission denied, boo!
                    Toast.makeText(getActivity(), getString(R.string.marshmallow_alert_2), Toast.LENGTH_LONG).show();

                }
                return;
            }

            case LOCATION_PERMISSION_NEARBY:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    advertise = new NearbyAdvertise(getContext(), getMatchState());
                    advertise.start();

                } else {

                    // permission denied, boo!
                    Toast.makeText(getActivity(), getString(R.string.denied_perm_location), Toast.LENGTH_LONG).show();

                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //OPEN THE SHARE SCREEN
    public void openScreen(){
        Intent myIntent = new Intent(getActivity(), ShareResultActivity.class);
        myIntent.putExtra("name1", textNome1.getText().toString());
        myIntent.putExtra("name2", textNome2.getText().toString());
        myIntent.putExtra("name3", textNome3.getText().toString());
        myIntent.putExtra("score1", tot1);
        myIntent.putExtra("score2", tot2);
        myIntent.putExtra("score3", tot3);
        this.startActivity(myIntent);
    }


    //CONTROLLI ERRORI
    public boolean checkErrors(){
        int check=1;
        boolean res=false;
        if(PM1.getText().toString().matches("")){check=0;}
        else{check = Integer.parseInt(PM1.getText().toString());}

        if(PZ1.isChecked()){
            if(CH1.isChecked()){
                Snackbar.make(getView(), R.string.error01, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
            if(check==0&&bypass==false){
                Snackbar.make(getView(), R.string.error03, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
        }

        if(PM2.getText().toString().matches("")){check=0;}
        else{check = Integer.parseInt(PM2.getText().toString());}

        if(PZ2.isChecked()){
            if(CH2.isChecked()){
                Snackbar.make(getView(), R.string.error01, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
            if(check==0&&bypass==false){
                Snackbar.make(getView(), R.string.error03, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
        }

        if(PM3.getText().toString().matches("")){check=0;}
        else{check = Integer.parseInt(PM3.getText().toString());}

        if(PZ3.isChecked()){
            if(CH3.isChecked()){
                Snackbar.make(getView(), R.string.error01, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
            if(check==0&&bypass==false){
                Snackbar.make(getView(), R.string.error03, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
        }
        return res;
    }

    /**
     * SAVE MATCH TO DATABASE
     * If one of the players wins, the score of the match will be saved in History ScoreDB
     */
    public void saveScoreToDB(final String player1, final String player2, final String player3, final int point1, final int point2, final int point3) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable(){
            public void run(){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
            String date = dateformat.format(c.getTime());

            ScoreDB db = new ScoreDB(getActivity());
            db.open();
            long id = db.insertScore(player1, player2, player3, point1, point2, point3, date, generateHandsDetail());
            db.close();
            }
        });
    }

    private void saveScoreToFirebase(String player1, String player2, String player3, int score1, int score2, int score3){
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.CHARACTER, player1);
        bundle1.putLong(FirebaseAnalytics.Param.LEVEL, 3);
        bundle1.putLong(FirebaseAnalytics.Param.SCORE, score1);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putString(FirebaseAnalytics.Param.CHARACTER, player2);
        bundle2.putLong(FirebaseAnalytics.Param.LEVEL, 3);
        bundle2.putLong(FirebaseAnalytics.Param.SCORE, score2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle2);

        Bundle bundle3 = new Bundle();
        bundle3.putString(FirebaseAnalytics.Param.CHARACTER, player3);
        bundle3.putLong(FirebaseAnalytics.Param.LEVEL, 3);
        bundle3.putLong(FirebaseAnalytics.Param.SCORE, score3);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle3);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.GROUP_ID, "3 Player Mode");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.JOIN_GROUP, bundle);

    }

    /**
     * Show ads
     */
    private void showAds(){
        AdRequest adRequest;
        if(((MainActivity)getActivity()).adsPersonalized) {
            adRequest = new AdRequest.Builder()
                    .build();
        }
        else{
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        }
        mAdView.loadAd(adRequest);
    }

    //AVVIA SET NOMI
    public void showNoticeDialog() {
        new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog)
                .setTitle(getString(R.string.guida))
                .setMessage(getString(R.string.change_name))
                .show();
    }

    //AVVIA MOSTRA DETTAGLIO PUNTEGGI PARZIALI
    public void showDettPuntParz() {
        SharedPreferences sharedPref =  getActivity().getPreferences(Context.MODE_PRIVATE);
            String html=sharedPref.getString("dpp3", "");
            if(html == ""){
                Snackbar.make(getView(), getString(R.string.errore_dpp), Snackbar.LENGTH_SHORT).show();
            }
            else {
                //SHOW BOTTOM SHEET FRAGMENT
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                // Supply data from Hand Details as an argument.
                Bundle args = new Bundle();
                args.putString("data", colors + generateHandsDetail());
                bottomSheetFragment.setArguments(args);
                bottomSheetFragment.show(getChildFragmentManager() ,bottomSheetFragment.getTag());
            }

    }

    public WebView generateWebView(){
        WebView webview = new WebView(getActivity());
        String data = generateHandsDetail();
        webview.loadDataWithBaseURL(null,colors + data, "text/html; charset=UTF-8", null, null);
        return webview;
    }

    public String generateHandsDetail(){
        SharedPreferences sharedPref =  getActivity().getPreferences(Context.MODE_PRIVATE);
        String html_inner = sharedPref.getString("dpp3", "");
        String header = "<table><tr><th>" + textNome1.getText().toString() + "</th><th>" + textNome2.getText().toString() + "</th><th>" + textNome3.getText().toString() + "</th></tr>";
        String data = header + html_inner + "</table></body></html>";
        return data;
    }

    /**
     * ON ACTIVITY RESULT
     * Sets images choosen by user and manages its crop
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_PICTURE_1) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's get data from URI
            // We cannot access this Uri directly in Android 10
            Uri photo = data.getData();
            //Later we will use this bitmap to create the File
            Bitmap bitmap = MediaStoreUtils.getBitmap(getActivity(), photo);
            //Save this bitmap to a temporary File
            File temp = new File(getActivity().getFilesDir(), "temp.jpg");
            MediaStoreUtils.convertBitmaptoFile(temp, bitmap);
            //Create the definitive file (saved inside App Dir)
            File destination = new File(getActivity().getFilesDir(), "img_m3_1.jpg");
            //Start UCrop with the temp and definitive file that will be cropped
            startActivityForResult(
                    UCrop.of(
                            Uri.fromFile(temp),
                            Uri.fromFile(destination))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(1080, 1080)
                            .getIntent(getActivity()
                            ), REQUEST_CROP_PICTURE_1);
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CROP_PICTURE_1) {
            // When we are done cropping, display it in the ImageView.
            final Uri resultUri = UCrop.getOutput(data);
            ImageView IMG1 = (ImageView) getView().findViewById(R.id.image1);
            IMG1.setImageURI(resultUri);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }


        if ((requestCode == REQUEST_PICTURE_2) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's get data from URI
            // We cannot access this Uri directly in Android 10
            Uri photo = data.getData();
            //Later we will use this bitmap to create the File
            Bitmap bitmap = MediaStoreUtils.getBitmap(getActivity(), photo);
            //Save this bitmap to a temporary File
            File temp = new File(getActivity().getFilesDir(), "temp.jpg");
            MediaStoreUtils.convertBitmaptoFile(temp, bitmap);
            //Create the definitive file (saved inside App Dir)
            File destination = new File(getActivity().getFilesDir(), "img_m3_2.jpg");
            //Start UCrop with the temp and definitive file that will be cropped
            startActivityForResult(
                    UCrop.of(
                            Uri.fromFile(temp),
                            Uri.fromFile(destination))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(1080, 1080)
                            .getIntent(getActivity()
                            ), REQUEST_CROP_PICTURE_2);
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CROP_PICTURE_2) {
            // When we are done cropping, display it in the ImageView.
            final Uri resultUri = UCrop.getOutput(data);
            ImageView IMG2 = (ImageView) getView().findViewById(R.id.image2);
            IMG2.setImageURI(resultUri);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

        if ((requestCode == REQUEST_PICTURE_3) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's get data from URI
            // We cannot access this Uri directly in Android 10
            Uri photo = data.getData();
            //Later we will use this bitmap to create the File
            Bitmap bitmap = MediaStoreUtils.getBitmap(getActivity(), photo);
            //Save this bitmap to a temporary File
            File temp = new File(getActivity().getFilesDir(), "temp.jpg");
            MediaStoreUtils.convertBitmaptoFile(temp, bitmap);
            //Create the definitive file (saved inside App Dir)
            File destination = new File(getActivity().getFilesDir(), "img_m3_3.jpg");
            //Start UCrop with the temp and definitive file that will be cropped
            startActivityForResult(
                    UCrop.of(
                            Uri.fromFile(temp),
                            Uri.fromFile(destination))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(1080, 1080)
                            .getIntent(getActivity()
                            ), REQUEST_CROP_PICTURE_3);
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CROP_PICTURE_3) {
            // When we are done cropping, display it in the ImageView.
            final Uri resultUri = UCrop.getOutput(data);
            ImageView IMG3 = (ImageView) getView().findViewById(R.id.image3);
            IMG3.setImageURI(resultUri);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

    }

    /**
     * MAKE VIBRATION
     */
    public void makeVibration(){
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            }
            else{
                vibrator.vibrate(50);
            }
        }
    }

}
