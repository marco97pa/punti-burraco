package com.marco97pa.puntiburraco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareResultActivity extends AppCompatActivity {
private TextView name1, name2, name3, score1, score2, score3;
boolean diRitornoDaCondivisione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_result);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        name1 = (TextView) findViewById(R.id.name1);
        name2 = (TextView) findViewById(R.id.name2);
        name3 = (TextView) findViewById(R.id.name3);
        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);

        Intent intent = getIntent();
        String s_name1 = intent.getStringExtra("name1");
        String s_name2 = intent.getStringExtra("name2");
        String s_name3 = intent.getStringExtra("name3");
        String s_score1 = Integer.toString(intent.getIntExtra("score1",0));
        String s_score2 = Integer.toString(intent.getIntExtra("score2",0));
        String s_score3 = Integer.toString(intent.getIntExtra("score3",0));

        name1.setText(s_name1);
        name2.setText(s_name2);
        score1.setText(s_score1);
        score2.setText(s_score2);
        if(s_name3 != null){
            name3.setText(s_name3);
            score3.setText(s_score3);
        }
        else{
            name3.setVisibility(View.GONE);
            score3.setVisibility(View.GONE);
        }

        //Imposta lo sfondo
        LinearLayout root = (LinearLayout) findViewById(R.id.rootLayout);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String background_selection = sharedPreferences.getString("background_stories", "1");
        switch (background_selection){
            case "1": root.setBackgroundResource(R.drawable.gradient_1); break;
            case "2": root.setBackgroundResource(R.drawable.gradient_2); break;
            case "3": root.setBackgroundResource(R.drawable.gradient_3); break;
            case "4": root.setBackgroundResource(R.drawable.gradient_4); break;
        }

        //CLICCA OVUNQUE PER USCIRE
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Activity avviata
        diRitornoDaCondivisione = false;

    }

    public void onResume() {
        super.onResume();
        //Nonappena ha finito di creare l'Activity, aspetta 200ms
        //e poi FAI LO SCREENSHOT E CONDIVIDI
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(diRitornoDaCondivisione == false) {
                    diRitornoDaCondivisione = true;
                    screenshotAndShare();
                }
                else{
                    finish();
                }
        }
        }, 200);

    }
    /**
     * TAKE SCREENSHOT, then SHARE
     * This method shares the scores.
     * A screenshot of the app will be taken, saved in memory, then invoked the Android Share Intent
     */
    public void screenshotAndShare(){
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        String nomeFoto = "SCREEN"+ System.currentTimeMillis() + ".png";
        File imageFileToShare;
        try {
            // Save as png
            FileOutputStream fos = new FileOutputStream(imageFileToShare = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            //Share
            Intent share = new Intent(Intent.ACTION_SEND);

            share.setType("image/png");

            //I have put png image named myImage.png in my app directory
            //String imagePath = Environment.getExternalStorageDirectory()+nomeFoto;
            //File imageFileToShare = new File(imagePath);

            Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.marco97pa.puntiburraco.provider",
                    imageFileToShare);
            share.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(share, getString(R.string.action_share)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
