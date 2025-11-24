// java
package com.example.android.notepad;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class TodoEditorActivity extends Activity {
    public static final String EXTRA_TODO_ID = "todo_id";

    private TodoDbHelper dbHelper;
    private EditText titleView;
    private EditText noteView;
    private CheckBox doneView;
    private long todoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_editor);

        dbHelper = new TodoDbHelper(this);
        titleView = (EditText) findViewById(R.id.todo_title);
        noteView = (EditText) findViewById(R.id.todo_note);
        doneView = (CheckBox) findViewById(R.id.todo_done);
        Button saveBtn = (Button) findViewById(R.id.todo_save);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_TODO_ID)) {
            todoId = getIntent().getLongExtra(EXTRA_TODO_ID, -1);
            if (todoId >= 0) {
                Cursor c = dbHelper.queryTodoById(todoId);
                if (c != null && c.moveToFirst()) {
                    titleView.setText(c.getString(c.getColumnIndexOrThrow(TodoDbHelper.COL_TITLE)));
                    noteView.setText(c.getString(c.getColumnIndexOrThrow(TodoDbHelper.COL_NOTE)));
                    int done = c.getInt(c.getColumnIndexOrThrow(TodoDbHelper.COL_DONE));
                    doneView.setChecked(done != 0);
                    c.close();
                }
            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleView.getText().toString().trim();
                String note = noteView.getText().toString().trim();
                int done = doneView.isChecked() ? 1 : 0;
                if (todoId >= 0) {
                    dbHelper.updateTodo(todoId, title, note, done);
                } else {
                    long now = System.currentTimeMillis();
                    dbHelper.insertTodo(title, note, done, now);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 可选择性关闭 dbHelper.getWritableDatabase()，SQLiteOpenHelper 可复用
    }
}
