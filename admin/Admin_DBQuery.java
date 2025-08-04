private static Map<String, QueryDocumentSnapshot> toDocumentMap(QuerySnapshot queryDocumentSnapshots) {
    Map<String, QueryDocumentSnapshot> map = new ArrayMap<>();
    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
        map.put(doc.getId(), doc);
    }
    return map;
}

Map<String, QueryDocumentSnapshot> lessonsDocumentMap = toDocumentMap(queryDocumentSnapshots);

private static <T> void loadLessons(
        String collectionName,
        String totalDocId,
        int lessonCount,
        LessonMapper<T> mapper,
        ArrayList<T> lessonList,
        MyCompleteListener myCompleteListener
) {
    g_firestore.collection(collectionName).get()
        .addOnSuccessListener(documentSnapshot -> {
            Map<String, QueryDocumentSnapshot> docMap = toDocumentMap(documentSnapshot);
            QueryDocumentSnapshot totalDoc = docMap.get(totalDocId);
            for (int i = 1; i <= lessonCount; i++) {
                String lessonId = totalDoc.getString("Lesson" + i + "_ID");
                QueryDocumentSnapshot lessonDoc = docMap.get(lessonId);
                lessonList.add(mapper.map(i, lessonDoc));
            }
            myCompleteListener.onSuccess();
        })
        .addOnFailureListener(e -> {
            e.printStackTrace();
            myCompleteListener.onFailure(e);
        });
}

private interface LessonMapper<T> {
    T map(int i, QueryDocumentSnapshot doc);
}

public static void loadVocabularyLessons(final MyCompleteListener myCompleteListener){
    Log.d("loadVocabularyLessons","accessed SUCCESSFULLY");
    if (g_vocabularyLessonList == null) {
        g_vocabularyLessonList = new ArrayList<>();
    }
    g_vocabularyLessonList.clear();

    getLessonNumber(lessonNumber -> {
        loadLessons(
            "Lessons",
            "TOTAL_LESSONS",
            g_vocabularyLessonNumber,
            (i, doc) -> new AdminLessonChoices(
                doc.getString("Lesson" + i + "_ID"),
                doc.getString("Lesson" + i + "_Title"),
                doc.getString("Lesson" + i + "_Name"),
                doc.getString("Lesson" + i + "_Image")
            ),
            g_vocabularyLessonList,
            myCompleteListener
        );
    });
} 