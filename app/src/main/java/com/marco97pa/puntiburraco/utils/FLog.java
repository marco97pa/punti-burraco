package com.marco97pa.puntiburraco.utils;

import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import androidx.annotation.NonNull;

/*
 * FLog: Fantauzzo's Log
 * This class makes easier to Log what is happening in the app
 * It will write messages on the classic Log and on the FirebaseCrashlytics Log
 *
 * HOW TO USE:
 *      Initialization:
 *          FLog log = new FLog(LOG_TAG);
 *
 *      Log:
 *          for debug: log.d("message");
 *          for info: log.i("message");
 *          for warning: log.w("message");
 *          for error: log.e("message");
 *          for verbose: log.v("message");
 */
public class FLog {
    private String LOG_TAG;
    private FirebaseCrashlytics firebaseCrashlytics;

    public FLog(@NonNull String LOG_TAG){
        this.LOG_TAG = LOG_TAG;
        firebaseCrashlytics =  FirebaseCrashlytics.getInstance();
    }

    public void d(@NonNull String msg){
        Log.d(LOG_TAG, msg);
        firebaseCrashlytics.log(msg);
    }

    public void w(@NonNull String msg){
        Log.w(LOG_TAG, msg);
        firebaseCrashlytics.log(msg);
    }

    public void i(@NonNull String msg){
        Log.i(LOG_TAG, msg);
        firebaseCrashlytics.log(msg);
    }

    public void e(@NonNull String msg){
        Log.e(LOG_TAG, msg);
        firebaseCrashlytics.log(msg);
    }

    public void v(@NonNull String msg){
        Log.v(LOG_TAG, msg);
        firebaseCrashlytics.log(msg);
    }
}
