package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.li.videoapplication.R;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;


/**
 * Created by Liaochanghe on 2015/5/7.
 * 修改密码页面
 */
public class ModifyPsdActivity extends Activity implements View.OnClickListener {
    private ImageButton modifyBack;
    private EditText beginPsdEt, newPsdEt, reNewPsdEt;
    private Button submitBtn;
    private Intent intent;
    private ExApplication exApplication;
    private String member_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modifypsd);
        intent = getIntent();
        initView();
    }

    public void initView() {
        modifyBack = (ImageButton) findViewById(R.id.modify_back);
        modifyBack.setOnClickListener(this);
        beginPsdEt = (EditText) findViewById(R.id.modify_beginning_psd_et);
        newPsdEt = (EditText) findViewById(R.id.modify_new_psd_et);
        reNewPsdEt = (EditText) findViewById(R.id.modify_renew_psd_et);
        submitBtn = (Button) findViewById(R.id.modify_submit_btn);
        submitBtn.setOnClickListener(this);
        if (!intent.getStringExtra("activity_name").equals("PersonalInfoActivity")) {
            beginPsdEt.setVisibility(View.GONE);
            member_id = intent.getStringExtra("id");
        } else {
            exApplication = new ExApplication(getApplicationContext());
            member_id = exApplication.MEMBER_ID;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_back:
                finish();
                break;
            case R.id.modify_submit_btn:
                if (intent.getStringExtra("activity_name").equals("PersonalInfoActivity")) {
                    if (beginPsdEt.getText().toString().equals("")) {
                        ToastUtils.showToast(getApplicationContext(), "原密码不能为空");
                        return;
                    }
                }
                if (newPsdEt.getText().toString().equals("")) {
                    ToastUtils.showToast(getApplicationContext(), "新密码不能为空");
                    return;
                }
                if (reNewPsdEt.getText().toString().equals("")) {
                    ToastUtils.showToast(getApplicationContext(), "确认密码不能为空");
                    return;
                }
                if (!newPsdEt.getText().toString().equals(reNewPsdEt.getText().toString())) {
                    ToastUtils.showToast(getApplicationContext(), "两次输入密码不一致");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ModifyPsdTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ModifyPsdTask().execute();
                }
                break;
        }
    }

    private class ModifyPsdTask extends AsyncTask<Void, Void, String> {
        boolean result;

        @Override
        protected String doInBackground(Void... params) {
            if (intent.getStringExtra("activity_name").equals("PersonalInfoActivity")) {
                result = JsonHelper.directModifyPsd(member_id, beginPsdEt.getText().toString(), newPsdEt.getText().toString());
            } else {
                result = JsonHelper.verifyModifyPsd(member_id, newPsdEt.getText().toString());
            }
            if (result) {
                return "s";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ("s".equals(s)) {
                ToastUtils.showToast(getApplicationContext(), "密码修改成功！");
                Intent intent1 = new Intent(getApplicationContext(), PersonalInfoActivity.class);
                startActivity(intent1);
                finish();
            } else {
                ToastUtils.showToast(getApplicationContext(), "密码修改失败！");
            }
        }
    }
}
