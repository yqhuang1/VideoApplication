package com.fmscreenrecord.frontcamera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class CameraViewManager {

	private static CameraViewManager manager;

	private CameraViewManager() {
	}

	private static WindowManager winManager;
	private static Context context;
	private LayoutParams params;
	private LayoutParams paramsContent;

	/**
	 * 画中画界面
	 */
	private CameraView floatContentView;

	// 屏幕尺寸
	private static int displayWidth;
	private static int displayHeight;

	/**
	 * @param context
	 *            ApplicationContext
	 * @return
	 */
	public static synchronized CameraViewManager getInstance(Context context) {
		if (manager == null) {
			CameraViewManager.context = context;
			winManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			displayWidth = winManager.getDefaultDisplay().getWidth();
			displayHeight = winManager.getDefaultDisplay().getHeight();

			manager = new CameraViewManager();
		}
		return manager;
	}

	/**
	 * 显示浮窗
	 */
	public void show() {

	}

	/**
	 * 显示画中画界面
	 */
	public void showContent() {
		CameraView cameraView = getContentView();
		if (cameraView.getParent() == null) {
			winManager.addView(cameraView, paramsContent);

		}

	}

	// 移动
	public void move(View view, int delatX, int deltaY) {

		if (view == floatContentView) {
			paramsContent.x += delatX;
			paramsContent.y += deltaY;
			winManager.updateViewLayout(view, paramsContent);
		}

	}

	/**
	 * 关闭
	 */
	public void dismiss() {
		if (floatContentView != null) {
			winManager.removeView(floatContentView);
		}

		floatContentView = null;
	}

	public void back() {

		winManager.removeView(floatContentView);
		floatContentView = null;
	}

	/**
	 * 更新界面
	 * 
	 * @return
	 */
	public boolean isUpdate() {
		if (floatContentView == null) {
			return false;
		}
		return true;
	}

	// 获取画中画界面参数
	public CameraView getContentView() {
		if (floatContentView == null) {
			floatContentView = new CameraView(context);
		}
		if (paramsContent == null) {
			paramsContent = new LayoutParams();
			paramsContent.type = LayoutParams.TYPE_PHONE;
			paramsContent.format = PixelFormat.RGBA_8888;
			paramsContent.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
			paramsContent.gravity = Gravity.LEFT | Gravity.TOP;

			paramsContent.width = floatContentView.mWidth;
			paramsContent.height = floatContentView.mHeight;

			paramsContent.x = (displayWidth - floatContentView.mWidth);// / 2;
			paramsContent.y = 0;// (displayHeight - floatContentView.mHeight) /
								// 2;
		}
		return floatContentView;
	}
}
