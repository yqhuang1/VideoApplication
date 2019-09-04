package com.fmscreenrecord.floatview;

/**
 * 浮窗展开
 * @author lin
 * Create：2014-07
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.activity.ScreenCapture50;
import com.fmscreenrecord.animation.FloatContentAnimation;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.RootUtils;
import com.li.videoapplication.fragment.ScreenShotFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FloatContentView extends RelativeLayout implements OnClickListener {

	public int mWidth;
	public int mHeight;
	public int contentViewWidth;
	public int contentViewHeight;
	public static Handler handlerFromFloatService;
	public static FloatViewManager manager;

	private LayoutInflater inflater;

	private ImageView btStart;

	// private ImageView btMic;
	private ImageView btBackHome;

	private ImageView btLaySC;

	SoundPool soundPool;
	private int preX;
	public static int preY;
	public static int preXFirst;
	public static int preYFirst;
	public static int notMoveCnt = 0;

	private int x;
	private int y;
	private boolean isMove;
	public static boolean openMic = false;

	private FrameLayout frameLayout;

	// private boolean isLayoutBackClick = false; // 是否识别为按下logo收回键
	/**
	 * 浮窗第一次启动标示 此变量为true时，启动浮窗时，浮窗状态会回到floatview1。
	 */
	public static boolean isFirstTimeUse = true;
	public RelativeLayout relativeLayout;
	int lastX;
	int lastY;
	int screenWidth = 0;
	int screenHeight = 0;
	View view;
	// 图片路径
	private static String picFileUrl;

	Bitmap bmp = null;

	private static FloatContentView thisView;

	public static Context mContext;
	public static Context mmContext;
	public ViewGroup.LayoutParams params;

	FloatContentAnimation floatContentAnimation;
	RecordVideo recrodVideo;
	public static List<FloatContentView> activityList = new ArrayList<FloatContentView>();

	public FloatContentView(Context context) {

		this(context, null);
	}

	public FloatContentView(Context context, AttributeSet attrs) {
		super(context, attrs);

		thisView = FloatContentView.this;
		mContext = context;

		// TODO
		view = LayoutInflater.from(mContext).inflate(
				MResource.getIdByName(mContext, "layout",
						"fm_float_content_view"), this);
		// 查找页面控件
		findViews(context);
		manager = FloatViewManager.getInstance(context);
		floatContentAnimation = new FloatContentAnimation(mContext, view,
				manager);
		inflater = LayoutInflater.from(context);

		inflater.inflate(MResource.getIdByName(context, "layout",
				"fm_float_content_view"), this);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		// 50作为浮窗跳动补偿
		screenHeight = dm.heightPixels - 50;

		params = findViewById(
				MResource.getIdByName(context, "id", "content_layout"))
				.getLayoutParams();
		mWidth = params.width;
		mHeight = params.height;

		// 相机声音准备
		soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(mContext,
				MResource.getIdByName(mContext, "raw", "kuaimen"), 1);

		handler.sendEmptyMessageDelayed(4, 1);
		ViewGroup.LayoutParams paramsContent = findViewById(
				MResource.getIdByName(context, "id", "content"))
				.getLayoutParams();
		contentViewWidth = paramsContent.width;
		contentViewHeight = paramsContent.height;

		// 重置之前的UI状态
		if (RecordVideo.isStart == true) {

			btStart.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(context, "drawable", "bt_stop_down")));
		} else {

			btStart.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(context, "drawable", "bt_start")));
		}
		recrodVideo = new RecordVideo(mContext);
		handlerFromFloatService = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 1) {
					// 展开浮窗详细框
					ShowContentView(screenWidth, FloatViewManager.layY1);
					relativeLayout.setVisibility(FrameLayout.VISIBLE);
				} else if (msg.what == 2) {
					// TODO
					ShowContentView(screenWidth, FloatViewManager.layY1);

					// 根据安卓系统版本不同分别进行视频合成
					if (MainActivity.SDKVesion < 19) {// 4.4以下
						recrodVideo.StopRecordForVesionOther();
						// TODO

					}

					else if (MainActivity.SDKVesion >= 19
							&& MainActivity.SDKVesion < 21) {// 4.4
						recrodVideo.StopRecordForVesion19();

					}
					btStart.setImageDrawable(getResources().getDrawable(
							MResource.getIdByName(mContext, "drawable",
									"bt_start")));
					manager.BackToView1();
				}

				else if (msg.what == 6) {

					manager.back();
					// File file = new File(picFileUrl);
					// root判断
					if (RootUtils.appRoot1()) {
						MinUtil.showToast(mContext, "截屏成功!");

						// 更新手机图库
						scanPhotos(picFileUrl, getContext());

						//刷新适配器
						ScreenShotFragment.screenShotHandle.sendEmptyMessage(2);

					} else {

						MinUtil.showToast(mContext, "截屏失败，请确保手机ROOT成功后重试!");

					}

				}
			}
		};

		// 控件监听事件
		setOnclickEvent();

		// 第一次启动不展开悬浮窗，避免错位问题
		if (ExApplication.firstExtandAnmin == false) {
			floatContentAnimation.extandAnmin();

		} else {
			ExApplication.firstExtandAnmin = false;

		}
		floatContentAnimation.setFloatContentViewLocation(mContext);
	}

	/** 查找页面控件 */
	private void findViews(Context context) {
		btStart = (ImageView) findViewById(MResource.getIdByName(context, "id",
				"bt_starts"));
		btBackHome = (ImageView) findViewById(MResource.getIdByName(context,
				"id", "bt_backHome"));
		frameLayout = (FrameLayout) findViewById(MResource.getIdByName(context,
				"id", "content_layout"));
		relativeLayout = (RelativeLayout) findViewById(MResource.getIdByName(
				context, "id", "content"));

		btLaySC = (ImageView) findViewById(MResource.getIdByName(context, "id",
				"float_layout_botton_SC"));

	}

	/** 控件监听事件 */
	private void setOnclickEvent() {
		btBackHome.setOnClickListener(this);
		frameLayout.setOnClickListener(this);
		btStart.setOnClickListener(this);
		btLaySC.setOnClickListener(this);

	}

	void ShowContentView(int x, int y) {

		int left = x;
		int top = y;
		int right = relativeLayout.getWidth() + left;
		int bottom = relativeLayout.getHeight() + top;
		if (left < 0) {
			left = 0;
			right = left + relativeLayout.getWidth();
		}
		if (right > screenWidth) {
			right = screenWidth;
			left = right - relativeLayout.getWidth();
		}
		if (top < 0) {
			top = 0;
			bottom = top + relativeLayout.getHeight();
		}
		if (bottom > screenHeight) {
			bottom = screenHeight;
			top = bottom - relativeLayout.getHeight();
		}

		relativeLayout.layout(left, top, right, bottom);

	}

	void ViewMove(int dx, int dy) {
		int left = relativeLayout.getLeft() + dx;
		int top = relativeLayout.getTop() + dy;
		int right = relativeLayout.getWidth() + left;
		int bottom = relativeLayout.getHeight() + top;
		if (left < 0) {
			left = 0;
			right = left + relativeLayout.getWidth();
		}
		if (right > screenWidth) {
			right = screenWidth;
			left = right - relativeLayout.getWidth();
		}
		if (top < 0) {
			top = 0;
			bottom = top + relativeLayout.getHeight();
		}
		if (bottom > screenHeight) {
			bottom = screenHeight;
			top = bottom - relativeLayout.getHeight();
		}
		relativeLayout.layout(left, top, right, bottom);
	}

	void ViewShow(int x, int y) {
		int left = x;
		int top = y;
		int right = x + relativeLayout.getWidth();
		if (right >= screenWidth) {
			right = screenWidth;
			left = right - relativeLayout.getWidth();
		}
		int bottom = y + relativeLayout.getHeight();

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) relativeLayout
				.getLayoutParams();
		params.setMargins(x, y, right, bottom);// 改变位置
		relativeLayout.setLayoutParams(params);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 4) {
				if (isFirstTimeUse) // && thisView != null &&
									// !isLayoutBackClick)
				{

					thisView.setVisibility(View.GONE);
					isFirstTimeUse = false;
					manager.back();

				}
			}

			else if (msg.what == 5) {
				manager.back();
			}
		}
	};

	public void onClick(View v) {
		if (v == btStart) {
			// TODO

			ShowContentView(screenWidth, FloatViewManager.layY1);
			recrodVideo.stardRecordVideo();

			btStart.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(mContext, "drawable", "bt_start")));
			manager.BackToView2();

		} else if (v == btLaySC) {// 截屏layout处理事件

			if (MainActivity.SDKVesion < 21) {
				// 隐藏浮窗
				thisView.setVisibility(GONE);
				// 播放截屏音效
				soundPool.play(1, 1, 1, 0, 0, 1);
				// 开启线程进行截屏
				try {

					MyThread myTHread = MyThread.getMyThread();
					myTHread.start();

				} catch (Exception e) {

					e.printStackTrace();
				}

			} else {

				// 本浮窗隐藏
				thisView.setVisibility(GONE);
				// 将本FloatContentView加入list
				activityList.add(thisView);
				// 打开5.0截屏activity
				Intent intent2 = new Intent();
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.setClass(mContext, ScreenCapture50.class);
				mContext.startActivity(intent2);
			}
			// ExApplication.isScPictur=false;
			MinUtil.upUmenEventValue(mContext, "截屏次数", "bt_screen");
		} else if (v == frameLayout) {

			floatContentAnimation.extandAnmin();
		} else if (v == btBackHome) { // 返回主页

			Intent intent2 = new Intent();
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.setClass(mContext, com.li.videoapplication.activity.MainActivity.class);

			mContext.startActivity(intent2);

			manager.back();
		}
	}

	// 刷新图库
	public static void scanPhotos(String filePath, Context context) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(new File(filePath));
		intent.setData(uri);
		context.sendBroadcast(intent);
	}

	/**
	 * 新开线程进行截屏
	 * 
	 * @author WYX
	 * 
	 */
	public static class MyThread extends Thread {
		private static MyThread myThread = null;

		private MyThread() {
		}

		public static MyThread getMyThread() {
			if (myThread != null) {
				myThread.interrupt();

			}
			myThread = new MyThread();
			return myThread;

		}

		public void run() {
			try {

				// 发出消息通知界面更新，显示浮窗

				handlerFromFloatService.sendEmptyMessage(6);

				// 如果superlulu下没有Picture文件夹，则创建
				File destDir = new File("/mnt/sdcard/LuPingDaShi/Picture/");
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				// 进行截屏
				picFileUrl = "/mnt/sdcard/LuPingDaShi/Picture/Pic"
						+ new SimpleDateFormat("yyyyMMddHHmmsss")
								.format(new Date()) + ".png";
				String command = "screencap -p " + picFileUrl;
				MainActivity.runCommand(command);
				// 更新手机图库(暂时关闭)
				// File file = new File(picFileUrl);
				// try {
				//
				// MediaStore.Images.Media.insertImage(
				// mContext.getContentResolver(), picFileUrl,
				// file.getName(), null);
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				//
				// }
				// mContext.sendBroadcast(new Intent(
				// Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
				// .fromFile(file)));
			} catch (Exception e) {

				e.printStackTrace();

			}

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {

			preX = (int) event.getRawX();
			preY = (int) event.getRawY();
			preXFirst = preX;
			preYFirst = preY;
			isMove = false;
			break;
		}
		case MotionEvent.ACTION_UP: {

			break;
		}
		case MotionEvent.ACTION_MOVE: {

			x = (int) event.getRawX();
			y = (int) event.getRawY();

			// 消除手指点击误差
			if (((x - preXFirst) > -15 && (x - preXFirst) < 15)
					&& ((y - preYFirst) > -15 && (y - preYFirst) < 15)) {
				// notMoveCnt++;
				// if(notMoveCnt>=2)
				// { notMoveCnt = 0;
				isMove = false;
				// }
			} else {
				manager.move(FloatContentView.this, x - preX, y - preY);
				isMove = true;
			}

			preX = x;
			preY = y;
			break;
		}

		}

		return super.onTouchEvent(event);

	}

	private OnButtonClickListener onButtonClickListener;

	public interface OnButtonClickListener {
		void onButtonClick(View v, int id);
	}

	public void setOnButtonClickListener(
			OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}
}