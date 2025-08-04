package com.example.jchiiki.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.Ready;
import com.example.jchiiki.items.QuestionItems;
import com.example.jchiiki.items.SummarizedPracticeChoices;

import java.util.List;

public class Questions_Adaptor extends RecyclerView.Adapter<Questions_Adaptor.ViewHolder> {

    private Context context;
    private static List<QuestionItems> questionList;

    public Questions_Adaptor(Context context, List<QuestionItems> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionItems currentQuestion = questionList.get(position);
        holder.setData(currentQuestion, position); // Gọi phương thức setData của ViewHolder để gán dữ liệu
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView question_TextView;
        Button option1_Button, option2_Button, option3_Button, option4_Button;
        Button previousAnswer_Button; // previousAnswer_Button để lưu trữ nút chọn trước đó

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            question_TextView = itemView.findViewById(R.id.question_TextView);
            option1_Button = itemView.findViewById(R.id.option1_Button);
            option2_Button = itemView.findViewById(R.id.option2_Button);
            option3_Button = itemView.findViewById(R.id.option3_Button);
            option4_Button = itemView.findViewById(R.id.option4_Button);
            previousAnswer_Button = null;

        }

        private void setData(QuestionItems question,final int position){
            question_TextView.setText(questionList.get(position).getQuestion());
            option1_Button.setText(questionList.get(position).getOption1());
            option2_Button.setText(questionList.get(position).getOption2());
            option3_Button.setText(questionList.get(position).getOption3());
            option4_Button.setText(questionList.get(position).getOption4());
            // Cập nhật giao diện dựa vào giá trị selectedAnswer
            updateOptionButtons(question.getSelectedAnswer());

            option1_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedOptionBackground(option1_Button, 0, position);
                }
            });

            option2_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedOptionBackground(option2_Button, 1, position);
                }
            });

            option3_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedOptionBackground(option3_Button, 2, position);
                }
            });

            option4_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedOptionBackground(option4_Button, 3, position);
                }
            });
        }

        private void updateOptionButtons(int selectedOption) {
            // Ví dụ: nếu selectedOption = 0, option1 được đánh dấu, các nút khác là trạng thái mặc định
            option1_Button.setBackgroundResource(selectedOption == 0 ? R.drawable.chosen_answer_bg : R.drawable.custom_card_white);
            option2_Button.setBackgroundResource(selectedOption == 1 ? R.drawable.chosen_answer_bg : R.drawable.custom_card_white);
            option3_Button.setBackgroundResource(selectedOption == 2 ? R.drawable.chosen_answer_bg : R.drawable.custom_card_white);
            option4_Button.setBackgroundResource(selectedOption == 3 ? R.drawable.chosen_answer_bg : R.drawable.custom_card_white);
        }

        private void setSelectedOptionBackground(Button button, int optionPosition, int questionPosition){

            // Trường hợp chưa có nút nào được chọn
            if (previousAnswer_Button == null) {
                button.setBackgroundResource(R.drawable.chosen_answer_bg);

                switch (DBQuery.g_practiceOptionChoice){
                    case "Vocabulary":
                        DBQuery.g_vocabularyQuestionList.get(questionPosition).setSelectedAnswer(optionPosition);
                        break;
                    case "Grammar":
                        break;
                    case "Reading":

                        break;
                    case "Writing":

                        break;
                    case "Everyday":

                        break;
                    case "Summarized":
                        DBQuery.g_summarizedPracticeQuestionList.get(questionPosition).setSelectedAnswer(optionPosition);
                        break;
                }

                previousAnswer_Button = button; // Lưu lại nút chọn trước đó

            }
            // Trường hợp đã có nút được chọn
            else{

                // Nếu người dùng nhấn lại nút mà trùng với nút chọn trước đó
                if (previousAnswer_Button.getId() == button.getId()){
                    button.setBackgroundResource(R.drawable.custom_card_white);

                    switch (DBQuery.g_practiceOptionChoice){
                        case "Vocabulary":
                            DBQuery.g_vocabularyQuestionList.get(questionPosition).setSelectedAnswer(-1); // Nút chọn trước đó bị bỏ chọn (nghĩa là -1)
                            break;
                        case "Grammar":
                            break;
                        case "Reading":

                            break;
                        case "Writing":

                            break;
                        case "Everyday":

                            break;
                        case "Summarized":
                            DBQuery.g_summarizedPracticeQuestionList.get(questionPosition).setSelectedAnswer(-1); // Nút chọn trước đó bị bỏ chọn (nghĩa là -1)
                            break;
                    }

                    previousAnswer_Button = null; // previousAnswer_Button bị set lại thành null
                }
                // Nếu người dùng nhấn nút khác với nút chọn trước đó
                else{
                    button.setBackgroundResource(R.drawable.chosen_answer_bg);
                    previousAnswer_Button.setBackgroundResource(R.drawable.custom_card_white);

                    switch (DBQuery.g_practiceOptionChoice){
                        case "Vocabulary":
                            DBQuery.g_vocabularyQuestionList.get(questionPosition).setSelectedAnswer(optionPosition); // Lưu lại lựa chọn của người dùng cho object QuestionItems tương ứng
                            break;
                        case "Grammar":
                            break;
                        case "Reading":

                            break;
                        case "Writing":

                            break;
                        case "Everyday":

                            break;
                        case "Summarized":
                            DBQuery.g_summarizedPracticeQuestionList.get(questionPosition).setSelectedAnswer(optionPosition); // Lưu lại lựa chọn của người dùng cho object QuestionItems tương ứng
                            break;
                    }

                    previousAnswer_Button = button; // Lưu lại nút chọn trước đó
                }

            }

        }

    }
}
