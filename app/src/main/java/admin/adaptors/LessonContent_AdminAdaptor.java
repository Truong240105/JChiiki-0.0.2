package admin.adaptors;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.ActivityCallsFragments;
import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.Ready;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.items.SummarizedPracticeChoices;

import java.util.List;

import admin.Admin_DBQuery;
import admin.items.AdminLessonChoices;
import admin.items.AdminVocabularyWords;

public class LessonContent_AdminAdaptor extends RecyclerView.Adapter<LessonContent_AdminAdaptor.ViewHolder> {

    private Context context;
    private List<AdminVocabularyWords> vocavularyWordList;
    private String lessonTitle;

    public LessonContent_AdminAdaptor(Context context, List<AdminVocabularyWords> vocabularyWordList, String lessonTitle) {
        this.context = context;
        this.vocavularyWordList = vocabularyWordList;
        this.lessonTitle = lessonTitle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vocabulary_word_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DBQuery.g_selectedTest_index = position;
        AdminVocabularyWords item = vocavularyWordList.get(position);

        String wordContent = item.getWordContent();
        String simplifiedContent = item.getSimplifiedContent();
        String pronunciation = item.getPronunciation();
        String meaning = item.getMeaning();

        holder.wordContent_EditText.setText(wordContent);
        holder.simplifiedContent_EditText.setText(simplifiedContent);
        holder.pronunciation_EditText.setText(pronunciation);
        holder.meaning_EditText.setText(meaning);


        holder.itemView.setOnClickListener( v -> showEditDeleteDialog(item, position));
    }


    @Override
    public int getItemCount() {
        return vocavularyWordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView wordContent_EditText, simplifiedContent_EditText, pronunciation_EditText, meaning_EditText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wordContent_EditText = itemView.findViewById(R.id.wordContent_TextView);
            simplifiedContent_EditText = itemView.findViewById(R.id.simplify_TextView);
            pronunciation_EditText = itemView.findViewById(R.id.pronunciation_TextView);
            meaning_EditText = itemView.findViewById(R.id.meaning_TextView);
        }
    }

    private void showEditDeleteDialog(AdminVocabularyWords word, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chỉnh sửa/Xóa từ vựng");
        builder.setMessage("Bạn muốn chỉnh sửa hay xóa từ vựng?");

        builder.setPositiveButton("Chỉnh sửa", (dialog, which) -> showEditWordDialog(word, position));
        builder.setNegativeButton("Xóa", (dialog, which) -> {
            String wordID = vocavularyWordList.get(position).getWordID(); // Lấy ID của từ vựng cần xóa

            vocavularyWordList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, vocavularyWordList.size()); // Cập nhật danh sách sau khi xóa

            Admin_DBQuery.deleteLessonContent(lessonTitle, wordID, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure() {
                    Toast.makeText(context, "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.create().show();
    }

    // Hiển thị dialog sửa từ vựng
    private void showEditWordDialog(AdminVocabularyWords word, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chỉnh sửa từ vựng");

        View view = LayoutInflater.from(context).inflate(R.layout.add_word_dialog, null);
        EditText content_EditText = view.findViewById(R.id.content_EditText);
        EditText simplifiedContent_EditText = view.findViewById(R.id.simplifiedContent_EditText);
        EditText pronunciation_EditText = view.findViewById(R.id.pronunciation_EditText);
        EditText meaning_EditText = view.findViewById(R.id.meaning_EditText);

        // Lấy thông tin cần cho chỉnh sửa
        String wordContent = vocavularyWordList.get(position).getWordContent();
        String simplifiedContent = vocavularyWordList.get(position).getSimplifiedContent();
        String pronunciation = vocavularyWordList.get(position).getPronunciation();
        String meaning = vocavularyWordList.get(position).getMeaning();

        content_EditText.setText(wordContent);
        simplifiedContent_EditText.setText(simplifiedContent);
        pronunciation_EditText.setText(pronunciation);
        meaning_EditText.setText(meaning);

        builder.setView(view);
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newWordContent = content_EditText.getText().toString().trim();
            String newSimplifiedContent = simplifiedContent_EditText.getText().toString().trim();
            String newPronunciation = pronunciation_EditText.getText().toString().trim();
            String newMeaning = meaning_EditText.getText().toString().trim();
            String wordID = word.getWordID();

            if(!newWordContent.isEmpty() && !newPronunciation.isEmpty() && !newMeaning.isEmpty()){
                Admin_DBQuery.updateLessonContent(lessonTitle, wordID, newWordContent, newSimplifiedContent, newPronunciation, newMeaning, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        // Cập nhật từng item trong từ vựng
                        word.setWordContent(newWordContent);
                        word.setSimplifiedContent(newSimplifiedContent);
                        word.setPronunciation(newPronunciation);
                        word.setMeaning(newMeaning);

                        notifyDataSetChanged();
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(context, "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }

        });

        builder.create().show();
    }
}
