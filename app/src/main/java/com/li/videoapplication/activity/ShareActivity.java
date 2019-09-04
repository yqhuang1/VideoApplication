package com.li.videoapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fmscreenrecord.VideoList.VideoThumbnailLoader;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.utils.HttpUtils;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.Adapter.LocalVideoAdapter;
import com.li.videoapplication.Adapter.MyMatchesAdapter;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.Service.UploadVideoService;
import com.li.videoapplication.Service.UploadVideoService.MsgBinder;
import com.li.videoapplication.View.MorePopWindow;
import com.li.videoapplication.View.MyListView;
import com.li.videoapplication.callback.UpVideoCallBack;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.MD5;
import com.li.videoapplication.utils.ResumableUpload;
import com.li.videoapplication.utils.VideoTimeThumbnailLoader;
import com.qiniu.android.common.Config;
import com.qiniu.android.utils.AsyncRun;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * 视频管理 本地视频 （上传）分享页面
 */
public class ShareActivity extends Activity implements OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout back;
    private static Context mContext;

    // 文件大小
    private TextView shareFileSize;
    // 视频图片
    private ImageView shareFileImage;

    private Intent intent;
    // 视频文件的ID，名称，路径,视频所在列表ID
    String fileID, fileName, filePath, listID;
    public static TextView game_name;
    private static EditText shareEdittext;
    private Button editorVideo;

    static String sharchannel = "WechatMoments";

    // 视频编辑框
    private static LinearLayout editorFrame;
    // 视频上传进度框
    private static LinearLayout upvideoLinear;
    // 暂停上传按钮
    private static ImageView stopUpVideo;
    // 继续上传按钮
    private static LinearLayout goonUpVideo;
    // 上传进度条
    private static LinearLayout progressbarLinear;
    // 上传进度百分比
    private static TextView progresssPercent;
    // 上传进度文本
    private static TextView progresssText;
    // 视频编辑框父窗体
    private static FrameLayout editoVideoFrameParent;
    // 上传进度条
    private static ProgressBar progressbar;

    // 当前视频是否处于上传状态
    private static boolean isuploading = false;
    // 是否在此页面onPause时不finish;
    public static boolean isgotoselectgame = false;
    // 对分享按钮进行约束，防止多次点击造成重复分享调用
    View shareView;
    // 上传视频服务
    Intent serviceIntent;
    /**
     * 服务绑定用途 0：开始上传；1：继续上传 2：暂停上传
     */
    int bindforwhat = 0;
    // 视频时长
    long videolength;
    /**
     * 分享内容
     */
    private String text = "这是刚才我用《手游视界》在手机上录制的一段精彩视频，大家快来欣赏吧";

    // 三角箭头
    ImageView triangleArrow;

    //视频描述
    private EditText describeEdittext;
    //参与活动
    private LinearLayout matchLayout;
    private MyListView matchListview;
    private List<MatchEntity> myConnecList;//我的活动列表
    private List<MatchEntity> myMatchList;//我的可上传视频的活动列表
    private MyMatchesAdapter myMatchAdapter;
    //视频分享、暂不分享
    private TextView noShareTextview;
    private TextView doShareTextview;

    //popwindow起点位置
    private View popWindowStart;

    //sharePopWindow布局
    PopupWindow sharePopWindow;
    private View conentView;
    private LinearLayout popup;
    // popwindow分享频道
    private LinearLayout shareSYSJ;
    private LinearLayout shareQQ, shareQQZone, shareSinaWeibo,
            shareWechatmoments, shareWechat;
    private TextView shareCancelTv;


    LinearLayout spinner;
    // 旧的分享链接 http://www.17sysj.com/index.php/Wap/Video/shareWap/play/
    private String qiuniuUrl = com.li.videoapplication.activity.ExApplication.shareURL;

    private static PlatformActionListener platformActionListener;

    // 用户ID，（活动ID）,视频名称视频描述
    private String memberID = null, videoTitle = null, videoDescribe = null;
    // 视频的游戏类型
    public static String gameName = null;
    public static String matchID = null, match_name = null;
    // 数据库
    VideoDB videoDB = null;
    // 视频上传状态
    VideoInfo vi;
    SharedPreferences sharedPreferences;

    public static Handler handler;

    public PlatformActionListener getPlatformActionListener() {
        return platformActionListener;
    }

    public void setPlatformActionListener(
            PlatformActionListener platformActionListener) {
        this.platformActionListener = platformActionListener;
    }

    UploadVideoService.MsgBinder binder;

    private ServiceConnection conn = new ServiceConnection() {

        /**
         * 当该activity与Server断开连接时回调该方法
         * **/
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        /**
         * 当该activity与Server成功连接时回调该方法
         * */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MsgBinder) service;

            if (bindforwhat == 0) {

                binder.startUpLoadVideo(mContext, memberID, matchID, videoTitle,
                        gameName, filePath, myCallBack);

            } else if (bindforwhat == 1) {
                // "绑定后继续上传");
                binder.goonUploadVideo(mContext);
            } else if (bindforwhat == 2) {
                // "绑定后暂停上传");
                binder.stopUpLoadVideo();
            } else if (bindforwhat == 4) {
                // "重新赋值后继续上传");
                binder.setvalue(memberID, matchID, videoTitle, gameName, filePath,
                        myCallBack);
                binder.goonUploadVideo(mContext);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fm_share_activity);
        mContext = this;
        videoDB = new VideoDB(mContext);
        // 设置进入退出动画
        overridePendingTransition(MResource.getIdByName(getApplication(),
                "anim", "push_bottom_in"), MResource.getIdByName(
                getApplication(), "anim", "push_bottom_out"));
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        // 获取intent
        intent = getIntent();
        // 获取视频文件ID,路径，名称
        filePath = intent.getStringExtra("videosrc");
        memberID = intent.getStringExtra("memberID");
        videoTitle = intent.getStringExtra("videoTitle");

        // 截取视频文件后缀名前的字符串
        final String[] fileName = videoTitle.split("\\.mp4");
        videoTitle = fileName[0];
        vi = videoDB.getupdateContect(filePath);

        // 查找页面控件
        findViews();

        // 注册监听
        setOnClick();

        DialogUtils.createLoadingDialog(mContext, "");
        // 初始化数据
        indata();

        DialogUtils.cancelLoadingDialog();
        //初始化活动数据
        initMatchData();

        //初始化分享SharePopWindow
        initSharePopwindow();

        // TODO
        handler = new Handler() {
            @SuppressLint("NewApi")
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        // 弹出泡泡窗口
                        MorePopWindow morePopWindow = new MorePopWindow(
                                (Activity) mContext);
                        morePopWindow.showPopupWindow(spinner);
                        break;
                    case 2:// 显示向右箭头
                        triangleArrow.setBackground(mContext.getResources()
                                .getDrawable(
                                        MResource.getIdByName(mContext, "drawable",
                                                "triangle_right")));
                        break;
                    case 3:// 显示向下箭头
                        triangleArrow.setBackground(mContext.getResources()
                                .getDrawable(
                                        MResource.getIdByName(mContext, "drawable",
                                                "triangle_down")));
                        break;
                    case 4:
                        // 解绑并停止服务
                        try {
                            unbindService(conn);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // "绑定停止异常");
                        } finally {
                            try {
                                stopService(serviceIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // "服务停止异常");
                            }

                        }

                        bindforwhat = 4;
                        serviceIntent = new Intent(mContext,
                                UploadVideoService.class);
                        startService(serviceIntent);
                        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
                        break;
                    case 5: // 刷新本地视频列表
                        VideoManagerActivity.handlerViewChange.sendEmptyMessage(9);
//                        LocalVideoFragment.localVideoHandle.sendEmptyMessage(2);
                        break;
                }
            }
        };
    }

    private void indata() {
        // 视频长度
        Long filelong = LocalVideoAdapter.getFileSizes(filePath);
        // 显示视频大小
        String filesize = LocalVideoAdapter.FormetFileSize(filelong);
        shareFileSize.setText("视频大小:" + filesize);
        // 设置视频标题
        if (isValidDate(videoTitle)) {
            shareEdittext.setText(null);
        } else {
            shareEdittext.setText(videoTitle);
            shareEdittext.setSelection(videoTitle.length());
        }

        // 获取本地视频图片
        new VideoThumbnailLoader(mContext).DisplayThumbnailForLocalVideo(
                filePath, shareFileImage);
        // 获取视频时长
        VideoTimeThumbnailLoader videoTT = new VideoTimeThumbnailLoader(
                mContext);
        String videolengthtmp = videoTT.GetThumbnailForLocalVideo(filePath);
        videolength = Long.valueOf(videolengthtmp);

        String videotitle = vi.getUpvideotitle();
        String gamename = vi.getGamename();
        String videodescribe = vi.getVideodescribe();
        // 重新赋值上传视频和上传标题，视频描述
        videoTitle = videotitle;
        gameName = gamename;
        videoDescribe = videodescribe;
        // 设置视频标题和游戏标题，视频描述
        shareEdittext.setText(videotitle);
        game_name.setText(gamename);
        describeEdittext.setText(videodescribe);

        // 如果该视频处于上传暂停状态
        if (vi.getVideoStation().equals("pauseupvideo")) {

            upvideoLinear.setVisibility(View.VISIBLE);
            progressbarLinear.setVisibility(View.GONE);
            goonUpVideo.setVisibility(View.VISIBLE);
            // 隐藏视频编辑框
            editorFrame.setVisibility(View.GONE);

//            String videotitle = vi.getUpvideotitle();
//            String gamename = vi.getGamename();
//            String videodescribe = vi.getVideodescribe();
            double precent = vi.getPrecent();

//            // 重新赋值上传视频和上传标题，视频描述
//            videoTitle = videotitle;
//            gameName = gamename;
//            videoDescribe = videodescribe;
//            // 设置视频标题和游戏标题，视频描述
//            shareEdittext.setText(videotitle);
//            game_name.setText(gamename);
//            describeEdittext.setText(videodescribe);
            // 设置上传进度
            int mprecent = (int) (precent * 100);
            progresssPercent.setText((mprecent) + "%");
            // 已上传文件大小
            String upFileSize = LocalVideoAdapter
                    .FormetFileSize((long) (filelong * precent));
            progresssText.setText(upFileSize + "/" + filesize);
            // 设置控件不可点击
            shareEdittext.setFocusable(false);
            shareEdittext.setFocusableInTouchMode(false);
            spinner.setEnabled(false);
            editoVideoFrameParent.setBackgroundColor(0xfff1f1f1);

        } else if (vi.getVideoStation().equals("uploading")) {// 如果处于上传中状态

            upvideoLinear.setVisibility(View.VISIBLE);
            progressbarLinear.setVisibility(View.VISIBLE);
            goonUpVideo.setVisibility(View.GONE);
            // 隐藏视频编辑框
            editorFrame.setVisibility(View.GONE);

            isuploading = true;

//            String videotitle = vi.getUpvideotitle();
//            String gamename = vi.getGamename();
//            String videodescribe = vi.getVideodescribe();

//            // 重新赋值上传视频和上传标题，视频描述
//            videoTitle = videotitle;
//            gameName = gamename;
//            videoDescribe = videodescribe;
//            // 设置视频标题和游戏标题，视频描述
//            shareEdittext.setText(videotitle);
//            game_name.setText(gamename);
//            describeEdittext.setText(videodescribe);
            // 设置上传进度
            progresssPercent.setText("0%");
            // 设置控件不可点击
            shareEdittext.setFocusable(false);
            shareEdittext.setFocusableInTouchMode(false);
            spinner.setEnabled(false);
            sharchannel = "SYSJ";
            editoVideoFrameParent.setBackgroundColor(0xfff1f1f1);

            // 如果resumableUpload类丢失，重启上传服务(针对上传过程中应用被强行清退)
            if (UploadVideoService.resumableUpload == null) {
                // "服务被取消，重启上传服务");
                bindforwhat = 0;
                serviceIntent = new Intent(this, UploadVideoService.class);
                startService(serviceIntent);
                bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
            }
        }

    }

    private void findViews() {
        back = (LinearLayout) findViewById(MResource.getIdByName(mContext,
                "id", "fm_share_linerarlayout_back"));

        shareFileSize = (TextView) findViewById(MResource.getIdByName(mContext,
                "id", "fm_sharefilesize"));
        shareEdittext = (EditText) findViewById(MResource.getIdByName(mContext,
                "id", "fm_share_edittext"));
        shareFileImage = (ImageView) findViewById(MResource.getIdByName(
                mContext, "id", "fm_sharefileimage"));
        editorVideo = (Button) findViewById(MResource.getIdByName(mContext,
                "id", "fm_goto_editovideo"));


        game_name = (TextView) findViewById(MResource.getIdByName(mContext,
                "id", "fm_select_game_name"));
        editorFrame = (LinearLayout) findViewById(MResource.getIdByName(
                mContext, "id", "fm_upvideo_edito_frame"));
        upvideoLinear = (LinearLayout) findViewById(MResource.getIdByName(
                mContext, "id", "fm_upvideo_progress_linear"));
        stopUpVideo = (ImageView) findViewById(MResource.getIdByName(mContext,
                "id", "fm_stopupvideo_image"));
        goonUpVideo = (LinearLayout) findViewById(MResource.getIdByName(
                mContext, "id", "fm_goon_upvideo_linear"));
        progressbarLinear = (LinearLayout) findViewById(MResource.getIdByName(
                mContext, "id", "fm_upvidoe_progressbar_linear"));
        editoVideoFrameParent = (FrameLayout) findViewById(MResource
                .getIdByName(mContext, "id", "fm_upvideo_edito_frame_parent"));
        progressbar = (ProgressBar) findViewById(MResource.getIdByName(
                mContext, "id", "fm_upvidoe_progressbar"));
        progresssPercent = (TextView) findViewById(MResource.getIdByName(
                mContext, "id", "fm_upvideo_progress_text"));
        progresssText = (TextView) findViewById(MResource.getIdByName(mContext,
                "id", "fm_upvideo_finish_text"));
        spinner = (LinearLayout) findViewById(MResource.getIdByName(mContext,
                "id", "fm_goto_select_game_spinner"));
        triangleArrow = (ImageView) findViewById(MResource.getIdByName(
                mContext, "id", "fm_triangle_arrow"));

        describeEdittext = (EditText) findViewById(R.id.fm_share_describe_edittext);
        matchLayout = (LinearLayout) findViewById(R.id.fm_share_participate_layout);
        matchListview = (MyListView) findViewById(R.id.fm_share_participate_listview);
        myConnecList = new ArrayList<MatchEntity>();
        myMatchList = new ArrayList<MatchEntity>();
        myMatchAdapter = new MyMatchesAdapter(mContext, myMatchList);
        matchListview.setAdapter(myMatchAdapter);

        doShareTextview = (TextView) findViewById(R.id.fm_share_doshare_textview);
        noShareTextview = (TextView) findViewById(R.id.fm_share_noshare_textview);
        popWindowStart = (View) findViewById(R.id.fm_share_popwindowstart_view);

    }

    private void initMatchData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new MyMatchAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new MyMatchAsync().execute();
        }
    }


    /**
     * 异步获取 我的活动 列表
     */
    public class MyMatchAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            myMatchList.clear();
            myConnecList = JsonHelper.getMyMatchList();
            for (MatchEntity matchEntity : myConnecList) {
                if (matchEntity.getStatus() == 1 && matchEntity.getMark() == 0) {//无过期且待上传视频
                    matchEntity.setCleckedFlag(false);
                    myMatchList.add(matchEntity);
                }
            }
            if (myMatchList != null && myMatchList.size() > 0) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                matchLayout.setVisibility(View.VISIBLE);
                myMatchAdapter.notifyDataSetChanged();
            } else {
//                ToastUtils.showToast(context, "没有参加活动的数据");
            }

        }
    }


    private void setOnClick() {
        back.setOnClickListener(this);
        editorVideo.setOnClickListener(this);

        stopUpVideo.setOnClickListener(this);
        goonUpVideo.setOnClickListener(this);
        shareEdittext.setOnClickListener(this);
        spinner.setOnClickListener(this);

        matchListview.setOnItemClickListener(this);

        doShareTextview.setOnClickListener(this);
        noShareTextview.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        if (v == back || v == noShareTextview) {

            if (isuploading) {// 如果处于上传状态，将上传状态记录进数据库
                String videorul = videoDB.getVideoToken(filePath).getVideoURL();
                videoDB.updateContect(filePath, "uploading", gameName,
                        videoTitle, 0, videorul);
                // "按到了返回键");
            } else {
                gameName = game_name.getText().toString();
                videoTitle = shareEdittext.getText().toString();
                videoDescribe = describeEdittext.getText().toString();
                videoDB.updateEditContect(filePath, gameName, videoTitle, videoDescribe);
            }
            finish();
//		} else if (v == editorVideo) {// 编辑视频
//			if (videolength > 30000) {
//				Intent intent = new Intent();
//
//				intent.setClass(mContext, VideoEditorActivity.class);
//
//				intent.putExtra("filePath", filePath);
//				intent.putExtra("memberID", memberID);
//				intent.putExtra("videoTitle", videoTitle);
//				startActivity(intent);
//			} else {
//				MinUtil.showToast(mContext, "30秒以上的时长才可以编辑");
//			}

        } else if (v == doShareTextview) {

            //弹出分享sharePopwindow
            showSharePopwindow();


        } else if (v == goonUpVideo) {// 继续上传
            // 获取当前网络环境
            int netType = MinUtil.getNetworkType(mContext);
            if (netType == 0) {
                MinUtil.showToast(mContext, "当前网络不可用，请检查后再上传.");
            } else if (netType == 1) {// wifi
                // 显示进度条
                progressbarLinear.setVisibility(View.VISIBLE);
                // 隐藏继续上传按钮
                goonUpVideo.setVisibility(View.GONE);
                // 将分析渠道设置为SYSJ，表示上传后不进行分享
                sharchannel = "SYSJ";
                isuploading = true;
                if (binder == null) {
                    // 检测暂停上传之前是否已经退出过应用
                    VideoDB videodb = new VideoDB(mContext);
                    String token = videodb.getVideoToken(filePath).getToken();
                    if (PreferenceManager.getDefaultSharedPreferences(mContext)
                            .getBoolean(token + "exit", false)) {
                        // 退出过的应用就重新上传
                        bindforwhat = 0;
                    } else {
                        bindforwhat = 1;
                    }

                    serviceIntent = new Intent(this, UploadVideoService.class);
                    startService(serviceIntent);
                    bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);

                } else {
                    binder.goonUploadVideo(mContext);
                }

                // 设置背景色
                editoVideoFrameParent.setBackgroundColor(0xfff1f1f1);
            } else {
                upVideodialog();
            }

        } else if (v == stopUpVideo) {// 暂停上传
            goonUpVideo.setVisibility(View.VISIBLE);
            progressbarLinear.setVisibility(View.GONE);
            if (binder == null) {
                bindforwhat = 2;
                serviceIntent = new Intent(this, UploadVideoService.class);
                startService(serviceIntent);
                bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);

            } else {
                binder.stopUpLoadVideo();
            }
            isuploading = false;
            // 设置背景色
            editoVideoFrameParent.setBackgroundColor(0xfff1f1f1);
            spinner.setEnabled(false);
        } else if (v == spinner) {
            // 如果软键盘有打开，则回收
            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(((Activity) mContext).getWindow()
                        .getDecorView().getWindowToken(), 0);
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(100);
                            handler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }

                    }

                    ;
                }.start();

            } else {
                MorePopWindow morePopWindow = new MorePopWindow(this);
                morePopWindow.showPopupWindow(spinner);
            }

        } else if (v == shareQQ) {

            shareView = v;
            shareView.setClickable(false);
            if (videoFileEstimate("QQ")) {

                sharchannel = "QQ";
                MinUtil.showToast(mContext, "准备进行QQ分享..");

                MinUtil.upUmenEventValue(mContext, "本地分享到QQ次数",
                        "shareUrlToFriends");

            }
        } else if (v == shareQQZone) {// 分享到QQ空间

            shareView = v;
            shareView.setClickable(false);
            if (videoFileEstimate("QZone")) {

                sharchannel = "QZone";
                MinUtil.showToast(mContext, "准备进行QQ空间分享..");

                MinUtil.upUmenEventValue(mContext, "本地分享到QQ空间次数",
                        "shareUrlToFriends");

            }

        } else if (v == shareSinaWeibo) {

            shareView = v;
            shareView.setClickable(false);
            if (videoFileEstimate("SinaWeibo")) {// 分享到新浪微博

                sharchannel = "SinaWeibo";
                MinUtil.showToast(mContext, "准备进行新浪微博分享..");

                MinUtil.upUmenEventValue(mContext, "本地分享到新浪微博次数",
                        "shareUrlToFriends");

            }
        } else if (v == shareWechatmoments) {

            shareView = v;
            shareView.setClickable(false);
            // 朋友圈只支持分享标题，不支持分享内容
            if (videoFileEstimate("WechatMoments")) {

                sharchannel = "WechatMoments";
                MinUtil.showToast(mContext, "准备进行朋友圈分享..");

                MinUtil.upUmenEventValue(mContext, "本地分享到朋友圈次数",
                        "shareUrlToFriends");

            }

        } else if (v == shareWechat) {

            shareView = v;
            shareView.setClickable(false);
            if (videoFileEstimate("Wechat")) {

                sharchannel = "Wechat";
                MinUtil.showToast(mContext, "准备进行微信分享..");

                MinUtil.upUmenEventValue(mContext, "本地分享到微信次数",
                        "shareUrlToFriends");

            }

        } else if (v == shareSYSJ) {

            if (videoFileEstimate("SYSJ")) {
                sharchannel = "SYSJ";

                MinUtil.upUmenEventValue(mContext, "本地分享到手游视界次数",
                        "shareUrlToFriends");
            }
        } else if (v == shareCancelTv) {
            hiddenSharePopwindow();
        }
    }

    /**
     * 初始化sharePopupWindow
     */
    private void initSharePopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(
                MResource.getIdByName(mContext, "layout", "share_popup_dialog"), null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        sharePopWindow = new PopupWindow(conentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        sharePopWindow.setFocusable(true);


        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        sharePopWindow.setBackgroundDrawable(dw);


        // 设置popWindow的显示和消失动画
        sharePopWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        popup = (LinearLayout) conentView.findViewById(MResource.getIdByName(
                mContext, "id", "share_popup_layout"));

        shareSYSJ = (LinearLayout) conentView.findViewById(MResource.getIdByName(mContext,
                "id", "fm_share_sysj"));
        shareQQ = (LinearLayout) conentView.findViewById(MResource.getIdByName(mContext,
                "id", "fm_share_qq"));
        shareQQZone = (LinearLayout) conentView.findViewById(MResource.getIdByName(
                mContext, "id", "fm_share_qqzone"));
        shareSinaWeibo = (LinearLayout) conentView.findViewById(MResource.getIdByName(
                mContext, "id", "fm_share_SinaWeibo"));
        shareWechatmoments = (LinearLayout) conentView.findViewById(MResource.getIdByName(
                mContext, "id", "fm_share_wechatmoments"));
        shareWechat = (LinearLayout) conentView.findViewById(MResource.getIdByName(
                mContext, "id", "fm_share_wechat"));
        shareCancelTv = (TextView) conentView.findViewById(MResource.getIdByName(
                mContext, "id", "share_popup_cancel"));

        popup.setOnClickListener(this);
        shareSYSJ.setOnClickListener(this);
        shareQQ.setOnClickListener(this);
        shareQQZone.setOnClickListener(this);
        shareSinaWeibo.setOnClickListener(this);
        shareWechatmoments.setOnClickListener(this);
        shareWechat.setOnClickListener(this);
        shareCancelTv.setOnClickListener(this);

    }

    /**
     * 显示sharePopupWindow
     */
    private void showSharePopwindow() {
        // 在底部显示
        sharePopWindow.showAtLocation(popWindowStart, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 隐藏sharePopupWindow
     */
    private void hiddenSharePopwindow() {
        // 在底部显示
        sharePopWindow.dismiss();
    }


    /**
     * 上传视频并进行分享
     */
    private void upvideoAndShare() {
        // 设置控件不可点击
        spinner.setEnabled(false);
        serviceIntent = new Intent(this, UploadVideoService.class);
        startService(serviceIntent);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);

    }

    /**
     * 判断当前视频是否已经上传到云端
     *
     * @return
     */
    private int isVideoUpSuccess() {

        // 重新获取数据库中的视频信息
        vi = videoDB.getupdateContect(filePath);
        if (vi.getVideoStation().equals("success")
                || vi.getVideoStation().equals("uploading")) {

            return 0;
        } else if (vi.getVideoStation().equals("hideinserver")) {
            return 1;
        }

        return 2;
    }

    /**
     * 对即将分享的文件进行空值、上传状态判断
     *
     * @return
     */
    private boolean videoFileEstimate(String sharchannel) {
        if (shareEdittext.getText().toString().isEmpty()) {
            MinUtil.showToast(mContext, "请输入游戏标题");
            sharePopWindow.dismiss();
            return false;
        } else {
            if (game_name.getText().toString().equals("请选择视频类型")) {
                MinUtil.showToast(mContext, "请选择视频类型");
                sharePopWindow.dismiss();
                return false;
            } else if (isVideoUpSuccess() == 0) {// 判断视频是否已经上传到云端

                if (!sharchannel.equals("SYSJ")) {

                    // 获得数据库中该视频的信息
                    VideoInfo vinfo = videoDB.getupdateContect(filePath);
                    // 获得视频云端连接地址
                    String Videourl = vinfo.getVideoURL();
                    // 生成视频图片
                    String iamgeurl = ResumableUpload
                            .getImageFromVideoPath(filePath);
                    isgotoselectgame = true;
                    // 开始分享
                    share(mContext, videoTitle, qiuniuUrl + Videourl + "a",
                            text, iamgeurl, sharchannel);
                } else {
                    MinUtil.showToast(mContext, "视频已经上传到云端手游视界，不必重复上传");
                }
                sharePopWindow.dismiss();
                return true;
            } else if (isVideoUpSuccess() == 1) {// 如果之前是删除过云端视频

                // 获得视频云端连接地址
                String Videourl = null;
                // 获得数据库中该视频的信息
                VideoInfo vinfo = videoDB.getupdateContect(filePath);
                // 获得视频云端连接地址
                Videourl = vinfo.getVideoURL();
                if (!sharchannel.equals("SYSJ")) {
                    // 生成视频图片
                    String iamgeurl = ResumableUpload
                            .getImageFromVideoPath(filePath);
                    isgotoselectgame = true;
                    // 开始分享
                    share(mContext, videoTitle, qiuniuUrl + Videourl + "a",
                            text, iamgeurl, sharchannel);
                } else {
                    MinUtil.showToast(mContext, "视频上传完成");
                }
                // 显示云端视频
                HttpGetThread httpGetThread = new HttpGetThread(Videourl);
                httpGetThread.start();
                // 修改数据库视频上传状态
                videoDB.setupdateContect(Videourl, "success");

            } else {// 准备本地上传并分享

                // 检测是否有视频正在上传中
                if (sharedPreferences.getBoolean("videoUploading", false)) {
                    if (sharchannel.equals("SYSJ")) {
                        MinUtil.showToast(mContext, "当前有视频正在上传，请稍候");
                        sharePopWindow.dismiss();
                        return true;
                    }

                }

                if (binder != null) {
                    // "继续上传");
                    // 先暂停再调用上传，防止视频重复上传
                    binder.stopUpLoadVideo();
                    // 继续上传，调用分享方法
                    binder.goonUploadVideo(mContext);
                } else {
                    // 开始上传
                    upvideoAndShare();
                }

                gameName = game_name.getText().toString();
                videoTitle = shareEdittext.getText().toString().trim();
                // 设置背景色
                editoVideoFrameParent.setBackgroundColor(0xfff1f1f1);
                // 隐藏视频编辑框
                editorFrame.setVisibility(View.GONE);
                // 显示上传进度框
                upvideoLinear.setVisibility(View.VISIBLE);
                // 隐藏继续上传按钮
                goonUpVideo.setVisibility(View.GONE);
                // 显示上传进度条
                progressbarLinear.setVisibility(View.VISIBLE);
                isuploading = true;

            }

        }
        sharePopWindow.dismiss();
        return true;

    }

    /**
     * 子线程显示云端视频
     *
     * @author WYX
     */
    public class HttpGetThread extends Thread {
        String videourl;

        HttpGetThread(String videourl) {
            this.videourl = videourl;
        }

        public void run() {

            super.run();
            HttpUtils.httpGet(VideoManagerActivity.delSerVideoUrl + "qn_key="
                    + videourl + "a");

        }
    }

    /**
     * 分享方法
     * <p/>
     * 微信朋友圈无法分享内容 ,只能分享标题
     * 新浪微博        ,QQ无法分享超链接 ,所以将超链接直接放在分享内容中
     *
     * @param context  上下文
     * @param title    分享的标题
     * @param url      分享的超链接
     * @param text     分享的内容
     * @param imageurl 分享的图片（1.2.6之前为网络图片，1.2.6以后为本地图片链接)
     */
    public static void share(Context context, String title, String url,
                             String text, String imageurl, String sharchannel) {

        ShareParams sp = new ShareParams();

        // 本地图片
        sp.setImagePath(imageurl);
        // 分享的标题
        sp.setTitle(title);
        // 分享的内容
        sp.setText(text);
        // 分享的链接
        sp.setUrl(url);
        // 标题链接
        sp.setTitleUrl(url);

        // (新浪微博文字内容无法携带超链接,所以将超链接直接放在分享内容中)
        if (sharchannel.equals("SinaWeibo")) {
            sp.setText(text + url);
        }
        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform plat = null;

        /**
         * 根据sharchannel值进行所选平台的分享
         */
        plat = ShareSDK.getPlatform(context, sharchannel);
        if (platformActionListener != null) {
            plat.setPlatformActionListener(platformActionListener);
        }

        plat.share(sp);

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (shareView != null) {
            shareView.setClickable(true);
        }

        isgotoselectgame = false;
    }

    protected void onDestroy() {
        super.onDestroy();

        ShareSDK.stopSDK(this);
        try {
            unbindService(conn);
        } catch (Exception e) {

        }

    }

    /**
     * 分享回调 视频上传七牛拿到视频ID后会回调该方法进行社交平台分享
     */
    public UpVideoCallBack myCallBack = new UpVideoCallBack() {

        // 分享方法
        public void share(String VideoId, String iamgeurl) {
            // 手游视界不进行分享操作
            if (!sharchannel.equals("SYSJ")) {
                isgotoselectgame = true;
                ShareActivity.share(mContext, videoTitle, qiuniuUrl + VideoId
                        + "a", text, iamgeurl, sharchannel);

            }

        }

        // 回复视频编辑框
        public void recoveryWindow(String filepath, String gameName,
                                   String videoTitle, String videoUrlId) {
            isuploading = false;
            // 显示视频编辑框
            editorFrame.setVisibility(View.VISIBLE);
            // 隐藏上传进度框
            upvideoLinear.setVisibility(View.GONE);
            // 设置背景色
            editoVideoFrameParent.setBackgroundColor(0xffffffff);

            videoDB.updateContect(filepath, "success", gameName, videoTitle,
                    100.0, videoUrlId);
            handler.sendEmptyMessage(5);
            // 设置控件可点击
            shareEdittext.setFocusable(true);
            shareEdittext.setFocusableInTouchMode(true);
            spinner.setEnabled(true);
            // 将视频标题同步到数据库
            // TODO
            setFileNameForPath(videoTitle, filepath);
            // 解绑并停止服务
            try {
                unbindService(conn);
            } catch (Exception e) {
                e.printStackTrace();
                // "解除绑定异常");
            } finally {
                try {
                    stopService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    // "服务停止异常");
                }

            }
            // 获取前台运行的activity
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
                    .getClassName();
            // 判断当前activity是否处在前台
            if (runningActivity
                    .equals("com.fmscreenrecord.activity.ShareActivity")) {
                ExApplication.isgotservervideomange = true;
                finish();
            }

        }

        private void setFileNameForPath(String videoTitle, String filepath) {
            // 获得视频的父路径
            final String pFile = new File(filepath).getParentFile().getPath()
                    + File.separator;
            File newfile = new File(pFile + videoTitle + ".mp4");
            int result = videoDB.setFileNameForPath(videoTitle,
                    newfile.getPath(), filepath);
            // 如果文件名存在重复,递归重命名
            if (result == 0) {

                videoTitle = videoTitle + "1";


                setFileNameForPath(videoTitle, filepath);
            } else if (result == 1) {
                new File(filepath).renameTo(newfile);
            }
        }

        /**
         * 修改上传控件
         */
        public void showupdateStatus(final double percentage,
                                     long uploadLastTimePoint, long uploadFileLength,
                                     long uploadLastOffset, final long filelong,
                                     final String filesize) {

            long now = System.currentTimeMillis();
            long deltaTime = now - uploadLastTimePoint;
            long currentOffset = (long) (percentage * uploadFileLength);
            long deltaSize = currentOffset - uploadLastOffset;
            if (deltaTime <= 0 || deltaSize < Config.CHUNK_SIZE) {
                return;
            }

            //通过Handler启动线程
            AsyncRun.run(new Runnable() {
                String upFileSize;

                public void run() {

                    int progress = (int) (percentage * 100);
                    progressbar.setProgress(progress);
                    progresssPercent.setText(progress + " %");
                    upFileSize = LocalVideoAdapter
                            .FormetFileSize((long) (filelong * percentage));
                    progresssText.setText(upFileSize + "/" + filesize);

                    {//更新通知服务
                        Message msg = UploadVideoService.handler.obtainMessage();
                        msg.what = 1;
                        msg.arg1 = progress;
                        msg.obj = upFileSize + "/" + filesize;
                        UploadVideoService.handler.sendMessage(msg);
                    }
                }
            });

        }

        @Override
        public void recoveryWindow(String filepath) {
            isuploading = false;
            // 显示视频编辑框
            editorFrame.setVisibility(View.VISIBLE);
            // 隐藏上传进度框
            upvideoLinear.setVisibility(View.GONE);
            // 设置背景色
            editoVideoFrameParent.setBackgroundColor(0xffffffff);
            // 取消视频上传中标志
            sharedPreferences.edit().putBoolean("videoUploading", false)
                    .commit();

            // 设置控件可点击
            shareEdittext.setFocusable(true);
            shareEdittext.setFocusableInTouchMode(true);
            spinner.setEnabled(true);
            videoDB.setupdateContect(filepath, "local");
            handler.sendEmptyMessage(5);
            // 解绑并停止服务
            try {
                unbindService(conn);
            } catch (Exception e) {
                e.printStackTrace();
                // "绑定停止异常");
            } finally {
                try {
                    stopService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    // "服务停止异常");
                }

            }

        }

    };

    // TODO

    /**
     * 返回获得游戏类型
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String gamename = data.getExtras().getString("game_name");
            game_name.setAlpha(255);
            game_name.setText(gamename);
            String videoName = shareEdittext.getText().toString();
            // 检查并删除带书名号的游戏类型标题
            try {
                videoName = videoName.substring(videoName.indexOf("》") + 1);
            } catch (Exception e) {

            }

            shareEdittext.setText("《" + gamename + "》" + videoName);

        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (isuploading) {// 如果处于上传状态，将上传状态记录进数据库

            String videorul = videoDB.getVideoToken(filePath).getVideoURL();
            videoDB.updateContect(filePath, "uploading", gameName, videoTitle,
                    0, videorul);

            handler.sendEmptyMessage(5);
        }
        if (isgotoselectgame == false) {// 如果触发onpause不是跳转到选择游戏页面，则关闭此页面
            finish();
        }

    }

    private void upVideodialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("注意")
                .setMessage("当前手机处于非WIFI环境，上传视频将消耗一定的手机流量,是否上传视频？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 显示进度条
                                progressbarLinear.setVisibility(View.VISIBLE);
                                // 隐藏继续上传按钮
                                goonUpVideo.setVisibility(View.GONE);
                                // 将分享渠道设置为SYSJ，表示上传后不进行分享
                                sharchannel = "SYSJ";
                                isuploading = true;
                                if (binder == null) {
                                    bindforwhat = 1;
                                    serviceIntent = new Intent(mContext,
                                            UploadVideoService.class);
                                    startService(serviceIntent);
                                    bindService(serviceIntent, conn,
                                            Service.BIND_AUTO_CREATE);

                                } else {

                                    binder.goonUploadVideo(mContext);
                                }
                                // 设置背景色
                                editoVideoFrameParent
                                        .setBackgroundColor(0xfff1f1f1);
                            }
                        }).setNegativeButton("取消", null).show();
    }

    /**
     * 判断字符串是否为合法的时间戳
     *
     * @param str
     * @return
     */
    public boolean isValidDate(String str) {

        // yyyy-MM-dd_HH_mm_ss
        String regEx = "^\\d{4}-\\d{2}-\\d{2}_\\d{2}_\\d{2}_\\d{2}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(str);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
    }
}