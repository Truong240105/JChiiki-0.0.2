package com.example.jchiiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jchiiki.callback.MyCompleteListener;

public class Score extends AppCompatActivity {
    TextView score_TextView, warning_TextView;
    Button backToHome_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        //// Khởi tạo các thành phần giao diện
        score_TextView = findViewById(R.id.score_TextView);
        warning_TextView = findViewById(R.id.warning_TextView);
        backToHome_Button = findViewById(R.id.backToHome_Button);

        //// Xử lý lấy điểm của người dùng
        // Lấy số lượng câu hỏi từ Intent
        Intent intent = getIntent();
        String stringQuestionNumber = (intent.getStringExtra("questionNumber")); // Lấy số lượng câu hỏi đã chọn từ Intent
        int questionNumber = Integer.parseInt(stringQuestionNumber); // Chuyển đổi sang kiểu số nguyên
        Log.d("questionNumber", String.valueOf(questionNumber));

        // Ẩn thông báo nếu không phải là bài kiểm tra tổng hợp
        if(DBQuery.g_practiceOptionChoice != "Summarized")
            warning_TextView.setVisibility(View.GONE);

        loadData(questionNumber);

        //// Xử lý sự kiện khi người dùng nhấn backToHome_Button
        backToHome_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Score.this, ActivityCallsFragments.class);
                startActivity(intent);
                Score.this.finish();
            }
        });
    }

    private void loadData(int questionNumber){
        int correctAnswerNumber = 0;

        switch (DBQuery.g_practiceOptionChoice){
            case "Vocabulary":
                for (int i = 0; i < questionNumber; i++) {
                    // Kiểm tra đáp án đúng
                    String correctAnswerString = DBQuery.g_vocabularyQuestionList.get(i).getCorrectAnswer().toString(); // Lấy đáp án đúng
                    String selectedAnswerString = DBQuery.g_vocabularyQuestionList.get(i).getSelectedAnswer().toString(); // Lấy đáp án đã chọn

                    if(selectedAnswerString.equals(correctAnswerString)){
                        correctAnswerNumber++;
                    }
                }
                break;
            case "Grammar":
                break;
            case "Listening":
                for (int i = 0; i < questionNumber; i++) {
                    // Kiểm tra đáp án đúng
                    String correctAnswerString = DBQuery.g_listeningQuestionList.get(i).getCorrectAnswer().toString(); // Lấy đáp án đúng
                    String selectedAnswerString = DBQuery.g_listeningQuestionList.get(i).getSelectedAnswer().toString(); // Lấy đáp án đã chọn

                    if(selectedAnswerString.equals(correctAnswerString)){
                        correctAnswerNumber++;
                    }
                }
                break;
            case "Speaking":

                break;
            case "Reading":

                break;
            case "Writing":

                break;
            case "Everyday":

                break;
            case "Summarized":
                for (int i = 0; i < questionNumber; i++) {
                    // Kiểm tra đáp án đúng
                    String correctAnswerString = DBQuery.g_summarizedPracticeQuestionList.get(i).getCorrectAnswer().toString(); // Lấy đáp án đúng
                    String selectedAnswerString = DBQuery.g_summarizedPracticeQuestionList.get(i).getSelectedAnswer().toString(); // Lấy đáp án đã chọn

                    if(selectedAnswerString.equals(correctAnswerString)){
                        correctAnswerNumber++;
                    }
                }
                break;
        }

        score_TextView.setText(correctAnswerNumber+"/"+questionNumber);

        // Gọi hàm lưu kết quả bài kiểm tra vào tài khoản người dùng
        switch (DBQuery.g_practiceOptionChoice){
            case "Vocabulary":
                saveVocabularyTestResult(correctAnswerNumber);
                break;
            case "Grammar":
                break;
            case "Listening":
                saveListeningTestResult(correctAnswerNumber);
                break;
            case "Speaking":

                break;
            case "Reading":

                break;
            case "Writing":

                break;
            case "Everyday":
                break;
            case "Summarized":
                saveSummarizedPracticeResult(correctAnswerNumber);
                break;
        }


    }
    private void saveVocabularyTestResult(int score){
        DBQuery.saveVocabularyLessonResult(score, new MyCompleteListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {
                Toast.makeText(Score.this, "Điểm chưa được lưu ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveListeningTestResult(int score){
        DBQuery.saveListeningLessonResult(score, new MyCompleteListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {
                Toast.makeText(Score.this, "Điểm chưa được lưu ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSummarizedPracticeResult(int score){
        // Lấy cấp độ ngôn ngữ cho việc lưu kết quả theo bài kiểm tra với trình độ tương ứng
        SharedPreferences languageLevelPreference = getSharedPreferences("LanguageLevel", MODE_PRIVATE);
        String languageLevelString = languageLevelPreference.getString("LanguageLevel", "");
        int languageLevel = Integer.parseInt(languageLevelString);

        // Lưu kết quả
        DBQuery.saveSummarizedPracticeResult(score, languageLevel, new MyCompleteListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {
                Toast.makeText(Score.this, "Điểm chưa được lưu ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}