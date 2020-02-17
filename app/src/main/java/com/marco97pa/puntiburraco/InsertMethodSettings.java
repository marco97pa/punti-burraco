package com.marco97pa.puntiburraco;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class InsertMethodSettings extends SettingsFragment {

    public static final String FRAGMENT_TAG = "insert_method_fragment";

    public InsertMethodSettings() {
    }

    public void onCreatePreferences(Bundle bundle, String rootKey) {
        addPreferencesFromResource(R.xml.method_preference);
    }
}