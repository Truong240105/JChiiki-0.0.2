package com.example.jchiiki;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.jchiiki.adaptors.ListeningQuestions_Adaptor;
import com.example.jchiiki.adaptors.Questions_Adaptor;
import com.example.jchiiki.adaptors.SpeakingQuestions_Adaptor;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.callback.OnVoiceInputListener;

import java.util.ArrayList;
import java.util.Collections;

public class Questions extends AppCompatActivity {
    Dialog progressDialog;
    TextView dialogTextView, back_TextView, questionID_TextView;
    Button submit_Button;
    RecyclerView question_RecyclerView;
    Questions_Adaptor questionAdapter;
    ListeningQuestions_Adaptor listeningQuestionAdapter;
    SpeakingQuestions_Adaptor speakingQuestionAdapter;
    ImageButton previousQuestion_ImageButton, nextQuestion_Button;
    int questionPosition; // Biến lưu trữ vị trí của câu hỏi hiện tại trong danh sách
    String questionNumber = "0"; // Biến lưu trữ số lượng câu hỏi tuỳ theo chế độ học
    public static String Question_spokenText;
    public static int Question_similarity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        // Khởi tạo các thành phần giao diện
        back_TextView = findViewById(R.id.back_TextView);
        submit_Button = findViewById(R.id.submit_Button);
        questionID_TextView = findViewById(R.id.questionID_TextView);
        question_RecyclerView = findViewById(R.id.question_RecyclerView);
        previousQuestion_ImageButton = findViewById(R.id.previousQuestion_ImageButton);
        nextQuestion_Button = findViewById(R.id.nextQuestion_Button);
        questionPosition = 0;

        /// Khởi tạo Dialog khi đang tải các câu hỏi
        progressDialog = new Dialog(Questions.this);
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog

        // Đặt nội dung cho TextView trong Dialog
        dialogTextView = progressDialog.findViewById(R.id.dialogTextView);
        dialogTextView.setText("Đang tải ...");

        progressDialog.show();

        //// Xử lý sự kiện khi người dùng nhấn back_TextView
        back_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //// Các biến Xử lý hiển thị số thứ tự cho câu hỏi hiện tại
        Intent intent = getIntent();

        //// Xử lý Recycler View
        switch (DBQuery.g_practiceOptionChoice){
            case "Vocabulary":
                loadVocabularyQuestions();
                break;
            case "Grammar":
                break;
            case "Listening":
                loadListeningQuestions();
                break;
            case "Speaking":
                loadSpeakingQuestions();
                break;
            case "Reading":

                break;
            case "Writing":

                break;
            case "Everyday":
                break;
            case "Summarized":
                loadSummarizedPracticeQuestions();
                questionNumber = intent.getStringExtra("questionNumber"); // Lấy số lượng câu hỏi đã chọn từ Intent
                questionID_TextView.setText((questionPosition+1)+"/"+questionNumber);
                break;
        }

        // Thiết lập layout manager cho RecyclerView để hiển thị các mục câu hỏi theo chiều ngang
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // Đặt hướng hiển thị là ngang
        question_RecyclerView.setLayoutManager(layoutManager);

        /* Vì các item khi hiển thị theo chiều ngang có thể kéo thả tự do nên ta cần cố định lại,
        nghĩa là mỗi lần lướt chỉ sang 1 item khác chứ không lướt 1 đoạn qua nhiều item. Để làm được,
        ta cần có 1 Snap Helper */
        setSnapHelper();

        //// Xử lý sự kiện khi người dùng nhấn nút previousQuestion_ImageButton và nextQuestion_ImageButton
        setNavigateQuestionClickListener();

        //// Xử lý sự kiện khi người dùng nhấn submit_Button
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Questions.this, Score.class);
                intent.putExtra("questionNumber", questionNumber);
                startActivity(intent);
            }
        });
    }

    //////// ========= VOCABULARY LESSONS =========
    private void loadVocabularyQuestions(){
        DBQuery.loadVocabularyLessonQuestion(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Collections.shuffle(DBQuery.g_vocabularyQuestionList); // Xáo trộn danh sách câu hỏi

                questionAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(Questions.this, "Có lỗi xảy ra! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        // Khởi tạo adapter
        questionAdapter = new Questions_Adaptor(this, DBQuery.g_vocabularyQuestionList);
        question_RecyclerView.setAdapter(questionAdapter);
    }
    //////// ========= GRAMMAR LESSONS =========

    //////// ========= LISTENING LESSONS =========
    private void loadListeningQuestions(){
        // Tải các câu hỏi cho bài học nghe
        DBQuery.loadListeningLessonContent(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Collections.shuffle(DBQuery.g_listeningQuestionList); // Xáo trộn danh sách câu hỏi

                questionID_TextView.setText(questionPosition+1 + "/" + DBQuery.g_listeningQuestionList.size());

                listeningQuestionAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(Questions.this, "Có lỗi xảy ra! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        // Khởi tạo adapter
        listeningQuestionAdapter = new ListeningQuestions_Adaptor(this, DBQuery.g_listeningQuestionList);
        question_RecyclerView.setAdapter(listeningQuestionAdapter);
    }

    //////// ========= SPEAKING LESSONS =========
    private static final int REQUEST_CODE_SPEECH = 1;
    private int currentSpeakingPosition = -1;
    private void loadSpeakingQuestions(){
        DBQuery.loadSpeakingLessonContent(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Collections.shuffle(DBQuery.g_speakingQuestionList); // Xáo trộn danh sách câu hỏi

                Log.d("DBQuery.g_listeningQuestionList.size(): ", String.valueOf(DBQuery.g_speakingQuestionList.size()));
                questionID_TextView.setText(questionPosition+1 + "/" + DBQuery.g_speakingQuestionList.size());

                speakingQuestionAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(Questions.this, "Có lỗi xảy ra! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        speakingQuestionAdapter = new SpeakingQuestions_Adaptor(this, DBQuery.g_speakingQuestionList, new OnVoiceInputListener() {
            @Override
            public void onVoiceInputRequested(int position) {
                currentSpeakingPosition = position;

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "話してください…");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH);
                } else {
                    Toast.makeText(Questions.this, "Thiết bị không hỗ trợ nhận diện giọng nói.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        question_RecyclerView.setAdapter(speakingQuestionAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);

            Question_spokenText = spokenText;

            String originalText = DBQuery.g_speakingQuestionList.get(currentSpeakingPosition).getPronunciation();
            int similarity = calculateSimilarity(spokenText, originalText);
            Question_similarity = similarity;

            speakingQuestionAdapter.notifyItemChanged(currentSpeakingPosition);
        }
    }

    // So sánh độ giống nhau đơn giản theo ký tự (Levenshtein Distance)
    private int calculateSimilarity(String a, String b) {
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) return 100;
        int dist = levenshtein(a, b);
        return (100 * (maxLen - dist)) / maxLen;
    }

    // Hàm tính khoảng cách Levenshtein
    private int levenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++)
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
            }

        return dp[s1.length()][s2.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        int min = Integer.MAX_VALUE;
        for (int n : numbers) {
            if (n < min) min = n;
        }
        return min;
    }

    //////// ========= READING LESSONS =========

    //////// ========= WRITING LESSONS =========

    //////// ========= SUMMARIZED PRACTICES =========
    private void loadSummarizedPracticeQuestions(){
        // Lấy cấp độ ngôn ngữ từ SharedPreferences
        SharedPreferences languageLevelPreference = getSharedPreferences("LanguageLevel", MODE_PRIVATE);
        String languageLevel = languageLevelPreference.getString("LanguageLevel", "");
        int level;
        if(languageLevel.isEmpty()){
            level = 5;
        }
        else level = Integer.parseInt(languageLevel);

        // Tải các câu hỏi cho bài test tương ứng với cấp độ ngôn ngữ và bài thứ mấy
        DBQuery.loadSummarizedPracticeQuestions(level, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Collections.shuffle(DBQuery.g_summarizedPracticeQuestionList); // Xáo trộn danh sách câu hỏi

                questionAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(Questions.this, "Có lỗi xảy ra! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        // Khởi tạo adapter
        questionAdapter = new Questions_Adaptor(this, DBQuery.g_summarizedPracticeQuestionList);
        question_RecyclerView.setAdapter(questionAdapter);

    }

    //////// ========= OTHERS =========
    private void setSnapHelper(){
        SnapHelper snapHelper = new androidx.recyclerview.widget.PagerSnapHelper();
        snapHelper.attachToRecyclerView(question_RecyclerView); // Gán Snap Helper vào Recycler View

        question_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //// Hàm dùng thay đổi trạng thái của Recycler View
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Intent intent = getIntent();

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                questionPosition = recyclerView.getLayoutManager().getPosition(view);

                // Cập nhật số thứ tự cho câu hỏi hiện tại
                switch (DBQuery.g_practiceOptionChoice){
                    case "Vocabulary":
                        questionID_TextView.setText(questionPosition+1 + "/" + DBQuery.g_vocabularyQuestionList.size());
                        questionNumber = DBQuery.g_vocabularyQuestionList.size() + "";
                        break;
                    case "Grammar":
                        break;
                    case "Listening":
                        questionID_TextView.setText(questionPosition+1 + "/" + DBQuery.g_listeningQuestionList.size());
                        questionNumber = DBQuery.g_listeningQuestionList.size() + "";
                        break;
                    case "Speaking":
                        questionID_TextView.setText(questionPosition+1 + "/" + DBQuery.g_speakingQuestionList.size());
                        questionNumber = DBQuery.g_speakingQuestionList.size() + "";
                        break;
                    case "Reading":

                        break;
                    case "Writing":

                        break;
                        case "Everyday":

                        break;
                    case "Summarized":
                        questionID_TextView.setText(questionPosition+1 + "/" + DBQuery.g_summarizedPracticeQuestionList.size());
                        questionNumber = intent.getStringExtra("questionNumber");
                        break;
                }

                // Đặt màu background cho nút previousQuestion_ImageButton và nextQuestion_ImageButton
                previousQuestion_ImageButton.setBackgroundColor(getResources().getColor(R.color.white));

                if(questionPosition == 0){
                    previousQuestion_ImageButton.setBackgroundColor(getResources().getColor(R.color.darkgray));
                }
                else if(questionPosition == Integer.parseInt(questionNumber)-1){
                    nextQuestion_Button.setBackgroundColor(getResources().getColor(R.color.darkgray));
                }else{
                    previousQuestion_ImageButton.setBackgroundColor(getResources().getColor(R.color.white));
                    nextQuestion_Button.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }

            //// Hàm dùng để kiểm tra vị trí hiện tại của Recycler View khi người dùng lướt
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    // Hàm dùng để thiết lập sự kiện khi người dùng nhấn nút previousQuestion_ImageButton và nextQuestion_ImageButton
    private void setNavigateQuestionClickListener() {
        Log.d("questionID: ", String.valueOf(questionPosition));
        previousQuestion_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionPosition > 0) {
                    question_RecyclerView.smoothScrollToPosition(questionPosition-1); // Cuộn đến vị trí câu hỏi trước đó
                }
            }
        });
        nextQuestion_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                switch (DBQuery.g_practiceOptionChoice){
                    case "Vocabulary":
                        questionNumber = DBQuery.g_vocabularyQuestionList.size() + "";
                        break;
                    case "Grammar":
                        break;
                    case "Listening":
                        questionNumber = DBQuery.g_listeningQuestionList.size() + "";
                        break;
                    case "Speaking":
                        questionNumber = DBQuery.g_speakingQuestionList.size() + "";
                        break;
                    case "Reading":

                        break;
                    case "Writing":

                        break;
                        case "Everyday":

                        break;
                    case "Summarized":
                        questionNumber = intent.getStringExtra("questionNumber"); // Lấy số lượng câu hỏi đã chọn từ Intent
                        break;
                }

                if (questionPosition < Integer.parseInt(questionNumber)-1) {
                    question_RecyclerView.smoothScrollToPosition(questionPosition+1); // Cuộn đến vị trí câu hỏi tiếp theo
                }
            }
        });
    }
}