package com.fmscreenrecord.videoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.fmscreenrecord.utils.MResource;
import com.li.videoapplication.activity.VideoEditorActivity;

public class VideoSliceSeekBar extends ImageView {
	private static final int SELECT_THUMB_LEFT = 1;
	private static final int SELECT_THUMB_RIGHT = 2;
	private static final int SELECT_THUMB_NON = 0;
	// 秒数
	static long seconds;
	/** 左边滑块 */
	private Bitmap thumbSliceLeft;
	/** 右边边滑块 */
	private Bitmap thumbSliceRight;
	/** 视频播放进度块 */
	private Bitmap thumbCurrentVideoPosition;

	private int progressMinDiff = 10; // percentage
	/** 进度条背景颜色 */
	private int progressColor;
	private int txtColor;
	/** 选中的视频进度颜色 */
	private int secondaryProgressColor;
	/** 进度条高度 */
	private int progressHalfHeight = 50;
	private int thumbPadding;
	private long maxValue = 100;

	private int minValue = 10;

	private int progressMinDiffPixels;
	/** 滑块left方向起始坐标 */
	private int thumbSliceLeftX;
	/** 滑块right方式起始坐标 */
	int thumbSliceRightX;
	int thumbCurrentVideoPositionX;
	private long thumbSliceLeftValue, thumbSliceRightValue;
	private long thumbSliceAllValue;
	// private int thumbSliceY;
	private int thumbSliceRightY;
	private int thumbSliceLeftY;
	private int thumbCurrentVideoPositionY;
	private Paint paint = new Paint();

	private Paint paintThumb = new Paint();
	private int selectedThumb;
	private int thumbSliceHalfWidth, thumbCurrentVideoPositionHalfWidth;
	private SeekBarChangeListener scl;

	private int progressTop;
	private int progressBottom;

	private boolean blocked;
	/** 视频是否处于播放 */
	private boolean isVideoStatusDisplay;

	private int limitMovecnt = 0;// 极限处往极限方向位移次数

	private int lastTouchEventX = 0;// 第一次触屏jf位置

	private Context mContext;

	public VideoSliceSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		thumbSliceLeft = BitmapFactory.decodeResource(getResources(), MResource
				.getIdByName(mContext, "drawable", "fm_thumb_slice_left"));
		thumbSliceRight = BitmapFactory.decodeResource(getResources(),
				MResource.getIdByName(mContext, "drawable",
						"fm_thumb_slice_right"));
		thumbCurrentVideoPosition = BitmapFactory.decodeResource(
				getResources(),
				MResource.getIdByName(mContext, "drawable", "fm_leftthumb"));
		thumbPadding = getResources().getDimensionPixelOffset(
				MResource.getIdByName(mContext, "dimen",
						"fm_video_editor_margin"));

		progressColor = getResources().getColor(
				MResource.getIdByName(mContext, "color", "gold"));
		txtColor = getResources()
				.getColor(
						MResource.getIdByName(mContext, "color",
								"black_75_transparent"));

		secondaryProgressColor = getResources().getColor(
				MResource.getIdByName(mContext, "color", "red"));

	}

	public VideoSliceSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// 左滑块
		thumbSliceLeft = BitmapFactory.decodeResource(getResources(), MResource
				.getIdByName(mContext, "drawable", "fm_thumb_slice_left"));
		// 右滑块
		thumbSliceRight = BitmapFactory.decodeResource(getResources(),
				MResource.getIdByName(mContext, "drawable",
						"fm_thumb_slice_right"));
		// 播放进度滑块
		thumbCurrentVideoPosition = BitmapFactory.decodeResource(
				getResources(),
				MResource.getIdByName(mContext, "drawable", "fm_leftthumb"));
		thumbPadding = getResources().getDimensionPixelOffset(
				MResource.getIdByName(mContext, "dimen",
						"fm_video_editor_margin"));

		progressColor = getResources().getColor(
				MResource.getIdByName(mContext, "color", "black"));
		txtColor = getResources()
				.getColor(
						MResource.getIdByName(mContext, "color",
								"black_75_transparent"));

		secondaryProgressColor = getResources().getColor(
				MResource.getIdByName(mContext, "color", "white"));

	}

	public VideoSliceSeekBar(Context context) {
		super(context);
		mContext = context;
		thumbSliceLeft = BitmapFactory.decodeResource(getResources(), MResource
				.getIdByName(mContext, "drawable", "fm_thumb_slice_left"));
		thumbSliceRight = BitmapFactory.decodeResource(getResources(),
				MResource.getIdByName(mContext, "drawable",
						"fm_thumb_slice_right"));
		thumbCurrentVideoPosition = BitmapFactory.decodeResource(
				getResources(),
				MResource.getIdByName(mContext, "drawable", "fm_leftthumb"));
		thumbPadding = getResources().getDimensionPixelOffset(
				MResource.getIdByName(mContext, "dimen",
						"fm_video_editor_margin"));

		progressColor = getResources().getColor(
				MResource.getIdByName(mContext, "color", "gray"));
		txtColor = getResources()
				.getColor(
						MResource.getIdByName(mContext, "color",
								"black_75_transparent"));

		secondaryProgressColor = getResources().getColor(
				MResource.getIdByName(mContext, "color", "green2"));
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		init();
	}

	private void init() {

		if (thumbSliceLeft.getHeight() > getHeight()) {
			getLayoutParams().height = thumbSliceLeft.getHeight();
		}

		thumbSliceLeftY = (getHeight() / 2 + 4);
		thumbSliceRightY = (getHeight() / 2) - (thumbSliceLeft.getHeight() + 4);

		thumbCurrentVideoPositionY = (getHeight() / 2)
				- (thumbCurrentVideoPosition.getHeight() / 2);

		thumbSliceHalfWidth = thumbSliceLeft.getWidth() / 2;
		thumbCurrentVideoPositionHalfWidth = thumbCurrentVideoPosition
				.getWidth() / 2;
		if (thumbSliceLeftX == 0 || thumbSliceRightX == 0) {
			thumbSliceLeftX = thumbPadding;
			thumbSliceRightX = getWidth() - thumbPadding;
		}
		progressMinDiffPixels = calculateCorrds(progressMinDiff) - 2
				* thumbPadding;
		progressTop = getHeight() / 2 - progressHalfHeight;
		progressBottom = getHeight() / 2 + progressHalfHeight;
		invalidate();
		thumbSliceAllValue = thumbSliceRightValue;
	}

	public void setSeekBarChangeListener(SeekBarChangeListener scl) {
		this.scl = scl;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect;
		// 抗锯齿
		paint.setAntiAlias(true);
		// 绘制进度条两端被舍弃视频进度的颜色
		paint.setColor(progressColor);
		//
		paint.setStyle(Paint.Style.FILL);
		// 透明度
		paint.setAlpha(150);
		rect = new Rect(thumbPadding, progressTop, thumbSliceLeftX,
				progressBottom);
		canvas.drawRect(rect, paint);
		rect = new Rect(thumbSliceRightX, progressTop, getWidth()
				- thumbPadding, progressBottom);
		canvas.drawRect(rect, paint);

		// 绘制选中的视频进度颜色
		paint.setColor(secondaryProgressColor);
		// 空心
		paint.setStyle(Paint.Style.STROKE);
		// 边框粗细
		paint.setStrokeWidth(8);

		rect = new Rect(thumbSliceLeftX, progressTop, thumbSliceRightX,
				progressBottom);
		canvas.drawRect(rect, paint);
		

		if (!blocked) {
			// 绘制左右游标
			// canvas.drawBitmap(thumbSliceLeft, thumbSliceLeftX
			// - thumbSliceHalfWidth, thumbSliceLeftY, paintThumb);
			// canvas.drawBitmap(thumbSliceRight, thumbSliceRightX
			// - thumbSliceHalfWidth, thumbSliceRightY, paintThumb);
			paint.setStrokeWidth(12);
			rect = new Rect(thumbSliceLeftX - thumbSliceHalfWidth,
					progressTop + 20, thumbSliceLeftX - 2, progressBottom - 20);
			canvas.drawRect(rect, paint);

			rect = new Rect(thumbSliceRightX + 2, progressTop + 20,
					thumbSliceRightX + 20, progressBottom - 20);
			canvas.drawRect(rect, paint);

			long[] text = new long[2];
			text[0] = thumbSliceLeftValue;
			text[1] = thumbSliceRightValue;
			Message msg = new Message();
			msg.obj = text;
			msg.what = 1;
			// 发送消息更新textview
			VideoEditorActivity.handler.sendMessage(msg);
			// TODO
		}
		if (isVideoStatusDisplay) {
			// 生成并绘制视频播放进度块
			canvas.drawBitmap(thumbCurrentVideoPosition,
					thumbCurrentVideoPositionX
							- thumbCurrentVideoPositionHalfWidth,
					thumbCurrentVideoPositionY, paintThumb);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!blocked) {
			int mx = (int) event.getX();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//判断按压点位置，以决定绘制左右哪一个滑块
				if (mx > thumbSliceLeftX && mx > thumbSliceRightX) {
					selectedThumb = SELECT_THUMB_RIGHT;

				} else if (mx < thumbSliceLeftX) {
					selectedThumb = SELECT_THUMB_LEFT;

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mx > thumbSliceLeftX && mx > thumbSliceRightX) {

					selectedThumb = SELECT_THUMB_RIGHT;

				} else if (mx < thumbSliceLeftX) {

					selectedThumb = SELECT_THUMB_LEFT;

				}

				// 控制左右两个滑块的最小距离
				if ((event.getX() <= thumbSliceLeftX + progressMinDiffPixels && selectedThumb == SELECT_THUMB_RIGHT)
						|| (event.getX() >= thumbSliceRightX
								- progressMinDiffPixels && selectedThumb == SELECT_THUMB_LEFT)) {

					selectedThumb = SELECT_THUMB_NON;
				}

				if (selectedThumb == SELECT_THUMB_LEFT) {
					thumbSliceLeftX = mx;
				} else if (selectedThumb == SELECT_THUMB_RIGHT) {
					thumbSliceRightX = mx;
				}
				break;
			case MotionEvent.ACTION_UP:
				selectedThumb = SELECT_THUMB_NON;
				break;
			}
			notifySeekBarValueChanged();
		}
		return true;
	}

	private void notifySeekBarValueChanged() {
		if (thumbSliceLeftX < thumbPadding)
			thumbSliceLeftX = thumbPadding;

		if (thumbSliceRightX < thumbPadding)
			thumbSliceRightX = thumbPadding;

		if (thumbSliceLeftX > getWidth() - thumbPadding)
			thumbSliceLeftX = getWidth() - thumbPadding;

		if (thumbSliceRightX > getWidth() - thumbPadding)
			thumbSliceRightX = getWidth() - thumbPadding;

		invalidate();
		if (scl != null) {
			calculateThumbValue();
			scl.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue);
		}
	}

	private void calculateThumbValue() {
		thumbSliceLeftValue = (maxValue * (thumbSliceLeftX - thumbPadding))
				/ (getWidth() - 2 * thumbPadding);

		thumbSliceRightValue = (maxValue * (thumbSliceRightX - thumbPadding))
				/ (getWidth() - 2 * thumbPadding);

	}

	private int calculateCorrds(int progress) {
		return (int) (((getWidth() - 2d * thumbPadding) / maxValue) * progress);
	}

	private int calculateCorrdsRight(long progress) {
		return (int) (((getWidth() - 2d * thumbPadding) / maxValue) * progress)
				+ thumbPadding;
	}

	public void setLeftProgress(int progress) {
		if (progress < thumbSliceRightValue - progressMinDiff) {
			thumbSliceLeftX = calculateCorrds(progress);
		}
		notifySeekBarValueChanged();
	}

	public void setRightProgress(int progress) {
		if (progress > thumbSliceLeftValue + progressMinDiff) {
			thumbSliceRightX = calculateCorrdsRight(progress);
		}
		notifySeekBarValueChanged();
	}

	public long getLeftProgress() {
		return thumbSliceLeftValue;
	}

	public long getRightProgress() {
		return thumbSliceRightValue;
	}

	public void setProgress(int leftProgress, int rightProgress) {
		if (rightProgress - leftProgress > progressMinDiff) {
			thumbSliceLeftX = calculateCorrds(leftProgress);
			thumbSliceRightX = calculateCorrds(rightProgress);
		}
		notifySeekBarValueChanged();
	}

	public void videoPlayingProgress(long progress) {
		isVideoStatusDisplay = true;
		thumbCurrentVideoPositionX = calculateCorrdsRight(progress);
		invalidate();
	}

	public void removeVideoStatusThumb() {
		isVideoStatusDisplay = false;
		invalidate();
	}

	public void setSliceBlocked(boolean isBLock) {
		blocked = isBLock;
		invalidate();
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void setProgressMinDiff(int progressMinDiff) {
		this.progressMinDiff = progressMinDiff;
		progressMinDiffPixels = calculateCorrds(progressMinDiff);
	}

	public void setProgressHeight(int progressHeight) {
		this.progressHalfHeight = progressHalfHeight / 2;
		invalidate();
	}

	public void setProgressColor(int progressColor) {
		this.progressColor = progressColor;
		invalidate();
	}

	public void setSecondaryProgressColor(int secondaryProgressColor) {
		this.secondaryProgressColor = secondaryProgressColor;
		invalidate();
	}

	public void setThumbSliceLeft(Bitmap thumbSlice) {
		this.thumbSliceLeft = thumbSlice;
		init();
	}

	public void setThumbSliceRight(Bitmap thumbSlice) {
		this.thumbSliceRight = thumbSlice;
		init();
	}

	public void setThumbCurrentVideoPosition(Bitmap thumbCurrentVideoPosition) {
		this.thumbCurrentVideoPosition = thumbCurrentVideoPosition;
		init();
	}

	public void setThumbPadding(int thumbPadding) {
		this.thumbPadding = thumbPadding;
		invalidate();
	}

	public interface SeekBarChangeListener {
		void SeekBarValueChanged(long leftThumb, long rightThumb);
	}
}