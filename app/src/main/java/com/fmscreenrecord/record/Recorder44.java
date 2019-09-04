package com.fmscreenrecord.record;

/**
 * 4.4录制
 *
 * @author lin
 * Create：2014-12
 */

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.utils.FileUtils;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.RUtils;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.RootUtils;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.fragment.LocalVideoFragment;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Recorder44 implements Runnable {

    // 语音文件保存路径
    public static String audioName = null;

    private static OutputStream stdin;
    private static InputStream stdout;
    private static Process process;

    private static Context mContext;
    static SharedPreferences sp;
    /**
     * 是否录制声音
     */
    public static boolean isRecordAudio = true;
    /**
     * 核心版本
     */
    static String coreVersions;

    @Override
    public void run() {

        MainActivity.runCommand("./system/bin/screenrecord "
                + MainActivity.path_dir + "/"
                + new SimpleDateFormat("yyyy-MM-dd_HHmmsss").format(new Date())
                + ".mp4");
    }

    /**
     * 开始录制视频
     */
    @SuppressLint("SimpleDateFormat")
    public final static void StartRecordVideo(final String resolutions,
                                              Context context) {
        // 根据当前系统时间命名视频文件
        final String videoFileName = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
                .format(new Date()) + ".mp4";
        mContext = context;
        MainActivity.videofilename = videoFileName;
        sp = PreferenceManager.getDefaultSharedPreferences(context
                .getApplicationContext());
        newRecordCore(resolutions, videoFileName);

    }

    /**
     * 新版录制核心方法
     */
    public final static void newRecordCore(final String resolutions,
                                           final String videoFileName) {

        new Thread() {
            public void run() {

                // 1280*720 3000 000 21M/min
                // 720*480 2000 000 15M/min
                // 640*360 1000 000 10M/min
                /**
                 * 根据分辨率录制视频 <br/>
                 * 流畅分辨率已于2.0.2版本废弃
                 * */
                if (resolutions.equals("流畅")) {
                    // 如果是横屏录制按钮，采用旧核心
                    if (sp.getBoolean("horizontalRecord", false)) {
                        coreVersions = "fmOldCore";
                        Settings.width = 640;
                        Settings.height = 360;
                    } else {
                        coreVersions = "fmNewCore";
                        // 根据手机屏幕方向设定分辨率
                        if (MainActivity.getConfiguration()) {
                            Settings.width = 640;
                            Settings.height = 360;
                        } else {
                            Settings.width = 360;
                            Settings.height = 640;
                        }
                    }

                    if (isRecordAudio) {// 录制声音
                        StartRecord("./data/data/com.li.videoapplication/files/"
                                + coreVersions + " --key_fmkj_yes_yes"
                                + " --bit-rate 1000000" + " --audio"
                                + " --size " + Settings.width + "x"
                                + Settings.height + " " + MainActivity.path_dir
                                + File.separator + videoFileName);
                    } else {
                        StartRecord("./data/data/com.li.videoapplication/files/"
                                + coreVersions + " --key_fmkj_yes_yes"
                                + " --bit-rate 1000000" + " --size "
                                + Settings.width + "x" + Settings.height + " "
                                + MainActivity.path_dir + File.separator
                                + videoFileName);
                    }

                } else if (resolutions.equals(ExApplication.SQuality)) {
                    // 如果是横屏录制按钮，采用旧核心
                    if (sp.getBoolean("horizontalRecord", false)) {
                        coreVersions = "fmOldCore";
                        Settings.width = 768;
                        Settings.height = 432;
                    } else {
                        coreVersions = "fmNewCore";
                        if (MainActivity.getConfiguration()) {
                            Settings.width = 768;
                            Settings.height = 432;
                        } else {
                            Settings.width = 432;
                            Settings.height = 768;
                        }
                    }
                    if (isRecordAudio) {// 录制声音
                        StartRecord("./data/data/com.li.videoapplication/files/"
                                + coreVersions + " --key_fmkj_yes_yes"
                                + " --bit-rate 1200000" + " --audio"
                                + " --size " + Settings.width + "x"
                                + Settings.height + " " + MainActivity.path_dir
                                + File.separator + videoFileName);
                    } else {
                        StartRecord("./data/data/com.li.videoapplication/files/"
                                + coreVersions + " --key_fmkj_yes_yes"
                                + " --bit-rate 1200000" + " --size "
                                + Settings.width + "x" + Settings.height + " "
                                + MainActivity.path_dir + File.separator
                                + videoFileName);
                    }
                } else {// 超清

                    // 如果是横屏录制按钮，采用旧核心
                    if (sp.getBoolean("horizontalRecord", false)) {
                        coreVersions = "fmOldCore";
                        Settings.width = 1280;
                        Settings.height = 720;
                    } else {
                        coreVersions = "fmNewCore";
                        if (MainActivity.getConfiguration()) {
                            Settings.width = 1280;
                            Settings.height = 720;
                        } else {
                            Settings.width = 720;
                            Settings.height = 1280;
                        }
                    }
                    if (isRecordAudio) {// 录制声音
                        StartRecord("./data/data/com.li.videoapplication/files/"
                                + coreVersions + " --key_fmkj_yes_yes"
                                + " --bit-rate 2000000" + " --audio"
                                + " --size " + Settings.width + "x"
                                + Settings.height + " " + MainActivity.path_dir
                                + File.separator + videoFileName);
                    } else {
                        StartRecord("./data/data/com.li.videoapplication/files/"
                                + coreVersions + " --key_fmkj_yes_yes"
                                + " --bit-rate 2000000" + " --size "
                                + Settings.width + "x" + Settings.height + " "
                                + MainActivity.path_dir + File.separator
                                + videoFileName);
                    }

                }

            }
        }.start();
    }

    private static void runCommand(String command) {
        try {

            String str = command + "\n";
            stdin.write(str.getBytes());
            stdin.flush();
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    public static void StartRecord(String executable) {

        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(new String[]{"su", "-c", executable});

            stdin = process.getOutputStream();
            stdout = process.getInputStream();
            try {
                process.waitFor();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            ;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    // 暂停录制
    public final static void PauseRecordVideo(Context mContext) {
        runCommand("fmkj_yes_haha_p");
    }

    // 录制重启
    public final static void RestartRecordVideo(Context mContext) {
        runCommand("fmkj_yes_haha_r");
    }

    /**
     * 停止录制视频 ，写命令行
     */
    public final static void StopRecordVideo() {
        // "新核心停止");
        runCommand("fmkj_yes_haha_s");
        open_record_file_action();
    }

    /**
     * 4.4录制结果通知
     */
    // TODO
    @SuppressLint("SdCardPath")
    private final static void open_record_file_action() {
        // 设置录屏的视频信息
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setPath(MainActivity.path_dir);
        videoInfo.setDisplayName(MainActivity.videofilename);
        videoInfo.setTime(MainActivity.videolong);

        int fileSize = (int) FileUtils.getFileSize(new File(videoInfo.getPath()
                + File.separator + videoInfo.getDisplayName()));

        File file = new File(videoInfo.getPath() + File.separator
                + videoInfo.getDisplayName());
        // 录制成功通知
        if (fileSize > 512) {

            // 对话框通知
            MinUtil.showToast(mContext,
                    RUtils.getRString("fm_record_succeed_and_toast_notif"));
            MinUtil.upUmenEventValue(mContext, "录制成功(4.4)", "recordResult");

            // 将视频路径和视频名称填入数据库
            VideoDB videoDB = new VideoDB(mContext);
            videoDB.insert(file.getName(), file.getPath(), "rec", "local");
            RecordVideo.nofity(file.getPath(), mContext);
            // 判断是否跳转到视频管理页
            Boolean isgotoVideoManage = PreferenceManager
                    .getDefaultSharedPreferences(mContext).getBoolean(
                            "isGotoVideoManage", true);
            if (isgotoVideoManage) {
                MinUtil.upUmenEventValue(mContext, "录屏后跳转到视频管理页",
                        "fmSettingPageCount");
                ExApplication.isgotovideomange = true;

                // 跳转到视频管理页
                if (isApplicationBroughtToBackground(mContext)) {
                    //APP处于后台时，跳转到手游视界
                    Intent intent2 = new Intent();
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.setClass(mContext, com.li.videoapplication.activity.MainActivity.class);
                    mContext.startActivity(intent2);
                } else {
                    //APP处于视频管理页时，不跳转只刷新适配器
                    if (VideoManagerActivity.isINVideoManagerActivity) {
                        LocalVideoFragment.localVideoHandle.sendEmptyMessage(2);
                    } else {
                        Intent intent2 = new Intent();
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.setClass(mContext, VideoManagerActivity.class);
                        mContext.startActivity(intent2);
                    }
                }

            } else {
                MinUtil.upUmenEventValue(mContext, "录屏后不跳转到视频管理页",
                        "fmSettingPageCount");
            }

        }
        // 录制失败通知
        else {

            if (RootUtils.appRoot1()) {// 判断是因为ROOT导致合成失败还是其他原因
                MinUtil.upUmenEventValue(mContext, "录制失败(4.4):合成失败",
                        "recordResult");
                // "杀死底层");
                MainActivity
                        .runCommand("/data/data/com.li.videoapplication/files/busybox pkill -SIGINT fmOldCore");
                MainActivity
                        .runCommand("/data/data/com.li.videoapplication/files/busybox pkill -SIGINT fmNewCore");

                RecordVideo.RecordStateNofity("非常抱歉，录屏失败，请退出手游视界再试", mContext);
                // 吐司通知录制失败
                MinUtil.showToast(mContext, "非常抱歉，录屏失败，请退出手游视界再试");
            } else {
                MinUtil.upUmenEventValue(mContext, "录制失败(4.4):Root失败",
                        "recordResult");
                RecordVideo.RecordStateNofity(
                        RUtils.getRString("fm_record_fail_and_toast_notif"),
                        mContext);
                // 吐司通知录制失败
                MinUtil.showToast(mContext,
                        RUtils.getRString("fm_record_fail_and_toast_notif"));
            }

        }
    }

    /**
     * 判断录屏大师是否处于后台
     *
     * @param context
     * @return
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                // 后台

                return true;
            }
        }
        // 前台

        return false;

    }
}