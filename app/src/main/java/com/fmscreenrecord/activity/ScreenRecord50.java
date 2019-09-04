package com.fmscreenrecord.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.encoder.MediaAudioEncoder;
import com.fmscreenrecord.encoder.MediaEncoder;
import com.fmscreenrecord.encoder.MediaMuxerWrapper;
import com.fmscreenrecord.encoder.MediaScreenEncoder;
import com.fmscreenrecord.floatview.FloatContentView;
import com.fmscreenrecord.floatview.FloatView2;
import com.fmscreenrecord.floatview.FloatViewManager;
import com.fmscreenrecord.record.Recorder44;
import com.fmscreenrecord.record.Settings;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.utils.FileUtils;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.RecordVideo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 5.0录制activity
 * 
 * @author WYX
 * 
 */
public class ScreenRecord50 extends Activity {
	private static final int REQUEST_CODE = 1;
	private MediaProjectionManager mMediaProjectionManager;

	public static boolean startRecoing = false;
	final int width = 1280;
	final int height = 720;
	public static FloatViewManager manager;
	private static Context mContext;
	private static SharedPreferences sp;

	public static MediaMuxerWrapper mMuxer;
	private static File file;
	/***
	 * 是否通过摇晃启动此页面
	 */
	boolean shake = false;
	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 判断文件
				int fileSize = (int) FileUtils.getFileSize(file);
				if (fileSize > 512) {
					Message mMsg = new Message();
					// 视频文件路径
					mMsg.obj = file.getPath();
					mMsg.what = 2;
					// 发送消息准备吐司通知用户录制成功

					FloatViewService.mHandler.sendMessage(mMsg);

				} else {
					FloatViewService.mHandler.sendEmptyMessage(3);
				}
				break;

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"fm_screenrecord50_activity"));
		if (ExApplication.mConfiguration == true) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		mContext = ScreenRecord50.this;
		shake = getIntent().getBooleanExtra("shake", false);
		mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

	}

	@Override
	// 将开始录屏提示放在onResume中，确保每一次打开activity都能调用到
	protected void onResume() {
		super.onResume();
		startRecord();
	}

	public void startRecord() {

		if (startRecoing == false) {
			// 弹出谷歌提示框提示用户是否允许录屏
			Intent captureIntent = mMediaProjectionManager
					.createScreenCaptureIntent();
			startActivityForResult(captureIntent, REQUEST_CODE);

		}

	}

	public static void stopRecord() {
		if (mMuxer != null) {

			mMuxer.stopRecording();

			mMuxer = null;

		}
		handler.sendEmptyMessageDelayed(1, 1500);

		RecordVideo.isStop = true;
		RecordVideo.isStart = false;
		RecordVideo.useFloatView = false;
		RecordVideo.isRecordering = false;
		startRecoing = false;
		// MainActivity.endRecord = true;
		FloatView2.min = 0; // 时间清零
		FloatView2.sec = 0;
		// 如果开启了悬浮窗录制模式，返回悬浮窗1
		if (!sp.getBoolean("show_float_view", false)) {
			manager.BackToView1();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 开始录屏返回-1，取消录屏返回0
		if (resultCode == -1) {

			manager = FloatViewManager.getInstance(getApplicationContext());
			manager.showContent();
			manager.removeFirst();
			// FloatViewService.handlerFromFloat.sendEmptyMessage(1);
			RecordVideo.isStart = true;
			RecordVideo.isStop = false;
			RecordVideo.isRecordering = true;
			RecordVideo.recordering = true;
			MainActivity.endRecord = false;
			FloatContentView.manager.BackToView2();
			startRecoing = true;

			try {

				file = new File(Environment.getExternalStorageDirectory()
						+ "/LuPingDaShi/", new SimpleDateFormat(
						"yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".mp4");
				String videoFileName = file.getAbsolutePath();

				mMuxer = new MediaMuxerWrapper(videoFileName);
			} catch (IOException e) {

				e.printStackTrace();
			}

			MediaProjection mediaProjection = mMediaProjectionManager
					.getMediaProjection(resultCode, data);

			if (mediaProjection == null) {
				Log.e("@@", "media projection is null");
				return;
			}

			// 获取SP中是否已经勾选录制声音
			sp = PreferenceManager.getDefaultSharedPreferences(mContext);
			// 如果是摇晃录屏，则根据当前屏幕方向来录屏
			if (shake) {

				setvideoQualityForShake(ExApplication.videoQuality);
			} else {// 根据当前配置来录屏
				setvideoQuality(ExApplication.videoQuality);

			}

			if (sp.getBoolean("record_sound", true)) {
				Recorder44.isRecordAudio = true;

			} else {
				Recorder44.isRecordAudio = false;
			}

			// 录制声音
			if (Recorder44.isRecordAudio) {
				new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
			}

			new MediaScreenEncoder(mMuxer, mMediaEncoderListener,
					Settings.width, Settings.height, 1, mediaProjection,
					Settings.BITRATE);

			try {
				mMuxer.prepare();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mMuxer.startRecording();
			moveTaskToBack(true);
			this.finish();
		} else {
			// 弹回第一个浮窗
			manager = FloatViewManager.getInstance(getApplicationContext());
			FloatContentView.manager.BackToView1();
			RecordVideo.isStop = true;
			RecordVideo.isStart = false;
			this.finish();
		}
	}

	/**
	 * callback methods from encoder
	 */
	private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
		@Override
		public void onPrepared(final MediaEncoder encoder) {
		}

		@Override
		public void onStopped(final MediaEncoder encoder) {

		}
	};

	// 根据清晰度设置分辨率
	private void setvideoQuality(String videoQuality) {
		if (videoQuality.equals(ExApplication.HQuality)) {
			Settings.BITRATE = 2000000;
			// 根据sp配置或者横竖屏设定分辨率
			if (sp.getBoolean("horizontalRecord", true)) {// 横屏
				Settings.width = 1280;
				Settings.height = 720;

			} else {// 竖屏
				Settings.width = 720;
				Settings.height = 1280;

			}

		} else if (videoQuality.equals(ExApplication.SQuality)) {
			Settings.BITRATE = 1200000;
			// 根据sp配置或者横竖屏设定分辨率
			if (sp.getBoolean("horizontalRecord", true)) {
				Settings.width = 720;
				Settings.height = 480;

			} else {
				Settings.width = 480;
				Settings.height = 720;

			}

		}
		/**
		 * 此配置已经作废
		 * 
		 */
		else if (videoQuality.equals("流畅")) {
			if (MainActivity.getConfiguration()) {
				Settings.width = 640;
				Settings.height = 360;

			} else {
				Settings.width = 360;
				Settings.height = 640;

			}

		}

	}

	// 根据清晰度设置分辨率
	private void setvideoQualityForShake(String videoQuality) {
		if (videoQuality.equals(ExApplication.HQuality)) {
			Settings.BITRATE = 2000000;
			// 根据横竖屏来判断录屏方向
			if (MainActivity.getConfiguration()) {// 横屏
				Settings.width = 1280;
				Settings.height = 720;

			} else {// 竖屏
				Settings.width = 720;
				Settings.height = 1280;

			}

		} else if (videoQuality.equals(ExApplication.SQuality)) {
			Settings.BITRATE = 1200000;
			// 根据横竖屏来判断录屏方向
			if (MainActivity.getConfiguration()) {
				Settings.width = 720;
				Settings.height = 480;

			} else {
				Settings.width = 480;
				Settings.height = 720;

			}

		}
		/**
		 * 此配置已经作废
		 * 
		 */
		else if (videoQuality.equals("流畅")) {
			if (MainActivity.getConfiguration()) {
				Settings.width = 640;
				Settings.height = 360;

			} else {
				Settings.width = 360;
				Settings.height = 640;

			}

		}

	}

	protected void onDestroy() {
		super.onDestroy();
		// if (mRecorder != null) {
		// mRecorder.quit();
		// mRecorder = null;
		// }
	}
}
