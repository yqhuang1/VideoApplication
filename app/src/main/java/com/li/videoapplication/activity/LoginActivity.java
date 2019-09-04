package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.mob.tools.utils.UIHandler;
import com.umeng.message.PushAgent;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 用户登录 界面*
 */
public class LoginActivity extends Activity implements View.OnClickListener, Callback,
        PlatformActionListener {
    private Context context;
    private ImageButton loginBack, qqIb, weiboIb, wechatIb;
    private TextView phoneLoginTv;
    private int openState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        init();
        initView();

        ExApplication.upUmenEventValue(getApplicationContext(), "登陆总次数", "login_count");
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {
        ShareSDK.initSDK(this);
        loginBack = (ImageButton) findViewById(R.id.login_back);
        loginBack.setOnClickListener(this);
        qqIb = (ImageButton) findViewById(R.id.login_qq_ib);
        qqIb.setOnClickListener(this);
        weiboIb = (ImageButton) findViewById(R.id.login_weibo_ib);
        weiboIb.setOnClickListener(this);
        wechatIb = (ImageButton) findViewById(R.id.login_wechat_ib);
        wechatIb.setOnClickListener(this);

        phoneLoginTv = (TextView) findViewById(R.id.activity_login_phone_tv);
        phoneLoginTv.setOnClickListener(this);
    }

    private void initView() {

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.login_back:
                finish();
                break;
            case R.id.login_qq_ib:
                openState = 1;
                authorize(new QQ(this));
                ExApplication.upUmenEventValue(getApplicationContext(), "QQ登陆次数", "qq_login_count");
                break;
            case R.id.login_weibo_ib:
                openState = 2;
                authorize(new SinaWeibo(this));
                ExApplication.upUmenEventValue(getApplicationContext(), "微博登陆次数", "sina_login_count");
                break;
            case R.id.login_wechat_ib:
                openState = 3;
                authorize(new Wechat(this));
                ExApplication.upUmenEventValue(getApplicationContext(), "微信登陆次数", "wechat_login_count");
                break;
            case R.id.activity_login_phone_tv:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                ExApplication.upUmenEventValue(getApplicationContext(), "手机登陆次数", "phone_login_count");
                break;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SMSSDK.unregisterAllEventHandler();

    }


    private static UserEntity user;

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
            userEntity = JsonHelper.getOtherUser(LoginActivity.this, openId, name, sex, location, figureurl);
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

                Intent intent = new Intent(LoginActivity.this, PersonalInfoActivity.class);
                intent.putExtra("flag", "persion");
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
