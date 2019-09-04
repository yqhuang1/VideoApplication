package com.fmscreenrecord.record;

import android.util.Log;

public class NativeProcessRunner implements
        com.fmscreenrecord.record.RecorderProcess.OnStateChangeListener {
    private static final String TAG = "RecorderProcess";
    private String executable;
    com.fmscreenrecord.record.RecorderProcess process;

    private OnReadyListener mOnReadyListener;

    public void setOnReadyListener(OnReadyListener mOnReadyListener) {
        this.mOnReadyListener = mOnReadyListener;
    }

    public NativeProcessRunner() {
    }

    public void destroy() {
        if ((this.process == null) || (this.process.isStopped())) {
            return;
        }
        if (this.process.isRecording()) {
            this.process.stopRecording();
        } else {
            this.process.destroy();
        }
    }

    public void initialize() {
        if ((this.process == null) || (this.process.isStopped())) {
            this.process = new com.fmscreenrecord.record.RecorderProcess(this.executable, this);
            new Thread(this.process).start();
        }
    }

    public void onStateChange(com.fmscreenrecord.record.RecorderProcess process,
                              com.fmscreenrecord.record.RecorderProcess.ProcessState oldState,
                              com.fmscreenrecord.record.RecorderProcess.ProcessState newState, int paramInt,
                              float paramFloat) {
        if (process != this.process) {
            Log.w(TAG, "received state update from old process");
            return;
        }
        Log.w(TAG, "received new state " + newState);
        if (newState == com.fmscreenrecord.record.RecorderProcess.ProcessState.READY
                && null != mOnReadyListener) {
            mOnReadyListener.onReady();
        } else if (newState == com.fmscreenrecord.record.RecorderProcess.ProcessState.FINISHED
                && null != mOnReadyListener) {
            mOnReadyListener.onFinished();
        }
    }

    public void setExecutable(String paramString) {
        this.executable = paramString;
    }

    public void start(String outFile) {
        this.process.startRecording(outFile);
    }

    public void stop() {
        this.process.stopRecording();
    }

    public static interface OnReadyListener {
        void onReady();

        void onFinished();
    }
}