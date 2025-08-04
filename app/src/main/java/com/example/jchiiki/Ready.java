package com.example.jchiiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.items.SummarizedPracticeChoices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Ready extends AppCompatActivity {
    TextView backTextView;
    Spinner questionNumber_Spinner;
    Button start_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        // Khởi tạo các thành phần giao diện
        backTextView = findViewById(R.id.back_TextView);
        questionNumber_Spinner = findViewById(R.id.questionNumber_Spinner);
        start_Button = findViewById(R.id.start_Button);

        // Xử lý sự kiện khi người dùng nhấn backTextView
        backTextView.setOnClickListener(v -> {
            finish();
        });

        //// Xử lý Spinner
        // Tạo một danh sách các giá trị để hiển thị trong Spinner
        List<Integer> questionNumberList = new ArrayList<>();

        // Thêm giá trị vào danh sách
        questionNumberList.add(10);
        questionNumberList.add(15);
        questionNumberList.add(20);

        // Tạo ArrayAdapter để kết nối danh sách với Spinner
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questionNumberList);

        // Chọn kiểu layout cho các mục trong Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Áp dụng adapter cho Spinner
        questionNumber_Spinner.setAdapter(adapter);

        start_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ready.this, Questions.class);
                intent.putExtra("questionNumber", questionNumber_Spinner.getSelectedItem().toString()); // Truyền số lượng câu hỏi đã chọn qua Intent

                startActivity(intent);
            }
        });

    }
}