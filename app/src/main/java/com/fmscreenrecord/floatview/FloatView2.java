package com.fmscreenrecord.floatview;

/**
 * 计时浮窗
 * @author lin
 * Create：2014-07
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.activity.ScreenRecord50;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.frontcamera.CameraView;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.fmscreenrecord.utils.StoreDirUtil;

import java.util.Timer;
import java.util.TimerTask;

public class FloatView2 extends LinearLayout {
	public static LinearLayout tvInfo;
	private FloatViewManager manager;
	private static RelativeLayout floatViewRL2;
	public static TextView timeText;
	public int mWidth;
	public int mHeight;
	private int preX;
	private int preY;
	public static int preXFirst;
	public static int preYFirst;
	private int x;
	private int y;
	private boolean isMove;
	private static Context mContext = null;
	public static int min = 0, sec = 0;

	static Timer timer = new Timer(); // 定时器
	static TimerTask task; // 定时任务

	public static WindowManager mWindowManager;
	public static WindowManager.LayoutParams mLayoutParams;

	private static SharedPreferences sharedPreferences;
	public static View waterView;
	public static boolean stopCnt = false;
	public static float alpha = 1;
	// 录屏时剩余空间阀值控制
	private static boolean percent10 = false;
	private static boolean percent5 = false;
	/** 防止点击两下导致崩溃 */
	public static boolean doubleclick = false;

	// logo帧动画
	public static AnimationDrawable animationDrawable;
	// logo
	private ImageView recordTV;

	private View view;

	/**
	 * 获得机身内存总大小
	 */
	static float allSize;
	/**
	 * 获得机身剩余内存
	 */
	static float residueSize;
	/**
	 * 总内存与剩余内存比率
	 */
	static float rate;

	public FloatView2(Context context) {
		super(context);

		mContext = context;
		LayoutInflater.from(context).inflate(
				MResource.getIdByName(context, "layout", "fm_float_view_2"),
				this);
		tvInfo = (LinearLayout) findViewById(MResource.getIdByName(context,
				"id", "layout_view_2"));

		floatViewRL2 = (RelativeLayout) findViewById(MResource.getIdByName(
				context, "id", "float_view_RL2"));
		timeText = (TextView) findViewById(MResource.getIdByName(context, "id",
				"time_txt"));
		recordTV = (ImageView) findViewById(MResource.getIdByName(context,
				"id", "record_anmin_tv"));
		timeText.setText("  0 :00");

		mWidth = tvInfo.getLayoutParams().width;
		mHeight = tvInfo.getLayoutParams().height;

		manager = FloatViewManager.getInstance(context);

		animationDrawable = (AnimationDrawable) recordTV.getBackground();

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		// 定时器任务
		task = new TimerTask() {
			@Override
			public void run() {
				handler_timedisplay.sendEmptyMessage(1);

			}
		};
		timer.schedule(task, 1000, 1000); // 启动定时器,时间为1s
		boolean showtouch = SharedPreferencesUtils.getPreferenceboolean(
				context, "show_float_touch");
		if (showtouch == true) {
			android.provider.Settings.System.putInt(
					context.getContentResolver(), "show_touches", 1);
		} else {
			android.provider.Settings.System.putInt(
					context.getContentResolver(), "show_touches", 0);
		}
		if (ExApplication.pauseRecVideo) {
			recordTV.setImageDrawable(getResources()
					.getDrawable(
							MResource.getIdByName(context, "drawable",
									"fm_pause_menu")));
		} else {
			recordTV.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(context, "anim", "recordanmin")));
		}
	}

	// 用于显示录制时间
	public static Handler handler_timedisplay = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(android.os.Message msg) {

			if (RecordVideo.isStart) {
				MainActivity.videolong++;

				if (msg.what == 1) {

					// 播放帧动画
					animationDrawable.start();
					// 判断是否刚开始录屏
					if (ExApplication.recStartTime == 0) {
						// showWater();
						// 记录开始录屏时的时间毫秒值
						ExApplication.recStartTime = System.currentTimeMillis();

						// 获取视频清晰度
						String videoMode = ExApplication.videoQuality;
						// 如果无法准确获取到视频清晰度,就通过SharedPreferences获取
						if (videoMode.equals("-1")) {

							if (MainActivity.SDKVesion >= 19) {
								videoMode = sharedPreferences.getString(
										"quality_of_video", ExApplication.HQuality);
							} else {
								videoMode = sharedPreferences.getString(
										"quality_of_video", ExApplication.SQuality);
							}

						}
						// 将视频清晰度传至友盟
						MinUtil.upUmenEventValue(mContext, videoMode,
								"bt_quality");

					}

					msg.what = 0;
					if (FloatViewService.times >= 2) {
						sec = (FloatViewService.times - 2) % 60;
						min = (FloatViewService.times - 2) / 60;

						// 以00:00的形式显示时间
						if (min < 10) {

							if (sec < 10) // 服务启动时会加一，故减一补偿
							{
								timeText.setText("  " + min + " :" + "0"
										+ (sec));
								// 录屏开始三秒之后悬浮窗逐渐透明
								if (sec > 3) {
									if (alpha >= 0.5) {
										alpha = (float) (alpha - 0.1);
										tvInfo.setAlpha(alpha);
									}
								}

							} else {
								timeText.setText("  " + min + " :" + (sec));
							}
						} else {
							// 重设背景图片以适应时间文本宽度
							timeText.setBackgroundDrawable(mContext
									.getResources().getDrawable(
											MResource.getIdByName(mContext,
													"drawable",
													"float_recording_bg")));
							if (sec < 10) {
								timeText.setText(" " + min + " : " + "0"
										+ (sec));
							} else {
								timeText.setText(" " + min + " : " + (sec));
							}
						}
						// 每10秒检查一次机身内存剩余
						if (sec % 10 == 0) {

							allSize = StoreDirUtil.getRomTotalSize(mContext);
							residueSize = StoreDirUtil
									.getRomAvailableSize(mContext);
							rate = residueSize / allSize;

							if (rate < 0.10 && rate > 0.05
									&& percent10 == false) {
								MinUtil.showToast(mContext,
										"您的手机内存剩余空间低于10%，建议停止录屏");
								percent10 = true;
							}
							if (rate < 0.05 && percent5 == false) {
								MinUtil.showToast(mContext,
										"您的手机内存剩余空间低于5%，建议停止录屏");
								percent5 = true;
							}
						}
					}

				}
			}

			else {
				sec = 0;
				min = 0;
				stopCnt = false;
				timeText.setText("  0 :00");
				percent10 = false;
				percent5 = false;
				// 关闭触摸显示
				try {
					android.provider.Settings.System.putInt(
							mContext.getContentResolver(), "show_touches", 0);
				} catch (Exception e) {
					e.fillInStackTrace();
				}

			}

			/*
			 * if(LoadingActivity.SDKVesion >= 19) { sendEmptyMessageDelayed(1,
			 * 1000); }
			 */

		}

	};

	// 用于调置页面上控制是否开启浮窗
	public static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mContext != null) {
				if (msg.what == 3) {
					msg.what = 0;
					HideFloatView();
				} else if (msg.what == 4) {
					msg.what = 0;
					showFloatView();
				}
			}
		}
	};

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
			// 停止移动时，将控件当前坐标重新赋值给全局变量
			int[] location = new int[2];
			tvInfo.getLocationOnScreen(location);
			ExApplication.moveX = location[0];
			// 50像素作为跳动补偿
			ExApplication.moveY = location[1] - 50;

			if (!isMove && doubleclick == false) {// 如果是单击，发送消息准备停止录屏
				if (MainActivity.SDKVesion >= 19) {

					manager.showContentForView2(FloatView2.this);
					FloatViewService.handlerFromFloat.sendEmptyMessage(3);

				} else {
					doubleclick = true;
					// 停止动画
					// 重新设置浮窗透明度
					alpha = 1;
					tvInfo.setAlpha(alpha);
					animationDrawable.stop();

					if (MainActivity.SDKVesion >= 21) {

						// 停止录屏，返回浮窗
						ScreenRecord50.stopRecord();

						ScreenRecord50.startRecoing = false;

					} else {

						FloatViewService.handlerFromFloat.sendEmptyMessage(4);

					}
					// 关闭触摸显示
					android.provider.Settings.System.putInt(
							mContext.getContentResolver(), "show_touches", 0);
					// 关闭画中画
					if (ExApplication.floatCameraClose == false) {
						CameraView.closeFloatView();

					}
				}

			}

			break;
		}
		case MotionEvent.ACTION_MOVE: {
			x = (int) event.getRawX();
			y = (int) event.getRawY();
			if (((x - preXFirst) > -15 && (x - preXFirst) < 15)
					&& ((y - preYFirst) > -15 && (y - preYFirst) < 15)) {
				isMove = false;
			} else {
				manager.move(this, x - preX, y - preY);
				isMove = true;
			}

			preX = x;
			preY = y;
			break;
		}

		}
		return super.onTouchEvent(event);
	}

	public static boolean HideFloatView() {
		floatViewRL2.setVisibility(RelativeLayout.INVISIBLE);
		return true;
	}

	public static boolean showFloatView() {
		floatViewRL2.setVisibility(RelativeLayout.VISIBLE);
		return true;
	}

}