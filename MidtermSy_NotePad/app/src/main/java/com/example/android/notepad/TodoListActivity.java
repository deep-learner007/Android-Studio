// java
package com.example.android.notepad;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TodoListActivity extends ListActivity {
    private static final int REQUEST_ADD = 100;
    private static final int REQUEST_EDIT = 101;

    private TodoDbHelper dbHelper;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new TodoDbHelper(this);
        cursor = dbHelper.queryAllTodos();

        String[] from = new String[] { TodoDbHelper.COL_TITLE, TodoDbHelper.COL_DONE };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor, from, to, 0);

        adapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor c, int columnIndex) {
                int idxDone = c.getColumnIndexOrThrow(TodoDbHelper.COL_DONE);
                if (view.getId() == android.R.id.text2) {
                    int done = c.getInt(idxDone);
                    ((TextView) view).setText(done == 1 ? "已完成" : "未完成");
                    return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, TodoEditorActivity.class);
        intent.putExtra(TodoEditorActivity.EXTRA_TODO_ID, id);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // 动态添加一个“添加待办”菜单项，避免额外文件
        menu.add(0, R.id.menu_todo_add, 0, "添加待办")
                .setIcon(android.R.drawable.ic_input_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_todo_add) {
            // 启动编辑界面用于新建（不传 EXTRA）
            Intent intent = new Intent(this, TodoEditorActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 仅在保存成功时刷新列表
        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD || requestCode == REQUEST_EDIT)) {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            cursor = dbHelper.queryAllTodos();
            adapter.changeCursor(cursor);
        }
    }
}
