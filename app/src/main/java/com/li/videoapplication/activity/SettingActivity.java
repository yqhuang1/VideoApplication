package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.li.videoapplication.R;
import com.li.videoapplication.entity.Update;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.li.videoapplication.utils.VersionUtils;
import com.umeng.message.PushAgent;

/**
 * 应用设置 页面*
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private Context context;
    private TextView settingTv, aboutUsTv, versionTv;
    private RelativeLayout updateRl;
    private ImageButton backBtn;

    private ExApplication exApplication;
    /**
     * 更新方式：百度（更新） 或者 后台（更新）
     * WAY：Baidu or Background
     */
    private final String WAY = "Background";
    private Update update;

    //百度自动更新
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        context = SettingActivity.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        initView();
    }

    private void initView() {

        update = new Update();
        exApplication = new ExApplication(this);
        backBtn = (ImageButton) findViewById(R.id.setting_back);
        backBtn.setOnClickListener(this);

        settingTv = (TextView) findViewById(R.id.setting_setting);
        settingTv.setOnClickListener(this);

        aboutUsTv = (TextView) findViewById(R.id.setting_about);
        aboutUsTv.setOnClickListener(this);

//        updateTv=(TextView)findViewById(R.id.setting_update);
//        updateTv.setOnClickListener(this);
        updateRl = (RelativeLayout) findViewById(R.id.setting_update_rl);
        updateRl.setOnClickListener(this);

        versionTv = (TextView) findViewById(R.id.setting_version_tv);
        versionTv.setText("当前版本:" + VersionUtils.getCurrentVersionName(SettingActivity.this));

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_back:
                this.finish();
                break;
            case R.id.setting_setting://功能设置
                Intent intent1 = new Intent();
                intent1.setClass(SettingActivity.this, FunctionSettingActivity.class);
                SettingActivity.this.startActivity(intent1);
                break;
            case R.id.setting_about://关于我们
                Intent intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_update_rl://检查更新
                if (WAY.equals("Background")) {//后台更新Background
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new UpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new UpdateTask().execute();
                    }
                } else if (WAY.equals("Baidu")) {//百度自动更新 UI更新
                    dialog.show();
                    BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
                }
                break;
        }
    }

    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
        public void onCheckComplete() {
            dialog.dismiss();
        }

    }

    /**
     * 版本更新
     */
    private class UpdateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            update = JsonHelper.getUpdate(SettingActivity.this);
            if (update != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(SettingActivity.this, "检查更新失败,请检查网络！");
                return;
            }
            if (s.equals("s")) {
                if ("U".equals(update.getUpdate_flag())) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("发现新版本");
                    builder.setMessage("你当前安装的版本是" + VersionUtils.getCurrentVersionName(SettingActivity.this) +
                            "手游视界已经发布最新" + update.getVersion_str() + "版本，是否现在升级？");
                    builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(update.getUpdate_url());
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            SettingActivity.this.startActivity(it);
                        }
                    });
                    builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                } else if ("A".equals(update.getUpdate_flag())) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("更新提示");
                    builder.setMessage("手游视界" + update.getVersion_str() + "更新日志：\n" +
                            changelog);
                    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(update.getUpdate_url());
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            SettingActivity.this.startActivity(it);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                } else {
                    Toast.makeText(SettingActivity.this, "当前已经是最新版本", Toast.LENGTH_LONG).show();
                }
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
