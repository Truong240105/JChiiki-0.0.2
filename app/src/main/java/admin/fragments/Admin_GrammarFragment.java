package admin.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jchiiki.R;
import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.items.GrammarLessonChoices;

import java.util.ArrayList;

import admin.Admin_DBQuery;
import admin.adaptors.GrammarContent_AdminAdaptor;
import admin.adaptors.GrammarLessons_AdminAdaptor;
import admin.callback.MyCompleteListenerWithData2;
import admin.items.AdminGrammarPoint;

/**
 * Fragment quản lý ngữ pháp cho admin
 */
public class Admin_GrammarFragment extends Fragment {
    LinearLayout addGrammarPoint_Container;
    Dialog progressDialog;
    Button addGrammarLessons_Button, grammarLessonTitleSearch_Button, addGrammarPoint_Button;
    RecyclerView grammarLessons_RecyclerView, grammarContent_RecyclerView;
    EditText grammarLessonTitleSearch_EditText;
    GrammarLessons_AdminAdaptor grammarLessons_adminAdaptor;
    GrammarContent_AdminAdaptor grammarContent_adminAdaptor;
    AlertDialog.Builder builder;
    
    String search;
    Integer grammarContentCount = 0;

    public Admin_GrammarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_grammar, container, false);

        // Khởi tạo các thành phần giao diện
        addGrammarPoint_Container = view.findViewById(R.id.addGrammarPoint_Container);
        addGrammarLessons_Button = view.findViewById(R.id.addGrammarLessons_Button);
        addGrammarPoint_Button = view.findViewById(R.id.addGrammarPoint_Button);
        grammarLessonTitleSearch_Button = view.findViewById(R.id.grammarLessonTitleSearch_Button);
        grammarLessons_RecyclerView = view.findViewById(R.id.grammarLessons_RecyclerView);
        grammarContent_RecyclerView = view.findViewById(R.id.grammarContent_RecyclerView);
        grammarLessonTitleSearch_EditText = view.findViewById(R.id.grammarLessonTitleSearch_EditText);

        // Khởi tạo Dialog loading
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog.show();

        // Xử lý phần Recycler View của Grammar Lessons
        if (Admin_DBQuery.g_grammarLessonList == null) {
            Admin_DBQuery.g_grammarLessonList = new ArrayList<>();
        }

        grammarLessons_adminAdaptor = new GrammarLessons_AdminAdaptor(getContext(), Admin_DBQuery.g_grammarLessonList);

        if (Admin_DBQuery.g_grammarLessonList == null || Admin_DBQuery.g_grammarLessonList.size() == 0) {
            Admin_DBQuery.loadGrammarLessons(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    grammarLessons_adminAdaptor.notifyDataSetChanged();
                }

                @Override
                public void onFailure() {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Đã xảy ra lỗi ! Vui lòng thử lại sau.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            grammarLessons_adminAdaptor.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        grammarLessons_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        grammarLessons_RecyclerView.setAdapter(grammarLessons_adminAdaptor);

        // Xử lý thêm bài học ngữ pháp
        addGrammarLessons_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGrammarLessonDialog();
            }
        });

        // Xử lý phần Recycler View của Grammar Content
        addGrammarPoint_Container.setVisibility(View.GONE);

        grammarLessonTitleSearch_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = grammarLessonTitleSearch_EditText.getText().toString().trim();

                if (search.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập tiêu đề bài học!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    Admin_DBQuery.searchGrammarPoints(search, new MyCompleteListenerWithData2() {
                        @Override
                        public void onSuccess(ArrayList<AdminGrammarPoint> arrayList) {
                            grammarContentCount = arrayList.size();

                            grammarContent_adminAdaptor = new GrammarContent_AdminAdaptor(getContext(), arrayList, search);
                            grammarContent_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            grammarContent_RecyclerView.setAdapter(grammarContent_adminAdaptor);

                            addGrammarPoint_Container.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            grammarContent_RecyclerView.setAdapter(null);
                            addGrammarPoint_Container.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Không tìm thấy điểm ngữ pháp nào! Vui lòng kiểm tra lại tiêu đề bài học", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Xử lý thêm điểm ngữ pháp
        addGrammarPoint_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGrammarPointDialog();
            }
        });

        return view;
    }

    // Hàm hiển thị dialog thêm bài học ngữ pháp
    public void showGrammarLessonDialog(){
        builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_grammar_lesson_dialog, null);
        builder.setView(view);

        EditText lessonTitle_EditText = view.findViewById(R.id.grammarLessonTitle_EditText);
        EditText lessonName_EditText = view.findViewById(R.id.grammarLessonName_EditText);
        EditText lessonImage_EditText = view.findViewById(R.id.grammarLessonImage_EditText);

        builder.setTitle("Thêm bài học ngữ pháp");

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String lessonTitle = lessonTitle_EditText.getText().toString().trim();
            String lessonName = lessonName_EditText.getText().toString().trim();
            String lessonImage = lessonImage_EditText.getText().toString().trim();

            if (lessonTitle.isEmpty() || lessonName.isEmpty() || lessonImage.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (lessonTitle.length() < 5 || !lessonTitle.startsWith("Bài ")) {
                Toast.makeText(getContext(), "Tiêu đề phải theo định dạng: Bài <số>", Toast.LENGTH_SHORT).show();
                return;
            }

            String numberPart = lessonTitle.substring(4);
            if (!numberPart.matches("\\d+")) {
                Toast.makeText(getContext(), "Tiêu đề phải theo định dạng: Bài <số>", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("grammarLessonTitle", "SUCCESSFUL");
            Admin_DBQuery.addGrammarLesson(lessonTitle, lessonName, lessonImage, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Admin_DBQuery.loadGrammarLessons(new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            grammarLessons_adminAdaptor.notifyDataSetChanged();
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

    // Hàm hiển thị dialog thêm điểm ngữ pháp
    public void showGrammarPointDialog(){
        builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_grammar_point_dialog, null);
        builder.setView(view);

        EditText grammarPoint_EditText = view.findViewById(R.id.grammarPoint_EditText);
        EditText explanation_EditText = view.findViewById(R.id.grammarExplanation_EditText);
        EditText example_EditText = view.findViewById(R.id.grammarExample_EditText);
        EditText translation_EditText = view.findViewById(R.id.grammarTranslation_EditText);

        builder.setTitle("Thêm điểm ngữ pháp");

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String grammarPoint = grammarPoint_EditText.getText().toString().trim();
            String explanation = explanation_EditText.getText().toString().trim();
            String example = example_EditText.getText().toString().trim();
            String translation = translation_EditText.getText().toString().trim();
            String lessonTitle = grammarLessonTitleSearch_EditText.getText().toString().trim();

            if (!grammarPoint.isEmpty() && !explanation.isEmpty() && !example.isEmpty() && !translation.isEmpty()) {
                Admin_DBQuery.addGrammarPoint(lessonTitle, grammarContentCount, grammarPoint, explanation, example, translation, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), "Thêm thành công!", Toast.LENGTH_SHORT).show();

                        progressDialog.show();
                        Admin_DBQuery.searchGrammarPoints(search, new MyCompleteListenerWithData2() {
                            @Override
                            public void onSuccess(ArrayList<AdminGrammarPoint> arrayList) {
                                grammarContentCount = arrayList.size();

                                grammarContent_adminAdaptor = new GrammarContent_AdminAdaptor(getContext(), arrayList, search);
                                grammarContent_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                grammarContent_RecyclerView.setAdapter(grammarContent_adminAdaptor);

                                addGrammarPoint_Container.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                grammarContent_RecyclerView.setAdapter(null);
                                addGrammarPoint_Container.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Không tìm thấy điểm ngữ pháp nào! Vui lòng kiểm tra lại tiêu đề bài học", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getActivity(), "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
} 