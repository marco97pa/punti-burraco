package com.marco97pa.puntiburraco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareResultActivity extends AppCompatActivity {
private TextView name1, name2, name3, score1, score2, score3;
boolean diRitornoDaCondivisione;
int selection;
private LinearLayout root;
private BottomSheetBehavior bottomSheetBehavior;
private ExtendedFloatingActionButton fab;
private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //lock rotation to portrait
        setContentView(R.layout.activity_share_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance

            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }

        final LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet_share);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_share));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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
        selection = 1;
        changeBackground();
        /* Handling swipes on rootView
         * Change background of the story if BottomSheet is not open
         */
        root.setOnTouchListener(new OnSwipeTouchListener(ShareResultActivity.this) {
            @Override
            public void onTouchDown() {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    //if is open, hide BottomSheet and show Fab button, but don't change background
                    fab_show();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }

            public void onSwipeRight() {
                //if is closed than change background
                selection--;
                changeBackground();
            }

            public void onSwipeLeft() {
                //if is closed than change background
                selection++;
                changeBackground();
            }

        });

        //BUTTONS
        //fab
        fab = (ExtendedFloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                fab.setVisibility(View.INVISIBLE);
            }
        });

        //instagram
        LinearLayout instagram = (LinearLayout) findViewById(R.id.bottom_sheet_share_instagram);
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.setVisibility(View.GONE); //hide BottomSheet without animations (to boost performance)
                shareOnInstagram();
            }
        });

        //facebook
        LinearLayout facebook = (LinearLayout) findViewById(R.id.bottom_sheet_share_facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.setVisibility(View.GONE); //hide BottomSheet without animations (to boost performance)
                shareOnFacebook();
            }
        });

        //share
        LinearLayout btn = (LinearLayout) findViewById(R.id.bottom_sheet_share_others);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.setVisibility(View.GONE); //hide BottomSheet without animations (to boost performance)
                shareOnOtherApps();
            }
        });
        ;

        //Activity started
        diRitornoDaCondivisione = false;

        //Display hint
        Toast toast = Toast.makeText(this,getString(R.string.change_background_hint), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
            case 0:
                selection = 6;
                root.setBackgroundResource(R.drawable.gradient_6); break;
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

        //add event to Firebase
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Other apps");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

        //Share
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, image);
        startActivity(Intent.createChooser(share, getString(R.string.action_share)));
    }

    public void shareOnFacebook(){
        // Define image asset URI and attribution link URL
        Uri backgroundAssetUri = takeScreenshot();
        String attributionLinkUrl = getString(R.string.link);

        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, and attribution link
        Intent intent = new Intent("com.facebook.stories.ADD_TO_STORY");
        intent.setDataAndType(backgroundAssetUri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("content_url", attributionLinkUrl);

        // Instantiate activity and verify it will resolve implicit intent
        Activity activity = this;
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {

            //add event to Firebase
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Facebook stories");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            activity.startActivityForResult(intent, 0);
        }
        else{
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), "Facebook"), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    public void shareOnInstagram(){
        // Define image asset URI and attribution link URL
        Uri backgroundAssetUri = takeScreenshot();
        String attributionLinkUrl = getString(R.string.link);

        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, and attribution link
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setDataAndType(backgroundAssetUri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("content_url", attributionLinkUrl);

        // Instantiate activity and verify it will resolve implicit intent
        Activity activity = this;
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {

            //add event to Firebase
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Instagram stories");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            activity.startActivityForResult(intent, 0);
        }
        else {
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), "Instagram"), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    /**
     * TAKE SCREENSHOT
     * This method hides the BottomSheetDialog then
     * A screenshot of the app will be taken, saved in memory
     */
    public Uri takeScreenshot(){
        //First hide the ControllerView
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_share));;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

    public void fab_show(){
        // previously invisible view
        View fab = findViewById(R.id.fab);
        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = fab.getWidth() / 2;
            int cy = fab.getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0f, finalRadius);

            // make the view visible and start the animation
            fab.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            // set the view to invisible without a circular reveal animation below Lollipop
            fab.setVisibility(View.INVISIBLE);
        }
    }


    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                onTouchDown();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

        public void onTouchDown(){
        }
    }
}
