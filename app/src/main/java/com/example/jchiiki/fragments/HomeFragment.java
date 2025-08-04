package com.example.jchiiki.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.jchiiki.ActivityCallsFragments;
import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.Splash;
import com.example.jchiiki.Translator;
import com.example.jchiiki.adaptors.HomeFragment_Adaptor;
import com.example.jchiiki.items.HomePracticeOptions;
import com.example.jchiiki.adaptors.ExpandableHeightGridView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    Toolbar homeToolbar;
    Dialog levelChoices_Dialog;
    SharedPreferences languageLevelPreference;
    SharedPreferences.Editor editor;
    ExpandableHeightGridView practice_GridView, examPractice_GridView;
    ArrayList<HomePracticeOptions> practiceOptionArrayList, examPracticeOptionArrayList;
    HomeFragment_Adaptor homeFragment_adaptor;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Giúp Fragment thông báo rằng nó có menu riêng
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các biến
        homeToolbar = view.findViewById(R.id.homeFragment_ToolBar); // Khởi tạo Toolbar
        practice_GridView = view.findViewById(R.id.practice_GridView);
        examPractice_GridView = view.findViewById(R.id.examPractice_GridView);
        practice_GridView.setExpanded(true);
        examPractice_GridView.setExpanded(true);

        //// Xử lý sự kiện khi chọn cấp độ ngôn ngữ

        ((AppCompatActivity) requireActivity()).setSupportActionBar(homeToolbar); // Đặt Toolbar làm ActionBar của Activity

        // Gán cấp độ ngôn ngữ cho Toolbar
        String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

        if(languageLevel == ""){
            homeToolbar.setTitle("N5");

            editor = languageLevelPreference.edit();
            editor.putString("LanguageLevel", "5");
            editor.putString("PreviousLanguageLevel", "-1");
            editor.commit();
        }
        else{
            homeToolbar.setTitle("N"+languageLevel);
        }

        // Cài đặt cho Dialog
        levelChoices_Dialog = new Dialog(getContext());
        levelChoices_Dialog.setContentView(R.layout.language_levels_dialog); // Đặt layout cho Dialog
        levelChoices_Dialog.setCancelable(true); // Cho phép tắt bằng cách nhấp vào ngoài
        levelChoices_Dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog

        //// Thêm các Practice Option và hiển thị lên GridView
        if(practiceOptionArrayList == null){
            addPracticeOptions();

            homeFragment_adaptor = new HomeFragment_Adaptor(getContext(), R.layout.home_practice_item, practiceOptionArrayList);
            practice_GridView.setAdapter(homeFragment_adaptor);
        }

        //// Thêm các Exam Practice Option và hiển thị lên GridView
        if(examPracticeOptionArrayList == null){
            addExamPracticeOptions();

            homeFragment_adaptor = new HomeFragment_Adaptor(getContext(), R.layout.home_practice_item, examPracticeOptionArrayList);
            examPractice_GridView.setAdapter(homeFragment_adaptor);
        }

        //// Xử lý sự kiện khi click vào Practice Option
        practice_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectPracticeOption(i);
            }
        });

        examPractice_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectExamPracticeOption(i);
            }
        });

        return view;
    }

    //////////////// Xử lý phần Toolbar

    // Khởi tạo SharedPreferences để lưu trữ cấp độ ngôn ngữ
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        languageLevelPreference = context.getSharedPreferences("LanguageLevel", Context.MODE_PRIVATE);
        editor = languageLevelPreference.edit();
    }

    // Các icon trong Toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Lấy các TextView trong Dialog
        TextView levelN5_TextView = levelChoices_Dialog.findViewById(R.id.levelN5_TextView);
        TextView levelN4_TextView = levelChoices_Dialog.findViewById(R.id.levelN4_TextView);
        TextView levelN3_TextView = levelChoices_Dialog.findViewById(R.id.levelN3_TextView);
        TextView levelN2_TextView = levelChoices_Dialog.findViewById(R.id.levelN2_TextView);
        TextView levelN1_TextView = levelChoices_Dialog.findViewById(R.id.levelN1_TextView);

        levelChoices_Dialog.show();

        // Xử lý sự kiện khi chọn cấp độ ngôn ngữ trong Dialog
        levelN5_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                // Cập nhật cấp độ ngôn ngữ cho TextView
                homeToolbar.setTitle("N5");

                editor.putString("PreviousLanguageLevel", languageLevel);
                Log.d("PreviousLanguageLevel: ", languageLevel);
                Log.d("LanguageLevel: ", "5");

                editor.putString("LanguageLevel", "5");

                editor.commit();
                levelChoices_Dialog.dismiss(); // Đóng Dialog

                // Finish ActivityCallsFragments và quay lại Splash
                Intent intent = new Intent(getActivity(), Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đảm bảo Splash là Activity duy nhất trên stack
                startActivity(intent);
                getActivity().finish(); // Kết thúc ActivityCallsFragments
            }
        });

        levelN4_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                homeToolbar.setTitle("N4");

                editor.putString("PreviousLanguageLevel", languageLevel);
                Log.d("PreviousLanguageLevel: ", languageLevel);
                Log.d("LanguageLevel: ", "4");

                editor.putString("LanguageLevel", "4");

                editor.commit();
                levelChoices_Dialog.dismiss();

                // Finish ActivityCallsFragments và quay lại Splash
                Intent intent = new Intent(getActivity(), Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đảm bảo Splash là Activity duy nhất trên stack
                startActivity(intent);
                getActivity().finish(); // Kết thúc ActivityCallsFragments
            }
        });

        levelN3_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                homeToolbar.setTitle("N3");

                editor.putString("PreviousLanguageLevel", languageLevel);
                Log.d("PreviousLanguageLevel: ", languageLevel);
                Log.d("LanguageLevel: ", "3");

                editor.putString("LanguageLevel", "3");

                editor.commit();
                levelChoices_Dialog.dismiss();

                // Finish ActivityCallsFragments và quay lại Splash
                Intent intent = new Intent(getActivity(), Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đảm bảo Splash là Activity duy nhất trên stack
                startActivity(intent);
                getActivity().finish(); // Kết thúc ActivityCallsFragments
            }
        });

        levelN2_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                homeToolbar.setTitle("N2");

                editor.putString("PreviousLanguageLevel", languageLevel);
                Log.d("PreviousLanguageLevel: ", languageLevel);
                Log.d("LanguageLevel: ", "2");

                editor.putString("LanguageLevel", "2");

                editor.commit();
                levelChoices_Dialog.dismiss();

                // Finish ActivityCallsFragments và quay lại Splash
                Intent intent = new Intent(getActivity(), Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đảm bảo Splash là Activity duy nhất trên stack
                startActivity(intent);
                getActivity().finish(); // Kết thúc ActivityCallsFragments
            }
        });

        levelN1_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

                homeToolbar.setTitle("N1");

                editor.putString("PreviousLanguageLevel", languageLevel);
                Log.d("PreviousLanguageLevel: ", languageLevel);
                Log.d("LanguageLevel: ", "1");

                editor.putString("LanguageLevel", "1");

                editor.commit();
                levelChoices_Dialog.dismiss();

                // Finish ActivityCallsFragments và quay lại Splash
                Intent intent = new Intent(getActivity(), Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đảm bảo Splash là Activity duy nhất trên stack
                startActivity(intent);
                getActivity().finish(); // Kết thúc ActivityCallsFragments
            }
        });

        return super.onOptionsItemSelected(item);
    }

    //////////////// Xử lý phần Practice
    //// Thêm các Practice Option
    private void addPracticeOptions() {
        practiceOptionArrayList = new ArrayList<>();

        practiceOptionArrayList.add(new HomePracticeOptions(R.drawable.lesson_book_icon, "Từ vựng"));
        practiceOptionArrayList.add(new HomePracticeOptions(R.drawable.grammar_icon, "Ngữ pháp"));
        practiceOptionArrayList.add(new HomePracticeOptions(R.drawable.listening_icon, "Nghe hiểu"));
        practiceOptionArrayList.add(new HomePracticeOptions(R.drawable.reading_icon, "Đọc hiểu"));
        practiceOptionArrayList.add(new HomePracticeOptions(R.drawable.writing_icon, "Luyện viết"));
        practiceOptionArrayList.add(new HomePracticeOptions(R.drawable.speaking_icon, "Luyện nói"));

    }

    private void selectPracticeOption(int position) {

        switch (position){
            case 0:
                replaceFragment(new LessonsFragment());
                DBQuery.g_practiceOptionChoice = "Vocabulary";
                break;
            case 1:
                replaceFragment(new GrammarLessonsFragment());
                DBQuery.g_practiceOptionChoice = "Grammar";
                break;
            case 2:
                replaceFragment(new ListeningLessonsFragment());
                DBQuery.g_practiceOptionChoice = "Listening";
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                replaceFragment(new SpeakingLessonsFragment());
                DBQuery.g_practiceOptionChoice = "Speaking";
                break;
        }
    }

    //////////////// Xử lý phần Exam Practice

    //// Thêm các Exam Practice Option
    private void addExamPracticeOptions() {
        examPracticeOptionArrayList = new ArrayList<>();

        examPracticeOptionArrayList.add(new HomePracticeOptions(R.drawable.dictionary_icon, "Tra từ"));
        examPracticeOptionArrayList.add(new HomePracticeOptions(R.drawable.pass_icon, "Bao đỗ"));
        examPracticeOptionArrayList.add(new HomePracticeOptions(R.drawable.listening_icon, "Thi thử 1"));
        examPracticeOptionArrayList.add(new HomePracticeOptions(R.drawable.reading_icon, "Thi thử 2"));

    }

    private void selectExamPracticeOption(int position) {

        switch (position){
            case 0:
                Intent intent = new Intent(getContext(), Translator.class);;

                startActivity(intent);
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:
                break;
            case 4:
                break;
            case 5:

                break;
        }
    }

    /* ======================== */

    //// Thay thế fragment
    private void replaceFragment(Fragment selectedFragment) {
        if (selectedFragment == null) {
            return;
        }
        // Tạo một giao dịch để thay thế fragment
        requireActivity().getSupportFragmentManager() // Thay vì getSupportFragmentManager() vì đây là fragment
                .beginTransaction() // Thêm giao dịch vào hàng đợi
                .replace(R.id.fragmentContainerView, selectedFragment) // Thay thế fragment hiện tại bằng fragment mới
                .commit(); // Thực hiện giao dịch
    }
}