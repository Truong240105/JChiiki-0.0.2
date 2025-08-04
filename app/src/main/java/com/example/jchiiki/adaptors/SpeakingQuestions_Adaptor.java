package com.example.jchiiki.adaptors;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.Questions;
import com.example.jchiiki.R;
import com.example.jchiiki.callback.OnVoiceInputListener;
import com.example.jchiiki.items.QuestionItems;

import java.util.List;
import java.util.Locale;

public class SpeakingQuestions_Adaptor extends RecyclerView.Adapter<SpeakingQuestions_Adaptor.ViewHolder> {
    private Context context;
    private static List<QuestionItems> questionList;
    public OnVoiceInputListener onVoiceInputListener;
    public SpeakingQuestions_Adaptor(Context context, List<QuestionItems> questionList, OnVoiceInputListener onVoiceInputListener) {
        this.context = context;
        this.questionList = questionList;
        this.onVoiceInputListener = onVoiceInputListener;
    }

    @NonNull
    @Override
    public SpeakingQuestions_Adaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.speaking_question_item, parent, false);
        return new SpeakingQuestions_Adaptor.ViewHolder(view, onVoiceInputListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeakingQuestions_Adaptor.ViewHolder holder, int position) {
        QuestionItems currentQuestion = questionList.get(position);
        holder.setData(currentQuestion, position); // Gọi phương thức setData của ViewHolder để gán dữ liệu
    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView simplify_TextView1, questionContent_TextView1, simplify_TextView2, questionContent_TextView2, simplify_TextView3, questionContent_TextView3, pronunciation_TextView, speakingResult_TextView, compare_TextView;;
        ImageView sampleReading_ImageView, voice_ImageView;
        private TextToSpeech tts;
        private OnVoiceInputListener onVoiceInputListener;

        public ViewHolder(@NonNull View itemView, OnVoiceInputListener onVoiceInputListener) {
            super(itemView);
            this.onVoiceInputListener = onVoiceInputListener;

            simplify_TextView1 = itemView.findViewById(R.id.simplify_TextView1);
            questionContent_TextView1 = itemView.findViewById(R.id.questionContent_TextView1);
            simplify_TextView2 = itemView.findViewById(R.id.simplify_TextView2);
            questionContent_TextView2 = itemView.findViewById(R.id.questionContent_TextView2);
            simplify_TextView3 = itemView.findViewById(R.id.simplify_TextView3);
            questionContent_TextView3 = itemView.findViewById(R.id.questionContent_TextView3);
            pronunciation_TextView = itemView.findViewById(R.id.pronunciation_TextView);
            speakingResult_TextView = itemView.findViewById(R.id.speakingResult_TextView);
            compare_TextView = itemView.findViewById(R.id.compare_TextView);
            sampleReading_ImageView = itemView.findViewById(R.id.sampleReading_ImageView);
            voice_ImageView = itemView.findViewById(R.id.voice_ImageView);


            // Khởi tạo TextToSpeech
            tts = new TextToSpeech(itemView.getContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    // Thiết lập ngôn ngữ tiếng Nhật
                    int result = tts.setLanguage(Locale.JAPAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(itemView.getContext(), "Ngôn ngữ tiếng Nhật không được hỗ trợ", Toast.LENGTH_SHORT).show();
                    }
                    tts.setSpeechRate(0.75f); // Giảm tốc độ nói xuống còn 75%
                }
            });
        }

        private void setData(QuestionItems question, final int position) {


            simplify_TextView1.setText(question.getSimplifiedContent());

            String questionContent = question.getQuestion();
            int questionContentLength = questionContent.length();

            if(questionContentLength / 10 <= 1){
                questionContent_TextView1.setText(question.getQuestion());
            }
            else if( (double) questionContentLength / 10 > 1){
                String firstContent = questionContent.substring(0,9);
                questionContent_TextView1.setText(firstContent);

                String restContent = questionContent.substring(10);
                questionContent_TextView2.setText(restContent);
                questionContent_TextView2.setVisibility(View.VISIBLE);
            }
            else if ((double) questionContentLength / 10 > 2){
                String firstContent = questionContent.substring(0,9);
                questionContent_TextView1.setText(firstContent);

                String restContent1 = questionContent.substring(10,19);
                questionContent_TextView2.setText(restContent1);
                questionContent_TextView2.setVisibility(View.VISIBLE);

                String restContent2 = questionContent.substring(20);
                questionContent_TextView3.setText(restContent2);
                questionContent_TextView3.setVisibility(View.VISIBLE);
            }

            pronunciation_TextView.setText(question.getPronunciation());

            sampleReading_ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    speakJapanese(position);
                }
            });

            voice_ImageView.setOnClickListener(v -> {
                if (onVoiceInputListener != null) {
                    onVoiceInputListener.onVoiceInputRequested(position); // Gửi yêu cầu STT về Activity

                    speakingResult_TextView.setText("Bạn đã nói: \n" + Questions.Question_spokenText);
                    compare_TextView.setText("Độ trùng khớp:" + Questions.Question_similarity + "%");
                }
            });

        }

        //// Xử lý Text To Speech
        private void speakJapanese(int position) {
            // Lấy mẫu câu hỏi hiện tại
            String text = questionList.get(position).getQuestion();

            if (tts != null && text != null) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1");
            }
        }

    }
}
