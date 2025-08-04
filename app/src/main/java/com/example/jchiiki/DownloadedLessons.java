package com.example.jchiiki;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.adaptors.DownloadedLessons_Adaptor;
import com.example.jchiiki.decorations.SpaceItemDecoration;
import com.example.jchiiki.helper.OfflineContract;
import com.example.jchiiki.helper.OfflineDBHelper;
import com.example.jchiiki.items.LessonChoices;
import com.example.jchiiki.callback.MyCompleteListener;

import java.util.ArrayList;

public class DownloadedLessons extends AppCompatActivity implements DownloadedLessons_Adaptor.OnDeleteClickListener {

    private RecyclerView downloadedLessonsRecyclerView;
    private ImageView backImageView;
    private OfflineDBHelper dbHelper;
    private DownloadedLessons_Adaptor adapter;
    private ArrayList<LessonChoices> downloadedLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_lessons);

        dbHelper = new OfflineDBHelper(this);

        // Kiểm tra nếu database trống thì tự động nạp lại dữ liệu mẫu
        if (isDatabaseEmpty()) {
            DBQuery.loadVocabularyLessons(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    // Tải từng bài học và từ vựng về SQLite
                    for (int i = 0; i < DBQuery.g_vocabularyLessonList.size(); i++) {
                        DBQuery.g_selectedLesson_index = i;
                        // Giả lập như khi tải từng bài học về máy
                        LessonContent.downloadLessonStatic(DownloadedLessons.this, dbHelper);
                    }
                    // Sau khi nạp xong, load lại danh sách
                    loadDownloadedLessons();
                }
                @Override
                public void onFailure() {
                    Toast.makeText(DownloadedLessons.this, "Không thể nạp lại dữ liệu mẫu từ server!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        downloadedLessonsRecyclerView = findViewById(R.id.downloaded_lessons_RecyclerView);
        backImageView = findViewById(R.id.back_ImageView);

        backImageView.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadDownloadedLessons();
    }

    private void setupRecyclerView() {
        downloadedLessons = new ArrayList<>();
        adapter = new DownloadedLessons_Adaptor(this, downloadedLessons);
        adapter.setOnDeleteClickListener(this);
        downloadedLessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        downloadedLessonsRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        downloadedLessonsRecyclerView.setAdapter(adapter);
    }

    private void loadDownloadedLessons() {
        Log.d("DownloadedLessons", "Bắt đầu tải các bài học từ SQLite.");
        downloadedLessons.clear(); // Xóa dữ liệu cũ trước khi tải mới

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                OfflineContract.LessonEntry.TABLE_NAME,
                null, // null selects all columns
                null,
                null,
                null,
                null,
                OfflineContract.LessonEntry.COLUMN_NAME_LESSON_NAME + " ASC"
        );

        Log.d("DownloadedLessons", "Tìm thấy " + cursor.getCount() + " bài học trong DB.");

        while(cursor.moveToNext()) {
            String lessonId = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID));
            String lessonName = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_NAME));
            String lessonImage = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_IMAGE));

            Log.d("DownloadedLessons", "Đang tải: " + lessonName);

            // The constructor expects (lessonID, lessonTitle, lessonName, lessonImage)
            // We don't have a title in the DB, so we pass an empty string for now.
            downloadedLessons.add(new LessonChoices(lessonId, "", lessonName, lessonImage));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        Log.d("DownloadedLessons", "Đã cập nhật Adapter.");
    }

    // Hàm kiểm tra database trống
    private boolean isDatabaseEmpty() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(OfflineContract.LessonEntry.TABLE_NAME, null, null, null, null, null, null);
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();
        return isEmpty;
    }

    @Override
    public void onDeleteClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bài học")
                .setMessage("Bạn có chắc chắn muốn xóa bài học này không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteLesson(position);
                })
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteLesson(int position) {
        if (position >= 0 && position < downloadedLessons.size()) {
            String lessonIdToDelete = downloadedLessons.get(position).getLessonID();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                db.beginTransaction();

                // Delete vocabulary associated with the lesson
                String selectionVocab = OfflineContract.WordEntry.COLUMN_NAME_LESSON_ID_FK + " = ?";
                String[] selectionArgsVocab = { lessonIdToDelete };
                db.delete(OfflineContract.WordEntry.TABLE_NAME, selectionVocab, selectionArgsVocab);

                // Delete the lesson itself
                String selectionLesson = OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID + " = ?";
                String[] selectionArgsLesson = { lessonIdToDelete };
                db.delete(OfflineContract.LessonEntry.TABLE_NAME, selectionLesson, selectionArgsLesson);

                db.setTransactionSuccessful();
                Log.d("DownloadedLessons", "Đã xóa bài học: " + lessonIdToDelete);

                // Remove from list and notify adapter
                downloadedLessons.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, downloadedLessons.size());

                Toast.makeText(this, "Đã xóa bài học", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("DownloadedLessons", "Lỗi khi xóa bài học: " + lessonIdToDelete, e);
                Toast.makeText(this, "Lỗi khi xóa bài học", Toast.LENGTH_SHORT).show();
            } finally {
                db.endTransaction();
            }
        }
    }
}