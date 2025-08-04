package com.example.jchiiki.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.R;
import com.example.jchiiki.Ready;
import com.example.jchiiki.adaptors.Lessons_Adaptor;
import com.example.jchiiki.adaptors.SummarizedPractices_Adaptor;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.items.LessonChoices;
import com.example.jchiiki.items.SummarizedPracticeChoices;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EveryDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EveryDayFragment extends Fragment {
    Toolbar everyDayFragment_ToolBar;
    TabLayout vocabularyPractice_TabLayout, grammarPractice_TabLayout, listeningPractice_TabLayout;
    RecyclerView summarizedPractice_RecyclerView;
    SummarizedPractices_Adaptor summarizedPracticeAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EveryDayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EveryDayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EveryDayFragment newInstance(String param1, String param2) {
        EveryDayFragment fragment = new EveryDayFragment();
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
        View view = inflater.inflate(R.layout.fragment_every_day, container, false);

        everyDayFragment_ToolBar = view.findViewById(R.id.everyDayFragment_ToolBar);
        vocabularyPractice_TabLayout = view.findViewById(R.id.vocabularyPractice_TabLayout);
        grammarPractice_TabLayout = view.findViewById(R.id.grammarPractice_TabLayout);
        listeningPractice_TabLayout = view.findViewById(R.id.listeningPractice_TabLayout);
        summarizedPractice_RecyclerView = view.findViewById(R.id.summarizedPractice_RecyclerView);

        //////////////// Xử lý phần Recycler View
        // Khởi tạo DBQuery.g_lessonList nếu chưa có
        if (DBQuery.g_summarizedPracticeList == null) {
            Log.d("Lessons: DBQuery.g_summarizedPracticeList", "null");
            DBQuery.g_summarizedPracticeList = new ArrayList<>();
        }

        // Sắp xếp lại danh sách các bài luyện tập tổng hợp theo ID
        Collections.sort(DBQuery.g_summarizedPracticeList, new Comparator<SummarizedPracticeChoices>() {
            @Override
            public int compare(SummarizedPracticeChoices o1, SummarizedPracticeChoices o2) {
                return Integer.compare(o1.getSummarizedPracticeID(),o2.getSummarizedPracticeID());
            }
        });

        // Khởi tạo adapter
        summarizedPracticeAdapter = new SummarizedPractices_Adaptor(getContext(), DBQuery.g_summarizedPracticeList);

        //// Nếu danh sách bài học rỗng, tải dữ liệu từ Firebase

        // Lấy cấp độ ngôn ngữ từ SharedPreferences
        SharedPreferences languageLevelPreference = requireActivity().getSharedPreferences("LanguageLevel", MODE_PRIVATE);
        String languageLevel = languageLevelPreference.getString("LanguageLevel", "");

        int level;
        if(languageLevel == ""){
            level = 5;
        }
        else level = Integer.parseInt(languageLevel);
        Log.d("level: ", level+"");

        // Lấy cấp độ ngôn ngữ trước đó từ SharedPreferences (chỉ mang tính Debug)
        String previousLanguageLevel = languageLevelPreference.getString("PreviousLanguageLevel", "");

        int preLevel;
        if(previousLanguageLevel == ""){
            preLevel = -1;
        }
        else preLevel = Integer.parseInt(previousLanguageLevel);
        Log.d("preLevel: ", preLevel+"");

        if (DBQuery.g_summarizedPracticeList == null || DBQuery.g_summarizedPracticeList.size() == 0) {
            Log.d("EveryDayFragment: DBQuery.g_summarizedPracticeList", "null");
            DBQuery.loadSummarizedPractices(level, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Log.d("EveryDayFragment: onSuccess", "SUCCESS");
                    // Khi dữ liệu được tải xong, cập nhật lại adapter
                    summarizedPracticeAdapter.notifyDataSetChanged();

                    // Cập nhật lại PreviousLanguageLevel để khớp với LanguageLevel hiện tại
                    SharedPreferences languageLevelPreference = requireActivity().getSharedPreferences("LanguageLevel", MODE_PRIVATE);
                    SharedPreferences.Editor editor = languageLevelPreference.edit();
                    editor.putString("PreviousLanguageLevel", languageLevelPreference.getString("LanguageLevel", ""));
                    editor.apply();
                }

                @Override
                public void onFailure() {
                    Log.d("EveryDayFragment: onFailure", "FAILURE");
                    // Xử lý lỗi nếu cần
                }
            });
        } else {
            Log.d("EveryDayFragment: DBQuery.g_summarizedPracticeList", "NOT NULL");
            // Nếu đã có dữ liệu, chỉ cần cập nhật adapter (hoặc không cần làm gì thêm)
            summarizedPracticeAdapter.notifyDataSetChanged();
        }

        summarizedPractice_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1)); // Số cột = 1
        summarizedPractice_RecyclerView.setAdapter(summarizedPracticeAdapter);

        return view;
    }

    //////////////// Xử lý phần Tab Layout (Vocabulary Practice)
    private void selectedVocabularyPracticeTabLayoutItem(){
        vocabularyPractice_TabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        Intent intent = new Intent(getContext(), Ready.class);
                        startActivity(intent);
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    //////////////// Xử lý phần Tab Layout (Grammar Practice)
    private void selectedGrammarPracticeTabLayoutItem(){
        grammarPractice_TabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        replaceFragment(new GrammarLessonsFragment());
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //////////////// Xử lý phần Tab Layout (Listening Practice)
    private void selectedListeningPracticeTabLayoutItem(){
        listeningPractice_TabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        Intent intent = new Intent(getContext(), Ready.class);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

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