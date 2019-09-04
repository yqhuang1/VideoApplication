package com.fmscreenrecord.activity;

/**
 * 设置页
 *
 * @author lin
 * Creat:2014-12
 * Refactor:2014-12
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.record.RecordAction;
import com.fmscreenrecord.service.ScreenRECService;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.OpenFloatView;
import com.fmscreenrecord.utils.RUtils;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.fmscreenrecord.utils.ViewUtils;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SettingActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener {
    public static SettingActivity last2;
    long onPreferenceTreeClickTime = 0;
    private HomeKeyEventBroadCastReceiver receiver;

    private SharedPreferences sharedPreferences;
    boolean isDeviceRooted = true;
    boolean isSupportDevice = true;
    boolean isAuthed = true;
    String storeDir;
    boolean isFirstResume = true;
    volatile static boolean isShowNotify = false;

    private List<Activity> activityList = new LinkedList<Activity>();
    private static SettingActivity instance;
    private Context mContext;
    private ImageButton back;

    // 声音录制checkbox
    private CheckBox recordSound;
    // 摇晃录屏
    private CheckBox yaHangJiePing;
    // 显示悬浮框
    private CheckBox showFloatView;
    // 显示触摸位置
    private CheckBox showTouch;
    // 前置摄像头
    private CheckBox frontCamera;
    // 是否显示游戏列表
    private CheckBox showGameList;

    // 录屏后跳转到视频管理
    private CheckBox gotoVideoManage;
    // 画质选择
    private LinearLayout qualityOfVideo;
    // 储存路径
    // private LinearLayout storeDirLinear;
    // 显示当前清晰度
    private TextView qualityTextview;

    // 开启悬浮窗
    private LinearLayout openfloat;

    public static AlarmManager alarmManager;

    // 手机操作系统
    String manufacturer;

    // 接受消息推送勾选框
    private CheckBox PushNotification;

    public static PendingIntent sender;

    private OpenFloatView openFloatView;

    // 检查服务器最新版本
    // UpdateTask updateTask;

    // 录屏大师
    private String SRUrl = "http://m.ifeimo.com/product.php";
    private String iconUrl = "http://apps.ifeimo.com/Public/Uploads/Game/Flag/icon.png";

    // 构造函数
    public SettingActivity() {
        // Log.i(TAG, "Instantiated new " + this.getClass());
    }

    // 单例模式中获取唯一的MyApplication实例
    public static SettingActivity getInstance() {
        if (null == instance) {
            instance = new SettingActivity();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    public void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);

        last2 = SettingActivity.this;
        mContext = SettingActivity.this;

        setContentView(MResource.getIdByName(getApplication(), "layout",
                "fm_preference_list"));

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        openFloatView = new OpenFloatView();

        // 查找页面控件
        findViews();
        // 注册监听
        setOnClick();
        // 初始化页面数据
        initData();

        startService();
        receiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        File store = StoreDirUtil.getDefault(this);
        if (store != null) {
            storeDir = store.getAbsolutePath();
        }

        showScreenshotNotification();
    }

    private void findViews() {

        //退出设置页面，返回手游视界
        back = (ImageButton) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_back"));
        // 录制声音
        recordSound = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_record_sound_checkbox"));
        // 摇晃录屏
        yaHangJiePing = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_yahangjieping_checkbox"));
        // 显示悬浮框
        showFloatView = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_show_float_view_checkbox"));
        // 显示触摸位置
        showTouch = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_showptouch_checkbox"));
        // 显示触摸位置
        frontCamera = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_show_front_camera"));
        // 视频清晰度
        qualityOfVideo = (LinearLayout) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_quality_of_video_linearlayout"));
        qualityTextview = (TextView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_quality_of_video_textview"));
        // 显示游戏列表
        showGameList = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_gamelistnotify_checkbox"));

        // 开启悬浮窗
        openfloat = (LinearLayout) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_open_floatview"));
        // 录屏后跳转到视频管理
        gotoVideoManage = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_recordgoto_videomanage"));
        // 迁移存储路径
        // storeDirLinear = (LinearLayout) findViewById(MResource.getIdByName(
        // getApplication(), "id", "fm_image_store_dir_linearlayout"));

        // 获取消息推送勾选框
        PushNotification = (CheckBox) findViewById(MResource.getIdByName(
                getApplication(), "id", "checkbox_Push_Notification"));
    }

    private void setOnClick() {
        back.setOnClickListener(this);
        recordSound.setOnCheckedChangeListener(this);
        yaHangJiePing.setOnCheckedChangeListener(this);
        showFloatView.setOnCheckedChangeListener(this);
        showTouch.setOnCheckedChangeListener(this);
        frontCamera.setOnCheckedChangeListener(this);
        gotoVideoManage.setOnCheckedChangeListener(this);
        showGameList.setOnCheckedChangeListener(this);

        // 消息推送checkbox监听
        PushNotification.setOnCheckedChangeListener(this);

        qualityOfVideo.setOnClickListener(this);
        openfloat.setOnClickListener(this);

    }

    /**
     * 初始化各个控件显示的值
     */
    private void initData() {
        // 判断手机操作系统
        manufacturer = openFloatView.getManufacturer(mContext);
        // 如果是miui系统或者华为系统，显示悬浮窗选项
        if (manufacturer.equals("MIUI") || manufacturer.equals("HUAWEI")) {
            openfloat.setVisibility(View.VISIBLE);
        } else {
            openfloat.setVisibility(View.GONE);
        }
        // 录制声音
        boolean recoudsound = sharedPreferences
                .getBoolean("record_sound", true);
        recordSound.setChecked(recoudsound);
        // 摇晃录屏
        boolean yahuan = sharedPreferences.getBoolean("yahangjieping", false);
        yaHangJiePing.setChecked(yahuan);
        // 显示浮窗
        boolean showfloat = sharedPreferences.getBoolean("show_float_view",
                true);
        showFloatView.setChecked(showfloat);
        // 触摸位置
        boolean showtouch = sharedPreferences.getBoolean("show_touch_view",
                false);
        showTouch.setChecked(showtouch);
        // 前摄像头
        boolean showcamera = sharedPreferences.getBoolean("show_front_camera",
                false);
        frontCamera.setChecked(showcamera);
        // 扫描游戏列表
        boolean showgamelist = sharedPreferences.getBoolean(
                "PackageInfoGridviewNotify", false);
        showGameList.setChecked(showgamelist);

        // 跳转到视频管理
        boolean showvideomanage = sharedPreferences.getBoolean(
                "isGotoVideoManage", true);
        gotoVideoManage.setChecked(showvideomanage);

        // 清晰度
        String quality = "";
        if (MainActivity.SDKVesion >= 19) {
            quality = sharedPreferences.getString("quality_of_video", "0");
        } else {

            quality = sharedPreferences.getString("quality_of_video", "1");
        }
        // 将2.0.1.1及之前版本的清晰度修改成最新版本
        if (quality.equals("高清")) {
            quality = "0";
            sharedPreferences.edit()
                    .putString("quality_of_video", ExApplication.HQuality)
                    .commit();
        } else if (quality.equals("标准")) {
            quality = "1";
            sharedPreferences.edit()
                    .putString("quality_of_video", ExApplication.HQuality)
                    .commit();
        } else if (quality.equals("流畅")) {
            sharedPreferences.edit()
                    .putString("quality_of_video", ExApplication.HQuality)
                    .commit();
            quality = "0";
        }

        if (quality.equals("0")) {
            qualityTextview.setText(ExApplication.HQualityVaule);
        } else {
            qualityTextview.setText(ExApplication.SQualityVaule);
        }


        // 获取到消息推送勾选框的布尔值
        boolean mboolean = sharedPreferences.getBoolean("isPushNotification",
                false);
        // 根据布尔值设置勾选框状态
        if (mboolean == true) {
            PushNotification.setChecked(true);

        } else {
            PushNotification.setChecked(false);

        }

    }

    private void startService() {
        // UtilService.startService(SettingActivity.this);
        mContext.startService(new Intent(mContext, ScreenRECService.class));
    }

    private void endYaohuangjiepingService() {
        startService();
        Intent intent = new Intent(RecordAction.ACTION);
        intent.putExtra("action", "end_yaohuangjieping_service");
        sendBroadcast(intent);
    }

    private void startYaohuangjiepingService() {
        startService();
        Intent intent = new Intent(RecordAction.ACTION);
        intent.putExtra("action", "start_yaohuangjieping_service");
        sendBroadcast(intent);
    }

    // 显示通知栏
    private void showScreenshotNotification() {
        if (!isShowNotify) {
            isShowNotify = true;
            startService();
            Intent intent = new Intent(RecordAction.ACTION);
            intent.putExtra("action", "show_screenshot_notification");
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        last2 = null;
        unregisterReceiver(receiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("hideStatusPanel", false)) {
            ViewUtils.hideStatusPanel(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            // onCreateActivity();
        }
        SharedPreferences sp = SharedPreferencesUtils
                .getMinJieKaiFaPreferences(this);

    }

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";// home key
        static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null) {
                    if (reason.equals(SYSTEM_HOME_KEY)) {
                        SettingActivity.this.finish();
                    } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                    }
                }
            }
        }
    }

    public String getString(String name) {
        return RUtils.getRString(name);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            if (!RecordVideo.isRecordering && MainActivity.last == null) {
                // FloatView1.handler.sendEmptyMessage(4);
                MainActivity.isInMain = false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        if (v == back) {
            this.finish();

        } else if (v == qualityOfVideo) { // 清晰度选择监听
            dialogQualityOfVideo();

        } else if (v == openfloat) {// 开启悬浮窗

            if (manufacturer.equals("MIUI")) {
                openFloatView.showInstalledAppDetails(mContext,
                        getPackageName());
            } else if (manufacturer.equals("HUAWEI")) {

                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                try {

                    intent.setClassName("com.huawei.systemmanager",
                            "com.huawei.systemmanager.SystemManagerMainActivity");
                    startActivity(intent);
                } catch (Exception e) {
                    try {
                        intent.setClassName("com.huawei.systemmanager",
                                "com.huawei.systemmanager.mainscreen.MainScreenActivity");
                        startActivity(intent);
                    } catch (Exception ee) {
                        MinUtil.showToast(mContext, "抱歉，您当前的系统版本需要手动打开浮窗");
                    }
                }

            }
        }
    }

    public void exitProgrames() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        android.os.Process.killProcess(android.os.Process.myPid());
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

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    }


    /**
     * 勾选框监听事件
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == recordSound) {// 声音选项
            if (isChecked) {//
                sharedPreferences.edit().putBoolean("record_sound", true)
                        .commit();

            } else {
                sharedPreferences.edit().putBoolean("record_sound", false)
                        .commit();
                MinUtil.upUmenEventValue(mContext, "关闭声音录制次数",
                        "fmSettingPageCount");
            }

        } else if (buttonView == yaHangJiePing) {
            if (isChecked) {// 摇晃截屏
                startYaohuangjiepingService();
                sharedPreferences.edit().putBoolean("yahangjieping", true)
                        .commit();
                MinUtil.upUmenEventValue(mContext, "开启摇晃录屏次数",
                        "fmSettingPageCount");
            } else {
                endYaohuangjiepingService();
                sharedPreferences.edit().putBoolean("yahangjieping", false)
                        .commit();
            }

        } else if (buttonView == showFloatView) {
            if (isChecked) {// 显示悬浮框
                sharedPreferences.edit().putBoolean("show_float_view", true)
                        .commit();

            } else {
                sharedPreferences.edit().putBoolean("show_float_view", false)
                        .commit();
                MinUtil.upUmenEventValue(mContext, "关闭悬浮窗次数",
                        "fmSettingPageCount");
            }

        } else if (buttonView == PushNotification) {// 推送选项

            // 根据checkbox勾选状态设定isPushNotification的布尔值
            if (isChecked == true) {
                sharedPreferences.edit().putBoolean("isPushNotification", true)
                        .commit();
                //
                // // 随机参数一个86400000到86400000*3(一到三天)之间的随机数
                // int minTime = 86400000;
                // int maxTime = 86400000 * 2;
                // Random rand = new Random();
                // int randNum = rand.nextInt(maxTime) + minTime;
                // long systemTime = System.currentTimeMillis();
                // // 将推送时间加上系统时间，作为系统闹铃启动的绝对时间
                // sharedPreferences.edit().putLong("PushTime",
                // randNum + systemTime);
                // sharedPreferences.edit().commit();

                // 开启闹铃
                // setAlarmTime(this, randNum);
                MinUtil.upUmenEventValue(mContext, "接收推送", "fmPush");
            } else {
                sharedPreferences.edit()
                        .putBoolean("isPushNotification", false).commit();

            }

        } else if (buttonView == showTouch) {// 触摸选项
            if (isChecked == true) {
                sharedPreferences.edit().putBoolean("show_touch_view", true)
                        .commit();
                MinUtil.upUmenEventValue(mContext, "开启显示触摸位置次数",
                        "fmSettingPageCount");
            } else {
                sharedPreferences.edit().putBoolean("show_touch_view", false)
                        .commit();
            }

        } else if (buttonView == frontCamera) {// 前摄像头选项
            if (isChecked == true) {
                sharedPreferences.edit().putBoolean("show_front_camera", true)
                        .commit();
                MinUtil.upUmenEventValue(mContext, "开启前置摄像头次数",
                        "fmSettingPageCount");

            } else {
                sharedPreferences.edit().putBoolean("show_front_camera", false)
                        .commit();
            }

        } else if (buttonView == showGameList) {// 游戏列表框
            if (isChecked == true) {
                sharedPreferences.edit()
                        .putBoolean("PackageInfoGridviewNotify", true).commit();

            } else {
                sharedPreferences.edit()
                        .putBoolean("PackageInfoGridviewNotify", false)
                        .commit();
                MinUtil.upUmenEventValue(mContext, "关闭游戏扫描次数",
                        "fmSettingPageCount");
            }

        } else if (buttonView == gotoVideoManage) {
            if (isChecked == true) {
                sharedPreferences.edit().putBoolean("isGotoVideoManage", true)
                        .commit();

            } else {
                sharedPreferences.edit().putBoolean("isGotoVideoManage", false)
                        .commit();

            }
        }
    }

    /**
     * 闹钟，用以定时启动推送
     *
     * @param context
     * @param pushTime 闹铃间隔时间
     */
    public static void setAlarmTime(Context context, int pushTime) {

        alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent("android.alarm.push.action");

        sender = PendingIntent.getBroadcast(

                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 1500毫秒后执行，闹铃间隔时间为pushTime
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1500, pushTime,
                sender);

    }

    /**
     * 清晰度选择对话框
     */
    public void dialogQualityOfVideo() {
        final String[] array = new String[]{ExApplication.HQualityVaule,
                ExApplication.SQualityVaule};

        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("画质选择")

                .setItems(array, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                sharedPreferences
                                        .edit()
                                        .putString("quality_of_video",
                                                ExApplication.HQuality).commit();
                                qualityTextview.setText(array[which]);
                                // 写入XML配置文件
                                // xmlFileWriter.creat(mContext);
                                break;
                            case 1:
                                sharedPreferences
                                        .edit()
                                        .putString("quality_of_video",
                                                ExApplication.SQuality).commit();
                                qualityTextview.setText(array[which]);
                                // 写入XML配置文件
                                // xmlFileWriter.creat(mContext);
                                break;

                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        alertDialog.show();
    }

}