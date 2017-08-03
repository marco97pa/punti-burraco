package com.marco97pa.puntiburraco;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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
