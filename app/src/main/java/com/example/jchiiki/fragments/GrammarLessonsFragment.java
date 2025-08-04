package com.example.jchiiki.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.LessonContent;
import com.example.jchiiki.R;
import com.example.jchiiki.adaptors.GrammarLessons_Adaptor;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.items.GrammarLessonChoices;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GrammarLessonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrammarLessonsFragment extends Fragment {
    Dialog progressDialog;
    GridView grammarLesson_GridView;
    ArrayList<GrammarLessonChoices> grammarLessonArrayList;
    GrammarLessons_Adaptor grammarLessonAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GrammarLessonsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GrammarLessonsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GrammarLessonsFragment newInstance(String param1, String param2) {
        GrammarLessonsFragment fragment = new GrammarLessonsFragment();
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
        View view = inflater.inflate(R.layout.fragment_grammar_lessons, container, false);
        grammarLesson_GridView = view.findViewById(R.id.grammarLesson_GridView);
        grammarLessonArrayList = new ArrayList<>();

        /// Khởi tạo Dialog khi đang tải các câu hỏi
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog
        progressDialog.show();

        // Khởi tạo DBQuery.g_lessonList nếu chưa có
        if (DBQuery.g_grammarLessonList == null) {
            DBQuery.g_grammarLessonList = new ArrayList<>();
        }

        grammarLessonAdapter = new GrammarLessons_Adaptor(getContext(), R.layout.lesson_item, DBQuery.g_grammarLessonList);
        grammarLesson_GridView.setAdapter(grammarLessonAdapter);

        // Nếu danh sách bài học rỗng, tải dữ liệu từ Firebase
        if (DBQuery.g_grammarLessonList == null || DBQuery.g_grammarLessonList.size() == 0) {
            DBQuery.loadGrammarLessons(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();

                    // Khi dữ liệu được tải xong, cập nhật lại adapter
                    grammarLessonAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure() {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu đã có dữ liệu, chỉ cần cập nhật adapter (hoặc không cần làm gì thêm)
            grammarLessonAdapter.notifyDataSetChanged();
        }

        grammarLesson_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DBQuery.g_selectedLesson_index = position;

                Intent intent = new Intent(getContext(), LessonContent.class);
                startActivity(intent);

            }
        });

        return view;
    }

}