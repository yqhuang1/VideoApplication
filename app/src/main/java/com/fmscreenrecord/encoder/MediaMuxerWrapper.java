package com.fmscreenrecord.encoder;

/**
 * 封装混合器实体
 * 2015-09-12
 * LIN
 */

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

@SuppressLint("NewApi")
public class MediaMuxerWrapper {
	private static final boolean DEBUG = false; // TODO set false on release
	private static final String TAG = "MediaMuxerWrapper";

	private static final String DIR_NAME = "AVRecSample";
	private static final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

	private String mOutputPath;
	private final MediaMuxer mMediaMuxer; // API >= 18
	private int mEncoderCount, mStatredCount;
	private boolean mIsStarted;
	private MediaEncoder mVideoEncoder, mAudioEncoder;

	static long ptsUsecDec = 0; // 时间戳的缩减量
	static long ptsUsecPause = 0; // 暂停时的时间戳
	static long ptsUsecRestart = 0;// 重启时的时间戳

	/**
	 * Constructor
	 * 
	 * @param ext
	 *            extension of output file
	 * @throws IOException
	 */
	@SuppressLint("NewApi")
	public MediaMuxerWrapper(String videoFileName) throws IOException {

		mMediaMuxer = new MediaMuxer(videoFileName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
		mEncoderCount = mStatredCount = 0;
		mIsStarted = false;
	}

	public String getOutputPath() {
		return mOutputPath;
	}

	public void prepare() throws IOException {
		if (mVideoEncoder != null)
			mVideoEncoder.prepare();
		if (mAudioEncoder != null)
			mAudioEncoder.prepare();
	}

	public void startRecording() {
		if (mVideoEncoder != null)
			mVideoEncoder.startRecording();
		if (mAudioEncoder != null)
			mAudioEncoder.startRecording();

		ptsUsecDec = 0; // 时间戳的缩减量
		ptsUsecPause = 0; // 暂停时的时间戳
		ptsUsecRestart = 0;// 重启时的时间戳

	}

	public void stopRecording() {
		if (mVideoEncoder != null)
			mVideoEncoder.stopRecording();
		mVideoEncoder = null;

		if (mAudioEncoder != null)
			mAudioEncoder.stopRecording();
		mAudioEncoder = null;

	}

	public void pauseRecording() {
		if (mVideoEncoder != null)
			mVideoEncoder.pauseRecording();
		if (mAudioEncoder != null)
			mAudioEncoder.pauseRecording();
		ptsUsecPause = getPTSUs();


	}

	public void restartRecording() {
		if (mVideoEncoder != null)
			mVideoEncoder.restartRecording();
		if (mAudioEncoder != null)
			mAudioEncoder.restartRecording();

		ptsUsecRestart = getPTSUs() - 1000000;// 错开1秒
	

		ptsUsecDec = ptsUsecDec + (ptsUsecRestart - ptsUsecPause); // 原有值加上增量，为所要缩减的时间量，在多次暂停启动时用到

	}

	protected long getPTSUs() {
		long result = System.nanoTime() / 1000L;
		return result;
	}

	public synchronized boolean isStarted() {
		return mIsStarted;
	}

	// **********************************************************************
	// **********************************************************************
	/**
	 * assign encoder to this calss. this is called from encoder.
	 * 
	 * @param encoder
	 *            instance of MediaVideoEncoder or MediaAudioEncoder
	 */
	/* package */void addEncoder(final MediaEncoder encoder) {
		if (encoder instanceof MediaScreenEncoder) {
			if (mVideoEncoder != null)
				throw new IllegalArgumentException("Video encoder already added.");
			mVideoEncoder = encoder;
		} else if (encoder instanceof MediaAudioEncoder) {
			if (mAudioEncoder != null)
				throw new IllegalArgumentException("Video encoder already added.");
			mAudioEncoder = encoder;
		} else
			throw new IllegalArgumentException("unsupported encoder");
		mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
	}

	/**
	 * request start recording from encoder
	 * 
	 * @return true when muxer is ready to write
	 */
	/* package */@SuppressLint("NewApi")
	synchronized boolean start() {
		if (DEBUG)
			Log.v(TAG, "start:");
		mStatredCount++;
		if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {
			mMediaMuxer.start();
			mIsStarted = true;
			notifyAll();
			if (DEBUG)
				Log.v(TAG, "MediaMuxer started:");
		}
		return mIsStarted;
	}

	/**
	 * request stop recording from encoder when encoder received EOS
	 */
	/* package */@SuppressLint("NewApi")
	synchronized void stop() {
		if (DEBUG)
			Log.v(TAG, "stop:mStatredCount=" + mStatredCount);
		mStatredCount--;
		if ((mEncoderCount > 0) && (mStatredCount <= 0)) {
			mMediaMuxer.stop();
			mMediaMuxer.release();
			mIsStarted = false;
			if (DEBUG)
				Log.v(TAG, "MediaMuxer stopped:");
		}
	}

	/**
	 * assign encoder to muxer
	 * 
	 * @param format
	 * @return minus value indicate error
	 */
	/* package */synchronized int addTrack(final MediaFormat format) {
		if (mIsStarted)
			throw new IllegalStateException("muxer already started");
		final int trackIx = mMediaMuxer.addTrack(format);

		return trackIx;
	}

	/**
	 * write encoded data to muxer
	 * 
	 * @param trackIndex
	 * @param byteBuf
	 * @param bufferInfo
	 */
	/* package */synchronized void writeSampleData(final int trackIndex, final ByteBuffer byteBuf,
			final MediaCodec.BufferInfo bufferInfo) {
		if (mStatredCount > 0)
			mMediaMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
	}

	// **********************************************************************
	// **********************************************************************
	/**
	 * generate output file
	 * 
	 * @param type
	 *            Environment.DIRECTORY_MOVIES / Environment.DIRECTORY_DCIM etc.
	 * @param ext
	 *            .mp4(.m4a for audio) or .png
	 * @return return null when this app has no writing permission to external
	 *         storage.
	 */
	public static final File getCaptureFile(final String type, final String ext) {
		final File dir = new File(Environment.getExternalStoragePublicDirectory(type), DIR_NAME);
		Log.d(TAG, "path=" + dir.toString());
		dir.mkdirs();
		if (dir.canWrite()) {
			return new File(dir, getDateTimeString() + ext);
		}
		return null;
	}

	/**
	 * get current date and time as String
	 * 
	 * @return
	 */
	private static final String getDateTimeString() {
		final GregorianCalendar now = new GregorianCalendar();
		return mDateTimeFormat.format(now.getTime());
	}
}
