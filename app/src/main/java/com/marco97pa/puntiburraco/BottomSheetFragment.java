package com.marco97pa.puntiburraco;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private WebView webview;

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

        Bundle args = getArguments();
        String data = args.getString("data", "");

        webview = (WebView) view.findViewById(R.id.web);
        WebSettings settings = webview.getSettings();
        settings.setTextZoom(120);
        webview.loadData(data, "text/html; charset=UTF-8", null);
        return view;
    }


    }