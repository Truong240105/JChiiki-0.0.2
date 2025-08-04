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
import com.example.jchiiki.Questions;
import com.example.jchiiki.R;
import com.example.jchiiki.adaptors.Lessons_Adaptor;
import com.example.jchiiki.adaptors.ListeningLessons_Adaptor;
import com.example.jchiiki.callback.MyCompleteListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListeningLessonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListeningLessonsFragment extends Fragment {
    Dialog progressDialog;
    GridView lesson_GridView;
    ListeningLessons_Adaptor lessonAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListeningLessonsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListeningLessonsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListeningLessonsFragment newInstance(String param1, String param2) {
        ListeningLessonsFragment fragment = new ListeningLessonsFragment();
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
        View view = inflater.inflate(R.layout.fragment_listening_lessons, container, false);
        lesson_GridView = view.findViewById(R.id.lesson_GridView);

        // Khởi tạo Dialog khi đang tải dữ liệu
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog
        progressDialog.show();

        // Tải dữ liệu của bài học từ vựng từ Firebase
        DBQuery.loadVocabularyLessons(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                lessonAdapter = new ListeningLessons_Adaptor(getContext(), R.layout.lesson_item, DBQuery.g_vocabularyLessonList);
                lesson_GridView.setAdapter(lessonAdapter);
                progressDialog.dismiss();

                // Khi dữ liệu được tải xong, cập nhật lại adapter
                lessonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        lesson_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DBQuery.g_selectedLesson_index = i;

                Intent intent = new Intent(getContext(), Questions.class);
                startActivity(intent);
            }
        });

        return view;
    }
}