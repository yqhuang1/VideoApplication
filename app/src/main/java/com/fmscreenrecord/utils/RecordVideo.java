package com.fmscreenrecord.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.activity.ScreenRecord50;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.app.SRApplication;
import com.fmscreenrecord.floatview.FloatView2;
import com.fmscreenrecord.frontcamera.FrontCameraService;
import com.fmscreenrecord.record.Recorder44;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.service.ScreenRECService;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.fragment.LocalVideoFragment;

import java.io.File;

/**
 * 录屏相关类
 *
 * @author WYX
 */
public class RecordVideo {
    Context mContext;

    /**
     * 是否开始录屏
     */
    public static boolean isStart = false;
    /**
     * 是否停止录屏
     */
    public static boolean isStop = true;
    public static boolean useFloatView = false;
    /**
     * 是否处于录屏之中
     */
    public static boolean isRecordering = false;
    public static boolean recordering = false;
    public static Handler mHandler;
    // 视频模式（高清，标准）
    String videoMode;
    private SharedPreferences sp;

    public RecordVideo(Context context) {
        mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 1: {
                        ExApplication.isCompositionVideo = true;
                        MinUtil.showToast(mContext, "视频合成中,请稍候...");

                    }
                    break;
                    case 2: {

                        ExApplication.isCompositionVideo = false;
                        MinUtil.showToast(mContext, "视频已合成,可点击通知栏进行预览");
                        MinUtil.upUmenEventValue(mContext, "录制成功", "recordResult");

                        if (ExApplication.islaterCloaseApp == true) {
                            exitProgrames();
                        }

                        // 视频合成路径
                        String path = (String) msg.obj;
                        // 截取视频路径中的视频名称
                        String name = path.substring(path.lastIndexOf("/") + 1);
                        VideoDB videoDB = new VideoDB(mContext);
                        videoDB.insert(name, path, "rec", "local");
                        nofity(path, mContext);

                        // 判断是否跳转到视频管理页
                        Boolean isgotoVideoManage = PreferenceManager
                                .getDefaultSharedPreferences(mContext).getBoolean(
                                        "isGotoVideoManage", true);

                        if (isgotoVideoManage) {
                            MinUtil.upUmenEventValue(mContext, "录屏后跳转到视频管理页",
                                    "fmSettingPageCount");
                            ExApplication.isgotovideomange = true;

                            // 跳转到视频管理页
                            if (Recorder44.isApplicationBroughtToBackground(mContext)) {
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
                    break;
                    case 3: {

                        ExApplication.isCompositionVideo = false;
                        String content;
                        if (MainActivity.getAndroidSDKVersion() >= 21) {
                            content = "非常抱歉,您的视频合成失败了";
                        } else {
                            content = "非常抱歉,您的视频合成失败了，请确保手机真正root成功后重试";
                        }

                        MinUtil.showToast(mContext, content);
                        if (ExApplication.islaterCloaseApp == true) {
                            exitProgrames();
                        }
                        if (RootUtils.appRoot1()) {
                            MinUtil.upUmenEventValue(mContext, "录制失败:合成失败", "recordResult");

                        } else {
                            MinUtil.upUmenEventValue(mContext, "录制失败:Root失败", "recordResult");

                        }
                    }
                    break;
                    default:
                        break;
                }
            }

            /**
             * 退出录屏大师后台服务
             */
            private void exitProgrames() {

                // 取消所有的通知
                NotificationManager mNotificationManager;
                mNotificationManager = (NotificationManager) SRApplication
                        .Get().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();

                // 终止录制服务
                SRApplication.Get()
                        .stopService(
                                new Intent(SRApplication.Get(),
                                        ScreenRECService.class));

                // 终止浮窗服务
                SRApplication.Get()
                        .stopService(
                                new Intent(SRApplication.Get(),
                                        FloatViewService.class));

                android.os.Process.killProcess(android.os.Process.myPid());

            }
        };
    }

    public void stardRecordVideo() {

        // if (ExApplication.floatCameraClose == true) {

        if (MainActivity.SDKVesion < 19) {
            // 这里同时也兼应用第一次录屏时设置清晰度（根据android版本不同设置不同默认配置，4.4以下是标清，4.4以上是超清）
            videoMode = sp
                    .getString("quality_of_video", ExApplication.SQuality);

            // 将2.0.1.1及之前版本的清晰度修改成最新版本
            if (videoMode.equals("高清")) {
                videoMode = "0";
            } else if (videoMode.equals("标准")) {
                videoMode = "1";
            } else if (videoMode.equals("流畅")) {
                videoMode = "0";
            }
            // 将当前清晰度配置赋给全局变量
            ExApplication.videoQuality = videoMode;

            // 开始录屏
            StartRecordForVesionOther();

        } else if (MainActivity.SDKVesion >= 19 && MainActivity.SDKVesion < 21) {
            videoMode = sp
                    .getString("quality_of_video", ExApplication.HQuality);

            // 将2.0.1.1及之前版本的清晰度修改成最新版本
            if (videoMode.equals("高清")) {
                videoMode = "0";
            } else if (videoMode.equals("标准")) {
                videoMode = "1";
            } else if (videoMode.equals("流畅")) {
                videoMode = "0";
            }

            ExApplication.videoQuality = videoMode;
            StartRecordForVesion19();

        } else {
            videoMode = sp
                    .getString("quality_of_video", ExApplication.HQuality);

            // 将2.0.1.1及之前版本的清晰度修改成最新版本
            if (videoMode.equals("高清")) {
                videoMode = "0";
            } else if (videoMode.equals("标准")) {
                videoMode = "1";
            } else if (videoMode.equals("流畅")) {
                videoMode = "0";
            }

            ExApplication.videoQuality = videoMode;
            ExApplication.mConfiguration = MainActivity.getConfiguration();
            Intent intent2 = new Intent();
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setClass(mContext, ScreenRecord50.class);

            mContext.startActivity(intent2);

        }
        // 如果打开了设置页中的触摸选项，录屏时显示触摸点
        boolean isShowTouch = sp.getBoolean("show_touch_view", false);
        if (isShowTouch) {
            android.provider.Settings.System.putInt(
                    mContext.getContentResolver(), "show_touches", 1);
        }
        // 如果打开了前置摄像头选项
        boolean isfloatCamera = sp.getBoolean("show_front_camera", false);
        if (isfloatCamera) {
            // 开启画中画
            Intent intent = new Intent(mContext, FrontCameraService.class);
            mContext.startService(intent);
            ExApplication.floatCameraClose = false;

        }
        FloatView2.doubleclick = false;
    }

    /**
     * 4.4版本开始录制方法
     */
    public void StartRecordForVesion19() {

        if (isStart == false && isStop == true) {

            sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (sp.getBoolean("record_sound", true)) {
                Recorder44.isRecordAudio = true;

            } else {
                Recorder44.isRecordAudio = false;
            }

            isRecordering = true;
            recordering = true;
            isStart = true;
            isStop = false;
            MainActivity.endRecord = false;

            Recorder44.StartRecordVideo(videoMode, mContext);

        }
    }

    /**
     * 4.4版本停止录制方法
     */
    public void StopRecordForVesion19() {
        // SettingShowTouchesController.setShowTouches(mContext, false);
        isRecordering = false;
        isStop = true;
        isStart = false;

        FloatView2.min = 0; // 时间清零
        FloatView2.sec = 0;

        Recorder44.StopRecordVideo();

    }

    /**
     * 4.4以下系统开始录制方法
     */
    public void StartRecordForVesionOther() {
        if (isStart == false && isStop == true) {
            isStart = true;
            isStop = false;
            isRecordering = true;
            MainActivity.endRecord = false;
            // btStart.setImageDrawable(getResources().getDrawable(MResource.getIdByName(mContext,"drawable","bt_stop_down")));
            useFloatView = true;
            // 录制结果对用户进行通知
            ScreenRECService.handlerFloat.sendEmptyMessage(4);
            // isLayoutBackClick = true;

        }

    }

    /**
     * 4.4以下系统停止录制方法
     */
    public void StopRecordForVesionOther() {

        isStop = true;
        isStart = false;
        useFloatView = false;
        isRecordering = false;

        // MainActivity.endRecord = true;
        FloatView2.min = 0; // 时间清零
        FloatView2.sec = 0;


        // 录制结果对用户进行通知
        ScreenRECService.handlerFloat.sendEmptyMessage(4);
        // isLayoutBackClick = true;

    }

    /**
     * 录制成功通知栏
     *
     * @param fileName 视频保存路径
     */
    @SuppressLint("NewApi")
    public static void nofity(String fileName, Context mContext) {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(ns);
        int icon = MResource.getIdByName(mContext, "drawable", "icon");
        CharSequence tickerText = "视频合成完成,来这里预览你的作品~";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Context context = mContext;
        CharSequence contentTitle = "手游视界";
        CharSequence contentText = "点击预览视频";
        File file = new File(fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndTypeAndNormalize(uri, "video/mp4");
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        mNotificationManager.notify(0, notification);

    }

    /**
     * 录制状态通知栏
     *
     * @param content
     * @param mContext
     */
    @SuppressWarnings("deprecation")
    public static void RecordStateNofity(String content, Context mContext) {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(ns);
        int icon = MResource.getIdByName(mContext, "drawable", "icon");
        CharSequence tickerText = content;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Context context = mContext;
        CharSequence contentTitle = "手游視界";
        CharSequence contentText = content;

        Intent intent = new Intent();

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        mNotificationManager.notify(0, notification);

    }
}
