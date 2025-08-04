package com.example.jchiiki;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.helper.GoogleLoginHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    TextView later_TextView;
    TextView loginNow_TextView;
    ImageView back_ImageView;
    EditText nameSignUp_EditText, birthdateSignUp_EditText, emailSignUp_EditText, phoneNumberSignUp_EditText, passwordSignUp_EditText, confirmedPasswordSignUp_EditText;
    CheckBox policy_Checkbox;
    Button googleAccountLogin_Button, signUp_Button;
    Dialog progressDialog, emailVerificationDialog;
    TextView dialogTextView;
    private FirebaseAuth mAuth;
    String name, birthdate, email, phoneNumber, password, confirmedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        /// Ánh xạ các thành phần giao diện
        back_ImageView = findViewById(R.id.back_ImageView);
        googleAccountLogin_Button = findViewById(R.id.googleAccountLogin_Button);
        nameSignUp_EditText = findViewById(R.id.nameSignUp_EditText);
        emailSignUp_EditText = findViewById(R.id.emailSignUp_EditText);
        phoneNumberSignUp_EditText = findViewById(R.id.phoneNumberSignUp_EditText);
        birthdateSignUp_EditText = findViewById(R.id.birthdateSignUp_EditText);
        passwordSignUp_EditText = findViewById(R.id.passwordSignUp_EditText);
        confirmedPasswordSignUp_EditText = findViewById(R.id.confirmedPasswordSignUp_EditText);
        policy_Checkbox = findViewById(R.id.policy_Checkbox);
        signUp_Button = findViewById(R.id.signUp_Button);
        later_TextView = findViewById(R.id.later_TextView);
        loginNow_TextView = findViewById(R.id.loginNow_TextView);

        /// Custom cho checkbox điều khoản và bảo mật
        CheckBox agreePolicyCheckbox = findViewById(R.id.policy_Checkbox);

        // Tạo text có từ nhấn mạnh
        String fullText = "Bằng việc đăng nhập với JChiiki, bạn đồng ý với điều khoản và điều kiện của chúng tôi.";
        SpannableString spannableString = new SpannableString(fullText);

        // Tìm vị trí của từ "điều khoản và điều kiện"
        int startIndex = fullText.indexOf("điều khoản và điều kiện");
        int endIndex = startIndex + "điều khoản và điều kiện".length();

        // Đặt màu cho đoạn text đó
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")),
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set text vào checkbox
        agreePolicyCheckbox.setText(spannableString);
        /// end custom cho checkboxx điều khoản và bảo mật

        // Gạch chân
        later_TextView.setText("Để sau");
        later_TextView.setPaintFlags(later_TextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginNow_TextView.setText("Đăng nhập ngay");
        loginNow_TextView.setPaintFlags(loginNow_TextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        /// Khởi tạo Dialog xác thực email
        emailVerificationDialog = new Dialog(SignUp.this);
        emailVerificationDialog.setContentView(R.layout.email_verification_dialog);
        emailVerificationDialog.setCancelable(false);
        emailVerificationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button okButton = emailVerificationDialog.findViewById(R.id.ok_Button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailVerificationDialog.dismiss();
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        /// Khởi tạo Dialog khi thực hiện đăng ký
        progressDialog = new Dialog(SignUp.this);
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog

        // Đặt nội dung cho TextView trong Dialog
        dialogTextView = progressDialog.findViewById(R.id.dialogTextView);
        dialogTextView.setText("Đang đăng ký ...");


        /// Đặt chế độ không chỉnh sửa cho EditText
        birthdateSignUp_EditText.setFocusable(false);  // Để tránh việc chỉnh sửa thủ công
        birthdateSignUp_EditText.setClickable(true);   // Cho phép nhấn vào để mở DatePicker

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth
        mAuth.getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false); // Tùy chọn: bỏ xác minh Recaptcha
        FirebaseAuth.getInstance().getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(true); // Dùng để test OTP không cần reCAPTCHA

        //// Xử lý sự kiện khi nhấn back_ImageView
        back_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //// Xử lý sự kiện khi nhấn đăng nhập bằng tài khoản Google

        // Khởi tạo ActivityResultLauncher (PHẢI đăng ký trước khi activity đã ở trạng thái RESUMED)
        GoogleLoginHelper googleLoginHelper = new GoogleLoginHelper(SignUp.this);
        googleLoginHelper.registerLauncher(this); // Đăng ký ActivityResultLauncher

        googleAccountLogin_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = googleLoginHelper.mGoogleSignInClient.getSignInIntent(); // Khởi tạo Intent để đăng nhập bằng tài khoản Google
                googleLoginHelper.getLauncher().launch(intent); // Gọi hàm launch để đăng nhập bằng tài khoản Google

                /* Unused */
                //googleAccount_Login();
            }
        });

        //// Xử lý sự kiện khi nhấn birthdateSignUp_EditText đồng thời giúp chọn lịch thay vì nhập từ bàn phím
        birthdateSignUp_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ngày hiện tại
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Tạo DatePickerDialog để chọn ngày
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Cập nhật TextView hoặc EditText với ngày đã chọn
                                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                                birthdateSignUp_EditText.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        //// Xử lý sự kiện khi nhấn đăng ký
        signUp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// Lấy dữ liệu từ các EditText
                name = nameSignUp_EditText.getText().toString().trim();
                birthdate = birthdateSignUp_EditText.getText().toString().trim();
                email = emailSignUp_EditText.getText().toString().trim();
                phoneNumber = phoneNumberSignUp_EditText.getText().toString().trim();
                password = passwordSignUp_EditText.getText().toString().trim();
                confirmedPassword = confirmedPasswordSignUp_EditText.getText().toString().trim();

                if(dataValidation()){
                    signUp();
                }
            }
        });

    }

    //// Kiểm tra dữ liệu hợp lệ
    public Boolean dataValidation(){
        Boolean isValid = true;

        /// Kiểm tra tên
        if(name.isEmpty()){
            nameSignUp_EditText.setError("Vui lòng nhập tên người dùng");
            isValid = false;
        }
        if(name.length() > 15){
            nameSignUp_EditText.setError("Tên người dùng không được quá 15 kí tự");
            isValid = false;
        }

        /// Kiểm tra ngày sinh
        if(birthdate.isEmpty()){
            birthdateSignUp_EditText.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        }

        /// Kiểm tra email
        if(email.isEmpty()){
            emailSignUp_EditText.setError("Vui lòng nhập email");
            isValid = false;
        }
        if(email.contains("@gmail.com") == false){
            emailSignUp_EditText.setError("Email phải có đuôi '@gmail.com'");
            isValid = false;
        }

        /// Kiểm tra số điện thoại
        if(phoneNumber.isEmpty()){
            phoneNumberSignUp_EditText.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        }
        if(phoneNumber.length() > 18){
            phoneNumberSignUp_EditText.setError("Vui lòng nhập số điện thoại ít hơn 18 chữ số");
            isValid = false;
        }

        /// Kiểm tra mật khẩu
        if(password.isEmpty()){
            passwordSignUp_EditText.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }
        if(password.length() < 6){
            passwordSignUp_EditText.setError("Mật khẩu cần chứa ít nhất 6 kí tự");
            isValid = false;
        }

        /// Kiểm tra mật khẩu nhập lại
        if(confirmedPassword.isEmpty()){
            confirmedPasswordSignUp_EditText.setError("Vui lòng nhập lại mật khẩu");
            isValid = false;
        }
        if(!password.equals(confirmedPassword)){
            confirmedPasswordSignUp_EditText.setError("Mật khẩu nhập lại không khớp");
            isValid = false;
        }

        /// Kiểm tra đồng ý điều khoản
        if(policy_Checkbox.isChecked() == false){
            policy_Checkbox.setError("Vui lòng đồng ý với điều khoản và điều kiện");
            isValid = false;
        }

        return isValid;
    }

    //// Các hàm xử lý đăng ký

    public void signUp(){
        progressDialog.show(); /// Hiển thị Dialog khi đăng ký

        /// Thực hiện đăng ký
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Nếu đăng ký thành công (khi nhập form thoả điều kiện), thực hiện các thao tác tiếp theo
                        if (task.isSuccessful()) {
                            // Lưu dữ liệu người dùng vào SharedPreferences tạm thời
                            SharedPreferences sharedPreferences = getSharedPreferences("tempUserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", name);
                            editor.putString("birthdate", birthdate);
                            editor.putString("email", email);
                            editor.putString("phoneNumber", phoneNumber);
                            editor.putString("password", password);
                            editor.apply();

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailTask) {
                                                progressDialog.dismiss();
                                                if (emailTask.isSuccessful()) { // Nếu xác thực email thành công
                                                    emailVerificationDialog.show();
                                                } else {
                                                    Toast.makeText(SignUp.this, "Không thể gửi email xác thực: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                        /// Nếu đăng ký thất bại (khi nhập form chưa thoả điều kiện), hiển thị thông báo lỗi
                        else {
                            progressDialog.dismiss(); // Ẩn Dialog khi đăng ký thất bại
                            Toast.makeText(SignUp.this, "Email đã được sử dụng cho tài khoản khác !", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi

                        }
                    }
                });
    }

}