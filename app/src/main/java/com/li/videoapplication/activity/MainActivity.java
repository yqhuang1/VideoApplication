package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fmscreenrecord.app.SRApplication;
import com.fmscreenrecord.floatview.FloatViewManager;
import com.fmscreenrecord.service.FloatViewService;
import com.fmscreenrecord.service.ScreenRECService;
import com.fmscreenrecord.utils.RecordVideo;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.Service.ODLoadVideoService;
import com.li.videoapplication.download.DownloadState;
import com.li.videoapplication.download.DownloadTask;
import com.li.videoapplication.download.DownloadTaskManager;
import com.li.videoapplication.fragment.ActivityFragment;
import com.li.videoapplication.fragment.DiscoverFragment;
import com.li.videoapplication.fragment.GiftFragment;
import com.li.videoapplication.fragment.HomeFragment;
import com.li.videoapplication.utils.Screen;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.StatusBarCompat;
import com.li.videoapplication.utils.StatusBarUtil;
import com.li.videoapplication.utils.ToastUtils;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static Activity ActivityMain;
    private Context context;

    // 顶部（标题）布局
    private RelativeLayout mainTitleLayout;
    // 顶部标题
    private TextView mTitleTv;
    private RelativeLayout searchLayout;
    private ImageView searchIv;
    private TextView searchTv;
    public static ImageButton persionBtn, videoBtn;
    public ImageView noticeIv;

    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    public static String netState = "";
    private LinearLayout noticeLl;

    private ViewPager mViewPager;// 用来放置界面切换
    private List<Fragment> mViews = new ArrayList<Fragment>();// 用来存放4个页面

    // 底部Tab布局
    private LinearLayout mainTabLayout;
    // 四个Tab，每个Tab包含一个按钮
    private LinearLayout mTabMain, mTabDiscover, mTabGift, mTabActivity;
    // 四个按钮
    private ImageButton mMainIb, mDiscoverIb, mGiftIb, mActivityIb;
    // 四个标题
    private TextView mMainTv, mDiscoverTv, mGiftTv, mActivityTv;

    int[] select_bg = new int[]{R.drawable.bottom_home_bg_select, R.drawable.bottom_discover_bg_select,
            R.drawable.bottom_gift_bg_select, R.drawable.bottom_activity_bg_select};
    int[] normal_bg = new int[]{R.drawable.bottom_home_bg_nomal, R.drawable.bottom_discover_bg_nomal,
            R.drawable.bottom_gift_bg_nomal, R.drawable.bottom_activity_bg_nomal};

    //侧滑个人菜单
    private static DrawerLayout mDrawerLayout;

    private SharedPreferences sysj_sharedPreferences;

    private static int lastId = 1;//

    private ImageView bottomProjection, titleLine;

    FeedbackAgent agent;

    private int mStatusBarColor;
    private int mAlpha = StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA;

    public static Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://关闭侧护栏
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                            Gravity.LEFT);
                    break;
                case 1://展开侧护栏
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                            Gravity.LEFT);
                    break;
                case 2:// 模拟点击录制按钮
                    videoBtn.performClick();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉android头部label的方法
        setContentView(R.layout.activity_my);

//        StatusBarCompat.compat(this);
//        StatusBarCompat.compat(this, 0xFFFF0000);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        ActivityMain = MainActivity.this;
        context = MainActivity.this;

        /**开启 友盟 推送服务**/
//        PushAgent mPushAgent = PushAgent.getInstance(context);
//        mPushAgent.enable();
        agent = new FeedbackAgent(this);
        agent.sync();
        agent.openFeedbackPush();
        PushAgent.getInstance(context).enable();
        PushAgent.getInstance(context).isEnabled();//查询状态
        String device_token = UmengRegistrar.getRegistrationId(context);//获取设备的Token
        System.out.println("device_token===" + device_token);

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        if (!SharePreferenceUtil.getPreference(this, "isFirst").equals("1")) {
            Intent intent = new Intent(this, IntroduceActivity.class);
            startActivity(intent);
        }

        if (SharePreferenceUtil.getUserEntity(this) != null) {
            ExApplication.MEMBER_ID = SharePreferenceUtil.getUserEntity(this).getId();
        }

        /**在清单文件中注册广播接收器，
         * “android.net.conn.CONNECTIVITY_CHANGE”是系统预定义好的 action 值，
         * 只要系统网络状态发生变化，NetworkReceiver 就能收到广播
         * **/
        //注册网络状态监听广播 sendOrderedBroadcast
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(myNetReceiver, mFilter);

        doNetworkTypeCheck();

        initView();
        initViewPage();
        initEvent();

        //默认打开2G/3G提醒
        if (SharePreferenceUtil.getPreference(context, "isGprsOpen") == "") {
            SharePreferenceUtil.setPreference(context, "isGprsOpen", "open");
        }
        //默认打开消息通知
        if (SharePreferenceUtil.getPreference(context, "isMessageOpen") == "") {
            SharePreferenceUtil.setPreference(context, "isMessageOpen", "open");
        }

        sysj_sharedPreferences = SharedPreferencesUtils
                .getMinJieKaiFaPreferences(this);
//        onResumeFirst();

    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        mStatusBarColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.main_drawerLayout), mStatusBarColor, mAlpha);
    }

    private void initView() {
        mainTitleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        mTitleTv = (TextView) findViewById(R.id.main_title_tv);

        searchLayout = (RelativeLayout) findViewById(R.id.main_title_searchLayout);
        searchLayout.setOnClickListener(this);
        searchIv = (ImageView) findViewById(R.id.main_title_searchIv);
        searchTv = (TextView) findViewById(R.id.main_title_searchTv);

        persionBtn = (ImageButton) findViewById(R.id.main_persion);
        persionBtn.setOnClickListener(this);
        videoBtn = (ImageButton) findViewById(R.id.main_video);
        videoBtn.setOnClickListener(this);
        noticeIv = (ImageView) findViewById(R.id.main_notice);

        noticeLl = (LinearLayout) findViewById(R.id.main_network_notice_layout);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpage);
        mViewPager.setOffscreenPageLimit(3);
        bottomProjection = (ImageView) findViewById(R.id.main_bottom_projection);

        mainTabLayout = (LinearLayout) findViewById(R.id.main_tab_layout);
        // 初始化四个LinearLayout
        mTabMain = (LinearLayout) findViewById(R.id.main_bottom_main);
        mTabMain.setOnClickListener(this);
        mTabDiscover = (LinearLayout) findViewById(R.id.main_bottom_discover);
        mTabDiscover.setOnClickListener(this);
        mTabGift = (LinearLayout) findViewById(R.id.main_bottom_gift);
        mTabGift.setOnClickListener(this);
        mTabActivity = (LinearLayout) findViewById(R.id.main_bottom_activity);
        mTabActivity.setOnClickListener(this);

        // 初始化四个按钮
        mMainIb = (ImageButton) findViewById(R.id.main_bottom_main_ib);
        mDiscoverIb = (ImageButton) findViewById(R.id.main_bottom_discover_ib);
        mGiftIb = (ImageButton) findViewById(R.id.main_bottom_gift_ib);
        mActivityIb = (ImageButton) findViewById(R.id.main_bottom_activity_ib);
        // 初始化四个标题
        mMainTv = (TextView) findViewById(R.id.main_bottom_main_tv);
        mDiscoverTv = (TextView) findViewById(R.id.main_bottom_discover_tv);
        mGiftTv = (TextView) findViewById(R.id.main_bottom_gift_tv);
        mActivityTv = (TextView) findViewById(R.id.main_bottom_activity_tv);

        titleLine = (ImageView) findViewById(R.id.main_title_line);

//        //初始化侧滑菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.LEFT);
//        mDrawerLayout.setScrimColor(Color.TRANSPARENT);//取消阴影背景

    }

    //展开侧护栏
    public void OpenLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.LEFT);
    }

    //关闭侧护栏
    public void CloseLeftMenu() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.LEFT);
    }

    /**
     * 初始化ViewPage
     */
    private void initViewPage() {

        mViews.add(new HomeFragment());
        mViews.add(DiscoverFragment.newInstance(Screen.getScreenWidth(getWindowManager()) + "", ""));
        mViews.add(new GiftFragment());
        mViews.add(new ActivityFragment());

        // 适配器初始化并设置
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, mViews);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                System.out.println("Extra...i: " + i);
            }
        });
    }

    private void initEvent() {
        mTabMain.setOnClickListener(this);
        mTabDiscover.setOnClickListener(this);
        mTabGift.setOnClickListener(this);
        mTabActivity.setOnClickListener(this);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             *ViewPage左右滑动时
             */
            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        selectMain();
                        break;
                    case 1:
                        selectDiscover();
                        break;
                    case 2:
                        selectGift();
                        break;
                    case 3:
                        selectActivity();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        /**
         * 侧滑栏监听
         */
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
//                float rightScale = 0.8f + scale * 0.2f;
                if (drawerView.getTag().equals("LEFT")) {
//                  设置窗口缩放比例为没有缩放
                    float leftScale = 1 - 0.3f * scale;
//                    ViewHelper.setScaleX(mMenu, leftScale);
//                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
//                    ViewHelper.setScaleX(mContent, rightScale);
//                    ViewHelper.setScaleY(mContent, rightScale);
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
//                    ViewHelper.setScaleX(mContent, rightScale);
//                    ViewHelper.setScaleY(mContent, rightScale);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    private void whiteTitle(String title) {
        titleLine.setVisibility(View.VISIBLE);
        mainTitleLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        mTitleTv.setText(title);
        mTitleTv.setTextColor(Color.parseColor("#818181"));
        persionBtn.setImageResource(R.drawable.main_persional_ic_red);
        videoBtn.setImageResource(R.drawable.main_record_ic_red);
        noticeIv.setBackgroundResource(R.drawable.message_notice_red);
//        StatusBarUtil.setColorForDrawerLayout(MainActivity.this, mDrawerLayout, mStatusBarColor, mAlpha);
    }

    private void redTitle() {
        titleLine.setVisibility(View.GONE);
        mainTitleLayout.setBackgroundColor(Color.parseColor("#fc3c2d"));
        mTitleTv.setText("手游视界");
        mTitleTv.setTextColor(Color.parseColor("#ffffff"));
        persionBtn.setImageResource(R.drawable.main_persional_ic_white);
        videoBtn.setImageResource(R.drawable.main_record_ic_white);
        noticeIv.setBackgroundResource(R.drawable.message_notice_white);
//        StatusBarUtil.setColorForDrawerLayout(MainActivity.this, mDrawerLayout, mStatusBarColor, mAlpha);
    }

    /**
     * 判断哪个要显示，及设置按钮图片
     */
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.main_bottom_main:
                selectMain();
                mViewPager.setCurrentItem(0);
                break;
            case R.id.main_bottom_discover:
                selectDiscover();
                mViewPager.setCurrentItem(1);
                break;
            case R.id.main_bottom_gift:
                selectGift();
                mViewPager.setCurrentItem(2);
                break;
            case R.id.main_bottom_activity:
                selectActivity();
                mViewPager.setCurrentItem(3);
                break;
            case R.id.main_title_searchLayout:
                if (netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.main_persion:
                OpenLeftMenu();
                break;
            case R.id.main_video:
                //-----------------精简版 录屏大师------------------------------------------
                if (FloatViewService.isInFloatViewService) {
                    if (RecordVideo.isStart == false && RecordVideo.isStop == true) {
                        //不在录制
                        MainVideoExit(context);//关闭录制功能方法
                    } else if (RecordVideo.isStart == true && RecordVideo.isStop == false) {
                        //正在录制
                        ToastUtils.showToast(context, "正在录制视频中，中途退出可能导致异常，请停止录制后再退出！");
                    }
                } else {

                    if (null == bitmap) {
//                        // 需要在actvity显示出来后 再进行截屏操作。
//                        bitmap = takeScreenShot(MainActivity.this);
//                        MyBitmapStore.setBmp(bitmap);
                        Intent intentVideo = new Intent(this, com.fmscreenrecord.activity.MainActivity.class);
                        startActivity(intentVideo);
                    }

                }
                //-------------------------------------------------------------------------
                break;
            default:
                break;
        }
    }

    private Bitmap bitmap;


    /**
     * Tab中 首页 被选择
     */
    private void selectMain() {
        mViewPager.setCurrentItem(0);
        redTitle();
        mTitleTv.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);

        mMainIb.setBackgroundResource(R.drawable.bottom_home_bg_select);
        mDiscoverIb.setBackgroundResource(R.drawable.bottom_discover_bg_nomal);
        mGiftIb.setBackgroundResource(R.drawable.bottom_gift_bg_nomal);
        mActivityIb.setBackgroundResource(R.drawable.bottom_activity_bg_nomal);

        mMainTv.setTextColor(context.getResources().getColor(R.color.red));
        mDiscoverTv.setTextColor(context.getResources().getColor(R.color.gray));
        mGiftTv.setTextColor(context.getResources().getColor(R.color.gray));
        mActivityTv.setTextColor(context.getResources().getColor(R.color.gray));
    }

    /**
     * Tab中 发现 被选择
     */
    private void selectDiscover() {
        mViewPager.setCurrentItem(1);
        onShowTab();
        mainTitleLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        whiteTitle("发现");
        mTitleTv.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);

        mMainIb.setBackgroundResource(R.drawable.bottom_home_bg_nomal);
        mDiscoverIb.setBackgroundResource(R.drawable.bottom_discover_bg_select);
        mGiftIb.setBackgroundResource(R.drawable.bottom_gift_bg_nomal);
        mActivityIb.setBackgroundResource(R.drawable.bottom_activity_bg_nomal);

        mMainTv.setTextColor(context.getResources().getColor(R.color.gray));
        mDiscoverTv.setTextColor(context.getResources().getColor(R.color.red));
        mGiftTv.setTextColor(context.getResources().getColor(R.color.gray));
        mActivityTv.setTextColor(context.getResources().getColor(R.color.gray));
    }

    /**
     * Tab中 礼包 被选择
     */
    private void selectGift() {
        mViewPager.setCurrentItem(2);
        onShowTab();
        mainTitleLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        whiteTitle("礼包");
        mTitleTv.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);

        mMainIb.setBackgroundResource(R.drawable.bottom_home_bg_nomal);
        mDiscoverIb.setBackgroundResource(R.drawable.bottom_discover_bg_nomal);
        mGiftIb.setBackgroundResource(R.drawable.bottom_gift_bg_select);
        mActivityIb.setBackgroundResource(R.drawable.bottom_activity_bg_nomal);

        mMainTv.setTextColor(context.getResources().getColor(R.color.gray));
        mDiscoverTv.setTextColor(context.getResources().getColor(R.color.gray));
        mGiftTv.setTextColor(context.getResources().getColor(R.color.red));
        mActivityTv.setTextColor(context.getResources().getColor(R.color.gray));
    }

    /**
     * Tab中 活动 被选择
     */
    private void selectActivity() {
        mViewPager.setCurrentItem(3);
        onShowTab();
        mainTitleLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        whiteTitle("活动");
        mTitleTv.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);

        mMainIb.setBackgroundResource(R.drawable.bottom_home_bg_nomal);
        mDiscoverIb.setBackgroundResource(R.drawable.bottom_discover_bg_nomal);
        mGiftIb.setBackgroundResource(R.drawable.bottom_gift_bg_nomal);
        mActivityIb.setBackgroundResource(R.drawable.bottom_activity_bg_select);

        mMainTv.setTextColor(context.getResources().getColor(R.color.gray));
        mDiscoverTv.setTextColor(context.getResources().getColor(R.color.gray));
        mGiftTv.setTextColor(context.getResources().getColor(R.color.gray));
        mActivityTv.setTextColor(context.getResources().getColor(R.color.red));
    }


    public void onHiddenTab() {
        mainTabLayout.setVisibility(View.GONE);
        bottomProjection.setVisibility(View.GONE);
    }

    public void onShowTab() {
        mainTabLayout.setVisibility(View.VISIBLE);
        bottomProjection.setVisibility(View.VISIBLE);
    }

    /**
     * 检查网络类型
     */
    public void doNetworkTypeCheck() {

        if ("open".equals(SharePreferenceUtil.getPreference(context, "isGprsOpen"))) {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
                if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
                    ToastUtils.showToast(context, "当前使用的是移动网络!");
                    netState = "CONNECTED";
                }
            }
        }
    }

    /////////////监听网络状态变化的广播接收器

    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {

        /**广播发送出去后，
         * 所以已注册的 BroadcastReceiver 会检查注册时的 IntentFilter 是否与发送的 Intent 相匹配，
         * 若匹配则会调用 BroadcastReceiver 的 onReceiver() 方法
         * **/
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {

                    /////////////网络连接
                    String name = netInfo.getTypeName();

                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络
                        noticeLl.setVisibility(View.GONE);
                        netState = "WIFI";
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络
                        noticeLl.setVisibility(View.GONE);
                        netState = "ETHERNET";
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络
                        noticeLl.setVisibility(View.GONE);
                        netState = "MOBILE";
                    }
                } else {
                    ////////网络断开
                    noticeLl.setVisibility(View.VISIBLE);
                    netState = "DISCONNECTED";
//                    HomeFragment.viewhandler.sendEmptyMessage(0);
//                    GiftFragment.viewhandler.sendEmptyMessage(0);
                }
            }

        }
    };


///--------------------------------------------------------------------

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtils.showToast(MainActivity.this, "再按一次退出应用");
                    exitTime = System.currentTimeMillis();

                } else {
                    pauseDownloadings(context);
                    //-------------------精简版 录屏大师 退出----------------
                    if (!RecordVideo.isRecordering) {
                        com.fmscreenrecord.activity.MainActivity.isInMain = false;
                        // FloatView1.handler.sendEmptyMessage(4);
                    }

                    //退出时关闭底层录制核心
//                    Recorder44.StopRecordVideo();

                    // 终止录制服务
                    SRApplication.Get().stopService(
                            new Intent(SRApplication.Get(), ScreenRECService.class));

                    if (com.fmscreenrecord.app.ExApplication.isCompositionVideo == false) {
                        // 终止浮窗服务
                        SRApplication.Get().stopService(
                                new Intent(SRApplication.Get(), FloatViewService.class));

                    } else {
                        // 待合成后再关闭录屏大师
                        com.fmscreenrecord.app.ExApplication.islaterCloaseApp = true;
                    }

                    // 终止离线下载服务
                    SRApplication.Get().stopService(
                            new Intent(SRApplication.Get(), ODLoadVideoService.class));

                    FloatViewManager.getInstance(context).removeFirst();

                    //------------------------------------------------------

                    Intent it = new Intent(Intent.ACTION_MAIN);
                    it.addCategory(Intent.CATEGORY_HOME);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                    finish();
                    if (com.fmscreenrecord.app.ExApplication.isCompositionVideo == false) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                    System.exit(0);
                }
        }
        return true;
    }

    private DownloadTaskManager mDlTaskMng;

    /**
     * 设置正在下载的视频列表（数据库中的状态）为暂停
     * 以防止APP强制退出时，不能进入视频下载页面进行继续下载
     */
    private void pauseDownloadings(Context context) {
        mDlTaskMng = DownloadTaskManager.getInstance(context);
        List<DownloadTask> mDownloadinglist = mDlTaskMng.getDownloadingTask();
        for (DownloadTask task : mDownloadinglist) {
            if (task.getDownloadState().equals(DownloadState.DOWNLOADING)) {
                task.setDownloadState(DownloadState.PAUSE);
                mDlTaskMng.updateDownloadTask(task);
            }
        }
    }

    //------------------------------------------------------------------------------
    //精简版 录屏大师


    //关闭录制功能方法
    public void MainVideoExit(final Context context) {

        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("确定要退出 （录屏功能） 吗?")
                .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // hideScreenshotNotification();
                        // NotificationService.isCancleNotification = true;
                        NotificationManager mNotificationManager;
                        mNotificationManager = (NotificationManager) SRApplication
                                .Get().getSystemService(Context.NOTIFICATION_SERVICE);

                        mNotificationManager.cancelAll();

                        // 终止录制服务
                        SRApplication.Get()
                                .stopService(
                                        new Intent(SRApplication.Get(),
                                                ScreenRECService.class));

                        if (com.fmscreenrecord.app.ExApplication.isCompositionVideo == false) {
                            // 终止浮窗服务
                            SRApplication.Get().stopService(
                                    new Intent(SRApplication.Get(), FloatViewService.class));

                        } else {
                            // 待合成后再关闭录屏大师
                            com.fmscreenrecord.app.ExApplication.islaterCloaseApp = true;
                        }

                        // SRApplication.Get().stopService(
                        // new Intent(SRApplication.Get(),
                        // NotificationService.class));
                        // exitProgrames();
                        FloatViewManager.getInstance(context).removeFirst();
                        // !!!               finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();

    }


    //------------------------------------------------------------------------------


    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("homeActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("homeActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {

        // TODO Auto-generated method stub
        //---------------------------------
//        /**友盟 关闭客户端的通知服务，来查询状态**/
//        PushAgent.getInstance(context).disable();
        PushAgent.getInstance(context).isEnabled();//查询状态

        if (myNetReceiver != null) {
            context.unregisterReceiver(myNetReceiver);
        }

        super.onDestroy();
    }

}

