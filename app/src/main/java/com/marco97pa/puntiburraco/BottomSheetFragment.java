package com.marco97pa.puntiburraco;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
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

        //Set webview
        webview = (WebView) view.findViewById(R.id.web);
        WebSettings settings = webview.getSettings();
        settings.setTextZoom(120);
        webview.loadDataWithBaseURL(null, data, "text/html; charset=UTF-8", null,null);
        return view;
    }

    /*
        HUGE FIX: On tablets and high density screens in landscape mode, the Bottom Sheet Fragment was not fully expanded
        This solution is provided by https://medium.com/@OguzhanAlpayli/bottom-sheet-dialog-fragment-expanded-full-height-65b725c8309
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }


    }