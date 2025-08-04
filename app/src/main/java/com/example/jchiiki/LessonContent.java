package com.example.jchiiki;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.adaptors.LessonContent_Adaptor;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.decorations.SpaceItemDecoration;
import com.example.jchiiki.helper.OfflineContract;
import com.example.jchiiki.helper.OfflineDBHelper;
import com.example.jchiiki.items.LessonChoices;
import com.example.jchiiki.items.VocabularyWords;
import com.example.jchiiki.items.GrammarPoint;
import com.example.jchiiki.items.ExampleSentence;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LessonContent extends AppCompatActivity {
    Dialog progressDialog;
    TextView lessonName_TextView;
    ImageView back_ImageView, lessonMode_ImageView;
    RecyclerView lessonContent_RecyclerView;
    LessonContent_Adaptor lessonContent_Adaptor;
    Button practiceButton;
    ImageButton downloadButton;
    private OfflineDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_content);

        dbHelper = new OfflineDBHelper(this);

        back_ImageView = findViewById(R.id.back_ImageView);
        lessonMode_ImageView = findViewById(R.id.lessonContent_ImageView);
        lessonName_TextView = findViewById(R.id.lessonName_TextView);
        lessonContent_RecyclerView = findViewById(R.id.lessonContent_RecyclerView);
        practiceButton = findViewById(R.id.practice_Button);
        downloadButton = findViewById(R.id.download_Button);

        // Vô hiệu hóa các nút cho đến khi dữ liệu được tải
        practiceButton.setEnabled(false);
        downloadButton.setEnabled(false);

        /// Khởi tạo Dialog khi đang tải các câu hỏi
        progressDialog = new Dialog(LessonContent.this);
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog
        progressDialog.show();

        /// Xử lý sự kiện khi nhấn back_ImageView
        back_ImageView.setOnClickListener(v -> finish());

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LessonContent.this, Questions.class);
                startActivity(intent);
            }
        });

        downloadButton.setOnClickListener(v -> downloadLesson());

        // Lấy lessonId đúng với Firestore của bạn
        String lessonId = "lesson_1"; // Đúng với document bạn đã tạo
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("grammar_le...") // Đúng với tên collection bạn đã tạo, ví dụ: "grammar_lessons"
            .document(lessonId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    ArrayList<GrammarPoint> grammarPoints = new ArrayList<>();
                    // Đổi tên field cho đúng: "grammar points"
                    java.util.List<Map<String, Object>> grammarPointsData = (java.util.List<Map<String, Object>>) documentSnapshot.get("grammar points");
                    if (grammarPointsData != null) {
                        for (Map<String, Object> gpData : grammarPointsData) {
                            String title = (String) gpData.get("title");
                            String explanation = (String) gpData.get("explanation");
                            ArrayList<ExampleSentence> examples = new ArrayList<>();
                            java.util.List<Map<String, Object>> examplesData = (java.util.List<Map<String, Object>>) gpData.get("examples");
                            if (examplesData != null) {
                                for (Map<String, Object> exData : examplesData) {
                                    String sentence = (String) exData.get("sentence");
                                    String reading = (String) exData.get("reading");
                                    String meaning = (String) exData.get("meaning");
                                    examples.add(new ExampleSentence(sentence, reading, meaning));
                                }
                            }
                            grammarPoints.add(new GrammarPoint(title, explanation, examples));
                        }
                    }
                    lessonContent_Adaptor = new LessonContent_Adaptor(LessonContent.this, grammarPoints);
                    lessonContent_RecyclerView.setLayoutManager(new LinearLayoutManager(LessonContent.this));
                    lessonContent_RecyclerView.addItemDecoration(new com.example.jchiiki.decorations.SpaceItemDecoration(10));
                    lessonContent_RecyclerView.setAdapter(lessonContent_Adaptor);
                    lessonContent_Adaptor.notifyDataSetChanged();
                    practiceButton.setEnabled(true);
                    downloadButton.setEnabled(true);
                } else {
                    Toast.makeText(LessonContent.this, "Bài học này chưa có dữ liệu ngữ pháp.", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(LessonContent.this, "Lỗi khi tải dữ liệu từ Firebase.", Toast.LENGTH_SHORT).show();
            });
    }

    private void downloadLesson() {
        LessonChoices currentLesson = DBQuery.g_vocabularyLessonList.get(DBQuery.g_selectedLesson_index);
        ArrayList<VocabularyWords> words = DBQuery.g_lessonContentList;

        if (currentLesson == null || words == null || words.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để tải.", Toast.LENGTH_SHORT).show();
            Log.e("DownloadLesson", "Dữ liệu bài học hoặc từ vựng rỗng.");
            return;
        }
        Log.d("DownloadLesson", "Bắt đầu tải bài học: " + currentLesson.getLessonName() + " với " + words.size() + " từ.");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the lesson is already downloaded
        Cursor cursor = db.query(OfflineContract.LessonEntry.TABLE_NAME,
                new String[]{OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID},
                OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID + " = ?",
                new String[]{currentLesson.getLessonID()}, null, null, null);

        if (cursor.getCount() > 0) {
            Toast.makeText(this, "Bài học đã được tải xuống.", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        cursor.close();

        // Use a transaction for atomic and efficient insertion
        db.beginTransaction();
        try {
            // Insert Lesson
            ContentValues lessonValues = new ContentValues();
            lessonValues.put(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID, currentLesson.getLessonID());
            lessonValues.put(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_NAME, currentLesson.getLessonName());
            lessonValues.put(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_IMAGE, currentLesson.getLessonImage());
            long lessonRowId = db.insert(OfflineContract.LessonEntry.TABLE_NAME, null, lessonValues);
            Log.d("DownloadLesson", "Đã chèn bài học, ID hàng: " + lessonRowId);

            // Insert Words
            for (VocabularyWords word : words) {
                ContentValues wordValues = new ContentValues();
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_LESSON_ID_FK, currentLesson.getLessonID());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_WORD_CONTENT, word.getWordContent());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_SIMPLIFIED_CONTENT, word.getSimplifiedContent());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_PRONUNCIATION, word.getPronunciation());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_MEANING, word.getMeaning());
                db.insert(OfflineContract.WordEntry.TABLE_NAME, null, wordValues);
            }
            Log.d("DownloadLesson", "Đã chèn xong " + words.size() + " từ.");

            db.setTransactionSuccessful();
            Toast.makeText(this, "Tải xuống thành công!", Toast.LENGTH_SHORT).show();
            Log.d("DownloadLesson", "Giao dịch thành công.");
        } catch (Exception e) {
            Log.e("DownloadLesson", "Lỗi trong quá trình giao dịch: " + e.getMessage());
        }
        finally {
            db.endTransaction();
        }
    }

    // Hàm static cho phép nạp dữ liệu vào SQLite từ nơi khác
    public static void downloadLessonStatic(android.content.Context context, OfflineDBHelper dbHelper) {
        if (DBQuery.g_selectedLesson_index >= DBQuery.g_vocabularyLessonList.size()) return;
        LessonChoices currentLesson = DBQuery.g_vocabularyLessonList.get(DBQuery.g_selectedLesson_index);
        ArrayList<VocabularyWords> words = DBQuery.g_lessonContentList;

        if (currentLesson == null) return;
        if (words == null || words.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Kiểm tra nếu đã download bài học
        Cursor cursor = db.query(OfflineContract.LessonEntry.TABLE_NAME,
                new String[]{OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID},
                OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID + " = ?",
                new String[]{currentLesson.getLessonID()}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        cursor.close();
        db.beginTransaction();
        try {
            ContentValues lessonValues = new ContentValues();
            lessonValues.put(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID, currentLesson.getLessonID());
            lessonValues.put(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_NAME, currentLesson.getLessonName());
            lessonValues.put(OfflineContract.LessonEntry.COLUMN_NAME_LESSON_IMAGE, currentLesson.getLessonImage());
            db.insert(OfflineContract.LessonEntry.TABLE_NAME, null, lessonValues);
            for (VocabularyWords word : words) {
                ContentValues wordValues = new ContentValues();
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_LESSON_ID_FK, currentLesson.getLessonID());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_WORD_CONTENT, word.getWordContent());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_SIMPLIFIED_CONTENT, word.getSimplifiedContent());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_PRONUNCIATION, word.getPronunciation());
                wordValues.put(OfflineContract.WordEntry.COLUMN_NAME_MEANING, word.getMeaning());
                db.insert(OfflineContract.WordEntry.TABLE_NAME, null, wordValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // ignore
        } finally {
            db.endTransaction();
        }
    }

    public void addSampleGrammarLessonToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo dữ liệu mẫu cho bài học
        Map<String, Object> lesson = new HashMap<>();
        lesson.put("title", "Bài 1");

        List<Map<String, Object>> grammarPoints = new ArrayList<>();

        // Ngữ pháp 1
        Map<String, Object> grammar1 = new HashMap<>();
        grammar1.put("title", "こんにちは");
        grammar1.put("explanation", "konnichiwa\nXin chào.");

        List<Map<String, Object>> examples1 = new ArrayList<>();
        Map<String, Object> ex1 = new HashMap<>();
        ex1.put("sentence", "こんにちは、お父さん");
        ex1.put("reading", "konnichiwa, otousan");
        ex1.put("meaning", "Xin chào, bố.");
        examples1.add(ex1);
        grammar1.put("examples", examples1);

        grammarPoints.add(grammar1);

        // Ngữ pháp 2
        Map<String, Object> grammar2 = new HashMap<>();
        grammar2.put("title", "おはよう");
        grammar2.put("explanation", "ohayou\nChào buổi sáng.");

        List<Map<String, Object>> examples2 = new ArrayList<>();
        Map<String, Object> ex2 = new HashMap<>();
        ex2.put("sentence", "おはようございます");
        ex2.put("reading", "ohayou gozaimasu");
        ex2.put("meaning", "Chào buổi sáng.");
        examples2.add(ex2);
        grammar2.put("examples", examples2);

        grammarPoints.add(grammar2);

        lesson.put("grammarPoints", grammarPoints);

        // Ghi lên Firestore (document: lesson1)
        db.collection("grammar_le...") // Đúng với tên collection bạn đã tạo, ví dụ: "grammar_lessons"
            .document("lesson1")
            .set(lesson)
            .addOnSuccessListener(aVoid -> {
                // Thành công
                Log.d("Firestore", "Thêm dữ liệu mẫu thành công!");
            })
            .addOnFailureListener(e -> {
                // Thất bại
                Log.e("Firestore", "Lỗi khi thêm dữ liệu mẫu", e);
            });
    }
}