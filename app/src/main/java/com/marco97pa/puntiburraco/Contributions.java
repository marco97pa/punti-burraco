package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

        Button card_trans = (Button) findViewById(R.id.button3);
        card_trans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://punti-burraco.oneskyapp.com/"));
                startActivity(browserIntent);
            }
        });

        Button card_git = (Button) findViewById(R.id.button4);
        card_git.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/marco97pa/punti-burraco"));
                startActivity(browserIntent);
            }
        });

        Button card_review = (Button) findViewById(R.id.button6);
        card_review.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getString(R.string.message_out_to_play_store), Toast.LENGTH_LONG).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.marco97pa.puntiburraco"));
                startActivity(browserIntent);
            }
        });

        Button card_share = (Button) findViewById(R.id.button5);
        card_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                String testo = getString(R.string.share_message)+ " https://play.google.com/store/apps/details?id=" + appPackageName;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, testo);
                startActivity(Intent.createChooser(share, getString(R.string.share_hint)));
            }
        });
    }


}
