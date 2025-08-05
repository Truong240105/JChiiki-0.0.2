package admin.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.R;
import com.example.jchiiki.items.GrammarLessonChoices;

import java.util.ArrayList;

public class GrammarLessons_AdminAdaptor extends RecyclerView.Adapter<GrammarLessons_AdminAdaptor.ViewHolder> {

    private Context context;
    private ArrayList<GrammarLessonChoices> grammarLessonArrayList;

    public GrammarLessons_AdminAdaptor(Context context, ArrayList<GrammarLessonChoices> grammarLessonArrayList) {
        this.context = context;
        this.grammarLessonArrayList = grammarLessonArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_grammar_lesson_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrammarLessonChoices grammarLesson = grammarLessonArrayList.get(position);

        holder.lessonTitle_TextView.setText(grammarLesson.getLessonTitle());
        holder.lessonName_TextView.setText(grammarLesson.getLessonName());

        holder.edit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement edit functionality
            }
        });

        holder.delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement delete functionality
            }
        });
    }

    @Override
    public int getItemCount() {
        return grammarLessonArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lessonTitle_TextView, lessonName_TextView;
        Button edit_Button, delete_Button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonTitle_TextView = itemView.findViewById(R.id.lessonTitle_TextView);
            lessonName_TextView = itemView.findViewById(R.id.lessonName_TextView);
            edit_Button = itemView.findViewById(R.id.edit_Button);
            delete_Button = itemView.findViewById(R.id.delete_Button);
        }
    }
} 