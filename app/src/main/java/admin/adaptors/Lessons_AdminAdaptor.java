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

public class Lessons_AdminAdaptor extends RecyclerView.Adapter<Lessons_AdminAdaptor.ViewHolder> {

    private Context context;
    private List<AdminLessonChoices> vocavularyLessonList;

    public Lessons_AdminAdaptor(Context context, List<AdminLessonChoices> vocavularyLessonList) {
        this.context = context;
        this.vocavularyLessonList = vocavularyLessonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_vocabulary_lesson_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DBQuery.g_selectedTest_index = position;
        AdminLessonChoices item = vocavularyLessonList.get(position);

        holder.lessonTitle_TextView.setText(item.getLessonTitle());
        holder.lessonName_TextView.setText(item.getLessonName());

        holder.itemView.setOnClickListener( v -> showEditDeleteDialog(item, position));
    }


    @Override
    public int getItemCount() {
        return vocavularyLessonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lessonTitle_TextView, lessonName_TextView;;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lessonTitle_TextView = itemView.findViewById(R.id.lessonTitle_TextView);
            lessonName_TextView = itemView.findViewById(R.id.lessonName_TextView);
        }
    }

    private void showEditDeleteDialog(AdminLessonChoices word, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chỉnh sửa/Xóa bài học");
        builder.setMessage("Bạn muốn chỉnh sửa hay xóa bài học?");

        builder.setPositiveButton("Chỉnh sửa", (dialog, which) -> showEditLessonDialog(word, position));
        builder.setNegativeButton("Xóa", (dialog, which) -> {
            String lessonID = vocavularyLessonList.get(position).getLessonID(); // Lấy ID của bài học cần xóa
            String lessonTitle = vocavularyLessonList.get(position).getLessonTitle(); // Lấy tiêu đề của bài học cần xóa

            vocavularyLessonList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, vocavularyLessonList.size()); // Cập nhật danh sách sau khi xóa

            Admin_DBQuery.deleteVocabularyLesson(lessonTitle, lessonID, new MyCompleteListener() {
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

    // Hiển thị dialog sửa bài học
    private void showEditLessonDialog(AdminLessonChoices lesson, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chỉnh sửa bài học");

        View view = LayoutInflater.from(context).inflate(R.layout.add_vocabulary_lesson_dialog, null);
        EditText lessonTitle_EditText = view.findViewById(R.id.lessonTitle_EditText);
        EditText lessonName_EditText = view.findViewById(R.id.lessonName_EditText);
        EditText lessonImage_EditText = view.findViewById(R.id.lessonImage_EditText);

        lessonTitle_EditText.setEnabled(false); // Đảm bảo không sửa được tiêu đề

        // Lấy thông tin cần cho chỉnh sửa
        String lessonID = vocavularyLessonList.get(position).getLessonID();
        String lessonTitle = vocavularyLessonList.get(position).getLessonTitle();

        lessonTitle_EditText.setText(lessonTitle);// Gán lại giá trị cho lessonTitle (giá trị không thể sửa được)
        lessonName_EditText.setText(lesson.getLessonName());
        lessonImage_EditText.setText(lesson.getLessonImage());

        builder.setView(view);
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newLessonName = lessonName_EditText.getText().toString().trim();
            String newLessonImage = lessonImage_EditText.getText().toString().trim();

            if(!newLessonName.isEmpty() && !newLessonImage.isEmpty()){
                Admin_DBQuery.updateVocabularyLesson(lessonTitle, lessonID, newLessonName, newLessonImage, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        // Cập nhật từng item trong bài học
                        lesson.setLessonName(newLessonName);
                        lesson.setLessonImage(newLessonImage);

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
