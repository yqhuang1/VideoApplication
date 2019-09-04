package com.fmscreenrecord.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/9/29 0029.
 * 打开手机悬浮窗方法类
 *
 * @author WYX
 */
public class OpenFloatView {
    // 获取设备厂商
    public String getManufacturer(Context context) {

        String version;
        BufferedReader input = null;
        try {// 判断是否MIUI系统
            Process p = Runtime.getRuntime().exec(
                    "getprop " + "ro.miui.ui.version.name");
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            version = input.readLine();
            input.close();
        } catch (IOException ex) {
            // 不能直接返回null(空)
            return "null";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {

                }
            }
        }
        if (!version.isEmpty()) {// 判断是否小米系统
            return "MIUI";
        }
        if (isInstallApp("com.huawei.systemmanager",context)) {// 判断是否华为系统
            return "HUAWEI";
        }
        return "null";
    }
    /**
     * 打开小米系统悬浮窗
     */
    private final String SCHEME = "package";
    private final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private final String APP_PKG_NAME_22 = "pkg";
    private final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    public void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            MinUtil.showToast(context, "抱歉，您当前的系统版本需要手动打开浮窗");
        }

    }

    public boolean isInstallApp(String packageName,Context context) {
        PackageInfo packageInfo;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);

        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {

            return false;
        } else {

            return true;
        }
    }
}
