package com.li.videoapplication.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.li.videoapplication.R;
import com.li.videoapplication.View.CircularImage;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.FavorRecommendActivity;
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.activity.MissionCenterActivity;
import com.li.videoapplication.activity.MyGiftActivity;
import com.li.videoapplication.activity.MyMessageActivity;
import com.li.videoapplication.activity.PersonalInfoActivity;
import com.li.videoapplication.activity.RecordActivity;
import com.li.videoapplication.activity.RegisterActivity;
import com.li.videoapplication.activity.SettingActivity;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.download.DownloadListActivity;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by feimoyuangong on 2015/5/4.
 * 主页 左侧栏
 */
public class PersionalFragment extends Fragment implements View.OnClickListener, Callback, PlatformActionListener {

    private CircularImage headImg;
    private RelativeLayout loginLayout, personLayout, unloginLayout, giftLayout, collectLayout, msgLayout,
            videoLayout, missionLayout, playrecordLayout, setLayout, missionCountLayout, recommendLayout;
    private RelativeLayout downloadLayout;
    private ImageView sexIv;
    private TextView nameTv, levelTv, experienceTv, focusTv, fansTv, missionCountTv;
    private ImageButton phone_Ib, qq_Ib, wechat_Ib, weibo_Ib;
    private ProgressBar experiencePb;
    private View view;
    private Context context;
    private UserEntity user;
    private Intent intent;
    private ExApplication exApplication;

    private int openState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_persional_layout, container, false);
        context = view.getContext();
        exApplication = new ExApplication(context);

        init();

        ExApplication.upUmenEventValue(context, "登陆总次数", "login_count");

        return view;
    }

    private void init() {
        ShareSDK.initSDK(context);

        //个人头像
        headImg = (CircularImage) view.findViewById(R.id.personal_head_img);

        //已登录显示的信息 昵称、经验条、经验值
        personLayout = (RelativeLayout) view.findViewById(R.id.persional_person_rl);
        //已登录的界面
        loginLayout = (RelativeLayout) view.findViewById(R.id.persional_login_rl);
        loginLayout.setOnClickListener(this);
        //未登录的界面
        unloginLayout = (RelativeLayout) view.findViewById(R.id.persional_unlogin_rl);

        sexIv = (ImageView) view.findViewById(R.id.persional_sex_Iv);
        levelTv = (TextView) view.findViewById(R.id.persional_degree_tv);
        nameTv = (TextView) view.findViewById(R.id.persional_name_tv);
        experienceTv = (TextView) view.findViewById(R.id.persional_experience_tv);
        focusTv = (TextView) view.findViewById(R.id.persional_focus_count_tv);
        fansTv = (TextView) view.findViewById(R.id.persional_fans_count_tv);
        missionCountTv = (TextView) view.findViewById(R.id.personal_mission_count_tv);

        //手机、QQ、微信、微博   登录
        phone_Ib = (ImageButton) view.findViewById(R.id.persional_login_phone);
        phone_Ib.setOnClickListener(this);
        qq_Ib = (ImageButton) view.findViewById(R.id.persional_login_qq);
        qq_Ib.setOnClickListener(this);
        wechat_Ib = (ImageButton) view.findViewById(R.id.persional_login_wechat);
        wechat_Ib.setOnClickListener(this);
        weibo_Ib = (ImageButton) view.findViewById(R.id.persional_login_weibo);
        weibo_Ib.setOnClickListener(this);

        experiencePb = (ProgressBar) view.findViewById(R.id.persional_experience_pb);

        giftLayout = (RelativeLayout) view.findViewById(R.id.persional_gift_rl);
        giftLayout.setOnClickListener(this);
//        collectLayout = (RelativeLayout) view.findViewById(R.id.persional_collect_rl);
//        collectLayout.setOnClickListener(this);
        msgLayout = (RelativeLayout) view.findViewById(R.id.persional_msg_rl);
        msgLayout.setOnClickListener(this);
        videoLayout = (RelativeLayout) view.findViewById(R.id.persional_video_rl);
        videoLayout.setOnClickListener(this);
        missionLayout = (RelativeLayout) view.findViewById(R.id.persional_mission_rl);
        missionLayout.setOnClickListener(this);
        playrecordLayout = (RelativeLayout) view.findViewById(R.id.persional_playrecord_rl);
        playrecordLayout.setOnClickListener(this);
        downloadLayout = (RelativeLayout) view.findViewById(R.id.persional_download_rl);
        downloadLayout.setOnClickListener(this);
        recommendLayout = (RelativeLayout) view.findViewById(R.id.persional_recommend_rl);
        recommendLayout.setOnClickListener(this);
        setLayout = (RelativeLayout) view.findViewById(R.id.persional_set_rl);
        setLayout.setOnClickListener(this);
        missionCountLayout = (RelativeLayout) view.findViewById(R.id.personal_mission_count_rl);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ExApplication.MEMBER_ID.equals("")) {
            missionCountLayout.setVisibility(View.GONE);
            ((MainActivity) context).noticeIv.setVisibility(View.GONE);
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
            case R.id.persional_gift_rl://我的礼物
                if (user != null) {
                    intent = new Intent(context, MyGiftActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(context, "请先登录！");
                }
                break;
//            case R.id.persional_collect_rl:
//                if (ExApplication.MEMBER_ID == null || "".equals(ExApplication.MEMBER_ID)) {
//                    ToastUtils.showToast(context, "请先登录");
//                    return;
//                }
//                intent = new Intent(context, CollectActivity.class);
//                startActivity(intent);
//                break;
            case R.id.persional_msg_rl://我的消息
                if (ExApplication.MEMBER_ID == null || "".equals(ExApplication.MEMBER_ID)) {
                    ToastUtils.showToast(context, "请先登录");
                    return;
                }
                intent = new Intent(context, MyMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.persional_video_rl://我的视频
                intent = new Intent(context, VideoManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.persional_mission_rl://我的任务
                intent = new Intent(context, MissionCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.persional_playrecord_rl://观看记录
                intent = new Intent(context, RecordActivity.class);
                startActivity(intent);
                break;
            case R.id.persional_download_rl://下载管理
                intent = new Intent(context, DownloadListActivity.class);
                intent.putExtra(DownloadListActivity.DOWNLOADED, false);
                startActivity(intent);
                break;
            case R.id.persional_recommend_rl://精彩推荐
                intent = new Intent(context, FavorRecommendActivity.class);
                intent.putExtra("flag", "personal");
                startActivity(intent);
                break;
            case R.id.persional_set_rl://设置
                intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.persional_login_rl://已登录界面
                intent = new Intent(context, PersonalInfoActivity.class);
                intent.putExtra("flag", "persion");
                startActivity(intent);
                break;

            case R.id.persional_login_phone://手机登录
                intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
                ExApplication.upUmenEventValue(context, "手机登陆次数", "phone_login_count");
                break;
            case R.id.persional_login_qq://QQ登录
                openState = 1;
                authorize(new QQ(context));
                ToastUtils.showLongToast(context, "正在 QQ登录 中");
                ExApplication.upUmenEventValue(context, "QQ登陆次数", "qq_login_count");
                break;
            case R.id.persional_login_wechat://微信登录
                openState = 3;
                authorize(new Wechat(context));
                ToastUtils.showLongToast(context, "正在 微信登录 中");
                ExApplication.upUmenEventValue(context, "微信登陆次数", "wechat_login_count");
                break;
            case R.id.persional_login_weibo://微博登录
                openState = 2;
                authorize(new SinaWeibo(context));
                ToastUtils.showLongToast(context, "正在 微博登录 中");
                ExApplication.upUmenEventValue(context, "微博登陆次数", "sina_login_count");
                break;

        }
    }


    /**
     * 获取用户信息
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
                loginLayout.setVisibility(View.VISIBLE);
                unloginLayout.setVisibility(View.GONE);
                exApplication.imageLoader.displayImage(user.getImgPath(), headImg, exApplication.getHeadOptions());
//                headImg.setBackgroundResource(R.drawable.persional_image_login);
                if (user.getSex().equals("1")) {//男性
                    sexIv.setBackgroundResource(R.drawable.sex_person_male);
                } else if (user.getSex().equals("2")) {//女性
                    sexIv.setBackgroundResource(R.drawable.sex_person_female);
                }
                levelTv.setText("Lv." + user.getDegree());
                nameTv.setText(user.getTitle());
                experiencePb.setMax(user.getNext_exp());
                experiencePb.setProgress(Integer.parseInt(user.getRank()));
                experienceTv.setText(user.getRank() + "/" + user.getNext_exp());
                focusTv.setText("关注：" + user.getAttention());
                fansTv.setText("粉丝：" + user.getFans());
                if (!"0".equals(user.getMisstion_count())) {
                    missionCountTv.setText(user.getMisstion_count());
                    missionCountLayout.setVisibility(View.VISIBLE);
                    ((MainActivity) context).noticeIv.setVisibility(View.VISIBLE);
                } else {
                    missionCountLayout.setVisibility(View.GONE);
                    ((MainActivity) context).noticeIv.setVisibility(View.GONE);
                }
            } else {
                loginLayout.setVisibility(View.GONE);
                unloginLayout.setVisibility(View.VISIBLE);
                headImg.setImageResource(R.drawable.persional_image_default);
            }
        }

    }

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
        Dialog dlg = new Dialog(context);
        View dlgView = View.inflate(context, R.layout.other_plat_dialog, null);
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
                Toast.makeText(context, R.string.userid_found, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {

                String text = getString(R.string.logining, msg.obj);
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(context, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_ERROR: {
                Toast.makeText(context, R.string.auth_error, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_COMPLETE: {
                Toast.makeText(context, R.string.auth_complete, Toast.LENGTH_SHORT).show();
            }
            break;
        }
        return false;
    }

    private String reponse = "";
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
            userEntity = JsonHelper.getOtherUser(context, openId, name, sex, location, figureurl);
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
                    ExApplication.upUmenEventValue(context, "QQ登陆成功次数", "qq_login_success_count");
                } else {
                    ExApplication.upUmenEventValue(context, "微博登陆成功次数", "sina_login_success_count");
                }
                ExApplication.upUmenEventValue(context, "登陆成功次数", "login_success_count");

                Intent intent = new Intent(context, PersonalInfoActivity.class);
                intent.putExtra("flag", "persion");
                startActivity(intent);
            }
        }
    }


}

