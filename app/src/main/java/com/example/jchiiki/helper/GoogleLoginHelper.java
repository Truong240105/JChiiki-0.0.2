package com.example.jchiiki.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jchiiki.ActivityCallsFragments;
import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.callback.MyCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleLoginHelper extends AppCompatActivity {
    Dialog progressDialog;
    public FirebaseAuth mAuth; // Khởi tạo FirebaseAuth
    public GoogleSignInClient mGoogleSignInClient; // Khởi tạo GoogleSignInClient
    Context context;
    ActivityResultLauncher<Intent> launcher;

    public GoogleLoginHelper(Context context) {
        this.context = context;
        initializeVariables();
    }

    public ActivityResultLauncher<Intent> getLauncher() {
        return launcher;
    } // Lấy launcher

    public void initializeVariables(){
        /// Khởi tạo Dialog khi nhấn nút đăng ký
        progressDialog = new Dialog(context); // Khởi tạo Dialog cho 1 Context
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        /// Khởi tạo GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.client_id)) // Đảm bảo rằng chuỗi token được lấy từ context mà bạn đã truyền vào constructor của GoogleLoginHelper bằng cách thêm "context."
                .requestEmail()
                .build();

        // Khởi tạo GoogleSignInClient và ép kiểu context về Activity nếu chắc chắn context là Activity
        mGoogleSignInClient = GoogleSignIn.getClient( (AppCompatActivity) context, gso);

    }

    // Phương thức đăng ký ActivityResultLauncher
    public void registerLauncher(AppCompatActivity activity) {
        launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        progressDialog.show();
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class); // Lấy tài khoản Google
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null); // Tạo AuthCredential từ tài khoản Google
                            mAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser(); // Lấy thông tin người dùng đã đăng nhập
                                    if (task.getResult().getAdditionalUserInfo().isNewUser()) { // Nếu là người dùng mới (đăng ký với Google) thì tạo 1 Document cho người dùng trong Collection Users
                                        DBQuery.createUserData(user.getDisplayName(), user.getEmail(), new MyCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                progressDialog.dismiss();
                                                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                                // Chuyển màn hình (ví dụ, chuyển sang Home)
                                                activity.startActivity(new Intent(context, ActivityCallsFragments.class));
                                                activity.finish();
                                            }
                                            @Override
                                            public void onFailure() {
                                                progressDialog.dismiss();
                                                Toast.makeText(context, "Đã xảy ra lỗi! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        activity.startActivity(new Intent(context, ActivityCallsFragments.class));
                                        activity.finish();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Đã xảy ra lỗi! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (ApiException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(context, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                    }
                }
        );
    }

    /*==================== Unused ====================*/
    /*
    //// Biến xử lý đăng nhập bằng tài khoản Google
    public final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

                                String googleAccountDisplayName = mAuth.getCurrentUser().getDisplayName();
                                String googleAccountEmail = mAuth.getCurrentUser().getEmail();

                                Log.d("Login.java -> activityResultLauncher: googleAccountDisplayName", googleAccountDisplayName + "");
                                Log.d("Login.java -> activityResultLauncher: googleAccountEmail", googleAccountEmail + "");

                                // Kiểm tra xem nếu người dùng mới tạo tài tài khoản
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Nếu là người dùng mới (đăng ký với Google) thì tạo 1 Document cho người dùng trong Collection Users
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    DBQuery.createUserData(user.getDisplayName(), user.getEmail(), new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent intent = new Intent(context, ActivityCallsFragments.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            progressDialog.dismiss();
                                            Toast.makeText(GoogleLoginHelper.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else { // Nếu người dùng đã đăng nhập (bằng Google) thì chuyển sang màn hình Home
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(GoogleLoginHelper.this, ActivityCallsFragments.class);
                                    startActivity(intent);
                                    GoogleLoginHelper.this.finish();
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(GoogleLoginHelper.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                    progressDialog.dismiss(); // Ẩn progressDialog nếu có lỗi
                    Toast.makeText(GoogleLoginHelper.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Người dùng huỷ đăng nhập, ẩn progressDialog
                progressDialog.dismiss();
            }
        }
    });
    */

    /*==================== Unused ====================*/
    /*
    public ActivityResultLauncher<Intent> getActivityResultLauncher(AppCompatActivity activity) {
        // Đăng ký launcher với lifecycle của Activity
        return activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Xử lý kết quả đăng nhập Google như bạn đã làm
                        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            progressDialog.show();
                            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            String googleAccountDisplayName = mAuth.getCurrentUser().getDisplayName();
                                            String googleAccountEmail = mAuth.getCurrentUser().getEmail();

                                            Log.d("Login.java -> activityResultLauncher: googleAccountDisplayName", googleAccountDisplayName + "");
                                            Log.d("Login.java -> activityResultLauncher: googleAccountEmail", googleAccountEmail + "");

                                            // Kiểm tra xem nếu người dùng mới tạo tài tài khoản
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            // Nếu là người dùng mới (đăng ký với Google) thì tạo 1 Document cho người dùng trong Collection Users
                                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                                DBQuery.createUserData(user.getDisplayName(), user.getEmail(), new MyCompleteListener() {
                                                    @Override
                                                    public void onSuccess() { // Nếu người dùng đã đăng nhập (bằng Google) thì chuyển sang màn hình Home
                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();

                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        Intent intent = new Intent(context, ActivityCallsFragments.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure() {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(GoogleLoginHelper.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else { // Nếu người dùng đã đăng nhập (bằng Google) thì chuyển sang màn hình Home
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(GoogleLoginHelper.this, ActivityCallsFragments.class);
                                                startActivity(intent);
                                                GoogleLoginHelper.this.finish();
                                            }

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(GoogleLoginHelper.this, "Đã xảy ra lỗi ! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } catch (ApiException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();  // Ẩn progressDialog nếu có lỗi
                                Toast.makeText(context, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Người dùng huỷ đăng nhập, ẩn progressDialog
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    */
}
