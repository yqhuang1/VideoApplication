package com.fmscreenrecord.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import java.util.Comparator;

/**
 * 排序类 主要对第三方应用列表进行(根据启动次数)排序
 * 
 * @author WYX
 * 
 */
public class PackageInfoIntComparator implements Comparator {
	private SharedPreferences sharedPreferences;
	// 传入的包名
	String packageName1, packageName2;
	// 最多启动次数
	int moreTimes1, moreTimes2;
	// 最新启动应用
	long newTimes1, newTimes2;
	// 排序方式
	int switchCompare;

	public PackageInfoIntComparator(Context context, int switchCompare) {
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.switchCompare = switchCompare;
	}

	public int compare(Object lhs, Object rhs) {
		// 获取传入的两个应用的包名
		packageName1 = ((PackageInfo) lhs).packageName;
		packageName2 = ((PackageInfo) rhs).packageName;
		if (switchCompare == 0) {

			// 根据包名获取存于SP的启动次数
			moreTimes1 = sharedPreferences.getInt(packageName1, 0);
			moreTimes2 = sharedPreferences.getInt(packageName2, 0);
			// 对启动次数进行排序
			if (moreTimes1 < moreTimes2) {
				return 1;
			}
			if (moreTimes1 > moreTimes2) {
				return -1;
			}
		}else{
			// 根据包名获取存于SP的最近启动次数
			newTimes1 = sharedPreferences.getLong(packageName1+"new", 0);
			newTimes2 = sharedPreferences.getLong(packageName2+"new", 0);
			// 对启动次数进行排序
			if (newTimes1 < newTimes2) {
				return 1;
			}
			if (newTimes1 > newTimes2) {
				return -1;
			}
		}
		return 0;
	}

}
