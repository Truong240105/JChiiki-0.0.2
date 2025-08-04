package com.example.jchiiki.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.Ready;
import com.example.jchiiki.items.LeaderBoardItems;
import com.example.jchiiki.items.SummarizedPracticeChoices;

import java.util.List;

public class LeaderBoard_Adaptor extends RecyclerView.Adapter<LeaderBoard_Adaptor.ViewHolder> {
    private Context context;
    private List<LeaderBoardItems> leaderBoardItemList;

    public LeaderBoard_Adaptor(Context context, List<LeaderBoardItems> leaderBoardItemList) {
        this.context = context;
        this.leaderBoardItemList = leaderBoardItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LeaderBoardItems leaderBoardItem = leaderBoardItemList.get(position);

        holder.userName_TextView.setText(leaderBoardItem.getUserName());
        holder.score_TextView.setText("Điểm: " + leaderBoardItem.getScore());
        holder.rank_TextView.setText("🏆 Rank - " + leaderBoardItem.getRank());
    }


    @Override
    public int getItemCount() {
        if(leaderBoardItemList.size() > 10){
            return 10;
        }
        return leaderBoardItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName_TextView, score_TextView, rank_TextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName_TextView = itemView.findViewById(R.id.userName_TextView);
            score_TextView = itemView.findViewById(R.id.score_TextView);
            rank_TextView = itemView.findViewById(R.id.rank_TextView);

        }
    }
}
