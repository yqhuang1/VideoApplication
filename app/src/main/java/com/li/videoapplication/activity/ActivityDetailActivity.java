package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.Adapter.VideoDetailListAdapter;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.entity.VedioDetail;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;
import com.umeng.message.PushAgent;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by feimoyuangong on 2015/2/6.
 * <p/>
 * 活动详情 页面
 */
public class ActivityDetailActivity extends Activity implements View.OnClickListener,
        RefreshListView.IXListViewListener, View.OnTouchListener {
    //    private MatchEntity activityEntity;
    private Context context;
    private MatchEntity matchEntity;

    private List<VedioDetail> vedioList;
    private List<VedioDetail> connecList;

    boolean textBoolean = false;//测试入口

    private ImageButton backBtn;

    private ImageView imgIv;
    private Button downloadBtn;
    private TextView activityname, note, endtime;

    RelativeLayout resume_Rl, reward_Rl, rule_Rl, winners_Rl;
    TextView resume_tv, reward_tv, rule_tv, winners_tv;
    ImageView resume_iv, reward_iv, rule_iv, winners_iv;
    ScrollView scrollView;
    TextView resume, reward, rule, winners;
    RelativeLayout entries_RL;

    private Button joinBtn;

    //个人信息
    private static UserEntity user = new UserEntity();
    private static String QQ;
    private static String phone;

    private RefreshListView refreshListView;
    private VideoDetailListAdapter listAdapter;

    private VideoThumbnailLoader videoThumbnailLoader;
    private ExApplication exApplication;
    private SimpleDateFormat simpleDateFormat;
    private long startTime, endTime;
    private String match_id;
    private int page = 1;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    public static String videoTitle;
    private static String fileSrc;
    // 数据库
    static VideoDB videoDB;
    VideoInfo videoInfo;

    //刷新参与活动按钮
    public Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (matchEntity.getMark() == 0) {//活动未过期
                        if (matchEntity.getStatus() == 0) {
                            joinBtn.setBackgroundResource(R.drawable.corner_red_stroke);
                            joinBtn.setText("参与活动");
                        } else if (matchEntity.getStatus() == 1) {//活动参与进行中
                            joinBtn.setBackgroundResource(R.drawable.corner_red_stroke);
                            joinBtn.setText("进行中 上传视频");
                        } else if (matchEntity.getStatus() == 2) {
                            joinBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
                            joinBtn.setText("视频已上传");
                        } else if (matchEntity.getStatus() == 3) {
                            joinBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
                            joinBtn.setText("视频已审核");
                        }
                    } else if (matchEntity.getMark() == 1) {//活动已过期
                        joinBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
                        joinBtn.setText("活动已结束");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉android头部label的方法
        setContentView(R.layout.activity_activity_detail);
        context = ActivityDetailActivity.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);

        Intent intent = getIntent();
        match_id = intent.getStringExtra("id");

        vedioList = new ArrayList<VedioDetail>();
        connecList = new ArrayList<VedioDetail>();
        listAdapter = new VideoDetailListAdapter(context, vedioList);

        refreshListView = (RefreshListView) findViewById(R.id.activity_activity_detail_rflistview);
        backBtn = (ImageButton) findViewById(R.id.activity_detail_back);
        backBtn.setOnClickListener(this);

        DialogUtils.createLoadingDialog(context, "");
        initHeaderView();

    }

    private void initHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.activity_activity_detail_head, null);

        //活动详情页面布局
        imgIv = (ImageView) view.findViewById(R.id.activity_detail_img);
        downloadBtn = (Button) view.findViewById(R.id.activity_detail_download);
        downloadBtn.setOnClickListener(this);
        activityname = (TextView) view.findViewById(R.id.activity_detail_activityname);
        note = (TextView) view.findViewById(R.id.activity_detail_note);
        endtime = (TextView) view.findViewById(R.id.activity_detail_endtime);

        resume_Rl = (RelativeLayout) view.findViewById(R.id.activity_detail_resume_Rl);
        resume_Rl.setOnClickListener(this);
        reward_Rl = (RelativeLayout) view.findViewById(R.id.activity_detail_reward_Rl);
        reward_Rl.setOnClickListener(this);
        rule_Rl = (RelativeLayout) view.findViewById(R.id.activity_detail_rule_Rl);
        rule_Rl.setOnClickListener(this);
        winners_Rl = (RelativeLayout) view.findViewById(R.id.activity_detail_winners_Rl);
        winners_Rl.setOnClickListener(this);

        resume_tv = (TextView) view.findViewById(R.id.activity_detail_resume_tv);
        reward_tv = (TextView) view.findViewById(R.id.activity_detail_reward_tv);
        rule_tv = (TextView) view.findViewById(R.id.activity_detail_rule_tv);
        winners_tv = (TextView) view.findViewById(R.id.activity_detail_winners_tv);

        resume_iv = (ImageView) view.findViewById(R.id.activity_detail_resume_iv);
        reward_iv = (ImageView) view.findViewById(R.id.activity_detail_reward_iv);
        rule_iv = (ImageView) view.findViewById(R.id.activity_detail_rule_iv);
        winners_iv = (ImageView) view.findViewById(R.id.activity_detail_winners_iv);

        scrollView = (ScrollView) view.findViewById(R.id.activity_detail_scrollview);
        scrollView.setOnTouchListener(this);

        resume = (TextView) view.findViewById(R.id.activity_detail_resume);
        reward = (TextView) view.findViewById(R.id.activity_detail_reward);
        rule = (TextView) view.findViewById(R.id.activity_detail_rule);
        winners = (TextView) view.findViewById(R.id.activity_detail_winners);

        joinBtn = (Button) view.findViewById(R.id.activity_detail_join_Btn);
        joinBtn.setOnClickListener(this);

        entries_RL = (RelativeLayout) view.findViewById(R.id.activity_detail_entries_RL);

        refreshListView.addHeaderView(view);
        refreshListView.setAdapter(listAdapter);

        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setPullLoadEnable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetActivityInfoTask(match_id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetActivityInfoTask(match_id).execute();
        }

        ExApplication.upUmenEventValue(getApplicationContext(), "活动查看次数", "activity_check_count");
    }

    public void initView() {
        videoThumbnailLoader = new VideoThumbnailLoader(getApplicationContext());
        exApplication = new ExApplication(context);
        exApplication.imageLoader.displayImage(matchEntity.getPic_pld(), imgIv);
        if (!matchEntity.getAndroid_download().equals("")) {//下载链接不为空时，显示下载游戏按钮
            downloadBtn.setVisibility(View.VISIBLE);
        }
        activityname.setText(matchEntity.getName());
        note.setText("请见下列详请");

        endtime.setText(matchEntity.getEndtime());
        resume.setText(matchEntity.getDescription());
        reward.setText(matchEntity.getRewards());
        rule.setText(matchEntity.getMatch_rule());
        winners.setText(matchEntity.getWinners());

        viewHandler.sendEmptyMessage(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMatchVideoListTask(match_id, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMatchVideoListTask(match_id, page + "").execute();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activity_detail_back:
                finish();
                break;
            case R.id.activity_detail_download://下载此游戏
                if (matchEntity != null) {
                    downloadBtn.setClickable(false);//设置下载游戏按钮只能被点击（下载游戏）一次
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                        new DownloadCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                    } else {
//                        new DownloadCountTask().execute();
//                    }
                    Uri uri = Uri.parse(matchEntity.getAndroid_download());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    ExApplication.upUmenEventValue(context, "安装游戏次数", "Install_game_count");
                }
                break;
            case R.id.activity_detail_resume_Rl:
                showResume();
                break;
            case R.id.activity_detail_rule_Rl:
                showRule();
                break;
            case R.id.activity_detail_reward_Rl:
                showReward();
                break;
            case R.id.activity_detail_winners_Rl:
                showWinners();
                break;
            case R.id.activity_detail_join_Btn://活动参与（状态）按钮

                if (!ExApplication.MEMBER_ID.equals("") || textBoolean) {//已经登录
                    if (matchEntity.getMark() == 0 || textBoolean) {//活动未到期

                        if (matchEntity.getMatch_id() == 13) {
                            hintDialog();
                            return;
                        }

                        if (matchEntity.getStatus() == 0) {//活动未到期且未参加活动，去参加

                            //获取用户资料，判断QQ和手机号是否为空
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                new getInfoDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                new getInfoDetailTask().execute();
                            }

                        } else if (matchEntity.getStatus() == 1) {//已参加活动，还未上传视频，去上传视频
                            Toast.makeText(context, "尚未上传视频，请快去上传视频！", Toast.LENGTH_SHORT).show();
                            gotoVideoManager();

                        } else if (matchEntity.getStatus() == 2) {//已上传视频，待审核
                            Toast.makeText(context, "视频已经上传成功，后台正在审核中！", 0).show();
                        } else if (matchEntity.getStatus() == 3) {//已审核，待通知结果
                            Toast.makeText(context, "视频审核成功，请静候颁奖佳音！", 0).show();
                        }
                    } else if (matchEntity.getMark() == 1) {
                        Toast.makeText(context, "活动已经结束了！", 0).show();
                    }
                } else {
                    Toast.makeText(context, "请先登录！", 0).show();
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                    ExApplication.upUmenEventValue(getApplicationContext(), "手机登陆次数", "phone_login_count");
                }
                break;
        }
    }

    /**
     * 活动提示对话框
     */
    public void hintDialog() {
        final Dialog hintDialog = new Dialog(context, R.style.loading_dialog);
        /**这里有getLayoutInflater解决bug**/
        View view = getLayoutInflater().inflate(R.layout.alter_activity_dialog, null);
        hintDialog.setContentView(view);
        final TextView tipTextView = (TextView) view.findViewById(R.id.alter_activity_dialog_msg);
        ImageView tipImageview = (ImageView) view.findViewById(R.id.alter_activity_dialog_backTV);
        CheckBox copyCB = (CheckBox) view.findViewById(R.id.alter_activity_dialog_copyCB);
        CheckBox goneCB = (CheckBox) view.findViewById(R.id.alter_activity_dialog_goneCB);

        copyCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(tipTextView.getText());
                ToastUtils.showToast(context, "已复制到剪切板");
            }
        });

        goneCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintDialog.cancel();
            }
        });

        hintDialog.setCancelable(true);// 可以用“返回键”取消
        hintDialog.show();
    }


    //填写联系信息 对话框
    private void contactDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View contentView = getLayoutInflater().inflate(R.layout.contactdialog_layout, null);
        View titleView = getLayoutInflater().inflate(R.layout.contactdialog_title, null);
        dialog.setView(contentView);
        dialog.setCustomTitle(titleView);
        final TextView title1 = (TextView) titleView.findViewById(R.id.contactdialog_title);
        final TextView title2 = (TextView) titleView.findViewById(R.id.contactdialog_title_hint);
        final EditText qq_ET = (EditText) contentView.findViewById(R.id.contactdialog_qq);
        final EditText phone_ET = (EditText) contentView.findViewById(R.id.contactdialog_phone);
        if (!QQ.equals("")) {
            qq_ET.setText(QQ);
        }
        if (!phone.equals("")) {
            phone_ET.setText(phone);
        }

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (qq_ET.getText().toString().trim().equals("") && phone_ET.getText().toString().trim().equals("")) {//填写QQ和电话号码都为空
                    title1.setTextColor(Color.RED);
                    title2.setTextColor(Color.RED);
                    ToastUtils.showToast(context, "请至少填写一项联系方式");
                    preventDismissDialog(dialogInterface);
                    return;
                } else {
                    if (!isMobileNO(phone_ET.getText().toString().trim()) &&
                            !phone_ET.getText().toString().trim().equals("")) {//输入的手机号码（不为空时）不合法
                        ToastUtils.showToast(context, "请输入合法的手机号码");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else {
                        if (!qq_ET.getText().toString().trim().equals("")) {//输入的QQ不为空
                            QQ = qq_ET.getText().toString().trim();
                        }
                        if (!phone_ET.getText().toString().trim().equals("")) {//输入的phone不为空
                            phone = phone_ET.getText().toString().trim();
                        }
                        //更新用户QQ和电话号码
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new UpdateQQPhoneInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new UpdateQQPhoneInfoTask().execute();
                        }
                        participateIn();
                        dismissDialog(dialogInterface);
                    }
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismissDialog(dialogInterface);
            }
        });
        dialog.create().show();
    }

    /**
     * 关闭对话框
     */
    private void dismissDialog(DialogInterface dialogInterface) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, true);
        } catch (Exception e) {
        }
        dialogInterface.dismiss();
    }

    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog(DialogInterface dialogInterface) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(dialogInterface, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * （QQ或手机号不为空 后），
     * 确定参加活动*
     */
    private void participateIn() {
        ExApplication.upUmenEventValue(getApplicationContext(), "活动参与次数", "activity_join_count");
        //参加赛事活动，将活动添加到我的活动列表
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new JoinMatchTask(match_id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new JoinMatchTask(match_id).execute();
        }

    }


    /**
     * 去VideoManagerActivity分享视频
     */
    public void gotoVideoManager() {
        Intent intent = new Intent(ActivityDetailActivity.this, VideoManagerActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 判断手机号码是否合法*
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    /**
     * scrollview
     * 重写onTouch()事件,在事件里通过requestDisallowInterceptTouchEvent(boolean)方法来设置父类的可用性
     * true表示父类的不可用*，即scrollview可以滑动
     * false表示父类的可用*，即scrollview不可滑动
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int scrollY = view.getScrollY();
        int height = view.getHeight();
        int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN://当手指按下的时候
                x1 = motionEvent.getX();
                y1 = motionEvent.getY();
                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP://当手指离开的时候
                x2 = motionEvent.getX();
                y2 = motionEvent.getY();
                if (scrollY == 0) {//滑动到了顶端
                    if (y1 - y2 > 50) {//向上滑
                    } else if (y2 - y1 > 50) {//向下滑
                    }
                } else if ((scrollY + height) == scrollViewMeasuredHeight) {//滑动到了底部
                    if (y1 - y2 > 50) {//向上滑
                    } else if (y2 - y1 > 50) {//向下滑
                    }
                }
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
//            case MotionEvent.ACTION_MOVE:
//                x2 = (int) motionEvent.getRawX();
//                y2 = (int) motionEvent.getRawY();
//                if (scrollY == 0) {//滑动到了顶端
//                    if (y1 - y2 > 50) {//向上滑
//                        view.getParent().requestDisallowInterceptTouchEvent(true);
//                    } else if (y2 - y1 > 50) {//向下滑
//                        view.getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                } else if ((scrollY + height) == scrollViewMeasuredHeight) {//滑动到了底部
//                    if (y1 - y2 > 50) {//向上滑
//                        view.getParent().requestDisallowInterceptTouchEvent(false);
//                    } else if (y2 - y1 > 50) {//向下滑
//                        view.getParent().requestDisallowInterceptTouchEvent(true);
//                    }
//                }
//                break;
            default:
                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return false;
    }

//    @Override
//    public void onBottom() {
//        //设为false表示已经不关心该事件了，允许父组件进行事件拦截
//        scrollView.getParent().requestDisallowInterceptTouchEvent(false);
//    }
//
//    @Override
//    public void onTop() {
//        //设为false表示已经不关心该事件了，允许父组件进行事件拦截
//        scrollView.getParent().requestDisallowInterceptTouchEvent(false);
//    }
//
//    @Override
//    public void onMiddle() {
//        //在没靠岸的时候不允许父组件拦截事件
//        scrollView.getParent().requestDisallowInterceptTouchEvent(true);
//    }

    @Override
    public void onRefresh() {
        page = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetActivityInfoTask(match_id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetActivityInfoTask(match_id).execute();
        }
    }

    @Override
    public void onLoadMore() {
        page += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMatchVideoListTask(match_id, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMatchVideoListTask(match_id, page + "").execute();
        }
    }

    private void showResume() {
        resume_tv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        resume_iv.setVisibility(View.VISIBLE);
        reward_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        reward_iv.setVisibility(View.GONE);
        rule_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        rule_iv.setVisibility(View.GONE);
        winners_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        winners_iv.setVisibility(View.GONE);

        resume.setVisibility(View.VISIBLE);
        reward.setVisibility(View.GONE);
        rule.setVisibility(View.GONE);
        winners.setVisibility(View.GONE);
    }

    private void showRule() {
        resume_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        resume_iv.setVisibility(View.GONE);
        reward_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        reward_iv.setVisibility(View.GONE);
        rule_tv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        rule_iv.setVisibility(View.VISIBLE);
        winners_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        winners_iv.setVisibility(View.GONE);

        resume.setVisibility(View.GONE);
        reward.setVisibility(View.GONE);
        rule.setVisibility(View.VISIBLE);
        winners.setVisibility(View.GONE);
    }

    private void showReward() {
        resume_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        resume_iv.setVisibility(View.GONE);
        reward_tv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        reward_iv.setVisibility(View.VISIBLE);
        rule_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        rule_iv.setVisibility(View.GONE);
        winners_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        winners_iv.setVisibility(View.GONE);

        resume.setVisibility(View.GONE);
        reward.setVisibility(View.VISIBLE);
        rule.setVisibility(View.GONE);
        winners.setVisibility(View.GONE);
    }

    private void showWinners() {
        resume_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        resume_iv.setVisibility(View.GONE);
        reward_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        reward_iv.setVisibility(View.GONE);
        rule_tv.setTextColor(getResources().getColor(R.color.video_play_tv_default));
        rule_iv.setVisibility(View.GONE);
        winners_tv.setTextColor(getResources().getColor(R.color.video_play_tv_focus));
        winners_iv.setVisibility(View.VISIBLE);

        resume.setVisibility(View.GONE);
        reward.setVisibility(View.GONE);
        rule.setVisibility(View.GONE);
        winners.setVisibility(View.VISIBLE);
    }

    //获取活动(详情)信息 异步方法
    private class GetActivityInfoTask extends AsyncTask<Void, Void, String> {
        private String match_id;

        public GetActivityInfoTask(String match_id) {
            this.match_id = match_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            System.out.println("activityid====" + match_id);
            matchEntity = JsonHelper.getMatchInfo(match_id);
            if (matchEntity != null) {
                return "s";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ("s".equals(s)) {
                initView();
            } else {
                ToastUtils.showToast(getApplicationContext(), "获取活动数据失败");
            }
        }
    }

//    /**
//     * 游戏下载量统计
//     */
//    private class DownloadCountTask extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            return JsonHelper.submitDownloadCount();
//        }
//    }

    //获取活动详情 对应赛事视频(参赛视频)列表 异步方法
    private class GetMatchVideoListTask extends AsyncTask<Void, Void, String> {
        private String match_id;
        private String page;

        public GetMatchVideoListTask(String match_id, String page) {
            this.match_id = match_id;
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            System.out.println("match_id====" + match_id);
            connecList = JsonHelper.getMatchVideoList(match_id, page);
            if (connecList != null) {
                return "s";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {//刷新
                if (s.equals("s")) {
                    refreshListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    vedioList.clear();
                    if (connecList.size() == 0) {
//                        ToastUtils.showToast(getActivity(), "没有找到相关数据");
                    } else {
                        entries_RL.setVisibility(View.VISIBLE);
                        vedioList.addAll(connecList);
                    }
                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            } else if (asyncType == LOADMORE) {//加载
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                        refreshListView.onHiddenFooterView();
                    } else {
                        vedioList.addAll(connecList);
                    }

                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            }
            listAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
            DialogUtils.cancelLoadingDialog();
        }
    }

    //参加赛事活动 异步方法
    private class JoinMatchTask extends AsyncTask<Void, Integer, Integer> {
        private String match_id;
        boolean aBoolean = false;

        public JoinMatchTask(String match_id) {
            this.match_id = match_id;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            System.out.println("activityid***" + match_id);
            aBoolean = JsonHelper.getJoinMatch(match_id);
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (aBoolean) {
                //活动状态变为进行中
                matchEntity.setStatus(1);//设置已参加活动
                joinBtn.setBackgroundResource(R.drawable.corner_red_stroke);
                joinBtn.setText("进行中 上传视频");
                ToastUtils.showToast(getApplicationContext(), "参加活动成功");
            } else {
                ToastUtils.showToast(getApplicationContext(), "参加活动失败");
            }
        }
    }


    /**
     * 异步获取 个人信息
     */
    private class getInfoDetailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            user = JsonHelper.getUserDetailInfo(context, ExApplication.MEMBER_ID, ExApplication.MEMBER_ID, "");
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
                System.out.println("获取个人信息成功");
                QQ = user.getQQ();
                phone = user.getMobile();
                if (QQ.equals("") && phone.equals("")) {//QQ与手机号码均为空
                    contactDialog();
                } else {
                    participateIn();
                }
            } else {
                System.out.println("获取个人信息不成功");
            }
            DialogUtils.cancelLoadingDialog();
        }
    }

    /**
     * 更新个人信息
     * QQ和手机号码
     */
    private class UpdateQQPhoneInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.updateQQPhoneInfo(ExApplication.MEMBER_ID, QQ, phone, context);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                ToastUtils.showToast(context, "提交联系信息成功");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
