package com.example.jchiiki;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Paint;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.callback.MyCompleteListenerWithData;
import com.example.jchiiki.helper.GoogleLoginHelper;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import admin.Admin_ActivityCallsFragments;
import admin.Admin_DBQuery;

public class Login extends AppCompatActivity {
    EditText emailLogin_EditText, passwordLogin_EditText;
    TextView forgotPassword_TextView, signup_TextView;
    Button loginButton, googleAccountLogin_Button;
    CheckBox policy_Checkbox;
    Dialog progressDialog;
    TextView dialogTextView;
    FirebaseAuth mAuth; // Khởi tạo FirebaseAuth
    GoogleSignInClient mGoogleSignInClient; // Khởi tạo GoogleSignInClient
    String email, password; // Biến lưu trữ email và mật khẩu người dùng đã nhập từ form

    /*===== UNUSED =====*/
    // int RC_SIGN_IN = 104; // Mã yêu cầu đăng nhập Google (ghi số nào cũng được)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Liên kết giao diện người dùng với layout được định nghĩa trong file XML activity_login.xml

        // Khởi tạo các biến
        googleAccountLogin_Button = findViewById(R.id.googleAccountLogin_Button);
        emailLogin_EditText = findViewById(R.id.emailLogin_EditText);
        passwordLogin_EditText = findViewById(R.id.passwordLogin_EditText);
        policy_Checkbox = findViewById(R.id.policy_Checkbox);
        signup_TextView = findViewById(R.id.signup_TextView);
        loginButton = findViewById(R.id.loginButton);

        // Tạo text có từ nhấn mạnh
        String fullText = "Bằng việc đăng nhập với JChiiki, bạn đồng ý với điều khoản và điều kiện của chúng tôi.";
        SpannableString spannableString = new SpannableString(fullText);

        // Tìm vị trí của từ "điều khoản và điều kiện"
        int startIndex = fullText.indexOf("điều khoản và điều kiện");
        int endIndex = startIndex + "điều khoản và điều kiện".length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")), // Đặt màu cho đoạn text đó
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set text vào checkbox
        policy_Checkbox.setText(spannableString);


       // Gạch chân text
        TextView textView1 = findViewById(R.id.signup_TextView);
        TextView textView2=findViewById(R.id.textView3);
        textView2.setText("Để sau");
        textView1.setText("Tạo tài khoản ngay");
        textView1.setPaintFlags(textView1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView2.setPaintFlags(textView2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        /// Khởi tạo Dialog khi nhấn nút đăng ký
        progressDialog = new Dialog(Login.this);
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog

        /// Đặt nội dung cho TextView trong Dialog
        dialogTextView = progressDialog.findViewById(R.id.dialogTextView);
        dialogTextView.setText("Đang đăng nhập ...");

        FirebaseApp.initializeApp(this); // Khởi tạo FirebaseApp cho việc đăng nhập

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        //// Xử lý sự kiện khi nhấn đăng nhập bằng tài khoản người dùng
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailLogin_EditText.getText().toString().trim();
                password = passwordLogin_EditText.getText().toString().trim();

                if(dataValidation()){ // Kiểm tra dữ liệu hợp lệ
                    userAccount_Login();
                }
            }
        });


        //// Xử lý sự kiện khi nhấn đăng nhập bằng tài khoản Google

        // Khởi tạo ActivityResultLauncher (PHẢI đăng ký trước khi activity đã ở trạng thái RESUMED)
        GoogleLoginHelper googleLoginHelper = new GoogleLoginHelper(Login.this);
        googleLoginHelper.registerLauncher(this);

        googleAccountLogin_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = googleLoginHelper.mGoogleSignInClient.getSignInIntent();

                // Lấy cấp độ ngôn ngữ từ SharedPreferences
                SharedPreferences languageLevelPreference = getSharedPreferences("LanguageLevel", MODE_PRIVATE);
                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                int level;

                // Ở phần này chúng ta chỉ mới load các bài kiểm tra chứ thực chất chưa load các câu hỏi của các bài Summarized Practices
                if(languageLevel == ""){
                    level = 5;
                }
                else level = Integer.parseInt(languageLevel);

                // Load dữ liệu lessonList cho khách hàng
                loadSummarizedPracticeData(level);

                // Lưu cách thức đăng nhập vào SharedPreferences
                SharedPreferences loginMethodPreference = getSharedPreferences("LoginMethod", MODE_PRIVATE);
                SharedPreferences.Editor editor = loginMethodPreference.edit();
                editor.putString("LoginMethod", "GoogleAccount");
                editor.apply();

                googleLoginHelper.getLauncher().launch(intent);

                /* Unused */
                //googleAccount_Login();
            }
        });


        /// Chuyển sang màn hình đăng ký
        signup_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    //// Kiểm tra dữ liệu hợp lệ
    public Boolean dataValidation(){
        String email = emailLogin_EditText.getText().toString();
        String password = passwordLogin_EditText.getText().toString();
        Boolean isValid = true;

        /// Kiểm tra email
        if(email.isEmpty()){
            emailLogin_EditText.setError("Vui lòng nhập Email");
            isValid = false;
        }
        if(email.contains("@gmail.com") == false){
            emailLogin_EditText.setError("Email phải có đuôi '@gmail.com'");
            isValid = false;
        }

        /// Kiểm tra mật khẩu
        if(password.isEmpty()){
            passwordLogin_EditText.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }

        /// Kiểm tra đồng ý điều khoản
        if(policy_Checkbox.isChecked() == false){
            policy_Checkbox.setError("Vui lòng đồng ý với điều khoản và điều kiện");
            isValid = false;
        }

        return isValid;
    }


    //// Hàm xử lý đăng nhập bằng tài khoản người dùng
    public void userAccount_Login(){
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser(); // Lấy thông tin người dùng hiện tại ()

                            if (user != null && !user.isEmailVerified()) { // Nếu email chưa được xác thực
                                progressDialog.dismiss();
                                mAuth.signOut();
                                Toast.makeText(Login.this, "Vui lòng xác thực email của bạn để đăng nhập.", Toast.LENGTH_LONG).show();
                            } else if (user != null && user.isEmailVerified()) {
                                // Email đã được xác thực, kiểm tra xem người dùng đã tồn tại trong Firestore chưa
                                DBQuery.checkUserExistence(user.getUid(), new MyCompleteListenerWithData() {
                                    @Override
                                    public void onSuccess(long userExists) {
                                        if (userExists == 0) {
                                            // Người dùng chưa tồn tại trong Firestore, lấy dữ liệu từ Shared Preferences
                                            SharedPreferences sharedPreferences = getSharedPreferences("tempUserData", MODE_PRIVATE);
                                            String name = sharedPreferences.getString("name", "");
                                            String birthdate = sharedPreferences.getString("birthdate", "");
                                            String email = sharedPreferences.getString("email", "");
                                            String phoneNumber = sharedPreferences.getString("phoneNumber", "");
                                            String password = sharedPreferences.getString("password", "");

                                            // Lưu dữ liệu người dùng vào Firestore
                                            DBQuery.createUserData(name, email, password, birthdate, phoneNumber, new MyCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    // Xóa dữ liệu tạm thời trong Shared Preferences
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.clear();
                                                    editor.apply();

                                                    continueLoginProcess(user.getUid());
                                                }

                                                @Override
                                                public void onFailure() {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Login.this, "Lỗi khi lưu dữ liệu người dùng! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            // Người dùng đã tồn tại trong Firestore, tiếp tục xét mếu người đăng nhập là Admin hay Customer
                                            continueLoginProcess(user.getUid());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Login.this, "Lỗi kiểm tra người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Đăng nhập thất bại: Người dùng không tồn tại hoặc lỗi xác thực.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void continueLoginProcess(String uid) {
        Admin_DBQuery.adminAuthentifications(uid, new MyCompleteListenerWithData() {
            @Override
            public void onSuccess(long isAdmin) {

                Intent intent = null; // Khai báo biến intent
                if (isAdmin == 1) { // Nếu là admin thì chuyển sang màn hình quản lý

                    intent = new Intent(Login.this, Admin_ActivityCallsFragments.class);
                } else { // Nếu là khách hàng thì chuyển sang màn hình chính

                    // Lấy cấp độ ngôn ngữ từ SharedPreferences
                    SharedPreferences languageLevelPreference = getSharedPreferences("LanguageLevel", MODE_PRIVATE);
                    String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                    int level;

                    // Ở phần này chúng ta chỉ mới load các bài kiểm tra chứ thực chất chưa load các câu hỏi của các bài Summarized Practices
                    if (languageLevel == "") {
                        level = 5;
                    } else level = Integer.parseInt(languageLevel);
                    loadSummarizedPracticeData(level);

                    intent = new Intent(Login.this, ActivityCallsFragments.class);
                }

                // Lưu cách thức đăng nhập vào SharedPreferences
                SharedPreferences loginMethodPreference = getSharedPreferences("LoginMethod", MODE_PRIVATE);
                SharedPreferences.Editor editor = loginMethodPreference.edit();
                editor.putString("LoginMethod", "AppAccount");
                editor.apply();

                Toast.makeText(Login.this, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

                startActivity(intent);
                Login.this.finish();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSummarizedPracticeData(Integer level){
        DBQuery.loadSummarizedPractices(level, new MyCompleteListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {
                Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* ========================== Official (Inherited and Developed in GoogleLoginHelper) ==========================*/

    //// Biến xử lý đăng nhập bằng tài khoản Google
    /*
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                // Người dùng đã đăng nhập thành công, hiển thị progressDialog
                progressDialog.show();

                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

                                String googleAccountDisplayName = mAuth.getCurrentUser().getDisplayName();
                                String googleAccountEmail = mAuth.getCurrentUser().getEmail();

                                // Kiểm tra xem nếu người dùng mới tạo tài tài khoản
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Nếu là người dùng mới (đăng ký với Google) thì tạo 1 Document cho người dùng trong Collection Users
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    DBQuery.createUserData(user.getDisplayName(), user.getEmail(), new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {

                                            DBQuery.loadLessons(new MyCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Login.this, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();

                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Intent intent = new Intent(Login.this, Home.class);
                                                    startActivity(intent);
                                                    Login.this.finish();
                                                }
                                                @Override
                                                public void onFailure() {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailure() {
                                            progressDialog.dismiss();
                                            Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else { // Nếu người dùng đã đăng nhập (bằng Google) thì chuyển sang màn hình Home
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    Login.this.finish();
                                }
                                
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                    progressDialog.dismiss(); // Ẩn progressDialog nếu có lỗi
                    Toast.makeText(Login.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressDialog.dismiss(); // Người dùng huỷ đăng nhập, ẩn progressDialog
            }
        }
    });
    */


    /* ========================== Unused ==========================*/
    //// Các hàm xử lý đăng nhập bằng tài khoản Google
    /*
    public void googleAccount_Login(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /// Trả về kết quả thông qua việc mở Intent từ GoogleSignInApi.getSignInIntent(){...};
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                // Google Sign In thành công, xác minh bằng Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            }catch (Exception e){
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        progressDialog.show();

        /// Got an ID token from Google. Use it to authenticate with Firebase.
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null); // Tạo AuthCredential từ ID token
        mAuth.signInWithCredential(firebaseCredential) // Thực hiện đăng nhập bằng AuthCredential
                .addOnCompleteListener(this,(task) -> { // Xử lý kết quả đăng nhập

                    if (task.isSuccessful()) {
                        /// Sign in success, update UI with the signed-in user's information
                        Toast.makeText(Login.this, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();

                        /// Kiểm tra xem nếu người dùng mới tạo tài tài khoản
                        FirebaseUser user = mAuth.getCurrentUser();

                        /// Nếu là người dùng mới (đăng ký với Google) thì tạo 1 Document cho người dùng trong Collection Users
                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                            DBQuery.createUserData(user.getDisplayName(), user.getEmail(), new MyCompleteListener() {
                                @Override
                                public void onSuccess() {

                                    DBQuery.loadLessons(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(Login.this, Home.class);
                                            startActivity(intent);
                                            Login.this.finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            progressDialog.dismiss();
                                            Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                @Override
                                public void onFailure() {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else { /// Nếu người dùng đã đăng nhập (bằng Google) thì chuyển sang màn hình Home
                            progressDialog.dismiss();
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            Login.this.finish();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }


                });



    }
    */

}