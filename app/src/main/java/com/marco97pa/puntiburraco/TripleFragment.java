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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TRIPLE FRAGMENT
 * Fragment of the 3 players mode
 * Some parts of this code has been written by me at the age of 16, so it can be written better
 * It is really similar to DoubleFragment and QuadFragment, except for some calculation and error checks difference
 * So I will not provide comments of this Fragment
 *
 * @author Marco Fantauzzo
 */

public class TripleFragment extends Fragment {
    int bp1,bp2,bp3, bi1, bi2,bi3, bs1, bs2,bs3,pn1,pn2,pn3,tot1,tot2,tot3,pm1,pm2,pm3;
    int tot=0;
    private TextView textNome1, textNome2, textNome3;
    private TextView punti1, punti2, punti3;
    private EditText BP1, BP2, BP3;
    private EditText BI1, BI2, BI3;
    private EditText BS1, BS2, BS3;
    private EditText PN1, PN2, PN3;
    private EditText PM1, PM2, PM3;
    private CheckBox CH1, CH2, CH3;
    private CheckBox RB1, RB2, RB3;
    private CheckBox PZ1, PZ2, PZ3;
    boolean check1=false, check2=false, check3=false;
    int SET;
    final int PDefault=0;
    boolean win=false;
    String winner,loser1,loser2;
    private final static int STORAGE_PERMISSION_SCREENSHOT = 30;

    public int old_tot1;
    public int old_tot2;
    public int old_tot3;

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
                    if(BP3.getVisibility()==View.VISIBLE){
                        SET=View.INVISIBLE;
                    }
                    else{
                        SET=View.VISIBLE;
                    }
                    BP3.setVisibility(SET);
                    BI3.setVisibility(SET);
                    PN3.setVisibility(SET);
                    PM3.setVisibility(SET);
                    CH3.setVisibility(SET);
                    PZ3.setVisibility(SET);
                    BS3.setVisibility(SET);
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
                    if(BP1.getVisibility()==View.VISIBLE){
                        SET=View.INVISIBLE;
                    }
                    else{
                        SET=View.VISIBLE;
                    }
                    BP1.setVisibility(SET);
                    BI1.setVisibility(SET);
                    PN1.setVisibility(SET);
                    PM1.setVisibility(SET);
                    CH1.setVisibility(SET);
                    PZ1.setVisibility(SET);
                    BS1.setVisibility(SET);
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
                    if(BP2.getVisibility()==View.VISIBLE){
                        SET=View.INVISIBLE;
                    }
                    else{
                        SET=View.VISIBLE;
                    }
                    BP2.setVisibility(SET);
                    BI2.setVisibility(SET);
                    PN2.setVisibility(SET);
                    PM2.setVisibility(SET);
                    CH2.setVisibility(SET);
                    PZ2.setVisibility(SET);
                    BS2.setVisibility(SET);
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
    protected void dialogGioc3() {

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
                        textNome3.setText(editText.getText());
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

    //RIPRISTINO NOMI
    public void openNomi(){
        textNome1.setText(getString(R.string.g1));
        textNome2.setText(getString(R.string.g2));
        textNome3.setText(getString(R.string.g3));
        onSave();
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
        BP3.setVisibility(View.VISIBLE);
        BI3.setVisibility(View.VISIBLE);
        BS3.setVisibility(View.VISIBLE);
        PN3.setVisibility(View.VISIBLE);
        PM3.setVisibility(View.VISIBLE);
        CH3.setVisibility(View.VISIBLE);
        BP1.setVisibility(View.VISIBLE);
        BI1.setVisibility(View.VISIBLE);
        BS1.setVisibility(View.VISIBLE);
        PN1.setVisibility(View.VISIBLE);
        PM1.setVisibility(View.VISIBLE);
        CH1.setVisibility(View.VISIBLE);
        BP2.setVisibility(View.VISIBLE);
        BI2.setVisibility(View.VISIBLE);
        BS2.setVisibility(View.VISIBLE);
        PN2.setVisibility(View.VISIBLE);
        PM2.setVisibility(View.VISIBLE);
        CH2.setVisibility(View.VISIBLE);
        PZ1.setVisibility(View.VISIBLE);
        PZ2.setVisibility(View.VISIBLE);
        PZ3.setVisibility(View.VISIBLE);
        win=false;
        //salva tutto
        onSave();
        //reset dpp
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dpp3", "");
        editor.putInt("interrupted", 0);
        editor.commit();
        Snackbar.make(getView(), R.string.reset, Snackbar.LENGTH_SHORT).show();
    }



    //AVVIA CALCOLO PUNTI
    public void openStart(){
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
            }

            old_tot1 = tot1;
            old_tot2 = tot2;
            old_tot3 = tot3;

            if(RB1.isChecked()){
                tot1=tot1+( ((bp1*200)+(bi1*100)+(bs1*150)+pn1)-pm1);
                if (CH1.isChecked()) {
                    tot1=tot1+100;
                }
                if (PZ1.isChecked()) {
                    tot1=tot1-100;
                }
                punti1.setText(Integer.toString(tot1));

                tot=( ((bp2*200)+(bi2*100)+(bs2*150)+pn2)-pm2);
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
                tot2=tot2+( ((bp2*200)+(bi2*100)+(bs2*150)+pn2)-pm2);
                if (CH2.isChecked()) {
                    tot2=tot2+100;
                }
                if (PZ2.isChecked()) {
                    tot2=tot2-100;
                }
                punti2.setText(Integer.toString(tot2));

                tot=( ((bp3*200)+(bi3*100)+(bs3*150)+pn3)-pm3);
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
                tot3=tot3+( ((bp3*200)+(bi3*100)+(bs3*150)+pn3)-pm3);
                if (CH3.isChecked()) {
                    tot3=tot3+100;
                }
                if (PZ3.isChecked()) {
                    tot3=tot3-100;
                }
                punti3.setText(Integer.toString(tot3));

                tot=( ((bp1*200)+(bi1*100)+(bs1*150)+pn1)-pm1);
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
                tot1=tot1+( ((bp1*200)+(bi1*100)+(bs1*150)+pn1)-pm1);
                if (CH1.isChecked()) {
                    tot1=tot1+100;
                }
                if (PZ1.isChecked()) {
                    tot1=tot1-100;
                }
                tot2=tot2+( ((bp2*200)+(bi2*100)+(bs2*150)+pn2)-pm2);
                if (CH2.isChecked()) {
                    tot2=tot2+100;
                }
                if (PZ2.isChecked()) {
                    tot2=tot2-100;
                }
                tot3=tot3+( ((bp3*200)+(bi3*100)+(bs3*150)+pn3)-pm3);
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
            BP3.setText("");
            BI3.setText("");
            BS3.setText("");
            PN3.setText("");
            PM3.setText("");
            CH1.setChecked(false);
            CH2.setChecked(false);
            CH3.setChecked(false);
            PZ1.setChecked(false);
            PZ2.setChecked(false);
            PZ3.setChecked(false);
            RB1.setChecked(false);
            RB2.setChecked(false);
            RB3.setChecked(false);
            BP3.setVisibility(View.VISIBLE);
            BI3.setVisibility(View.VISIBLE);
            PN3.setVisibility(View.VISIBLE);
            PM3.setVisibility(View.VISIBLE);
            BS3.setVisibility(View.VISIBLE);
            CH3.setVisibility(View.VISIBLE);
            BP1.setVisibility(View.VISIBLE);
            BI1.setVisibility(View.VISIBLE);
            PN1.setVisibility(View.VISIBLE);
            PM1.setVisibility(View.VISIBLE);
            CH1.setVisibility(View.VISIBLE);
            BS1.setVisibility(View.VISIBLE);
            BP2.setVisibility(View.VISIBLE);
            BI2.setVisibility(View.VISIBLE);
            PN2.setVisibility(View.VISIBLE);
            PM2.setVisibility(View.VISIBLE);
            CH2.setVisibility(View.VISIBLE);
            BS2.setVisibility(View.VISIBLE);
            PZ1.setVisibility(View.VISIBLE);
            PZ2.setVisibility(View.VISIBLE);
            PZ3.setVisibility(View.VISIBLE);
            //SnackBar con annulla
            Snackbar.make(getView(), getString(R.string.add_point), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ko), new annullaPunti())  // action text on the right side
                    .setDuration(10000).show();
            //vincita
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            int limite = Integer.parseInt(sharedPreferences.getString("limite", "2005"));;
            String winText = getString(R.string.win);
            String points="";
            if(tot1>=limite){
                win=true;
                winner=textNome1.getText().toString();
                loser1=textNome2.getText().toString();
                loser2=textNome3.getText().toString();
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

                String out=textNome1.getText().toString();
                out = out +" ";
                out=out.concat(winText);
                builder.setTitle(out);
                points=punti1.getText().toString();
                points=points.concat(" - ");
                points=points.concat(punti2.getText().toString());
                points=points.concat(" - ");
                points=points.concat(punti3.getText().toString());
                builder.setMessage(points);
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            if(tot2>=limite){
                winner=textNome2.getText().toString();
                loser1=textNome1.getText().toString();
                loser2=textNome3.getText().toString();
                win=true;
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                String out=textNome2.getText().toString();
                out = out +" ";
                out=out.concat(winText);
                builder.setTitle(out);
                points=punti1.getText().toString();
                points=points.concat(" - ");
                points=points.concat(punti2.getText().toString());
                points=points.concat(" - ");
                points=points.concat(punti3.getText().toString());
                builder.setMessage(points);
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            if(tot3>=limite){
                winner=textNome3.getText().toString();
                loser1=textNome2.getText().toString();
                loser2=textNome1.getText().toString();
                win=true;
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                String out=textNome3.getText().toString();
                out = out +" ";
                out=out.concat(winText);
                builder.setTitle(out);
                points=punti1.getText().toString();
                points=points.concat(" - ");
                points=points.concat(punti2.getText().toString());
                points=points.concat(" - ");
                points=points.concat(punti3.getText().toString());
                builder.setMessage(points);
                AlertDialog dialog=builder.create();
                dialog.show();
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
            tot3=old_tot3;
            punti1.setText(Integer.toString(tot1));
            punti2.setText(Integer.toString(tot2));
            punti3.setText(Integer.toString(tot3));
            onSave();
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
            editor.commit();
        }
        else{
            editor.putInt("t1", PDefault);
            editor.putInt("t2", PDefault);
            editor.putInt("t3", PDefault);
            editor.putString("sqd1", sq1Default);
            editor.putString("sqd2",sq2Default);
            editor.putString("sqd3",sq3Default);
            editor.putInt("interrupted", 0);
            //archiviaPartita
                showDettPuntParz();
            editor.putString("dpp3", "");
            editor.commit();
        }
    }

    //RICHIESTA PERMESSI Android 6.0+ (Marshmallow)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

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

        if(PM3.getText().toString().matches("")){check=0;}
        else{check = Integer.parseInt(PM3.getText().toString());}

        if(PZ3.isChecked()){
            if(CH3.isChecked()){
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
            String html=sharedPref.getString("dpp3", "");
            if(html == ""){
                Snackbar.make(getView(), getString(R.string.errore_dpp), Snackbar.LENGTH_SHORT).show();
            }
            else {
                WebView webview = new WebView(getActivity());
                String header = "<html><body bgcolor=\"#FFFFFF\"><table><tr><th>" + textNome1.getText().toString() + "</th><th>" + textNome2.getText().toString() + "</th><th>" + textNome3.getText().toString() + "</th></tr>";
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

}
