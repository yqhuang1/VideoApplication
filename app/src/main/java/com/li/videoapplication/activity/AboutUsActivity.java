package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fmscreenrecord.utils.MinUtil;
import com.li.videoapplication.R;
import com.li.videoapplication.entity.Update;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.li.videoapplication.utils.VersionUtils;
import com.qiniu.android.common.Config;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.PushAgent;

/**
 * 关于我们 页面*
 */
public class AboutUsActivity extends Activity implements View.OnClickListener {

    private Context context;
    private ImageButton backBtn;
    private TextView versionTv;
    private LinearLayout functionLayout, websiteLayout, wechatLayout, qqGroupLayout,
            emailLayout, recruitmentLayout, statementLayout, feedbackLayout;

    private Update update;
    private FeedbackAgent agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about_us);
        context = AboutUsActivity.this;
        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        agent = new FeedbackAgent(context);
        agent.setWelcomeInfo("手游视界交流群：" + getString(R.string.us_qqGroup) +
                "。\n你可以去群里图文并茂的反映你的问题以便更快的得到解决！");

        initView();

    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.about_us_back);
        backBtn.setOnClickListener(this);
        versionTv = (TextView) findViewById(R.id.about_us_versionTv);
        versionTv.setText(VersionUtils.getCurrentVersionName(AboutUsActivity.this));

        functionLayout = (LinearLayout) findViewById(R.id.about_us_functionLayout);
        functionLayout.setOnClickListener(this);
        websiteLayout = (LinearLayout) findViewById(R.id.about_us_websiteLayout);
        websiteLayout.setOnClickListener(this);
        wechatLayout = (LinearLayout) findViewById(R.id.about_us_wechatLayout);
        wechatLayout.setOnClickListener(this);
        qqGroupLayout = (LinearLayout) findViewById(R.id.about_us_qqGroupLayout);
        qqGroupLayout.setOnClickListener(this);
        emailLayout = (LinearLayout) findViewById(R.id.about_us_emailLayout);
        emailLayout.setOnClickListener(this);
        recruitmentLayout = (LinearLayout) findViewById(R.id.about_us_recruitmentLayout);
        recruitmentLayout.setOnClickListener(this);
        statementLayout = (LinearLayout) findViewById(R.id.about_us_statementLayout);
        statementLayout.setOnClickListener(this);
        feedbackLayout = (LinearLayout) findViewById(R.id.about_us_feedbackLayout);
        feedbackLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.about_us_back:
                this.finish();
                break;
            case R.id.about_us_functionLayout://新功能介绍
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new functionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new functionTask().execute();
                }
                break;
            case R.id.about_us_websiteLayout://官方网站
                Uri uri_f = Uri.parse(getString(R.string.us_website));
                Intent intent_f = new Intent(Intent.ACTION_VIEW, uri_f);
                intent_f.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                this.startActivity(intent_f);
                break;
            case R.id.about_us_wechatLayout://微信公众号
                joinWeChat();
                break;
            case R.id.about_us_qqGroupLayout://玩家QQ群
                String key = "9HXwMlhi_N2hUFm4U88pDSMWGFSEhyed";
                joinQQGroup(key);
                break;
            case R.id.about_us_emailLayout://合作邮箱
                mailContact(getString(R.string.us_email));
                break;
            case R.id.about_us_recruitmentLayout://人才招聘
                mailContact(getString(R.string.us_recruitemnt));
                break;
            case R.id.about_us_statementLayout://隐私及免责声明
                intent = new Intent(context, UsingAgreementActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.about_us_feedbackLayout://意见及反馈
                agent.startFeedbackActivity();
                finish();
                break;
        }
    }

    private void joinWeChat() {
        // 将微信公众号复制进粘贴板
        ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cmb.setText("fmsysj");

        /**微信提示框**/
        new AlertDialog.Builder(context)
                .setMessage("\n公众号“fmsysj”已复制，您可以在微信中直接粘贴负责搜索添加关注\n")
                .setPositiveButton("前往微信", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent();
                            ComponentName cmp = new ComponentName("com.tencent.mm",
                                    "com.tencent.mm.ui.LauncherUI");
                            intent.setAction(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setComponent(cmp);
                            startActivityForResult(intent, 0);
                        } catch (Exception e) {
                            MinUtil.showToast(context, "打开失败，请确保您手机有安装微信客户端");
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    /**
     * *************
     * <p/>
     * 发起添加群流程。群号：手游视界交流群(459419773) 的 key 为： 9HXwMlhi_N2hUFm4U88pDSMWGFSEhyed
     * 调用 joinQQGroup(9HXwMlhi_N2hUFm4U88pDSMWGFSEhyed) 即可发起手Q客户端申请加群 手游视界交流群(459419773)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     * ****************
     */
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * Android发送邮件，并弹出邮箱类应用供选择*
     */
    public void mailContact(String mailAdress) {
        Uri uri = Uri.parse("mailto:" + mailAdress);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        //intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        // intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
        // intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
        context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }


    /**
     * 版本更新
     */
    private class functionTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            update = JsonHelper.getUpdate(context);
            if (update != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(context, "检查更新失败,请检查网络！");
                return;
            }
            if (s.equals("s")) {
                String changelog = update.getChange_log();
                String[] changeArray = changelog.split(";");
                changelog = "";
                for (int i = 0; i < changeArray.length; i++) {
                    if (i != changeArray.length) {
                        changelog += changeArray[i] + "\n";
                    } else {
                        changelog += changeArray[i];
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("新功能介绍");
                builder.setMessage(changelog);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
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
