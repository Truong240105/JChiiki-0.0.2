package com.example.jchiiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.callback.MyCompleteListenerWithData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import admin.Admin_ActivityCallsFragments;
import admin.Admin_DBQuery;

public class Splash extends AppCompatActivity {

    ImageView logo_ImageView;
    TextView slogan_TextView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo_ImageView = findViewById(R.id.logo_ImageView);
        slogan_TextView = findViewById(R.id.slogan_TextView);

        /// Hoạt ảnh cho slogan
        Animation starting_app_animation = AnimationUtils.loadAnimation(this, R.anim.starting_app_animation);
        slogan_TextView.setAnimation(starting_app_animation);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth
        DBQuery.g_firestore = FirebaseFirestore.getInstance(); // Khởi tạo FirebaseFirestore

        //// Hoạt ảnh cho slogan
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Xử lý ngoại lệ nếu có
                }

                /// Kiểm tra người dùng đã đăng nhập hay chưa, nếu rôi chuyển sang màn hình Home, ngược lại chuyển sang màn hình Login
                if(mAuth.getCurrentUser() != null){
                    String uid = mAuth.getCurrentUser().getUid();
                    Admin_DBQuery.adminAuthentifications(uid, new MyCompleteListenerWithData() { // Kiểm tra nếu là Admin
                        @Override
                        public void onSuccess(long isAdmin) {
                            Intent intent = null;

                            if(isAdmin == 1){ // Nếu là Admin
                                intent = new Intent(Splash.this, Admin_ActivityCallsFragments.class);
                            }
                            else{ // Nếu là khách hàng

                                // Lấy cấp độ ngôn ngữ từ SharedPreferences
                                SharedPreferences languageLevelPreference = getSharedPreferences("LanguageLevel", MODE_PRIVATE);
                                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                                int level;

                                // Ở phần này chúng ta chỉ mới load các bài kiểm tra chứ thực chất chưa load các câu hỏi của các bài Summarized Practices
                                if(languageLevel == ""){
                                    level = 5;
                                }
                                else level = Integer.parseInt(languageLevel);

                                DBQuery.loadSummarizedPractices(level, new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("Splash: level", level+"");

                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(Splash.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                                intent = new Intent(Splash.this, ActivityCallsFragments.class);
                            }

                            startActivity(intent);
                            Splash.this.finish();
                        }
                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                }
                else{
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    Splash.this.finish();
                }

            }

        }.start();

    }
}