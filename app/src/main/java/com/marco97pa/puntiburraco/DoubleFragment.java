package com.marco97pa.puntiburraco;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import androidx.appcompat.app.AlertDialog;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.PopupMenu;


import android.view.LayoutInflater;
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


import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * DOUBLE FRAGMENT
 * Fragment of the 2 players mode
 * Some parts of this code was written by me at the age of 16, so it can be written better
 * It is really similar to TripleFragment and QuadFragment, except for some calculation and error checks difference
 * Specific comments will be provided near relative instruction
 *
 * @author Marco Fantauzzo
 */

public class DoubleFragment extends Fragment {
    /**
     * VARIABLE DECLARATION
     * Many of these variable are relative to user input
     * Each int variable has its respective "Widget" (editText) variable
     * Example:
     *      int bp1 = Clean run player 1 = value of BP1 EditText
     *      int bp2 = Clean run player 2 = value of BP2 EditText
     */

    private final Boolean google_has_fixed_nightmode_bug = false;
    int bp1,bp2, bi1, bi2, bs1, bs2,pn1,pn2,tot1,tot2,pm1,pm2;
    private TextView textNome1, textNome2;
    private TextView punti1, punti2;
    private EditText BP1, BP2; //Clean run EditText
    private EditText BI1, BI2; //Semi clean run EditText
    private EditText BS1, BS2; //Dirty run EditText
    private EditText PN1, PN2; //Points on table EditText
    private EditText PM1, PM2; //Points in hand EditText
    private CheckBox CH1, CH2, PZ1, PZ2; //Close and No Pots Checkbox
    private ImageView IMG1, IMG2; //Images of players
    final int PDefault=0;
    boolean win=false; //Actual state of game
    String winner,loser; //Names of winner and loser
    //Constants response to import images in app
    private static int REQUEST_PICTURE_2 = 12;
    private static int REQUEST_CROP_PICTURE_2 = 22;
    private static int REQUEST_PICTURE_1 = 11;
    private static int REQUEST_CROP_PICTURE_1 = 21;
    //Constants response to permission request (Android 6.0+)
    private final static int STORAGE_PERMISSION_PICTURE_1 = 13;
    private final static int STORAGE_PERMISSION_PICTURE_2 = 23;
    private final static int STORAGE_PERMISSION_SCREENSHOT = 30;
    //Old totals are variable used to revert point changes
    public int old_tot1;
    public int old_tot2;
    //colors for alerts
    String bgColor, txtColor, colors;
    boolean bypass = false;
    MediaPlayer sound;

    public DoubleFragment() {
        // Empty constructor required for fragment subclasses
    }

    /**
     * ONCREATEVIEW
     * Set Fragment view and linking code variables with Widgets of the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_double, container, false);

        textNome1 = (TextView) rootView.findViewById(R.id.editNome1);
        textNome2 = (TextView) rootView.findViewById(R.id.editNome2);
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
        CH1 = (CheckBox) rootView.findViewById(R.id.chiusura1);
        CH2 = (CheckBox) rootView.findViewById(R.id.chiusura2);
        PZ1 = (CheckBox) rootView.findViewById(R.id.pozzetto1);
        PZ2 = (CheckBox) rootView.findViewById(R.id.pozzetto2);

        IMG1= (ImageView) rootView.findViewById(R.id.image1);
        IMG2= (ImageView) rootView.findViewById(R.id.image2);

        sound = MediaPlayer.create(getActivity(), R.raw.fischio);

        //get Actual Theme Colors
        bgColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getActivity(), R.color.dialogBackground)));
        txtColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getActivity(), R.color.dialogText)));
        colors = "<html><body bgcolor='"+ bgColor +"' style='color: " + txtColor + "'>";

        //Invoking method to recover the state of an interrupted game
        Restore();


        //Setting OnclickListeners for each Player TextView
        textNome1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Onclick a dialog will be shown to enter the name...
                dialogGioc1();
                //...and changes are saved
                onSave();
            }
        });
        textNome2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Same as textNome1
                dialogGioc2();
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
                                textNome1.setText(getString(R.string.gioc_1));
                                onSave();
                                return true;
                            case R.id.removeImage:
                                File file1 = new File(getActivity().getFilesDir(), "img_m2_1.jpg");
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
                                textNome2.setText(getString(R.string.gioc_2));
                                onSave();
                                return true;
                            case R.id.removeImage:
                                File file2 = new File(getActivity().getFilesDir(), "img_m2_2.jpg");
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

        /**
         * RECOVER IMAGES OF LAST GAME
         * Images are showed only if user has choose it in settings
         */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Boolean isImgActivated = sharedPref.getBoolean("img", false) ;
        if(isImgActivated) {
            //BitmapFactory -> ImageDecoder per Android 9.0+ P fix
            Bitmap bitmap1 = null, bitmap2 = null;
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                try {
                    File bmp1 = new File(getActivity().getFilesDir() + "/img_m2_1.jpg");
                    ImageDecoder.Source source1 = ImageDecoder.createSource(bmp1);
                    bitmap1 = ImageDecoder.decodeBitmap(source1);
                } catch (IOException e) { e.printStackTrace(); }
                try {
                    File bmp2 = new File(getActivity().getFilesDir() + "/img_m2_2.jpg");
                    ImageDecoder.Source source2 = ImageDecoder.createSource(bmp2);
                    bitmap2 = ImageDecoder.decodeBitmap(source2);
                } catch (IOException e) { e.printStackTrace(); }
            }
            else{
                bitmap1 = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/img_m2_1.jpg");
                bitmap2 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m2_2.jpg");
            }
            //Set Bitmap to Image if not null
            if(bitmap1 != null) {
                IMG1.setImageBitmap(bitmap1);
            }
            if(bitmap2 != null) {
                IMG2.setImageBitmap(bitmap2);
            }
        }
        else{
            IMG1.setVisibility(View.GONE);
            IMG2.setVisibility(View.GONE);
        }
        //improves usability in Android N with MultiWindow and avoids bugs
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (getActivity().isInMultiWindowMode()) {
                IMG1.setVisibility(View.GONE);
                IMG2.setVisibility(View.GONE);
            }
        }


          /**
           * PUNTI DIRETTI e PUNTI IN MANO NASCOSTI
           *
           * */
            Boolean isManoModeActivated = sharedPref.getBoolean("input_puntimano", true) ;
            if(!isManoModeActivated){
                PM1.setVisibility(View.GONE);
                PM2.setVisibility(View.GONE);
                bypass = true; //PERMETTI DI CHIUDERE SENZA POZZETTO E SENZA PUNTI IN MANO
            }
            Boolean isDirectModeActivated = sharedPref.getBoolean("input_direct", false) ;
            if(isDirectModeActivated){
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
            }

        return rootView;
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
                        textNome1.setText(editText.getText());
                        onSave();
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
                        textNome2.setText(editText.getText());
                        onSave();
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
                /**
                 * SHARE SCREENSHOT OF THE SCORE
                 * It makes an instant screenshot of the whole app and send it to Android Share Intent
                 */
                //First, close the (eventually) opened keyboard
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(textNome1.getWindowToken(), 0);

                //Than make the screenshot, asking the permission in Android 6.0+ (because it will be saved to memory)
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
                //Shows an alert on how to change names
                showNoticeDialog();
                return true;

            case R.id.action_reset:
                //Starts a new game
                openReset();
                return true;

            case R.id.action_nomi:
                //Sets default names and profile image for each player
                openNomi();
                return true;

            case R.id.action_dpp:
                //Shows "Hand details" dialog
                showDettPuntParz();
                return true;

            default:
                return super.onOptionsItemSelected(item);}
    }


    /**
     * RECOVER SCORES OF LAST GAME
     * Tries to recover last score. If nothing is saved, it will return default values
     * Default values are:
     * int PDefault = 0 : each player will start its game from zero points
     * String sq1Default and String sq2Default : Default players name according to string.xml
     */
    public void Restore(){
        final String sq1Default=getString(R.string.gioc_1);
        final String sq2Default=getString(R.string.gioc_2);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        tot1 = sharedPref.getInt("p1",PDefault);
        tot2 = sharedPref.getInt("p2",PDefault);
        punti1.setText(Integer.toString(tot1));
        punti2.setText(Integer.toString(tot2));
        textNome1.setText(sharedPref.getString("sq1",sq1Default));
        textNome2.setText(sharedPref.getString("sq2",sq2Default));
    }

    public void openInsert(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.show();

    }

    /**
     * RESET DEFAULT NAMES AND IMAGES
     * This method sets default names and images for each player
     */
    public void openNomi(){
        textNome1.setText(getString(R.string.gioc_1));
        textNome2.setText(getString(R.string.gioc_2));
        onSave();
        //reset Immagini
        File file1 = new File(getActivity().getFilesDir(), "img_m2_1.jpg");
        file1.delete();
        File file2 = new File(getActivity().getFilesDir(), "img_m2_2.jpg");
        file2.delete();
        IMG1.setImageResource(R.drawable.circle_placeholder);
        IMG2.setImageResource(R.drawable.circle_placeholder);
    }

    /**
     * STARTS A NEW GAME
     * Resets the game
     */
    public void openReset(){
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
        CH1.setChecked(false);
        CH2.setChecked(false);
        PZ1.setChecked(false);
        PZ2.setChecked(false);
        tot1=0;
        tot2=0;
        punti1.setText(Integer.toString(tot1));
        punti2.setText(Integer.toString(tot2));
        win=false;
        //save all
        onSave();
        //reset dpp (hands detail string)
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dpp", "");
        editor.putInt("interrupted", 0);
        editor.commit();
        //make action bar standard again
        ((MainActivity)getActivity()).setMenuAlternative(false);
        //make a Snackbar to alert the user
        Snackbar.make(getView(), R.string.reset, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * CALCULATE POINTS
     * This method is called when touching the FAB button
     * Clean Run = 200 points
     * SemiClean Run = 150 points
     * Dirty Run = 100 points
     * Closing = 100 points
     * No Pots = -100 points
     */
    public void openStart(){
        try {
            //Checks if there are errors in user input
            boolean ciSonoErrori = checkErrors();
            //If there aren't errors, we can start
            if (ciSonoErrori == false) {

                //Getting values from user. If some fields are empty (aka ""), then they will be considered zero
                if (BP1.getText().toString().matches("")) {
                    bp1 = 0;
                } else {
                    bp1 = Integer.parseInt(BP1.getText().toString());
                }
                if (BI1.getText().toString().matches("")) {
                    bi1 = 0;
                } else {
                    bi1 = Integer.parseInt(BI1.getText().toString());
                }
                if (BS1.getText().toString().matches("")) {
                    bs1 = 0;
                } else {
                    bs1 = Integer.parseInt(BS1.getText().toString());
                }
                if (PN1.getText().toString().matches("")) {
                    pn1 = 0;
                } else {
                    pn1 = Integer.parseInt(PN1.getText().toString());
                }
                if (PM1.getText().toString().matches("")) {
                    pm1 = 0;
                } else {
                    pm1 = Integer.parseInt(PM1.getText().toString());
                }

                //Backing up old totals, so we can revert changes if user makes a mistake
                old_tot1 = tot1;
                old_tot2 = tot2;

                //Calculating new total
                tot1 = tot1 + (((bp1 * 200) + (bi1 * 100) + (bs1 * 150) + pn1) - pm1);

                //Adding Closing points
                if (CH1.isChecked()) {
                    tot1 = tot1 + 100;
                }
                //Subtract No Pots points
                if (PZ1.isChecked()) {
                    tot1 = tot1 - 100;
                }

                //Set the new score to the TextView
                punti1.setText(Integer.toString(tot1));

                /**
                 * SAME THING FOR SECOND PLAYER
                 * I don't provide comments again, just scroll :D
                 */

                if (BP2.getText().toString().matches("")) {
                    bp2 = 0;
                } else {
                    bp2 = Integer.parseInt(BP2.getText().toString());
                }
                if (BI2.getText().toString().matches("")) {
                    bi2 = 0;
                } else {
                    bi2 = Integer.parseInt(BI2.getText().toString());
                }
                if (BS2.getText().toString().matches("")) {
                    bs2 = 0;
                } else {
                    bs2 = Integer.parseInt(BS2.getText().toString());
                }
                if (PN2.getText().toString().matches("")) {
                    pn2 = 0;
                } else {
                    pn2 = Integer.parseInt(PN2.getText().toString());
                }
                if (PM2.getText().toString().matches("")) {
                    pm2 = 0;
                } else {
                    pm2 = Integer.parseInt(PM2.getText().toString());
                }

                //Calculating total
                tot2 = tot2 + (((bp2 * 200) + (bi2 * 100) + (bs2 * 150) + pn2) - pm2);

                if (CH2.isChecked()) {
                    tot2 = tot2 + 100;
                }
                if (PZ2.isChecked()) {
                    tot2 = tot2 - 100;
                }
                //Set the new score to the TextView
                punti2.setText(Integer.toString(tot2));


                //Generates the ddp string (Hands detail string).
                //It is a simple html table of the scores, written in a string
                //This will be shown when the user opens the Hand details dialog
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                String html = sharedPref.getString("dpp", "");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("dpp", html + "<tr><td>" + tot1 + "</td><td>" + tot2 + "</td></tr>");
                editor.commit();

                //reset the interface widget (polish EditText and uncheck checkboxes)
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
                CH1.setChecked(false);
                CH2.setChecked(false);
                PZ1.setChecked(false);
                PZ2.setChecked(false);

                //SnackBar to alert user about the new score
                Snackbar.make(getView(), getString(R.string.add_point), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ko), new annullaPunti())  // action text on the right side to revert changes
                        .setDuration(10000).show();


                /**
                 * CHECK IF A PLAYER HAS WIN
                 * If one of the two players overtakes 2005 points, it will be the winner
                 *
                 * int limite = closing points, when the game ends, set by the user (default: 2005)
                 * String winText = "wins"
                 * String out = String to display if someone wins. It will be "PlayerName wins"
                 * String points = Another string to display if someone wins. It will contain the final score
                 */
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                int limite = Integer.parseInt(sharedPreferences.getString("limite", "2005"));
                String winText = getString(R.string.win);
                String points = "";
                if (tot1 > tot2) {
                    if (tot1 >= limite) {
                        win = true;
                        winner = textNome1.getText().toString();
                        loser = textNome2.getText().toString();
                        //save score to DB
                        saveScoreToDB(textNome1.getText().toString(), textNome2.getText().toString(), tot1, tot2);
                        //make alert
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
                        String out = textNome1.getText().toString();
                        out = out + " ";
                        out = out.concat(winText);
                        builder.setTitle(out);
                        points = punti1.getText().toString();
                        points = points.concat(" - ");
                        points = points.concat(punti2.getText().toString());
                        //Play sound
                        Boolean soundActive = sharedPreferences.getBoolean("sound", true);
                        if (soundActive) {
                            sound.start();
                        }
                        //If player1 wins, a dialog will be displayed
                        WebView webview = generateWebView();
                        builder.setView(webview);
                        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    if (tot2 >= limite) {
                        win = true;
                        winner = textNome2.getText().toString();
                        loser = textNome1.getText().toString();
                        //save score to DB
                        saveScoreToDB(textNome1.getText().toString(), textNome2.getText().toString(), tot1, tot2);
                        //make alert
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog);
                        String out = textNome2.getText().toString();
                        out = out + " ";
                        out = out.concat(winText);
                        builder.setTitle(out);
                        points = punti1.getText().toString();
                        points = points.concat(" - ");
                        points = points.concat(punti2.getText().toString());
                        //Play sound
                        Boolean soundActive = sharedPreferences.getBoolean("sound", true);
                        if (soundActive) {
                            sound.start();
                        }
                        //If player1 wins, a dialog will be displayed
                        WebView webview = generateWebView();
                        builder.setView(webview);
                        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                //save the new score
                onSave();
            }
        }
        catch (NullPointerException e){
            Snackbar.make(getView(),getString(R.string.error00), BaseTransientBottomBar.LENGTH_LONG).show();
        }
        catch (NumberFormatException e){
            Snackbar.make(getView(),getString(R.string.error04), BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }

    public class annullaPunti implements View.OnClickListener{

        public void onClick(View v) {
            // Code to undo the user's last action
            tot1=old_tot1;
            tot2=old_tot2;
            punti1.setText(Integer.toString(tot1));
            punti2.setText(Integer.toString(tot2));
            onSave();
        }
    }

    /**
     * AUTOMATIC SAVES
     * This method saves the scores. It is called every time the score  or players name are modified
     * If user has won, default values will be saved, to start a new game.
     */
    public void onSave(){
        final String sq1Default=getString(R.string.gioc_1);
        final String sq2Default=getString(R.string.gioc_2);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(win == false){
            editor.putInt("p1", tot1);
            editor.putInt("p2", tot2);
            editor.putString("sq1",textNome1.getText().toString());
            editor.putString("sq2",textNome2.getText().toString());
            editor.putInt("interrupted", 2);
            editor.commit();
        }
        else{
            editor.putInt("p1", PDefault);
            editor.putInt("p2", PDefault);
            editor.putString("sq1", sq1Default);
            editor.putString("sq2",sq2Default);
            editor.putInt("interrupted", 0);
            editor.putString("dpp", "");
            //set alternative bar
            ((MainActivity)getActivity()).setMenuAlternative(true);
            editor.commit();
        }
    }

    //ASK PERMISSION Android 6.0+ (Marshmallow)
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //OPEN THE SHARE SCREEN
    public void openScreen(){
        Intent myIntent = new Intent(getActivity(), ShareResultActivity.class);
        myIntent.putExtra("name1", textNome1.getText().toString());
        myIntent.putExtra("name2", textNome2.getText().toString());
        myIntent.putExtra("score1", tot1);
        myIntent.putExtra("score2", tot2);
        this.startActivity(myIntent);
    }



    /**
     * CHECK ERRORS
     * Users sometimes make mistakes. The app will alert them
     * It is invoked before calculating totals
     * @return boolean: false if no errors
     */
    public boolean checkErrors(){
        int check=1;
        boolean res = false;
        if(PM1.getText().toString().matches("")){
            check=0;
        }
        else{
            check = Integer.parseInt(PM1.getText().toString());
        }
        if(CH1.isChecked()){
            if(check!=0){
                //BIG FIX
                Snackbar.make(getView(), R.string.error02, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
        }
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
        if(CH2.isChecked()){
            if(check!=0){
                //BIG FIX
                Snackbar.make(getView(), R.string.error02, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
        }
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
        return res;
    }

    /**
     * SAVE MATCH TO DATABASE
     * If one of the players wins, the score of the match will be saved in History ScoreDB
     */
    public void saveScoreToDB(String player1,String player2,int point1,int point2) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
        String date = dateformat.format(c.getTime());

        ScoreDB db = new ScoreDB(getActivity());
        db.open();
        long id = db.insertScore(player1, player2, point1, point2, date, generateHandsDetail());
        db.close();
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

    /**
     * ALERT HOW-TO CHANGE NAMES
     * A little alert to help user change players names
     */
    public void showNoticeDialog() {
        new MaterialAlertDialogBuilder(getActivity(), R.style.AppTheme_Dialog)
                .setTitle(getString(R.string.guida))
                .setMessage(getString(R.string.change_name))
                .show();
    }

    /**
     * DIALOG SHOW HAND'S DETAILS
     * It gets the ddp string from SharedPreferences and renders it as HTML code.
     * It is a simple table of the scores of each hand
     */
    public void showDettPuntParz() {
        SharedPreferences sharedPref =  getActivity().getPreferences(Context.MODE_PRIVATE);
            String html=sharedPref.getString("dpp", "");
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
        webview.loadDataWithBaseURL(null, colors + data, "text/html; charset=UTF-8", null, null);
        return webview;
    }

    public String generateHandsDetail(){
        SharedPreferences sharedPref =  getActivity().getPreferences(Context.MODE_PRIVATE);
        String html_inner =sharedPref.getString("dpp", "");
        String header = "<table><tr><th>" + textNome1.getText().toString() + "</th><th>" + textNome2.getText().toString() + "</th></tr>";
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
            // When the user is done picking a picture, let's start the CropImage Activity,
            Uri photo = data.getData();

            File file = new File(getActivity().getFilesDir(), "img_m2_1.jpg");
            Uri result = Uri.fromFile(file);
            startActivityForResult(
                    UCrop.of(photo, result)
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
            // When the user is done picking a picture, let's start the CropImage Activity,
            Uri photo = data.getData();

            File file = new File(getActivity().getFilesDir(), "img_m2_2.jpg");
            Uri result = Uri.fromFile(file);
            startActivityForResult(
                    UCrop.of(photo, result)
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

    }

    /**
     * SAVE IMAGE
     * Saves profile images in application data path.
     * It is invoked when setting a new profile image
     */
    public void saveImage(File file, File croppedImageFile){
        try {
            FileInputStream inStream = new FileInputStream(croppedImageFile);
            FileOutputStream outStream = new FileOutputStream(file);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
