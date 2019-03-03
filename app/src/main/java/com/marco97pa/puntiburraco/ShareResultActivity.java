package com.marco97pa.puntiburraco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareResultActivity extends AppCompatActivity {
private TextView name1, name2, name3, score1, score2, score3;
boolean diRitornoDaCondivisione;
int selection;
LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_result);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance

            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
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

        //Set background based on user selection
        root = (LinearLayout) findViewById(R.id.rootLayout);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String background_selection = sharedPreferences.getString("background_stories", "1");
        selection = Integer.parseInt(background_selection);
        changeBackground();

        //BUTTONS
        Button instagram = (Button) findViewById(R.id.button_instagram);
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnInstagram();
            }
        });

        Button facebook = (Button) findViewById(R.id.button_facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnFacebook();
            }
        });

        Button btn = (Button) findViewById(R.id.button_share);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnOtherApps();
            }
        });

        //Change background by tapping on view
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection++;
                changeBackground();
            }
        });

        //Activity started
        diRitornoDaCondivisione = false;

        //Display hint
        Toast toast = Toast.makeText(this,getString(R.string.change_background_hint), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    public void onResume() {
        super.onResume();
        //Close after sharing
        if(diRitornoDaCondivisione) {
            finish();
        }

    }

    public void changeBackground(){
        switch (selection){
            case 1: root.setBackgroundResource(R.drawable.gradient_1); break;
            case 2: root.setBackgroundResource(R.drawable.gradient_2); break;
            case 3: root.setBackgroundResource(R.drawable.gradient_3); break;
            case 4: root.setBackgroundResource(R.drawable.gradient_4); break;
            case 5: root.setBackgroundResource(R.drawable.gradient_5); break;
            case 6: root.setBackgroundResource(R.drawable.gradient_6); break;
            default:
                selection = 1;
                root.setBackgroundResource(R.drawable.gradient_1); break;
        }
    }

    public void shareOnOtherApps(){
        Uri image = takeScreenshot();
        //Share
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, image);
        startActivity(Intent.createChooser(share, getString(R.string.action_share)));
    }

    public void shareOnFacebook(){
        // Define image asset URI and attribution link URL
        Uri backgroundAssetUri = takeScreenshot();
        String attributionLinkUrl = "https://punti-burraco.firebaseapp.com/";

        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, and attribution link
        Intent intent = new Intent("com.facebook.stories.ADD_TO_STORY");
        intent.setDataAndType(backgroundAssetUri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("https://punti-burraco.firebaseapp.com/", attributionLinkUrl);

        // Instantiate activity and verify it will resolve implicit intent
        Activity activity = this;
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);
        }
        else{
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), "Facebook"), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void shareOnInstagram(){
        // Define image asset URI and attribution link URL
        Uri backgroundAssetUri = takeScreenshot();
        String attributionLinkUrl = "https://punti-burraco.firebaseapp.com/";

        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, and attribution link
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setDataAndType(backgroundAssetUri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("https://punti-burraco.firebaseapp.com/", attributionLinkUrl);

        // Instantiate activity and verify it will resolve implicit intent
        Activity activity = this;
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);
        }
        else {
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), "Instagram"), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * TAKE SCREENSHOT
     * This method hides the ControllerView then
     * A screenshot of the app will be taken, saved in memory
     */
    public Uri takeScreenshot(){
        //First hide the ControllerView
        LinearLayout controller = (LinearLayout) findViewById(R.id.ControllerLayout) ;
        controller.setVisibility(View.GONE);
        diRitornoDaCondivisione = true;

        //Then Take screenshot and save as JPG
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        String nomeFoto = "SCREEN"+ System.currentTimeMillis() + ".jpg";
        File imageFileToShare;
        try {
            // Save as png
            FileOutputStream fos = new FileOutputStream(imageFileToShare = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //Get URI
            Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.marco97pa.puntiburraco.provider",
                    imageFileToShare);

            return uri;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
