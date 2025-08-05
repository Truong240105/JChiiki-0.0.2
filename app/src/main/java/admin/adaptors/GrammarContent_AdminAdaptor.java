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
import admin.items.AdminGrammarPoint;

import java.util.ArrayList;

public class GrammarContent_AdminAdaptor extends RecyclerView.Adapter<GrammarContent_AdminAdaptor.ViewHolder> {

    private Context context;
    private ArrayList<AdminGrammarPoint> grammarPointArrayList;
    private String lessonTitle;

    public GrammarContent_AdminAdaptor(Context context, ArrayList<AdminGrammarPoint> grammarPointArrayList, String lessonTitle) {
        this.context = context;
        this.grammarPointArrayList = grammarPointArrayList;
        this.lessonTitle = lessonTitle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_grammar_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminGrammarPoint grammarPoint = grammarPointArrayList.get(position);

        holder.grammarPoint_TextView.setText(grammarPoint.getGrammarPoint());
        holder.explanation_TextView.setText(grammarPoint.getExplanation());
        holder.example_TextView.setText(grammarPoint.getExample());
        holder.translation_TextView.setText(grammarPoint.getTranslation());

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
        return grammarPointArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView grammarPoint_TextView, explanation_TextView, example_TextView, translation_TextView;
        Button edit_Button, delete_Button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            grammarPoint_TextView = itemView.findViewById(R.id.grammarPoint_TextView);
            explanation_TextView = itemView.findViewById(R.id.explanation_TextView);
            example_TextView = itemView.findViewById(R.id.example_TextView);
            translation_TextView = itemView.findViewById(R.id.translation_TextView);
            edit_Button = itemView.findViewById(R.id.edit_Button);
            delete_Button = itemView.findViewById(R.id.delete_Button);
        }
    }
} 