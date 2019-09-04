package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.VideoList.VideoCallBack;
import com.fmscreenrecord.VideoList.VideoScanningThrread;
import com.fmscreenrecord.utils.HttpUtils;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.fmscreenrecord.video.ImageInfo;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.Adapter.CollectAdapter;
import com.li.videoapplication.Adapter.ColudVideoAdapter;
import com.li.videoapplication.Adapter.ExtVideoListAdapter;
import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.View.CustomViewPager;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.entity.VedioDetail;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.fragment.CollectVideoFragment;
import com.li.videoapplication.fragment.ColudVideoFragment;
import com.li.videoapplication.fragment.LocalVideoFragment;
import com.li.videoapplication.fragment.ScreenShotFragment;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.StatusBarCompat;
import com.li.videoapplication.utils.StatusBarUtil;
import com.li.videoapplication.utils.Titles;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/29.
 * 视频管理 页面
 */
public class VideoManagerActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 滑动按钮的外围布局
     */
    private RelativeLayout rl_nav;

    /**
     * 水平滑动控件
     */
    private SyncHorizontalScrollView mHsv;

    /**
     * 标题选项
     */
    private RadioGroup rg_nav_content;
    /**
     * 标题滚动下标
     */
    private ImageView iv_nav_indicator;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    public static CustomViewPager mViewPager;

    /**
     * 标题滚动下标长度
     */
    private int indicatorWidth;
    public static String[] tabTitle = Titles.VideoManager;
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;
    public static int position = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private static Context context;

    /**
     * 处在哪一个页面，0为本地视频页，1为云端视频页，2为截图页
     */
    public static int IN_WHITCH_PAGE = 0;

    /**
     * 顶部标题布局 返回按钮、导入、编辑
     */
    private static RelativeLayout topTitleRL;
    private static ImageButton backBtn;
    private static TextView importTv, editorTv;

    /**
     * 是否处于编辑的状态*
     */
    public static boolean inEditorState = false;

    /**
     * 是否处于 视频管理 页面*
     */
    public static boolean isINVideoManagerActivity = false;

    /**
     * 顶部隐藏删除布局 选中了 0 项textview、取消按钮*
     */
    private static RelativeLayout topDelRL;
    private static TextView cntTv;
    private static Button cancleBtn;

    /**
     * 底部隐藏布局 全选、保存到相册、删除按钮*
     */
    private static LinearLayout bottomLL;
    private static Button selAllBtn, saveToPhotoBtn, delBtn;

    // SD卡剩余空间，总空间
    public static String insdcardSurplusSize;
    public static String insdcardSizes;
    // 录屏大师占用空间
    public static String SRFileSsizes;
    private static SharedPreferences sharedPreferences;

    // 云端视频删除接口
    public static String delSerVideoUrl = "http://apps.ifeimo.com/home/video/displayVideo.html?";

    // 文件路径
    private static File path;
    private static File SDcardPath;
    private String path_dir;
    private String path_SDdir;
    SharedPreferences sp;
    // 数据库
    static VideoDB videoDB;

    /**
     * 计算占用空间的线程
     */
    static ThreadText threadText;

    // 剩余空间提示
    private static TextView sdcardSizeHint;
    /**
     * 外部视频
     */
    private static String PREFS_NAME = "ext_video";
    /**
     * 记录已导入的外部视频
     */
    public static SharedPreferences spExtVideoCheck;
    /**
     * 已选择，将要导入的文件列表
     */
    public static List<VideoInfo> listCheckToLead = null;


    public static Handler handlerViewChange = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:// 取消全选
                {
                    if (IN_WHITCH_PAGE == 0) {// 清空本地视频全选列表数据
                        LocalVideoFragment.localVideoHandle.sendEmptyMessage(1);
                    } else if (IN_WHITCH_PAGE == 1) {// 清空云端视频全选列表数据
                        ColudVideoFragment.coloudlVideoHandle.sendEmptyMessage(1);
                    } else if (IN_WHITCH_PAGE == 2) {// 清空云端收藏视频全选列表数据
                        CollectVideoFragment.collectVideoHandle.sendEmptyMessage(1);
                    } else if (IN_WHITCH_PAGE == 3) {// 清空本地图片全选列表数据
                        ScreenShotFragment.screenShotHandle.sendEmptyMessage(1);
                    }
                    handlerViewChange.sendEmptyMessage(2);
                }

                break;

                case 2:// 显示选择的文件个数
                {
                    if (IN_WHITCH_PAGE == 0) {
                        cntTv.setText(Html
                                .fromHtml("选中了<font color=\"#15b4eb\"> "
                                        + LocalVideoFragment.localVideoListCheckToDel.size()
                                        + " </font> 个视频"));
                    } else if (IN_WHITCH_PAGE == 1) {
                        cntTv.setText(Html
                                .fromHtml("选中了<font color=\"#15b4eb\"> "
                                        + ColudVideoFragment.coludVideoListCheckToDel.size()
                                        + " </font> 个视频"));
                    } else if (IN_WHITCH_PAGE == 2) {
                        cntTv.setText(Html
                                .fromHtml("选中了<font color=\"#15b4eb\"> "
                                        + CollectVideoFragment.collectVideoListCheckToDel.size()
                                        + " </font> 个视频"));
                    } else if (IN_WHITCH_PAGE == 3) {
                        cntTv.setText(Html
                                .fromHtml("选中了<font color=\"#15b4eb\"> "
                                        + ScreenShotFragment.imageListCheckToDel.size()
                                        + " </font> 张图片"));
                    }
                }
                break;

                case 3: // 确认删除
                {
                    mViewPager.setScanScroll(true);
                    topDelRL.setVisibility(View.GONE);
                    topTitleRL.setVisibility(View.VISIBLE);
                    inEditorState = false;
                    if (IN_WHITCH_PAGE == 0) {
                        File file = null;
                        for (VideoInfo x : LocalVideoFragment.localVideoListCheckToDel) {
                            SharedPreferences.Editor editor = spExtVideoCheck.edit();
                            editor.putBoolean(x.getPath(), false);
                            editor.commit();
                            file = new File(x.getPath());
                            if (file.exists()) {
                                file.delete();
                            }
                            videoDB = new VideoDB(context);
                            videoDB.delete(Integer.valueOf(x.getVideoId())
                                    .intValue());
                            // 逐个输出数组元素的值
                        }
                        Toast.makeText(context, "删除成功", 0).show();
                        new VideoScanningThrread(context, path, mCallBack).start();

                        // 刷新视频列表数据
                        LocalVideoFragment.localVideoListData = videoDB.GetVideoList();
                        LocalVideoFragment.localVideoAdapter.refresh(LocalVideoFragment.localVideoListData);
                        //删除列表清零
                        LocalVideoFragment.localVideoListCheckToDel.clear();
                    } else if (IN_WHITCH_PAGE == 1) {// 删除云端视频数据
                        DeleteAsyncTast deleteAsyncTast = new DeleteAsyncTast();
                        deleteAsyncTast.execute();
                        ColudVideoFragment.coloudlVideoHandle.sendEmptyMessage(5);
                    } else if (IN_WHITCH_PAGE == 2) {// 删除云端收藏视频数据
                        DelectVideoAsync delectVideoAsync = new DelectVideoAsync();
                        delectVideoAsync.execute();
                        CollectVideoFragment.collectVideoHandle.sendEmptyMessage(5);
                    } else if (IN_WHITCH_PAGE == 3) {
                        for (ImageInfo x : ScreenShotFragment.imageListCheckToDel) {
                            File f = new File(x.getPath());
                            f.delete();
                        }

                        ScreenShotFragment.ThreadPic myThread2 = new ScreenShotFragment.ThreadPic();
                        myThread2.start();

                        // 刷新图片列表数据
                        ScreenShotFragment.screenShotList = ScreenShotFragment.GetPictureileName(context);
                        ScreenShotFragment.screenShotAdapter.update(ScreenShotFragment.screenShotList);
                        //删除列表清零
                        ScreenShotFragment.imageListCheckToDel.clear();
                        Toast.makeText(context, "删除成功", 0).show();
                    }
                    mViewPager.setScanScroll(true);

                    // 启动子线程计算SD卡剩余空间录屏大师路径和文件大小
                    threadText = new ThreadText();
                    threadText.start();
                }
                break;

                case 4://编辑

                    break;

                case 5://取消

                    break;

                case 6:// 全选按钮 选择全选
                    if (IN_WHITCH_PAGE == 0) {// 本地视频
                        LocalVideoFragment.localVideoListCheckToDel.clear();
                        for (int i = 0; i < LocalVideoFragment.localVideoListData.size(); i++) {
                            LocalVideoFragment.localVideoAdapter.ListDelcheck.set(i, true);
                            LocalVideoFragment.localVideoListCheckToDel.add(LocalVideoFragment.localVideoListData.get(i));
                        }
                        LocalVideoFragment.localVideoAdapter.notifyDataSetChanged();
                    } else if (IN_WHITCH_PAGE == 1) {// 云端视频
                        ColudVideoFragment.coludVideoListCheckToDel.clear();
                        for (int i = 0; i < ColudVideoFragment.serverVideoListData.size(); i++) {
                            ColudVideoFragment.coludVideoAdapter.ListDelcheck.set(i, true);
                            ColudVideoFragment.coludVideoListCheckToDel.add(ColudVideoFragment.serverVideoListData
                                    .get(i));
                        }
                        ColudVideoFragment.coludVideoAdapter.notifyDataSetChanged();
                    } else if (IN_WHITCH_PAGE == 2) {
                        CollectVideoFragment.collectVideoListCheckToDel.clear();
                        for (int i = 0; i < CollectVideoFragment.videoList.size(); i++) {
                            CollectVideoFragment.collectAdapter.ListDelcheck.set(i, true);
                            CollectVideoFragment.collectVideoListCheckToDel.add(CollectVideoFragment.videoList
                                    .get(i));
                        }
                        CollectVideoFragment.collectAdapter.notifyDataSetChanged();

                    } else if (IN_WHITCH_PAGE == 3) {// 本地图片
                        ScreenShotFragment.imageListCheckToDel.clear();
                        for (int i = 0; i < ScreenShotFragment.screenShotList.size(); i++) {
                            ScreenShotFragment.screenShotAdapter.ListDelcheck.set(i, true);
                            ScreenShotFragment.imageListCheckToDel.add(ScreenShotFragment.screenShotList.get(i));
                        }
                        ScreenShotFragment.screenShotAdapter.notifyDataSetChanged();
                    }
                    this.sendEmptyMessage(2);
                    break;

                case 7:// 全选按钮 取消全选
                    if (IN_WHITCH_PAGE == 0) {
                        for (int i = 0; i < LocalVideoFragment.localVideoListData.size(); i++) {
                            LocalVideoFragment.localVideoAdapter.ListDelcheck.set(i, false);
                            LocalVideoFragment.localVideoListCheckToDel.clear();
                        }
                        LocalVideoFragment.localVideoAdapter.notifyDataSetChanged();
                    } else if (IN_WHITCH_PAGE == 1) {
                        for (int i = 0; i < ColudVideoFragment.serverVideoListData.size(); i++) {
                            ColudVideoAdapter.ListDelcheck.set(i, false);
                            ColudVideoFragment.coludVideoListCheckToDel.clear();
                        }
                        ColudVideoFragment.coludVideoAdapter.notifyDataSetChanged();

                    } else if (IN_WHITCH_PAGE == 2) {
                        for (int i = 0; i < CollectVideoFragment.videoList.size(); i++) {
                            CollectAdapter.ListDelcheck.set(i, false);
                            CollectVideoFragment.collectVideoListCheckToDel.clear();
                        }
                        CollectVideoFragment.collectAdapter.notifyDataSetChanged();

                    } else if (IN_WHITCH_PAGE == 3) {
                        for (int i = 0; i < ScreenShotFragment.screenShotList.size(); i++) {
                            ScreenShotFragment.screenShotAdapter.ListDelcheck.set(i, false);
                            ScreenShotFragment.imageListCheckToDel.clear();
                        }
                        ScreenShotFragment.screenShotAdapter.notifyDataSetChanged();
                    }
                    this.sendEmptyMessage(2);
                    break;

                case 8:// 显示本地文件占用情况
                    sdcardSizeHint.setText(Html
                            .fromHtml("共<font color=\"#15b4eb\">"
                                    + LocalVideoFragment.localVideoListData.size()
                                    + "</font>个视频,<font color=\"#15b4eb\">"
                                    + ScreenShotFragment.screenShotList.size()
                                    + "</font>张图片, 占用空间<font color=\"#15b4eb\">"
                                    + SRFileSsizes
                                    + "M</font>, 剩余  <font color=\"#15b4eb\">"
                                    + insdcardSurplusSize + "</font>/"
                                    + insdcardSizes));
                    break;

                case 9:
                    // 获取数据库数据并刷新本地视频列表数据
                    if (videoDB == null) {
                        videoDB = new VideoDB(context);
                    }

                    LocalVideoFragment.localVideoListData = videoDB.GetVideoList();

                    LocalVideoFragment.localVideoAdapter
                            .refresh(LocalVideoFragment.localVideoListData);
                    break;

            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_manager);
//        StatusBarCompat.compat(this, 0xFFFF0000);
        isINVideoManagerActivity = true;

        context = VideoManagerActivity.this;
        spExtVideoCheck = getSharedPreferences(PREFS_NAME, 0);

        initView();
        InitStoreDir();
        setListener();

        // 启动子线程计算SD卡剩余空间录屏大师路径和文件大小
        threadText = new ThreadText();
        threadText.start();

    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white_75_transparent));
    }

    private void initView() {

        /**顶部标题布局 返回按钮、导入、编辑**/
        topTitleRL = (RelativeLayout) findViewById(R.id.activity_video_manager_topTitleRL);
        backBtn = (ImageButton) findViewById(R.id.activity_video_manager_back);
        backBtn.setOnClickListener(this);
        importTv = (TextView) findViewById(R.id.activity_video_manager_import);
        importTv.setOnClickListener(this);
        editorTv = (TextView) findViewById(R.id.activity_video_manager_editor);
        editorTv.setOnClickListener(this);

        /**顶部隐藏删除布局 选中了 0 项textview、取消按钮**/
        topDelRL = (RelativeLayout) findViewById(R.id.activity_video_manager_topDelRL);
        cntTv = (TextView) findViewById(R.id.activity_video_manager_tv_CntDel);
        cancleBtn = (Button) findViewById(R.id.activity_video_manager_btn_CancleDel);
        cancleBtn.setOnClickListener(this);

        /**底部隐藏布局 全选、保存到相册、删除按钮**/
        bottomLL = (LinearLayout) findViewById(R.id.activity_video_manager_bottomLL);
        selAllBtn = (Button) findViewById(R.id.activity_video_manager_btn_SelAll);
        selAllBtn.setOnClickListener(this);
        saveToPhotoBtn = (Button) findViewById(R.id.activity_video_manager__btn_SaveToPhoto);
        saveToPhotoBtn.setOnClickListener(this);
        delBtn = (Button) findViewById(R.id.activity_video_manager_btn_Del);
        delBtn.setOnClickListener(this);

        rl_nav = (RelativeLayout) findViewById(R.id.activity_video_manager_rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.activity_video_manager_mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.activity_video_manager_rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.activity_video_manager_iv_nav_indicator);
        sdcardSizeHint = (TextView) findViewById(R.id.activity_video_manager_sdcardsize_hint);
        mViewPager = (CustomViewPager) findViewById(R.id.activity_video_manager_mViewPager);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / 4;
        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, (Activity) context);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        fragments.add(LocalVideoFragment.newInstance("", ""));
        fragments.add(ColudVideoFragment.newInstance("", ""));
        fragments.add(CollectVideoFragment.newInstance("", ""));
        fragments.add(ScreenShotFragment.newInstance("", ""));

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageScrollStateChanged(int i) {
                super.onExtraPageScrollStateChanged(i);
                System.out.println("Extra...i: " + i);

            }
        });


    }

    // 获取文件路径
    private void InitStoreDir() {
        sp = SharedPreferencesUtils.getMinJieKaiFaPreferences(this);
        path_dir = sp.getString("image_store_dir", StoreDirUtil
                .getDefault(this).toString());
        if (StoreDirUtil.getSDDEfault(this) != null) {
            path_SDdir = StoreDirUtil.getSDDEfault(this).toString();
            SDcardPath = new File(path_SDdir);
        }
        path = new File(path_dir);
    }

    private void initNavigationHSV() {

        rg_nav_content.removeAllViews();

        for (int i = 0; i < tabTitle.length; i++) {

            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            if (i == 0) {
                rb.setTextColor(VideoManagerActivity.this.getResources().getColor(R.color.search_result_focus));
            } else {
                rb.setTextColor(VideoManagerActivity.this.getResources().getColor(R.color.search_result_default));
            }
            rb.setText(tabTitle[i]);
            rb.setTextSize(16);

            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
        }
    }


    private void setListener() {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton) rg_nav_content.getChildAt(position)).performClick();

                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) findViewById(i);
                        tempButton.setTextColor(VideoManagerActivity.this.getResources().getColor(R.color.search_result_default));
                    }
                    RadioButton tempButton = (RadioButton) findViewById(position);
                    tempButton.setTextColor(VideoManagerActivity.this.getResources().getColor(R.color.search_result_focus));
                }

                if (position == 0) {
                    IN_WHITCH_PAGE = 0;
                    importTv.setVisibility(View.VISIBLE);
                    sdcardSizeHint.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    IN_WHITCH_PAGE = 1;
                    importTv.setVisibility(View.GONE);
                    sdcardSizeHint.setVisibility(View.GONE);
                } else if (position == 2) {
                    IN_WHITCH_PAGE = 2;
                    importTv.setVisibility(View.GONE);
                    sdcardSizeHint.setVisibility(View.GONE);
                } else if (position == 3) {
                    IN_WHITCH_PAGE = 3;
                    importTv.setVisibility(View.GONE);
                    sdcardSizeHint.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        ((RadioButton) rg_nav_content.getChildAt(2)).setChecked(true);
        ((RadioButton) rg_nav_content.getChildAt(0)).setChecked(true);
        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (rg_nav_content.getChildAt(checkedId) != null && inEditorState == false) {


                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft,
                            ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    //执行位移动画
                    iv_nav_indicator.startAnimation(animation);

                    mViewPager.setCurrentItem(checkedId);    //ViewPager 跟随一起 切换

                    //记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();

                    mHsv.smoothScrollTo(
                            (checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
                } else {
                    //编辑状态时radiogroup不能选择
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_video_manager_back:
                this.finish();
                break;

            //导入
            case R.id.activity_video_manager_import:
                ExtVideo();
                break;

            //编辑
            case R.id.activity_video_manager_editor:
                inEditorState = true;
                mViewPager.setScanScroll(false);
                topTitleRL.setVisibility(View.GONE);
                topDelRL.setVisibility(View.VISIBLE);
                bottomLL.setVisibility(View.VISIBLE);

                //云端视频、已收藏管理页，隐藏“保存到相册按钮”
                if (IN_WHITCH_PAGE == 0) {
                    saveToPhotoBtn.setVisibility(View.VISIBLE);
                } else if (IN_WHITCH_PAGE == 1) {
                    saveToPhotoBtn.setVisibility(View.GONE);
                    ColudVideoFragment.coloudlVideoHandle.sendEmptyMessage(4);
                } else if (IN_WHITCH_PAGE == 2) {
                    saveToPhotoBtn.setVisibility(View.GONE);
                    CollectVideoFragment.collectVideoHandle.sendEmptyMessage(4);
                } else if (IN_WHITCH_PAGE == 3) {
                    saveToPhotoBtn.setVisibility(View.VISIBLE);
                }

                this.handlerViewChange.sendEmptyMessage(2);
                break;

            //取消
            case R.id.activity_video_manager_btn_CancleDel:
                inEditorState = false;
                mViewPager.setScanScroll(true);
                topTitleRL.setVisibility(View.VISIBLE);
                topDelRL.setVisibility(View.GONE);
                bottomLL.setVisibility(View.GONE);
                if (IN_WHITCH_PAGE == 0) {
                    LocalVideoFragment.localVideoHandle.sendEmptyMessage(3);
                } else if (IN_WHITCH_PAGE == 1) {
                    ColudVideoFragment.coloudlVideoHandle.sendEmptyMessage(3);
                } else if (IN_WHITCH_PAGE == 2) {
                    CollectVideoFragment.collectVideoHandle.sendEmptyMessage(3);
                } else if (IN_WHITCH_PAGE == 3) {
                    ScreenShotFragment.screenShotHandle.sendEmptyMessage(3);
                }

            {
                VideoManagerActivity.handlerViewChange.sendEmptyMessage(7);
                selAllBtn.setText("全选");
            }

            break;

            //全选 取消全选
            case R.id.activity_video_manager_btn_SelAll:
                if (selAllBtn.getText().equals("全选")) {
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(6);
                    selAllBtn.setText("取消全选");
                } else {
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(7);
                    selAllBtn.setText("全选");
                }
                break;

            //保存到相册
            case R.id.activity_video_manager__btn_SaveToPhoto:
                if (IN_WHITCH_PAGE == 0) {
                    LocalVideoFragment.localVideoHandle.sendEmptyMessage(5);
                } else if (IN_WHITCH_PAGE == 3) {
                    ScreenShotFragment.screenShotHandle.sendEmptyMessage(5);
                }
                break;

            //删除
            case R.id.activity_video_manager_btn_Del:
                if (IN_WHITCH_PAGE == 0) {
                    if (LocalVideoFragment.localVideoListCheckToDel.size() > 0) {
                        dialogDelVideo(LocalVideoFragment.localVideoListCheckToDel.size());
                    } else {
                        Toast.makeText(getApplicationContext(), "请先选择要删除的视频",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (IN_WHITCH_PAGE == 1) {
                    if (ColudVideoFragment.coludVideoListCheckToDel.size() > 0) {
                        dialogDelVideo(ColudVideoFragment.coludVideoListCheckToDel.size());
                    } else {
                        Toast.makeText(getApplicationContext(), "请先选择要删除的视频",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (IN_WHITCH_PAGE == 2) {
                    if (CollectVideoFragment.collectVideoListCheckToDel.size() > 0) {
                        dialogDelVideo(CollectVideoFragment.collectVideoListCheckToDel.size());
                    } else {
                        Toast.makeText(getApplicationContext(), "请先选择要删除的视频",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (IN_WHITCH_PAGE == 3) {
                    if (ScreenShotFragment.imageListCheckToDel.size() > 0) {
                        dialogDelImage();
                    } else {
                        Toast.makeText(getApplicationContext(), "请先选择要删除的图片",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    //--------------------------------------------------------------------

    /**
     * 导入
     */
    private void ExtVideo() {
        List<VideoInfo> ext_list = new ArrayList<VideoInfo>();
        ext_list = getExternalVideo(context);

        listCheckToLead = new ArrayList<VideoInfo>();
        listCheckToLead.clear();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("导入外部视频");
        builder.setAdapter(
                new ExtVideoListAdapter(ext_list, context),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        builder.setNeutralButton("导入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences.Editor editor;
                for (int i = 0; i < listCheckToLead.size(); i++) {
                    videoDB = new VideoDB(context);
                    videoDB.insert(listCheckToLead.get(i).getDisplayName(),
                            listCheckToLead.get(i).getPath(), "ext", "local");

                    editor = VideoManagerActivity.spExtVideoCheck.edit();
                    editor.putBoolean(listCheckToLead.get(i).getPath(), true);
                    editor.commit();
                }
                new VideoScanningThrread(context, path, mCallBack).start();

                //刷新适配器
                LocalVideoFragment.localVideoHandle.sendEmptyMessage(2);
            }
        });
        builder.show();
    }

    public List<VideoInfo> getExternalVideo(Context context) {
        List<VideoInfo> list = new ArrayList<VideoInfo>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();
        for (int i = 0; i < fileNum; i++) {
            String str = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.DATA));
            if (!str.contains("LuPingDaShi")) {
                VideoInfo vi = new VideoInfo();
                vi.setDisplayName(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                vi.setPath(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                list.add(vi);
            }
            cursor.moveToNext();
        }
        return list;
    }

    /**
     * 回调获取视频信息
     */
    public static VideoCallBack mCallBack = new VideoCallBack() {
        @Override
        public void getList(final List<VideoInfo> list) {
            LocalVideoFragment.localVideoListData = list;
        }
    };


    /**
     * 删除视频 对话框
     */
    protected void dialogDelVideo(int size) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Html.fromHtml("确认删除这<font color=\"#15b4eb\"> "
                + size
                + " </font> 个视频?"));
        builder.setPositiveButton(Html.fromHtml("<font color=\"#15b4eb\"> "
                + "确认" + " </font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bottomLL.setVisibility(View.GONE);
                handlerViewChange.sendEmptyMessage(3);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();

    }

    /**
     * 删除相片 对话框
     */
    protected void dialogDelImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Html.fromHtml("确认删除这<font color=\"#15b4eb\"> "
                + ScreenShotFragment.imageListCheckToDel.size() + " </font> 张图片?"));
        builder.setPositiveButton(Html.fromHtml("<font color=\"#15b4eb\"> "
                + "确认" + " </font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bottomLL.setVisibility(View.GONE);
                handlerViewChange.sendEmptyMessage(3);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();

    }


    /**
     * 删除云端视频
     *
     * @author WYX
     */
    static class DeleteAsyncTast extends AsyncTask<Void, Integer, Integer> {
        // TODO
        // 云端视频ID
        String videoid = null;
        // 删除视频数
        int delnum = 0;
        // 视频删除结果
        String delResult = null;
        // 视频渠道
        String playChannel = null;

        protected Integer doInBackground(Void... params) {

            videoDB = new VideoDB(context);
            for (VideoEntity x : ColudVideoFragment.coludVideoListCheckToDel) {

                videoid = x.getId();
                VedioDetail detail = JsonHelper.getVedioDetail(context, videoid, ExApplication.MEMBER_ID);
                String qn_key = detail.getQn_key();
                String url = detail.getUrl();

                // 隐藏云端视频
                if (!qn_key.equals("") && qn_key != null) {
                    delResult = HttpUtils.httpGet(delSerVideoUrl + "qn_key="
                            + qn_key);
                } else {
                    delResult = HttpUtils.httpGet(delSerVideoUrl + "url="
                            + url);
                }

                if (delResult != null) {
                    delnum++;
                }

                // 修改数据库视频上传状态,视频ID要去掉最后一个字符"a"
                videoDB.setupdateContect(
                        videoid.substring(0, videoid.length() - 1),
                        "hideinserver");

            }

            ColudVideoFragment.serverVideoListData = JsonHelper.getColudVideoList(1 + "");
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            // 取消加载
            DialogUtils.cancelLoadingDialog();
            ColudVideoFragment.coloudlVideoHandle.sendEmptyMessage(5);
            // 刷新云端数据
            ColudVideoFragment.coludVideoAdapter.update(ColudVideoFragment.serverVideoListData);
            ColudVideoFragment.coludVideoListCheckToDel.clear();
            Toast.makeText(context, "一共删除" + delnum + "个云端视频", 0).show();

        }
    }

    /**
     * 删除(云端)收藏视频
     */
    static class DelectVideoAsync extends AsyncTask<Void, Integer, Integer> {
        // TODO
        // 云端收藏视频ID
        String videoid = null;
        // 删除云端收藏视频数
        int delnum = 0;
        // 云端收藏视频删除结果
        String delResult = null;
        // 云端收藏视频渠道
        String playChannel = null;

        protected Integer doInBackground(Void... params) {

            for (VideoEntity x : CollectVideoFragment.collectVideoListCheckToDel) {
                videoid = x.getId();
                String result = JsonHelper.getCancelCollectVideo(videoid, ExApplication.MEMBER_ID);
                if (result.equals("s")) {
                    delnum++;
                }
            }
            CollectVideoFragment.videoList = JsonHelper.getCollectList(context, ExApplication.MEMBER_ID, "1");
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            // 取消加载
            DialogUtils.cancelLoadingDialog();
            CollectVideoFragment.collectVideoHandle.sendEmptyMessage(5);
            // 刷新云端收藏数据
            CollectVideoFragment.collectAdapter.update(CollectVideoFragment.videoList);
            CollectVideoFragment.collectVideoListCheckToDel.clear();
            Toast.makeText(context, "一共删除" + delnum + "个收藏视频", 0).show();

        }
    }

    /**
     * 计算录屏大师文件夹大小
     *
     * @param file
     * @return
     */
    private static int getFileSize(File file) { // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                int size = 0;
                for (File f : children)
                    size += getFileSize(f);
                return size;
            } else {// 如果是文件则直接返回其大小,以“兆”为单位
                int size = (int) (file.length() / 1024 / 1024);
                return size;
            }
        } else {

            return 0;
        }
    }

    /**
     * 计算内外SD卡剩余空间录屏大师路径和文件大小，
     */

    public static class ThreadText extends Thread {

        public void run() {
            // 设置空间提示
            DecimalFormat df = new DecimalFormat("0");

            double tmpsize = getFileSize(new File("/mnt/sdcard/LuPingDaShi"));
            SRFileSsizes = df.format(tmpsize);
            // 图片大小
            ScreenShotFragment.screenShotList = new ArrayList<ImageInfo>();
            ScreenShotFragment.screenShotList = ScreenShotFragment.GetPictureileName(context);
            // 获得内置SD卡剩余空间
            insdcardSurplusSize = Formatter.formatFileSize(context,
                    StoreDirUtil.getRomAvailableSize(context));
            insdcardSurplusSize = insdcardSurplusSize.substring(0, 3) + "G";
            // 获得内置SD卡空间总大小
            insdcardSizes = Formatter.formatFileSize(context,
                    StoreDirUtil.getRomTotalSize(context));
            insdcardSizes = insdcardSizes.substring(0, 3) + "G";
            // 发消息通知界面更改
            handlerViewChange.sendEmptyMessage(8);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        isINVideoManagerActivity = false;
        super.onDestroy();
    }
}

