package com.marco97pa.puntiburraco;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

public class MainIntroActivity extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Force Light Theme on first startup to address strange colours
        if(getDelegate().getLocalNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        }
        setButtonBackVisible(false);
        //First slide: Welcome
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.app_description)
                .image(R.mipmap.ic_launcher_foreground)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(true)
                .build());
        //Second slide: Share with friends
        addSlide(new SimpleSlide.Builder()
                .title(R.string.action_share)
                .description(R.string.intro_share)
                .image(R.drawable.ic_forum_white)
                .background(R.color.colorAccent)
                .backgroundDark(R.color.colorSecondary)
                .scrollable(true)
                .build());
        //Ask for Notification permission on Android 13+
        // We actually ignore the response since if the user denies, notification won't be posted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.setting_notification)
                    .description(R.string.channel_description)
                    .image(R.drawable.baseline_notifications_active_24)
                    .background(R.color.design_default_color_secondary)
                    .backgroundDark(R.color.design_default_color_secondary_variant)
                    .scrollable(false)
                    .permission(Manifest.permission.POST_NOTIFICATIONS)
                    .build());
        }
        //Third slide: How points are counted
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_personalize)
                .description(R.string.intro_set_input_method)
                .image(R.drawable.cards_playing_outline)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(true)
                .build());
        //Fourth slide: choose your favourite method
        FragmentSlide loginSlide = new FragmentSlide.Builder()
                .background(R.color.colorOnSecondary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(InputMethodChooser.newInstance())
                .build();
        addSlide(loginSlide);
    }
}
