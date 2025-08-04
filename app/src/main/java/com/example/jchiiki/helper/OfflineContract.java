package com.example.jchiiki.helper;

import android.provider.BaseColumns;

public final class OfflineContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private OfflineContract() {}

    /* Inner class that defines the table contents */
    public static class LessonEntry implements BaseColumns {
        public static final String TABLE_NAME = "lessons";
        public static final String COLUMN_NAME_LESSON_ID = "lesson_id";
        public static final String COLUMN_NAME_LESSON_NAME = "lesson_name";
        public static final String COLUMN_NAME_LESSON_IMAGE = "lesson_image";
    }

    public static class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_NAME_LESSON_ID_FK = "lesson_id_fk";
        public static final String COLUMN_NAME_WORD_CONTENT = "word_content";
        public static final String COLUMN_NAME_SIMPLIFIED_CONTENT = "simplified_content";
        public static final String COLUMN_NAME_MEANING = "meaning";
        public static final String COLUMN_NAME_PRONUNCIATION = "pronunciation";
    }
} 