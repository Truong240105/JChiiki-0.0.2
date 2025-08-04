package com.example.jchiiki.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.EditProfile;
import com.example.jchiiki.Questions;
import com.example.jchiiki.R;
import com.example.jchiiki.adaptors.LeaderBoard_Adaptor;
import com.example.jchiiki.adaptors.OptionMenu_Achivements_Adapter;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.callback.MyCompleteListenerWithData_2;
import com.example.jchiiki.decorations.SpaceItemDecoration;
import com.example.jchiiki.items.OptionMenuItems_Achivements;
import com.example.jchiiki.items.Profile;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AchievementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AchievementsFragment extends Fragment {
    Dialog progressDialog;
    RecyclerView optionMenu_RecyclerView, leaderBoard_RecyclerView;
    OptionMenu_Achivements_Adapter optionMenuAdapter;
    LeaderBoard_Adaptor leaderBoardAdapter;
    ArrayList<OptionMenuItems_Achivements> optionMenuArrayList;
    ConstraintLayout bannerContainer;
    ImageView editProfile_ImageView, closeBanner_ImageView;
    TextView userNameInfo_TextView, userScore_TextView, date_TextView, todayExp_TextView;
    FirebaseAuth mAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AchievementsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Achievements.
     */
    // TODO: Rename and change types and number of parameters
    public static AchievementsFragment newInstance(String param1, String param2) {
        AchievementsFragment fragment = new AchievementsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        editProfile_ImageView = view.findViewById(R.id.editProfile_ImageView);
        userScore_TextView = view.findViewById(R.id.userScore_TextView);
        optionMenu_RecyclerView = view.findViewById(R.id.optionMenu_RecyclerView);
        bannerContainer = view.findViewById(R.id.banner_container);
        closeBanner_ImageView = view.findViewById(R.id.closeBanner_ImageView);
        userNameInfo_TextView = view.findViewById(R.id.userNameInfo_TextView);
        date_TextView = view.findViewById(R.id.date_TextView);
        todayExp_TextView = view.findViewById(R.id.todayExp_TextView);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        /// Khởi tạo Dialog khi đang tải các câu hỏi
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog
        progressDialog.show();

        // Đặt sự kiện click cho editProfile_ImageView
        editProfile_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Lấy cách thức đăng nhập bằng SharedPreferences
        SharedPreferences loginMethodPreference = getContext().getSharedPreferences("LoginMethod", MODE_PRIVATE);
        String loginMethod = loginMethodPreference.getString("LoginMethod", "");

        DBQuery.getUserProfile(loginMethod, new MyCompleteListenerWithData_2() {
                    @Override
                    public void onSuccess(Profile profile) {
                        Integer totalScore = profile.getScore();
                        userScore_TextView.setText("Điểm: " + totalScore.toString());

                        userNameInfo_TextView.setText(profile.getName());
                        userNameInfo_TextView.setTextColor(getResources().getColor(R.color.white));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Có lỗi xảy ra! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    }
                });


        //////// ========= OPTION MENU =========

        optionMenu_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4)); // Chia lưới 4 cột

        // Khởi tạo danh sách chức năng
        addMenuOption();
        optionMenuAdapter = new OptionMenu_Achivements_Adapter(getContext(), optionMenuArrayList);
        optionMenu_RecyclerView.setAdapter(optionMenuAdapter);

        //////// ========= BANNER =========

        // Đặt sự kiện click cho nút đóng banner
        closeBanner_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerContainer.setVisibility(View.GONE);
            }
        });

        // Lấy ngày hiện tại từ hệ thống
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Lưu ý: Calendar.MONTH trả về giá trị từ 0 đến 11

        // Đặt text cho date_TextView
        String dateText = "Hôm nay, " + day + "/" + month;
        date_TextView.setText(dateText);

        //////// ========= LEADER BOARD =========

        leaderBoard_RecyclerView = view.findViewById(R.id.leaderBoard_RecyclerView);


        DBQuery.getTopUsers(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                leaderBoardAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Có lỗi xảy ra! Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        leaderBoardAdapter = new LeaderBoard_Adaptor(getContext(), DBQuery.g_rankingList);

        leaderBoard_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1)); // Chia lưới 1 cột
        leaderBoard_RecyclerView.addItemDecoration(new SpaceItemDecoration(20)); // Thêm khoảng cách giữa các item là 20dp
        leaderBoard_RecyclerView.setAdapter(leaderBoardAdapter);

        return view;
    }

    private void addMenuOption(){
        // Tạo danh sách menu
        optionMenuArrayList = new ArrayList<>();
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Tiến độ học"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Lịch học"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Tư vấn"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Đề thi"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Học liệu"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Thông báo"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Tích điểm"));
        optionMenuArrayList.add(new OptionMenuItems_Achivements(R.drawable.avatar_achievements, "Cài đặt"));
    }

}