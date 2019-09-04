package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.li.videoapplication.utils.DialogUtils;
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
 * Created by feimoyuangong on 2015/5/6.
 * <p>
 * 手机登录 界面
 */
public class RegisterActivity extends Activity implements View.OnClickListener, Handler.Callback, PlatformActionListener {

    private Context context;
    private ImageButton registerBack, qqIb, wechatIb, weiboIb;
    private TextView agreeTv;
    private ImageView agreeIv;
    private EditText phoneEt, codeEt;
    private Button getCodeBtn, loginBtn;
    private boolean agreeFlag = true;
    private int openState;
    private boolean canGetCode = true;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        context = RegisterActivity.this;
//        init();
        ShareSDK.initSDK(this);
        initView();
    }

//    private void init() {
//        ShareSDK.initSDK(this);
//        SMSSDK.initSDK(this, "3407e70513bc", "ec08272e03b38c375d68f2ecd6849a77");
//        EventHandler eventHandler = new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                handler.sendMessage(msg);
//            }
//        };
//        // 注册回调监听接口
//        SMSSDK.registerEventHandler(eventHandler);
//    }

    private void initView() {
        registerBack = (ImageButton) findViewById(R.id.register_back);
        registerBack.setOnClickListener(this);
        qqIb = (ImageButton) findViewById(R.id.register_qq_ib);
        qqIb.setOnClickListener(this);
        wechatIb = (ImageButton) findViewById(R.id.register_wechat_ib);
        wechatIb.setOnClickListener(this);
        weiboIb = (ImageButton) findViewById(R.id.register_weibo_ib);
        weiboIb.setOnClickListener(this);

        agreeTv = (TextView) findViewById(R.id.register_agree_tv);
        agreeTv.setOnClickListener(this);

        agreeIv = (ImageView) findViewById(R.id.register_agree_iv);
        agreeIv.setOnClickListener(this);

        phoneEt = (EditText) findViewById(R.id.register_phone_et);
        codeEt = (EditText) findViewById(R.id.register_code_et);

        getCodeBtn = (Button) findViewById(R.id.register_getcode_btn);
        getCodeBtn.setOnClickListener(this);
        loginBtn = (Button) findViewById(R.id.register_register_btn);
        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.register_back:
                finish();
                break;
            case R.id.register_qq_ib:
                openState = 1;
                authorize(new QQ(this));
                ToastUtils.showLongToast(context, "正在 QQ登录 中");
                break;
            case R.id.register_wechat_ib:
                openState = 2;
                authorize(new Wechat(this));
                ToastUtils.showLongToast(context, "正在 微信登录 中");
                break;
            case R.id.register_weibo_ib:
                openState = 3;
                authorize(new SinaWeibo(this));
                ToastUtils.showLongToast(context, "正在 微博登录 中");
                break;
            case R.id.register_agree_iv:
                if (agreeFlag) {
                    agreeIv.setBackgroundResource(R.drawable.register_disagree);
                    loginBtn.setClickable(false);
                    loginBtn.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    agreeFlag = false;
                } else {
                    agreeIv.setBackgroundResource(R.drawable.register_agree);
                    loginBtn.setClickable(true);
                    loginBtn.setBackgroundColor(Color.parseColor("#fc3c2d"));
                    agreeFlag = true;
                }
                break;
            case R.id.register_agree_tv:
                intent = new Intent(RegisterActivity.this, UsingAgreementActivity.class);
                startActivity(intent);
                break;
            case R.id.register_getcode_btn:
                if (canGetCode) {
                    if (!TextUtils.isEmpty(phoneEt.getText().toString())) {
//                        if (!isMobileNum(phoneEt.getText().toString())){
//                            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        SMSSDK.getVerificationCode("86", phoneEt.getText().toString());
                        phoneNum = phoneEt.getText().toString().trim();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new GetCodeTask(phoneNum).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new GetCodeTask(phoneNum).execute();
                        }
                        Message msg = new Message();
                        msg.what = 0;
                        mWaitHandler.sendMessage(msg);
                    } else {
                        Toast.makeText(this, "电话不能为空", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "操作太频繁，请稍候重试", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.register_register_btn:
                if (TextUtils.isEmpty(phoneEt.getText().toString())) {
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(codeEt.getText().toString())) {
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                DialogUtils.createLoadingDialog(RegisterActivity.this, "");
//                    SMSSDK.submitVerificationCode("86", phoneEt.getText().toString(), codeEt.getText().toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new RegisterTask(phoneNum, codeEt.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new RegisterTask(phoneNum, codeEt.getText().toString()).execute();
                }
                break;
        }
    }

//    Handler handler=new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            int event = msg.arg1;
//            int result = msg.arg2;
//            Object data = msg.obj;
//            Log.e("event", "event=" + event);
//            if (result == SMSSDK.RESULT_COMPLETE) {
//                //短信注册成功后，返回MainActivity,然后提示新好友
//                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
//                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////                        new LoginTask(phoneEt.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
////                    }else{
////                        new LoginTask(phoneEt.getText().toString()).execute();
////                    }
//                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
//                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
//                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
//                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                DialogUtils.cancelLoadingDialog();
//                try {
//                    ((Throwable) data).printStackTrace();
//                    Throwable throwable = (Throwable) data;
//                    JSONObject object = new JSONObject(throwable.getMessage());
//                    String des = object.optString("detail");
//                    if (!TextUtils.isEmpty(des)) {
//                        Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                //#if def{lang} == cn
//                // 如果木有找到资源，默认提示
//                //#elif def{lang} == en
//                // show default error when can't find the resource of string
//                //#endif
//
//                Toast.makeText(RegisterActivity.this,"请求错误", Toast.LENGTH_SHORT).show();
//
//            }
//
//        }
//
//    };

    private Handler mWaitHandler = new Handler() {
        private static final int SECOND = 1000;
        private static final int MINUTE = 60 * SECOND;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.arg1 <= MINUTE) {
                canGetCode = false;
                getCodeBtn.setEnabled(false);
                getCodeBtn.setText(String.format("(%s)重试", (MINUTE - msg.arg1) / SECOND));
                getCodeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.getcode_gray));
                Message newMsg = obtainMessage();
                newMsg.arg1 = msg.arg1 + SECOND;
                sendMessageDelayed(newMsg, SECOND);

            } else {
                canGetCode = true;
                getCodeBtn.setEnabled(true);
                getCodeBtn.setText("获取验证码");
                getCodeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.install));
            }
        }
    };

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
            System.out.println("wechat_res===" + res.toString());
            String openId = res.get("unionid").toString();
            String nickname = res.get("nickname").toString();
            String sex = res.get("sex").toString();
            String location = res.get("province").toString() + res.get("city").toString();
            String figureurl = res.get("headimgurl").toString();
            new OtherLoginTask(openId, nickname, sex, location, figureurl).execute().toString();
        }
        if (openState == 3) {
            System.out.println("sina++++++++++");
            String openId = res.get("id").toString();
            String nickname = res.get("screen_name").toString();
            String sex = ((res.get("gender").equals("m")) ? "男" : "女").toString();
            String location = res.get("location").toString();
            String figureurl = res.get("avatar_large").toString();
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
        UIHandler.sendMessage(msg, RegisterActivity.this);
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

    private static UserEntity user;

    /**
     * 获取验证码
     */
    private class GetCodeTask extends AsyncTask<Void, Void, String> {

        private String key;

        public GetCodeTask(String key) {
            this.key = key;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.getPhoneCode(key);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DialogUtils.cancelLoadingDialog();
            if ("s".equals(s)) {
                ToastUtils.showToast(RegisterActivity.this, "验证码已发送");
            } else {
                ToastUtils.showToast(RegisterActivity.this, "验证码发送失败");
            }
        }
    }

    /**
     * 手机注册
     */
    private class RegisterTask extends AsyncTask<Void, Void, String> {

        private String key;
        private String code;

        public RegisterTask(String key, String code) {
            this.key = key;
            this.code = code;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.phoneRegister(key, code);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DialogUtils.cancelLoadingDialog();
            if ("s".equals(s)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetUserInfoTask(phoneEt.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetUserInfoTask(phoneEt.getText().toString()).execute();
                }
            } else {
                ToastUtils.showToast(RegisterActivity.this, "注册失败");
            }
        }
    }

    /**
     * 手机注册
     */
    private class GetUserInfoTask extends AsyncTask<Void, Void, String> {

        private String key;

        public GetUserInfoTask(String key) {
            this.key = key;
        }

        @Override
        protected String doInBackground(Void... voids) {
            user = JsonHelper.getUserInfo(RegisterActivity.this, key);
            if (user != null) {
                ExApplication.MEMBER_ID = user.getId();
                return "s";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DialogUtils.cancelLoadingDialog();
            if ("s".equals(s)) {
                //记录登陆过的号码，方便
                // 下次登陆提示,只记录5个记录
//                boolean b=false;
//                int j=0;
//                for (int i=0;i<5;i++){
//                    if (phoneNum.equals(SharePreferenceUtil.getPreference(RegisterActivity.this, i + ""))){
//                        b=true;
//                    }
//                    if("".equals(SharePreferenceUtil.getPreference(RegisterActivity.this,i+""))){
//                        j=i;
//                    }
//                }
//                if (b==false){
//                    SharePreferenceUtil.setPreference(RegisterActivity.this,j+"",phoneNum);
//                }
                if ("videoplay".equals(getIntent().getStringExtra("flag"))) {
                    finish();
                    return;
                }
                Intent intent = new Intent(RegisterActivity.this, PersonalInfoActivity.class);
                intent.putExtra("flag", "persion");
                startActivity(intent);
                finish();

                CompleteTaskUtils utils;
                utils = new CompleteTaskUtils(RegisterActivity.this, "15");
                utils.completeMission();

                ExApplication.upUmenEventValue(getApplicationContext(), "手机登陆成功次数", "phone_login_success_count");
            } else {
                ToastUtils.showToast(RegisterActivity.this, "该账号已注册！");
            }

        }
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
            userEntity = JsonHelper.getOtherUser(RegisterActivity.this, openId, name, sex, location, figureurl);
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
                Intent intent = new Intent(RegisterActivity.this, PersonalInfoActivity.class);
                intent.putExtra("flag", "persion");
                startActivity(intent);
                finish();
            }
        }
    }

}
