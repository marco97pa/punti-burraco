package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

/**
 * CONTRIBUTIONS ACTIVITY
 * This activity code does very little:
 * - Sets the Content View
 * - Sets the navigation bar color to black
 * - Handles clicks on third and fourth cards, launching the browser
 *
 * @author Marco Fantauzzo
 */

public class Contributions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributions);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.barBlack));
        }

        CardView card_trans = (CardView) findViewById(R.id.card_view_3);
        card_trans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.localize.im/projects/x4"));
                startActivity(browserIntent);
            }
        });
        CardView card_git = (CardView) findViewById(R.id.card_view_4);
        card_git.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/marco97pa/punti-burraco"));
                startActivity(browserIntent);
            }
        });
    }


}
