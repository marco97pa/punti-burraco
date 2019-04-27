package com.marco97pa.puntiburraco;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.core.content.ContextCompat;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private WebView webview;
    private TextView title;
    private LinearLayout dialog;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        //Retrieve data
        Bundle args = getArguments();
        String data = args.getString("data", "");

        //Set Night according to theme
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isNight = sharedPreferences.getBoolean("night", false) ;
        if(isNight){
            title = (TextView) view.findViewById(R.id.title);
            title.setTextColor(ContextCompat.getColor(getActivity(), R.color.barWhite));
            dialog = (LinearLayout) view.findViewById(R.id.dialog);
            dialog.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bgBlack));
        }

        //Set webview
        webview = (WebView) view.findViewById(R.id.web);
        WebSettings settings = webview.getSettings();
        settings.setTextZoom(120);
        webview.loadData(data, "text/html; charset=UTF-8", null);
        return view;
    }


    }