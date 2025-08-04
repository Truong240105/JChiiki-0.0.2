package com.example.jchiiki.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OfflineDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "JChiikiOffline.db";

    private static final String SQL_CREATE_LESSONS_TABLE =
            "CREATE TABLE " + OfflineContract.LessonEntry.TABLE_NAME + " (" +
                    OfflineContract.LessonEntry._ID + " INTEGER PRIMARY KEY," +
                    OfflineContract.LessonEntry.COLUMN_NAME_LESSON_ID + " TEXT UNIQUE," +
                    OfflineContract.LessonEntry.COLUMN_NAME_LESSON_NAME + " TEXT," +
                    OfflineContract.LessonEntry.COLUMN_NAME_LESSON_IMAGE + " TEXT)";

    private static final String SQL_CREATE_WORDS_TABLE =
            "CREATE TABLE " + OfflineContract.WordEntry.TABLE_NAME + " (" +
                    OfflineContract.WordEntry._ID + " INTEGER PRIMARY KEY," +
                    OfflineContract.WordEntry.COLUMN_NAME_LESSON_ID_FK + " TEXT," +
                    OfflineContract.WordEntry.COLUMN_NAME_WORD_CONTENT + " TEXT," +
                    OfflineContract.WordEntry.COLUMN_NAME_SIMPLIFIED_CONTENT + " TEXT," +
                    OfflineContract.WordEntry.COLUMN_NAME_PRONUNCIATION + " TEXT," +
                    OfflineContract.WordEntry.COLUMN_NAME_MEANING + " TEXT)";

    private static final String SQL_DELETE_LESSONS_TABLE =
            "DROP TABLE IF EXISTS " + OfflineContract.LessonEntry.TABLE_NAME;
    private static final String SQL_DELETE_WORDS_TABLE =
            "DROP TABLE IF EXISTS " + OfflineContract.WordEntry.TABLE_NAME;


    public OfflineDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LESSONS_TABLE);
        db.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + OfflineContract.WordEntry.TABLE_NAME + " ADD COLUMN " + OfflineContract.WordEntry.COLUMN_NAME_PRONUNCIATION + " TEXT;");
        }
        db.execSQL(SQL_DELETE_LESSONS_TABLE);
        db.execSQL(SQL_DELETE_WORDS_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
} 