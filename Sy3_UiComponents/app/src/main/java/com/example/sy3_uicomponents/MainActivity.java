package com.example.sy3_uicomponents;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.widget.Toolbar;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private TextView tvSample;
    private ListView listView;
    // 建议：将常量声明为 static final
    private static final String CHANNEL_ID = "exp3_channel";
    // 新增：权限请求码
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    private int currentPosition; // 用于在长按菜单中记录位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvSample = findViewById(R.id.tvSample);
        listView = findViewById(R.id.listView);

        initListView();
        createNotificationChannel();
        // 新增：检查并请求通知权限
        checkAndRequestNotificationPermission();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String title = dataList.get(position).get("title").toString();
            Toast.makeText(MainActivity.this, "点击了：" + title, Toast.LENGTH_SHORT).show();
            sendNotification(title, "这是来自实验的通知");
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            currentPosition = position; // 记录当前长按的位置
            startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    getMenuInflater().inflate(R.menu.context_menu, menu);
                    mode.setTitle("选择操作");
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.ctx_delete) {
                        Toast.makeText(MainActivity.this, "删除第 " + (currentPosition + 1) + " 项", Toast.LENGTH_SHORT).show();
                        mode.finish(); // 关闭上下文菜单
                        return true;
                    } else if (itemId == R.id.ctx_share) {
                        Toast.makeText(MainActivity.this, "分享第 " + (currentPosition + 1) + " 项", Toast.LENGTH_SHORT).show();
                        mode.finish(); // 关闭上下文菜单
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // 不需要特别处理
                }
            });
            return true; // 返回true表示消费了长按事件
        });
    }

    // 新增：检查和请求权限的方法
    private void checkAndRequestNotificationPermission() {
        // 仅在 Android 13 (API 33) 及以上版本需要此操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 如果权限未被授予，则发起请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // 新增：处理权限请求结果的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限
                Toast.makeText(this, "通知权限已授予！", Toast.LENGTH_SHORT).show();
            } else {
                // 用户拒绝了权限
                Toast.makeText(this, "您拒绝了通知权限，将无法收到通知。", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void initListView() {
        dataList = new ArrayList<>();
        int[] icons = {
                R.drawable.cat,
                R.drawable.dog,
                R.drawable.elephant,
                R.drawable.lion,
                R.drawable.monkey,
                R.drawable.tiger,

        };

        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("img", icons[i]);
            map.put("title", "图片示例 " + (i + 1));
            map.put("sub", "这是第 " + (i + 1) + " 张图片的说明");
            dataList.add(map);
        }

        String[] from = {"img", "title", "sub"};
        int[] to = {R.id.item_icon, R.id.item_title, R.id.item_sub};
        adapter = new SimpleAdapter(this, dataList, R.layout.list_item, from, to);
        listView.setAdapter(adapter);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "实验通知频道", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("用于实验的通知");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String content) {
        // 在发送前再次检查权限，是更严谨的做法
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "没有通知权限，无法发送通知。", Toast.LENGTH_SHORT).show();
            return; // 如果没有权限，则不执行发送操作
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi)
                .setAutoCancel(true);

        nm.notify((int) System.currentTimeMillis() % 10000, builder.build());
    }

    // ----------------- 菜单操作 -----------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.font_small) {
            tvSample.setTextSize(10);
            return true;
        } else if (itemId == R.id.font_medium) {
            tvSample.setTextSize(16);
            return true;
        } else if (itemId == R.id.font_large) {
            tvSample.setTextSize(20);
            return true;
        } else if (itemId == R.id.color_red) {
            tvSample.setTextColor(Color.RED);
            return true;
        } else if (itemId == R.id.color_black) {
            tvSample.setTextColor(Color.BLACK);
            return true;
        } else if (itemId == R.id.menu_normal) {
            Toast.makeText(this, "点击了普通菜单项", Toast.LENGTH_SHORT).show();
            showCustomDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void showCustomDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);

        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);

        new AlertDialog.Builder(this)
                .setTitle("用户登录") // 标题栏标题
                .setIcon(R.drawable.cat) // 这里可用你自己的图片
                .setView(dialogView)
                .setPositiveButton("登录", (dialog, which) -> {
                    String name = etUsername.getText().toString();
                    String pwd = etPassword.getText().toString();
                    Toast.makeText(this, "用户名: " + name + "\n密码: " + pwd, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }


}

