package com.example.jchiiki.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.jchiiki.items.SummarizedPracticeChoices;

import java.util.List;

public class SummarizedPractices_Adaptor extends RecyclerView.Adapter<SummarizedPractices_Adaptor.ViewHolder> {

    private Context context;
    private List<SummarizedPracticeChoices> summarizedPracticeChoicesList;

    public SummarizedPractices_Adaptor(Context context, List<SummarizedPracticeChoices> summarizedPracticeChoicesList) {
        this.context = context;
        this.summarizedPracticeChoicesList = summarizedPracticeChoicesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.summarized_practice_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DBQuery.g_selectedTest_index = position;
        SummarizedPracticeChoices item = summarizedPracticeChoicesList.get(position);

        holder.summarizedPracticeLevel_TextView.setText(item.getSummarizedPracticeLanguageLevel());
        holder.summarizedPracticeTitle_TextView.setText(item.getSummarizedPracticeTitle());

        holder.itemView.setOnClickListener(v -> {
            DBQuery.g_selectedTest_index = position; // Lưu lại vị trí của item được chọn
            DBQuery.g_practiceOptionChoice = "Summarized"; // Lưu lại chế độ học được chọn để hiển thị câu hỏi phù hợp trong Questions.java

            Context context = v.getContext();
            Intent intent = new Intent(context, Ready.class);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return summarizedPracticeChoicesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView summarizedPracticeLevel_TextView, summarizedPracticeTitle_TextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            summarizedPracticeLevel_TextView = itemView.findViewById(R.id.summarizedPracticeLevel_TextView);
            summarizedPracticeTitle_TextView = itemView.findViewById(R.id.summarizedPracticeTitle_TextView);

        }
    }
}
