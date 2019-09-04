package com.fmscreenrecord.record;

import android.util.Log;

import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

class RecorderProcess implements Runnable
{
    private static final String TAG = "ScreenRECTrail";
    private Timeout configureTimeout;
    private boolean destroying;
    private String executable;
    private int exitValue;
    private Integer exitValueOverride;
    private volatile boolean forceKilled;
    private Float fps;
    private OnStateChangeListener onStateChangeListener;
    private Process process;
    private Timeout startTimeout;
    private volatile ProcessState state;
    private OutputStream stdin;
    private InputStream stdout;
    private Timeout stopTimeout;

    RecorderProcess(String executable, OnStateChangeListener listener) {
        this.state = ProcessState.NEW;
        this.exitValue = -1;
        this.fps = -1.0F;
        this.destroying = false;
        this.forceKilled = false;
        this.configureTimeout = new Timeout(5000, "recording_error", "configure_timeout", 301);
        this.startTimeout = new Timeout(10000, "recording_error", "start_timeout", 302);
        this.stopTimeout = new Timeout(10000, "stopping_error", "stop_timeout", 303);
        this.executable = executable;
        this.onStateChangeListener = listener;
    }

    private void checkStatus(String errType, String errName, int errCode) {
        if ((this.forceKilled) || (this.destroying) || (errName == null)) return;
        if (!errType.equals(errName)) {
            Log.e(TAG, "Incorrect status received: " + errName);
            this.exitValueOverride = errCode;
            forceKill();
        } else {
            Log.e(TAG, errName);
        }
    }

    private void forceKill() {
        Log.d(TAG, "forceKill");
        if (this.forceKilled) {
            Log.d(TAG, "Already force killed");
        } else {
            this.forceKilled = true;
            killProcess(this.executable);
        }
    }

    private void killMediaServer() {
        Log.d(TAG, "restartMediaServer");
        killProcess("/system/bin/mediaserver");
    }

    private void killProcess(String paramString) {
        Log.d(TAG, "kill process " + paramString);
        RootTools.killProcess(paramString);
    }

    private boolean mediaServerRelatedError() {
        if (this.destroying) {
            if (Settings.encoder.getValue() >= 0) {
                return true;
            }
        }
        return false;
    }

    private void parseFps(String fps) {
        Log.i(TAG, "paramString : " + fps);
        if ((fps != null) && (fps.startsWith("fps ")) && (fps.length() > 4)) {
            try {
                this.fps = Float.parseFloat(fps.substring(4));
                if ((!this.destroying) && (this.fps < 0.0F)) {
                    Log.e(TAG, "Incorrect fps value received \"" + fps + "\"");
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void runCommand(String command) {
        try {
            Log.e(TAG, "Run Command : " + command);
            String str = command + "\n";
            this.stdin.write(str.getBytes());
            this.stdin.flush();
        } catch (IOException ex) {
            Log.e(TAG, "Error running command");
        }
    }

    private void setErrorState() {
        setState(ProcessState.ERROR);
        if (mediaServerRelatedError())
            killMediaServer();
    }

    private void setState(ProcessState newState) {
        Log.d(TAG, "setState " + newState);
        ProcessState oldState = this.state;
        this.state = newState;
        if (this.onStateChangeListener != null) {
            this.onStateChangeListener.onStateChange
                    (this, oldState, newState, this.exitValue, this.fps);
        }
    }

    private void stopProcess() {
        if (this.process != null) {
            try {
                this.stdin.close();
            } catch (IOException localIOException) {
                localIOException.printStackTrace();
            }
        }
    }

    void destroy() {
        if (this.process != null) {
            Log.d(TAG, "Destroying process");
            this.destroying = true;
            this.stopTimeout.start();
            stopProcess();
        }
    }

    boolean isRecording() {
        return this.state == ProcessState.RECORDING;
    }

    boolean isStopped() {
        return this.state == ProcessState.FINISHED || this.state == ProcessState.ERROR;
    }

    public void run() {
        setState(ProcessState.STARTING);
        BufferedReader reader;
        Log.d(TAG, "Starting native process");
        Runtime runtime = Runtime.getRuntime();
        try 
        {
            this.process = runtime.exec(new String[]{"su", "-c", this.executable});
            Log.d(TAG, "Native process started");
            this.stdin = this.process.getOutputStream();
            this.stdout = this.process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(this.stdout));
            checkStatus("ready", reader.readLine(), 304);
            setState(ProcessState.READY);
            checkStatus("configured", reader.readLine(), 305);
            this.configureTimeout.cancel();
            checkStatus("recording", reader.readLine(), 306);
            this.startTimeout.cancel();
            parseFps(reader.readLine());
            //////////////////////////////////
            Log.d(TAG, "Waiting for native process to exit");
            this.process.waitFor();
            Log.d(TAG, "Native process finished");
            this.stopTimeout.cancel();
            this.exitValue = this.process.exitValue();
        } 
        catch (IOException exc)
        {
            Log.e(TAG, "Error starting a new native process");
            forceKill();
            if (mediaServerRelatedError()) {
                killMediaServer();
            }
        } 
        catch (InterruptedException exc)
        {
            try 
            {
                this.exitValue = this.process.exitValue();
            } 
            catch (Exception e)
            {
                //
            }
            Log.e(TAG, "Native process interrupted");
            if (this.exitValueOverride != null) 
            {
                if (this.exitValue < 200) 
                {
                    this.exitValue = exitValueOverride;
                }
                setErrorState();
            } 
            else if (this.state == ProcessState.STOPPING) 
            {
                setState(ProcessState.FINISHED);
            } 
            else 
            {
                setErrorState();
            }
        } 
        finally 
        {
            if (this.exitValueOverride != null) 
            {
                if (this.exitValue < 200) 
                {
                    this.exitValue = this.exitValueOverride;
                }
                setState(ProcessState.ERROR);
                if (!this.destroying) 
                {
                    killMediaServer();
                }
                Log.d(TAG, "Return value: " + this.exitValue);
                this.exitValueOverride = 307;
                forceKill();
            } 
            else 
            {
                Log.d(TAG, "Success exit code : " + this.exitValue);
            }
            setState(ProcessState.FINISHED);
        }
    }

    void startRecording(String outFile) {
        Log.d(TAG, "startRecording " + outFile);
        if (this.state != ProcessState.READY) {
            Log.e(TAG, "Can't start recording in current state: " + this.state);
            return;
        }
        setState(ProcessState.RECORDING);
        this.configureTimeout.start();
        this.startTimeout.start();

        runCommand(outFile);
        runCommand(Settings.rotation.getCommand());
        runCommand(Settings.audioSource.getCommand());
        runCommand(String.valueOf(Settings.width));
        runCommand(String.valueOf(Settings.height));
        runCommand("0");
        runCommand("0");
        runCommand(String.valueOf(Settings.fps));
        runCommand(Settings.cpuGpu.getCommand());
        runCommand(Settings.getColorFixCommand());
        runCommand(Settings.videoQuality.getCommand());
        runCommand(Settings.audioQuality.getCommand());
        runCommand(Settings.encoder.getCommand());
        runCommand(String.valueOf("0"));
    }

    void stopRecording() {
        if (this.state != ProcessState.RECORDING) {
            Log.e(TAG, "Can't stop recording in current state: " + this.state);
        } else {
            Log.d(TAG, "stopRecording");
            setState(ProcessState.STOPPING);
            runCommand("stop");
            this.stopTimeout.start();
        }
    }

    static interface OnStateChangeListener {
        abstract void onStateChange(
                com.fmscreenrecord.record.RecorderProcess process,
                com.fmscreenrecord.record.RecorderProcess.ProcessState newState,
                com.fmscreenrecord.record.RecorderProcess.ProcessState oldState,
                int errCode, float fps);
    }

    static enum ProcessState {
        NEW,
        STARTING,
        READY,
        RECORDING,
        STOPPING,
        FINISHED,
        ERROR
    }

    class Timeout {
        private String errType;
        private int errorCode;
        private String errorName;
        private int time;
        private Timer timer;

        Timeout(int time, String errType, String errName, int errCode) {
            this.time = time;
            this.errType = errType;
            this.errorName = errName;
            this.errorCode = errCode;
        }

        void cancel() {
            synchronized (com.fmscreenrecord.record.RecorderProcess.this) {
                if (this.timer != null) {
                    this.timer.cancel();
                    this.timer = null;
                }
            }
        }

        void start() {
            synchronized (com.fmscreenrecord.record.RecorderProcess.this) {
                if (this.timer != null) {
                    Log.e(TAG, "Timeout already started");
                } else {
                    this.timer = new Timer();
                    this.timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (com.fmscreenrecord.record.RecorderProcess.this.process != null) {
                                Log.d(TAG, "Timeout," + errorName + ", killing the native process");
                                com.fmscreenrecord.record.RecorderProcess.this.forceKill();
                            }
                        }
                    }, this.time);
                }
            }
        }
    }
}