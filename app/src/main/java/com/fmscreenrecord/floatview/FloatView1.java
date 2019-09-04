package com.fmscreenrecord.floatview;

/**
 * 浮窗1
 * @author lin
 * Create：2014-07
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.activity.PackageInfoGridview;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.utils.MResource;

import java.util.Timer;
import java.util.TimerTask;

public class FloatView1 extends LinearLayout {
	private ImageView tvInfo;
	private FloatViewManager manager;
	private static RelativeLayout floatViewRL;
	public int mWidth;
	public int mHeight;

	private int preX;
	private int preY;
	public static int preXFirst;
	public static int preYFirst;
	public static int notMoveCnt = 0;

	private int x;
	private int y;
	private boolean isMove;
	private static Context mContext = null;

	public static boolean showFloatView = true;
	private final Timer timer = new Timer(); // 定时器
	private TimerTask task;

	public FloatView1(Context context) {
		this(context, null);
	}

	public FloatView1(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		LayoutInflater.from(context).inflate(
				MResource.getIdByName(context, "layout", "fm_float_view_1"),
				this);
		tvInfo = (ImageView) findViewById(MResource.getIdByName(context, "id",
				"background1"));

		floatViewRL = (RelativeLayout) findViewById(MResource.getIdByName(
				context, "id", "float_view_RL"));

		mWidth = tvInfo.getLayoutParams().width;
		mHeight = tvInfo.getLayoutParams().height;

		manager = FloatViewManager.getInstance(context);

		handler.sendEmptyMessage(0);

		// 定时器任务
		task = new TimerTask() {
			@Override
			public void run() {

				if (MainActivity.isInMain
						|| PackageInfoGridview.isInPackageInfo) {
					handler.sendEmptyMessage(3);

				} else {
					handler.sendEmptyMessage(4);

				}
			}
		};

		timer.schedule(task, 1000, 1000); // 启动定时器
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		manager.move(FloatView1.this, width, height / 2);

	}

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
			} else {
				if (msg.what == 3) {
					msg.what = 0;

					showFloatView = false;
				} else if (msg.what == 4) {
					msg.what = 0;
					showFloatView = true;
				}
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			tvInfo.setImageDrawable(getResources().getDrawable(
					MResource.getIdByName(mContext, "drawable",
							"fm_float_ico_down")));
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
			// 50作为浮窗跳动补偿
			ExApplication.moveY = location[1] - 50;
			// 重新设置图片
			tvInfo.setImageDrawable(getResources()
					.getDrawable(
							MResource.getIdByName(mContext, "drawable",
									"fm_float_ico")));
			if (!isMove) {
				// 将isFirstTimeUse置为false，悬浮窗直接跳转到floatcontentview，而不是floatview1；
				FloatContentView.isFirstTimeUse = false;
				manager.showContent(FloatView1.this);
				FloatViewService.handlerFromFloat.sendEmptyMessage(1);
			}
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
				manager.move(FloatView1.this, x - preX, y - preY);
				isMove = true;
			}

			preX = x;
			preY = y;
			break;
		}

		}
		return super.onTouchEvent(event);

	}

	/**
	 * 隐藏悬浮窗
	 * 
	 * @return
	 */
	public static boolean HideFloatView() {

		floatViewRL.setVisibility(RelativeLayout.GONE);
		return true;
	}

	/**
	 * 显示悬浮窗
	 * 
	 * @return
	 */
	public static boolean showFloatView() {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		// 是否开启无浮窗模式
		if (sp.getBoolean("show_float_view", false)) {
			floatViewRL.setVisibility(RelativeLayout.GONE);
		} else {
			floatViewRL.setVisibility(RelativeLayout.VISIBLE);
		}

		return true;
	}

}