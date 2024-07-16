package com.marco97pa.puntiburraco.utils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.WindowManager;

public class UserActivityReceiver extends BroadcastReceiver {
    private Runnable dimRunnable;
    private Handler handler;
    private Context context;

    public UserActivityReceiver(Context context, Runnable dimRunnable, Handler handler) {
        this.context = context;
        this.dimRunnable = dimRunnable;
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        handler.removeCallbacks(dimRunnable);
        resetScreenBrightness(context);
        handler.postDelayed(dimRunnable, 60000); // Reset the timer
    }

    private void resetScreenBrightness(Context context) {
        WindowManager.LayoutParams layoutParams = ((Activity) context).getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; // Reset to default brightness
        ((Activity) context).getWindow().setAttributes(layoutParams);
    }
}