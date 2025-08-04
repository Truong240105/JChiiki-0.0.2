package com.example.jchiiki.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.jchiiki.DownloadedLessons;
import com.example.jchiiki.Login;
import com.example.jchiiki.R;
import com.example.jchiiki.adaptors.Settings_Adaptor;
import com.example.jchiiki.items.SettingChoices;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    ArrayList<SettingChoices> settingArrayList;
    Settings_Adaptor settings_adaptor;
    GridView settings_GridView;

    Button signOut_Button;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settings_GridView = view.findViewById(R.id.settings_GridView);

        addSettingOptions();

        settings_adaptor = new Settings_Adaptor(getContext(), R.layout.setting_item, settingArrayList);
        settings_GridView.setAdapter(settings_adaptor);

        settings_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = settingArrayList.get(position).getSettingName();
                if (selectedOption.equals("Bài học đã tải xuống")) {
                    Intent intent = new Intent(getContext(), DownloadedLessons.class);
                    startActivity(intent);
                }
            }
        });

        signOut_Button = view.findViewById(R.id.signOut_Button);

        signOut_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                /*

                    Tạo một đối tượng GoogleSignInOptions bằng cách sử dụng GoogleSignInOptions.Builder. Đây là cấu hình để
                yêu cầu một số thông tin từ Google Sign-In:

                + requestIdToken(): yêu cầu mã thông báo (ID token) từ Google (sử dụng client_id đã được định nghĩa trong
                tệp strings.xml).
                + requestEmail(): yêu cầu email của người dùng khi đăng nhập qua Google.

                */
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build();


                /*
                     Tạo ra một đối tượng GoogleSignInClient với cấu hình gso đã được tạo trước đó. GoogleSignInClient là
                 đối tượng dùng để thực hiện các thao tác đăng nhập, đăng xuất và xử lý thông tin liên quan đến Google Sign-In.
                */
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


                /// Yêu cầu đăng xuất người dùng khỏi Google
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đặt cờ để đảm bảo rằng tất cả các màn hình trước đó trong lịch sử hoạt động sẽ bị xóa

                        startActivity(intent);
                        getActivity().finish();

                    }
                });


            }
        });


        return view;
    }

    private void addSettingOptions(){
        settingArrayList = new ArrayList<>();

        settingArrayList.add(new SettingChoices("Bài học đã tải xuống"));
        settingArrayList.add(new SettingChoices("Quản lý thông báo"));
        settingArrayList.add(new SettingChoices("Câu hỏi thường gặp"));
        settingArrayList.add(new SettingChoices("Làm mới tiến độ học"));
        settingArrayList.add(new SettingChoices("Điều khoản"));

    }

}