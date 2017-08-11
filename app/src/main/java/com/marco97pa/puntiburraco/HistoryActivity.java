package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private List<Score> scoreList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ScoreAdapter mAdapter;
    private DividerItemDecoration mDividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ScoreAdapter(scoreList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new MainActivity.ClickListener() {

            public void onClick(View view, int position) {
                Score selezionato = scoreList.get(position);
                String points3, player3;
                if(selezionato.getPoint3() == -1) {
                    points3 = "";
                    player3 = "";
                }
                else{
                    points3 = " - "+Integer.toString(selezionato.getPoint3());
                    player3 = " vs "+selezionato.getPlayer3();
                }
                String scoreToText = selezionato.getPlayer1()+ " vs "+ selezionato.getPlayer2() + player3 + ": " + selezionato.getPoint1() + " - " + selezionato.getPoint2() + points3;

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, scoreToText);
                startActivity(Intent.createChooser(share, getString(R.string.share_hint)));
            }

            public void onLongClick(View view, int position) {

            }
        }));

        prepareData();
    }

    private void prepareData() {
        Score score;

        ScoreDB db = new ScoreDB(this);
        db.open();
        Cursor c = db.getAllScores();
        if (c.moveToFirst())
        {
            do {
                score = new Score(c.getString(1),c.getString(2),c.getString(3),c.getInt(4), c.getInt(5), c.getInt(6), c.getString(7));
                scoreList.add(score);
            } while (c.moveToNext());
        }
        db.close();

        mAdapter.notifyDataSetChanged();
    }
}
