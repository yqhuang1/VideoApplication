package com.li.videoapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.utils.MResource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 对ImageLoer类进行封装
 * @author WYX
 *
 */
public class MImageLoader {
	// 异步加载图片
	public ImageLoader loader;
	public DisplayImageOptions options;

	public MImageLoader(Context mContext) {

		// 初始化图片下载(采用开源框架Image-Loader)
		ExApplication.initImageLoader(mContext);
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(
						MResource.getIdByName(mContext, "drawable",
								"fm_play_bg"))
				// 正在加载
				.showImageForEmptyUri(
						MResource.getIdByName(mContext, "drawable",
								"fm_play_bg"))
				// 空图片
				.showImageOnFail(
						MResource.getIdByName(mContext, "drawable",
								"fm_play_bg"))
				// 错误图片
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

}
