package com.fmscreenrecord.service;

/**
 * 浮窗服务
 *
 * @author lin
 * Create：2014-12
 */

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.app.SRApplication;
import com.fmscreenrecord.floatview.FloatContentView;
import com.fmscreenrecord.floatview.FloatContentView2;
import com.fmscreenrecord.floatview.FloatViewManager;
import com.fmscreenrecord.record.Recorder44;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.RootUtils;
import com.fmscreenrecord.video.VideoInfo;
import com.fmscreenrecord.videoeditor.VideoEditorUtil;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.fragment.LocalVideoFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FloatViewService extends Service {

    public static FloatViewManager windowManager;
    private ScheduledExecutorService threadPool;

    public static Handler handlerFloatViewService;
    public static Handler handlerFromFloat;

    public static Handler mHandler;
    // private Handler handler = new Handler();
    public static Context mContextFloatService;
    public static int times = 0;

    public boolean isHide = false;
    public boolean isShow = false;
    public static int recordCnt = 1; // 录制次数

    public static boolean isRecordAgain = false; // 是否多次录制

    private Toast mToast;

    private static VideoInfo videoInfo = new VideoInfo();

    public static boolean isInFloatViewService = false;


    @Override
    public void onCreate() {
        super.onCreate();

        isInFloatViewService = true;

        mContextFloatService = com.fmscreenrecord.service.FloatViewService.this;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, com.fmscreenrecord.service.FloatViewService.class), 0);

        // 浮窗的服务捆绑到状态栏中，相当于把服务从后台间接移到前台，提高应用的稳定性，防止被系统杀死
        Notification notification = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setTicker("启动录制服务")
                .setContentTitle("手游视界")
                .setSmallIcon(
                        MResource.getIdByName(mContextFloatService, "drawable",
                                "float_view_ico_down_small"))
                .setWhen(System.currentTimeMillis()).setAutoCancel(false)
                .getNotification();
        startForeground(1, notification);

        handlerFromFloat = new Handler() {
            @SuppressLint("SdCardPath")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        FloatContentView.handlerFromFloatService
                                .sendEmptyMessage(1);
                    }
                    break;
                    case 2: {
                        Intent intent2 = new Intent();
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.setClass(mContextFloatService, MainActivity.class);
                        mContextFloatService.startActivity(intent2);
                    }
                    break;
                    case 3: {
                        FloatContentView2.handlerFromFloatService
                                .sendEmptyMessage(1);
                    }
                    break;
                    case 4: {

                        // 给FloatContentView发送消息停止录屏
                        FloatContentView.handlerFromFloatService
                                .sendEmptyMessage(2);

                    }
                    break;
                    default:
                        break;
                }

            }

        };
        //TODO
        mHandler = new Handler() {
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 1: {
                        ExApplication.isCompositionVideo = true;
                        showToast("视频合成中,请稍候...");
                    }
                    break;
                    case 2: {
                        ExApplication.isCompositionVideo = false;
                        showToast("视频已合成,可点击通知栏进行预览");
                        MinUtil.upUmenEventValue(mContextFloatService, "录制成功",
                                "recordResult");

                        if (ExApplication.islaterCloaseApp == true) {
                            exitProgrames();
                        }

                        // 视频合成路径
                        String path = (String) msg.obj;
                        // 截取视频路径中的视频名称
                        String name = path.substring(path.lastIndexOf("/") + 1);
                        VideoDB videoDB = new VideoDB(mContextFloatService);
                        videoDB.insert(name, path, "rec", "local");
                        nofity(path);

                        // 判断是否跳转到视频管理页
                        Boolean isgotoVideoManage = PreferenceManager
                                .getDefaultSharedPreferences(mContextFloatService)
                                .getBoolean("isGotoVideoManage", true);
                        if (isgotoVideoManage) {
                            MinUtil.upUmenEventValue(mContextFloatService,
                                    "录屏后跳转到视频管理页", "fmSettingPageCount");
                            ExApplication.isgotovideomange = true;

                            // 跳转到视频管理页
                            if (Recorder44
                                    .isApplicationBroughtToBackground(mContextFloatService)) {
                                //APP处于后台时，跳转到手游视界
                                Intent intent2 = new Intent();
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent2.setClass(mContextFloatService,
                                        com.li.videoapplication.activity.MainActivity.class);
                                mContextFloatService.startActivity(intent2);
                            } else {
                                //APP处于视频管理页时，不跳转只刷新适配器
                                if (VideoManagerActivity.isINVideoManagerActivity) {
                                    LocalVideoFragment.localVideoHandle.sendEmptyMessage(2);
                                } else {
                                    Intent intent2 = new Intent();
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent2.setClass(mContextFloatService,
                                            VideoManagerActivity.class);
                                    mContextFloatService.startActivity(intent2);
                                }
                            }

                        } else {
                            MinUtil.upUmenEventValue(mContextFloatService,
                                    "录屏后不跳转到视频管理页", "fmSettingPageCount");

                        }

                    }
                    break;
                    case 3: {
                        ExApplication.isCompositionVideo = false;
                        //Log.i("mtime", "视频合成失败");
                        showToast("非常抱歉,您的视频合成失败了，请确保手机真正root成功后重试");
                        if (ExApplication.islaterCloaseApp == true) {
                            exitProgrames();
                        }
                        if (RootUtils.appRoot1()) {
                            MinUtil.upUmenEventValue(mContextFloatService, "录制失败:合成失败",
                                    "recordResult");

                        } else {
                            MinUtil.upUmenEventValue(mContextFloatService, "录制失败:Root失败",
                                    "recordResult");

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
                                        com.fmscreenrecord.service.FloatViewService.class));

                android.os.Process.killProcess(android.os.Process.myPid());

            }
        };

    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContextFloatService, text,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public void DelAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                DelAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isInFloatViewService = true;
        if (windowManager == null) {
            windowManager = FloatViewManager
                    .getInstance(getApplicationContext());
        }
        if (threadPool == null) {
            threadPool = Executors.newScheduledThreadPool(1);
            threadPool.scheduleAtFixedRate(command, 0, 1, TimeUnit.SECONDS);

        }

        // 开启悬浮框
        windowManager.showContent();
        return START_REDELIVER_INTENT;// super.onStartCommand(intent, flags,
        // startId);
    }

    private Runnable command = new Runnable() {
        @Override
        public void run() {

            // 在该方法中，定时更新ui，比如访问网络数据的实时更新，主要完成于悬浮框的通信，这里暂不实现

            if (RecordVideo.isStop == false
                    && RecordVideo.isStart == true) {
                if (ExApplication.pauseRecVideo == false) {
                    times++;
                }

            } else if (RecordVideo.isStart == false
                    && RecordVideo.isStop == true) {
                times = 0;
                recordCnt = 1;
            }
            // System.out.println("run--time==" + times + "---cnt==" +
            // recordCnt);

        }
    };

    public void onDestroy() {
        isInFloatViewService = false;
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
        super.onDestroy();
    }

    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称 （系统原装应用、Go桌面等等） 这些应用都会包含："android.intent.category.Home"；
     * 所以只要找出所有的声明为Home的activity的"android.intent.action.MAIN"所在的包名就可以了！
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                FloatViewManager.isLandscape = true;
                windowManager.changeOrientation(1);
            }
            break;

            case Configuration.ORIENTATION_PORTRAIT: {
                FloatViewManager.isLandscape = false;
                windowManager.changeOrientation(0);
            }
            break;
        }
    }

    public static void muxVideoAndAudio(final String[] fileList,
                                        final String audioName) {
        new Thread() {

            public void run() {

                if (fileList.length > 1) {
                    mHandler.sendEmptyMessage(1);

                    // String[] fileList = Recorder44.fileList.toArray(new
                    // String[Recorder44.fileList.size()]);
                    String appendVideo = VideoEditorUtil.appendVideo(fileList);
                    if (appendVideo != null) {
                        if (Recorder44.isRecordAudio) {
                            System.out.println("======appendVideo======"
                                    + appendVideo);
                            String videoFile = VideoEditorUtil
                                    .MuxVideoAndAudio(appendVideo, audioName);
                            if (videoFile != null) {

                                Message msg = new Message();
                                msg.obj = videoFile;
                                msg.what = 2;
                                mHandler.sendMessage(msg);
                                // mHandler.sendEmptyMessage(2);

                                System.out.println("===============视频已合成");
                            } else {
                                mHandler.sendEmptyMessage(3);
                                System.out.println("===============合成失败1");
                            }

                            // endRecord(videoFile);

                        } else {
                            // mHandler.sendEmptyMessage(2);
                            System.out.println("===============视频已合成");
                            // endRecord(appendVideo);
                            Message msg = new Message();
                            msg.obj = appendVideo;
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        mHandler.sendEmptyMessage(3);
                        System.out.println("===============合成失败2");
                    }
                    // 删除源文件
                    for (String x : fileList) //
                    {
                        File file = new File(x);
                        if (file.exists()) {
                            file.delete();
                        }
                    }

                } else {
                    // String[] fileList = Recorder44.fileList.toArray(new
                    // String[Recorder44.fileList.size()]);

                    System.out.println("sl====" + fileList[0]);
                    if (Recorder44.isRecordAudio) {
                        mHandler.sendEmptyMessage(1);
                        String videoFile = VideoEditorUtil.MuxVideoAndAudio(
                                fileList[0], audioName);
                        if (videoFile != null) {
                            System.out.println("===============视频已合成");
                            // 视频已合成;
                            Message msg = new Message();
                            msg.obj = videoFile;
                            msg.what = 2;
                            mHandler.sendMessage(msg);

                        } else {
                            mHandler.sendEmptyMessage(3);
                            System.out.println("===============合成失败3");
                        }

                        // endRecord(videoFile);
                    } else {
                        // mHandler.sendEmptyMessage(2);
                        System.out.println("===============视频已合成");
                        Message msg = new Message();
                        msg.obj = fileList[0];
                        msg.what = 2;
                        mHandler.sendMessage(msg);

                    }
                }

            }
        }.start();
    }

    public boolean endRecord(String videoFile) {
        MainActivity.videofilename = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
                .format(new Date()) + ".mp4";
        String newName = MainActivity.path_dir + File.separator
                + MainActivity.videofilename;
        new File(videoFile).renameTo(new File(newName));
        MainActivity.endRecord = true;
        return true;
    }

    // 通知栏通知

    /**
     * @param fileName 视频保存路径
     */
    @SuppressLint("NewApi")
    public void nofity(String fileName) {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = MResource.getIdByName(mContextFloatService, "drawable",
                "tubiao");
        CharSequence tickerText = "视频合成完成,来这里预览你的作品~";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Context context = getApplicationContext();
        CharSequence contentTitle = "手游视界";
        CharSequence contentText = "点击预览视频";
        File file = new File(fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndTypeAndNormalize(uri, "video/mp4");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        mNotificationManager.notify(0, notification);
    }
}