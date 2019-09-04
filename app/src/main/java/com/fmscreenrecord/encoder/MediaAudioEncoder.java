package com.fmscreenrecord.encoder;

/**
 * 5.0录制核心相关类
 */

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

@SuppressLint("NewApi")
public class MediaAudioEncoder extends MediaEncoder {
	private static final boolean DEBUG = false; // TODO set false on release
	private static final String TAG = "MediaAudioEncoder";

	private static final String MIME_TYPE = "audio/mp4a-latm";
	private static final int SAMPLE_RATE = 44100; // 44.1[KHz] is only setting
													// guaranteed to be
													// available on all devices.
	private static final int BIT_RATE = 64000;
	public static final int SAMPLES_PER_FRAME = 1024; // AAC,
														// bytes/frame/channel
	public static final int FRAMES_PER_BUFFER = 25; // AAC, frame/buffer/sec

	private AudioThread mAudioThread = null;

	public MediaAudioEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener) {
		super(muxer, listener);
	}

	@SuppressLint("NewApi")
	@Override
	protected void prepare() throws IOException {

		mTrackIndex = -1;
		mMuxerStarted = mIsEOS = false;
		// prepare MediaCodec for AAC encoding of audio data from inernal mic.
		final MediaCodecInfo audioCodecInfo = selectAudioCodec(MIME_TYPE);
		if (audioCodecInfo == null) {

			return;
		}

		final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
		audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
		audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
		audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
		audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
		// audioFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE,
		// inputFile.length());
		// audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );

		mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
		mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		mMediaCodec.start();

		if (mListener != null) {
			try {
				mListener.onPrepared(this);
			} catch (final Exception e) {
				Log.e(TAG, "prepare:", e);
			}
		}
	}

	@Override
	protected void startRecording() {
		super.startRecording();
		// create and execute audio capturing thread using internal mic
		if (mAudioThread == null) {
			mAudioThread = new AudioThread();
			mAudioThread.start();
		}
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

	@Override
	protected void release() {
		mAudioThread = null;
		super.release();
	}

	/**
	 * Thread to capture audio data from internal mic as uncompressed 16bit PCM
	 * data and write them to the MediaCodec encoder
	 */
	private class AudioThread extends Thread {
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			try {
				final int min_buffer_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
				if (buffer_size < min_buffer_size)
					buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

				final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
						AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);

				try {
					if (mIsCapturing && (audioRecord.getState() == AudioRecord.STATE_INITIALIZED)) {
						if (DEBUG)
							Log.v(TAG, "AudioThread:start audio recording");
						final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
						int readBytes;
						audioRecord.startRecording();
						try {
							while (mIsCapturing && !mRequestStop && !mIsEOS) {
								// read audio data from internal mic
								buf.clear();
								readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
								if (readBytes > 0) {
									// set audio data to encoder
									buf.position(readBytes);
									buf.flip();
									encode(buf, readBytes, getPTSUs());
									frameAvailableSoon();
								}
							}
							frameAvailableSoon();
						} finally {
							audioRecord.stop();
						}
					}
				} finally {
					audioRecord.release();
				}
			} catch (final Exception e) {
				Log.e(TAG, "AudioThread#run", e);
			}
			if (DEBUG)
				Log.v(TAG, "AudioThread:finished");
		}
	}

	/**
	 * select the first codec that match a specific MIME type
	 * 
	 * @param mimeType
	 * @return
	 */
	@SuppressLint("NewApi")
	private static final MediaCodecInfo selectAudioCodec(final String mimeType) {

		MediaCodecInfo result = null;
		// get the list of available codecs
		final int numCodecs = MediaCodecList.getCodecCount();
		LOOP: for (int i = 0; i < numCodecs; i++) {
			final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
			if (!codecInfo.isEncoder()) { // skipp decoder
				continue;
			}
			final String[] types = codecInfo.getSupportedTypes();
			for (int j = 0; j < types.length; j++) {
				if (DEBUG)
					Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
				if (types[j].equalsIgnoreCase(mimeType)) {
					if (result == null) {
						result = codecInfo;
						break LOOP;
					}
				}
			}
		}
		return result;
	}
}
