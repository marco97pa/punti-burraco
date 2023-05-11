package com.marco97pa.puntiburraco;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.marco97pa.puntiburraco.utils.FLog;

import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;

public class NearbyAdvertise {
    private static final String TAG = "NearbyAdvertise";
    FLog log = new FLog(TAG);
    
    private static String CHANNEL_ID = "channel_shared_match";
    private static int NOTIFICATION_ID = 2;
    private String SERVICE_ID;
    private ArrayList<String> connectedEndpoints;

    // variable to hold context
    private Context context;
    //variable containing Match state to send
    private String state;
    // variable that indicates Advertise state
    private Boolean running = false;

    public NearbyAdvertise(Context context, String state) {
        this.context = context;
        this.state = state;
    }
    
    public void start(){
        //Set Notification Channel (as of Android 8.0 Oreo)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = CHANNEL_ID;
            // The user-visible name of the channel.
            CharSequence name = context.getString(R.string.shared_match);
            // The user-visible description of the channel.
            String description = context.getString(R.string.shared_match_notif);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        SERVICE_ID = context.getPackageName();
        connectedEndpoints =  new ArrayList<String>();
        log.d( "Nearby starting...");

        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(P2P_STAR).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        getUserNickname(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // We're advertising!
                                log.d( "We're advertising!");

                                running = true;

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog);
                                builder .setTitle(context.getString(R.string.searching))
                                        .setMessage(context.getString(R.string.discoverable) + "\n" +
                                                context.getString(R.string.open_nearby_hint, context.getString(R.string.app_name), context.getString(R.string.nav_mirroring)))
                                        .setIcon(R.drawable.ic_search)
                                        .setNegativeButton(context.getString(R.string.ko), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                stop();
                                            }
                                        })
                                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id) {
                                                //nothing, dismiss
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_mirroring_black_24dp)
                                        .setContentTitle(context.getString(R.string.shared_match))
                                        .setContentText(context.getString(R.string.discoverable))
                                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                        .setOngoing(true)
                                        .setChannelId(CHANNEL_ID);
                                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start advertising.
                                log.d( "We were unable to start advertising.");

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog);
                                builder .setTitle(context.getString(R.string.nearby_error))
                                        .setMessage(context.getString(R.string.nearby_error_d))
                                        .setCancelable(false)
                                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id) {
                                                //nothing, dismiss
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
    }

    public void stop(){
        Nearby.getConnectionsClient(context).stopAdvertising();
        Nearby.getConnectionsClient(context).stopAllEndpoints();

        running = false;

        //Delete notification (if there is any)
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(NOTIFICATION_ID);
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                    log.d("Found endpoint " + info.getEndpointName());
                    Nearby.getConnectionsClient(context)
                            .requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // We successfully requested a connection. Now both sides
                                            // must accept before the connection is established.
                                            log.d( "We successfully requested a connection");
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Nearby Connections failed to request the connection.
                                            log.d( "Failed to request the connection.");
                                        }
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                    log.d( "A previously discovered endpoint has gone away");
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
                            connectedEndpoints.add(endpointId);
                            sendState(endpointId);
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            log.d("The connection was rejected by one or both sides");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            log.d( "The connection broke before it was able to be accepted");
                            break;
                        default:
                            // Unknown status code
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    log.d("We've been disconnected from this endpoint: "+ endpointId);
                    connectedEndpoints.remove(endpointId);
                    // sent or received.
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
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
                    // after the call to onPayloadReceived().
                }
            };

    public void update(String state){
        this.state = state;
        for (String endpoint: connectedEndpoints) {
            sendState(endpoint);
        }
    }

    private void sendState(String toEndpointId){
        //Convert String state to byte before sending
        byte[] send = state.getBytes(Charset.forName("UTF-8"));
        //Send bytes
        log.d( "Sending state to " + toEndpointId);
        Payload bytesPayload = Payload.fromBytes(send);
        Nearby.getConnectionsClient(context).sendPayload(toEndpointId, bytesPayload);
    }

    private String getUserNickname(){
        return  Build.MANUFACTURER + " " + Build.MODEL;
    }

    public Boolean isRunning(){
        return running;
    }
}
