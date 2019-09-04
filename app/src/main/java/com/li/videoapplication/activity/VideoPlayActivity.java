package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.fmscreenrecord.utils.MinUtil;
import com.li.videoapplication.Adapter.CommentAdapter;
import com.li.videoapplication.Adapter.FaceAdapter;
import com.li.videoapplication.Adapter.HisVideoAdapter;
import com.li.videoapplication.DB.DBManager;
import com.li.videoapplication.R;
import com.li.videoapplication.Service.DownLoadService;
import com.li.videoapplication.View.CircularImage;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.View.VerticalSeekBar;
import com.li.videoapplication.download.DownloadNotificationListener;
import com.li.videoapplication.download.DownloadTask;
import com.li.videoapplication.download.DownloadTaskManager;
import com.li.videoapplication.entity.CommentEntity;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.entity.VedioDetail;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.li.videoapplication.utils.DateUtils;
import com.li.videoapplication.utils.DensityUtil;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.MyApplication;
import com.li.videoapplication.utils.Screen;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.ToastUtils;
import com.mob.tools.utils.UIHandler;
import com.umeng.message.PushAgent;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static android.view.View.VISIBLE;


/**
 * 视频播放 页面*
 */
public class VideoPlayActivity extends Activity implements View.OnClickListener,
        BVideoView.OnPreparedListener, BVideoView.OnCompletionListener, BVideoView.OnErrorListener,
        View.OnTouchListener, GestureDetector.OnGestureListener, RefreshListView.IXListViewListener, Handler.Callback,
        PlatformActionListener, CommentAdapter.ReplyCallback {

    //2014-11-2 23:12 新加入的web放大按钮
    private Button webZoomBtn;


    private Button mZoomButton;
    private ImageView playBtn;
    private View view;
    private LayoutInflater inflater;
    private BVideoView mVV = null;//百度播放器
    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurPosition = null;
    private Button mPlayBtn = null;
    private RelativeLayout mController = null;
    private RelativeLayout mHeaderWrapper;
    private String AK = "GKotMsyWiwZO530yUGyiDEF3";
    private String SK = "ZY8T4a3SHYloQql1";

    //UI更新事件
    private final int UI_EVENT_PLAY_PREPARE = 0;
    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private final int UI_EVENT_PLAY_LAYOUT_GONE = 2;
    private final int UI_EVENT_PLAY_COMPLETE = 3;


    private RelativeLayout mViewHolder = null;
    private int mCurrentScape;
    public static final int LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
    public static final int PORTRAIT = Configuration.ORIENTATION_PORTRAIT;
    private int mLastPos = 0;
    //手势操作
    private VerticalSeekBar soundSb;
    public AudioManager audiomanage;
//    private int maxVolume, currentVolume;


    private String url = "";
    private String id = "";
    private String qn_key = "";
    private String qn_url = "";

    private int currentPage;

    private RefreshListView comment_rlv;
    private List<CommentEntity> comment_list;
    private List<CommentEntity> comment_connList;
    private CommentAdapter comment_adapter;

    private RefreshListView hisvideo_rlv;
    private List<VideoEntity> hisvideo_list;
    private List<VideoEntity> hisvideo_connList;
    private HisVideoAdapter hisvideo_adapter;

    private TextView gameDescription = null;

    private LinearLayout playLayout, commentLayout;
    private ImageButton backBtn, transcribBtn, downloadBtn;

    private TextView flowerTv, collectTv, shareTv, downloadTv;
    private ImageView loveIv, colectIv;
    private SimpleDateFormat dateFormat = null;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private int commentPageId = 0;
    private int hisVideoPageId = 0;

    private static int playTimes = 0;//播放次数，标记播放任务次数
    private static int commentCount = 0;//评论次数，标记评论任务次数

    private Button installBtn;

    /**
     * 2014.8.21 16：54
     * 播放横竖屏切换的头部和尾部控件
     */


    //小部件  显示视频的图片、标题、介绍、名称
    private RelativeLayout sTitleLayout;
    private CircularImage sheadImg;
    private TextView sTitle;
    private TextView sIntroduceTv;
    private TextView sName;
    private RelativeLayout sPlayLayout;

    //大部件
    private RelativeLayout bTitleLayout;
    private TextView bTitle;//走马灯效果的标题
    private CircularImage bheadImg;
    private TextView bName;
    private TextView bIntroduceTv;
    private ImageView shareIv, downloadIv, collectIv, fllowerIv;

    /**
     * 百度播放器控制条  布局  当前启用*
     */
    private RelativeLayout bPlayLayout;
    private ImageView bPlayBtn, bUpBtn, bDownBtn;
    private Button bZoomBtn;
    private TextView bCurrentTv, btotalTv;
    private SeekBar bProgress;

    /**
     * 判断是否初始准备播放中
     */
    private boolean isPreparing = true;
    /**
     * 判断是否刚开始
     */
    private boolean isBeginning = true;
    private boolean isVisibility = true;//播放器控制栏是否可见

    private VedioDetail detail = new VedioDetail();

//    private ImageView ;

    private Button faceBtn;
    private TextView submitTv;
    private EditText commentEdt;

    private String replyToString = "";
    private int replyToLength = 0;

    private DBManager dbManager;
    private DownloadTaskManager mDlTaskMng;

    //    private TextView titleTv;
    private ExApplication exApplication;


    private String recId = "";

    //游戏介绍展开收起
    private static final int DESCRIPTION_CONTENT_DESC_MAXLINE = 2;// 默认展示最大行数2行
    private static final int DESCRIPTION_UP_STATE = 1;// 收起状态
    private static final int DESCRIPTION_DOWN_STATE = 2;// 展开状态
    private static int mState = DESCRIPTION_UP_STATE;//默认收起状态

    MediaPlayer player;
    WebView webview;
    String file_url = "file:///android_asset/v.html?";
    private ImageView newHeadImg;
    private TextView newIntroduceTv;
    private TextView updataTimeTv;
    private TextView focusTv;
    private RelativeLayout videoRl;

    private List<Integer> faceList = null;//表情的资源ID集合集合
    private FaceAdapter faceAdapter = null;//表情适配器
    private GridView gridFace = null;
    private boolean hasFace = false;

    private String lastTime, currentTime = "";//时间
    private Context context;
    CompleteTaskUtils utils;

    //准备（七牛播放，百度播放器）的准备窗口
    private RelativeLayout prepareLayout;
    private ImageButton preparePlay;
    private ImageView prepareImg;
    /**
     * 七牛播放完全后，显示的（重播或播放下一个的）界面*
     */
    private RelativeLayout completeLayout;
    private ImageButton replaly, nextVideo;
    /**
     * 百度播放器的整个布局*
     */
    private RelativeLayout bPlayerView;

    //评论布局、TA的视频、游戏介绍
    private RelativeLayout commentRl, hisvideoRl, gameRl;
    private TextView commentTv, hisvideoTv, gameTv;
    private TextView commentCountTv;
    private ImageView commentIv, hisvideoIv, gameIv;
    private RelativeLayout commentLayoutRl, hisvideoLayoutRl, gameLayoutRl;//内容区块
    private ScrollView gameSv;

    public Dialog loginDialog = null;
    private int openState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_play);
        context = VideoPlayActivity.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        app = (MyApplication) getApplication();
        dbManager = new DBManager(this);
        mDlTaskMng = DownloadTaskManager.getInstance(this);
        exApplication = new ExApplication(this);
////        先判断是否打开： 0为禁止 1为允许
//        int flag =Settings.System.getInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
////        打开关闭，关闭打开：
//        Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,1);

        if (VideoPlayActivity.this.getIntent().getExtras() != null) {
            id = VideoPlayActivity.this.getIntent().getStringExtra("id");
            recId = id;
//            titleTv = (TextView) findViewById(R.id.video_play_title);
//            titleTv.setText(VideoPlayActivity.this.getIntent().getStringExtra("title"));
        }

        DialogUtils.createLoadingDialog(context, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetVedioDetail(id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetVedioDetail(id).execute();
        }
        initView();
        initPlayer();

//        startPlayAnimationFromNet(url, 0);
        if ("".equals(SharePreferenceUtil.getPreference(context, "11task_flag"))) {
            utils = new CompleteTaskUtils(this, "11");
            utils.completeMission();
            SharePreferenceUtil.setPreference(context, "11task_flag", "true");
        }

        ExApplication.upUmenEventValue(context, "视频点击次数", "video_click_count");
        ShareSDK.initSDK(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("onConfigurationChanged", "");
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("info", "landscape");
//            titleLayout.setVisibility(View.GONE);
            commentLayout.setVisibility(View.GONE);
            setMaxSize(true);
            bTitleLayout.setVisibility(View.GONE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("info", "portrait");
//            titleLayout.setVisibility(VISIBLE);
            commentLayout.setVisibility(VISIBLE);
            setMinSize(true);
            bTitleLayout.setVisibility(View.VISIBLE);
            bTitle.setFocusable(true);
            bTitle.requestFocus();
        }

    }

    private MyApplication app;

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LastPosition", mLastPos);
        Log.e("+++mLastPos", "onSaveInstanceState+++" + mLastPos + "");
        Log.e("+++qn_url", "onSaveInstanceState+++" + qn_url + "");
    }

    /**
     * Activity被系统杀死后再重建时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("LastPosition")) {
            mLastPos = savedInstanceState.getInt("LastPosition");
            Log.e("+++mLastPos", "onRestoreInstanceState+++" + mLastPos + "");
            Log.e("+++qn_url", "onRestoreInstanceState+++" + qn_url + "");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // 刷新UI
        // 通过Handler启动线程
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        });

        try {
            webview.getClass().getMethod("onResume").invoke(webview, (Object[]) null);
            if (!mVV.isPlaying()) {
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                if (!isPreparing) {
                    bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                }
                mVV.resume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //继续播放，在之前一个播放时间点上继续
        mLastPos = app.getPlayPos();
        qn_url = app.getPlayUrl();
        if ((mLastPos != 0) && (!qn_url.equals(""))) {
            startPlayAnimationFromNet(qn_url, mLastPos);
            Log.e("---url---mLastPos---", qn_url + "  " + mLastPos + "  ");
            completeLayout.setVisibility(View.GONE);
        }
        Log.e("+++mLastPos", "onResume+++" + mLastPos + "");

    }


    private void initView() {
        audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);

        inflater = LayoutInflater.from(VideoPlayActivity.this);
//        view = inflater.inflate(R.layout.video_play_head, null);
//        titleLayout = (RelativeLayout) findViewById(R.id.video_play_title_layout);
        playLayout = (LinearLayout) findViewById(R.id.play_layout);
        commentLayout = (LinearLayout) findViewById(R.id.comment_layout);

        submitTv = (TextView) findViewById(R.id.play_sumbit);
        submitTv.setOnClickListener(this);
        faceBtn = (Button) findViewById(R.id.face_btn);
        faceBtn.setOnClickListener(this);
        gridFace = (GridView) findViewById(R.id.gridview_face);
        commentEdt = (EditText) findViewById(R.id.play_edt);
        commentEdt.setOnClickListener(this);
        commentEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    gridFace.setVisibility(View.GONE);
                    faceBtn.setBackgroundResource(R.drawable.face_nomal);
                    hasFace = false;
                }
            }
        });

        videoRl = (RelativeLayout) findViewById(R.id.video_play_comment_layout);

        initHeadView(view);
        initControls();
        backBtn = (ImageButton) findViewById(R.id.video_play_back);
        backBtn.setOnClickListener(this);
        downloadBtn = (ImageButton) findViewById(R.id.video_play_download);
        downloadBtn.setOnClickListener(this);
//        transcribBtn = (ImageButton) findViewById(R.id.video_play_transcribe);
//        transcribBtn.setOnClickListener(this);

        comment_rlv = (RefreshListView) findViewById(R.id.play_comment_list);
        comment_rlv.setPullLoadEnable(true);
        comment_rlv.setXListViewListener(this);
        comment_rlv.setPullRefreshEnable(false);
//        comment_rlv.addHeaderView(view);
        comment_rlv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    commentEdt.clearFocus();
                    gridFace.setVisibility(View.GONE);
                    faceBtn.setBackgroundResource(R.drawable.face_nomal);
                    hasFace = false;
                } catch (Exception e) {
                    Log.e("e:", e.toString());
                }
                return false;
            }
        });
        comment_list = new ArrayList<CommentEntity>();
        comment_connList = new ArrayList<CommentEntity>();
        comment_adapter = new CommentAdapter(VideoPlayActivity.this, comment_list, "videoplay", this);
        comment_rlv.setAdapter(comment_adapter);

        hisvideo_rlv = (RefreshListView) findViewById(R.id.play_hisvideo_list);
        hisvideo_rlv.setPullLoadEnable(true);
        hisvideo_rlv.setXListViewListener(this);
        hisvideo_rlv.setPullRefreshEnable(false);
        hisvideo_list = new ArrayList<VideoEntity>();
        hisvideo_connList = new ArrayList<VideoEntity>();
        hisvideo_adapter = new HisVideoAdapter(VideoPlayActivity.this, hisvideo_list);
        hisvideo_rlv.setAdapter(hisvideo_adapter);


        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//不加上白边

        //游戏介绍、评论布局
        commentRl = (RelativeLayout) findViewById(R.id.video_play_comment_Rl);
        commentRl.setOnClickListener(this);
        hisvideoRl = (RelativeLayout) findViewById(R.id.video_play_hisvideo_Rl);
        hisvideoRl.setOnClickListener(this);
        gameRl = (RelativeLayout) findViewById(R.id.video_play_gameinfo_Rl);
        gameRl.setOnClickListener(this);

        commentTv = (TextView) findViewById(R.id.video_play_comment_tv);
        commentCountTv = (TextView) findViewById(R.id.video_play_comment_count);
        hisvideoTv = (TextView) findViewById(R.id.video_play_hisvideo_tv);
        gameTv = (TextView) findViewById(R.id.video_play_gameinfo_tv);
        commentIv = (ImageView) findViewById(R.id.video_play_comment_iv);
        hisvideoIv = (ImageView) findViewById(R.id.video_play_hisvideo_iv);
        gameIv = (ImageView) findViewById(R.id.video_play_gameinfo_iv);

        commentLayoutRl = (RelativeLayout) findViewById(R.id.video_play_comment_layout_rl);
        hisvideoLayoutRl = (RelativeLayout) findViewById(R.id.video_play_hisvideo_layout_rl);
        gameLayoutRl = (RelativeLayout) findViewById(R.id.video_play_gameinfo_layout);

        gameSv = (ScrollView) findViewById(R.id.video_play_gameinfo_sv);
    }

    /**
     * 视频窗口的宽和高
     */
    private int playerWidth, playerHeight;
    /**
     * 视频播放时间,视频播放的总时长
     */
    private int playingTime, videoTotalTime;
    /**
     * 手势改变视频进度,音量,亮度
     */
    private RelativeLayout gesture_volume_layout, gesture_bright_layout;// 音量控制布局,亮度控制布局
    private TextView geture_tv_volume_percentage, geture_tv_bright_percentage;// 音量百分比,亮度百分比
    private ImageView gesture_iv_player_volume, gesture_iv_player_bright;// 音量图标,亮度图标
    private RelativeLayout gesture_progress_layout;// 进度图标
    private TextView geture_tv_progress_time;// 播放时间进度
    private ImageView gesture_iv_progress;// 快进或快退标志
    private GestureDetector gestureDetector;
    private AudioManager audiomanager;
    private int maxVolume, currentVolume;
    private float mBrightness = -1f; // 亮度
    private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
    private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
    private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
    private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量,3.调节亮度
    private static final int GESTURE_MODIFY_PROGRESS = 1;
    private static final int GESTURE_MODIFY_VOLUME = 2;
    private static final int GESTURE_MODIFY_BRIGHT = 3;

    /**
     * 初始化播放器手势操作控件
     */
    private void initControls() {
        // ****************音量/进度/亮度*********************
        gesture_volume_layout = (RelativeLayout) findViewById(R.id.gesture_volume_layout);
        gesture_bright_layout = (RelativeLayout) findViewById(R.id.gesture_bright_layout);
        gesture_progress_layout = (RelativeLayout) findViewById(R.id.gesture_progress_layout);
        geture_tv_progress_time = (TextView) findViewById(R.id.geture_tv_progress_time);
        geture_tv_volume_percentage = (TextView) findViewById(R.id.geture_tv_volume_percentage);
        geture_tv_bright_percentage = (TextView) findViewById(R.id.geture_tv_bright_percentage);
        gesture_iv_progress = (ImageView) findViewById(R.id.gesture_iv_progress);
        gesture_iv_player_volume = (ImageView) findViewById(R.id.gesture_iv_player_volume);
        gesture_iv_player_bright = (ImageView) findViewById(R.id.gesture_iv_player_bright);
        gestureDetector = new GestureDetector(this, this); //需要实现OnGestureListener监听
        mController.setLongClickable(true);
        gestureDetector.setIsLongpressEnabled(true);
        mController.setOnTouchListener(this);//需要实现OnTouchListener监听
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
    }


    /**
     * 定义头部
     *
     * @param view
     */
    private void initHeadView(View view) {
        mZoomButton = (Button) findViewById(R.id.zoom_btn);
        webZoomBtn = (Button) findViewById(R.id.web_zoom_btn);
        webZoomBtn.setOnClickListener(this);
        playBtn = (ImageView) findViewById(R.id.media_play_btn);
        mCurrentScape = PORTRAIT;
        mVV = (BVideoView) findViewById(R.id.video_view);
        mProgress = (SeekBar) findViewById(R.id.media_progress);
        mDuration = (TextView) findViewById(R.id.time_total);
        mCurPosition = (TextView) findViewById(R.id.time_current);
        mHeaderWrapper = (RelativeLayout) findViewById(R.id.header_wrapper);

        /**播放器比例为16:9**/
        int playwidth = Screen.getScreenWidth(getWindowManager());
        int playheith = getResources().getDimensionPixelSize(R.dimen.player_height);
        float ratio = (float) 9 / (float) 16;
        playheith = (int) (playwidth * ratio);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(playwidth, playheith);
        mHeaderWrapper.setLayoutParams(param);
        mCurrentScape = PORTRAIT;

        mPlayBtn = (Button) findViewById(R.id.play_btn);

        prepareLayout = (RelativeLayout) findViewById(R.id.prepare_layout);
        preparePlay = (ImageButton) findViewById(R.id.prepare_ib);
        preparePlay.setOnClickListener(this);
        prepareImg = (ImageView) findViewById(R.id.prepare_iv);

        completeLayout = (RelativeLayout) findViewById(R.id.complete_layout);
        bPlayerView = (RelativeLayout) findViewById(R.id.b_player_view);
        replaly = (ImageButton) findViewById(R.id.replay_ib);
        replaly.setOnClickListener(this);
        nextVideo = (ImageButton) findViewById(R.id.next_video_ib);
        nextVideo.setOnClickListener(this);

        mController = (RelativeLayout) findViewById(R.id.controlbar);
        mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);
        mViewHolder.setOnTouchListener(this);

        flowerTv = (TextView) findViewById(R.id.play_flower_txt);
        collectTv = (TextView) findViewById(R.id.play_collect_txt);
        shareTv = (TextView) findViewById(R.id.play_share_tv);
        shareTv.setOnClickListener(this);

        loveIv = (ImageView) findViewById(R.id.play_flower_iv);//点赞
        loveIv.setOnClickListener(this);
        colectIv = (ImageView) findViewById(R.id.play_collect_iv);//收藏
        colectIv.setOnClickListener(this);
        downloadTv = (TextView) findViewById(R.id.play_download_txt);//下载
        downloadTv.setOnClickListener(this);
        installBtn = (Button) findViewById(R.id.play_head_install);//安装
        installBtn.setOnClickListener(this);

        //小屏显示布局 视频标题 当前启用
        sTitleLayout = (RelativeLayout) findViewById(R.id.control_s_layout);
        sheadImg = (CircularImage) findViewById(R.id.video_play_head_simg);
//        ExApplication.imageLoader.displayImage("http://img5.imgtn.bdimg.com/it/u=1726668589,450500620&fm=11&gp=0.jpg", sheadImg, ExApplication.getOptions());
        sTitle = (TextView) findViewById(R.id.video_play_head_introduce_stitle);
        sIntroduceTv = (TextView) findViewById(R.id.video_play_head_sintroduce);
        sName = (TextView) findViewById(R.id.video_play_head_sname);
        sPlayLayout = (RelativeLayout) findViewById(R.id.video_play_head_play_s_layout);

        newHeadImg = (ImageView) findViewById(R.id.video_play_head_nimg);
        newHeadImg.setOnClickListener(this);
        newIntroduceTv = (TextView) findViewById(R.id.video_play_nintroduce);
        updataTimeTv = (TextView) findViewById(R.id.video_play_updataTime);

        focusTv = (TextView) findViewById(R.id.video_play_focus);//关注按钮
        if (ExApplication.MEMBER_ID.equals("")) {//未登录，关注按钮不显示
            focusTv.setVisibility(View.GONE);
        } else if (ExApplication.MEMBER_ID.equals(detail.getMember_id())) {//登录时，如果玩家即为用户，关注按钮不显示
            focusTv.setVisibility(View.GONE);
        } else {
            focusTv.setVisibility(View.VISIBLE);
        }
        focusTv.setOnClickListener(this);


        //大部件
        bTitleLayout = (RelativeLayout) findViewById(R.id.video_play_head_play_btitleLL);
        bTitle = (TextView) findViewById(R.id.video_play_head_play_btitle);
        bheadImg = (CircularImage) findViewById(R.id.video_play_head_bimg);
//        ExApplication.imageLoader.displayImage("http://img5.imgtn.bdimg.com/it/u=1726668589,450500620&fm=11&gp=0.jpg", bheadImg, ExApplication.getOptions());
        bName = (TextView) findViewById(R.id.video_play_head_bname);

        bIntroduceTv = (TextView) findViewById(R.id.video_play_head_b_introduce);
//        private ImageView shareIv,downloadIv,collectIv,fllowerIv;

        //百度播放器控制条  布局  当前启用
        bPlayLayout = (RelativeLayout) findViewById(R.id.video_play_head_play_b_layout);
        bPlayBtn = (ImageView) findViewById(R.id.b_btn_play);
        bPlayBtn.setOnClickListener(this);
        bZoomBtn = (Button) findViewById(R.id.b_zoom_btn);
        bZoomBtn.setOnClickListener(this);
        bCurrentTv = (TextView) findViewById(R.id.b_time_current);
        btotalTv = (TextView) findViewById(R.id.b_time_total);
        bProgress = (SeekBar) findViewById(R.id.big_media_progress);

        shareIv = (ImageView) findViewById(R.id.video_play_head_b_share);
        shareIv.setOnClickListener(this);
        downloadIv = (ImageView) findViewById(R.id.video_play_head_b_download);
        downloadIv.setOnClickListener(this);
        collectIv = (ImageView) findViewById(R.id.video_play_head_b_star);
        collectIv.setOnClickListener(this);
        fllowerIv = (ImageView) findViewById(R.id.video_play_head_b_flower);
        fllowerIv.setOnClickListener(this);
        if (ExApplication.MEMBER_ID.equals("") && SharePreferenceUtil.getPreference(context, id + "unlogin_flower").equals("flowered")) {
            loveIv.setBackgroundResource(R.drawable.love_pressed);
        } else if (ExApplication.MEMBER_ID.equals("") && SharePreferenceUtil.getPreference(context, id + "unlogin_flower").equals("unflowered")) {
            loveIv.setBackgroundResource(R.drawable.love_normal);
        }
        gameDescription = (TextView) findViewById(R.id.game_description_view);

        soundSb = (VerticalSeekBar) findViewById(R.id.video_play_head_sound);
        maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  //获取系统最大音量
        soundSb.setMax(maxVolume);   //拖动条最高值与系统最大声匹配
        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
        soundSb.setProgress(currentVolume);
        soundSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() //调音监听器
        {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
                soundSb.setProgress(currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub


            }
        });

    }

    private void initPlayer() {
        BVideoView.setAKSK(AK, SK);
        mZoomButton.setOnClickListener(this);
        mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mVV.showCacheInfo(true);
        mPlayBtn.setOnClickListener(this);
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        playBtn.setOnClickListener(this);
        registerCallbackForControls();
    }

    /**
     * 根据qn_url和时间点来启动百度播放器*
     */
    private void startPlayAnimationFromNet(final String url, final int start) {
        mVV.setVideoPath(url);
        mVV.seekTo(start);
        mVV.start();
        mPlayBtn.setBackgroundResource(R.drawable.pause_btn_style);
        hideControls();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //开始进入视频播放页面 后延时隐藏视频播放信息半透明布局
        mUIHandler.sendEmptyMessageDelayed(UI_EVENT_PLAY_LAYOUT_GONE, 5000L);
        mVV.setLogLevel(5);

        if (start == 0 && isBeginning && isPreparing) {

            //观看3个视频
            if (!"已完成".equals(SharePreferenceUtil.getPreference(context, "19taskflag"))) {
                utils = new CompleteTaskUtils(this, "19");
                utils.completeMission();
            }

            if (detail != null) {
                VideoEntity video = new VideoEntity();
                video.setId(recId);
                String aa = detail.getTime_length();
                video.setTime(aa);
                video.setSimg_url(detail.getFlagPath());
                video.setFlower(detail.getFlower_count());
                video.setComment(detail.getComment_count());
                video.setViewCount(detail.getView_count());
                video.setTitle_content(detail.getName());
                //添加观看记录
                dbManager.addRecordVideo(video);
            }

        }
    }


    public void hideControls() {
        mController.setVisibility(View.INVISIBLE);
    }

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_EVENT_PLAY_PREPARE://播放初始准备状态
                    webview.setVisibility(View.GONE);
                    bPlayerView.setVisibility(VISIBLE);
                    exApplication.imageLoader.displayImage(detail.getFlagPath(), prepareImg, exApplication.getOptions());
                    bPlayLayout.setVisibility(VISIBLE);
                    prepareLayout.setVisibility(VISIBLE);
                    completeLayout.setVisibility(View.GONE);
                    mController.setVisibility(View.GONE);

                    bCurrentTv.setText("00:00");
                    btotalTv.setText(detail.getTime_length());
                    break;
                case UI_EVENT_UPDATE_CURRPOSITION://更新当前的播放位置
                    int currPosition = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();
                    updateTextViewWithTimeFormat(mCurPosition, currPosition);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(currPosition);

                    updateTextViewWithTimeFormat(bCurrentTv, currPosition);
                    updateTextViewWithTimeFormat(btotalTv, duration);
                    bProgress.setMax(duration);
                    bProgress.setProgress(currPosition);

                    mUIHandler.sendEmptyMessageDelayed(
                            UI_EVENT_UPDATE_CURRPOSITION, 200);
                    break;
                case UI_EVENT_PLAY_LAYOUT_GONE://隐藏视频播放信息半透明布局（百度播放器控制条）
                    bPlayLayout.setVisibility(View.GONE);
                    isVisibility = false;
                    break;
                case UI_EVENT_PLAY_COMPLETE://播放完全状态
                    completeLayout.setVisibility(VISIBLE);
                    mController.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    private void registerCallbackForControls() {

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                updateTextViewWithTimeFormat(mCurPosition, progress);
                updateTextViewWithTimeFormat(bCurrentTv, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekPosition = seekBar.getProgress();
                mVV.seekTo(seekPosition);
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        };
        mProgress.setOnSeekBarChangeListener(seekBarChangeListener);
        bProgress.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private void updateTextViewWithTimeFormat(TextView view, int second) {
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format(Locale.CHINA, "%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format(Locale.CHINA, "%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通过dispatchTouchEvent每次ACTION_DOWN事件中动态判断非EditText本身区域的点击事件，然后在事件中进行屏蔽键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.play_btn:
                if (mVV.isPlaying()) {//暂停播放
                    mPlayBtn.setBackgroundResource(R.drawable.play_btn_style);
                    mVV.pause();
                } else {//继续播放
                    mPlayBtn.setBackgroundResource(R.drawable.pause_btn_style);
                    mVV.resume();
                }
                mController.setVisibility(VISIBLE);
                break;

            case R.id.media_play_btn:
                if (isBeginning) {
//                    startPlayAnimationFromNet(url, 0);
                    mController.setVisibility(VISIBLE);
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                    mVV.pause();
                    isBeginning = false;
                } else {
                    if (mVV.isPlaying()) {
                        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                        mVV.pause();
                    } else {
                        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                        mVV.resume();
                    }
                    mController.setVisibility(VISIBLE);
                }
                break;

            case R.id.b_btn_play://百度播放器 左下角播放按钮
                if (isBeginning || isPreparing) {
                    prepareLayout.setVisibility(View.GONE);
                    bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                    startPlayAnimationFromNet(qn_url, 0);
                    if (mCurrentScape == LANDSCAPE) {//全屏播放时，显示操控窗口
                        mController.setVisibility(View.VISIBLE);
                    } else if (mCurrentScape == PORTRAIT) {//竖屏播放时，隐藏操控窗口
                        mController.setVisibility(View.GONE);
                    }

                    isBeginning = false;
                    isPreparing = false;
                } else {
                    if (mVV.isPlaying()) {
                        bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                        mVV.pause();
                    } else {
                        bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                        mVV.resume();
                    }
                }
                break;
            case R.id.web_zoom_btn:
                if (mCurrentScape == LANDSCAPE) {
//                    titleLayout.setVisibility(VISIBLE);
                    commentLayout.setVisibility(VISIBLE);
                    setMinSize(false);

                } else {
                    setMaxSize(false);
//                    titleLayout.setVisibility(View.GONE);
                    commentLayout.setVisibility(View.GONE);
                    flowerTv.clearFocus();
                    collectTv.clearFocus();
                    downloadTv.clearFocus();
                    installBtn.clearFocus();
                    shareIv.clearFocus();

                }
                break;
            case R.id.b_zoom_btn:
                if (mCurrentScape == LANDSCAPE) {//横屏变为竖屏
//                    titleLayout.setVisibility(VISIBLE);
                    commentLayout.setVisibility(VISIBLE);
                    bZoomBtn.setBackgroundResource(R.drawable.btn_big);
                    setMinSize(false);

                } else if (mCurrentScape == PORTRAIT) {//竖屏变为横屏
//                    titleLayout.setVisibility(View.GONE);
                    commentLayout.setVisibility(View.GONE);
                    setMaxSize(false);
                    flowerTv.clearFocus();
                    collectTv.clearFocus();
                    downloadTv.clearFocus();
                    installBtn.clearFocus();
                    shareIv.clearFocus();
                    bZoomBtn.setBackgroundResource(R.drawable.btn_samll);
                }
                break;
            case R.id.prepare_ib://初始状态，准备完毕进行播放
                prepareLayout.setVisibility(View.GONE);
                bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                startPlayAnimationFromNet(qn_url, 0);
                if (mCurrentScape == LANDSCAPE) {//全屏播放时，显示操控窗口
                    mController.setVisibility(View.VISIBLE);
                } else if (mCurrentScape == PORTRAIT) {//竖屏播放时，隐藏操控窗口
                    mController.setVisibility(View.GONE);
                }

                isBeginning = false;
                isPreparing = false;
                break;
            case R.id.replay_ib://重新播放
                startPlayAnimationFromNet(qn_url, 0);
                completeLayout.setVisibility(View.GONE);
                isBeginning = false;
                isPreparing = false;
                break;
            case R.id.next_video_ib://播放下一个视频
                completeLayout.setVisibility(View.GONE);
                DialogUtils.createLoadingDialog(context, "");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetNextVideoId(id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetNextVideoId(id).execute();
                }
                break;
            case R.id.video_play_back:
                stopPlay();
                VideoPlayActivity.this.finish();
                break;
            case R.id.video_play_download://（断点）离线下载按钮

                String saveFolder = "videoapplication/download";//文件最终保存的文件夹
                String savePath = Environment.getExternalStorageDirectory() + File.separator + saveFolder;
                String download_url = "http://7xiiwo.com2.z0.glb.qiniucdn.com/" + detail.getQn_key() + "?attname=";//下载URL
                String saveName = detail.getName() + ".mp4";
                String notifyTitle = detail.getName();
                String flagPath = detail.getFlagPath();
//                System.out.println("download--download_url=====" + download_url);
//                System.out.println("download--savePath=====" + savePath);
//                System.out.println("download--saveName=====" + saveName);
//                System.out.println("download--notifyTitle=====" + notifyTitle);
//                System.out.println("download--flagPath=====" + flagPath);
                
                if (mDlTaskMng.isUrlDownloaded(download_url)) {
                    ToastUtils.showToast(this, "你已经下载过该视频了");
                    return;
                }

                // 获取当前网络环境
                int netType = MinUtil.getNetworkType(context);
                if (netType == 0) {
                    MinUtil.showToast(context, "当前网络不可用，请检查后再上传.");
                    return;
                } else if (netType == 1) {// wifi

                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("注意")
                            .setMessage("当前手机处于非WIFI环境，下载视频将消耗一定的手机流量,是否下载视频？")
                            .setPositiveButton("确定",
                                    new android.content.DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    }).show();
                }

                DownloadTask downloadTask = new DownloadTask(
                        download_url,
                        savePath,
                        saveName,
                        notifyTitle,
                        flagPath);
                DownloadTaskManager.getInstance(context).registerListener(downloadTask,
                        new DownloadNotificationListener(context, downloadTask));
                DownloadTaskManager.getInstance(context).startDownload(downloadTask);

                break;
//            case R.id.video_play_transcribe:
//                intent = new Intent();
//                intent.setClassName(this, "com.fmscreenrecord.activity.FMMainActivity");
//                startActivity(intent);
//                break;
            case R.id.play_share_tv://分享
                showShare();
                break;
            case R.id.play_flower_iv://点赞 或 取消点赞

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new FlowerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new FlowerTask().execute();
                }
                break;
            case R.id.play_collect_iv://收藏 或 取消收藏

                if (ExApplication.MEMBER_ID.equals("")) {
                    ToastUtils.showToast(this, "请先登陆");
                    createLoginDialog(context);
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new CollectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new CollectTask().execute();
                }
                break;
            case R.id.video_play_head_nimg://（点击）玩家头像（跳转到玩家的信息界面）
                intent = new Intent(VideoPlayActivity.this, PersonalInfoActivity.class);
                intent.putExtra("flag", "videoplay");
                intent.putExtra("member_id", detail.getMember_id());
                startActivity(intent);
                break;
            case R.id.video_play_focus:// 关注玩家 或 取消关注玩家
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitFocusTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitFocusTask().execute();
                }
                break;
            case R.id.play_sumbit:
                //限制同一视频一分钟只能评论一次
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                currentTime = simpleDateFormat.format(date);
                if (detail != null) {
                    lastTime = SharePreferenceUtil.getPreference(VideoPlayActivity.this, "videocansumbit" + detail.getId());
                } else {
                    ToastUtils.showToast(this, "无法评论");
                }
                if (lastTime == "") {
                    if (TextUtils.isEmpty(commentEdt.getText().toString().trim())) {
                        ToastUtils.showToast(this, "评论不能为空");
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new SubmitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new SubmitTask().execute();
                    }

                    SharePreferenceUtil.setPreference(VideoPlayActivity.this, "videocansumbit" + detail.getId(), currentTime);
                } else {
                    if (isCanSumbit(lastTime, currentTime)) {
                        if (TextUtils.isEmpty(commentEdt.getText().toString().trim())) {
                            ToastUtils.showToast(this, "评论不能为空");
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new SubmitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new SubmitTask().execute();
                        }
                    } else {
                        ToastUtils.showToast(this, "60秒内只能评论一次哦！");
                        return;
                    }
                    SharePreferenceUtil.setPreference(VideoPlayActivity.this, "videocansumbit" + detail.getId(), currentTime);
                }
                //取消输入框焦点
                videoRl.setFocusable(true);
                videoRl.setFocusableInTouchMode(true);
                videoRl.requestFocus();
                gridFace.setVisibility(View.GONE);
                faceBtn.setBackgroundResource(R.drawable.face_nomal);
                hasFace = false;
                break;

            case R.id.play_download_txt://下载视频
                break;
            case R.id.video_play_head_b_download:

                if (dbManager.isDownloadVideoExist(id)) {
                    ToastUtils.showToast(this, "你已经下载过该视频了");
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new DownloadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new DownloadAsyncTask().execute();
                }

                if (youkuDetail != null) {
                    String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + "";
                    Intent updateIntent = new Intent(
                            VideoPlayActivity.this,
                            DownLoadService.class);
                    updateIntent.putExtra("id", id);
                    updateIntent.putExtra("name", name);
                    updateIntent.putExtra("url", youkuDetail.getUrl());
                    updateIntent.putExtra("img", youkuDetail.getFlagPath());
                    updateIntent.putExtra("title", youkuDetail.getName());
                    startService(updateIntent);
                }

                break;
            case R.id.play_head_install://安装游戏
                if (detail != null) {
                    installBtn.setClickable(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new DownloadCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new DownloadCountTask().execute();
                    }
                    Uri uri = Uri.parse(detail.getGameDownloadUrl());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    ExApplication.upUmenEventValue(context, "安装游戏次数", "Install_game_count");
                }
                break;
            case R.id.face_btn:
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFace == false) {
                    //在点击表情按钮后隐藏软键盘
                    im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    int resouseId = 0;
                    faceList = new ArrayList<Integer>();
                    Field field;
//                    String[] faceArray = getResources().getStringArray(R.array.faceArray);
                    String[] faceArray = getResources().getStringArray(R.array.expressionArray);
                    for (int i = 0; i < 34; i++) {
                        try {
                            // 从R.drawable类中获得相应资源ID（静态变量）的Field对象
                            field = R.drawable.class.getDeclaredField(faceArray[i]);
                            resouseId = Integer.parseInt(field.get(null).toString());
                            faceList.add(resouseId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    faceAdapter = new FaceAdapter(VideoPlayActivity.this, faceList, commentEdt);
                    gridFace.setAdapter(faceAdapter);
                    gridFace.setVisibility(View.VISIBLE);
                    faceBtn.setBackgroundResource(R.drawable.face_touch);
                    hasFace = true;
                } else {
                    //再一次点击表情按钮显示键盘隐藏表情
                    im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    gridFace.setVisibility(View.GONE);
                    faceBtn.setBackgroundResource(R.drawable.face_nomal);
                    hasFace = false;
                }
                break;
            case R.id.play_edt:
                hasFace = false;
                gridFace.setVisibility(View.GONE);
                faceBtn.setBackgroundResource(R.drawable.face_nomal);
                break;
            case R.id.video_play_comment_Rl:
                showComment();
                break;
            case R.id.video_play_hisvideo_Rl:
                showHisVideo();
                break;
            case R.id.video_play_gameinfo_Rl:
                showGameInfo();
                break;
        }
    }

    public void showComment() {
        commentLayoutRl.setVisibility(View.VISIBLE);
        hisvideoLayoutRl.setVisibility(View.GONE);
        gameLayoutRl.setVisibility(View.GONE);

        comment_rlv.setVisibility(View.VISIBLE);
        commentTv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        commentCountTv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        commentIv.setVisibility(View.VISIBLE);

        hisvideo_rlv.setVisibility(View.GONE);
        hisvideoTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        hisvideoIv.setVisibility(View.GONE);

        gameTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        gameIv.setVisibility(View.GONE);

        currentPage = 0;
    }

    public void showHisVideo() {
        commentLayoutRl.setVisibility(View.GONE);
        hisvideoLayoutRl.setVisibility(View.VISIBLE);
        gameLayoutRl.setVisibility(View.GONE);

        comment_rlv.setVisibility(View.GONE);
        commentTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        commentCountTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        commentIv.setVisibility(View.GONE);

        hisvideo_rlv.setVisibility(View.VISIBLE);
        hisvideoTv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        hisvideoIv.setVisibility(View.VISIBLE);

        gameTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        gameIv.setVisibility(View.GONE);

        currentPage = 1;

        if (hisvideo_list.size() == 0) {
            hisVideoPageId = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetUploadListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetUploadListTask().execute();
            }
        }

    }

    public void showGameInfo() {
        commentLayoutRl.setVisibility(View.GONE);
        hisvideoLayoutRl.setVisibility(View.GONE);
        gameLayoutRl.setVisibility(View.VISIBLE);

        comment_rlv.setVisibility(View.GONE);
        commentTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        commentCountTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        commentIv.setVisibility(View.GONE);

        hisvideo_rlv.setVisibility(View.GONE);
        hisvideoTv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        hisvideoIv.setVisibility(View.GONE);

        gameTv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        gameIv.setVisibility(View.VISIBLE);

        currentPage = 2;
    }

    /**
     * 自定义登陆对话框
     *
     * @param context
     */
    public void createLoginDialog(final Context context) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.login_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.login_dialog_view);// 加载布局
        // main.xml中的ImageView
        Button loginBtn = (Button) v.findViewById(R.id.login_dialog_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                intent.putExtra("flag", "videoplay");
                startActivityForResult(intent, 200);
            }
        });
        ImageButton qqBtn = (ImageButton) v.findViewById(R.id.video_play_login_qq_ib);
        qqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openState = 1;
                authorize(new QQ(context));
                ExApplication.upUmenEventValue(getApplicationContext(), "QQ登陆次数", "qq_login_count");
            }
        });
        ImageButton weiboBtn = (ImageButton) v.findViewById(R.id.video_play_login_weibo_ib);
        weiboBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openState = 2;
                authorize(new SinaWeibo(context));
                ExApplication.upUmenEventValue(getApplicationContext(), "微博登陆次数", "sina_login_count");
            }
        });
        ImageButton wechatBtn = (ImageButton) v.findViewById(R.id.video_play_login_wechat_ib);
        wechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openState = 3;
                authorize(new Wechat(context));
                ExApplication.upUmenEventValue(getApplicationContext(), "微信登陆次数", "wechat_login_count");
            }
        });
        loginDialog = new Dialog(context, R.style.login_dialog);// 创建自定义样式dialog

        loginDialog.setCancelable(true);// 可以用“返回键”取消
        loginDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loginDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cancelLoginDialog();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void cancelLoginDialog() {
        loginDialog.cancel();
    }

    /**
     * 三方登陆
     */
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    private void authorize(Platform plat) {
        if (plat == null) {
            popupOthers();
            return;
        }

        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                login(plat.getName(), userId, null);
                return;
            }
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    private void popupOthers() {
        Dialog dlg = new Dialog(this);
        View dlgView = View.inflate(this, R.layout.other_plat_dialog, null);
        View tvFacebook = dlgView.findViewById(R.id.tvFacebook);
        tvFacebook.setTag(dlg);
        tvFacebook.setOnClickListener(this);
        View tvTwitter = dlgView.findViewById(R.id.tvTwitter);
        tvTwitter.setTag(dlg);
        tvTwitter.setOnClickListener(this);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(dlgView);
        dlg.show();
    }

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        System.out.println("res========" + res.toString());
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            login(platform.getName(), platform.getDb().getUserId(), res);
        }

        if (openState == 1) {
            System.out.println("qq++++++++++");
            String openId = platform.getDb().getUserId();
            String nickname = res.get("nickname").toString();
            String sex = res.get("gender").toString();
            String location = res.get("province").toString() + res.get("city").toString();
            String figureurl = res.get("figureurl").toString();
            new OtherLoginTask(openId, nickname, sex, location, figureurl).execute().toString();
        }
        if (openState == 2) {
            System.out.println("sina++++++++++");
            String openId = res.get("id").toString();
            String nickname = res.get("screen_name").toString();
            String sex = ((res.get("gender").equals("m")) ? "男" : "女").toString();
            String location = res.get("location").toString();
            String figureurl = res.get("avatar_large").toString();
            new OtherLoginTask(openId, nickname, sex, location, figureurl).execute().toString();
        }

        if (openState == 3) {
            System.out.println("weixin++++++++++");
            String openId = res.get("unionid").toString();
            String nickname = res.get("nickname").toString();
            String sex = res.get("sex").toString();
            String location = res.get("province").toString() + " " + res.get("city").toString();
            String figureurl = res.get("headimgurl").toString();
            new OtherLoginTask(openId, nickname, sex, location, figureurl).execute().toString();
        }

    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND: {
                Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {

                String text = getString(R.string.logining, msg.obj);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_ERROR: {
                Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_COMPLETE: {
                Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
            }
            break;
        }
        return false;
    }

    private UserEntity userEntity;

    private class OtherLoginTask extends AsyncTask<Void, Void, String> {
        String openId = "";
        String name = "";
        String sex;
        String location;
        String figureurl;

        public OtherLoginTask(String openId, String name, String sex, String location, String figureurl) {
            this.openId = openId;
            this.name = name;
            this.sex = sex;
            this.location = location;
            this.figureurl = figureurl;
        }

        @Override
        protected String doInBackground(Void... voids) {
            userEntity = JsonHelper.getOtherUser(VideoPlayActivity.this, openId, name, sex, location, figureurl);
            if (userEntity != null) {
                ExApplication.MEMBER_ID = userEntity.getId();
                return "s";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                if (openState == 1) {
                    ExApplication.upUmenEventValue(getApplicationContext(), "QQ登陆成功次数", "qq_login_success_count");
                } else {
                    ExApplication.upUmenEventValue(getApplicationContext(), "微博登陆成功次数", "sina_login_success_count");
                }
                ExApplication.upUmenEventValue(getApplicationContext(), "登陆成功次数", "login_success_count");
                cancelLoginDialog();
            }
        }
    }

    /**
     * 全屏横版*
     */
    private void setMaxSize(boolean isAuto) {
//        if (!isAuto){
        if (Build.VERSION.SDK_INT >= 9) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
//        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if(Build.MODEL.equals("M040")){
//            if(!mSharedPreferences.getBoolean("Meizu",false)){
//                SuperToast superToast = new SuperToast(this);
//                superToast.setDuration(12000);
//                superToast.setText("魅族某些版本固件可能存在兼容性问题，建议您升级到最新固件");
//                superToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
//                superToast.show();
//                mSharedPreferences.edit().putBoolean("Meizu",true).commit();
//            }
//        }
        int playwidth = Screen.getScreenWidth(getWindowManager());
        int playheith = Screen.getScreenHeight(getWindowManager());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(playwidth, playheith);
        mHeaderWrapper.setLayoutParams(param);
        webview.setLayoutParams(param);
        mCurrentScape = LANDSCAPE;
        if (mVV.isPlaying()) {//转换横屏且正在播放时，显示播放器手势操作控件
            mController.setVisibility(View.VISIBLE);
        } else {//转换横屏且不在播放时，隐藏播放器手势操作控件
            mController.setVisibility(View.GONE);
        }

        /** 获取视频播放窗口的尺寸 */
        ViewTreeObserver viewObserver = mHeaderWrapper.getViewTreeObserver();
        viewObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeaderWrapper.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                playerWidth = mHeaderWrapper.getWidth();
                playerHeight = mHeaderWrapper.getHeight();
            }
        });
    }

    /**
     * 正常播放状态 竖屏*
     * 播放器比例为16:9
     */
    private void setMinSize(boolean isAuto) {
//        if (!isAuto){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int playwidth = Screen.getScreenWidth(getWindowManager());
        int playheith = getResources().getDimensionPixelSize(R.dimen.player_height);
        float ratio = (float) 9 / (float) 16;
        playheith = (int) (playwidth * ratio);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(playwidth, playheith);
        mHeaderWrapper.setLayoutParams(param);
        webview.setLayoutParams(param);
        mCurrentScape = PORTRAIT;
        mController.setVisibility(View.GONE);//转换竖屏情况下，绝对隐藏播放器手势操作控件
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    titleLayout.setVisibility(VISIBLE);
                    commentLayout.setVisibility(VISIBLE);
                    mController.setVisibility(VISIBLE);
                    setMinSize(false);

//                    startPlayAnimationFromNet(url,0);
                    break;
                case 1:
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                    bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_ig_play));
                    mController.setVisibility(VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onCompletion() {
        mLastPos = 0;
        if (mCurrentScape == LANDSCAPE) {
            mhandler.sendEmptyMessage(0);
        }
        isBeginning = true;
        mhandler.sendEmptyMessage(1);
        mUIHandler.sendEmptyMessage(UI_EVENT_PLAY_COMPLETE);

    }


    private void stopPlay() {
        if (mVV.isPlaying() == false)
            return;
        mLastPos = mVV.getCurrentPosition();
        mVV.stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webview != null) {
            webview.clearHistory();
            webview.removeAllViewsInLayout();
            webview.clearDisappearingChildren();
            webview.clearFocus();
            webview.clearView();
            webview.setVisibility(View.GONE);
            webview.loadUrl("");
            webview.destroy();
        }
        app.setPlayUrl("");
        app.setPlayPos(0);
        stopPlay();

    }

    @Override
    protected void onPause() {
        if (mVV.isPlaying()) {
            mLastPos = mVV.getCurrentPosition();
            app.setPlayPos(mLastPos);
            app.setPlayUrl(qn_url);
        }
        Log.e("+++mLastPos", "onpause+++" + mLastPos + "");
//        webview.onPause();
        try {
            webview.getClass().getMethod("onPause").invoke(webview, (Object[]) null);
            if (mVV.isPlaying()) {
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                mVV.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pausePlay();
        super.onPause();

    }

    private void pausePlay() {
        if (!mVV.isPlaying())
            return;
        mLastPos = mVV.getCurrentPosition();
        mVV.pause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onError(int i, int i2) {
        return false;
    }


    @Override
    public void onPrepared() {
//        mUIHandler.sendEmptyMessage(UI_EVENT_PLAY_PREPARE);
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
    }

    private int count = 0;
    private long firClick = 0;
    private long secClick = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        touchControlBar();

        if (view.getId() == R.id.controlbar) {
            // 手势里除了singleTapUp，没有其他检测up的方法
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                //只有处理播放进度后才设置播放位置
                if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
                    mVV.seekTo(playingTime);
                }

                GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
                gesture_volume_layout.setVisibility(View.GONE);
                gesture_bright_layout.setVisibility(View.GONE);
                gesture_progress_layout.setVisibility(View.GONE);
            } else if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                count++;
                if (count == 1) {
                    firClick = System.currentTimeMillis();

                } else if (count == 2) {
                    secClick = System.currentTimeMillis();

                    if (secClick - firClick < 1000) { //双击事件
                        if (mVV.isPlaying()) {//暂停播放
                            bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                            mVV.pause();
                            if (!isVisibility) {
                                bPlayLayout.setVisibility(View.VISIBLE);
                                isVisibility = true;
                                //触碰view_holder后延时隐藏视频播放信息半透明布局
                                mUIHandler.sendEmptyMessageDelayed(UI_EVENT_PLAY_LAYOUT_GONE, 5000L);
                            }
                        } else {//继续播放
                            bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                            mVV.resume();
                            if (isVisibility) {
                                bPlayLayout.setVisibility(View.GONE);
                                isVisibility = false;
                            }
                        }
                    } else { //单击事件
                        if (isVisibility) {
                            bPlayLayout.setVisibility(View.GONE);
                            isVisibility = false;
                        } else {
                            bPlayLayout.setVisibility(View.VISIBLE);
                            isVisibility = true;
                            //触碰view_holder后延时隐藏视频播放信息半透明布局
                            mUIHandler.sendEmptyMessageDelayed(UI_EVENT_PLAY_LAYOUT_GONE, 5000L);
                        }
                    }

                    count = 0;
                    firClick = 0;
                    secClick = 0;
                }
            }
            return gestureDetector.onTouchEvent(motionEvent);//如果想要监听到双击、滑动、长按等复杂的手势操作，这个时候就必须得用到OnGestureListener了
        }

        //双击屏幕控制暂停播放或继续播放
        if (view.getId() == R.id.view_holder) {

            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                count++;
                if (count == 1) {
                    firClick = System.currentTimeMillis();

                } else if (count == 2) {
                    secClick = System.currentTimeMillis();
                    if (secClick - firClick < 1000) { //双击事件
                        if (mVV.isPlaying()) {//暂停播放
                            bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
                            mVV.pause();
                            if (!isVisibility) {
                                bPlayLayout.setVisibility(View.VISIBLE);
                                isVisibility = true;
                                //触碰view_holder后延时隐藏视频播放信息半透明布局
                                mUIHandler.sendEmptyMessageDelayed(UI_EVENT_PLAY_LAYOUT_GONE, 5000L);
                            }
                        } else {//继续播放
                            bPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
                            mVV.resume();
                            if (isVisibility) {
                                bPlayLayout.setVisibility(View.GONE);
                                isVisibility = false;
                            }
                        }
                    } else { //单击事件
                        if (isVisibility) {
                            bPlayLayout.setVisibility(View.GONE);
                            isVisibility = false;
                        } else {
                            bPlayLayout.setVisibility(View.VISIBLE);
                            isVisibility = true;
                            //触碰view_holder后延时隐藏视频播放信息半透明布局
                            mUIHandler.sendEmptyMessageDelayed(UI_EVENT_PLAY_LAYOUT_GONE, 5000L);
                        }
                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;
                }
            }

        }
        return true;
    }


    // 当你实现了OnGestureListener监听需要覆写一下方法：
    // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
    @Override
    public boolean onDown(MotionEvent e) {
        firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
        return false;
    }


    // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float mOldX = e1.getX(), mOldY = e1.getY();
        int y = (int) e2.getRawY();
        if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
            // 横向的距离变化大则调整进度，纵向的变化大则调整音量
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                gesture_progress_layout.setVisibility(View.VISIBLE);
                gesture_volume_layout.setVisibility(View.GONE);
                gesture_bright_layout.setVisibility(View.GONE);
                GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
            } else {
                if (mOldX > playerWidth * 3.0 / 5) {// 音量
                    gesture_volume_layout.setVisibility(View.VISIBLE);
                    gesture_bright_layout.setVisibility(View.GONE);
                    gesture_progress_layout.setVisibility(View.GONE);
                    GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
                } else if (mOldX < playerWidth * 2.0 / 5) {// 亮度
                    gesture_bright_layout.setVisibility(View.VISIBLE);
                    gesture_volume_layout.setVisibility(View.GONE);
                    gesture_progress_layout.setVisibility(View.GONE);
                    GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
                }
            }
        }

        // 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
        if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
            playTimes = mVV.getCurrentPosition();
            videoTotalTime = mVV.getDuration();
            // distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
            if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
                if (distanceX >= DensityUtil.dip2px(this, STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
                    gesture_iv_progress.setImageResource(R.drawable.souhu_player_backward);
                    if (playingTime > 3) {// 避免为负
                        playingTime -= 3;// scroll方法执行一次快退3秒
                    } else {
                        playingTime = 0;
                    }
                } else if (distanceX <= -DensityUtil.dip2px(this, STEP_PROGRESS)) {// 快进
                    gesture_iv_progress.setImageResource(R.drawable.souhu_player_forward);
                    if (playingTime < videoTotalTime - 16) {// 避免超过总时长
                        playingTime += 3;// scroll执行一次快进3秒
                    } else {
                        playingTime = videoTotalTime - 10;
                    }
                }
                if (playingTime < 0) {
                    playingTime = 0;
                }
                //处理播放进度时设置播放位置
//                mVV.seekTo(playingTime);
                geture_tv_progress_time.setText(DateUtils.getTimeStr(playingTime) + "/" + DateUtils.getTimeStr(videoTotalTime));
            }
        }

        // 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
        else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME) {
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
            if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
                if (distanceY >= DensityUtil.dip2px(this, STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                    if (currentVolume < maxVolume) {// 为避免调节过快，distanceY应大于一个设定值
                        currentVolume++;
                    }
                    gesture_iv_player_volume.setImageResource(R.drawable.souhu_player_volume);
                } else if (distanceY <= -DensityUtil.dip2px(this, STEP_VOLUME)) {// 音量调小
                    if (currentVolume > 0) {
                        currentVolume--;
                        if (currentVolume == 0) {// 静音，设定静音独有的图片
                            gesture_iv_player_volume.setImageResource(R.drawable.souhu_player_silence);
                        }
                    }
                }
                int percentage = (currentVolume * 100) / maxVolume;
                geture_tv_volume_percentage.setText(percentage + "%");
                audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            }
        }

        // 如果每次触摸屏幕后第一次scroll是调节亮度，那之后的scroll事件都处理亮度调节，直到离开屏幕执行下一次操作
        else if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT) {
            gesture_iv_player_bright.setImageResource(R.drawable.souhu_player_bright);
            if (mBrightness < 0) {
                mBrightness = getWindow().getAttributes().screenBrightness;
                if (mBrightness <= 0.00f)
                    mBrightness = 0.50f;
                if (mBrightness < 0.01f)
                    mBrightness = 0.01f;
            }
            WindowManager.LayoutParams lpa = getWindow().getAttributes();
            lpa.screenBrightness = mBrightness + (mOldY - y) / playerHeight;
            if (lpa.screenBrightness > 1.0f)
                lpa.screenBrightness = 1.0f;
            else if (lpa.screenBrightness < 0.01f)
                lpa.screenBrightness = 0.01f;
            getWindow().setAttributes(lpa);
            geture_tv_bright_percentage.setText((int) (lpa.screenBrightness * 100) + "%");
        }

        firstScroll = false;// 第一次scroll执行完成，修改标志
        return false;
    }

    // 用户（轻触触摸屏后）松开，由1个MotionEvent ACTION_UP触发
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
    @Override
    public void onLongPress(MotionEvent e) {
    }

    // 用户轻触触摸屏，尚未松开或拖动，由1个MotionEvent ACTION_DOWN触发, 注意和onDown()的区别，强调的是没有松开或者拖动的状态
    @Override
    public void onShowPress(MotionEvent e) {
    }

    private Timer mt;

    public void touchControlBar() {
        if (mController.getVisibility() == View.INVISIBLE) {
            mController.setVisibility(VISIBLE);
            mt = new Timer();
            mt.schedule(new TimerTask() {
                @Override
                public void run() {
                    VideoPlayActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mController.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }, 6000);
        } else {
            if (mt != null) {
                mt.cancel();
            }
            mController.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        asyncType = REFRESH;
        if (currentPage == 0) {
            commentPageId = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetCommentAsync(VideoPlayActivity.this, id + "", commentPageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetCommentAsync(VideoPlayActivity.this, id + "", commentPageId + "").execute();
            }
        } else if (currentPage == 1) {
            hisVideoPageId = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetUploadListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetUploadListTask().execute();
            }
        }
    }

    @Override
    public void onLoadMore() {
        asyncType = LOADMORE;
        if (currentPage == 0) {
            commentPageId += 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetCommentAsync(VideoPlayActivity.this, id + "", commentPageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetCommentAsync(VideoPlayActivity.this, id + "", commentPageId + "").execute();
            }
        } else if (currentPage == 1) {
            hisVideoPageId += 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetUploadListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetUploadListTask().execute();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurrentScape == LANDSCAPE) {
//                titleLayout.setVisibility(VISIBLE);
                commentLayout.setVisibility(VISIBLE);
                setMinSize(false);
                return true;
            }
            if (hasFace) {
                gridFace.setVisibility(View.GONE);
                faceBtn.setBackgroundResource(R.drawable.face_nomal);
                hasFace = false;
                return true;
            }
//            if (commentEdt.hasFocus()) {
//                commentEdt.clearFocus();
//                gridFace.setVisibility(View.GONE);
////                shareBtn.setVisibility(VISIBLE);
//                hasFace = false;
//                return true;
//            }
            stopPlay();
            finish();
            return super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取下一个视频
     */
    public class GetNextVideoId extends AsyncTask<Void, Void, String> {
        String id = "";

        public GetNextVideoId(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            id = JsonHelper.getNextVideoId(VideoPlayActivity.this, id);
            if (id != null && !id.equals("")) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(VideoPlayActivity.this, "没有相关视频");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetVedioDetail(id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetVedioDetail(id).execute();
            }
        }
    }

    /**
     * 异步获取 视频详情
     */
    public class GetVedioDetail extends AsyncTask<Void, Void, String> {
        String id = "";

        public GetVedioDetail(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... params) {

            detail = JsonHelper.getVedioDetail(VideoPlayActivity.this, id, ExApplication.MEMBER_ID);
            if (detail != null) {
                return "s";
            }
            return "";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(VideoPlayActivity.this, "获取视频数据失败");
                return;
            }

            //设置视频播放器  七牛播放（百度播放器）或优酷播放（WebView播放器）
            url = detail.getUrl();
            qn_key = detail.getQn_key();
            qn_url = "http://7xiiwo.com2.z0.glb.qiniucdn.com/" + qn_key;
            if (!qn_key.equals("") && qn_key != null) {//当qn_key不为空时，选择启用百度播放器
                mUIHandler.sendEmptyMessage(UI_EVENT_PLAY_PREPARE);
            } else {//当qn_key为空时，选择启用WebView播放器
                webview.setVisibility(View.VISIBLE);
                webview.clearCache(true);
                webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                bPlayerView.setVisibility(View.GONE);
                bPlayLayout.setVisibility(View.GONE);
                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // TODO Auto-generated method stub
//                        view.loadUrl(url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                });
                webview.loadUrl(file_url + "vid=" + url);//通过传入的视频ID即可播放视频
            }
            handler.sendEmptyMessage(0);

            if (detail.getDescription().length() == 0) {
                gameDescription.setText("暂无游戏介绍");
            } else {
                gameDescription.setText(detail.getDescription());
//                gameDescription.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (gameDescription.getLineCount() < 2) {
//                            gameDescriptionMore.setVisibility(View.GONE);
//                        }
//                    }
//                });
            }

            if (detail.getFlower_mark() == 1) {
                loveIv.setBackgroundResource(R.drawable.love_pressed);
            } else if (detail.getFlower_mark() == 0) {
                loveIv.setBackgroundResource(R.drawable.love_normal);
            }
            if (detail.getCollection_mark() == 1) {
                colectIv.setBackgroundResource(R.drawable.collect_pressed);
            } else if (detail.getCollection_mark() == 0) {
                colectIv.setBackgroundResource(R.drawable.collect_normal);
            }

            Uri uri = Uri.parse(detail.getGameDownloadUrl());
            if ("".equals(uri.toString())) {
                installBtn.setText("敬请关注");
                installBtn.setTextColor(Color.parseColor("#ffffff"));
                installBtn.setBackgroundColor(Color.parseColor("#90000000"));
                installBtn.setClickable(false);
            }
//            onClick(playBtn);
            onRefresh();
            if (detail != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetVideoInfoTask(detail.getUrl()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetVideoInfoTask(detail.getUrl()).execute();
                }
            }
        }
    }


    /**
     * 异步获取 评论列表
     */
    private class GetCommentAsync extends AsyncTask<Void, Void, String> {
        String id = "";
        String page = "";
        Context context;

        public GetCommentAsync(Context context, String id, String page) {
            this.id = id;
            this.page = page;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            comment_connList = JsonHelper.getCommentList(context, id, page);
            Log.e("conList", comment_connList + "1");
            if (comment_connList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    comment_rlv.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    comment_list.clear();
                    comment_list.addAll(comment_connList);
                } else {
//                    ToastUtils.showToast(VideoPlayActivity.this, "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (comment_connList.size() == 0) {
                        ToastUtils.showToast(VideoPlayActivity.this, "无更多评论");
                    } else {
                        comment_list.addAll(comment_connList);
                    }
                } else {
//                    ToastUtils.showToast(VideoPlayActivity.this, "连接服务器失败");
                }
            }
            if (comment_list.size() > 0) {
                commentCountTv.setText("(" + comment_list.get(0).getItemsCount() + ")");
                showComment();
            } else {
                commentCountTv.setText("(" + 0 + ")");
                showGameInfo();
            }
            comment_adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(comment_rlv);
            comment_rlv.stopRefresh();
            comment_rlv.stopLoadMore();
        }
    }

    /**
     * 异步获取 个人（上传）视频 列表
     */
    public class GetUploadListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            hisvideo_connList = JsonHelper.getUploadList(hisVideoPageId + "", detail.getMember_id());
            if (hisvideo_connList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    hisvideo_rlv.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    hisvideo_list.clear();
                    hisvideo_list.addAll(hisvideo_connList);
                }
            } else {
                if (s.equals("s")) {
                    if (hisvideo_connList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        hisvideo_list.addAll(hisvideo_connList);
                    }
                } else {
                    ToastUtils.showToast(context, "没有更多数据");
                }
            }
            if (hisvideo_list.size() > 0) {
                showHisVideo();
            } else {
                showGameInfo();
            }
            hisvideo_adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(hisvideo_rlv);
            hisvideo_rlv.stopRefresh();
            hisvideo_rlv.stopLoadMore();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://刷新视频的显示信息

                    url = detail.getUrl();
                    qn_key = detail.getQn_key();
                    if (!qn_key.equals("") && qn_key != null) {//当qn_key不为空时，下载视频按钮可见（可离线下载视频文件）
                        downloadBtn.setVisibility(VISIBLE);
                    }

                    bTitle.setText(detail.getName() + "                    " + detail.getName());
//                    sName.setText("本视频由" + detail.getUserName() + "上传");
//                    sIntroduceTv.setText(detail.getContent());
                    flowerTv.setText(detail.getFlower_count());
                    collectTv.setText(detail.getCollection_count());
                    downloadTv.setText(detail.getDownload_count());
//                    exApplication.imageLoader.displayImage(detail.getAvatar(), sheadImg, exApplication.getOptions());
                    bName.setText(detail.getUserName());
                    bIntroduceTv.setText(detail.getContent());
                    exApplication.imageLoader.displayImage(detail.getAvatar(), bheadImg, exApplication.getOptions());
                    newIntroduceTv.setText(detail.getUserName());//玩家昵称
                    updataTimeTv.setText(detail.getTime());//编辑上传时间
                    if (detail != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new getInfoDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new getInfoDetailTask().execute();
                        }
                    }
                    exApplication.imageLoader.displayImage(detail.getAvatar(), newHeadImg, exApplication.getOptions());
                    break;
            }
        }
    };

    private void showShare() {
        if (detail != null) {
            final String url;
            if (detail.getQn_key().equals("")) {
                url = ExApplication.shareURL + detail.getUrl();
            } else {
                url = ExApplication.shareURL + detail.getQn_key();
            }
            ShareSDK.initSDK(this);
            final OnekeyShare oks = new OnekeyShare();
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_copy);
            // 定义图标的标签
            String label = getResources().getString(R.string.share_copy);
            // 图标点击后会通过Toast提示消息
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(url);
                    ToastUtils.showToast(getApplicationContext(), "视频链接已复制");
//                    oks.finish();
                }
            };
            oks.setCustomerLogo(logo, null, label, listener);
//            oks.show(context);
            //关闭sso授权
            oks.disableSSOWhenAuthorize();

            oks.setShareContentCustomizeCallback(new ShareContentCustomize());

//            // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//            oks.setNotification(R.drawable.tubiao_top, getString(R.string.app_name));
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//            if (platform.getName().equals(Wechat.NAME)) {//微信朋友圈内分享显示的标题
//                oks.setTitle(detail.getName());
//            } else {
            oks.setTitle(getString(R.string.share));
//            }
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url);
//            oks.setTitleUrl(youkuDetail.getLink());
            // text是分享文本，所有平台都需要这个字段
//            oks.setText("快来看看" + detail.getName() + youkuDetail.getLink());
            oks.setText("快来看看 " + detail.getName());
            /** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
            oks.setImageUrl(detail.getFlagPath());
            // url仅在微信（包括好友和朋友圈）中使用
//            oks.setUrl(youkuDetail.getLink());
            oks.setUrl(url);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url);
//            oks.setSiteUrl(youkuDetail.getLink());
            // 启动分享GUI
            oks.show(this);
        }
    }

    /**
     * 快捷分享项目现在添加为不同的平台添加不同分享内容的方法。*
     */
    public class ShareContentCustomize implements ShareContentCustomizeCallback {
        @Override
        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
            final String url;
            if (detail.getQn_key().equals("")) {
                url = ExApplication.shareURL + detail.getUrl();
            } else {
                url = ExApplication.shareURL + detail.getQn_key();
            }
            if (WechatMoments.NAME.equals(platform.getName())) {
                String title = detail.getName();
                paramsToShare.setTitle(title);
            } else if (SinaWeibo.NAME.equals(platform.getName())) {
                String text = "快来看看 " + detail.getName() + url;
                paramsToShare.setText(text);
            }
        }
    }

    /**
     * 点赞 或 取消点赞
     */
    private class FlowerTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            if (detail.getFlower_mark() == 1) {//已点赞
                return JsonHelper.cancelFlower(id, ExApplication.MEMBER_ID);//取消点赞
            } else if (detail.getFlower_mark() == 0) {//未点赞
                return JsonHelper.giveFlower(id, ExApplication.MEMBER_ID);//进行点赞
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);

            if (b.equals("c") && detail.getFlower_mark() == 1) {//已点赞，重复点赞
                ToastUtils.showToast(VideoPlayActivity.this, "你已经赞过了该视频");
                return;
            }
            if (detail.getFlower_mark() == 0) {//未点赞
                if (b.equals("s")) {//成功点赞
                    detail.setFlower_mark(1);
                    if (ExApplication.MEMBER_ID.equals("")) {
                        SharePreferenceUtil.setPreference(context, id + "unlogin_flower", "flowered");
                    }
                    ExApplication.upUmenEventValue(context, "点赞次数", "praise_count");
                    flowerTv.setText(Integer.parseInt(flowerTv.getText().toString().trim().equals("") ? "0" : flowerTv.getText().toString().trim()) + 1 + "");
                    loveIv.setBackgroundResource(R.drawable.love_pressed);
//                    ToastUtils.showToast(VideoPlayActivity.this, "视频点赞成功");
                    //每日任务——给1部视频点赞
                    if (!"已完成".equals(SharePreferenceUtil.getPreference(context, "21taskflag"))) {
                        utils = new CompleteTaskUtils(context, "21");
                        utils.completeMission();
                    }
                } else {
//                    ToastUtils.showToast(VideoPlayActivity.this, "点赞视频失败");
                }
            } else if (detail.getFlower_mark() == 1) {//已点赞
                if (b.equals("s")) {//取消点赞成功
                    detail.setFlower_mark(0);
                    if (ExApplication.MEMBER_ID.equals("")) {
                        SharePreferenceUtil.setPreference(context, id + "unlogin_flower", "unflowered");
                    }
                    ExApplication.upUmenEventValue(context, "取消点赞次数", "unpraise_count");
                    flowerTv.setText(Integer.parseInt(flowerTv.getText().toString().trim().equals("") ? "0" : flowerTv.getText().toString().trim()) - 1 + "");
                    loveIv.setBackgroundResource(R.drawable.love_normal);
//                    ToastUtils.showToast(VideoPlayActivity.this, "取消视频点赞成功");
                } else {
//                    ToastUtils.showToast(VideoPlayActivity.this, "取消点赞视频失败");
                }
            }
        }
    }

    /**
     * 收藏 或 取消收藏
     */
    private class CollectTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            if (detail.getCollection_mark() == 1) {//已收藏
                return JsonHelper.getCancelCollectVideo(id, ExApplication.MEMBER_ID);//取消收藏
            } else if (detail.getCollection_mark() == 0) {//未收藏
                return JsonHelper.getCollectVideo(id, ExApplication.MEMBER_ID);//进行收藏
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);
            if (b.equals("c") && detail.getCollection_mark() == 1) {//已收藏，重复收藏
                ToastUtils.showToast(VideoPlayActivity.this, "你已经收藏过了该视频");
                return;
            }
            if (detail.getCollection_mark() == 0) {//未收藏
                if (b.equals("s")) {//成功收藏
                    detail.setCollection_mark(1);
                    DialogUtils.createAlterDialog(VideoPlayActivity.this, "收藏成功");
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            DialogUtils.cancelAlterDialog();
                        }
                    };
                    timer.schedule(timerTask, 1500L);
                    collectTv.setText(Integer.parseInt(collectTv.getText().toString().trim().equals("") ? "0" : collectTv.getText().toString().trim()) + 1 + "");
                    colectIv.setBackgroundResource(R.drawable.collect_pressed);
//                    ToastUtils.showToast(VideoPlayActivity.this, "视频收藏成功");
                    //每日任务——收藏1个视频
                    if (!"已完成".equals(SharePreferenceUtil.getPreference(context, "20taskflag"))) {
                        utils = new CompleteTaskUtils(context, "20");
                        utils.completeMission();
                    }
                    ExApplication.upUmenEventValue(context, "收藏次数", "collection_count");
                } else {
//                    ToastUtils.showToast(VideoPlayActivity.this, "视频收藏失败");
                }
            } else if (detail.getCollection_mark() == 1) {//已收藏
                if (b.equals("s")) {//取消收藏
                    detail.setCollection_mark(0);
                    DialogUtils.createAlterDialog(VideoPlayActivity.this, "取消收藏成功");
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            DialogUtils.cancelAlterDialog();
                        }
                    };
                    timer.schedule(timerTask, 1500L);
                    collectTv.setText(Integer.parseInt(collectTv.getText().toString().trim().equals("") ? "0" : collectTv.getText().toString().trim()) - 1 + "");
                    colectIv.setBackgroundResource(R.drawable.collect_normal);
//                    ToastUtils.showToast(VideoPlayActivity.this, "取消视频收藏成功");
                    ExApplication.upUmenEventValue(context, "取消收藏次数", "uncollection_count");
                } else {
//                    ToastUtils.showToast(VideoPlayActivity.this, "取消视频收藏失败");
                }
            }
        }
    }


    /**
     * 游戏下载量统计
     */
    private class DownloadCountTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return JsonHelper.submitDownloadCount(detail.getGame_id());
        }
    }

    /**
     * 下载视频
     */
    private class DownloadAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.addDownLoadCount(id);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                downloadTv.setText(Integer.parseInt(downloadTv.getText().toString().trim().equals("") ? "0" : downloadTv.getText().toString().trim()) + 1 + "");
            } else {
                ToastUtils.showToast(VideoPlayActivity.this, "网络错误");
            }
        }
    }

    /**
     * 异步获取 个人信息
     * 主要获取是否已经关注的Mark
     */
    private static UserEntity user = new UserEntity();
    private int lastMark = 0;

    private class getInfoDetailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            user = JsonHelper.getUserDetailInfo(context, detail.getMember_id(), ExApplication.MEMBER_ID, "");
//            Log.e("name",user.getTitle());
            if (user != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                lastMark = user.getMark();
                if (lastMark == 1) {
                    focusTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    focusTv.setText("已关注");
                    lastMark = 1;
                } else {
                    focusTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    focusTv.setText("+关注");
                    lastMark = 0;
                }
            }
        }

    }

    /**
     * 关注或取消关注 玩家
     */
    private class submitFocusTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            boolean b = JsonHelper.submitFocus(ExApplication.MEMBER_ID, detail.getMember_id());
            if (b) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("s")) {
                if (lastMark == 0) {//未关注时，执行关注操作
                    focusTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    focusTv.setText("已关注");
                    lastMark = 1;
                    ToastUtils.showToast(context, "关注成功");
                } else {//已关注时，执行取消关注操作
                    focusTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    focusTv.setText("+关注");
                    lastMark = 0;
                    ToastUtils.showToast(context, "取消关注");
                }
            }

        }
    }

    /**
     * 接口方法，响应CommentAdapter中的回复按钮点击事件
     *
     * @param markFlag true为二级评论（非一级评级），false为一级评论
     */
    @Override
    public void replyClick(int postion, boolean markFlag) {
        replyToString = "//@" + comment_list.get(postion).getName() + ":" + comment_list.get(postion).getContent();
        replyToLength = replyToString.length();
        commentEdt.setText(replyToString);
        /**
         * 两部分：
         * 第一步通过requestFocus()方法取得焦点
         * 第二步是强制性的弹出键盘，由于焦点在editText上，所以输入正常
         **/
        commentEdt.requestFocus();
        InputMethodManager manager = (InputMethodManager) commentEdt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        if (markFlag) {
            replyMark = "1";
            replyId = comment_list.get(postion).getComment_id();
        } else {
            replyMark = "0";
            replyId = "";
        }
    }

    // 0或1，0为一级评级，1为非一级评级
    private String replyMark = "";
    private String replyId = "";

    /**
     * 提交评论 异步方法
     */
    private class SubmitTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.submitComment(id, ExApplication.MEMBER_ID, commentEdt.getText().toString().trim(), replyMark, replyId);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                ToastUtils.showToast(VideoPlayActivity.this, "评论成功");
                ExApplication.upUmenEventValue(context, "评论次数", "comment_count");
                commentEdt.setText("");
                onRefresh();
                showComment();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentEdt.getWindowToken(), 0);

                if ("".equals(SharePreferenceUtil.getPreference(context, "12task_flag"))) {
                    utils = new CompleteTaskUtils(context, "12");
                    utils.completeMission();
                    SharePreferenceUtil.setPreference(context, "12task_flag", "true");
                }
                if (!"已完成".equals(SharePreferenceUtil.getPreference(context, "18taskflag"))) {
                    utils = new CompleteTaskUtils(context, "18");
                    utils.completeMission();
                }

            } else {
                ToastUtils.showToast(VideoPlayActivity.this, "评论失败,请重试");
            }
        }
    }

    private VedioDetail youkuDetail;

    /**
     * 获取视频信息
     */
    private class GetVideoInfoTask extends AsyncTask<Void, Void, String> {
        String id = "";

        public GetVideoInfoTask(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            youkuDetail = JsonHelper.getYouKuDetail(id);
            if (youkuDetail != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            if (detail != null) {
//                VideoEntity video = new VideoEntity();
//                video.setId(recId);
//                String aa = detail.getTime_length();
//                video.setTime(aa);
//                video.setSimg_url(detail.getFlagPath());
//                video.setFlower(detail.getFlower_count());
//                video.setComment(detail.getComment_count());
//                video.setViewCount(detail.getView_count());
//                video.setTitle_content(detail.getName());
//                //添加观看记录
//                dbManager.addRecordVideo(video);
//            }
            DialogUtils.cancelLoadingDialog();
        }
    }

    public static boolean isCanSumbit(String lastSumbitTime, String currentTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = simpleDateFormat.parse(lastSumbitTime);
            Date date2 = simpleDateFormat.parse(currentTime);
            if ((date2.getTime() - date1.getTime()) > 60 * 1000) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 每次重新计算listview高度，
     * <p/>
     * 只要在设置ListView的Adapter后调用此静态方法即可让ListView正确的显示在其父ListView的ListItem中。
     * 但是要注意的是，子ListView的每个Item必须是LinearLayout，不能是其他的，
     * 因为其他的Layout(如RelativeLayout)没有重写onMeasure()，所以会在onMeasure()时抛出异常。
     * <p/>
     * 这个代码里面有一个问题，就是这个当你的ListView里面有多行的TextView的话，ListView的高度就会计算错误，它只算到了一行TextView的高度。
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(RefreshListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
                System.out.println("listItem.getMeasuredHeight()==" + i + "===" + listItem.getMeasuredHeight());
            }
        }

        System.out.println("listAdapter.getCount()===" + listAdapter.getCount());
        System.out.println("totalHeight===" + totalHeight);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
