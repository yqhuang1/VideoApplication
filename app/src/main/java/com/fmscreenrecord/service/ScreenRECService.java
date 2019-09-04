package com.fmscreenrecord.service;

/**
 * 录制服务
 *
 * @author lin
 * Create：2014-12
 */

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.activity.ActivityAlertDialog;
import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.activity.ScreenRecord50;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.app.SRApplication;
import com.fmscreenrecord.floatview.FloatView2;
import com.fmscreenrecord.floatview.FloatViewManager;
import com.fmscreenrecord.frontcamera.CameraView;
import com.fmscreenrecord.frontcamera.FrontCameraService;
import com.fmscreenrecord.record.NativeProcessRunner;
import com.fmscreenrecord.record.RecordAction;
import com.fmscreenrecord.record.Recorder44;
import com.fmscreenrecord.record.ScreenCoreHandler;
import com.fmscreenrecord.record.Settings;
import com.fmscreenrecord.record.ShakeListeners;
import com.fmscreenrecord.utils.EnumUtils;
import com.fmscreenrecord.utils.FileUtils;
import com.fmscreenrecord.utils.RUtils;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.fmscreenrecord.utils.ViewUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenRECService extends Service implements
        ShakeListeners.OnShakeListener {
    // 视频模式（高清，标准，流畅）
    String videoMode;
    private SharedPreferences sp;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            for (int i = 0; i < 50; i++) {
                showTransparentToast(ScreenRECService.this);
            }
        }
    };

    // 浮窗的消息处理
    public final static Handler handlerFloat = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4: // 开始或停止录制
                {
                    if (!isProgress) {

                        Intent intent = new Intent(RecordAction.ACTION);
                        intent.putExtra("action", "screen_shot");
                        intent.putExtra("hideStatusPanel", true);
                        mContextScreenService.sendBroadcast(intent);

                    } else {
                        Intent intent = new Intent(RecordAction.ACTION);
                        intent.putExtra("action", "screen_shot");
                        intent.putExtra("hideStatusPanel", true);
                        mContextScreenService.sendBroadcast(intent);

                        if (MainActivity.loading != null) // 在主页，隐藏浮窗
                        {
                            // FloatView1.handler.sendEmptyMessage(3);
                        }
                    }

                }
                break;
                case 5: // 回调设置界面
                {
                    // Intent intent2 = new Intent();
                    // intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent2.setClass(mContextScreenService,
                    // SettingActivity.class);
                    // mContextScreenService.startActivity(intent2);
                }
                break;
                case 6: // 回调设置界面
                {
                    Intent intent2 = new Intent();
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.setClass(mContextScreenService, MainActivity.class);
                    mContextScreenService.startActivity(intent2);
                }
                break;

                case 7: // 开声音
                {
                    SharedPreferences.Editor editor = sound_settings.edit();
                    editor.putBoolean("sound_setting", true);
                    editor.commit();
                }
                break;
                case 8: // 关声音
                {
                    SharedPreferences.Editor editor = sound_settings.edit();
                    editor.putBoolean("sound_setting", false);
                    editor.commit();
                }
                break;
                case 9: {
                    Intent intent = new Intent();
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent.setClass(mContextScreenService,
                    // FloatViewService.class);
                    intent = new Intent(mContextScreenService,
                            FloatViewService.class);
                    mContextScreenService.startService(intent);
                }
                break;
                case 10: {
                /*
				 * Intent intent = new Intent(RecordAction.ACTION);
				 * intent.putExtra("action", "open_record_file");
				 * intent.putExtra("out_file",MainActivity.path_dir+ "/"+
				 * MainActivity.videofilename);
				 * mContextScreenService.sendBroadcast
				 * (RecordAction.getActionIntent("open_record_file_action"));
				 */

                }
                break;
            }

        }
    };

    private static String PREFS_NAME = "sound_setting";
    public static SharedPreferences sound_settings;

    NativeProcessRunner mNativeProcessRunner;
    long screen_shot_last_time = 0;
    Thread thread;
    long lastShake = System.currentTimeMillis();
    private static boolean isProgress = false;
    // private SoundPool pool;
    // private int soundID;
    // private float streamVolume;
    private static String outFile;

    public static Context mContextScreenService;

    private FloatViewManager manager;

    private boolean yaohuangjieping = true;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if ("show_screenshot_notification".equals(action)) {
                // showScreenshotNotification();
            } else if ("start_yaohuangjieping_service".equals(action)) {
                start_yaohuangjieping_service();
            } else if ("end_yaohuangjieping_service".equals(action)) {
                end_yaohuangjieping_service();
            } else if ("start_record_no_alert".equals(action)) {
                start_record_no_alert();
            } else if ("cancel_record_no_alert".equals(action)) {
                cancel_record_no_alert();
            } else if ("open_record_file_action".equals(action)) {
                open_record_file_action();
            } else if ("open_record_file_without_alert".equals(action)) {
                final String fileName = intent.getStringExtra("file_path");
                open_record_file_without_alert(fileName);
            } else if ("share_record_file_to_friend".equals(action)) {
                share_record_file_to_friend(intent);
            } else if ("open_record_file_without_alert_preview".equals(action)) {
                final String fileName = intent.getStringExtra("file_path");
                open_record_file_without_alert_preview(fileName);
            } else if ("open_record_file_without_alert_download_mx_player"
                    .equals(action)) {

            } else if ("open_record_file_without_alert_dont_show_again"
                    .equals(action)) {

            } else if ("vote_comment_btn_action".equals(action)) {

            } else if ("alert_no_auth_get_pro_version".equals(action)) {
                alert_no_auth_get_pro_version();
            } else if ("screen_shot".equals(action)) {
                if (intent.getBooleanExtra("hideStatusPanel", false)) {
                    ViewUtils.hideStatusPanel(ScreenRECService.this);
                }
                if ((System.currentTimeMillis() - screen_shot_last_time) > 3000) {

                    if (!isProgress) {

                        start_record_no_alert();

                        RecordVideo.isStart = true;
                        RecordVideo.isStop = false;

                    } else {
                        isProgress = false;
                        RecordVideo.isStart = false;
                        RecordVideo.isStop = true;

                        FloatView2.min = 0; // 时间清零
                        FloatView2.sec = 0;

                        endScreenRecord();
                        MainActivity.endRecord = true;
                    }
                }
            } else if ("open_record_file".equals(action)) {
                String fileName = intent.getStringExtra("path");
                open_record_file_without_alert(fileName);
            }
        }
    };

    private void cancel_record_no_alert() {
        ScreenCoreHandler.uninstall(ScreenRECService.this);
        isProgress = false;
    }

    public void start_record_no_alert() {
        final Handler handler = new Handler() {
            private boolean flash = false;


            @Override
            public void handleMessage(Message msg) {
                int icon;
                icon = getRDrawableID("icon");
                if (msg.what == 1) {
                    msg.what = 0;

                    if (!flash) {
                        icon = getRDrawableID("icon");
                        flash = true;
                    } else {
                        icon = getRDrawableID("recording_2");
                        flash = false;
                    }
                }

                if (isProgress) {
                    sendEmptyMessageDelayed(1, 1000);
                } else {
                    // mNotificationManager.cancel(getRXmlID("screenshot_setting"));
                }
            }
        };
        showTransparentToastInUI();
        handler.sendEmptyMessageDelayed(0, 800);
        if (PreferenceManager
                .getDefaultSharedPreferences(ScreenRECService.this).getBoolean(
                        "play_screenshot_sound", true)) {

            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    _record_screen_now();
                }
            }.sendEmptyMessageDelayed(0, 700);
        } else {
            _record_screen_now();
        }
    }

    private static void showTransparentToast(Context context) {
        Toast result = new Toast(context);
        TextView tv = new TextView(context);
        tv.setText("");
        result.setView(tv);
        result.setDuration(Toast.LENGTH_LONG);
        result.show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(RecordAction.ACTION);

        registerReceiver(broadcastReceiver, filter);

        new ShakeListeners(this).setOnShakeListener(this);

        mContextScreenService = ScreenRECService.this;

        sound_settings = getSharedPreferences(PREFS_NAME, 0);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean hide_notify = sp.getBoolean("hide_notify", false);

        if (sp.getBoolean("yahangjieping", false)) {
            start_yaohuangjieping_service();
        } else {
            end_yaohuangjieping_service();
        }
        manager = FloatViewManager.getInstance(mContextScreenService);

        DisplayMetrics dm = mContextScreenService.getResources()
                .getDisplayMetrics();
        Settings.width = (int) (dm.widthPixels);
        Settings.height = (int) (dm.heightPixels);

    }

    void startRecord() {

        mNativeProcessRunner = new NativeProcessRunner();
        mNativeProcessRunner.setExecutable(ScreenCoreHandler.install(this));
        mNativeProcessRunner.initialize();

        mNativeProcessRunner
                .setOnReadyListener(new NativeProcessRunner.OnReadyListener() {
                    @Override
                    public void onReady() {

                        outFile = getOutputFile().getAbsolutePath();
                        mNativeProcessRunner.start(outFile);
                    }

                    @Override
                    public void onFinished() {
                        ScreenCoreHandler.uninstall(ScreenRECService.this);

                    }
                });
    }

    // 水印

    void stopRecording() {
        // FloatView2.removeWater();
        this.mNativeProcessRunner.stop();
        this.mNativeProcessRunner.destroy();
    }

    private void _record_screen_now() {
        isProgress = true;
        configSettings();

        startRecord();
        // alertNoAuth();
    }

    // -----录制参数配置-----！！！
    private void configSettings() {
        Context context = ScreenRECService.this;
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        // 是否录制声音
        if (sp.getBoolean("record_sound", false)) {
            Settings.audioSource = Settings.AudioSource.MIC;
        } else {
            Settings.audioSource = Settings.AudioSource.MUTE;
        }

        int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                Settings.rotation = EnumUtils.ordinalOf(Settings.Rotation.class,
                        Integer.valueOf("1"));
                break;
            case Surface.ROTATION_90:
                Settings.rotation = EnumUtils.ordinalOf(Settings.Rotation.class,
                        Integer.valueOf("0"));
                break;
            case Surface.ROTATION_180:
                Settings.rotation = EnumUtils.ordinalOf(Settings.Rotation.class,
                        Integer.valueOf("3"));
                break;
            case Surface.ROTATION_270:
                Settings.rotation = EnumUtils.ordinalOf(Settings.Rotation.class,
                        Integer.valueOf("2"));
                break;
        }

        if (sp.getBoolean("video_draw", false)) {
            Settings.cpuGpu = Settings.CpuGpu.GPU;
        } else {
            Settings.cpuGpu = Settings.CpuGpu.CPU;
        }

        // 图像格式
        Settings.colorFix = false;// sp.getBoolean("color_fix", false);
        // 触摸反馈
        Settings.showTouches = sp.getBoolean("show_touches", false);

        if (sp.getBoolean("use_debug", false)) {

            // 屏幕大小
            String size = sp.getString("save_format",
                    getRString("fm_orig_size"));
            if (getRString("fm_orig_size").equals(size)) // 原始大小
            {
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                Settings.width = dm.widthPixels;
                Settings.height = dm.heightPixels;
            } else // 自定义大小
            {
                String[] ss = size.split("x"); // 以乘号分割
                Settings.width = Integer.valueOf(ss[0]);
                Settings.height = Integer.valueOf(ss[1]);
            }

            // 帧率
            Settings.fps = sp.getInt("video_fps", 15);

            // 编码
            Settings.encoder = EnumUtils.ordinalOf(Settings.VideoEncoder.class,
                    Integer.valueOf(sp.getString("video_encoder", "0")));
            // 视频音频质量
            Settings.videoQuality = EnumUtils.ordinalOf(
                    Settings.VideoQuality.class,
                    Integer.valueOf(sp.getString("video_quality", "0")));
            Settings.audioQuality = EnumUtils.ordinalOf(
                    Settings.AudioQuality.class,
                    Integer.valueOf(sp.getString("audio_quality", "0")));
        } else {

            // 编码
            Settings.encoder = EnumUtils.ordinalOf(Settings.VideoEncoder.class,
                    Integer.valueOf("0"));

            String mode = sp.getString("quality_of_video", ExApplication.SQuality);

            if (mode.equals("流畅")) {
				/*
				 * Settings.fps = 12; DisplayMetrics dm =
				 * context.getResources().getDisplayMetrics(); Settings.width =
				 * (int)((dm.widthPixels)/2.5); Settings.height =
				 * (int)((dm.heightPixels)/2.5);
				 * 
				 * Settings.videoQuality =
				 * EnumUtils.ordinalOf(Settings.VideoQuality.class,
				 * Integer.valueOf("0")); Settings.audioQuality =
				 * EnumUtils.ordinalOf(Settings.AudioQuality.class,
				 * Integer.valueOf("1"));
				 */
                Settings.fps = 12;

                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                Settings.width = (int) (dm.widthPixels / 2);
                Settings.height = (int) (dm.heightPixels / 2);

                Settings.videoQuality = EnumUtils.ordinalOf(
                        Settings.VideoQuality.class, Integer.valueOf("2"));
                Settings.audioQuality = EnumUtils.ordinalOf(
                        Settings.AudioQuality.class, Integer.valueOf("2"));
            } else if (mode.equals(ExApplication.SQuality)) {
				/*
				 * Settings.fps = 15;
				 * 
				 * DisplayMetrics dm =
				 * context.getResources().getDisplayMetrics(); Settings.width =
				 * (int)(dm.widthPixels/2); Settings.height =
				 * (int)(dm.heightPixels/2);
				 * 
				 * Settings.videoQuality =
				 * EnumUtils.ordinalOf(Settings.VideoQuality.class,
				 * Integer.valueOf("2")); Settings.audioQuality =
				 * EnumUtils.ordinalOf(Settings.AudioQuality.class,
				 * Integer.valueOf("2"));
				 */
                Settings.fps = 15;
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                Settings.width = (int) (dm.widthPixels / 1.7);
                Settings.height = (int) (dm.heightPixels / 1.7);

                Settings.videoQuality = EnumUtils.ordinalOf(
                        Settings.VideoQuality.class, Integer.valueOf("3"));
                Settings.audioQuality = EnumUtils.ordinalOf(
                        Settings.AudioQuality.class, Integer.valueOf("2"));

            } else if (mode.equals(ExApplication.HQuality)) {
                Settings.fps = 15;
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                Settings.width = (int) (dm.widthPixels / 1.4);
                Settings.height = (int) (dm.heightPixels / 1.4);

                Settings.videoQuality = EnumUtils.ordinalOf(
                        Settings.VideoQuality.class, Integer.valueOf("3"));
                Settings.audioQuality = EnumUtils.ordinalOf(
                        Settings.AudioQuality.class, Integer.valueOf("2"));
            }

        }

    }

    private void showTransparentToastInUI() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        });
        thread.setPriority(1);
        thread.setDaemon(true);
        thread.start();

    }

    private void endScreenRecord() {
        if (null != thread) {
            thread.interrupt();
        }
        thread = null;
        stopRecording();

		/*
		 * Intent itt = new Intent(); itt.putExtra("out_file", outFile);
		 * itt.setClass(ScreenRECService.this, EndRecordAlertDialog.class);
		 * itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(itt);
		 */

        Intent intent = new Intent(RecordAction.ACTION);
        intent.putExtra("action", "open_record_file");
        intent.putExtra("out_file", outFile);
        sendBroadcast(RecordAction.getActionIntent("open_record_file_action"));

    }

    // 视频合成成功与否的吐司提示
    private void open_record_file_action() {

        if (outFile != null) {
            int fileSize = (int) FileUtils.getFileSize(new File(outFile));
            if (fileSize > 512) {

                Message msg = new Message();
                // 视频文件路径
                msg.obj = outFile;
                msg.what = 2;
                // 发送消息准备吐司通知用户录制成功
                FloatViewService.mHandler.sendMessage(msg);
            } else {

                // 发送消息准备吐司通知用户录制失败
                FloatViewService.mHandler.sendEmptyMessage(3);

            }
        }

    }

    private void openFile1(String fileName) {
        if (null != fileName && new File(fileName).exists()) {
            openFile(new File(fileName));
        }
        NotificationManager mNotificationManager = (NotificationManager) SRApplication
                .Get().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(getRDrawableID("icon"));
        restartService();
    }

    private void onOpenRecordFile(final String fileName) {
        Intent itt = new Intent();
        itt.putExtra("title", getRString("fm_gen_video_done"));
        itt.putExtra("content",
                Html.fromHtml(getRString("fm_record_done_and_click_open")));
        itt.putExtra("button1", getRString("fm_preview"));
        itt.putExtra("button2", getRString("fm_share_to_firend"));
        itt.putExtra("cancelable", false);
        Intent button1Intent = RecordAction
                .getActionIntent("open_record_file_without_alert");
        button1Intent.putExtra("file_path", fileName);
        itt.putExtra("button1Intent", button1Intent);
        Intent button2Intent = RecordAction
                .getActionIntent("share_record_file_to_friend");
        button2Intent.putExtra("file_path", fileName);
        itt.putExtra("button2Intent", button2Intent);
        itt.setClass(ScreenRECService.this, ActivityAlertDialog.class);
        itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(itt);
    }

    private void share_record_file_to_friend(Intent itt) {
        String fileName = itt.getStringExtra("file_path");
        Intent intent = createShareIntent(new File(fileName));
        intent = Intent.createChooser(intent, getRString("fm_slelct_to_share"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        NotificationManager mNotificationManager = (NotificationManager) SRApplication
                .Get().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(getRDrawableID("icon"));
        restartService();
    }

    private void open_record_file_without_alert_preview(String fileName) {
        openFile1(fileName);
    }

    private void open_record_file_without_alert_dont_show_again() {
        final SharedPreferences sp = SharedPreferencesUtils
                .getMinJieKaiFaPreferences(ScreenRECService.this);
        sp.edit().putBoolean("do_not_show_cant_play_dialog", true).commit();
    }

    private void open_record_file_without_alert(final String fileName) {

        openFile1(fileName);
        // }
        // voteAndComment();
    }

    private Intent createShareIntent(File file) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/html");
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_EMAIL,
                RUtils.getRString("fm_share_to_email_title"));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                RUtils.getRString("fm_share_to_email_title"));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                Html.fromHtml(RUtils.getRString("fm_share_def_content")));
        return shareIntent;
    }

    private void restartService() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: {
                        // UtilService.stopService(ExApplication.Get());
                        break;
                    }
                    case 1: {
                        // UtilService.startService(ExApplication.Get());
                        break;
                    }
                    case 2: {
                        check_show_notify_icon();
                        break;
                    }
                }
            }
        };
        handler.sendEmptyMessageDelayed(0, 100);
        handler.sendEmptyMessageDelayed(1, 500);
        handler.sendEmptyMessageDelayed(2, 700);
    }

    private void check_show_notify_icon() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean hide_notify = sp.getBoolean("hide_notify", false);
        if (!hide_notify) {
            // showScreenshotNotification();
        } else {
            NotificationManager mNotificationManager = (NotificationManager) SRApplication
                    .Get().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(getRXmlID("screenshot_setting"));
        }
    }

    public void onShake() {
        if (yaohuangjieping) {
            if (System.currentTimeMillis() - lastShake <= 5000) {
                return;
            }
            lastShake = System.currentTimeMillis();
            if (MainActivity.startFloatService == true) {
                sp = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                if ((RecordVideo.isStart == false && RecordVideo.isStop == true)) {
                    RecordVideo.isStart = true;
                    RecordVideo.isStop = false;
                    MainActivity.endRecord = false;
                    if (MainActivity.SDKVesion == 19) {
                        // 这里同时也兼应用第一次录屏时设置清晰度（根据android版本不同设置不同默认配置，4.4以下是标准，4.4以上是高清）
                        videoMode = sp.getString("quality_of_video", ExApplication.HQuality);

                        // TODO
                        StartRecordForVesion19();
                        // 打开其他配置
                        openOthreConfigure();
                    } else if (MainActivity.SDKVesion < 19) {
                        // 这里同时也兼应用第一次录屏时设置清晰度（根据android版本不同设置不同默认配置，4.4以下是标准，4.4以上是高清）
                        videoMode = sp.getString("quality_of_video", ExApplication.SQuality);
                        // 打开其他配置
                        openOthreConfigure();

                        on_shake_screenshot_action();

                    } else if (MainActivity.SDKVesion >= 21) {
                        // 这里同时也兼应用第一次录屏时设置清晰度（根据android版本不同设置不同默认配置，4.4以下是标准，4.4以上是高清）
                        videoMode = sp.getString("quality_of_video", ExApplication.HQuality);
                        // 打开其他配置
                        openOthreConfigure();
                        ExApplication.mConfiguration = MainActivity
                                .getConfiguration();
                        Intent intent2 = new Intent();
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.setClass(getApplicationContext(),
                                ScreenRecord50.class);
                        // Bundle bundle = new Bundle();
                        // bundle.putSerializable("ScreenRecord50", );

                        getApplicationContext().startActivity(intent2);
                    }
                    manager.Show2AndRemove1();

                    // FloatView1.handler.sendEmptyMessage(5);

                } else if (RecordVideo.isStop == false
                        && RecordVideo.isStart == true) {
                    RecordVideo.isStart = false;
                    RecordVideo.isStop = true;

                    if (MainActivity.SDKVesion == 19) {
                        StopReordForVesion19();
                        manager.Show1AndRemove2();
                    } else if (MainActivity.SDKVesion < 19) {
                        on_shake_screenshot_action();
                        manager.Show1AndRemove2();
                    } else if (MainActivity.SDKVesion >= 21) {
                        // 停止录屏，返回浮窗
                        ScreenRecord50.stopRecord();

                        ScreenRecord50.startRecoing = false;
                    }

                    // FloatView2.handler.sendEmptyMessage(5);
                    // 关闭触摸显示
                    android.provider.Settings.System.putInt(getApplication()
                            .getContentResolver(), "show_touches", 0);
                    // 关闭画中画
                    if (ExApplication.floatCameraClose == false) {
                        CameraView.closeFloatView();

                    }
                    // 关闭水印
                    // FloatView2.removeWater();
                }
            }
        }
    }

    // 录屏时打开其他配置
    private void openOthreConfigure() {
        FloatView2.doubleclick = false;
        // 将当前清晰度配置赋给全局变量
        ExApplication.videoQuality = videoMode;

        // 如果打开了设置页中的触摸选项，录屏时显示触摸点
        boolean isShowTouch = sp.getBoolean("show_touch_view", false);
        if (isShowTouch) {

            android.provider.Settings.System.putInt(getApplication()
                    .getContentResolver(), "show_touches", 1);
        }
        // 如果打开了前置摄像头选项
        boolean isfloatCamera = sp.getBoolean("show_front_camera", false);
        if (isfloatCamera) {
            // 开启画中画
            Intent intent = new Intent(mContextScreenService,
                    FrontCameraService.class);
            mContextScreenService.startService(intent);
            ExApplication.floatCameraClose = false;

        }

    }

    public void StartRecordForVesion19() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContextScreenService);
        String mode = sp.getString("quality_of_video", ExApplication.SQuality);

        if (sp.getBoolean("record_sound", true)) {
            Recorder44.isRecordAudio = true;

        } else {
            Recorder44.isRecordAudio = false;
        }

        Recorder44.StartRecordVideo(mode, mContextScreenService);
        MainActivity.endRecord = false;
        RecordVideo.isStart = true;
        RecordVideo.isStop = false;
    }

    public void StopReordForVesion19() {
        FloatView2.min = 0; // 时间清零
        FloatView2.sec = 0;
        RecordVideo.isStart = false;
        RecordVideo.isStop = true;
        Recorder44.StopRecordVideo();
    }

    private void on_shake_screenshot_action() {
        Intent intent = new Intent(RecordAction.ACTION);
        intent.putExtra("action", "screen_shot");
        sendBroadcast(intent);
    }

    private void start_yaohuangjieping_service() {
        yaohuangjieping = true;
    }

    private void end_yaohuangjieping_service() {
        yaohuangjieping = false;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void hideScreenshotNotification() {
        NotificationManager mNotificationManager = (NotificationManager) SRApplication
                .Get().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(getRXmlID("screenshot_setting"));
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        intent.setDataAndType(uri, type == null ? "*/*" : type);
        Intent it = Intent.createChooser(intent, getRString("fm_open_using"));
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }

    private File getOutputFile() {
        File localFile = new File(getImageStoreDir());
        if ((!localFile.exists()) && (!localFile.mkdirs())) {
            Log.w("RecorderService",
                    "mkdirs failed " + localFile.getAbsolutePath());
        }

        MainActivity.videofilename = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
                .format(new Date()) + ".mp4";
        return new File(localFile, MainActivity.videofilename);
    }

    private String getImageStoreDir() {
        SharedPreferences sp = SharedPreferencesUtils
                .getMinJieKaiFaPreferences(this);
        String path = sp.getString("image_store_dir",
                StoreDirUtil.getDefault(this).getAbsolutePath()).trim();
        if (path.endsWith("/"))
            return path;
        return path + "/";
    }

    private void alert_no_auth_get_pro_version() {
    }

    private void alertNoAuth() {
        Intent itt = new Intent();
        itt.putExtra("title", getRString("fm_app_tip"));

        itt.putExtra("content",
                Html.fromHtml(getRString("fm_tril_count_is_over")));

        itt.putExtra("button1", getRString("fm_get_pro_version"));

        itt.putExtra("button2", getRString("fm_cancel"));

        itt.putExtra("cancelable", false);
        itt.putExtra("button1Intent",
                RecordAction.getActionIntent("alert_no_auth_get_pro_version"));
        itt.putExtra("button2Intent",
                RecordAction.getActionIntent("cancel_alert_no_auth_action"));
        itt.setClass(ScreenRECService.this, ActivityAlertDialog.class);
        itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(itt);
    }

    private String getRString(String name) {
        return RUtils.getRString(name);
    }

    private int getRID(String name) {
        return RUtils.getRID(name);
    }

    private int getRLayout(String name) {
        return RUtils.getRLayoutID(name);
    }

    private int getRDrawableID(String name) {
        return RUtils.getRDrawableID(name);
    }

    private int getRXmlID(String name) {
        return RUtils.getRXmlID(name);
    }

    private int getRRawID(String name) {
        return RUtils.getRRawID(name);
    }

    private void floatViewControl() {

    }

}