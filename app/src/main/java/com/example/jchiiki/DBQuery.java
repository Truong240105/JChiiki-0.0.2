package com.example.jchiiki;

import android.util.ArrayMap;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.jchiiki.callback.MyCompleteListener;
import com.example.jchiiki.callback.MyCompleteListenerWithData;
import com.example.jchiiki.callback.MyCompleteListenerWithData_2;
import com.example.jchiiki.items.GrammarLessonChoices;
import com.example.jchiiki.items.LeaderBoardItems;
import com.example.jchiiki.items.LessonChoices;
import com.example.jchiiki.items.Profile;
import com.example.jchiiki.items.QuestionItems;
import com.example.jchiiki.items.SummarizedPracticeChoices;
import com.example.jchiiki.items.VocabularyWords;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.checkerframework.checker.regex.qual.PartialRegex;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DBQuery {
    // Access a Cloud Firestore instance from your Activity
    public static String g_practiceOptionChoice = ""; // Khai báo biến static để lưu trữ lựa chọn chế độ học (Từ vựng, Ngữ pháp, Nghe hiểu, ...) trong toàn bộ ứng dụng
    public static FirebaseFirestore g_firestore; // Khai báo biến static để lưu trữ instance của FirebaseFirestore trong toàn bộ ứng dụng
    public static Integer g_vocabularyLessonNumber, g_grammarLessonNumber, g_lessonContentNumber; // Khai báo biến static để lưu trữ số lượng bài học và chế độ học (HomeFragment LessonsFragment; LessonsModes) trong toàn bộ ứng dụng
    public static Integer g_summarizedPracticeNumber_n5, g_summarizedPracticeNumber_n4, g_summarizedPracticeNumber_n3, g_summarizedPracticeNumber_n2, g_summarizedPracticeNumber_n1; // Khai báo biến static để lưu trữ số lượng bài luyện tập tổng hợp
    public static Integer g_selectedLesson_index = 0, g_selectedTest_index = 0; // Khai báo biến static để lưu trữ bài học, bài test được chọn trong toàn bộ ứng dụng
    public static Integer g_userCount = 0; // Khai báo biến static để lưu trữ số lượng người dùng trong toàn bộ ứng dụng
    public static ArrayList<LessonChoices> g_vocabularyLessonList; // Khai báo biến static để lưu trữ danh sách các bài học từ vựng (HomeFragment) trong toàn bộ ứng dụng
    public static ArrayList<GrammarLessonChoices> g_grammarLessonList; // Khai báo biến static để lưu trữ danh sách các bài học ngữ pháp (HomeFragment) trong toàn bộ ứng dụng
    public static ArrayList<QuestionItems> g_vocabularyQuestionList; // Khai báo biến static để lưu trữ danh sách các câu hỏi trong bài học từ vựng (VocabularyLessonsFragment) trong toàn bộ ứng dụng
    public static ArrayList<QuestionItems> g_listeningQuestionList; // Khai báo biến static để lưu trữ danh sách các câu hỏi trong bài học nghe (ListeningLessonsFragment) trong toàn bộ ứng dụng
    public static ArrayList<QuestionItems> g_speakingQuestionList; // Khai báo biến static để lưu trữ danh sách các câu hỏi trong bài học nói (SpeakingLessonsFragment) trong toàn bộ ứng dụng
    public static ArrayList<SummarizedPracticeChoices> g_summarizedPracticeList; // Khai báo biến static để lưu trữ danh sách các bài luyện tập tổng hợp (EveryDayFragment) trong toàn bộ ứng dụng
    public static ArrayList<QuestionItems> g_summarizedPracticeQuestionList; // Khai báo biến static để lưu trữ danh sách các câu hỏi trong bài luyện tập tổng hợp (EveryDayFragment) trong toàn bộ ứng dụng
    public static ArrayList<LeaderBoardItems> g_rankingList; // Khai báo biến static để lưu trữ danh sách người dùng trong bảng xếp hạng trong toàn bộ ứng dụng (LeaderBoard) trong toàn bộ ứng dụng
    public static ArrayList<VocabularyWords> g_lessonContentList; // Khai báo biến static để lưu trữ danh sách các chế độ học trong toàn bộ ứng dụng



    ///// Hàm createUserData cho đăng ký tài khoản người dùng bằng Google
    public static void createUserData(String name, String email, MyCompleteListener myCompleteListener){
        Map<String, Object> userData = new ArrayMap<>(); // Tạo một Map để lưu trữ dữ liệu người dùng

        // Thêm dữ liệu vào userData
        userData.put("NAME",name);
        userData.put("EMAIL",email);
        userData.put("ROLE","Customer");

        // Tạo một tài liệu mới trong Firestore với dữ liệu người dùng
        DocumentReference userDoc = g_firestore.collection("Users") // Truy cập vào collection "Users" trong Firestore
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()); // Lấy ID của người dùng hiện tại (ID được tạo ngẫu nhiên khi tạo tài khoản)


        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch
        batch.set(userDoc, userData); // Thêm một thao tác cập nhật vào batch)

        DocumentReference countDoc = g_firestore.collection("Users")
                .document("TOTAL_USERS"); // Truy cập vào tài liệu "Count" trong Firestore

        batch.update(countDoc, "COUNT", FieldValue.increment(1)); // Thêm một thao tác cập nhật vào batch khi tạo tài khoản sẽ tăng số lượng người dùng (COUNT) lên 1 trong collection TOTAL_USERS

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
                        myCompleteListener.onFailure();
                    }
                });

    }

    ///// Hàm createUserData cho đăng ký tài khoản người dùng bằng activity SignUp
    public static void createUserData(String name, String email, String password, String birthday ,String phoneNumber, MyCompleteListener myCompleteListener){
        Map<String, Object> userData = new ArrayMap<>(); // Tạo một Map để lưu trữ dữ liệu người dùng

        // Thêm dữ liệu vào userData
        userData.put("NAME",name);
        userData.put("EMAIL",email);
        userData.put("PASSWORD",password);
        userData.put("BTH_DATE", birthday);
        userData.put("PHONE_NO", phoneNumber);
        userData.put("TOTAL_SCORE", 0);
        userData.put("ROLE","Customer");

        // Tạo một tài liệu mới trong Firestore với dữ liệu người dùng
        DocumentReference userDoc = g_firestore.collection("Users") // Truy cập vào collection "Users" trong Firestore
                                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()); // Lấy ID của người dùng hiện tại (ID được tạo ngẫu nhiên khi tạo tài khoản)


        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch
        batch.set(userDoc, userData); // Thêm một thao tác cập nhật vào batch

        DocumentReference countDoc = g_firestore.collection("Users")
                                        .document("TOTAL_USERS"); // Truy cập vào tài liệu "Count" trong Firestore

        batch.update(countDoc, "COUNT", FieldValue.increment(1)); // Thêm một thao tác cập nhật vào batch khi tạo tài khoản sẽ tăng số lượng người dùng (COUNT) lên 1 trong collection TOTAL_USERS

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
                        myCompleteListener.onFailure();
                    }
                });

    }

    // Hàm checkUserExistence để kiểm tra xem người dùng có tồn tại trong Firestore chưa
    public static void checkUserExistence(String uid, MyCompleteListenerWithData myCompleteListener) {
        g_firestore.collection("Users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            myCompleteListener.onSuccess(1);
                        } else {
                            myCompleteListener.onSuccess(0);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure(e);
                    }
                });
    }

    //////// ========= ACHIEVEMENT FRAGMENT =========
    ///// Hàm getTopUsers để lấy danh sách người dùng có điểm cao nhất trong bảng xếp hạng
    public static void getTopUsers(MyCompleteListener myCompleteListener){
        if (g_rankingList == null) {
            g_rankingList = new ArrayList<>();
        }
        g_rankingList.clear();

        g_firestore.collection("Users")
                .whereEqualTo("ROLE","Customer")
                .whereGreaterThan("TOTAL_SCORE",0) // Chỉ lấy các tài liệu có TOTAL_SCORE > 0
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING) // Xếp hạng theo điểm giảm dần
                .limit(20) // Giới hạn số lượng Document trả về
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int rank = 1;

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            if(doc.getString("ROLE").equals("Customer")){ // Chỉ lấy các tài liệu có ROLE là "Customer" nếu không sẽ xung đột với admin
                                g_rankingList.add(new LeaderBoardItems(
                                        doc.getString("NAME"),
                                        doc.getLong("TOTAL_SCORE").intValue(),
                                        rank));

                                ++rank; // Tăng rank lên 1
                            }
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

    //////// ========= EDIT PROFILE ACTIVITY =========
    ///// Hàm editProfile để cập nhật thông tin người dùng sử dụng tài khoản của app
    public static void editProfile(String name, String password, String birthday ,String phoneNumber, MyCompleteListener myCompleteListener){
        Map<String, Object> userData = new ArrayMap<>();
        userData.put("NAME",name);
        userData.put("PASSWORD",password);
        userData.put("BTH_DATE", birthday);
        userData.put("PHONE_NO", phoneNumber);

        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.updatePassword(password);

        DocumentReference userDoc = g_firestore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();
        batch.update(userDoc, userData);

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
                        myCompleteListener.onFailure();
                    }
                });
    }

    ///// Hàm editProfile để cập nhật thông tin người dùng sử dụng tài khoản Google
    public static void editProfile(String name, MyCompleteListener myCompleteListener){
        Map<String, Object> userData = new ArrayMap<>();
        userData.put("NAME",name);

        DocumentReference userDoc = g_firestore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();
        batch.update(userDoc, userData);

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
                        myCompleteListener.onFailure();
                    }
                });

    }


    //////// ========= SCORE ACTIVITY =========

    ///// Lưu kết quả BÀI HỌC TỪ VỰNG vào tài khoản người dùng
    public static void saveVocabularyLessonResult(int score, MyCompleteListener myCompleteListener){
        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch

        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("UserData").document("TestScores").collection("VocabularyLessons")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> lessonInfoDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonInfoDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        int lessonNumber = lessonInfoDocumentMap.size(); // Lấy số lượng bài học đã học để tính điểm trung bình

                        QueryDocumentSnapshot queryDocumentSnapshot = lessonInfoDocumentMap.get("VocabularyLesson_" + (g_selectedLesson_index + 1));
                        if(queryDocumentSnapshot != null){ // Kiểm tra xem có tồn tại document INFO không
                            int highScore = (queryDocumentSnapshot.getLong("HighScore") != null) ? queryDocumentSnapshot.getLong("HighScore").intValue() : -1; // Lấy điểm cao nhất

                            // Nếu điểm cao hơn điểm hiện tại thì cập nhật điểm cao nhất
                            if(score > highScore && highScore != -1){
                                Map<String, Object> lessonScoreData = new ArrayMap<>();
                                lessonScoreData.put("HighScore", score);

                                DocumentReference totalScoreDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                DocumentReference userDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("UserData").document("TestScores")
                                        .collection("VocabularyLessons").document("VocabularyLesson_" + (g_selectedLesson_index + 1));

                                // Tính điểm trung bình
                                double averageScore = (double) score / lessonNumber;

                                // Cập nhật điểm trung bình
                                batch.update(totalScoreDoc, "AVERAGE_VOCABULARY_SCORE", averageScore);

                                // Cập nhật điểm cao nhất
                                batch.update(userDoc, "HighScore", score);
                            }
                        }
                        else{ // Nếu không tồn tại document INFO thì tạo mới document INFO

                            DocumentReference userDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("UserData").document("TestScores")
                                    .collection("VocabularyLessons").document("VocabularyLesson_" + (g_selectedLesson_index + 1));

                            DocumentReference totalScoreDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            Map<String, Object> totalScoreData = new ArrayMap<>();
                            Map<String, Object> testScoreData = new ArrayMap<>();

                            // Lưu tổng điểm
                            totalScoreData.put("AVERAGE_VOCABULARY_SCORE", FieldValue.increment(score));
                            batch.update(totalScoreDoc, totalScoreData);

                            // Lưu điểm cao nhất
                            testScoreData.put("HighScore", score);
                            batch.set(userDoc, testScoreData);
                        }

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
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    ///// Lưu kết quả BÀI HỌC NGỮ PHÁP vào tài khoản người dùng

    ///// Lưu kết quả BÀI HỌC NGHE vào tài khoản người dùng
    public static void saveListeningLessonResult(int score, MyCompleteListener myCompleteListener){
        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch

        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("UserData").document("TestScores").collection("ListeningLessons")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> lessonInfoDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonInfoDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        int lessonNumber = lessonInfoDocumentMap.size(); // Lấy số lượng bài học đã học để tính điểm trung bình

                        QueryDocumentSnapshot queryDocumentSnapshot = lessonInfoDocumentMap.get("ListeningLesson_" + (g_selectedLesson_index + 1));
                        if(queryDocumentSnapshot != null){ // Kiểm tra xem có tồn tại document INFO không
                            int highScore = (queryDocumentSnapshot.getLong("HighScore") != null) ? queryDocumentSnapshot.getLong("HighScore").intValue() : -1; // Lấy điểm cao nhất

                            // Nếu điểm cao hơn điểm hiện tại thì cập nhật điểm cao nhất
                            if(score > highScore && highScore != -1){
                                Map<String, Object> lessonScoreData = new ArrayMap<>();
                                lessonScoreData.put("HighScore", score);

                                DocumentReference totalScoreDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                DocumentReference userDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("UserData").document("TestScores")
                                        .collection("ListeningLessons").document("ListeningLesson_" + (g_selectedLesson_index + 1));

                                // Tính điểm trung bình
                                double averageScore = (double) score / lessonNumber;

                                // Cập nhật điểm trung bình
                                batch.update(totalScoreDoc, "AVERAGE_LISTENING_SCORE", averageScore);

                                // Cập nhật điểm cao nhất
                                batch.update(userDoc, "HighScore", score);
                            }
                        }
                        else{ // Nếu không tồn tại document INFO thì tạo mới document INFO

                            DocumentReference userDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("UserData").document("TestScores")
                                    .collection("ListeningLessons").document("ListeningLesson_" + (g_selectedLesson_index + 1));

                            DocumentReference totalScoreDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            Map<String, Object> totalScoreData = new ArrayMap<>();
                            Map<String, Object> testScoreData = new ArrayMap<>();

                            // Lưu tổng điểm
                            totalScoreData.put("AVERAGE_LISTENING_SCORE", FieldValue.increment(score));
                            batch.update(totalScoreDoc, totalScoreData);

                            // Lưu điểm cao nhất
                            testScoreData.put("HighScore", score);
                            batch.set(userDoc, testScoreData);
                        }

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
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    ///// Lưu kết quả BÀI HỌC NÓI vào tài khoản người dùng

    ///// Lưu kết quả BÀI HỌC ĐỌC vào tài khoản người dùng

    ///// Lưu kết quả BÀI HỌC VIẾT vào tài khoản người dùng

    ///// Lưu kết quả BÀI KIỂM TRA TỔNG HỢP vào tài khoản người dùng
    public static void saveSummarizedPracticeResult(int score, int languageLevel, MyCompleteListener myCompleteListener){
        WriteBatch batch = g_firestore.batch(); // Tạo một batch để thực hiện nhiều thao tác cập nhật trong một giao dịch
        AtomicInteger practiceMaxNumber = new AtomicInteger();

        // Xét điều kiện để lấy số lượng bài luyện tập theo trình độ trong Collection "Summarized_Practices"
        g_firestore.collection("Summarized_Practices")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // queryDocumentSnapshots nắm tất cả các Documents trong Collection "Summarized_Practices"
                        //Chúng ta đã có biến queryDocumentSnapshots, việc cần làm là làm sao để lấy được toàn bộ các Documents trong đó. Để làm được,
                        //ta cần giải nén queryDocumentSnapshots bằng việc dùng 1 Map
                        Map<String, QueryDocumentSnapshot> practiceTestNumberDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            practiceTestNumberDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot queryDocumentSnapshot = practiceTestNumberDocumentMap.get("LEVEL_N"+languageLevel);

                        // Giả sử ta khai báo practiceTestNumberN5 dạng final int thì dù nó được chấp nhận nhưng ta không gán giá trị cho biến được,
                        // vì thế ta dùng kiểu AtomicInteger
                        practiceMaxNumber.set(queryDocumentSnapshot.getLong("COUNT").intValue());
                        Log.d("practiceTestNumber",practiceMaxNumber+"");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("UserData").document("TestScores").collection("SummarizedPractices").document("LEVEL_N" + languageLevel)
                .collection("Practice" + (g_selectedTest_index+1))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> practiceTestInfoDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            practiceTestInfoDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot queryDocumentSnapshot = practiceTestInfoDocumentMap.get("INFO");
                        if(queryDocumentSnapshot != null){ // Kiểm tra xem có tồn tại document INFO không
                            int highScore = (queryDocumentSnapshot.getLong("HighScore") != null) ? queryDocumentSnapshot.getLong("HighScore").intValue() : -1; // Lấy điểm cao nhất

                            // Nếu điểm cao hơn điểm hiện tại thì cập nhật điểm cao nhất
                            if(score > highScore && highScore != -1){
                                Map<String, Object> testScoreData = new ArrayMap<>();
                                testScoreData.put("HighScore", score);

                                DocumentReference totalScoreDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                DocumentReference userDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("UserData").document("TestScores")
                                        .collection("SummarizedPractices").document("LEVEL_N" + languageLevel)
                                        .collection("Practice" + (g_selectedTest_index+1)).document("INFO");

                                // Cập nhật tổng điểm
                                int plusScore = score - highScore;
                                batch.update(totalScoreDoc, "TOTAL_SCORE", FieldValue.increment(plusScore));

                                // Cập nhật điểm cao nhất
                                batch.update(userDoc, "HighScore", score);
                            }
                        }
                        else{ // Nếu không tồn tại document INFO thì tạo mới document INFO
                            DocumentReference userDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("UserData").document("TestScores")
                                    .collection("SummarizedPractices").document("LEVEL_N" + languageLevel)
                                    .collection("Practice" + (g_selectedTest_index+1)).document("INFO");

                            DocumentReference totalScoreDoc = g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            Map<String, Object> totalScoreData = new ArrayMap<>();
                            Map<String, Object> testScoreData = new ArrayMap<>();

                            // Lưu tổng điểm
                            totalScoreData.put("TOTAL_SCORE", FieldValue.increment(score));
                            batch.update(totalScoreDoc, totalScoreData);

                            // Lưu điểm cao nhất
                            testScoreData.put("HighScore", score);
                            batch.set(userDoc, testScoreData);
                        }

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
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    //////// ========= GETTER =========
    //// Hàm getUserCount để lấy số lượng người dùng trong Collection "Users"
    public void getUserCount(MyCompleteListener myCompleteListener){
        g_firestore.collection("Users").document("TOTAL_USERS").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        g_userCount = documentSnapshot.getLong("COUNT").intValue();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static void getUserProfile(String loginMethod, MyCompleteListenerWithData_2 myCompleteListenerWithData_2){
        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("NAME");
                        String email = documentSnapshot.getString("EMAIL");
                        String password = documentSnapshot.getString("PASSWORD");
                        String birthday = documentSnapshot.getString("BTH_DATE");
                        String phoneNumber = documentSnapshot.getString("PHONE_NO");
                        Integer totalScore = documentSnapshot.getLong("TOTAL_SCORE").intValue();
                        Profile profile = null;

                        switch (loginMethod){
                            case "AppAccount":
                                profile = new Profile(name, password, email, birthday, phoneNumber, totalScore);
                                break;
                            case "GoogleAccount":
                                profile = new Profile(name, email, totalScore);
                        }

                        myCompleteListenerWithData_2.onSuccess(profile);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        myCompleteListenerWithData_2.onFailure(e);
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

    //////// ========= LESSONS FRAGMENT =========

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
                        Log.d("g_lessonNumber",g_vocabularyLessonNumber+"");

                        /*
                            Ta truy cập các fields còn lại chứa các "Lesson<i>_ID" có giá trị bằng chính ID của các Documents (là các bài học) trong
                            Document "Lessons".
                        */
                        for(int i=1;i<=g_vocabularyLessonNumber;i++){
                            String lessonId = lessonsDocumentList.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "LESSON_"+i+"_ID" (chính là ID của mỗi lesson) trong Document "TOTAL_LESSONS"")
                            Log.d("vocabularyLessonID" + i,lessonId+"");
                            QueryDocumentSnapshot lessonDocument = lessonsDocumentMap.get(lessonId); // Truy cập vào Document có ID là <giá trị của lessonID>

                            String lessonID = lessonDocument.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "Lesson"+i+"_ID" trong Document "Lessons"
                            String lessonName = lessonDocument.getString("Lesson" + i + "_Name"); // Lấy giá trị của field "Lesson"+i+"_Name" trong Document "Lessons"
                            String lessonTitle = lessonDocument.getString("Lesson" + i + "_Title"); // Lấy giá trị của field "Lesson"+i+"_Title" trong Document "Lessons"
                            String lessonImage = lessonDocument.getString("Lesson" + i + "_Image"); // Lấy giá trị của field "Lesson"+i+"_Image" trong Document "Lessons"

                            Log.d("vocabularyLesson" + i + "_ID",lessonID+"");
                            Log.d("vocabularyLesson" + i + "_Name",lessonName+"");
                            Log.d("vocabularyLesson" + i + "_Title",lessonTitle+"");
                            Log.d("vocabularyLesson" + i + "_Image",lessonImage+"");

                            g_vocabularyLessonList.add(new LessonChoices(lessonID, lessonTitle, lessonName, lessonImage));
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

    public static void loadVocabularyLessonQuestion(final MyCompleteListener myCompleteListener){

        Log.d("loadVocabularyLessons","accessed SUCCESSFULLY");
        if (g_vocabularyQuestionList == null) {
            g_vocabularyQuestionList = new ArrayList<>();
        }
        g_vocabularyQuestionList.clear(); // Xóa dữ liệu cũ trong lessonList

        g_firestore.collection("Lessons").document("VocabularyLesson_" + (g_selectedLesson_index + 1))
                .collection("VocabularyQuestions").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> lessonContentDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonContentDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonContentCountDocument = lessonContentDocumentMap.get("TOTAL_QUESTIONS"); // Truy cập vào Document có id là "TOTAL_QUESTIONS"
                        long questionNumber = Long.parseLong(lessonContentCountDocument.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_QUESTIONS"
                        g_lessonContentNumber = (int) questionNumber;

                        for(int i=1;i<=g_lessonContentNumber;i++) {
                            String questionId = lessonContentCountDocument.getString("Question" + i + "_ID"); // Lấy giá trị của field "Question_"+i+"_ID" trong Document "TOTAL_QUESTIONS"")
                            QueryDocumentSnapshot document = lessonContentDocumentMap.get(questionId); // Truy cập vào Document có ID là <giá trị của lessonID>

                            String question = document.getString("QUESTION");
                            String optionA = document.getString("A");
                            String optionB = document.getString("B");
                            String optionC = document.getString("C");
                            String optionD = document.getString("D");
                            Integer answer = document.getLong("ANSWER").intValue();

                            // Thêm câu hỏi vào danh sách
                            g_vocabularyQuestionList.add(new QuestionItems(question, optionA, optionB, optionC, optionD, answer, -1)); // -1 là chưa chọn câu trả lời nào, ta chỉ lấy thông tin các câu hỏi ở phần truy xuất này

                            Log.d("question"+i,g_vocabularyQuestionList.get(i-1).getQuestion());
                            Log.d("optionA",g_vocabularyQuestionList.get(i-1).getOption1());
                            Log.d("optionB",g_vocabularyQuestionList.get(i-1).getOption2());
                            Log.d("optionC",g_vocabularyQuestionList.get(i-1).getOption3());
                            Log.d("optionD",g_vocabularyQuestionList.get(i-1).getOption4());
                            Log.d("answer",g_vocabularyQuestionList.get(i-1).getCorrectAnswer().toString());
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

    //////// ========= GRAMMAR LESSONS ACTIVITY =========
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

                        QueryDocumentSnapshot lessonsDocumentList = lessonsDocumentMap.get("TOTAL_GRAMMARS"); // Truy cập vào Document có id là "TOTAL_GRAMMARS"
                        if (lessonsDocumentList == null) {
                            Log.e("DBQuery", "Không tìm thấy document TOTAL_GRAMMARS trong collection Grammars!");
                            myCompleteListener.onFailure();
                            return;
                        }

                        long lessonNumber = Long.parseLong(lessonsDocumentList.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_GRAMMARS"
                        g_grammarLessonNumber = (int) lessonNumber;
                        Log.d("g_lessonNumber",g_grammarLessonNumber+"");

                        /*
                            Ta truy cập các fields còn lại chứa các "Lesson<i>_ID" có giá trị bằng chính ID của các Documents (là các bài học) trong
                            Document "Lessons".
                        */
                        for(int i=1;i<=g_grammarLessonNumber;i++){
                            String lessonId = lessonsDocumentList.getString("Lesson" + i + "_ID"); // Lấy giá trị của field "LESSON_"+i+"_ID" (chính là ID của mỗi lesson) trong Document "TOTAL_GRAMMARS"
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

    //////// ========= LISTENING LESSONS ACTIVITY =========
    //// Hàm loadListeningQuestions để lấy danh sách các câu hỏi cho bài học nghe trong Collection "Listening"
    public static void loadListeningLessonContent(final MyCompleteListener myCompleteListener){

        if (g_listeningQuestionList == null) {
            g_listeningQuestionList = new ArrayList<>();
        }
        g_listeningQuestionList.clear();

        g_firestore.collection("Listening").document("ListeningLesson_" + (g_selectedLesson_index + 1))
                .collection("ListeningQuestions").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> lessonContentDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonContentDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonContentCountDocument = lessonContentDocumentMap.get("TOTAL_QUESTIONS"); // Truy cập vào Document có id là "TOTAL_QUESTIONS"
                        long questionNumber = Long.parseLong(lessonContentCountDocument.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_QUESTIONS"
                        g_lessonContentNumber = (int) questionNumber;

                        for(int i=1;i<=g_lessonContentNumber;i++) {
                            String questionId = lessonContentCountDocument.getString("Question" + i + "_ID"); // Lấy giá trị của field "Question_"+i+"_ID" trong Document "TOTAL_QUESTIONS"")
                            QueryDocumentSnapshot document = lessonContentDocumentMap.get(questionId); // Truy cập vào Document có ID là <giá trị của lessonID>

                            String question = document.getString("QUESTION");
                            String optionA = document.getString("A");
                            String optionB = document.getString("B");
                            String optionC = document.getString("C");
                            String optionD = document.getString("D");
                            Integer answer = document.getLong("ANSWER").intValue();

                            // Thêm câu hỏi vào danh sách
                            g_listeningQuestionList.add(new QuestionItems(question, optionA, optionB, optionC, optionD, answer, -1)); // -1 là chưa chọn câu trả lời nào, ta chỉ lấy thông tin các câu hỏi ở phần truy xuất này

                            Log.d("question"+i,g_listeningQuestionList.get(i-1).getQuestion());
                            Log.d("optionA",g_listeningQuestionList.get(i-1).getOption1());
                            Log.d("optionB",g_listeningQuestionList.get(i-1).getOption2());
                            Log.d("optionC",g_listeningQuestionList.get(i-1).getOption3());
                            Log.d("optionD",g_listeningQuestionList.get(i-1).getOption4());
                            Log.d("answer",g_listeningQuestionList.get(i-1).getCorrectAnswer().toString());
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

    //////// ========= SPEAKING LESSONS ACTIVITY =========
    //// Hàm loadSpeakingQuestions để lấy danh sách các câu hỏi cho bài học nghe trong Collection "Listening"
    public static void loadSpeakingLessonContent(final MyCompleteListener myCompleteListener){

        if (g_speakingQuestionList == null) {
            g_speakingQuestionList = new ArrayList<>();
        }
        g_speakingQuestionList.clear();

        g_firestore.collection("Speaking").document("SpeakingLesson_" + (g_selectedLesson_index + 1))
                .collection("SpeakingQuestions").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> lessonContentDocumentMap = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            lessonContentDocumentMap.put(doc.getId(), doc); // Thêm 1 cặp gồm id (của Document) và Document doc vào trong Map
                        }

                        QueryDocumentSnapshot lessonContentCountDocument = lessonContentDocumentMap.get("TOTAL_QUESTIONS"); // Truy cập vào Document có id là "TOTAL_QUESTIONS"
                        long questionNumber = Long.parseLong(lessonContentCountDocument.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_QUESTIONS"
                        g_lessonContentNumber = (int) questionNumber;

                        Log.d("g_lessonContentNumber",g_lessonContentNumber+"");

                        for(int i=1;i<=g_lessonContentNumber;i++) {
                            String questionId = lessonContentCountDocument.getString("Question" + i + "_ID"); // Lấy giá trị của field "Question_"+i+"_ID" trong Document "TOTAL_QUESTIONS"")
                            QueryDocumentSnapshot document = lessonContentDocumentMap.get(questionId); // Truy cập vào Document có ID là <giá trị của lessonID>

                            String question = document.getString("QUESTION");
                            String simplifiedContent = document.getString("SIMPLIFIED_CONTENT");
                            String pronunciation = document.getString("PRONUNCIATION");

                            // Thêm câu hỏi vào danh sách
                            g_speakingQuestionList.add(new QuestionItems(question, simplifiedContent, pronunciation));

                            Log.d("question"+i,g_speakingQuestionList.get(i-1).getQuestion());
                            Log.d("simplifiedContent",g_speakingQuestionList.get(i-1).getSimplifiedContent());
                            Log.d("pronunciation",g_speakingQuestionList.get(i-1).getPronunciation());

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

    //////// ========= EVERYDAY FRAGMENT =========

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

    //////// ========= LESSON CONTENT ACTIVITY =========
    ///// Hàm loadLessonContent để lấy danh sách nội dung học trong Collection "VocabularyWords" của từng Document trong Collection "Lessons" (trừ Document "TOTAL_LESSONS")
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

                        QueryDocumentSnapshot lessonContentCountDocument = lessonContentDocumentMap.get("TOTAL_WORDS"); // Truy cập vào Document có id là "TOTAL_WORDS"
                        long wordNumber = Long.parseLong(lessonContentCountDocument.get("COUNT").toString()); // Lấy giá trị của field "COUNT" trong Document "TOTAL_WORDS"
                        g_lessonContentNumber = (int) wordNumber;
                        Log.d("g_lessonContentNumber",g_lessonContentNumber+"");

                        for(int i=1;i<=g_lessonContentNumber;i++) {
                            QueryDocumentSnapshot lessonContentDocument = lessonContentDocumentMap.get("Word_" + i); // Truy cập vào Document có ID là "Word"+i

                            String wordID = lessonContentDocument.getString("Word" + i + "_ID"); // Lấy giá trị của field "Word"+i+"_ID" trong Document "TOTAL_WORDS"
                            String wordSimplifiedContent = lessonContentDocument.getString("Word" + i + "_SimplifiedContent"); // Lấy giá trị của field "Word"+i+"_SimplifiedContent" trong Document "TOTAL_WORDS"
                            String wordContent = lessonContentDocument.getString("Word" + i + "_Content"); // Lấy giá trị của field "Word"+i+"_Content" trong Document "TOTAL_WORDS"
                            String pronunciation = lessonContentDocument.getString("Word" + i + "_Pronunciation"); // Lấy giá trị của field "Word"+i+"_Pronunciation" trong Document "TOTAL_WORDS"
                            String meaning = lessonContentDocument.getString("Word" + i + "_Meaning"); // Lấy giá trị của field "Word"+i+"_Meaning" trong Document "TOTAL_WORDS"

                            Log.d("word"+i+"_ID",wordID+"");
                            Log.d("word"+i+"_SimplifiedContent",wordSimplifiedContent+"");
                            Log.d("word"+i+"_Content",wordContent+"");
                            Log.d("word"+i+"_Pronunciation",pronunciation+"");
                            Log.d("word"+i+"_Meaning",meaning+"");

                            g_lessonContentList.add(new VocabularyWords(wordID, wordContent, wordSimplifiedContent, pronunciation, meaning));

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



}
