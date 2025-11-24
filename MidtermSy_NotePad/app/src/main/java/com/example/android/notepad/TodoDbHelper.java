// app/src/main/java/com/example/android/notepad/TodoDbHelper.java
package com.example.android.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "todos.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "todos";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_NOTE = "note";
    public static final String COL_DONE = "done"; // 0 or 1
    public static final String COL_CREATED = "created_ms";

    public TodoDbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_NOTE + " TEXT, "
                + COL_DONE + " INTEGER DEFAULT 0, "
                + COL_CREATED + " INTEGER"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // 简单升级策略：删除并重建（生产环境请做迁移）
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertTodo(String title, String note, int done, long createdMs) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_NOTE, note);
        cv.put(COL_DONE, done);
        cv.put(COL_CREATED, createdMs);
        return db.insert(TABLE_NAME, null, cv);
    }

    public int updateTodo(long id, String title, String note, int done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_NOTE, note);
        cv.put(COL_DONE, done);
        return db.update(TABLE_NAME, cv, COL_ID + "=?", new String[]{ String.valueOf(id) });
    }

    public Cursor queryAllTodos() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME,
                new String[]{COL_ID, COL_TITLE, COL_NOTE, COL_DONE, COL_CREATED},
                null, null, null, null, COL_CREATED + " DESC");
    }

    public Cursor queryTodoById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME,
                new String[]{COL_ID, COL_TITLE, COL_NOTE, COL_DONE, COL_CREATED},
                COL_ID + "=?", new String[]{ String.valueOf(id) },
                null, null, null);
    }

    public int deleteTodo(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + "=?", new String[]{ String.valueOf(id) });
    }
}
