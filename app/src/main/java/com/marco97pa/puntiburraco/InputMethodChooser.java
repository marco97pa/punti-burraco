package com.marco97pa.puntiburraco;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class InputMethodChooser extends Fragment {

    LinearLayout option1, option2, option3, layout_hand;
    RadioButton radioButton1, radioButton2, radioButton3;
    SwitchCompat switch_hand;
    SharedPreferences sharedPref;
    Boolean point_hand;
    int input_method;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        point_hand = sharedPref.getBoolean("input_puntimano", true);
        input_method = sharedPref.getInt("input_method", 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Setting activity title
        ((SettingActivity) getActivity()).setTitle(getString(R.string.setting_insert_method));

        View rootView = inflater.inflate(R.layout.input_method_chooser, container,false);

        option1 = (LinearLayout) rootView.findViewById(R.id.input_method_1);
        option2 = (LinearLayout) rootView.findViewById(R.id.input_method_2);
        option3 = (LinearLayout) rootView.findViewById(R.id.input_method_3);
        radioButton1 = (RadioButton) rootView.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) rootView.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) rootView.findViewById(R.id.radioButton3);
        layout_hand = (LinearLayout) rootView.findViewById(R.id.points_hand);
        switch_hand = (SwitchCompat) rootView.findViewById(R.id.switch_hand);

        switch (input_method){
            case 1: radioButton1.setChecked(true);
                    layout_hand.setVisibility(View.VISIBLE);
                    break;
            case 2: radioButton2.setChecked(true);
                    layout_hand.setVisibility(View.GONE);
                    break;
            case 3: radioButton3.setChecked(true);
                    layout_hand.setVisibility(View.GONE);
                    break;
        }
        switch_hand.setChecked(point_hand);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioButton1.setChecked(true);
                layout_hand.setVisibility(View.VISIBLE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("input_method", 1);
                editor.commit();
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioButton2.setChecked(true);
                layout_hand.setVisibility(View.GONE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("input_method", 2);
                editor.commit();
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButton1.setChecked(false);
                radioButton2.setChecked(false);
                radioButton3.setChecked(true);
                layout_hand.setVisibility(View.GONE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("input_method", 3);
                editor.commit();
            }
        });

        layout_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                point_hand = !point_hand;
                switch_hand.setChecked(point_hand);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("input_puntimano", point_hand);
                editor.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}
