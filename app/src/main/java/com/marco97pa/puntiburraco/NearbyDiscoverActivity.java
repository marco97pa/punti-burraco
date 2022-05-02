package com.marco97pa.puntiburraco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.marco97pa.puntiburraco.utils.FLog;

import java.nio.charset.Charset;

public class NearbyDiscoverActivity extends AppCompatActivity {
    private static final String TAG = "NearbyDiscover";
    FLog log;
    
    private String SERVICE_ID;
    private Context context;
    private final static int LOCATION_PERMISSION_NEARBY = 40;
    private final static int BLUETOOTH_PERMISSION = 41;
    public String endPointName;
    public String endPointID;

    private TextView player1_name, player2_name, player3_name;
    private TextView player1_points, player2_points, player3_points;
    private LinearLayout player3, game, search;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_discover);
        
        log = new FLog(TAG);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;
        SERVICE_ID = context.getPackageName();

        player1_name = (TextView) findViewById(R.id.player_1_name);
        player2_name = (TextView) findViewById(R.id.player_2_name);
        player3_name = (TextView) findViewById(R.id.player_3_name);
        player1_points = (TextView) findViewById(R.id.player_1_points);
        player2_points = (TextView) findViewById(R.id.player_2_points);
        player3_points = (TextView) findViewById(R.id.player_3_points);
        player3 = (LinearLayout) findViewById(R.id.player_3);

        game = (LinearLayout) findViewById(R.id.game);
        search = (LinearLayout) findViewById(R.id.search);

        //Upgrade to PRO button
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nearby.getConnectionsClient(context).stopDiscovery();
                if(endPointID != null) {
                    Nearby.getConnectionsClient(context).disconnectFromEndpoint(endPointID);
                }
                Intent myIntent = new Intent(context, UpgradeActivity.class);
                startActivity(myIntent);
            }
        });
        //Hide upgrade button for already PRO users
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isPro = sharedPref.getBoolean("pro_user", false) ;
        if(isPro){
            button.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //On old Android start Discovery directly
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startDiscovery();
        }
        //On Android 6 ask for Location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_NEARBY);
        }
        //On Android 12 ask for Bluetooth permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    BLUETOOTH_PERMISSION);
        }
    }


    private void updateUI(String state, String endPointName){
        String[] values = state.split(";");
        int player_mode = Integer.parseInt(values[0]);
        String v_player_1 = values[1];
        String v_player_2 = values[2];
        String v_player_3 = values[3];
        String v_points_1 = values[4];
        String v_points_2 = values[5];
        String v_points_3 = values[6];

        //Hide Player 3 Layout if not 3 players mode
        if(player_mode != 3){
            player3.setVisibility(View.GONE);
        }

        player1_name.setText(v_player_1);
        player2_name.setText(v_player_2);
        player3_name.setText(v_player_3);
        player1_points.setText(v_points_1);
        player2_points.setText(v_points_2);
        player3_points.setText(v_points_3);


        }


    private void startDiscovery() {
        log.d( "Nearby starting...");
        log.d( "Closing any still active connection...");
        Nearby.getConnectionsClient(context).stopDiscovery();
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // We're discovering!
                                log.d( "We're discovering!");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We're unable to start discovering.
                                log.e( "We're unable to start discovering.");
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog);
                                builder .setTitle(context.getString(R.string.nearby_error))
                                        .setMessage(context.getString(R.string.nearby_error_d))
                                        .setCancelable(false)
                                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id) {
                                                //nothing, dismiss
                                                finish();
                                            }
                                        })
                                        .setNegativeButton(context.getString(R.string.set_permissions), new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id) {
                                                //nothing, dismiss
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                intent.setData(uri);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                    log.d("Found endpoint " + info.getEndpointName());
                    endPointName = info.getEndpointName();
                    Nearby.getConnectionsClient(context)
                            .requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // We successfully requested a connection. Now both sides
                                            // must accept before the connection is established.
                                            log.d("We successfully requested a connection.");
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Nearby Connections failed to request the connection.
                                            log.d("Failed to request the connection.");
                                        }
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                    log.d("A previously discovered endpoint has gone away");
                    search.setVisibility(View.VISIBLE);
                    game.setVisibility(View.GONE);
                    Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                            getString(R.string.disconnected),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            };

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            log.d( "We're connected! Can now start sending and receiving data");
                            endPointID = endpointId;
                            search.setVisibility(View.GONE);
                            game.setVisibility(View.VISIBLE);
                            Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                                    getString(R.string.connected_to, endPointName),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            Nearby.getConnectionsClient(context).stopDiscovery();
                        break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            log.d( "The connection was rejected by one or both sides.");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            log.d( "The connection broke before it was able to be accepted.");
                            break;
                        default:
                            // Unknown status code
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                    log.d( "We've been disconnected from this endpoint " + endpointId);
                    search.setVisibility(View.VISIBLE);
                    game.setVisibility(View.GONE);
                    Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                            getString(R.string.disconnected),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            };

    /** Callbacks for payloads (bytes of data) sent from another device to us. */
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {

                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    // This always gets the full data of the payload. Will be null if it's not a BYTES
                    // payload. You can check the payload type with payload.getType().
                    byte[] receivedBytes = payload.asBytes();
                    //Converting bytes to String
                    String state = new String(receivedBytes, Charset.forName("UTF-8"));
                    log.d( "Received: " + state);
                    updateUI(state, endPointName);

                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
                    // after the call to onPayloadReceived().
                }
            };

    @Override
    public void onBackPressed() {
        Nearby.getConnectionsClient(context).stopDiscovery();
        if(endPointID != null) {
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endPointID);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Nearby.getConnectionsClient(context).stopDiscovery();
        if(endPointID != null) {
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endPointID);
        }
    }

    //ASK PERMISSION Android 6.0+ (Marshmallow)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_NEARBY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    startDiscovery();

                } else {

                    // permission denied, boo!
                    Toast.makeText(this, getString(R.string.denied_perm_location), Toast.LENGTH_LONG).show();
                    finish();

                }
                return;
            }

            case BLUETOOTH_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    startDiscovery();

                } else {

                    // permission denied, boo!
                    Toast.makeText(this, getString(R.string.denied_perm_bluetooth), Toast.LENGTH_LONG).show();
                    finish();

                }
                return;
            }
        }
    }

    public String getUserNickname(){
        return  Build.MANUFACTURER + " " + Build.MODEL;
    }
}
