package com.example.jchiiki.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.OfflineLessonContent;
import com.example.jchiiki.R;
import com.example.jchiiki.items.LessonChoices;

import java.util.ArrayList;

public class DownloadedLessons_Adaptor extends RecyclerView.Adapter<DownloadedLessons_Adaptor.ViewHolder> {

    private final ArrayList<LessonChoices> downloadedLessons;
    private final Context context;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public DownloadedLessons_Adaptor(Context context, ArrayList<LessonChoices> downloadedLessons) {
        this.context = context;
        this.downloadedLessons = downloadedLessons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloaded_lesson_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonChoices lesson = downloadedLessons.get(position);
        holder.lessonNameTextView.setText(lesson.getLessonName());
        holder.lessonTitleTextView.setText(lesson.getLessonID());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OfflineLessonContent.class);
            intent.putExtra(OfflineLessonContent.EXTRA_LESSON_ID, lesson.getLessonID());
            intent.putExtra(OfflineLessonContent.EXTRA_LESSON_NAME, lesson.getLessonName());
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                int positionToDelete = holder.getAdapterPosition();
                if (positionToDelete != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(positionToDelete);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadedLessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lessonNameTextView;
        TextView lessonTitleTextView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonNameTextView = itemView.findViewById(R.id.lessonName_TextView);
            lessonTitleTextView = itemView.findViewById(R.id.lessonTitle_TextView);
            deleteButton = itemView.findViewById(R.id.delete_Button);
        }
    }
} 