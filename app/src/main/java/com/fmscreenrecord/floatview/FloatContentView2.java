package com.fmscreenrecord.floatview;

/**
 * 浮窗展开
 * @author lin
 * Create：2014-07
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import com.fmscreenrecord.activity.ScreenRecord50;
import com.fmscreenrecord.animation.FloatContentViewAnimation;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.frontcamera.CameraView;
import com.fmscreenrecord.record.Recorder44;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.RootUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FloatContentView2 extends RelativeLayout implements
		OnClickListener {

	public int mWidth;
	public int mHeight;
	public int contentViewWidth;
	public int contentViewHeight;
	public static Handler handlerFromFloatService;
	public static FloatViewManager manager;

	private LayoutInflater inflater;

	/** 停止录屏 */
	private ImageView btStop;

	/** 返回首页 */
	private ImageView btBackHome;
	/** 截屏 */
	private ImageView btLaySC;
	/** 暂停录屏 */
	private ImageView btPause;

	public static boolean isStart = false; // 是否开始
	public static boolean isStop = true; // 是否停止
	SoundPool soundPool;
	private int preX;
	public static int preY;
	public static int preXFirst;
	public static int preYFirst;
	public static int notMoveCnt = 0;
	public static boolean recordering = false;
	private int x;
	private int y;
	private boolean isMove;
	public static boolean openMic = false;

	public static boolean useFloatView = false;
	private FrameLayout frameLayout;
	public static boolean isRecordering = false;

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

	// 视频模式（高清，标准）
	String videoMode;

	private static FloatContentView2 thisView;

	public static Context mContext;
	public ViewGroup.LayoutParams params;

	FloatContentViewAnimation floatContentViewAnimation;
	public static List<FloatContentView2> activityList = new ArrayList<FloatContentView2>();

	private SharedPreferences sp;

	public FloatContentView2(Context context) {

		this(context, null);
	}

	public FloatContentView2(Context context, AttributeSet attrs) {
		super(context, attrs);

		thisView = FloatContentView2.this;
		mContext = context;

		// TODO
		view = LayoutInflater.from(mContext).inflate(
				MResource.getIdByName(mContext, "layout",
						"fm_float_content_view2"), this);
		// 查找页面控件
		findViews(context);
		manager = FloatViewManager.getInstance(context);
		floatContentViewAnimation = new FloatContentViewAnimation(mContext,
				view, manager);
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

		ViewGroup.LayoutParams paramsContent = findViewById(
				MResource.getIdByName(context, "id", "content"))
				.getLayoutParams();
		contentViewWidth = paramsContent.width;
		contentViewHeight = paramsContent.height;

		// 重置之前的UI状态
		if (ExApplication.pauseRecVideo) {

			btPause.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(context, "drawable", "bt_goon")));
		} else {

			btPause.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(context, "drawable", "bt_pause")));
		}

		sp = PreferenceManager.getDefaultSharedPreferences(mContext);

		handlerFromFloatService = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 1) {
					// 展开浮窗详细框
					floatContentViewAnimation.extandAnmin();

				} else if (msg.what == 2) {

				}

				else if (msg.what == 6) {

					manager.back2();
					// File file = new File(picFileUrl);
					// root判断
					if (RootUtils.appRoot1()) {
						MinUtil.showToast(getContext(), "截屏成功!");

						// 更新手机图库
						scanPhotos(picFileUrl, getContext());

					} else {

						MinUtil.showToast(mContext, "截屏失败，请确保手机ROOT成功后重试!");

					}

				}
			}
		};

		// 控件监听事件
		setOnclickEvent();

		floatContentViewAnimation.extandAnmin();
		// 第一次启动不展开悬浮窗，避免错位问题
		if (ExApplication.firstExtandAnmin == false) {
			floatContentViewAnimation.extandAnmin();

		} else {
			ExApplication.firstExtandAnmin = false;

		}
		floatContentViewAnimation.setFloatContentViewLocation(mContext);

	}

	/** 查找页面控件 */
	private void findViews(Context context) {
		btStop = (ImageView) findViewById(MResource.getIdByName(context, "id",
				"bt_stop"));
		btPause = (ImageView) findViewById(MResource.getIdByName(context, "id",
				"bt_pause"));
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
		btStop.setOnClickListener(this);
		btLaySC.setOnClickListener(this);
		btPause.setOnClickListener(this);

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
				if (isFirstTimeUse) {

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
		if (v == btStop) {

			// 重新设置浮窗透明度
			FloatView2.alpha = 1;
			FloatView2.tvInfo.setAlpha(FloatView2.alpha);
			FloatView2.animationDrawable.stop();
			ExApplication.pauseRecVideo = false;
			if (MainActivity.SDKVesion >= 21) {

				// 停止录屏，返回浮窗
				ScreenRecord50.stopRecord();

				ScreenRecord50.startRecoing = false;

			} else {

				FloatViewService.handlerFromFloat.sendEmptyMessage(4);
				manager.back2();

			}
			// 关闭触摸显示
			android.provider.Settings.System.putInt(
					mContext.getContentResolver(), "show_touches", 0);
			// 关闭画中画
			if (ExApplication.floatCameraClose == false) {
				CameraView.closeFloatView();

			}

		} else if (v == btLaySC)

		{// 截屏layout处理事件

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

				if (RecordVideo.isStart == false) {
					// 本浮窗隐藏
					thisView.setVisibility(GONE);
					// 将本FloatContentView加入list
					activityList.add(thisView);
					// 打开5.0截屏activity
					Intent intent2 = new Intent();
					intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2.setClass(mContext, ScreenCapture50.class);
					mContext.startActivity(intent2);
				} else {
					MinUtil.showToast(mContext, "正在录屏中，请录屏后再截屏");
					floatContentViewAnimation.extandAnmin();
				}
			}

			MinUtil.upUmenEventValue(mContext, "截屏次数", "bt_screen");
		} else if (v == frameLayout)

		{

			floatContentViewAnimation.extandAnmin();
		} else if (v == btBackHome)

		{ // 返回主页

			Intent intent2 = new Intent();
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.setClass(mContext, com.li.videoapplication.activity.MainActivity.class);

			mContext.startActivity(intent2);

			manager.back2();
		} else if (v == btPause) {
			if (ExApplication.pauseRecVideo) {
				MinUtil.upUmenEventValue(mContext, "继续录屏次数", "bt_videoRecord");
				ExApplication.pauseRecVideo = false;
				if (MainActivity.SDKVesion >= 21) {
					if (ScreenRecord50.mMuxer != null) {
						ScreenRecord50.mMuxer.restartRecording();
					}
				} else {
					Recorder44.RestartRecordVideo(getContext());
				}

			} else {
				MinUtil.upUmenEventValue(mContext, "暂停录屏次数", "bt_videoRecord");
				ExApplication.pauseRecVideo = true;
				MinUtil.showToast(mContext, "录制暂停");
				if (MainActivity.SDKVesion >= 21) {
					if (ScreenRecord50.mMuxer != null) {
						ScreenRecord50.mMuxer.pauseRecording();
					}
				} else {
					Recorder44.PauseRecordVideo(getContext());
				}

			}

			manager.back2();
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
				manager.move(FloatContentView2.this, x - preX, y - preY);
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