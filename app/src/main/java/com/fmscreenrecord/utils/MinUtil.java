package com.fmscreenrecord.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.app.ExApplication;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/******************************************************
 * Copyright (c) 广州飞磨软件科技有限公司 <br/>
 * 项目名称: [录屏大师] <br/>
 * 文件名称: [小工具集合] - [MinUtily.java] <br/>
 * 功能描述:[集成多重小工具类] <br/>
 * 创 建 者 : [王涌鑫] <br/>
 * 创建时间: [2015-03-27]
 ******************************************************/
public class MinUtil {

	private static Toast mToast;

	/**
	 * 获取版本号
	 *
	 * @param context
	 * @return
	 */
	// 友盟点击次数统计相关的hashmap
	private static HashMap<String, String> Mopmap = null;

	public static String getVersion(Context context) {
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
			return "1.0";
		}

	}

	/**
	 * 获取版本号(内部识别号)
	 *
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 判断当前网络环境是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 检测是否处于wifi状态
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	/**
	 * 获取当前网络类型
	 *
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context) {
		// 获取当前网络环境信息
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		// 对网络环境进行判断
		if (networkInfo != null) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return NETTYPE_WIFI;
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				return NETTYPE_CMNET;
			}
		}

		return 0;
	}

	/**
	 * 将手机分辨率末尾数置为0 手机的分辨率末尾数如不为0，有一定几率导致视频合成失败
	 *
	 * @param resolution
	 *            传入需要转制的分辨率
	 *
	 *            public static int getResolution(int resolution) { // 获取分辨率数的长度
	 *            String mleng = String.valueOf(resolution); // 截取至分辨率末尾前一位 int
	 *            tempRes =
	 *            Integer.valueOf(String.valueOf(resolution).substring(0,
	 *            mleng.length() - 1)); int newResolution = tempRes * 10; return
	 *            newResolution; }
	 */
	/**
	 * 设置控件所在的位置XY，并且不改变宽高， XY为绝对位置
	 */
	public static void setLayout(View view, int x, int y) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(x, y, x + margin.width, y + margin.height);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	/***
	 * 根据浮窗所在象限修改浮窗控件坐标(非边角)
	 */
	public static float[] getlayoutXY(float x, float y) {
		float[] mfloat = new float[2];
		int DEVW = ExApplication.DEVW;
		int DEVH = ExApplication.DEVH;
		// 如果当前手机屏幕为横屏
		if (MainActivity.getConfiguration() == true) {
			// 交换横竖屏数值
			int temp = DEVH;
			DEVH = DEVW;
			DEVW = temp;

		}

		// 一、四象限
		if (ExApplication.moveX > (DEVW / 2)) {
			// 第四象限
			if (ExApplication.moveY > (DEVH / 2)) {
				x = -x;
			} else {// 第一象限
				x = -x;

			}
			ExApplication.FloatViewLeft = false;
		} else {
			ExApplication.FloatViewLeft = true;
		}
		mfloat[0] = x;
		mfloat[1] = y;

		return mfloat;

	}

	/***
	 * 根据浮窗所在象限修改浮窗控件坐标
	 */
	public static float[] getlayoutXY2(float x, float y) {
		float[] mfloat = new float[2];
		int DEVW = ExApplication.DEVW;
		int DEVH = ExApplication.DEVH;
		// 如果当前手机屏幕为横屏
		if (MainActivity.getConfiguration() == true) {
			// 交换横竖屏数值
			int temp = DEVH;
			DEVH = DEVW;
			DEVW = temp;
		}

		// 一、四象限
		if (ExApplication.moveX > (DEVW / 2)) {
			// 第四象限
			if (ExApplication.moveY > (DEVH / 2)) {
				x = -x;
				y = -y;

			} else {// 第一象限
				x = -x;
				y = y + 25;

			}
			ExApplication.FloatViewLeft = false;
		} else {// 二三象限
			// 第三象限
			if (ExApplication.moveY > (DEVH / 2)) {
				y = -y + 1;

			} // 第二象限
			else {
				y = y + 25;
			}
			ExApplication.FloatViewLeft = true;
		}
		mfloat[0] = x;
		mfloat[1] = y;

		return mfloat;

	}

	/******** 返回友盟设备识别信息 ***********/
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将相关按钮的统计次数上传至友盟
	 *
	 * @param context
	 *            上下文
	 * @param type
	 *            参数类型(如一个事件ID有不同类型参数时，type可以作为区分参考）
	 * @param id
	 *            事件ID(该ID作为在友盟后台查看自定义事件ID的筛选值，在程序中请确保唯一性）
	 */
	public static void upUmenEventValue(Context context, String type, String id) {
		Mopmap = new HashMap<String, String>();
		Mopmap.put("type", type);
		MobclickAgent.onEventValue(context, id, Mopmap, 1);

	}

	/**
	 * 计算录屏时间长度 根据不同时长返回相应的数值
	 *
	 * @param startTime
	 *            开始录屏时的时间毫秒值
	 * @param endTime
	 *            停止录屏是的时间毫秒值
	 * @return 1: 一分钟以内 <br/>
	 *         2: 一到五分钟 <br/>
	 *         3: 五到十分钟 <br/>
	 *         4: 十分钟以上
	 *
	 */
	public static int countRecTime(long startTime, long endTime) {
		long time;
		time = endTime - startTime;
		// 计算分钟数
		int min = (int) (time / 1000 / 60);
		// 根据录制时间的分钟数返回不同的值
		if (min < 1) {
			return 1;
		} else if (1 <= min && min < 5) {
			return 2;
		} else if (5 <= min && min < 10) {
			return 3;
		} else {
			return 4;
		}

	}

	public static void mylog(String str) {
		Log.i("mylog", str);
	}

	/**
	 * 自定义toast，防止多次调用一个toast时多次弹出问题
	 * @param context
	 * @param text toast内容
	 */
	public static void showToast(Context context,String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text,
					Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

}
