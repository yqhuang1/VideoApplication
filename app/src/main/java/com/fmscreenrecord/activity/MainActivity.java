package com.fmscreenrecord.activity;

/**
 * 主页
 *
 * @author lin
 * Creat:2014-12
 * Refactor:2014-12
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.app.SRApplication;
import com.fmscreenrecord.floatview.FloatContentView;
import com.fmscreenrecord.frontcamera.CameraView;
import com.fmscreenrecord.record.Recorder44;
import com.fmscreenrecord.record.Settings;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.service.ScreenRECService;
import com.fmscreenrecord.utils.LaunchCheckThread;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("SdCardPath")
public class MainActivity extends Activity implements OnClickListener,
        OnTouchListener {
    public static boolean isInMain = true;

    /**
     * 横竖屏录制按钮
     */
    private ImageView btStart_horizontal, btStart_vertical;

    public static MainActivity last;
    public static MainActivity loading;
    /**
     * ROOT检查类
     */
    LaunchCheckThread launchCheckThread;

    public static boolean isFirstTimeUse = true; // 判断是否启动第一次调用这界面
    static TimerTask task; // 定时任务
    static Timer timer;
    static int times = 0;// 时间
    String HMSTimes;
    public static String path_dir = null;
    /**
     * 录制视频名称
     */
    public static String videofilename = null;
    public static int videolong = 0;
    SharedPreferences sp;
    public static boolean startRecord = false;
    public static boolean endRecord = false;
    public static boolean startFloatService = false;
    static Intent intentfloatserver;

    public static int SDKVesion = 1;
    public static Context mContext;
    // 手机分辨率_高，默认值为720
    public static int ScreenH = 720;
    // 手机分辨率—_宽，默认值为1086
    public static int ScreenW = 1086;
    public static float density;
    int msgWhat = 0;
    FrameLayout container;

    public Intent IntentSRCServerce;
    Intent notify;
    String storeDir;


    DialogInterface mDialog;
    public static Handler handler;

    Intent serviceIntent;


    /**
     * 上、下箭头
     */
    ImageView up_img, down_img;
    /**
     * 上、下箭头点击区域
     */
    LinearLayout up, down;
    /**
     * 上、下箭头动画
     */
    // Animation animation_up, animation_down;
    /**
     * 横竖屏区域
     */
    FrameLayout frameLayout1, frameLayout2;
    FrameLayout mainFramelayout;
    /**
     * 录屏时阴影遮罩
     */
    ImageView record_horizontal_shade, record_vertical_shade;
    /**
     * 录屏提示页
     */
    LinearLayout hintLinearlayout;
    /**
     * 录屏提示按钮
     */
    ImageView hintButton;
    /**
     * 两个按钮所在区域的高度
     */
    int height;
    /***/
    static LayoutParams para, para2;
    private int preY;
    public int preYFirst;
    RecordVideo recordVideo;

    /**
     * ontouch事件是否进行过移动
     */
    boolean isOnTouchMove = false;
    /**
     * 如果当前横竖屏模式正进行动画中，暂时屏蔽其他的动画点击事件
     */
    boolean isMoving = false;

    /**
     * 滑动提示文本
     */
    TextView hintTextView;

    /**
     * 退出、视频、设置 操作
     */
    ImageView cancelIv, videoIv, settingIv;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        loading = this;
        isInMain = true;
        setContentView(R.layout.fm_main_activity);

        mContext = getApplicationContext();

        // 查找页面控件
        findViews();
        // 事件监听
        setonclick();
        onResumeFirst();
        // 初始化数据
        initData();
        File store = StoreDirUtil.getDefault(this);
        if (store != null) {
            storeDir = store.getAbsolutePath();
        }
        MobclickAgent.updateOnlineConfig(this);

        IntentSRCServerce = new Intent();
        IntentSRCServerce.setClass(MainActivity.this, ScreenRECService.class);
        startService(IntentSRCServerce);

        getDisplayMetrics();

        sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());


        // 对文件储存路径是否存在进行判断
        if (StoreDirUtil.getDefault(this) != null) {
            path_dir = StoreDirUtil.getDefault(this).getPath().toString();
        } else {
            launchCheckThread.AlertDialoshow();
        }
        // 获得当前设备系统版本
        SDKVesion = getAndroidSDKVersion();

        // 获得当前设备的分辨率
        Display mDisplay = getWindowManager().getDefaultDisplay();
        ExApplication.DEVW = mDisplay.getWidth();
        ExApplication.DEVH = mDisplay.getHeight();

        Settings.width = (int) (MainActivity.ScreenW / 1.7);
        Settings.height = (int) (MainActivity.ScreenH / 1.7);

        // 获取设备识别信息（友盟测试设备用）
        // String umengDeviceInfo = MinUtil.getDeviceInfo(mContext);
        // android.util.Log.i("umengDeviceInfo", "umengDeviceInfo:"
        // + umengDeviceInfo);

        handler = new Handler() {
            @SuppressLint("Recycle")
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:

                        launchCheckThread.createCustomDialog(MainActivity.this);
                        break;
                    case 2:

                        launchCheckThread.dialog_Root(MainActivity.this);
                        break;
                    case 3:
                        frameLayout1.setLayoutParams(para);
                        para2.height = height - para.height;
                        if (para2.height < 0) {
                            para2.height = 0;
                        }
                        frameLayout2.setLayoutParams(para2);
                        break;
                    case 4:
                        frameLayout2.setLayoutParams(para2);
                        para.height = height - para2.height;
                        if (para.height <= 0) {
                            para.height = 1;
                        }
                        frameLayout1.setLayoutParams(para);
                        break;
                    case 5:

                        break;
                    case 6:

                        break;

                    case 7:

                        // TODO
//                        frameLayout1.onTouchEvent(MotionEvent.obtain(
//                                SystemClock.uptimeMillis(),
//                                SystemClock.uptimeMillis(),
//                                MotionEvent.ACTION_DOWN, 200, 200, 0));
//                        frameLayout1.dispatchTouchEvent(MotionEvent.obtain(
//                                SystemClock.uptimeMillis() + 1000,
//                                SystemClock.uptimeMillis(),
//                                MotionEvent.ACTION_MOVE, 200, 250, 0));

                        break;
                    case 8:
                        // frameLayout1.dispatchTouchEvent(MotionEvent.obtain(
                        // SystemClock.uptimeMillis() + 1000,
                        // SystemClock.uptimeMillis(),
                        // MotionEvent.ACTION_MOVE, 200, 200, 0));
                        break;
                }

            }
        };

        // UMENG调试模式
        // MobclickAgent.setDebugMode(true);

        // 检测关闭前置摄像头
        if (ExApplication.floatCameraClose == false) {
            CameraView.closeFloatView();
        }

        // 当开发者回复用户反馈后，推送提醒用户
        FeedbackAgent agent = new FeedbackAgent(mContext);
        agent.sync();

        // 初始化录屏方法类
        recordVideo = new RecordVideo(mContext);

        iniViewState();

//        if (null == bitmap) {
//            new AsyncTask<Integer, Void, Void>() {
//
//                @Override
//                protected void onPreExecute() {
////                    Intent videoIntent = getIntent();
////                    if (videoIntent != null) {
////                        byte buff[] = (byte[]) videoIntent.getSerializableExtra("bitmap");
////                        bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);//重新编码出Bitmap对象
////                    }
//                    bitmap = MyBitmapStore.getBmp();
//                }
//
//                @Override
//                protected Void doInBackground(Integer... params) {
//                    try {
//                        //这里进行模糊化图片是个耗时操作，建议在项目中放到非UI线程去做
//                        if (bitmap != null) {
//                            blur(bitmap, mainFramelayout);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void vid) {
//                }
//
//            }.execute(0);
//        }

    }

    private Bitmap bitmap;


    protected void onResumeFirst() {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {

                    // 把录制核心拷贝到包名目录下(针对4.4)
                    if (SDKVesion < 21 && SDKVesion >= 19) {
                        cpRecrodCore();
                    }

                    Thread.sleep(700);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void vid) {

            }

        }.execute(0);
    }

    int copytimes = 0;

    /**
     * 将文件拷贝至data下的应用文件夹内
     */
    void cpRecrodCore() {

        if (copytimes == 0) {

            copyFile("fmNewCore", R.raw.screenrecord1111);
        } else if (copytimes == 1) {

            copyFile("busybox", R.raw.busybox);
        } else if (copytimes == 2) {

            copyFile("fmOldCore", R.raw.screenrecord10272);
        }


        if (copytimes == 0) {

            Recorder44
                    .StartRecord("chmod 777 /data/data/com.li.videoapplication/files/fmNewCore");

        } else if (copytimes == 1) {

            Recorder44
                    .StartRecord("chmod 777 /data/data/com.li.videoapplication/files/busybox");

        } else if (copytimes == 2) {

            Recorder44
                    .StartRecord("chmod 777 /data/data/com.li.videoapplication/files/fmOldCore");

        }
        copytimes++;
        if (copytimes < 3) {
            cpRecrodCore();
        }

    }

    /**
     * 将文件拷贝至data下的应用文件夹内
     *
     * @param FileName
     * @param Raw
     */
    private void copyFile(String FileName, int Raw) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File file = new File("/data/data/com.li.videoapplication/files/"
                    + FileName);
            is = mContext.getResources().openRawResource(Raw);

            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {

            e1.printStackTrace();
        }
        byte[] buffer = new byte[1024];
        int count = 0;

        try {
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void initData() {
        // 如果不是5.0以上系统，进行ROOT检测
        if (SDKVesion < 21) {
            // 子线程加载root提示对话框 防止对话框阻塞主线程
            launchCheckThread = new LaunchCheckThread(MainActivity.this);
            launchCheckThread.start();
        }

    }

    /**
     * 初始化控件状态
     */
    private void iniViewState() {
        // 如果是横屏录屏并且处于录屏中
        if (sp.getBoolean("horizontalRecord", false)
                && RecordVideo.isRecordering) {
            if (SDKVesion >= 19) {
                btStart_horizontal.setVisibility(View.GONE);
            } else {

            }
            record_vertical_shade.setVisibility(View.VISIBLE);
        } else if (!sp.getBoolean("horizontalRecord", false)
                && RecordVideo.isRecordering) {
            if (SDKVesion >= 19) {
                btStart_vertical.setVisibility(View.GONE);
            } else {

            }
            record_horizontal_shade.setVisibility(View.VISIBLE);
        }

    }

    // 首次启动应用显示遮罩层
    private void showMasked() {
        // 如果没有显示过遮罩层
        if (sp.getBoolean("showMaskedInMain", true)) {

            hintLinearlayout.setVisibility(View.VISIBLE);
            hintTextView.setVisibility(View.VISIBLE);
            sp.edit().putBoolean("showMaskedInMain", false).commit();

        }
    }

    private void setonclick() {
        hintButton.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        up.setOnTouchListener(this);
        down.setOnTouchListener(this);

        btStart_horizontal.setOnClickListener(this);
        btStart_vertical.setOnClickListener(this);

        hintButton.setOnClickListener(this);

        cancelIv.setOnClickListener(this);
        videoIv.setOnClickListener(this);
        settingIv.setOnClickListener(this);

        OnTouchListener onTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 将点击事件进行吞没，不传递到下一层
                return true;
            }
        };
        hintLinearlayout.setOnTouchListener(onTouchListener);
        record_vertical_shade.setOnTouchListener(onTouchListener);
        record_horizontal_shade.setOnTouchListener(onTouchListener);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        para = frameLayout1.getLayoutParams();
        para.height = frameLayout1.getHeight();
        para2 = frameLayout2.getLayoutParams();
        para2.height = frameLayout2.getHeight();

        // 获取应用区域高度
        height = para.height + para2.height;
        // 提示遮罩层
        showMasked();

    }

    /**
     * 查找控件
     */
    @SuppressLint("ResourceAsColor")
    private void findViews() {
        container = (FrameLayout) findViewById(MResource.getIdByName(
                getApplication(), "id", "container"));
        btStart_horizontal = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "main_record_server_bt_horizontal"));
        btStart_vertical = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "main_record_server_bt_vertical"));

        // 箭头图片
        up_img = (ImageView) findViewById(R.id.up_img);
        down_img = (ImageView) findViewById(R.id.down_img);
        // 箭头按钮
        up = (LinearLayout) findViewById(R.id.up_button);
        down = (LinearLayout) findViewById(R.id.down_button);
        hintTextView = (TextView) findViewById(R.id.fm_main_hint_text);

        frameLayout1 = (FrameLayout) findViewById(R.id.frameLayout1);
        frameLayout2 = (FrameLayout) findViewById(R.id.frameLayout2);
        mainFramelayout = (FrameLayout) findViewById(R.id.fm_main_record_server_framelayout);
        hintLinearlayout = (LinearLayout) findViewById(R.id.fm_main_hint_linearlayout);

        hintButton = (ImageView) findViewById(R.id.fm_main_hint_button);

        // 录屏阴影遮罩
        record_vertical_shade = (ImageView) findViewById(R.id.fm_main_record_vertical_shade);
        record_horizontal_shade = (ImageView) findViewById(R.id.fm_main_record_horizontal_shade);

        cancelIv = (ImageView) findViewById(R.id.fm_main_cancel);
        videoIv = (ImageView) findViewById(R.id.fm_main_video);
        settingIv = (ImageView) findViewById(R.id.fm_main_setting);
    }

    /**
     * 获取手机分辨率 *
     */
    public static void getDisplayMetrics() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        ScreenW = dm.widthPixels;
        ScreenH = dm.heightPixels;
        density = dm.density;

    }

    /**
     * 判断手机屏幕方向 横屏返回true,竖屏返回false *
     */
    public static boolean getConfiguration() {
        Configuration config = mContext.getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isMoving && !RecordVideo.isRecordering) {
            myOnTouchEvent(event);
        }
        return true;
    }

    /**
     * 触摸事件动画
     *
     * @param event
     */
    private void myOnTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                preYFirst = (int) event.getRawY();
                para.height = frameLayout1.getHeight();
                para2.height = frameLayout2.getHeight();
                height = para.height + para2.height;

                break;
            case MotionEvent.ACTION_MOVE:

                preY = (int) event.getRawY();

                if (preYFirst < preY) {// 向下滑

                    // 如果linearlayout1d的高度不超过屏幕高度
                    if (para.height < height) {

                        para.height = para.height + (preY - preYFirst);
                        frameLayout1.setLayoutParams(para);

                        para2.height = para2.height - (preY - preYFirst);
                        if (para2.height < 0) {
                            para2.height = 0;
                        }
                        frameLayout2.setLayoutParams(para2);

                    }

                } else if (preYFirst > preY) {

                    if (para2.height < height) {// 向上滑

                        para2.height = para2.height + (preYFirst - preY);
                        frameLayout2.setLayoutParams(para2);
                        para.height = para.height - (preYFirst - preY);
                        if (para.height < 0) {
                            para.height = 0;
                        }
                        frameLayout1.setLayoutParams(para);

                    }

                }

                preYFirst = preY;
                break;
            case MotionEvent.ACTION_UP:

                // 如果para的高度大于屏幕高度的70%，则向下递增高度
                if (para.height > (height * 0.7)) {// 横屏录屏按钮全屏
                    // 如果para.height超过了应用高度
                    if (para.height >= height) {
                        // 重新设定高度
                        para.height = frameLayout1.getHeight();

                    }
                    new Thread() {
                        public void run() {

                            // 当前linearyout与屏幕的高度差，作为循环的次数
                            int mHeight = (height - para.height) / 2;

                            for (int i = 0; i < mHeight; i++) {
                                try {
                                    // 循环遍历，模拟移动下滑动画
                                    para.height = para.height + 2;
                                    handler.sendEmptyMessage(3);
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {

                                    e.printStackTrace();
                                }

                            }
                        }
                    }.start();

                } else if (para2.height > (height * 0.7)) {// 竖屏录屏按钮全屏

                    // 如果para.height超过了应用高度
                    if (para2.height >= height) {
                        // 重新设定高度
                        para2.height = frameLayout2.getHeight();

                    }
                    new Thread() {
                        public void run() {
                            int mHeight = (height - para2.height) / 2;

                            for (int i = 0; i < mHeight; i++) {
                                try {

                                    para2.height = para2.height + 2;
                                    handler.sendEmptyMessage(4);
                                    Thread.sleep(1);

                                } catch (InterruptedException e) {

                                    e.printStackTrace();
                                }

                            }
                        }
                    }.start();

                } else {// 横屏，录屏占据二分之一

                    new Thread() {
                        public void run() {
                            // 计算para与屏幕二分之一高度的高度差
                            int mHeight = para.height - (height / 2);
                            // 将高度差绝对值化
                            int abs = Math.abs(mHeight);
                            if (mHeight > 0) {
                                for (int i = 0; i < abs; i++) {
                                    try {
                                        Thread.sleep(1);
                                        para.height = para.height - 1;
                                        handler.sendEmptyMessage(3);

                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }

                                }
                            } else {
                                for (int i = 0; i < abs; i++) {
                                    try {
                                        Thread.sleep(1);
                                        para.height = para.height + 1;
                                        handler.sendEmptyMessage(3);

                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }

                                }

                            }
                        }
                    }.start();

                }
                break;
        }

    }

    @Override
    public void onClick(View v) {

        if (v == btStart_horizontal) {
            record_horizontal();

        } else if (v == btStart_vertical) {
            record_vertical();

        } else if (v == up) {

            if (!isOnTouchMove && !isMoving && !RecordVideo.isRecordering) {

                isMoving = true;
                para.height = frameLayout1.getHeight();
                para2.height = frameLayout2.getHeight();
                height = para.height + para2.height;

                final int mHeight = (height - para2.height) / 2;

                new Thread() {
                    public void run() {

                        for (int i = 0; i < mHeight; i++) {
                            try {
                                Thread.sleep(1);
                                para2.height = para2.height + 2;
                                handler.sendEmptyMessage(4);

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }

                        }
                        isMoving = false;
                    }
                }.start();

            } else {
                isOnTouchMove = false;
            }
        } else if (v == down) {

            if (!isOnTouchMove && !isMoving && !RecordVideo.isRecordering) {

                isMoving = true;
                para.height = frameLayout1.getHeight();
                para2.height = frameLayout2.getHeight();
                height = para.height + para2.height;

                // 当前linearyout与屏幕的高度差，作为循环的次数
                final int mHeight = (height - para.height) / 2;

                new Thread() {
                    public void run() {

                        for (int i = 0; i < mHeight; i++) {
                            try {

                                Thread.sleep(1);
                                // 循环遍历，模拟移动下滑动画
                                para.height = para.height + 2;
                                handler.sendEmptyMessage(3);

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }

                        }

                        isMoving = false;
                    }

                }.start();
            } else {
                isOnTouchMove = false;
            }
        } else if (v == hintButton) {// 隐藏遮罩层
            final Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.zoom_exit);
            // 隐藏遮罩层
            hintLinearlayout.startAnimation(animation);
            hintLinearlayout.setVisibility(View.GONE);
            // TODO
            setSimulateClick(up, 200, 200);
            TimerTask task = new TimerTask() {

                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            hintTextView.startAnimation(animation);
                            hintTextView.setVisibility(View.GONE);
                        }
                    });

                }
            };
            // 五秒后隐藏文字提示框
            Timer timer = new Timer();
            timer.schedule(task, 5000);
        } else if (v == cancelIv) {
            this.finish();
        } else if (v == videoIv) {
            Intent intentVideo = new Intent(this, VideoManagerActivity.class);
            intentVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentVideo);
        } else if (v == settingIv) {
            Intent intentVideo = new Intent(this, SettingActivity.class);
            intentVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentVideo);
        }

    }

    // TODO

    @SuppressLint("Recycle")
    private void setSimulateClick(View view, float x, float y) {
        // handler.sendEmptyMessageDelayed(7, 1000);
        // handler.sendEmptyMessageDelayed(8, 1500);
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis() + 5000,
                SystemClock.uptimeMillis() + 5000, MotionEvent.ACTION_DOWN, 200, 200,
                0));
        view.dispatchTouchEvent(MotionEvent.obtain(
                SystemClock.uptimeMillis() + 5000, SystemClock.uptimeMillis() + 5000,
                MotionEvent.ACTION_MOVE, 200, 250, 0));
    }


    /**
     * 竖屏录制方法
     */
    private void record_vertical() {
        MinUtil.upUmenEventValue(mContext, "竖屏录制", "bt_record_ver_hor");
        boolean str = false;
        str = sp.getBoolean("FirstTimeUse", true);
//        // 是否进行无悬浮窗录制
//        boolean floatview = false;
//        floatview = sp.getBoolean("show_float_view", false);
        // HUAWEI,Xiaomi在首次启动时，开浮窗提醒
        if (str
                && (launchCheckThread.getManufacturer().equals("HUAWEI") || launchCheckThread
                .getManufacturer().equals("MIUI"))) {
            Editor editor = sp.edit();
            editor.putBoolean("FirstTimeUse", false);
            editor.commit();
            launchCheckThread.tipManufacturer();
        } else {
            boolean notify = sp.getBoolean("PackageInfoGridviewNotify", false);
            sp.edit().putBoolean("horizontalRecord", false).commit();
            // 是否已经勾选不再弹出应用列表
            if (!notify) {
                PackageInfoGridview.isInPackageInfo = false;

                // 直接打开浮窗
                intentfloatserver = new Intent();
                intentfloatserver.setClass(MainActivity.this, FloatViewService.class);
                startService(intentfloatserver);

                startFloatService = true;

                // 退出到桌面
//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);
                com.li.videoapplication.activity.MainActivity.ActivityMain.finish();
                MainActivity.this.finish();

                isInMain = false;
                FloatContentView.isFirstTimeUse = true;

            } else {// 打开第三方应用列表
                MinUtil.showToast(mContext, "正在加载应用列表，请稍候...");
                Intent intent = new Intent(this, PackageInfoGridview.class);
                startActivity(intent);

                startFloatService = true;
                MainActivity.this.finish();
                isInMain = false;
                FloatContentView.isFirstTimeUse = true;
            }

        }

    }

    /**
     * 横屏录制方法
     */
    private void record_horizontal() {
        MinUtil.upUmenEventValue(mContext, "横屏录制", "bt_record_ver_hor");

        boolean str = false;
        str = sp.getBoolean("FirstTimeUse", true);
//        // 是否进行无悬浮窗录制
//        boolean floatview = false;
//        floatview = sp.getBoolean("show_float_view", false);

        // HUAWEI,Xiaomi在首次启动时，开浮窗提醒
        if (str
                && (launchCheckThread.getManufacturer().equals("HUAWEI") || launchCheckThread
                .getManufacturer().equals("MIUI"))) {
            Editor editor = sp.edit();
            editor.putBoolean("FirstTimeUse", false);
            editor.commit();
            launchCheckThread.tipManufacturer();
        } else {
            boolean notify = sp.getBoolean("PackageInfoGridviewNotify", false);
            // 将横屏录制配置写入SP
            sp.edit().putBoolean("horizontalRecord", true).commit();
            // 是否已经勾选不再弹出应用列表
            if (!notify) {
                PackageInfoGridview.isInPackageInfo = false;

                // 直接打开浮窗
                intentfloatserver = new Intent();
                intentfloatserver.setClass(MainActivity.this, FloatViewService.class);
                startService(intentfloatserver);

                startFloatService = true;

//                // 退出到桌面
//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);
                com.li.videoapplication.activity.MainActivity.ActivityMain.finish();
                MainActivity.this.finish();

                isInMain = false;
                FloatContentView.isFirstTimeUse = true;

            } else {// 打开第三方应用列表
                MinUtil.showToast(mContext, "正在加载应用列表，请稍候...");
                Intent intent = new Intent(this, PackageInfoGridview.class);
                startActivity(intent);

                startFloatService = true;
                MainActivity.this.finish();
                isInMain = false;
                FloatContentView.isFirstTimeUse = true;
            }

        }
    }

    // 输入命令
    public static void runCommand(String command) {
        try {

            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(command + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            DataInputStream dis = new DataInputStream(p.getInputStream());
            DataInputStream des = new DataInputStream(p.getErrorStream());
            while (dis.available() > 0)
                Log.d("SC", "stdout: " + dis.readLine() + "\n");
            while (des.available() > 0)
                Log.d("SC", "stderr: " + des.readLine() + "\n");
            // Log.d("SC", "return: " + p.exitValue());
        } catch (Exception e) {
            // Log.d("SC", "exception: " + e);
        }
    }

    public static int getAndroidSDKVersion() {
        int version = 1;
        try {
            version = Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            // Log.e(e.toString());
        }
        return version;
    }

    /**
     * 退出录屏大师
     */
    public void exitProgrames() {
        // 取消所有的通知
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) SRApplication.Get()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        // 终止录制服务
        SRApplication.Get().stopService(
                new Intent(SRApplication.Get(), ScreenRECService.class));

        if (ExApplication.isCompositionVideo == false) {
            // 终止浮窗服务
            SRApplication.Get().stopService(
                    new Intent(SRApplication.Get(), FloatViewService.class));

        } else {
            // 待合成后再关闭录屏大师
            ExApplication.islaterCloaseApp = true;
        }
        // 退出到桌面
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

        if (ExApplication.isCompositionVideo == false) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    public boolean runInBack() {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(
                new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME), 0);
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startActivitySafely(startIntent);
        return true;
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_HOME) {
//            if (!RecordVideo.isRecordering) {
//                isInMain = false;
//                finish();
//            }
//        }
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            ExitApp();
//
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }

    private long exitTime = 0;

    /**
     * 双击退出函数
     */
    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {

        } else {

        }
    }

    @Override
    protected void onDestroy() {

        isInMain = false;
        loading = null;
        com.li.videoapplication.activity.MainActivity.viewHandler.sendEmptyMessage(4);

        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        isInMain = true;
        MobclickAgent.onResume(this);
        iniViewState();
        para = frameLayout1.getLayoutParams();
        para2 = frameLayout2.getLayoutParams();

    }

    @Override
    public void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);

    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isMoving && !RecordVideo.isRecordering) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    preYFirst = (int) event.getRawY();
                    para.height = frameLayout1.getHeight();
                    para2.height = frameLayout2.getHeight();
                    break;
                case MotionEvent.ACTION_MOVE:

                    preY = (int) event.getRawY();
                    // preYFirst、preY之间的差值必须小于或者大于阀值，不然，onclik的上移下移按钮会失效
                    if ((preYFirst - preY) < 0) {// 向下滑

                        isOnTouchMove = true;
                        // 如果linearlayout1d的高度不超过屏幕高度
                        if (para.height < height) {
                            para.height = para.height + (preY - preYFirst) * 2;
                            frameLayout1.setLayoutParams(para);
                            para2.height = para2.height - (preY - preYFirst) * 2;
                            frameLayout2.setLayoutParams(para2);

                        }
                    } else if ((preYFirst - preY) > 2) {

                        isOnTouchMove = true;
                        if (para2.height < height) {// 向上滑

                            para2.height = para2.height + (preYFirst - preY) * 2;
                            frameLayout2.setLayoutParams(para2);
                            para.height = para.height - (preYFirst - preY) * 2;
                            frameLayout1.setLayoutParams(para);
                        }

                    }
                    preYFirst = preY;
                    break;
                case MotionEvent.ACTION_UP:
                    para.height = frameLayout1.getHeight();
                    para2.height = frameLayout2.getHeight();
                    // 如果para的高度大于屏幕高度的70%，则向下递增高度
                    if (para.height > (height * 0.7)) {// 横屏录屏按钮全屏
                        // 如果para.height超过了应用高度
                        if (para.height >= height) {
                            // 重新设定高度
                            para.height = frameLayout1.getHeight();
                        }
                        new Thread() {
                            public void run() {

                                // 当前linearyout与屏幕的高度差，作为循环的次数
                                int mHeight = (height - para.height) / 2;

                                for (int i = 0; i < mHeight; i++) {
                                    try {

                                        // 循环遍历，模拟移动下滑动画
                                        para.height = para.height + 2;
                                        handler.sendEmptyMessage(3);
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }

                                }
                            }
                        }.start();

                    }
                    // 如果para2的高度大于屏幕高度的70%，则向上递增高度
                    else if (para2.height > (height * 0.7)) {// 竖屏录屏按钮全屏

                        // 如果para.height超过了应用高度
                        if (para2.height >= height) {
                            // 重新设定高度
                            para2.height = frameLayout2.getHeight();
                        }
                        new Thread() {
                            public void run() {
                                int mHeight = (height - para2.height) / 2;
                                for (int i = 0; i < mHeight; i++) {
                                    try {

                                        para2.height = para2.height + 2;
                                        handler.sendEmptyMessage(4);
                                        Thread.sleep(1);

                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }

                                }

                            }
                        }.start();

                    }
                    // 如果有触发ACTION_MOVE则进行以下操作
                    else if (isOnTouchMove) {// 横屏，录屏占据二分之一

                        new Thread() {
                            public void run() {
                                // 计算para与屏幕二分之一高度的高度差
                                int mHeight = para.height - (height / 2);
                                // 将高度差绝对值化
                                int abs = Math.abs(mHeight);
                                if (mHeight > 0) {
                                    for (int i = 0; i < abs; i++) {
                                        try {
                                            Thread.sleep(1);
                                            para.height = para.height - 1;
                                            handler.sendEmptyMessage(3);

                                        } catch (InterruptedException e) {

                                            e.printStackTrace();
                                        }

                                    }
                                } else {
                                    for (int i = 0; i < abs; i++) {
                                        try {

                                            Thread.sleep(1);
                                            para.height = para.height + 1;
                                            handler.sendEmptyMessage(3);

                                        } catch (InterruptedException e) {

                                            e.printStackTrace();
                                        }

                                    }

                                }

                            }
                        }.start();

                    }
                    break;
            }

        }
        return false;
    }
}
