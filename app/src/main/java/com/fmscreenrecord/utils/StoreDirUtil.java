package com.fmscreenrecord.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import com.fmscreenrecord.app.ExApplication;

import java.io.File;

public class StoreDirUtil {
	@SuppressLint("SdCardPath")
	@SuppressWarnings("unused")
	// SD卡路径
	static String SDPath;

	public static File getDefault(Context context) {

		String path = "/mnt/sdcard/SupperLulu";
		String newPath = "/mnt/sdcard/LuPingDaShi";

		File srcDir = new File(path);
		// 判断是否存在SupperLulu文件夹
		if (srcDir.exists()) {
			// 如果存在则重命名为LuPingDaShi
			File newFile = new File(newPath);
			srcDir.renameTo(newFile);

			return newFile;
		} else {// 不存在则新建LuPingDaShi文件夹

			File file = new File(newPath);
			if (file.exists() == false) {
				file = new File("/mnt/sdcard/LuPingDaShi");
				file.mkdirs();

			}
			if (file.exists()) {

				return file;

			}
		}

		return null;
	}

	public static File getSDDEfault(Context context) {
		File SdFile = null;

		StorageManager sm = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		// 获取sdcard的路径：外置和内置
		String[] paths = null;
		try {
			paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0])
					.invoke(sm, new Object[]{});
		} catch (Exception e) {

			e.printStackTrace();
		}

		// 如果数组的长度大于一，则说明有一个以上储存空间，即有外置SD卡
		if (paths.length > 1) {
			// 外置SD卡通常是第二个
			SDPath = paths[1];
			SdFile = new File(SDPath + "/LuPingDaShi");

			// 如果根目录存在LuPingDaShi文件夹
			if (SdFile.exists() == true) {
				// 尝试直接在SD卡根目录下创建文件夹,测试是否有操作SD卡根目录权限
				SdFile = new File(SDPath + "/LuPingDaShi/fmfile/fm");
				boolean ifmkdirs = SdFile.mkdirs();

				// 如果无法在根目录下创建，则在/Android/data/com.li.videoapplication下创建
				if (!ifmkdirs) {
					SdFile = new File(SDPath
							+ "/Android/data/com.li.videoapplication/LuPingDaShi");
					if (!SdFile.exists()) {
						// 在/Android/data/com.li.videoapplication/下创建文件夹
						context.getExternalFilesDir(null);
						// 在/Android/data/com.li.videoapplication/下创建LuPingDaShi
						SdFile.mkdirs();
					}
					ExApplication.ExpandSdCardAndroidData = true;
				} else {
					// 删除测试文件并返回路径
					SdFile.delete();
					File file = new File(SDPath + "/LuPingDaShi");
					return file;
				}

			} else { // 根目录下没有LuPingDaShi文件夹
				// 尝试直接在SD卡根目录下创建文件夹,测试是否有操作SD卡根目录权限
				SdFile = new File(SDPath + "/LuPingDaShi/fmfile/fm");
				boolean ifmkdirs = SdFile.mkdirs();

				// 如果无法在根目录下创建，则在/Android/data/com.li.videoapplication下创建
				if (!ifmkdirs) {
					SdFile = new File(SDPath
							+ "/Android/data/com.li.videoapplication/LuPingDaShi");
					if (!SdFile.exists()) {
						// 在/Android/data/com.li.videoapplication/下创建文件夹
						context.getExternalFilesDir(null);
						// 在/Android/data/com.li.videoapplication/下创建LuPingDaShi
						SdFile.mkdirs();
					}
					ExApplication.ExpandSdCardAndroidData = true;
				} else {
					// 删除测试文件并返回路径
					SdFile.delete();
					File file = new File(SDPath + "/LuPingDaShi");
					return file;
				}

			}

			if (SdFile.exists()) {

				return SdFile;

			}
		}
		// 没有外置SD卡，返回NULL
		return null;

	}

	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	public static long getSDAvailableSize(Context context) {
		String SDPath = null;
		StorageManager sm = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		// 获取sdcard的路径：外置和内置
		String[] paths = null;
		try {
			paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0])
					.invoke(sm, new Object[]{});
		} catch (Exception e) {

			e.printStackTrace();
		}
		// 如果数组的长度大于一，则说明有一个以上储存空间，即有外置SD卡
		if (paths.length > 1) {
			// 外置SD卡通常是第二个
			SDPath = paths[1];
			File path = new File(SDPath);
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			//以String形式返回剩余空间（GB）
//			return Formatter.formatFileSize(context, blockSize
//					* availableBlocks);
			return blockSize* availableBlocks;
		}
		return 0;
	}

	//TODO
	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public static long getRomTotalSize(Context context) {
		String Path = "/mnt/sdcard";
		File path = null;
		//如果能获取到内置SD卡,则计算内置SD卡空间，因为LuPingDaShi在此空间创建
		if(getDefault(context)!=null){
			path = new File(Path);
		}else{
			 path = Environment.getDataDirectory();
		}
		
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		// return Formatter.formatFileSize(context, blockSize
		// * totalBlocks);
		return blockSize * totalBlocks;
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	public static long getRomAvailableSize(Context context) {
		String Path = "/mnt/sdcard";
		File path = null;
		//如果能获取到内置SD卡,则计算内置SD卡空间，因为LuPingDaShi在此空间创建
		if(getDefault(context)!=null){
			path = new File(Path);
		}else{ 
			 //获取默认机身内存
			 path = Environment.getDataDirectory();
		}
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		// 以GB方式返回
		// return Formatter.formatFileSize(context, blockSize
		// * availableBlocks);
		return blockSize * availableBlocks;
	}
}
