package com.marco97pa.puntiburraco;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
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
 * QUAD FRAGMENT
 * Fragment of the 4 players mode
 * Some parts of this code has been written by me at the age of 16, so it can be written better
 * It is really similar to DoubleFragment and TripleFragment, except for some calculation and error checks difference
 * So I will not provide comments of this Fragment
 *
 * @author Marco Fantauzzo
 */

public class QuadFragment extends Fragment {
    int bp1,bp2, bi1, bi2, bs1, bs2,pn1,pn2,tot1,tot2,pm1,pm2;
    private TextView textNome1, textNome2;
    private TextView punti1, punti2;
    private EditText BP1, BP2;
    private EditText BI1, BI2;
    private EditText BS1, BS2;
    private EditText PN1, PN2;
    private EditText PM1, PM2;
    private CheckBox CH1, CH2, PZ1, PZ2;
    private ImageView IMG1, IMG2;
    final int PDefault=0;
    boolean win=false;
    String winner,loser;
    private static int REQUEST_PICTURE_2 = 12;
    private static int REQUEST_CROP_PICTURE_2 = 22;
    private static int REQUEST_PICTURE_1 = 11;
    private static int REQUEST_CROP_PICTURE_1 = 21;
    private final static int STORAGE_PERMISSION_PICTURE_1 = 13;
    private final static int STORAGE_PERMISSION_PICTURE_2 = 23;
    private final static int STORAGE_PERMISSION_SCREENSHOT = 30;

    public int old_tot1;
    public int old_tot2;

    public QuadFragment() {
        // Empty constructor required for fragment subclasses
    }
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
        IMG1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // First, request permission
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
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

            //SETUP IMMAGINI - Recupera immagini dalla memoria interna solo se attivate
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            Boolean isImgActivated = sharedPref.getBoolean("img", false) ;
            if(isImgActivated) {
                Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m4_1.jpg");
                if(bitmap1 != null) {
                    IMG1.setImageBitmap(bitmap1);
                }
                Bitmap bitmap2 = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/img_m4_2.jpg");
                if(bitmap2 != null) {
                    IMG2.setImageBitmap(bitmap2);
                }
            }
            else{
                IMG1.setVisibility(View.GONE);
                IMG2.setVisibility(View.GONE);
            }

            //ADMOB
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
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

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
            default:
                return super.onOptionsItemSelected(item);}
    }

    protected void dialogGioc1() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_squadra, null);
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
        View promptView = layoutInflater.inflate(R.layout.input_squadra, null);
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

    //Ripristino Save
    public void Restore(){
        final String sq1Default=getString(R.string.n1);
        final String sq2Default=getString(R.string.n2);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        tot1 = sharedPref.getInt("punti1",PDefault);
        tot2 = sharedPref.getInt("punti2",PDefault);
        punti1.setText(Integer.toString(tot1));
        punti2.setText(Integer.toString(tot2));
        textNome1.setText(sharedPref.getString("squadra1",sq1Default));
        textNome2.setText(sharedPref.getString("squadra2",sq2Default));
    }


    //RIPRISTINO NOMI
    public void openNomi(){
        textNome1.setText(getString(R.string.n1));
        textNome2.setText(getString(R.string.n2));
        onSave();
        //reset Immagini
        File file1 = new File(getActivity().getFilesDir(), "img_m4_1.jpg");
        file1.delete();
        File file2 = new File(getActivity().getFilesDir(), "img_m4_2.jpg");
        file2.delete();
        IMG1.setImageResource(R.drawable.ic_menu_gallery);
        IMG2.setImageResource(R.drawable.ic_menu_gallery);
    }
    //AVVIA RESET
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
        //salva tutto
        onSave();
        //reset dpp
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dpp4", "");
        editor.putInt("interrupted", 0);
        editor.commit();
        //reset Immagini
        File file1 = new File(getActivity().getFilesDir(), "img_m4_1.jpg");
        file1.delete();
        File file2 = new File(getActivity().getFilesDir(), "img_m4_2.jpg");
        file2.delete();
        IMG1.setImageResource(R.drawable.ic_menu_gallery);
        IMG2.setImageResource(R.drawable.ic_menu_gallery);
        Snackbar.make(getView(), R.string.reset, Snackbar.LENGTH_SHORT).show();
    }
    //AVVIA CALCOLO PUNTI
    public void openStart(){
        boolean ciSonoErrori=checkErrors();
        if(ciSonoErrori==false){
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
            old_tot1 = tot1;
            old_tot2 = tot2;

            tot1=tot1+( ((bp1*200)+(bi1*100)+(bs1*150)+pn1)-pm1);
            if (CH1.isChecked()) {
                tot1=tot1+100;
            }
            if (PZ1.isChecked()) {
                tot1=tot1-100;
            }
            punti1.setText(Integer.toString(tot1));
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
            tot2=tot2+( ((bp2*200)+(bi2*100)+(bs2*150)+pn2)-pm2);
            if (CH2.isChecked()) {
                tot2=tot2+100;
            }
            if (PZ2.isChecked()) {
                tot2=tot2-100;
            }
            punti2.setText(Integer.toString(tot2));
            //genera il dpp
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String html=sharedPref.getString("dpp4", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("dpp4",html+"<tr><td>"+tot1+"</td><td>"+tot2+"</td></tr>");
            editor.commit();
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
            CH1.setChecked(false);
            CH2.setChecked(false);
            PZ1.setChecked(false);
            PZ2.setChecked(false);
            //SnackBar con annulla
            Snackbar.make(getView(), getString(R.string.add_point), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ko), new annullaPunti())  // action text on the right side
                    .setDuration(10000).show();
            //controllo vincita
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            int limite = Integer.parseInt(sharedPreferences.getString("limite", "2005"));
            String winText = getString(R.string.win);
            String points="";
            if(tot1>tot2){
                if(tot1>=limite){
                    win=true;
                    winner=textNome1.getText().toString();
                    loser=textNome2.getText().toString();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    String out=textNome1.getText().toString();
                    out = out +" ";
                    out=out.concat(winText);
                    builder.setTitle(out);
                    points=punti1.getText().toString();
                    points=points.concat(" - ");
                    points=points.concat(punti2.getText().toString());
                    builder.setMessage(points);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
            else{
                if(tot2>=limite){
                    win=true;
                    winner=textNome2.getText().toString();
                    loser=textNome1.getText().toString();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    String out=textNome2.getText().toString();
                    out = out +" ";
                    out=out.concat(winText);
                    builder.setTitle(out);
                    points=punti1.getText().toString();
                    points=points.concat(" - ");
                    points=points.concat(punti2.getText().toString());
                    builder.setMessage(points);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
            //salva
            onSave();
            //salvataggiSeVince

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

    //SALVATAGGIO AUTOMATICO
    public void onSave(){
        final String sq1Default=getString(R.string.n1);
        final String sq2Default=getString(R.string.n2);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(win==false){
            editor.putInt("punti1", tot1);
            editor.putInt("punti2", tot2);
            editor.putString("squadra1",textNome1.getText().toString());
            editor.putString("squadra2",textNome2.getText().toString());
            editor.putInt("interrupted", 4);
            editor.commit();
        }
        else{
            editor.putInt("punti1", PDefault);
            editor.putInt("punti2", PDefault);
            editor.putString("squadra1", sq1Default);
            editor.putString("squadra2",sq2Default);
            editor.putInt("interrupted", 0);
            //archiviaPartita
                showDettPuntParz();
            editor.putString("dpp", "");
            editor.commit();
        }
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


    //AVVIA SCREENSHOT + SHARE
    public void openScreen(){
        View v = getActivity().getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        String nomeFoto="SCREEN"+ System.currentTimeMillis() + ".png";
        File imageFileToShare;
        try {
            FileOutputStream fos = new FileOutputStream(imageFileToShare = new File(Environment.getExternalStorageDirectory().toString(), nomeFoto));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();


            Intent share = new Intent(Intent.ACTION_SEND);

            // If you want to share a png image only, you can do:
            // setType("image/png"); OR for jpeg: setType("image/jpeg");
            share.setType("image/png");

            // Make sure you put example png image named myImage.png in your
            // directory
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

    //CONTROLLI ERRORI
    public boolean checkErrors(){
        int check=1;
        boolean res=false;
        if(PM1.getText().toString().matches("")){check=0;}
        else{check = Integer.parseInt(PM1.getText().toString());}
        if(CH1.isChecked()){
            if(check!=0){
                //BIG BUG
                //Toast.makeText(getActivity(), R.string.error02, Toast.LENGTH_SHORT).show();
                //res=true;
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
                //BIG BUG
                //Toast.makeText(getActivity(), R.string.error02, Toast.LENGTH_SHORT).show();
                //res=true;
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
    //AVVIA SET NOMI
    public void showNoticeDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.guida))
                .setMessage(getString(R.string.change_name))
                .show();
    }

    //AVVIA MOSTRA DETTAGLIO PUNTEGGI PARZIALI
    public void showDettPuntParz() {
        SharedPreferences sharedPref =  getActivity().getPreferences(Context.MODE_PRIVATE);
            String html=sharedPref.getString("dpp4", "");
            if(html == ""){
                Snackbar.make(getView(), getString(R.string.errore_dpp), Snackbar.LENGTH_SHORT).show();
            }
            else {
                WebView webview = new WebView(getActivity());
                String header = "<html><body bgcolor=\"#FFFFFF\"><table><tr><th>" + textNome1.getText().toString() + "</th><th>" + textNome2.getText().toString() + "</th></tr>";
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

            File file = new File(getActivity().getFilesDir(), "img_m4_1.jpg");
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

            File file = new File(getActivity().getFilesDir(), "img_m4_2.jpg");
            saveImage(file, croppedImageFile);
        }
    }

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
