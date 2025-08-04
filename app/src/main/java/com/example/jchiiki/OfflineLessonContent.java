package com.example.jchiiki;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.adaptors.LessonContent_Adaptor;
import com.example.jchiiki.decorations.SpaceItemDecoration;
import com.example.jchiiki.helper.OfflineContract;
import com.example.jchiiki.helper.OfflineDBHelper;
import com.example.jchiiki.items.VocabularyWords;
import com.example.jchiiki.items.GrammarPoint;
import com.example.jchiiki.items.ExampleSentence;

import java.util.ArrayList;

public class OfflineLessonContent extends AppCompatActivity {

    public static final String EXTRA_LESSON_ID = "extra_lesson_id";
    public static final String EXTRA_LESSON_NAME = "extra_lesson_name";

    private RecyclerView wordsRecyclerView;
    private TextView lessonNameTextView;
    private ImageView backImageView;

    private OfflineDBHelper dbHelper;
    private LessonContent_Adaptor adapter;
    private ArrayList<VocabularyWords> wordList;
    private ArrayList<GrammarPoint> grammarPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_lesson_content);

        dbHelper = new OfflineDBHelper(this);

        wordsRecyclerView = findViewById(R.id.offline_words_RecyclerView);
        lessonNameTextView = findViewById(R.id.lessonName_TextView);
        backImageView = findViewById(R.id.back_ImageView);

        backImageView.setOnClickListener(v -> finish());

        String lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        String lessonName = getIntent().getStringExtra(EXTRA_LESSON_NAME);
        lessonNameTextView.setText(lessonName);

        setupRecyclerView();
        loadWordsForLesson(lessonId);
    }

    private void setupRecyclerView() {
        wordList = new ArrayList<>();
        grammarPoints = new ArrayList<>();
        adapter = new LessonContent_Adaptor(this, grammarPoints);
        wordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        wordsRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
        wordsRecyclerView.setAdapter(adapter);
    }

    private void loadWordsForLesson(String lessonId) {
        if (lessonId == null) return;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                OfflineContract.WordEntry.COLUMN_NAME_WORD_CONTENT,
                OfflineContract.WordEntry.COLUMN_NAME_SIMPLIFIED_CONTENT,
                OfflineContract.WordEntry.COLUMN_NAME_PRONUNCIATION,
                OfflineContract.WordEntry.COLUMN_NAME_MEANING
        };

        String selection = OfflineContract.WordEntry.COLUMN_NAME_LESSON_ID_FK + " = ?";
        String[] selectionArgs = { lessonId };

        Cursor cursor = db.query(
                OfflineContract.WordEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        wordList.clear();
        grammarPoints.clear();
        while(cursor.moveToNext()) {
            String wordContent = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.WordEntry.COLUMN_NAME_WORD_CONTENT));
            String simplifiedContent = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.WordEntry.COLUMN_NAME_SIMPLIFIED_CONTENT));
            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.WordEntry.COLUMN_NAME_PRONUNCIATION));
            String meaning = cursor.getString(cursor.getColumnIndexOrThrow(OfflineContract.WordEntry.COLUMN_NAME_MEANING));

            wordList.add(new VocabularyWords("", wordContent, simplifiedContent, pronunciation, meaning));
            grammarPoints.add(new GrammarPoint(wordContent, pronunciation + "\n" + meaning, new ArrayList<ExampleSentence>()));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}