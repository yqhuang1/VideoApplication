package com.fmscreenrecord.VideoList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExternalImageLoader {
	com.fmscreenrecord.VideoList.MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 绾跨▼姹�
	ExecutorService executorService;
	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
	public ExternalImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 褰撹繘鍏istview鏃堕粯璁ょ殑鍥剧墖锛屽彲鎹㈡垚浣犺嚜宸辩殑榛樿鍥剧墖
	//final int stub_id = R.drawable.ic_launcher;

	// 鏈�涓昏鐨勬柟娉�
	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		// 鍏堜粠鍐呭瓨缂撳瓨涓煡鎵�
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			// 鑻ユ病鏈夌殑璇濆垯寮�鍚柊绾跨▼鍔犺浇鍥剧墖
			//imageView.setImageResource(stub_id);
			queuePhoto(url, imageView);
		}
	}
	
	
	public void DisplayVideoTime(String dir,TextView textView)
	{
		mmr.setDataSource(dir);
		String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 鎾斁鏃堕暱鍗曚綅涓烘绉�
		int time = 0;
		time= Integer.parseInt(duration);
		
		long min = time /1000 / 60;
		long sec = time /1000 % 60;
		//textView.setText(min+":"+sec);
		if(min<10)
		{
			if(sec<10)
			{
				textView.setText("0"+min+":"+"0"+sec);
			}
			else
			{
				textView.setText("0"+min+":"+sec);
			}
		}
		else
		{
			if(sec<10)
			{
				textView.setText(min+":"+"0"+sec);
			}
			else
			{
				textView.setText(min+":"+sec);
			}
		}
	}
	

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);
		// 鍏堜粠鏂囦欢缂撳瓨涓煡鎵炬槸鍚︽湁
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;
		try {
			Bitmap bitmap = null;

			bitmap = ThumbnailUtils.createVideoThumbnail(url, Thumbnails.MINI_KIND);
			System.out.println("=========");
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 鐢ㄤ簬鍦║I绾跨▼涓洿鏂扮晫闈�
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null){
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
			//else
//				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
}