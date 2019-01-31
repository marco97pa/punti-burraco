package com.marco97pa.puntiburraco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private List<Score> scoreList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private ScoreAdapter mAdapter;
    private DividerItemDecoration mDividerItemDecoration;

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set Theme
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isNight = sharedPreferences.getBoolean("night", false) ;
        if(isNight){
            setTheme(R.style.DarkTheme);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.barBlack));
            }
        }
        else{
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_history);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);

        mAdapter = new ScoreAdapter(scoreList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new MainActivity.ClickListener() {

            public void onClick(View view, final int position) {
            /*

                */
                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(HistoryActivity.this);
                View sheetView = HistoryActivity.this.getLayoutInflater().inflate(R.layout.history_bottom_sheet, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();
                
                //OnClickListeners
                LinearLayout share = (LinearLayout) sheetView.findViewById(R.id.fragment_history_bottom_sheet_edit);
                LinearLayout delete = (LinearLayout) sheetView.findViewById(R.id.fragment_history_bottom_sheet_delete);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        startActivity(Intent.createChooser(share, getString(R.string.action_share)));
                        mBottomSheetDialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Score selezionato = scoreList.get(position);
                        ScoreDB db = new ScoreDB(HistoryActivity.this);
                        db.open();
                        db.deleteScore(selezionato.getId());
                        Toast.makeText(HistoryActivity.this, "GOR", Toast.LENGTH_LONG);
                        db.close();
                        scoreList.remove(position);
                        recyclerView.removeViewAt(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                        mBottomSheetDialog.dismiss();
                    }
                });
                
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        prepareData();
        
        //Se dopo ver recuperato i dati, l'Adapter e' vuoto, allora mostra la emptyView
        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
   }

    private void prepareData() {
        Score score;

        ScoreDB db = new ScoreDB(this);
        db.open();
        Cursor c = db.getAllScores();
        if (c.moveToFirst())
        {
            do {
                score = new Score(c.getLong(0), c.getString(1),c.getString(2),c.getString(3),c.getInt(4), c.getInt(5), c.getInt(6), c.getString(7));
                scoreList.add(score);
            } while (c.moveToNext());
        }
        db.close();

        mAdapter.notifyDataSetChanged();
    }
}
