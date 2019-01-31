package com.marco97pa.puntiburraco;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marco F on 02/08/2017.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.MyViewHolder> {

    private List<Score> scoresList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView players, points, date;

        public MyViewHolder(View view) {
            super(view);
            players = (TextView) view.findViewById(R.id.players);
            points = (TextView) view.findViewById(R.id.score);
            date = (TextView) view.findViewById(R.id.date);
        }
    }


    public ScoreAdapter(List<Score> scoresList) {
        this.scoresList = scoresList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Score score = scoresList.get(position);
        holder.players.setText(score.getPlayer1() +" "+ score.getPlayer2() +" "+ score.getPlayer3());

        String score3Mod;
        if(score.getPoint3() == -1) {
            score3Mod = "";
        }
        else{
            score3Mod = " - "+Integer.toString(score.getPoint3());
        }
        holder.points.setText(score.getPoint1() +" - "+score.getPoint2()+score3Mod);
        holder.date.setText(score.getDate());
    }

    @Override
    public int getItemCount() {
        return scoresList.size();
    }
}
