package com.marco97pa.puntiburraco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ShareResultActivity extends AppCompatActivity {
private TextView name1, name2, name3, score1, score2, score3;
boolean diRitornoDaCondivisione;
int selection;
private LinearLayout root;
private BottomSheetBehavior bottomSheetBehavior;
private ExtendedFloatingActionButton fab;
private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Opt into true edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 2. Make status & nav bars transparent
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // 3. Toggle icon color: true=dark icons, false=light icons
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(false);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //lock rotation to portrait
        setContentView(R.layout.activity_share_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance

            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }

        final LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet_share);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_share));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        root = (LinearLayout) findViewById(R.id.rootLayout);
        fab = (ExtendedFloatingActionButton) findViewById(R.id.fab);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to the root view to account for system bars
            v.setPadding(systemBarInsets.left, systemBarInsets.top, systemBarInsets.right, systemBarInsets.bottom);

            ViewGroup.MarginLayoutParams fabParams = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            // Assuming 16dp base margin, convert to pixels
            int fabBaseMarginPx = (int) (16 * getResources().getDisplayMetrics().density);
            fabParams.bottomMargin = systemBarInsets.bottom + fabBaseMarginPx;
            fab.setLayoutParams(fabParams);

            // Apply bottom padding to the BottomSheet's content area
            // This ensures that when the BottomSheet is expanded, its content is above the navigation bar.
            if (bottomSheet != null) { // or bottomSheetContentContainer
                bottomSheet.setPadding(
                        bottomSheet.getPaddingLeft(),
                        bottomSheet.getPaddingTop(),
                        bottomSheet.getPaddingRight(),
                        systemBarInsets.bottom // Add the navigation bar height as bottom padding
                );
            }

            return WindowInsetsCompat.CONSUMED;
        });

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

        //Set Facebook and Instagram text
        TextView facebook_text = (TextView) findViewById(R.id.bottom_sheet_share_facebook_text);
        facebook_text.setText(String.format(getString(R.string.stories), getString(R.string.facebook)));
        TextView instagram_text = (TextView) findViewById(R.id.bottom_sheet_share_instagram_text);
        instagram_text.setText(String.format(getString(R.string.stories), getString(R.string.instagram)));

        //BUTTONS
        //fab
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

        //whatsapp
        LinearLayout whatsapp = (LinearLayout) findViewById(R.id.bottom_sheet_share_whatsapp);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.setVisibility(View.GONE); //hide BottomSheet without animations (to boost performance)
                shareOnWhatsapp();
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
        Uri image = takeScreenshot(this);

        //add event to Firebase
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Other apps");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

        //Share
        Intent share = new Intent(Intent.ACTION_SEND);
        // set flag to give temporary permission to external app to use your FileProvider
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // set the file type
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, image);
        startActivity(Intent.createChooser(share, getString(R.string.action_share)));
    }

    public void shareOnFacebook(){
        try{
            // Define image asset URI and attribution link URL
            Uri backgroundAssetUri = takeScreenshot(this);
            String attributionLinkUrl = getString(R.string.link);

            // Instantiate implicit intent with ADD_TO_STORY action,
            // background asset, and attribution link
            Intent intent = new Intent("com.facebook.stories.ADD_TO_STORY");
            intent.setDataAndType(backgroundAssetUri, "image/jpeg");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("content_url", attributionLinkUrl);

            // Instantiate activity and verify it will resolve implicit intent
            Activity activity = this;

            //add event to Firebase
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.facebook));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            activity.startActivityForResult(intent, 0);

        } catch (android.content.ActivityNotFoundException ex) {
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), getString(R.string.facebook)), Toast.LENGTH_LONG);
            toast.show();
            finish();

        } catch (NullPointerException e){
            //Show alert if Uri is null
            Toast toast = Toast.makeText(this,getString(R.string.error), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    public void shareOnInstagram(){
        try{
            // Define image asset URI and attribution link URL
            Uri backgroundAssetUri = takeScreenshot(this);
            String attributionLinkUrl = getString(R.string.link);

            // Instantiate implicit intent with ADD_TO_STORY action,
            // background asset, and attribution link
            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.setDataAndType(backgroundAssetUri, "image/jpeg");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("content_url", attributionLinkUrl);

            // Instantiate activity and verify it will resolve implicit intent
            Activity activity = this;


            //add event to Firebase
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.instagram));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            activity.startActivityForResult(intent, 0);

        } catch (android.content.ActivityNotFoundException ex) {
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), getString(R.string.instagram)), Toast.LENGTH_LONG);
            toast.show();
            finish();

        } catch (NullPointerException e){
            //Show alert if Uri is null
            Toast toast = Toast.makeText(this,getString(R.string.error), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    public void shareOnWhatsapp(){
        try{
            //Define image asset URI and text
            Uri backgroundAssetUri = takeScreenshot(this);
            String text = String.format(getString(R.string.share_message), getString(R.string.app_name), getString(R.string.link));

            //Define intent
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.putExtra(Intent.EXTRA_STREAM, backgroundAssetUri);
            sendIntent.setType("image/jpeg");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.setPackage("com.whatsapp");

            Activity activity = this;

            //add event to Firebase
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.whatsapp));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            //start intent
            activity.startActivity(sendIntent);

        } catch (android.content.ActivityNotFoundException ex) {
            //Show alert if app is not installed
            Toast toast = Toast.makeText(this,String.format(getString(R.string.app_not_installed), getString(R.string.whatsapp)), Toast.LENGTH_LONG);
            toast.show();
            finish();
        } catch (NullPointerException e){
            //Show alert if Uri is null
            Toast toast = Toast.makeText(this,getString(R.string.error), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    /**
     * TAKE SCREENSHOT
     * This method hides the BottomSheetDialog then
     * A screenshot of the app will be taken, saved in memory
     */
    public Uri takeScreenshot(Context context) throws NullPointerException{
        //First hide the ControllerView
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_share));;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        diRitornoDaCondivisione = true;

        //Then Take screenshot
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        //and save as JPG
        Uri jpg = saveImage(bmp, context);
        if(jpg == null){
            throw new NullPointerException("Image screenshot not saved");
        }

        return jpg;
    }

    private Uri saveImage(Bitmap bitmap, Context context){

        Uri uri = null;
        String filename = "Score_"+ System.currentTimeMillis() + ".jpg";

        try{
            File file = new File (context.getFilesDir(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
        }catch(Exception e){
            Log.e("saveScreenshot" , e.toString());
        }

        return uri;
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

    /**
     * Helper to listen for insets on any view and invoke a callback
     */
    private void applyWindowInsets(View view, InsetCallback callback) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            callback.onApply(windowInsets);
            return windowInsets;
        });
    }


    /**
     * Functional interface for inset callbacks
     */
    private interface InsetCallback {
        void onApply(WindowInsetsCompat insets);
    }
}
