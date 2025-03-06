package com.wifiadb.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView ipAddressText;
    private TextView portText;
    private TextView commandText;
    private Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        statusText = findViewById(R.id.statusText);
        ipAddressText = findViewById(R.id.ipAddressText);
        portText = findViewById(R.id.portText);
        commandText = findViewById(R.id.commandText);
        restartButton = findViewById(R.id.restartButton);

        // 启动ADB服务
        startAdbService();

        // 设置重启按钮点击事件
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartAdbService();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 更新UI显示
        updateUiInfo();
    }

    private void startAdbService() {
        Intent intent = new Intent(this, AdbService.class);
        startService(intent);
    }

    private void restartAdbService() {
        Intent intent = new Intent(this, AdbService.class);
        stopService(intent);
        startService(intent);
        updateUiInfo();
    }

    private void updateUiInfo() {
        // 获取ADB服务状态和信息
        AdbInfo adbInfo = AdbUtils.getAdbInfo(this);
        
        if (adbInfo.isEnabled()) {
            statusText.setText(getString(R.string.status_enabled));
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            statusText.setText(getString(R.string.status_disabled));
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        ipAddressText.setText(adbInfo.getIpAddress());
        portText.setText(String.valueOf(adbInfo.getPort()));
        commandText.setText("adb connect " + adbInfo.getIpAddress() + ":" + adbInfo.getPort());
    }
} 