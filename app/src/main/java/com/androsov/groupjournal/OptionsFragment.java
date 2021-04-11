package com.androsov.groupjournal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class OptionsFragment extends Fragment {

    private View view;
    static boolean optionWasChange = false;
    static int darkMode = AppCompatDelegate.MODE_NIGHT_NO;
    static int currentColor = 0;
    static int fontSize = 14;
    static int lang = 0;

    public OptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_options, container, false);

        updateFontSize();
        Switch modeSwitch = view.findViewById(R.id.theme_switch);

        modeSwitch.setChecked(darkMode == AppCompatDelegate.MODE_NIGHT_YES);

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                darkMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                darkMode = AppCompatDelegate.MODE_NIGHT_NO;
            }
            optionWasChange = true;
        });

        Button changeColorButton = view.findViewById(R.id.change_color_btn);
        changeColorButton.setOnClickListener(v -> changeColorPressed());

        SeekBar seekBar = view.findViewById(R.id.seek_bar_font_size);
        seekBar.setProgress(fontSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fontSize = progress;
                updateFontSize();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        RadioButton russianButton = view.findViewById(R.id.radio_ru);
        RadioButton englishButton = view.findViewById(R.id.radio_eng);
        switch (lang){
            case 0: {
                englishButton.setChecked(true);
                break;
            }
            case 1: {
                russianButton.setChecked(true);
                break;
            }
        }

        russianButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                englishButton.setChecked(false);
                TabBarActivity tabBar = (TabBarActivity) getActivity();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(tabBar);
                preferences.edit().putString("language", "ru").apply();
                optionWasChange = true;
                lang = 1;
                setLocale("ru");
            }
        });

        englishButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                russianButton.setChecked(false);
                TabBarActivity tabBar = (TabBarActivity) getActivity();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(tabBar);
                preferences.edit().putString("language", "en").apply();
                optionWasChange = true;
                lang = 0;
                setLocale("en");
            }
        });

        Button logoutButton = view.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(v -> {
            TabBarActivity tabBar = (TabBarActivity) getActivity();
            Intent myIntent = new Intent(tabBar, MainActivity.class);
            startActivity(myIntent);
        });

        if (currentColor != 0) {
            changeColorButton.setBackgroundColor(currentColor);
            logoutButton.setBackgroundColor(currentColor);
        }

        return view;
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getActivity().recreate();
    }

    public void updateFontSize(){
        TextView fontLabel = view.findViewById(R.id.font_size);
        fontLabel.setText(String.valueOf(fontSize));
        fontLabel.setTextSize(fontSize);
        TextView modeLabel = view.findViewById(R.id.text_theme);
        modeLabel.setTextSize(fontSize);
        modeLabel = view.findViewById(R.id.text_font_size);
        modeLabel.setTextSize(fontSize);
        modeLabel = view.findViewById(R.id.text_primary_color);
        modeLabel.setTextSize(fontSize);
        modeLabel = view.findViewById(R.id.text_lang);
        modeLabel.setTextSize(fontSize);

        RadioButton russianButton = view.findViewById(R.id.radio_ru);
        russianButton.setTextSize(fontSize);
        RadioButton englishButton = view.findViewById(R.id.radio_eng);
        englishButton.setTextSize(fontSize);
    }

    public void changeColorPressed(){
        openDialog(false);
    }

    private  void openDialog(boolean supportAlpha){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.getContext(), currentColor, supportAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                TabBarActivity tabBar = (TabBarActivity) getActivity();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(tabBar);
                preferences.edit().putInt("color", color).apply();
                optionWasChange = true;
                getActivity().recreate();
            }
        });

        dialog.show();
    }
}