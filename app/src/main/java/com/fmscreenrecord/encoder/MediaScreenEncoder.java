package com.fmscreenrecord.encoder;

/**
 * 屏幕录制类，继承于重新封装的MediaEncoder
 * 2015-09-12
 * LIN
 */
import java.io.IOException;

import android.annotation.SuppressLint;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

@SuppressLint("NewApi")
public class MediaScreenEncoder extends MediaEncoder {
	private static final boolean DEBUG = false; // TODO set false on release
	private static final String TAG = "ScreenRecorder";
	private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video
															// Coding
	private static final int FRAME_RATE = 30; // 30 fps
	private static final int IFRAME_INTERVAL = 10; // 10 seconds between
													// I-frames
	private static final int TIMEOUT_US = 10000;
	private int BITRATE = 2000000;
	private int mWidth;
	private int mHeight;
	private int mDpi;
	// private MediaCodec mEncoder;
	private Surface mSurface;
	MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
	private VirtualDisplay mVirtualDisplay;
	private MediaProjection mMediaProjection;

	private VideoThread mVideoThread = null;

	public MediaScreenEncoder(MediaMuxerWrapper muxer,
			MediaEncoderListener listener, final int width, final int height,
			final int dpi, MediaProjection mp, int bitrate) {
		super(muxer, listener);

		mDpi = dpi;
		mMediaProjection = mp;
		mWidth = width;
		mHeight = height;
		BITRATE = bitrate;
	}

	@Override
	void prepare() throws IOException {
		mTrackIndex = -1;
		mMuxerStarted = mIsEOS = false;
		// TODO Auto-generated method stub
		MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth,
				mHeight);
		format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
				MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
		format.setInteger(MediaFormat.KEY_BIT_RATE, BITRATE);
		format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
		format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

		mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
		mMediaCodec.configure(format, null, null,
				MediaCodec.CONFIGURE_FLAG_ENCODE);
		mSurface = mMediaCodec.createInputSurface();

		mMediaCodec.start();

		mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG
				+ "-display", mWidth, mHeight, 1,
				DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mSurface, null,
				null);

		if (mListener != null) {
			try {
				mListener.onPrepared(this);
			} catch (final Exception e) {
				Log.e(TAG, "prepare:", e);
			}
		}
	}

	@Override
	protected void release() {

		super.release();
		if (mSurface != null) {
			mSurface.release();
			mSurface = null;
		}

		if (mVirtualDisplay != null) {
			mVirtualDisplay.release();
		}
		if (mMediaProjection != null) {
			mMediaProjection.stop();
		}
		mVideoThread = null;

	}

	@Override
	protected void startRecording() {
		super.startRecording();
		if (mVideoThread == null) {
			mVideoThread = new VideoThread();
			mVideoThread.start();
		}
		// frameAvailableSoon();
	}

	@Override
	void pauseRecording() {
		// TODO Auto-generated method stub
		super.pauseRecording();
	}

	@Override
	void restartRecording() {
		// TODO Auto-generated method stub
		super.restartRecording();
	}

	private class VideoThread extends Thread {
		@Override
		public void run() {
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			while (mIsCapturing && !mRequestStop && !mIsEOS) {
				frameAvailableSoon();
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			frameAvailableSoon();
			if (DEBUG)
				Log.v(TAG, "AudioThread:finished");
		}
	}
}