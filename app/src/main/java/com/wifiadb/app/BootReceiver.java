package com.wifiadb.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * 开机自启动接收器
 * 接收开机广播，自动启动ADB服务
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && 
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 启动ADB服务
            Intent serviceIntent = new Intent(context, AdbService.class);
            
            // 在Android O及更高版本使用startForegroundService
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
} 