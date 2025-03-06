package com.wifiadb.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class AdbUtils {

    private static final int DEFAULT_ADB_PORT = 5555;

    /**
     * 执行Shell命令
     */
    public static String executeCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader reader = null;
        StringBuilder output = new StringBuilder();

        try {
            // 获取root权限的进程
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            // 执行命令
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            // 读取命令输出
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待命令执行完成
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return "执行命令出错: " + e.getMessage();
        } finally {
            try {
                if (os != null) os.close();
                if (reader != null) reader.close();
                if (process != null) process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return output.toString().trim();
    }

    /**
     * 获取ADB状态信息
     */
    public static AdbInfo getAdbInfo(Context context) {
        String ipAddress = getIpAddress(context);
        int port = getAdbPort();
        boolean isEnabled = isAdbEnabled();

        return new AdbInfo(ipAddress, port, isEnabled);
    }

    /**
     * 获取设备IP地址
     */
    private static String getIpAddress(Context context) {
        // 首先尝试获取WiFi IP
        String ipAddress = getWifiIpAddress(context);
        
        // 如果WiFi IP无效，尝试获取移动网络IP
        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equals("0.0.0.0")) {
            ipAddress = getMobileIpAddress();
        }
        
        // 如果两种方式都无法获取有效IP，返回默认值
        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equals("0.0.0.0")) {
            ipAddress = "127.0.0.1";
        }
        
        return ipAddress;
    }

    /**
     * 获取WiFi IP地址
     */
    private static String getWifiIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipInt = wifiInfo.getIpAddress();
                return String.format("%d.%d.%d.%d",
                        (ipInt & 0xff), (ipInt >> 8 & 0xff),
                        (ipInt >> 16 & 0xff), (ipInt >> 24 & 0xff));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }

    /**
     * 获取移动网络IP地址
     */
    private static String getMobileIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        return "";
    }

    /**
     * 获取ADB端口
     */
    private static int getAdbPort() {
        try {
            String result = executeCommand("getprop service.adb.tcp.port");
            if (result != null && !result.isEmpty() && !result.equals("-1")) {
                return Integer.parseInt(result);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        return DEFAULT_ADB_PORT;
    }

    /**
     * 检查ADB是否已启用
     */
    private static boolean isAdbEnabled() {
        try {
            String result = executeCommand("getprop service.adb.tcp.port");
            return result != null && !result.isEmpty() && !result.equals("-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
} 