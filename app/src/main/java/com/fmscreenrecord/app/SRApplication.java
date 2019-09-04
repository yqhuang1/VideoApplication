package com.fmscreenrecord.app;

/**
 * Application
 * @author lin
 * Create：2014-12
 */

import android.app.Application;
import android.content.Context;

public class SRApplication extends Application {
	private static com.fmscreenrecord.app.SRApplication application;
	public boolean isShowFloatView = true;
	public Context context;
	/**
	 * 判断当前上一个视频是否上传完成/结束。 当有视频上传中，值为false
	 */
	public static boolean isCanUpload = true;
	// 检测第一次启动app以便于检测更新
	public static boolean isCheckVersion = true;

	@Override
	public void onCreate() {
		application = com.fmscreenrecord.app.SRApplication.this;
		super.onCreate();
	}

	public static com.fmscreenrecord.app.SRApplication Get() {
		return application;
	}

	public static Context GetAppContext() {
		return application.context;
	}

	public static boolean getCheckVersion() {

		return isCheckVersion;
	}

	public static void setChekVersion() {
		isCheckVersion = false;
	}

}
