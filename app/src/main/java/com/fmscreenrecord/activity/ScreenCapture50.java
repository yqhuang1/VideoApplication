package com.fmscreenrecord.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.fmscreenrecord.floatview.FloatContentView;
import com.fmscreenrecord.floatview.FloatViewManager;
import com.fmscreenrecord.utils.MResource;
import com.li.videoapplication.fragment.ScreenShotFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 5.0截屏
 * 
 * @author WYX
 * 
 */

@SuppressLint("NewApi")
public class ScreenCapture50 extends Activity {
	private MediaProjectionManager mProjectionManager;
	private Handler mHandler;
	Handler handler;
	private static MediaProjection MEDIA_PROJECTION;

	private ImageReader mImageReader;
	private static String STORE_DIRECTORY;
	private int mWidth;
	private int mHeight;
	private int resultCode;
	private Intent data;
	private static int IMAGES_PRODUCED;
	private static final int REQUEST_CODE = 100;
	private static final String TAG = com.fmscreenrecord.activity.ScreenCapture50.class.getName();
	private static FloatViewManager manager;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"fm_screenrecord50_activity"));
		mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mHandler = new Handler();
				Looper.loop();
			}
		}.start();
		mContext = com.fmscreenrecord.activity.ScreenCapture50.this;
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {

				manager = FloatViewManager
						.getInstance(FloatContentView.activityList.get(0)
								.getContext());
				manager.back();

				MEDIA_PROJECTION = mProjectionManager.getMediaProjection(
						resultCode, data);

				if (MEDIA_PROJECTION != null) {
					STORE_DIRECTORY = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/LuPingDaShi/Picture/";
					File storeDirectory = new File(STORE_DIRECTORY);
					// 判断文件是否存在
					if (!storeDirectory.exists()) {
						boolean success = storeDirectory.mkdirs();
						if (!success) {
							Log.e(TAG,
									"failed to create file storage directory.");
							return;
						}
					} else {
						Toast.makeText(mContext, "截屏成功！", Toast.LENGTH_SHORT)
								.show();
						//刷新适配器
						ScreenShotFragment.screenShotHandle.sendEmptyMessage(2);
					}

					DisplayMetrics metrics = getResources().getDisplayMetrics();
					int density = metrics.densityDpi;
					int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
							| DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					mWidth = size.x;
					mHeight = size.y;

					mImageReader = ImageReader.newInstance(mWidth, mHeight,
							PixelFormat.RGBA_8888, 2);
					MEDIA_PROJECTION.createVirtualDisplay("screencap", mWidth,
							mHeight, density, flags, mImageReader.getSurface(),
							null, null);

					mImageReader.setOnImageAvailableListener(
							new ImageAvailableListener(), null);
					finish();
					stopProjection();
				}
			};
		};
		startProjection();
	}

	private class ImageAvailableListener implements
			ImageReader.OnImageAvailableListener {
		@Override
		public void onImageAvailable(ImageReader reader) {

			new Thread() {
				Image image = null;
				FileOutputStream fos = null;
				Bitmap bitmap = null;

				public void run() {
					try {
						image = mImageReader.acquireLatestImage();
						if (image != null) {
							Image.Plane[] planes = image.getPlanes();
							ByteBuffer buffer = planes[0].getBuffer();
							// 两个相邻像素之间距离字节单位 4
							int pixelStride = planes[0].getPixelStride();
							// 2944
							int rowStride = planes[0].getRowStride();
							// 64
							int rowPadding = rowStride - pixelStride * mWidth;

							int offset = 0;
							bitmap = Bitmap.createBitmap(mWidth, mHeight,
									Bitmap.Config.ARGB_8888);
							for (int i = 0; i < mHeight; ++i) {
								for (int j = 0; j < mWidth; ++j) {
									int pixel = 0;
									pixel |= (buffer.get(offset) & 0xff) << 16; // R
									pixel |= (buffer.get(offset + 1) & 0xff) << 8; // G
									pixel |= (buffer.get(offset + 2) & 0xff); // B
									pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
									bitmap.setPixel(j, i, pixel);
									offset += pixelStride;
								}
								offset += rowPadding;
							}

							fos = new FileOutputStream(
									STORE_DIRECTORY
											+ "Pic"
											+ new SimpleDateFormat(
													"yyyyMMddHHmmsss")
													.format(new Date())
											+ ".png");
							bitmap.compress(CompressFormat.PNG, 100, fos);

							IMAGES_PRODUCED++;

						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}

						if (bitmap != null) {
							bitmap.recycle();
						}

						if (image != null) {
							image.close();
						}
					}
				};
			}.start();

		}
	}

	private void stopProjection() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (MEDIA_PROJECTION != null)
					MEDIA_PROJECTION.stop();

			}
		});

		onDestroy();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE && resultCode == -1) {
			this.resultCode = resultCode;
			this.data = data;
			moveTaskToBack(true);

			handler.sendEmptyMessageDelayed(0, 1000);
			// TODO

		} else {

			manager = FloatViewManager
					.getInstance(FloatContentView.activityList.get(0)
							.getContext());
			manager.back();
			Toast.makeText(mContext, "截屏失败!", Toast.LENGTH_SHORT).show();
			this.finish();
			stopProjection();
		}

	}

	private void startProjection() {

		startActivityForResult(mProjectionManager.createScreenCaptureIntent(),
				REQUEST_CODE);

	}
}
