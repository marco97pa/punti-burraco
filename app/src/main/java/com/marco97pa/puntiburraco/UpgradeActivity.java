package com.marco97pa.puntiburraco;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ERROR;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class UpgradeActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final int BUY_SUCCESS = 0;
    private static final int GOOGLE_PLAY_NOT_AVAILABLE = 1;
    private static final int ONE_TIME_PURCHASE_UNSUPPORTED = 2;
    private static final int RESTORE_FAIL = 3;
    private static final int NO_INTERNET = 4;

    BillingProcessor bp;
    boolean isAvailable;
    boolean isOneTimePurchaseSupported;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView description;
    private ExtendedFloatingActionButton fab;
    private AppBarLayout appbar;
    private LinearLayout details_layout;
    private boolean expanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        details_layout = (LinearLayout) findViewById(R.id.details);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        appbar = (AppBarLayout) findViewById(R.id.app_bar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //Collapsed
                    expanded = false;
                }
                else {
                    //Expanded
                    expanded = true;
                }
                invalidateOptionsMenu();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        fab = (ExtendedFloatingActionButton) findViewById(R.id.fab);
        description = (TextView) findViewById(R.id.description);

        //Check if the device is connected
        boolean isConnected = checkConnectivity(this);

        if(isConnected) {
            isAvailable = BillingProcessor.isIabServiceAvailable(this);
            if (isAvailable) {
                // continue
                bp = BillingProcessor.newBillingProcessor(this, getString(R.string.google_play_license_key), this);
                bp.initialize();
            } else {
                showAlert(GOOGLE_PLAY_NOT_AVAILABLE);
            }
        }
        else{
            showAlert(NO_INTERNET);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy();
            }
        });

    }

    private void buy(){
        bp.purchase(this, getString(R.string.in_app_pro_version));
    }

    private void restore(){
        bp.loadOwnedPurchasesFromGoogle();
        if(bp.isPurchased(getString(R.string.in_app_pro_version))){
            upgrade();
        }
        else{
            showAlert(RESTORE_FAIL);
        }
    }

    private void upgrade(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("pro_user", true);
        editor.apply();
        showAlert(BUY_SUCCESS);
    }

    private void showAlert(int alertCode){
        String title = "";
        String description = "";
        int icon = 0;
        DialogInterface.OnClickListener action = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        switch (alertCode){
            case BUY_SUCCESS:
                icon = R.drawable.ic_local_mall_black_24dp;
                title = getString(R.string.thanks);
                description = getString(R.string.buy_success);
                action = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //RESTART app
                        restartApp(getApplicationContext());
                    }
                };
                break;

            case GOOGLE_PLAY_NOT_AVAILABLE:
                icon = R.drawable.ic_warning_24dp;
                title = getString(R.string.error);
                description = getString(R.string.error_buy_play);
                break;

            case ONE_TIME_PURCHASE_UNSUPPORTED:
                icon = R.drawable.ic_warning_24dp;
                title = getString(R.string.error);
                description = getString(R.string.error_buy_api);
                break;

            case RESTORE_FAIL:
                icon = R.drawable.ic_warning_24dp;
                title = getString(R.string.error);
                description = getString(R.string.error_restore_fail, getString(R.string.email));
                break;
            case NO_INTERNET:
                icon = R.drawable.ic_signal_wifi_off_24dp;
                title = getString(R.string.error_network);
                description = getString(R.string.error_buy_network);
                break;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AppTheme_Dialog);
        builder .setTitle(title)
                .setMessage(description)
                .setIcon(icon)
                .setPositiveButton(getString(R.string.ok), action)
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void redeemPromoCode(){
        final String base_url = "https://play.google.com/redeem?code=";

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AppTheme_Dialog);
        builder.setTitle(getString(R.string.action_redeem));
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String promo_code = input.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(base_url + promo_code));
                startActivity(i);
            }
        });
        builder.setNegativeButton(getString(R.string.ko), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(expanded) {
            getMenuInflater().inflate(R.menu.action_upgrade, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_restore) {
            restore();
            return true;
        }
        else if (id == R.id.action_redeem) {
            redeemPromoCode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
        if (isOneTimePurchaseSupported) {
            SkuDetails sku = bp.getPurchaseListingDetails(getString(R.string.in_app_pro_version));
            collapsingToolbar.setTitle(purgeTitle(sku.title));
            details_layout.setVisibility(View.VISIBLE);
            description.setText(sku.description);
            fab.setText(sku.priceText);
            fab.setEnabled(true);
        }
        else{
            showAlert(ONE_TIME_PURCHASE_UNSUPPORTED);
        }
    }

    /**
     * Removes anything between parenthesis in a string
     * As of InAppBilling library v3,
     * the "title" property from getPurchaseListingDetails() seems to always be of the form "<item title> (app name)"
     * This is ugly, because user already knows in which app he is.
     * So this method uses a Regex to remove anything between parenthesis in a string
     * @param title title of the app from getPurchaseListingDetails()
     * @return titleWithoutAppName same string as title without the app name
     */
    private String purgeTitle(String title){
        String skuTitleAppNameRegex = "(?> \\(.+?\\))$";
        Pattern p = Pattern.compile(skuTitleAppNameRegex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(title);
        String titleWithoutAppName = m.replaceAll("");
        return titleWithoutAppName;
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        if(productId.equals(getString(R.string.in_app_pro_version))){
            upgrade();
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred
         */
        String error_msg = "";

        switch (errorCode){

            //User pressed back or canceled a dialog
            case BILLING_RESPONSE_RESULT_USER_CANCELED:
                error_msg = getString(R.string.error_buy_user_cancel);
                break;

            // Network connection is down
            case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
                error_msg = getString(R.string.error_network);
                break;

            //Billing API version is not supported for the type requested
            case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                error_msg = getString(R.string.error_buy_api);
                break;

            //Requested product is not available for purchase
            case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
                error_msg = getString(R.string.error_buy_product);
                break;

            /*  Invalid arguments provided to the API. This error can also indicate that the application
                was not correctly signed or properly set up for In-app Billing in Google Play, or
                does not have the necessary permissions in its manifest
             */
            case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                error_msg = getString(R.string.error_buy_developer, getString(R.string.email));
                break;

            //Fatal error during the API action
            case BILLING_RESPONSE_RESULT_ERROR:
                error_msg = getString(R.string.error_buy_generic);
                break;

            //Failure to purchase since item is already owned
            case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                error_msg = getString(R.string.error_buy_already_own);
                upgrade();
                break;
        }

        Snackbar mySnackbar = Snackbar.make(appbar, error_msg, Snackbar.LENGTH_LONG);
        mySnackbar.show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * And don't forget to release your BillingProcessor instance!
     */
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    private void restartApp(Context context){
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 975684;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, mPendingIntent);
        System.exit(0);
    }

    public static boolean checkConnectivity(Context mContext) {
        if (mContext == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            nc.hasTransport((NetworkCapabilities.TRANSPORT_ETHERNET)));
                }
            } else {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo tempNetworkInfo : networkInfos) {
                    if (tempNetworkInfo.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
