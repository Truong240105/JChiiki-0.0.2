package admin;

import static java.security.AccessController.getContext;

import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.callback.MyCompleteListenerWithData;
import com.example.jchiiki.items.GrammarLessonChoices;
import com.example.jchiiki.items.QuestionItems;
import com.example.jchiiki.items.SummarizedPracticeChoices;
import com.example.jchiiki.items.VocabularyWords;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.Map;

import admin.callback.MyCompleteListenerWithData2;
import admin.items.AdminLessonChoices;
import admin.items.AdminVocabularyWords;
import admin.items.AdminGrammarPoint;

public class Admin_DBQuery {
    // Access a Cloud Firestore instance from your Activity
    public static FirebaseFirestore g_firestore; // Khai báo biến static để lưu trữ instance của FirebaseFirestore trong toàn bộ ứng dụng
    public static Integer g_vocabularyLessonNumber, g_grammarLessonNumber, g_lessonContentNumber; // Khai báo biến static để lưu trữ số lượng bài học và chế độ học (HomeFragment LessonsFragment; LessonsModes) trong toàn bộ ứng dụng
    public static Integer g_summarizedPracticeNumber_n5, g_summarizedPracticeNumber_n4, g_summarizedPracticeNumber_n3, g_summarizedPracticeNumber_n2, g_summarizedPracticeNumber_n1; // Khai báo biến static để lưu trữ số lượng bài luyện tập tổng hợp
    public static ArrayList<AdminLessonChoices> g_vocabularyLessonList; // Khai báo biến static để lưu trữ danh sách các bài học từ vựng (HomeFragment) trong toàn bộ ứng dụng
    public static ArrayList<GrammarLessonChoices> g_grammarLessonList; // Khai báo biến static để lưu trữ danh sách các bài học ngữ pháp (HomeFragment) trong toàn bộ ứng dụng
    public static ArrayList<SummarizedPracticeChoices> g_summarizedPracticeList; // Khai báo biến static để lưu trữ danh sách các bài luyện tập tổng hợp (EveryDayFragment) trong toàn bộ ứng dụng
    public static ArrayList<QuestionItems> g_summarizedPracticeQuestionList; // Khai báo biến static để lưu trữ danh sách các câu hỏi trong bài luyện tập tổng hợp (EveryDayFragment) trong toàn bộ ứng dụng
    public static int g_selectedLesson_index = 0, g_selectedTest_index = 0; // Khai báo biến static để lưu trữ bài học, bài test được chọn trong toàn bộ ứng dụng
    public static ArrayList<AdminVocabularyWords> g_lessonContentList; // Khai báo biến static để lưu trữ danh sách các chế độ học trong toàn bộ ứng dụng

    ///// Hàm adminAuthentifications để kiểm tra nếu người đang đăng nhập là Admin
    public static void adminAuthentifications(String uid, MyCompleteListenerWithData myCompleteListenerWithData){
        g_firestore = FirebaseFirestore.getInstance(); // Khởi tạo instance của FirebaseFirestore
        g_firestore.collection("Users").document(uid).get() // Truy cập vào Collection "Users" và lấy document có id là uid
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        String role = userData.get("ROLE").toString(); // Lấy giá trị của field "ROLE" trong document
                        Long isAdmin;

                        if(role.equals("Admin")){
                            isAdmin = (long) 1;
                            myCompleteListenerWithData.onSuccess(isAdmin);
                        }
                        else{
                            isAdmin = (long) 0;
                            myCompleteListenerWithData.onSuccess(isAdmin);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListenerWithData.onFailure(e);
                    }
                });
    }

    ///// Hàm getLessonNumber để lấy số lượng bài học trong Collection "Lessons"
    public static void getLessonNumber(final MyCompleteListenerWithData myCompleteListenerWithData){ // có thể thêm để truyền sang interface cho xử lý bất đồng bộ: (final MyCompleListenerWithData myCompleteListenerWithData)
        Log.d("getLessonNumber", "accessed SUCCESSFULLY ");
        g_firestore.collection("Lessons").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // queryDocumentSnapshots nắm tất cả các Documents trong Collection "Lessons"

                        //Chúng ta đã có biến queryDocumentSnapshots, việc cần làm là làm sao để lấy được toàn bộ các Documents trong đó. Để làm được,
                        //ta cần giải nén queryDocumentSnapshots bằng việc dùng 1 Map

                        Map<String, QueryDocumentSnapshot> lessonsDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonsDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonsDocumentList = lessonsDocumentMap.get("TOTAL_LESSONS"); // Truy cập vào Document có id là "TOTAL_LESSONS"

                        if (lessonsDocumentList != null) {
                            long lessonNumber = Long.parseLong(lessonsDocumentList.get("COUNT").toString());
                            g_vocabularyLessonNumber = (int) lessonNumber;
                            myCompleteListenerWithData.onSuccess(lessonNumber); // Truyền lessonNumber ra ngoài qua callback
                        } else {
                            myCompleteListenerWithData.onFailure(new Exception("Document TOTAL_LESSONS not found"));
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("getLessonNumber->onFailure", "accessed SUCCESSFULLY ");
                        //myCompleteListenerWithData.onFailure(e);
                        e.printStackTrace();
                    }
                });
    }


    ///////////////// Các hàm để lấy danh sách các bài học

    //// Hàm loadVocabularyLessons để lấy danh sách các bài học từ vựng trong Collection "Lessons"
    public static void loadVocabularyLessons(final MyCompleteListener myCompleteListener){
        Log.d("loadVocabularyLessons","accessed SUCCESSFULLY");
        if (g_vocabularyLessonList == null) {
            g_vocabularyLessonList = new ArrayList<>();
        }
        g_vocabularyLessonList.clear(); // Xóa dữ liệu cũ trong lessonList

        g_firestore.collection("Lessons").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // queryDocumentSnapshots nắm tất cả các Documents trong Collection "Lessons"

                        /* Chúng ta đã có biến queryDocumentSnapshots, việc cần làm là làm sao để lấy được toàn bộ các Documents trong đó. Để làm được,
                        ta cần giải nén queryDocumentSnapshots bằng việc dùng 1 Map

                        */

                        Map<String, QueryDocumentSnapshot> lessonsDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonsDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonsDocumentList = lessonsDocumentMap.get("TOTAL_LESSONS"); // Truy cập vào Document có id là "TOTAL_LESSONS"

                        long lessonNumber = Long.parseLong(lessonsDocumentList.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_LESSONS"
                        g_vocabularyLessonNumber = (int) lessonNumber;

                        /*
                            Ta truy cập các fields còn lại chứa các "Lesson<i>_ID" có giá trị bằng chính ID của các Documents (là các bài học) trong
                            Document "Lessons".
                        */
                        for(int i=1;i<=g_vocabularyLessonNumber;i++){
                            String lessonId = lessonsDocumentList.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "LESSON_"+i+"_ID" (chính là ID của mỗi lesson) trong Document "TOTAL_LESSONS"")
                            QueryDocumentSnapshot lessonDocument = lessonsDocumentMap.get(lessonId); // Truy cập vào Document có ID là <giá trị của lessonID>

                            String lessonID = lessonDocument.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "Lesson"+i+"_ID" trong Document "Lessons"
                            String lessonName = lessonDocument.getString("Lesson" + i + "_Name"); // Lấy giá trị của field "Lesson"+i+"_Name" trong Document "Lessons"
                            String lessonTitle = lessonDocument.getString("Lesson" + i + "_Title"); // Lấy giá trị của field "Lesson"+i+"_Title" trong Document "Lessons"
                            String lessonImage = lessonDocument.getString("Lesson" + i + "_Image"); // Lấy giá trị của field "Lesson"+i+"_Image" trong Document "Lessons"

                            g_vocabularyLessonList.add(new AdminLessonChoices(lessonID, lessonTitle, lessonName, lessonImage));
                        }

                        myCompleteListener.onSuccess(); // Truyền lessonNumber ra ngoài qua callback


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    ///// Hàm loadLessons để lấy danh sách các bài học trong Collection "Grammars"
    public static void loadGrammarLessons(final MyCompleteListener myCompleteListener){
        Log.d("loadGrammarLessons","accessed SUCCESSFULLY");
        if (g_grammarLessonList == null) {
            g_grammarLessonList = new ArrayList<>();
        }
        g_grammarLessonList.clear(); // Xóa dữ liệu cũ trong lessonList

        g_firestore.collection("Grammars").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // queryDocumentSnapshots nắm tất cả các Documents trong Collection "Lessons"

                        /* Chúng ta đã có biến queryDocumentSnapshots, việc cần làm là làm sao để lấy được toàn bộ các Documents trong đó. Để làm được,
                        ta cần giải nén queryDocumentSnapshots bằng việc dùng 1 Map

                        */

                        Map<String, QueryDocumentSnapshot> lessonsDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonsDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonsDocumentList = lessonsDocumentMap.get("TOTAL_GRAMMARS"); // Truy cập vào Document có id là "TOTAL_LESSONS"

                        long lessonNumber = Long.parseLong(lessonsDocumentList.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_LESSONS"
                        g_grammarLessonNumber = (int) lessonNumber;
                        Log.d("g_lessonNumber",g_grammarLessonNumber+"");

                        /*
                            Ta truy cập các fields còn lại chứa các "Lesson<i>_ID" có giá trị bằng chính ID của các Documents (là các bài học) trong
                            Document "Lessons".
                        */
                        for(int i=1;i<=g_grammarLessonNumber;i++){
                            String lessonId = lessonsDocumentList.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "LESSON_"+i+"_ID" (chính là ID của mỗi lesson) trong Document "TOTAL_LESSONS"")
                            Log.d("grammarLessonID" + i,lessonId+"");
                            QueryDocumentSnapshot lessonDocument = lessonsDocumentMap.get(lessonId); // Truy cập vào Document có ID là <giá trị của lessonID>

                            String lessonID = lessonDocument.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "Lesson"+i+"_ID" trong Document "Lessons"
                            String lessonName = lessonDocument.getString("Lesson" + i + "_Name"); // Lấy giá trị của field "Lesson"+i+"_Name" trong Document "Lessons"
                            String lessonTitle = lessonDocument.getString("Lesson" + i + "_Title"); // Lấy giá trị của field "Lesson"+i+"_Title" trong Document "Lessons"

                            Log.d("grammarLesson" + i + "_ID",lessonID+"");
                            Log.d("grammarLesson" + i + "_Name",lessonName+"");
                            Log.d("grammarLesson" + i + "_Title",lessonTitle+"");

                            g_grammarLessonList.add(new GrammarLessonChoices(lessonID, lessonTitle, lessonName));
                        }

                        myCompleteListener.onSuccess(); // Truyền lessonNumber ra ngoài qua callback


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    ///////////////// Các hàm để lấy danh sách các bài luyện tập ở EveryDayFragment

    //// Hàm loadSummarizedPractices để lấy danh sách các luyện tập tổng hợp trong Collection "Summarized_Practices"
    public static void loadSummarizedPractices(Integer level, final MyCompleteListener myCompleteListener){
        Log.d("loadSummarizedPractices","accessed SUCCESSFULLY");
        if (g_summarizedPracticeList == null) {
            g_summarizedPracticeList = new ArrayList<>();
        }
        g_summarizedPracticeList.clear(); // Xóa dữ liệu cũ trong summarizedPracticeList

        g_firestore.collection("Summarized_Practices").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // queryDocumentSnapshots nắm tất cả các Documents trong Collection "Summarized_Practices"

                        /* Chúng ta đã có biến queryDocumentSnapshots, việc cần làm là làm sao để lấy được toàn bộ các Documents trong đó. Để làm được,
                        ta cần giải nén queryDocumentSnapshots bằng việc dùng 1 Map

                        */

                        Map<String, QueryDocumentSnapshot> lessonsDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonsDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }


                        QueryDocumentSnapshot lessonsDocumentList_N5 = lessonsDocumentMap.get("LEVEL_N5"); // Truy cập vào Document có id là "LEVEL_N5"
                        QueryDocumentSnapshot lessonsDocumentList_N4 = lessonsDocumentMap.get("LEVEL_N4"); // Truy cập vào Document có id là "LEVEL_N4"
                        QueryDocumentSnapshot lessonsDocumentList_N3 = lessonsDocumentMap.get("LEVEL_N3"); // Truy cập vào Document có id là "LEVEL_N3"
                        QueryDocumentSnapshot lessonsDocumentList_N2 = lessonsDocumentMap.get("LEVEL_N2"); // Truy cập vào Document có id là "LEVEL_N2"
                        QueryDocumentSnapshot lessonsDocumentList_N1 = lessonsDocumentMap.get("LEVEL_N1"); // Truy cập vào Document có id là "LEVEL_N1"

                        g_summarizedPracticeNumber_n5 = Integer.parseInt(lessonsDocumentList_N5.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "LEVEL_N5"
                        Log.d("g_summarizedPracticeNumber_n5",g_summarizedPracticeNumber_n5+"");

                        g_summarizedPracticeNumber_n4 = Integer.parseInt(lessonsDocumentList_N4.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "LEVEL_N4"
                        Log.d("g_summarizedPracticeNumber_n4",g_summarizedPracticeNumber_n4+"");

                        //g_summarizedPracticeNumber_n3 = Integer.parseInt(lessonsDocumentList_N3.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "LEVEL_N3"
                        Log.d("g_summarizedPracticeNumber_n3",g_summarizedPracticeNumber_n3+"");

                        //g_summarizedPracticeNumber_n2 = Integer.parseInt(lessonsDocumentList_N2.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "LEVEL_N2"
                        Log.d("g_summarizedPracticeNumber_n2",g_summarizedPracticeNumber_n2+"");

                        //g_summarizedPracticeNumber_n1 = Integer.parseInt(lessonsDocumentList_N1.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "LEVEL_N1"
                        Log.d("g_summarizedPracticeNumber_n1",g_summarizedPracticeNumber_n1+"");

                        /*
                            Ta truy cập các fields còn lại chứa các "Lesson<i>_ID" có giá trị bằng chính ID của các Documents (là các bài test) trong
                            Document "Summarized_Practices".
                        */
                        switch (level){
                            case 5:
                                for(int i=1;i<=g_summarizedPracticeNumber_n5;i++){
                                    final Integer number = i;
                                    g_firestore.collection("Summarized_Practices").document("LEVEL_N5")
                                            .collection("Practice"+i).document("INFO").get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Integer practiceID = documentSnapshot.getLong("Practice"+number+"_ID").intValue();
                                                    Integer questionNumber = documentSnapshot.getLong("Practice"+number+"_QuestionNumber").intValue();
                                                    String practiceLevel = documentSnapshot.getString("Practice"+number+"_Level");
                                                    String practiceTitle = documentSnapshot.getString("Practice"+number+"_Title");


                                                    g_summarizedPracticeList.add(new SummarizedPracticeChoices(practiceID, questionNumber, practiceLevel, practiceTitle));
                                                    Log.d("practice"+number+"_ID",practiceID+"");
                                                    Log.d("practice"+number+"_QuestionNumber",questionNumber+"");
                                                    Log.d("practice"+number+"_Level",practiceLevel+"");
                                                    Log.d("practice"+number+"_Title",practiceTitle+"");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                }
                                break;
                            case 4:
                                for(int i=1;i<=g_summarizedPracticeNumber_n4;i++){
                                    final Integer number = i;
                                    g_firestore.collection("Summarized_Practices").document("LEVEL_N4")
                                            .collection("Practice"+i).document("INFO").get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Integer practiceID = documentSnapshot.getLong("Practice"+number+"_ID").intValue();
                                                    Integer questionNumber = documentSnapshot.getLong("Practice"+number+"_QuestionNumber").intValue();
                                                    String practiceLevel = documentSnapshot.getString("Practice"+number+"_Level");
                                                    String practiceTitle = documentSnapshot.getString("Practice"+number+"_Title");

                                                    g_summarizedPracticeList.add(new SummarizedPracticeChoices(practiceID, questionNumber, practiceLevel, practiceTitle));
                                                    Log.d("practice"+number+"_ID",practiceID+"");
                                                    Log.d("practice"+number+"_Level",practiceLevel+"");
                                                    Log.d("practice"+number+"_Title",practiceTitle+"");
                                                    Log.d("practice"+number+"_QuestionNumber",questionNumber+"");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                }
                                break;
                            case 3:
                                break;
                            case 2:
                                break;
                            case 1:
                                break;
                        }


                        myCompleteListener.onSuccess(); // Truyền lessonNumber ra ngoài qua callback


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

    }
    //// Hàm loadSummarizedPracticeQuestions để lấy danh sách các câu hỏi cho bài luyện tập tổng hợp trong Collection "VocabularyQuestions"
    public static void loadSummarizedPracticeQuestions(Integer level, final MyCompleteListener myCompleteListener){
        Log.d("loadSummarizedPracticeQuestions","accessed SUCCESSFULLY");
        if (g_summarizedPracticeQuestionList == null) {
            g_summarizedPracticeQuestionList = new ArrayList<>();
        }
        g_summarizedPracticeQuestionList.clear(); // Xóa dữ liệu cũ trong summarizedPracticeQuestionList

        // Xét điều kiện để lấy các câu hỏi
        g_firestore.collection("VocabularyQuestions").document("N"+level).collection("Practice"+(g_selectedTest_index+1))
                .whereEqualTo("Question_Level",level) // tương ứng với trình độ
                .whereEqualTo("Practice_ID",g_selectedTest_index+1) // tương ứng với id của bài test đã chọn
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i=1;
                        // Lặp để lấy các câu hỏi tương ứng với điều kiện
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String question = doc.getString("QUESTION");
                            String optionA = doc.getString("A");
                            String optionB = doc.getString("B");
                            String optionC = doc.getString("C");
                            String optionD = doc.getString("D");
                            Integer answer = doc.getLong("ANSWER").intValue();

                            // Thêm câu hỏi vào danh sách
                            g_summarizedPracticeQuestionList.add(new QuestionItems(question, optionA, optionB, optionC, optionD, answer, -1)); // -1 là chưa chọn câu trả lời nào, ta chỉ lấy thông tin các câu hỏi ở phần truy xuất này

                            Log.d("question"+i,g_summarizedPracticeQuestionList.get(i-1).getQuestion());
                            Log.d("optionA",g_summarizedPracticeQuestionList.get(i-1).getOption1());
                            Log.d("optionB",g_summarizedPracticeQuestionList.get(i-1).getOption2());
                            Log.d("optionC",g_summarizedPracticeQuestionList.get(i-1).getOption3());
                            Log.d("optionD",g_summarizedPracticeQuestionList.get(i-1).getOption4());
                            Log.d("answer",g_summarizedPracticeQuestionList.get(i-1).getCorrectAnswer().toString());
                            i++;
                        }
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm loadLessonContent để lấy danh sách các chế độ học trong Collection "VocabularyWords" của từng Document trong Collection "Lessons" (trừ Document "TOTAL_LESSONS")
    public static void loadLessonContent(final MyCompleteListener myCompleteListener){

        if (g_lessonContentList == null) {
            g_lessonContentList = new ArrayList<>();
        }
        g_lessonContentList.clear(); // Xóa dữ liệu cũ trong lessonModeList

        g_firestore.collection("Lessons").document(g_vocabularyLessonList.get(g_selectedLesson_index).getLessonID())
                .collection("VocabularyWords").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> lessonContentDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonContentDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonContentDocumentList = lessonContentDocumentMap.get("TOTAL_WORDS"); // Truy cập vào Document có id là "TOTAL_WORDS"
                        long wordNumber = Long.parseLong(lessonContentDocumentList.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_WORDS"
                        g_lessonContentNumber = (int) wordNumber;
                        Log.d("g_lessonContentNumber",g_lessonContentNumber+"");

                        for(int i=1;i<=g_lessonContentNumber;i++) {
                            String wordID = lessonContentDocumentList.getString("Word" + i + "_ID"); // Lấy giá trị của field "Word"+i+"_ID" trong Document "TOTAL_WORDS"
                            String wordSimplifiedContent = lessonContentDocumentList.getString("Word" + i + "_SimplifiedContent"); // Lấy giá trị của field "Word"+i+"_SimplifiedContent" trong Document "TOTAL_WORDS"
                            String wordContent = lessonContentDocumentList.getString("Word" + i + "_Content"); // Lấy giá trị của field "Word"+i+"_Content" trong Document "TOTAL_WORDS"
                            String pronunciation = lessonContentDocumentList.getString("Word" + i + "_Pronunciation"); // Lấy giá trị của field "Word"+i+"_Pronunciation" trong Document "TOTAL_WORDS"
                            String meaning = lessonContentDocumentList.getString("Word" + i + "_Meaning"); // Lấy giá trị của field "Word"+i+"_Meaning" trong Document "TOTAL_WORDS"

                            g_lessonContentList.add(new AdminVocabularyWords(wordID, wordContent, wordSimplifiedContent, pronunciation, meaning));

                        }

                        myCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///////////////// Các hàm cho CRUD

    //////////////////// QUẢN LÝ BÀI HỌC VỀ TỪ VỰNG
    ///// Hàm addVocabularyLesson để thêm bài học từ vựng vào Collection "Lessons"
    public static void addVocabularyLesson(String lessonTitle, String lessonName, String lessonImage, MyCompleteListener myCompleteListener){
        Map<String, Object> lessonData = new ArrayMap<>(); // Tạo một Map để lưu trữ dữ liệu bài học

        // Lấy số của bài học từ vựng (giả sử Bài 12 -> ["Bài","12"])
        String[] getLessonNumber = lessonTitle.trim().split(" ");
        Integer lessonNumber = Integer.parseInt(getLessonNumber[1]);

        lessonData.put("Lesson"+lessonNumber+"_ID","VocabularyLesson_"+lessonNumber);
        lessonData.put("Lesson"+lessonNumber+"_Title",lessonTitle);
        lessonData.put("Lesson"+lessonNumber+"_Name",lessonName);
        lessonData.put("Lesson"+lessonNumber+"_Image",lessonImage);

        DocumentReference lessonDoc = g_firestore.collection("Lessons")
                .document("VocabularyLesson_"+lessonNumber);

        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch
        batch.set(lessonDoc, lessonData); // Thêm một thao tác cập nhật vào batch

        DocumentReference countDoc = g_firestore.collection("Lessons")
                .document("TOTAL_LESSONS"); // Truy cập vào tài liệu "Count" trong Firestore
        Map<String, Object> countData = new ArrayMap<>(); // Tạo một Map để lưu trữ tên bài học

        countData.put("Lesson"+lessonNumber+"_ID","VocabularyLesson_"+lessonNumber);

        batch.set(countDoc,countData, SetOptions.merge()); // Ghép thêm dữ liệu vào document "TOTAL_LESSONS" chứ không override
        batch.update(countDoc, "COUNT", FieldValue.increment(1)); // Tăng số lượng bài học

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm deleteVocabularyLesson để xóa bài học từ vựng khỏi Collection "Lessons"
    public static void deleteVocabularyLesson(String lessonTitle, String lessonID, MyCompleteListener myCompleteListener) {
        // Lấy số bài học từ tiêu đề (Ví dụ: "Bài 12" -> lấy "12")
        String[] getLessonNumber = lessonTitle.split(" ");
        Integer lessonNumber = Integer.parseInt(getLessonNumber[1]);

        // Tham chiếu đến document của bài học
        DocumentReference lessonDoc = g_firestore.collection("Lessons").document(lessonID);

        // Tham chiếu đến document TOTAL_LESSONS
        DocumentReference countDoc = g_firestore.collection("Lessons").document("TOTAL_LESSONS");

        WriteBatch batch = g_firestore.batch();

        // Xóa document của bài học
        batch.delete(lessonDoc);

        // Xóa field tương ứng trong TOTAL_LESSONS
        Map<String, Object> countData = new ArrayMap<>();
        countData.put("Lesson"+lessonNumber+"_ID", FieldValue.delete());
        batch.set(countDoc, countData, SetOptions.merge());

        // Giảm COUNT nếu lớn hơn 0
        batch.update(countDoc, "COUNT", FieldValue.increment(-1));

        // Thực hiện batch commit
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    ///// Hàm updateVocabularyLesson để cập nhật bài học từ vựng vào Collection "Lessons"
    public static void updateVocabularyLesson(String lessonTitle, String lessonID, String newLessonName, String newLessonImage, MyCompleteListener myCompleteListener){
        // Lấy số bài học từ tiêu đề mới (Ví dụ: "Bài 12" -> lấy "12")
        String[] getLessonNumber = lessonTitle.split(" ");
        Integer lessonNumber = Integer.parseInt(getLessonNumber[1]);

        // Tham chiếu đến document của bài học
        DocumentReference lessonDoc = g_firestore.collection("Lessons").document(lessonID);

        WriteBatch batch = g_firestore.batch();

        // Cập nhật dữ liệu bài học
        Map<String, Object> lessonData = new ArrayMap<>();
        lessonData.put("Lesson" + lessonNumber + "_Name", newLessonName);
        lessonData.put("Lesson" + lessonNumber + "_Image", newLessonImage);

        batch.update(lessonDoc, lessonData);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }


    //////////////////// QUẢN LÝ BÀI HỌC VỀ TỪ VỰNG
    ///// Hàm addVocabularyLesson để thêm bài học từ vựng vào Collection "Lessons"
    public static void addLessonContent(String lessonTitle, Integer lessonContentCount, String content, String simplifiedContent, String pronunciation, String meaning, MyCompleteListener myCompleteListener){
        Map<String, Object> wordData = new ArrayMap<>(); // Tạo một Map để lưu trữ dữ liệu bài học

        // Lấy số của bài học từ vựng (giả sử Bài 12 -> ["Bài","12"])
        String[] getLessonNumber = lessonTitle.split(" ");
        Integer lessonNumber = Integer.parseInt(getLessonNumber[1]);
        Log.d("lessonNumber",lessonNumber+"");

        Integer currentCount = lessonContentCount + 1; // Số thứ tự của từ vựng đang cần thêm
        Log.d("currentCount",currentCount+"");

        wordData.put("Word"+currentCount+"_ID","Word_"+(lessonContentCount+1));
        wordData.put("Word"+currentCount+"_Content",content);
        wordData.put("Word"+currentCount+"_SimplifiedContent",simplifiedContent);
        wordData.put("Word"+currentCount+"_Pronunciation",pronunciation);
        wordData.put("Word"+currentCount+"_Meaning",meaning);

        DocumentReference wordDoc = g_firestore.collection("Lessons")
                .document("VocabularyLesson_"+lessonNumber).collection("VocabularyWords")
                .document("Word_"+currentCount);

        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch
        batch.set(wordDoc, wordData); // Thêm một thao tác cập nhật vào batch

        DocumentReference countDoc = g_firestore.collection("Lessons")
                .document("VocabularyLesson_"+lessonNumber).collection("VocabularyWords")
                .document("TOTAL_WORDS"); // Truy cập vào tài liệu "Count" trong Firestore

        batch.update(countDoc, "COUNT", FieldValue.increment(1)); // Tăng số lượng từ vựng

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm deleteVocabularyLesson để xóa bài học từ vựng khỏi Collection "Lessons"
    public static void deleteLessonContent(String lessonTitle, String wordID, MyCompleteListener myCompleteListener) {
        // Lấy số bài học từ tiêu đề (Ví dụ: "Bài 12" -> lấy "12")
        String[] getLessonNumber = lessonTitle.split(" ");
        Integer lessonNumber = Integer.parseInt(getLessonNumber[1]);
        Log.d("lessonNumber",lessonNumber+"");

        // Tham chiếu đến document của từ vựng
        DocumentReference wordDoc = g_firestore.collection("Lessons")
                .document("VocabularyLesson_"+lessonNumber).collection("VocabularyWords")
                .document(wordID);

        // Tham chiếu đến document TOTAL_WORDS
        DocumentReference countDoc = g_firestore.collection("Lessons")
                .document("VocabularyLesson_"+lessonNumber).collection("VocabularyWords")
                .document("TOTAL_WORDS"); // Truy cập vào tài liệu "Count" trong Firestore;

        WriteBatch batch = g_firestore.batch();

        // Xóa document của bài học
        batch.delete(wordDoc);

        // Giảm COUNT nếu lớn hơn 0
        batch.update(countDoc, "COUNT", FieldValue.increment(-1));

        // Thực hiện batch commit
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    ///// Hàm updateVocabularyLesson để cập nhật bài học từ vựng vào Collection "Lessons"
    public static void updateLessonContent(String lessonTitle, String wordID, String newWordContent, String newSimplifiedContent, String newPronunciation, String newMeaning , MyCompleteListener myCompleteListener){
        // Lấy số bài học từ tiêu đề mới (Ví dụ: "Bài 12" -> lấy "12")
        String[] getLessonNumber = lessonTitle.split(" ");
        String[] getWordNumber = wordID.split("_");
        Integer lessonNumber = Integer.parseInt(getLessonNumber[1]);
        Integer wordNumber = Integer.parseInt(getWordNumber[1]);

        // Tham chiếu đến document của từ vựng
        DocumentReference wordDoc = g_firestore.collection("Lessons")
                .document("VocabularyLesson_"+lessonNumber).collection("VocabularyWords")
                .document(wordID);

        WriteBatch batch = g_firestore.batch();

        // Cập nhật dữ liệu bài học
        Map<String, Object> wordData = new ArrayMap<>();

        wordData.put("Word"+wordNumber+"_Content",newWordContent);
        wordData.put("Word"+wordNumber+"_SimplifiedContent",newSimplifiedContent);
        wordData.put("Word"+wordNumber+"_Pronunciation",newPronunciation);
        wordData.put("Word"+wordNumber+"_Meaning",newMeaning);

        batch.update(wordDoc, wordData);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm searchVocabularyWords để trả về danh sách các từ vựng có trong bài học tương ứng
    public static void searchVocabularyWords(String search, MyCompleteListenerWithData2 myCompleteListenerWithData2){
        Boolean isFound = false;
        ArrayList<AdminVocabularyWords> vocabularyWordsList = new ArrayList<>();

        search = search.trim(); // Xóa khoảng trắng ở đầu và cuối chuỗi
        search = search.toLowerCase(); // Chuyển chuỗi thành chữ thường

        // Tìm bài học tương ứng với tiêu đề
        Integer position= 0;
        for (AdminLessonChoices choice: g_vocabularyLessonList) {
            if(choice.getLessonTitle().toLowerCase().contains(search)){
                isFound = true;
                break;
            }
            ++position;
        }

        if(isFound){
            g_selectedLesson_index = position;

            if (g_lessonContentList == null) {
                g_lessonContentList = new ArrayList<>();
            }
            g_lessonContentList.clear(); // Xóa dữ liệu cũ trong lessonModeList

            // Tìm từ vựng trong bài học
            g_firestore.collection("Lessons").document(Admin_DBQuery.g_vocabularyLessonList.get(position).getLessonID())
                    .collection("VocabularyWords").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            try{
                                Map<String, QueryDocumentSnapshot> lessonContentDocumentMap = new ArrayMap<>();

                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    lessonContentDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                                }

                                QueryDocumentSnapshot lessonContentCountDocument = lessonContentDocumentMap.get("TOTAL_WORDS"); // Truy cập vào Document có id là "TOTAL_WORDS"
                                long wordNumber = Long.parseLong(lessonContentCountDocument.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_WORDS"
                                g_lessonContentNumber = (int) wordNumber;

                                for(int i=1;i<=g_lessonContentNumber;i++) {
                                    QueryDocumentSnapshot lessonContentDocument = lessonContentDocumentMap.get("Word_" + i); // Truy cập vào Document có ID là "Word"+i

                                    String wordID = lessonContentDocument.getString("Word" + i + "_ID"); // Lấy giá trị của field "Word"+i+"_ID" trong Document "TOTAL_WORDS"
                                    String wordSimplifiedContent = lessonContentDocument.getString("Word" + i + "_SimplifiedContent"); // Lấy giá trị của field "Word"+i+"_SimplifiedContent" trong Document "TOTAL_WORDS"
                                    String wordContent = lessonContentDocument.getString("Word" + i + "_Content"); // Lấy giá trị của field "Word"+i+"_Content" trong Document "TOTAL_WORDS"
                                    String pronunciation = lessonContentDocument.getString("Word" + i + "_Pronunciation"); // Lấy giá trị của field "Word"+i+"_Pronunciation" trong Document "TOTAL_WORDS"
                                    String meaning = lessonContentDocument.getString("Word" + i + "_Meaning"); // Lấy giá trị của field "Word"+i+"_Meaning" trong Document "TOTAL_WORDS"

                                    vocabularyWordsList.add(new AdminVocabularyWords(wordID, wordContent, wordSimplifiedContent, pronunciation, meaning));

                                    myCompleteListenerWithData2.onSuccess(vocabularyWordsList);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                myCompleteListenerWithData2.onFailure(e);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            myCompleteListenerWithData2.onFailure(e);
                        }
                    });
        }

    }

    ///// Hàm addGrammarLesson để thêm bài học ngữ pháp mới
    public static void addGrammarLesson(String lessonTitle, String lessonName, String lessonImage, MyCompleteListener myCompleteListener){
        g_firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = g_firestore.batch();

        // Tìm vị trí bài học trong danh sách
        Integer position = 0;
        for (GrammarLessonChoices choice: g_grammarLessonList) {
            if(choice.getLessonTitle().toLowerCase().contains(lessonTitle.toLowerCase())){
                break;
            }
            ++position;
        }

        // Tạo document mới cho bài học
        DocumentReference lessonDoc = g_firestore.collection("Grammars").document(lessonTitle);
        Map<String, Object> lessonData = new ArrayMap<>();
        lessonData.put("Lesson" + (g_grammarLessonNumber + 1) + "_ID", lessonTitle);
        lessonData.put("Lesson" + (g_grammarLessonNumber + 1) + "_Name", lessonName);
        lessonData.put("Lesson" + (g_grammarLessonNumber + 1) + "_Title", lessonTitle);
        lessonData.put("Lesson" + (g_grammarLessonNumber + 1) + "_Image", lessonImage);

        batch.set(lessonDoc, lessonData);

        // Cập nhật số lượng bài học
        DocumentReference totalDoc = g_firestore.collection("Grammars").document("TOTAL_GRAMMARS");
        Map<String, Object> totalData = new ArrayMap<>();
        totalData.put("COUNT", g_grammarLessonNumber + 1);
        totalData.put("Lesson" + (g_grammarLessonNumber + 1) + "_ID", lessonTitle);

        batch.set(totalDoc, totalData, SetOptions.merge());

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        g_grammarLessonNumber++;
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm deleteGrammarLesson để xóa bài học ngữ pháp
    public static void deleteGrammarLesson(String lessonTitle, String lessonID, MyCompleteListener myCompleteListener) {
        g_firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = g_firestore.batch();

        // Xóa document bài học
        DocumentReference lessonDoc = g_firestore.collection("Grammars").document(lessonTitle);
        batch.delete(lessonDoc);

        // Cập nhật số lượng bài học
        DocumentReference totalDoc = g_firestore.collection("Grammars").document("TOTAL_GRAMMARS");
        Map<String, Object> totalData = new ArrayMap<>();
        totalData.put("COUNT", g_grammarLessonNumber - 1);

        batch.set(totalDoc, totalData, SetOptions.merge());

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        g_grammarLessonNumber--;
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm updateGrammarLesson để cập nhật bài học ngữ pháp
    public static void updateGrammarLesson(String lessonTitle, String lessonID, String newLessonName, String newLessonImage, MyCompleteListener myCompleteListener){
        g_firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = g_firestore.batch();

        DocumentReference lessonDoc = g_firestore.collection("Grammars").document(lessonTitle);
        Map<String, Object> lessonData = new ArrayMap<>();
        lessonData.put("Lesson" + lessonID + "_Name", newLessonName);
        lessonData.put("Lesson" + lessonID + "_Image", newLessonImage);

        batch.update(lessonDoc, lessonData);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm addGrammarPoint để thêm điểm ngữ pháp mới
    public static void addGrammarPoint(String lessonTitle, Integer grammarPointCount, String grammarPoint, String explanation, String example, String translation, MyCompleteListener myCompleteListener){
        g_firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = g_firestore.batch();

        // Tìm bài học tương ứng
        Integer position = 0;
        for (GrammarLessonChoices choice: g_grammarLessonList) {
            if(choice.getLessonTitle().toLowerCase().contains(lessonTitle.toLowerCase())){
                break;
            }
            ++position;
        }

        // Thêm điểm ngữ pháp vào subcollection
        DocumentReference grammarPointDoc = g_firestore.collection("Grammars").document(lessonTitle)
                .collection("GrammarPoints").document("GrammarPoint_" + (grammarPointCount + 1));
        Map<String, Object> grammarPointData = new ArrayMap<>();
        grammarPointData.put("GrammarPoint" + (grammarPointCount + 1) + "_ID", "GrammarPoint_" + (grammarPointCount + 1));
        grammarPointData.put("GrammarPoint" + (grammarPointCount + 1) + "_Point", grammarPoint);
        grammarPointData.put("GrammarPoint" + (grammarPointCount + 1) + "_Explanation", explanation);
        grammarPointData.put("GrammarPoint" + (grammarPointCount + 1) + "_Example", example);
        grammarPointData.put("GrammarPoint" + (grammarPointCount + 1) + "_Translation", translation);

        batch.set(grammarPointDoc, grammarPointData);

        // Cập nhật số lượng điểm ngữ pháp
        DocumentReference totalDoc = g_firestore.collection("Grammars").document(lessonTitle)
                .collection("GrammarPoints").document("TOTAL_GRAMMAR_POINTS");
        Map<String, Object> totalData = new ArrayMap<>();
        totalData.put("COUNT", grammarPointCount + 1);

        batch.set(totalDoc, totalData, SetOptions.merge());

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm searchGrammarPoints để tìm kiếm điểm ngữ pháp
    public static void searchGrammarPoints(String search, MyCompleteListenerWithData2 myCompleteListenerWithData2){
        Boolean isFound = false;
        ArrayList<AdminGrammarPoint> grammarPointsList = new ArrayList<>();

        search = search.trim();
        search = search.toLowerCase();

        // Tìm bài học tương ứng với tiêu đề
        Integer position = 0;
        for (GrammarLessonChoices choice: g_grammarLessonList) {
            if(choice.getLessonTitle().toLowerCase().contains(search)){
                isFound = true;
                break;
            }
            ++position;
        }

        if(isFound){
            g_selectedLesson_index = position;

            // Tìm điểm ngữ pháp trong bài học
            g_firestore.collection("Grammars").document(g_grammarLessonList.get(position).getLessonID())
                    .collection("GrammarPoints").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            try{
                                Map<String, QueryDocumentSnapshot> grammarPointsDocumentMap = new ArrayMap<>();

                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    grammarPointsDocumentMap.put(doc.getId(), doc);
                                }

                                QueryDocumentSnapshot grammarPointsCountDocument = grammarPointsDocumentMap.get("TOTAL_GRAMMAR_POINTS");
                                if (grammarPointsCountDocument != null) {
                                    long grammarPointNumber = Long.parseLong(grammarPointsCountDocument.get("COUNT").toString());

                                    for(int i=1; i<=grammarPointNumber; i++) {
                                        QueryDocumentSnapshot grammarPointDocument = grammarPointsDocumentMap.get("GrammarPoint_" + i);

                                        if (grammarPointDocument != null) {
                                            String grammarPointID = grammarPointDocument.getString("GrammarPoint" + i + "_ID");
                                            String grammarPoint = grammarPointDocument.getString("GrammarPoint" + i + "_Point");
                                            String explanation = grammarPointDocument.getString("GrammarPoint" + i + "_Explanation");
                                            String example = grammarPointDocument.getString("GrammarPoint" + i + "_Example");
                                            String translation = grammarPointDocument.getString("GrammarPoint" + i + "_Translation");

                                            grammarPointsList.add(new AdminGrammarPoint(grammarPointID, grammarPoint, explanation, example, translation, search));
                                        }
                                    }
                                }
                                myCompleteListenerWithData2.onSuccess(grammarPointsList);
                            }catch (Exception e){
                                e.printStackTrace();
                                myCompleteListenerWithData2.onFailure(e);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            myCompleteListenerWithData2.onFailure(e);
                        }
                    });
        } else {
            myCompleteListenerWithData2.onFailure(new Exception("Lesson not found"));
        }
    }

    ///// Hàm deleteGrammarPoint để xóa điểm ngữ pháp
    public static void deleteGrammarPoint(String lessonTitle, String grammarPointID, MyCompleteListener myCompleteListener) {
        g_firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = g_firestore.batch();

        DocumentReference grammarPointDoc = g_firestore.collection("Grammars").document(lessonTitle)
                .collection("GrammarPoints").document(grammarPointID);
        batch.delete(grammarPointDoc);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm updateGrammarPoint để cập nhật điểm ngữ pháp
    public static void updateGrammarPoint(String lessonTitle, String grammarPointID, String newGrammarPoint, String newExplanation, String newExample, String newTranslation, MyCompleteListener myCompleteListener){
        g_firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = g_firestore.batch();

        DocumentReference grammarPointDoc = g_firestore.collection("Grammars").document(lessonTitle)
                .collection("GrammarPoints").document(grammarPointID);
        Map<String, Object> grammarPointData = new ArrayMap<>();
        grammarPointData.put("GrammarPoint_Point", newGrammarPoint);
        grammarPointData.put("GrammarPoint_Explanation", newExplanation);
        grammarPointData.put("GrammarPoint_Example", newExample);
        grammarPointData.put("GrammarPoint_Translation", newTranslation);

        batch.update(grammarPointDoc, grammarPointData);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListener.onFailure();
                    }
                });
    }

}
