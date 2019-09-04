package com.fmscreenrecord.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtils {
	public static SharedPreferences getMinJieKaiFaPreferences(Context context) {
		return context.getSharedPreferences("com_fmscreenrecord.properties",
				Context.MODE_PRIVATE);
	}

	public static void setPreference(Context context, String key, String value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
		editor = null;
	}

	public static String getPreference(Context context, String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String value = preferences.getString(key, "");
		return value;
	}

	public static boolean getPreferenceboolean(Context context, String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean value = preferences.getBoolean(key, false);
		return value;
	}

	/**
	 * 设置保存登录用户信息
	 */
	public static void setUserEntity(Context context, String json) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("User", json);
		Log.e("user", json);
		editor.commit();
		editor = null;
	}

	/**
	 * 
	 * 
	 * 保存当前versionName和VersionCode
	 * 
	 * @param context
	 *            上下文
	 * @param code
	 *            build号
	 * @param name
	 *            版本号
	 */
	public static void setVersionCodeAndVersionName(Context context, int code,
			String name) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("VersionCode", code);
		editor.putString("VersionName", name);
		editor.commit();
		editor = null;
	}

	/**
	 * 获取versionName和VersionCode
	 */
	public static List<Object> getVersionCodeAndVersionName(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		int code = preferences.getInt("VersionCode", 0);
		String name = preferences.getString("VersionName", "");
		List<Object> list = new ArrayList<Object>();
		list.add(code);
		list.add(name);

		return list;
	}
}