package com.marco97pa.puntiburraco;

import android.app.AlertDialog;
import android.app.Fragment;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static android.app.Activity.RESULT_OK;

/**
 * DOUBLE FRAGMENT
 * Fragment of the 2 players mode
 * Some parts of this code has been written by me at the age of 16, so it can be written better
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

    public DoubleFragment() {
        // Empty constructor required for fragment subclasses
    }

    /**
     * ONCREATEVIEW
     * Set Fragment view and linking code variables with Widgets of the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        IMG2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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

        /**
         * RECOVER IMAGES OF LAST GAME
         * Images are showed only if user has choose it in settings
         */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Boolean isImgActivated = sharedPref.getBoolean("img", false) ;
        if(isImgActivated) {
            Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m2_1.jpg");
            if(bitmap1 != null) {
                IMG1.setImageBitmap(bitmap1);
            }
            Bitmap bitmap2 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m2_2.jpg");
            if(bitmap2 != null) {
                IMG2.setImageBitmap(bitmap2);
            }
        }
        else{
            IMG1.setVisibility(View.GONE);
            IMG2.setVisibility(View.GONE);
        }

        /**
         * ADMOB
         * Ads are showed only if user has choose it in settings
         */
            Boolean isAdActivated = sharedPref.getBoolean("pub", true) ;
            AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
            if(isAdActivated) {
                MobileAds.initialize(getActivity().getApplicationContext(), "ca-app-pub-9375533114553467~6724882232");
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
            else {
                mAdView.setVisibility(View.GONE);
            }

        return rootView;
    }

    protected void dialogGioc1() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_gioc, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

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
        final String sq1Default=getString(R.string.s1);
        final String sq2Default=getString(R.string.s2);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        tot1 = sharedPref.getInt("p1",PDefault);
        tot2 = sharedPref.getInt("p2",PDefault);
        punti1.setText(Integer.toString(tot1));
        punti2.setText(Integer.toString(tot2));
        textNome1.setText(sharedPref.getString("sq1",sq1Default));
        textNome2.setText(sharedPref.getString("sq2",sq2Default));
    }

    /**
     * RESET DEFAULT NAMES AND IMAGES
     * This method sets default names and images for each player
     */
    public void openNomi(){
        textNome1.setText(getString(R.string.s1));
        textNome2.setText(getString(R.string.s2));
        onSave();
        //reset Immagini
        File file1 = new File(getActivity().getFilesDir(), "img_m2_1.jpg");
        file1.delete();
        File file2 = new File(getActivity().getFilesDir(), "img_m2_2.jpg");
        file2.delete();
        IMG1.setImageResource(R.drawable.ic_menu_gallery);
        IMG2.setImageResource(R.drawable.ic_menu_gallery);
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
        //reset dpp (hands detail string)
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dpp", "");
        editor.commit();
        //save all
        onSave();
        //reset images
        File file1 = new File(getActivity().getFilesDir(), "img_m2_1.jpg");
        file1.delete();
        File file2 = new File(getActivity().getFilesDir(), "img_m2_2.jpg");
        file2.delete();
        IMG1.setImageResource(R.drawable.ic_menu_gallery);
        IMG2.setImageResource(R.drawable.ic_menu_gallery);
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
        //Checks if there are errors in user input
        boolean ciSonoErrori = checkErrors();
        //If there aren't errors, we can start
        if(ciSonoErrori == false){

            //Getting values from user. If some fields are empty (aka ""), then they will be considered zero
            if(BP1.getText().toString().matches("")){
                bp1=0;
            }
            else{
                bp1 = Integer.parseInt(BP1.getText().toString());
            }
            if(BI1.getText().toString().matches("")){
                bi1=0;
            }
            else{
                bi1 = Integer.parseInt(BI1.getText().toString());
            }
            if(BS1.getText().toString().matches("")){
                bs1=0;
            }
            else{
                bs1 = Integer.parseInt(BS1.getText().toString());
            }
            if(PN1.getText().toString().matches("")){
                pn1=0;
            }
            else{
                pn1 = Integer.parseInt(PN1.getText().toString());
            }
            if(PM1.getText().toString().matches("")){
                pm1=0;
            }
            else{
                pm1 = Integer.parseInt(PM1.getText().toString());
            }

            //Backing up old totals, so we can revert changes if user makes a mistake
            old_tot1 = tot1;
            old_tot2 = tot2;

            //Calculating new total
            tot1=tot1+( ((bp1*200)+(bi1*100)+(bs1*150)+pn1)-pm1);

            //Adding Closing points
            if (CH1.isChecked()) {
                tot1=tot1+100;
            }
            //Subtract No Pots points
            if (PZ1.isChecked()) {
                tot1=tot1-100;
            }

            //Set the new score to the TextView
            punti1.setText(Integer.toString(tot1));

            /**
             * SAME THING FOR SECOND PLAYER
             * I don't provide comments again, just scroll :D
             */

            if(BP2.getText().toString().matches("")){
                bp2=0;
            }
            else{
                bp2 = Integer.parseInt(BP2.getText().toString());
            }
            if(BI2.getText().toString().matches("")){
                bi2=0;
            }
            else{
                bi2 = Integer.parseInt(BI2.getText().toString());
            }
            if(BS2.getText().toString().matches("")){
                bs2=0;
            }
            else{
                bs2 = Integer.parseInt(BS2.getText().toString());
            }
            if(PN2.getText().toString().matches("")){
                pn2=0;
            }
            else{
                pn2 = Integer.parseInt(PN2.getText().toString());
            }
            if(PM2.getText().toString().matches("")){
                pm2=0;
            }
            else{
                pm2 = Integer.parseInt(PM2.getText().toString());
            }

            //Calculating total
            tot2=tot2+( ((bp2*200)+(bi2*100)+(bs2*150)+pn2)-pm2);

            if (CH2.isChecked()) {
                tot2=tot2+100;
            }
            if (PZ2.isChecked()) {
                tot2=tot2-100;
            }
            //Set the new score to the TextView
            punti2.setText(Integer.toString(tot2));


            //Generates the ddp string (Hands detail string).
            //It is a simple html table of the scores, written in a string
            //This will be shown when the user opens the Hand details dialog
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String html=sharedPref.getString("dpp", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("dpp",html+"<tr><td>"+tot1+"</td><td>"+tot2+"</td></tr>");
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
            String points="";
            if(tot1 > tot2){
                if(tot1 >= limite){
                    win = true;
                    winner = textNome1.getText().toString();
                    loser = textNome2.getText().toString();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    String out = textNome1.getText().toString();
                    out = out +" ";
                    out = out.concat(winText);
                    builder.setTitle(out);
                    points=punti1.getText().toString();
                    points=points.concat(" - ");
                    points=points.concat(punti2.getText().toString());
                    //If player1 wins, a dialog will be displayed
                    builder.setMessage(points);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
            else{
                if(tot2>=limite){
                    win = true;
                    winner = textNome2.getText().toString();
                    loser = textNome1.getText().toString();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    String out=textNome2.getText().toString();
                    out = out +" ";
                    out=out.concat(winText);
                    builder.setTitle(out);
                    points=punti1.getText().toString();
                    points=points.concat(" - ");
                    points=points.concat(punti2.getText().toString());
                    //If player2 wins, a dialog will be displayed
                    builder.setMessage(points);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
            //save the new score
            onSave();

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
        final String sq1Default=getString(R.string.s1);
        final String sq2Default=getString(R.string.s2);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(win == false){
            editor.putInt("p1", tot1);
            editor.putInt("p2", tot2);
            editor.putString("sq1",textNome1.getText().toString());
            editor.putString("sq2",textNome2.getText().toString());
            editor.commit();
        }
        else{
            editor.putInt("p1", PDefault);
            editor.putInt("p2", PDefault);
            editor.putString("sq1", sq1Default);
            editor.putString("sq2",sq2Default);
            //Show "Hands Details, then delete the string"
                showDettPuntParz();
            editor.putString("dpp", "");
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

    /**
     * TAKE SCREENSHOT, then SHARE
     * This method shares the scores.
     * A screenshot of the app will be taken, saved in memory, then invoked the Android Share Intent
     */
    public void openScreen(){
        View v = getActivity().getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        String nomeFoto = "SCREEN"+ System.currentTimeMillis() + ".png";
        File imageFileToShare;
        try {
            // Save as png
            FileOutputStream fos = new FileOutputStream(imageFileToShare = new File(Environment.getExternalStorageDirectory().toString(), nomeFoto));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            //Share
            Intent share = new Intent(Intent.ACTION_SEND);

            share.setType("image/png");

            //I have put png image named myImage.png in my app directory
            //String imagePath = Environment.getExternalStorageDirectory()+nomeFoto;
            //File imageFileToShare = new File(imagePath);

            Uri uri = Uri.fromFile(imageFileToShare);
            share.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(share, getString(R.string.action_share)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            if(check==0){
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
            if(check==0){
                Snackbar.make(getView(), R.string.error03, Snackbar.LENGTH_SHORT).show();
                res=true;
            }
        }
        return res;
    }

    /**
     * ALERT HOW-TO CHANGE NAMES
     * A little alert to help user change players names
     */
    public void showNoticeDialog() {
        new AlertDialog.Builder(getActivity())
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
                WebView webview = new WebView(getActivity());
                String header = "<html><body bgcolor=\"#EEEEEE\"><table><tr><th>" + textNome1.getText().toString() + "</th><th>" + textNome2.getText().toString() + "</th></tr>";
                String data = header + html + "</table></body></html>";
                webview.loadData(data, "text/html; charset=UTF-8", null);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle(R.string.intro_pro_dpp_t);
                ad.setView(webview);
                ad.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = ad.create();
                dialog.show();
            }

    }


    /**
     * ON ACTIVITY RESULT
     * Sets images choosen by user and manages its crop
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File croppedImageFile = new File(getActivity().getFilesDir(), "test.jpg");

        if ((requestCode == REQUEST_PICTURE_1) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.
            Uri croppedImage = Uri.fromFile(croppedImageFile);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
            cropImage.setOutlineColor(0xFF03A9F4);
            cropImage.setSourceImage(data.getData());

            startActivityForResult(cropImage.getIntent(getActivity()), REQUEST_CROP_PICTURE_1);
        } else if ((requestCode == REQUEST_CROP_PICTURE_1) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            ImageView IMG1 = (ImageView) getView().findViewById(R.id.image1);
            IMG1.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));

            File file = new File(getActivity().getFilesDir(), "img_m2_1.jpg");
            saveImage(file, croppedImageFile);
        }


        if ((requestCode == REQUEST_PICTURE_2) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.
            Uri croppedImage = Uri.fromFile(croppedImageFile);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
            cropImage.setOutlineColor(0xFF03A9F4);
            cropImage.setSourceImage(data.getData());

            startActivityForResult(cropImage.getIntent(getActivity()), REQUEST_CROP_PICTURE_2);
        } else if ((requestCode == REQUEST_CROP_PICTURE_2) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            ImageView IMG2 = (ImageView) getView().findViewById(R.id.image2);
            IMG2.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));

            File file = new File(getActivity().getFilesDir(), "img_m2_2.jpg");
            saveImage(file, croppedImageFile);
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
