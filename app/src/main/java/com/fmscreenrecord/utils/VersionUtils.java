package com.fmscreenrecord.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 版本控制工具类
 * Created by user on 2014/12/10.
 */
public class VersionUtils {
    public final static String PACKAGE_NAME="com.li.videoapplication";

    /**
     * 获取当前程序版本号
     * @param context
     * @return
     */
    public static int getCurrentVersionCode(Context context){
        try {
            return context.getPackageManager().getPackageInfo(PACKAGE_NAME,0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取当前程序版本名称
     * @param context
     * @return
     */
    public static String getCurrentVersionName(Context context){
        try {
            return context.getPackageManager().getPackageInfo(PACKAGE_NAME,0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     *根据版本名判断得到的版本是否为新版本
     * @param versionName 得到的版本名
     * @param currentVersionName 当前版本名
     * @return
     */
    public static boolean isNewVersion(String versionName,String currentVersionName){
        return versionName.compareToIgnoreCase(currentVersionName) > 0;
    }

    /**
     * 根据版本号判断得到的版本是否为新版本
     * @param versionCode 得到的版本号
     * @param currentVersionCode 当前版本号
     * @return
     */
    public static boolean isNewVersion(int versionCode,int currentVersionCode){
        return versionCode > currentVersionCode;
    }
}
