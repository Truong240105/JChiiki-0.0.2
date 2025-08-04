package com.example.jchiiki;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jchiiki.callback.MyCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {
    ImageView back_ImageView;
    LinearLayout appAccount_Container;
    EditText userName_EditText, password_EditText, confirmedPassword_EditText, email_EditText, phoneNumber_EditText, birthdate_EditText;
    Button saveChanges_Button;
    Dialog progressDialog;
    TextView dialogTextView;
    FirebaseAuth mAuth;

    String name, birthdate, email, phoneNumber, password, confirmedPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo các thành phần giao diện
        back_ImageView = findViewById(R.id.back_ImageView);
        appAccount_Container = findViewById(R.id.appAccount_Container);
        userName_EditText = findViewById(R.id.userName_EditText);
        password_EditText = findViewById(R.id.password_EditText);
        confirmedPassword_EditText = findViewById(R.id.confirmedPassword_EditText);
        email_EditText = findViewById(R.id.email_EditText);
        phoneNumber_EditText = findViewById(R.id.phone_EditText);
        birthdate_EditText = findViewById(R.id.birthdate_EditText);
        saveChanges_Button = findViewById(R.id.saveChanges_Button);

        /// Khởi tạo Dialog khi nhấn nút đăng ký
        progressDialog = new Dialog(EditProfile.this);
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Lấy email người dùng đã đăng nhập
        if(mAuth != null){
            String emailName = mAuth.getCurrentUser().getEmail().toString();
            Log.d("emailName", emailName);
            email_EditText.setText(emailName);
            email_EditText.setEnabled(false);
        }

        // Đặt nội dung cho TextView trong Dialog
        dialogTextView = progressDialog.findViewById(R.id.dialogTextView);
        dialogTextView.setText("Đang cập nhật ...");

        // Đặt sự kiện click cho nút back
        back_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this, ActivityCallsFragments.class);
                startActivity(intent);
                finish();
            }
        });

        //// Xử lý sự kiện khi nhấn birthdate_EditText đồng thời giúp chọn lịch thay vì nhập từ bàn phím
        birthdate_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ngày hiện tại
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Tạo DatePickerDialog để chọn ngày
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Cập nhật TextView hoặc EditText với ngày đã chọn
                                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                                birthdate_EditText.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        //// Xử lý cập nhật thông tin người dùng
        // Lấy cách thức đăng nhập bằng SharedPreferences
        SharedPreferences loginMethodPreference = getSharedPreferences("LoginMethod", MODE_PRIVATE);
        String loginMethod = loginMethodPreference.getString("LoginMethod", "");

        // Nếu đăng nhập bằng Google, ẩn các thành phần liên quan đến tài khoản App
        if(loginMethod.equals("GoogleAccount")){
            appAccount_Container.setVisibility(View.GONE);
        }

        saveChanges_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = userName_EditText.getText().toString();
                password = password_EditText.getText().toString();
                confirmedPassword = confirmedPassword_EditText.getText().toString();
                email = email_EditText.getText().toString();
                phoneNumber = phoneNumber_EditText.getText().toString();
                birthdate = birthdate_EditText.getText().toString();

                // Nếu dữ liệu không hợp lệ, không thực hiện thao tác
                if(!dataValidation()) return;

                progressDialog.show();

                switch (loginMethod){
                    case "AppAccount":
                        DBQuery.editProfile(name, password, birthdate, phoneNumber, new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss(); // Ẩn Dialog khi đăng ký thành công
                                Intent intent = new Intent(EditProfile.this, ActivityCallsFragments.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure() {
                                progressDialog.dismiss();
                                Toast.makeText(EditProfile.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "GoogleAccount":
                        DBQuery.editProfile(name, new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss(); // Ẩn Dialog khi đăng ký thành công
                                Intent intent = new Intent(EditProfile.this, ActivityCallsFragments.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure() {
                                progressDialog.dismiss();
                                Toast.makeText(EditProfile.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            }
        });
    }

    //// Kiểm tra dữ liệu hợp lệ
    public Boolean dataValidation(){
        Boolean isValid = true;

        /// Kiểm tra tên
        if(name.isEmpty()){
            userName_EditText.setError("Vui lòng nhập tên người dùng");
            isValid = false;
        }
        if(name.length() > 15){
            userName_EditText.setError("Tên người dùng không được quá 15 kí tự");
            isValid = false;
        }

        /// Kiểm tra ngày sinh
        if(birthdate.isEmpty()){
            birthdate_EditText.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        }

        /// Kiểm tra số điện thoại
        if(phoneNumber.isEmpty()){
            phoneNumber_EditText.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        }
        if(phoneNumber.length() > 18){
            phoneNumber_EditText.setError("Vui lòng nhập số điện thoại ít hơn 18 chữ số");
            isValid = false;
        }

        /// Kiểm tra mật khẩu
        if(password.isEmpty()){
            password_EditText.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }
        if(password.length() < 6){
            password_EditText.setError("Mật khẩu cần chứa ít nhất 6 kí tự");
            isValid = false;
        }

        /// Kiểm tra mật khẩu nhập lại
        if(confirmedPassword.isEmpty()){
            confirmedPassword_EditText.setError("Vui lòng nhập lại mật khẩu");
            isValid = false;
        }
        if(!password.equals(confirmedPassword)){
            confirmedPassword_EditText.setError("Mật khẩu nhập lại không khớp");
            isValid = false;
        }

        return isValid;
    }
}