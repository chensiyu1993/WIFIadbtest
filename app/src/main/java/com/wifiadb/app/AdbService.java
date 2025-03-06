package com.wifiadb.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AdbService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "wifi_adb_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 创建通知
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);

        // 启用WiFi ADB
        enableWifiAdb();

        // 如果服务被杀死，系统会尝试重启它
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在服务销毁时，可以考虑是否需要禁用WiFi ADB
        // 根据需求可以保持启用或禁用
        // disableWifiAdb();
    }

    private void enableWifiAdb() {
        try {
            // 启用TCP/IP模式
            String port = "5555";
            AdbUtils.executeCommand("setprop service.adb.tcp.port " + port);
            
            // 停止并重启ADB服务
            AdbUtils.executeCommand("stop adbd");
            AdbUtils.executeCommand("start adbd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableWifiAdb() {
        try {
            // 禁用TCP/IP模式
            AdbUtils.executeCommand("setprop service.adb.tcp.port -1");
            
            // 停止并重启ADB服务
            AdbUtils.executeCommand("stop adbd");
            AdbUtils.executeCommand("start adbd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_channel_description));
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_adb)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }
} 