package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.li.videoapplication.DB.DBManager;
import com.li.videoapplication.R;
import com.li.videoapplication.utils.ButtonUtils;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.ToastUtils;
import com.umeng.message.PushAgent;

/**
 * Created by feimoyuangong on 2014/12/29.
 * 功能设置 页面
 */
public class FunctionSettingActivity extends Activity implements View.OnClickListener {

    private Context context;
    private ImageView gprsIv, messageIv;
    private TextView clearTv;
    private ImageButton functionSettingIb;
    private Button clearBtn;
    private DBManager dbManager;
    private ExApplication exApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_setting);
        context = FunctionSettingActivity.this;
        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        initView();
    }

    public void initView() {
        dbManager = new DBManager(this);
        exApplication = new ExApplication(this);
        gprsIv = (ImageView) findViewById(R.id.function_setting_gprs_iv);
        gprsIv.setOnClickListener(this);
        if ("open".equals(SharePreferenceUtil.getPreference(context, "isGprsOpen"))) {
            gprsIv.setBackgroundResource(R.drawable.icon_switcher_open);
        } else {
            gprsIv.setBackgroundResource(R.drawable.icon_switcher_close);
        }
        messageIv = (ImageView) findViewById(R.id.function_setting_message_iv);
        messageIv.setOnClickListener(this);
        if ("open".equals(SharePreferenceUtil.getPreference(context, "isMessageOpen"))) {
            messageIv.setBackgroundResource(R.drawable.icon_switcher_open);
        } else {
            messageIv.setBackgroundResource(R.drawable.icon_switcher_close);
        }
        clearBtn = (Button) findViewById(R.id.function_setting_clear_btn);
        clearBtn.setOnClickListener(this);
        ButtonUtils.buttonEffect(clearBtn, R.drawable.get2, R.drawable.get);
        functionSettingIb = (ImageButton) findViewById(R.id.function_setting_back);
        functionSettingIb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.function_setting_gprs_iv:
                if ("open".equals(SharePreferenceUtil.getPreference(context, "isGprsOpen"))) {
                    gprsIv.setBackgroundResource(R.drawable.icon_switcher_close);
                    SharePreferenceUtil.setPreference(context, "isGprsOpen", "close");
                } else {
                    gprsIv.setBackgroundResource(R.drawable.icon_switcher_open);
                    SharePreferenceUtil.setPreference(context, "isGprsOpen", "open");
                }
                break;
            case R.id.function_setting_message_iv://开闭(推送)消息通知
                if ("open".equals(SharePreferenceUtil.getPreference(context, "isMessageOpen"))) {
                    messageIv.setBackgroundResource(R.drawable.icon_switcher_close);
                    SharePreferenceUtil.setPreference(context, "isMessageOpen", "close");
                    /**友盟 关闭客户端的通知服务**/
                    PushAgent.getInstance(context).disable();
                } else {
                    messageIv.setBackgroundResource(R.drawable.icon_switcher_open);
                    SharePreferenceUtil.setPreference(context, "isMessageOpen", "open");
                    /**友盟 开启客户端的通知服务**/
                    PushAgent.getInstance(context).enable();
                }
                break;
            case R.id.function_setting_clear_btn:
                Dialog dialog = new AlertDialog.Builder(FunctionSettingActivity.this)
                        .setTitle("温馨提示")
                        .setMessage("是否清除缓存？")
                        .setIcon(getResources().getDrawable(R.drawable.tubiao_top))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbManager.delectAllVideo();
                                exApplication.clear();
                                ToastUtils.showToast(FunctionSettingActivity.this, "清除缓存成功");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.function_setting_back:
                FunctionSettingActivity.this.finish();
                break;
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
