package admin.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jchiiki.DBQuery;
import com.example.jchiiki.LessonContent;
import com.example.jchiiki.Login;
import com.example.jchiiki.R;
import com.example.jchiiki.adaptors.LessonContent_Adaptor;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.decorations.SpaceItemDecoration;
import com.example.jchiiki.items.VocabularyWords;

import java.util.ArrayList;

import admin.Admin_DBQuery;
import admin.adaptors.LessonContent_AdminAdaptor;
import admin.adaptors.Lessons_AdminAdaptor;
import admin.callback.MyCompleteListenerWithData2;
import admin.items.AdminVocabularyWords;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Admin_HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Admin_HomeFragment extends Fragment {
    LinearLayout addWord_Container;
    Dialog progressDialog;
    Button addVocabularyLessons_Button, lessonTitleSearch_Button, addWord_Button;
    RecyclerView vocabularyLessons_RecyclerView, lessonContent_RecyclerView;
    EditText lessonTitleSearch_EditText;
    Lessons_AdminAdaptor lessons_adminAdaptor;
    LessonContent_AdminAdaptor lessonContent_adminAdaptor;
    AlertDialog.Builder builder;
    // Các biến khác
    String search;
    Integer lessonContentCount = 0; // Số lượng từ vựng trong bài học

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Admin_HomeFragment() {
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
    public static Admin_HomeFragment newInstance(String param1, String param2) {
        Admin_HomeFragment fragment = new Admin_HomeFragment();
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
        View view = inflater.inflate(R.layout.admin_fragment_home, container, false);

        // Khởi tạo các thành phần giao diện
        addWord_Container = view.findViewById(R.id.addWord_Container);
        addVocabularyLessons_Button = view.findViewById(R.id.addVocabularyLessons_Button);
        addWord_Button = view.findViewById(R.id.addWord_Button);
        lessonTitleSearch_Button = view.findViewById(R.id.lessonTitleSearch_Button);
        vocabularyLessons_RecyclerView = view.findViewById(R.id.vocabularyLessons_RecyclerView);
        lessonContent_RecyclerView = view.findViewById(R.id.lessonContent_RecyclerView);
        lessonTitleSearch_EditText = view.findViewById(R.id.lessonTitleSearch_EditText);

        /// Khởi tạo Dialog khi đang tải các câu hỏi
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog); // Đặt layout cho Dialog
        progressDialog.setCancelable(false); // Không cho phép tắt bằng cách nhấp vào ngoài
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Đặt kích thước cho Dialog
        progressDialog.show();

        ////////////////////////// Xử lý phần Recycler View của Vocabulary Lessons

        // Khởi tạo DBQuery.g_lessonList nếu chưa có
        if (Admin_DBQuery.g_vocabularyLessonList == null) {
            Admin_DBQuery.g_vocabularyLessonList = new ArrayList<>();
        }

        // Khởi tạo và thiết lập Adapter cho RecyclerView
        lessons_adminAdaptor = new Lessons_AdminAdaptor(getContext(), Admin_DBQuery.g_vocabularyLessonList);

        //// Xử lý hiển thị danh sách các bài học từ vựng
        if (Admin_DBQuery.g_vocabularyLessonList == null || Admin_DBQuery.g_vocabularyLessonList.size() == 0) {
            Admin_DBQuery.loadVocabularyLessons(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    lessons_adminAdaptor.notifyDataSetChanged();
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
            lessons_adminAdaptor.notifyDataSetChanged();

            progressDialog.dismiss();
        }

        vocabularyLessons_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        vocabularyLessons_RecyclerView.setAdapter(lessons_adminAdaptor);

        //// Xử lý thêm bài học từ vựng
        addVocabularyLessons_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVocabularyLessonDialog();
            }
        });

        ////////////////////////// Xử lý phần Recycler View của Lesson Content
        addWord_Container.setVisibility(View.GONE);

        //// Xử lý tải danh sách các từ vựng

        lessonTitleSearch_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = lessonTitleSearch_EditText.getText().toString().trim(); // Lấy giá trị cần tìm kiếm từ EditText

                if (search.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập tiêu đề bài học!", Toast.LENGTH_SHORT).show();
                } else {
                    Admin_DBQuery.searchVocabularyWords(search, new MyCompleteListenerWithData2() {
                        @Override
                        public void onSuccess(ArrayList<AdminVocabularyWords> arrayList) {
                            lessonContentCount = arrayList.size();

                            lessonContent_adminAdaptor = new LessonContent_AdminAdaptor(getContext(), arrayList, search);
                            lessonContent_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            lessonContent_RecyclerView.setAdapter(lessonContent_adminAdaptor);

                            addWord_Container.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            lessonContent_RecyclerView.setAdapter(null);

                            addWord_Container.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Không tìm thấy từ vựng nào! Vui lòng kiểm tra lại tiêu đề bài học", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //// Xử lý thêm bài học từ vựng
        addWord_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLessonContentDialog();
            }
        });

        return view;
    }

    // Hàm showVocabularyLessonDialog để hiển thị dialog thêm bài học từ vựng
    public void showVocabularyLessonDialog(){
        builder = new AlertDialog.Builder(getActivity()); // Khởi tạo builder
        View view = getLayoutInflater().inflate(R.layout.add_vocabulary_lesson_dialog, null); // Khởi tạo view và gán layout cho nó
        builder.setView(view);

        EditText lessonTitle_EditText = view.findViewById(R.id.lessonTitle_EditText);
        EditText lessonName_EditText = view.findViewById(R.id.lessonName_EditText);
        EditText lessonImage_EditText = view.findViewById(R.id.lessonImage_EditText);

        builder.setTitle("Thêm bài hoc từ vựng");

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String lessonTitle = lessonTitle_EditText.getText().toString().trim();
            String lessonName = lessonName_EditText.getText().toString().trim();
            String lessonImage = lessonImage_EditText.getText().toString().trim();

            // Kiểm tra rỗng
            if (lessonTitle.isEmpty() || lessonName.isEmpty() || lessonImage.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng "Bài <số>"
            if (lessonTitle.length() < 5 || !lessonTitle.startsWith("Bài ")) {
                Toast.makeText(getContext(), "Tiêu đề phải theo định dạng: Bài <số>", Toast.LENGTH_SHORT).show();
                return;
            }

            String numberPart = lessonTitle.substring(4); // từ ký tự thứ 4 trở đi
            if (!numberPart.matches("\\d+")) {
                Toast.makeText(getContext(), "Tiêu đề phải theo định dạng: Bài <số>", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("lessonTitle", "SUCCESSFUL");
            // Nếu tất cả hợp lệ → thêm bài học
            Admin_DBQuery.addVocabularyLesson(lessonTitle, lessonName, lessonImage, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Admin_DBQuery.loadVocabularyLessons(new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            lessons_adminAdaptor.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure() {}
                    });
                    Toast.makeText(getActivity(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });

        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public void showLessonContentDialog(){
        builder = new AlertDialog.Builder(getActivity()); // Khởi tạo builder
        View view = getLayoutInflater().inflate(R.layout.add_word_dialog, null); // Khởi tạo view và gán layout cho nó
        builder.setView(view);

        EditText content_EditText = view.findViewById(R.id.content_EditText);
        EditText simplifiedContent_EditText = view.findViewById(R.id.simplifiedContent_EditText);
        EditText pronunciation_EditText = view.findViewById(R.id.pronunciation_EditText);
        EditText meaning_EditText = view.findViewById(R.id.meaning_EditText);

        builder.setTitle("Thêm từ vựng");

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String content = content_EditText.getText().toString().trim();
            String simplifiedContent = simplifiedContent_EditText.getText().toString().trim();
            String pronunciation = pronunciation_EditText.getText().toString().trim();
            String meaning = meaning_EditText.getText().toString().trim();
            String lessonTitle = lessonTitleSearch_EditText.getText().toString().trim();
            Log.d("lessonTitle",lessonTitle);

            if (!content.isEmpty() && !pronunciation.isEmpty() && !meaning.isEmpty()) {
                Admin_DBQuery.addLessonContent(lessonTitle,lessonContentCount, content, simplifiedContent, pronunciation, meaning, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), "Thêm thành công!", Toast.LENGTH_SHORT).show();

                        progressDialog.show();
                        Admin_DBQuery.searchVocabularyWords(search, new MyCompleteListenerWithData2() {
                            @Override
                            public void onSuccess(ArrayList<AdminVocabularyWords> arrayList) {
                                lessonContentCount = arrayList.size();

                                lessonContent_adminAdaptor = new LessonContent_AdminAdaptor(getContext(), arrayList, search);
                                lessonContent_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                lessonContent_RecyclerView.setAdapter(lessonContent_adminAdaptor);

                                addWord_Container.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                lessonContent_RecyclerView.setAdapter(null);

                                addWord_Container.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Không tìm thấy từ vựng nào! Vui lòng kiểm tra lại tiêu đề bài học", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getActivity(), "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}