package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.MissionGroupAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.CircularImage;
import com.li.videoapplication.entity.MissionEntity;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.ToastUtils;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 任务中心
 * <p/>
 * 标题：我的任务
 * <p/>
 * Created by feimoliaochanghe on 2015/2/2.
 */
public class MissionCenterActivity extends Activity implements View.OnClickListener,
        ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    private ImageButton backBtn;

    private static RelativeLayout loginLayout, personLayout;
    private static CircularImage headImg;
    private static ImageView sexIv, questionIv, drawrightIv;
    private static TextView nameTv, levelTv, experienceTv, focusTv, fansTv;
    private static ProgressBar experiencePb;


    private ExpandableListView mEListView;
    private MissionGroupAdapter missionAdapter;
    private List<MissionEntity> list;

    private List<String> group_list;
    private List<MissionEntity> children1_list;
    private List<MissionEntity> children2_list;
    private List<List<MissionEntity>> children_list;

    private static Context context;
    private static UserEntity user;
    private Intent intent;
    private static ExApplication exApplication;
    CompleteTaskUtils utils;

    private Random rand = new Random();
    private String recommendId = "";

    public static Handler refreshHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    exApplication.imageLoader.displayImage(user.getImgPath(), headImg, exApplication.getHeadOptions());
//                headImg.setBackgroundResource(R.drawable.persional_image_login);
                    nameTv.setText(user.getTitle());
                    if (user.getSex().equals("1")) {//男性
                        sexIv.setBackgroundResource(R.drawable.sex_person_male);
                    } else if (user.getSex().equals("2")) {//女性
                        sexIv.setBackgroundResource(R.drawable.sex_person_female);
                    }
                    levelTv.setText("Lv." + user.getDegree());
                    experiencePb.setMax(user.getNext_exp());
                    experiencePb.setProgress(Integer.parseInt(user.getRank()));
                    experienceTv.setText(user.getRank() + "/" + user.getNext_exp());
                    focusTv.setText("关注 " + user.getAttention());
                    fansTv.setText("粉丝 " + user.getFans());
                    break;

                case 1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new getInfoDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new getInfoDetailTask().execute();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_center);
        context = MissionCenterActivity.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        exApplication = new ExApplication(context);
        {
            Set<String> adIdSet = SharePreferenceUtil.getHomeAdId(context);
            List<String> adIdList = new ArrayList<>(adIdSet);
            recommendId = adIdList.get(rand.nextInt(adIdList.size()));
        }
        init();
    }

    private void init() {
        backBtn = (ImageButton) findViewById(R.id.activity_mission_center_back);
        backBtn.setOnClickListener(this);

        loginLayout = (RelativeLayout) findViewById(R.id.activity_mission_center_login_rl);
        loginLayout.setOnClickListener(this);
        headImg = (CircularImage) findViewById(R.id.activity_mission_center_head_img);
        headImg.setOnClickListener(this);
        drawrightIv = (ImageView) findViewById(R.id.activity_mission_center_drawright_Iv);
        drawrightIv.setOnClickListener(this);

        personLayout = (RelativeLayout) findViewById(R.id.activity_mission_center_person_rl);
        nameTv = (TextView) findViewById(R.id.activity_mission_center_name_tv);
        sexIv = (ImageView) findViewById(R.id.activity_mission_center_sex_Iv);
        levelTv = (TextView) findViewById(R.id.activity_mission_center_degree_tv);
        levelTv.setOnClickListener(this);
        questionIv = (ImageView) findViewById(R.id.activity_mission_center_question_Iv);
        questionIv.setOnClickListener(this);
        experienceTv = (TextView) findViewById(R.id.activity_mission_center_experience_tv);
        experiencePb = (ProgressBar) findViewById(R.id.activity_mission_center_experience_pb);
        focusTv = (TextView) findViewById(R.id.activity_mission_center_focus_count_tv);
        fansTv = (TextView) findViewById(R.id.activity_mission_center_fans_count_tv);

        mEListView = (ExpandableListView) findViewById(R.id.activity_mission_center_elv);
        mEListView.setOnGroupClickListener(this);
        mEListView.setOnChildClickListener(this);

        list = new ArrayList<MissionEntity>();
        group_list = new ArrayList<String>();
        children1_list = new ArrayList<MissionEntity>();
        children2_list = new ArrayList<MissionEntity>();
        children_list = new ArrayList<List<MissionEntity>>();

        DialogUtils.createLoadingDialog(context, "");
        int pageId = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMissionTask("", pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMissionTask("", pageId + "").execute();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new getInfoDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new getInfoDetailTask().execute();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_mission_center_back:
                finish();
                break;

            case R.id.activity_mission_center_drawright_Iv:
            case R.id.activity_mission_center_head_img:
                intent = new Intent(context, PersonalInfoActivity.class);
                intent.putExtra("flag", "persion");
                startActivity(intent);
                break;

            case R.id.activity_mission_center_degree_tv:
            case R.id.activity_mission_center_question_Iv://手游视界等级解疑
                answerDialog();
                break;
        }
    }

    /**
     * ExpandableListView父项监听 设置为点击无张合*
     */
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        return true;
    }

    /**
     * ExpandableListView子项监听*
     */
    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
        missionIntent(groupPosition, childPosition);
        return false;
    }

    //任务意图
    public void missionIntent(int groupPosition, int childPosition) {
        Intent missionIntent;
        MissionEntity item_list = children_list.get(groupPosition).get(childPosition);
        if (!ExApplication.MEMBER_ID.equals("")) {
            if (item_list.getStatus_txt().equals("进行中")) {
                if (item_list.getId().equals("10")) {//跳转 搜索页面
                    Intent intent = new Intent(getApplication(), SearchActivity.class);
                    startActivity(intent);
                    this.finish();
                } else if (item_list.getId().equals("11")) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    this.finish();
                } else if (item_list.getId().equals("12")) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    this.finish();
                } else if (item_list.getId().equals("13")) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    this.finish();
                } else if (item_list.getId().equals("14")) {
                    Intent intent = new Intent(getApplication(), ShareActivity.class);
                    startActivity(intent);
                    this.finish();
                } else if (item_list.getId().equals("15")) {
                    if (!ExApplication.MEMBER_ID.equals("")) {
                        CompleteTaskUtils utils;
                        utils = new CompleteTaskUtils(getApplication(), "15");
                        utils.completeMission();
                    }
                } else if (item_list.getId().equals("16")) {//新手任务——完善个人资料
                    Intent intent = new Intent(getApplication(), PersonalInfoActivity.class);
                    intent.putExtra("flag", "persion");
                    startActivity(intent);
                    ToastUtils.showToast(context, "新手任务：完善个人资料");
                    this.finish();
                } else if (item_list.getId().equals("17")) {//新手任务——推广手游视界APP
                    showShare();
                } else if (item_list.getId().equals("18")) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    MainActivity.viewHandler.sendEmptyMessage(0);
                    this.finish();
                } else if (item_list.getId().equals("19")) {//每日任务——观看3个视频
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    MainActivity.viewHandler.sendEmptyMessage(0);
                    ToastUtils.showToast(context, "每日任务：观看3个视频");
                    this.finish();
                } else if (item_list.getId().equals("20")) {//每日任务——收藏1个视频
                    if (recommendId.equals("")) {
                        missionIntent = new Intent(getApplication(), MainActivity.class);
                    } else {
                        missionIntent = new Intent(getApplication(), VideoPlayActivity.class);
                        missionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        missionIntent.putExtra("id", recommendId);
                    }
                    startActivity(missionIntent);
                    MainActivity.viewHandler.sendEmptyMessage(0);
                    ToastUtils.showToast(context, "每日任务：收藏1个视频");
                    this.finish();
                } else if (item_list.getId().equals("21")) {//每日任务——给1部视频点赞
                    if (recommendId.equals("")) {
                        missionIntent = new Intent(getApplication(), MainActivity.class);
                    } else {
                        missionIntent = new Intent(getApplication(), VideoPlayActivity.class);
                        missionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        missionIntent.putExtra("id", recommendId);
                    }
                    startActivity(missionIntent);
                    MainActivity.viewHandler.sendEmptyMessage(0);
                    ToastUtils.showToast(context, "每日任务：给1部视频点赞");
                    this.finish();
                } else if (item_list.getId().equals("22")) {//每日任务——分享2个视频给好友
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    MainActivity.viewHandler.sendEmptyMessage(0);
                    ToastUtils.showToast(context, "每日任务：分享2个视频给好友");
                    this.finish();
                }
            } else {

            }
        } else {
            ToastUtils.showToast(getApplication(), "请先登录");
//            Intent intent = new Intent(this, RegisterActivity.class);
//            startActivity(intent);
//            finish();
//            ExApplication.upUmenEventValue(getApplicationContext(), "手机登陆次数", "phone_login_count");
        }
    }

    /**
     * 问题解疑对话框
     */
    public void answerDialog() {
        final Dialog answerDialog = new Dialog(context, R.style.loading_dialog);
        /**这里用getLayoutInflater解决bug**/
        View view = getLayoutInflater().inflate(R.layout.alter_answer_dialog, null);
        answerDialog.setContentView(view);
        TextView tipTextView = (TextView) view.findViewById(R.id.alter_answer_dialog_tv);
        tipTextView.setText(Html.fromHtml("<p>\n" +
                "\t<u><strong><span style=\"font-size:14px;\">1、手游视界等级体系是什么?</span></strong></u> \n" +
                "</p>\n" +
                "<p>\n" +
                "\t&nbsp; &nbsp; &nbsp;视界等级是手游视界独享的一套等级系统，根据经验累计值来获取等级的提升，账号等级越高，可享受到的等级福利越多。\n" +
                "</p>\n" +
                "<p>\n" +
                "\t<u><strong><span style=\"font-size:14px;\">2、如何提升账号等级?</span></strong><strong><span style=\"font-size:14px;\"></span></strong></u><strong><span style=\"font-size:14px;\"></span></strong> \n" +
                "</p>\n" +
                "<p>\n" +
                "\t&nbsp; &nbsp; &nbsp;目前手游视界设置的账号等级最高为48级。视友每天可以通过完成每日成长任务和系统任务来获取经验值。\n" +
                "</p>"));
        ImageView tipImageview = (ImageView) view.findViewById(R.id.alter_answer_dialog_iv);
        tipImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerDialog.cancel();
            }
        });

        answerDialog.setCancelable(true);// 可以用“返回键”取消
        answerDialog.show();
    }

    private void showShare() {

        final String url = "http://www.ifeimo.com/sysj-download.php";

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
                ToastUtils.showToast(getApplicationContext(), "分享链接已复制");
//                oks.finish();
            }
        };
        oks.setCustomerLogo(logo,null, label, listener);
//            oks.show(context);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        oks.setNotification(R.drawable.tubiao_top, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//            if (platform.getName().equals(Wechat.NAME)) {//微信朋友圈内分享显示的标题
//                oks.setTitle(detail.getName());
//            } else {
        oks.setTitle(getString(R.string.app_name));
//            }
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
//            oks.setTitleUrl(youkuDetail.getLink());
        // text是分享文本，所有平台都需要这个字段
//            oks.setText("快来看看" + detail.getName() + youkuDetail.getLink());
        oks.setText("玩手游，看视界，一切尽在手游视界。我刚刚下载了一个叫[手游视界]的APP，" +
                "里面有各种手游精彩视频和各种手游礼包活动，赶快一起来玩吧。");
        /** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
        oks.setImageUrl("http://www.17sysj.com/Public/logo/logo.png");
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

    /**
     * 获取用户信息
     */
    private static class getInfoDetailTask extends AsyncTask<Void, Void, String> {
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
                loginLayout.setVisibility(View.VISIBLE);
                refreshHandle.sendEmptyMessage(0);
            } else {
                loginLayout.setVisibility(View.GONE);
                headImg.setImageResource(R.drawable.persional_image_default);
            }
        }

    }


    /**
     * 获取任务列表异步请求
     */
    private class GetMissionTask extends AsyncTask<Void, Void, String> {

        String nickname = "";
        String page = "";

        public GetMissionTask(String nickname, String page) {
            this.page = page;
            this.nickname = nickname;
        }

        @Override
        protected String doInBackground(Void... params) {
            list = JsonHelper.getMissionList(nickname, page);
            if (list != null) {
                group_list.add("成长任务");
                group_list.add("系统任务");

                for (MissionEntity missionEntity : list) {
                    if (missionEntity.getTaskTypeName().equals("每日任务")) {//成长任务
                        children1_list.add(missionEntity);//属于新手任务且没领取奖励，即可以在列表中显示
                    } else if (missionEntity.getTaskTypeName().equals("新手任务") && missionEntity.getIs_get().equals("0")) {//系统任务
                        children2_list.add(missionEntity);
                    }
                }
                children_list.add(children1_list);
                children_list.add(children2_list);
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            missionAdapter = new MissionGroupAdapter(context, group_list, children_list);
            mEListView.setAdapter(missionAdapter);
            missionAdapter.notifyDataSetChanged();
            mEListView.expandGroup(0);
            if (children2_list.size() != 0) {
                mEListView.expandGroup(1);
            } else {
                mEListView.collapseGroup(1);
            }

            DialogUtils.cancelLoadingDialog();
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
