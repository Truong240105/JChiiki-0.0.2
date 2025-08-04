package com.example.jchiiki.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.items.GrammarPoint;
import com.example.jchiiki.items.ExampleSentence;

import java.util.List;

public class LessonContent_Adaptor extends RecyclerView.Adapter<LessonContent_Adaptor.ViewHolder> {
    private Context context;
    private List<GrammarPoint> grammarPointList;
    public LessonContent_Adaptor(Context context, List<GrammarPoint> grammarPointList) {
        this.context = context;
        this.grammarPointList = grammarPointList;
    }

    @NonNull
    @Override
    public LessonContent_Adaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vocabulary_word_item, parent, false);
        return new LessonContent_Adaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonContent_Adaptor.ViewHolder holder, int position) {
        GrammarPoint grammarPoint = grammarPointList.get(position);
        holder.wordContent_TextView.setText(grammarPoint.getTitle());
        holder.simplify_TextView.setText(""); // Có thể bỏ hoặc dùng giải thích ngắn
        holder.pronunciation_TextView.setText(grammarPoint.getExplanation());
        holder.meaning_TextView.setText(""); // Có thể dùng nghĩa tổng quát nếu có

        // Xử lý expand/collapse
        holder.exampleSentences_Layout.removeAllViews();
        if (grammarPoint.isExpanded()) {
            holder.exampleSentences_Layout.setVisibility(View.VISIBLE);
            for (ExampleSentence ex : grammarPoint.getExamples()) {
                View exView = LayoutInflater.from(context).inflate(R.layout.example_sentence_item, holder.exampleSentences_Layout, false);
                TextView sentence = exView.findViewById(R.id.exampleSentence_TextView);
                TextView reading = exView.findViewById(R.id.exampleReading_TextView);
                TextView meaning = exView.findViewById(R.id.exampleMeaning_TextView);
                sentence.setText(ex.getSentence());
                reading.setText(ex.getReading());
                meaning.setText(ex.getMeaning());
                holder.exampleSentences_Layout.addView(exView);
            }
        } else {
            holder.exampleSentences_Layout.setVisibility(View.VISIBLE);
            // Hiển thị dòng 'Ví dụ: ...' khi chưa expand
            TextView viDuText = new TextView(context);
            viDuText.setText("Ví dụ: ...");
            viDuText.setTextSize(15);
            viDuText.setTextColor(0xFF888888); // màu xám nhẹ
            holder.exampleSentences_Layout.addView(viDuText);
        }

        holder.itemView.setOnClickListener(v -> {
            grammarPoint.setExpanded(!grammarPoint.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return grammarPointList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView simplify_TextView, wordContent_TextView, pronunciation_TextView, meaning_TextView;
        LinearLayout exampleSentences_Layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            simplify_TextView = itemView.findViewById(R.id.simplify_TextView);
            wordContent_TextView = itemView.findViewById(R.id.wordContent_TextView);
            pronunciation_TextView = itemView.findViewById(R.id.pronunciation_TextView);
            meaning_TextView = itemView.findViewById(R.id.meaning_TextView);
            exampleSentences_Layout = itemView.findViewById(R.id.exampleSentences_Layout);
        }
    }
}
