package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.GiftEntity;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.umeng.message.PushAgent;

/**
 * 礼包详情 页面*
 * 主页礼包 领取礼包后进入
 */
public class GiftAtuoDetailActivity extends Activity implements View.OnClickListener {

    private Context context;
    private ImageView headImg;
    private ImageButton backBtn, persionBtn;
    private TextView titleTv, endTimeTv, countTv, changTv, contentTv;
    private ProgressBar progressBar;
    private ExApplication exApplication;
    private TextView codeTitleTv, codeTv;
    private LinearLayout codeLayoutTv;
    private String id;
    private Button copy, getCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gift_detail);
        context = GiftAtuoDetailActivity.this;
        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        exApplication = new ExApplication(this);
        initView();

        if (this.getIntent().getExtras() != null) {
            id = this.getIntent().getExtras().getString("id");
            DialogUtils.createLoadingDialog(GiftAtuoDetailActivity.this, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetDetailTask().execute();
            }
//            GiftEntity giftEntity=(GiftEntity)this.getIntent().getExtras().get("gift");
////            ExApplication.imageLoader.displayImage(giftEntity.getImgPath(),headImg,
////                    ExApplication.getOptions());
//            exApplication.imageLoader.displayImage(giftEntity.getImgPath(),headImg,exApplication.getOptions());
//            titleTv.setText(giftEntity.getTitle());
//            endTimeTv.setText("截止到"+giftEntity.getEndtime()+"结束");
//            countTv.setText(giftEntity.getCount()+"个");
//            changTv.setText(giftEntity.getTrade_type());
//            contentTv.setText(giftEntity.getContent());
//
//            if (!giftEntity.getActivity_code().equals("")){
//                codeTv.setText(giftEntity.getActivity_code());
//                codeTitleTv.setVisibility(View.VISIBLE);
//                codeLayoutTv.setVisibility(View.VISIBLE);
//            }
        }
    }

    private void initView() {
//        Log.e("member_id",ExApplication.MEMBER_ID);
        headImg = (ImageView) findViewById(R.id.gift_detail_img);
        backBtn = (ImageButton) findViewById(R.id.gift_detail_back);
        backBtn.setOnClickListener(this);
//        persionBtn = (ImageButton) findViewById(R.id.gift_detail_record);
//        persionBtn.setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.gift_detail_title);
        endTimeTv = (TextView) findViewById(R.id.gift_detail_endtime);
        countTv = (TextView) findViewById(R.id.gift_detail_count);
        changTv = (TextView) findViewById(R.id.gift_detail_change);
        contentTv = (TextView) findViewById(R.id.gift_detail_content);

        codeTitleTv = (TextView) findViewById(R.id.gift_code_title);
        codeTv = (TextView) findViewById(R.id.gift_detail_code);
        codeLayoutTv = (LinearLayout) findViewById(R.id.gift_code_layout);

        progressBar = (ProgressBar) findViewById(R.id.gift_detail_pb);
        copy = (Button) findViewById(R.id.gift_detail_copy);
        copy.setOnClickListener(this);
        getCode = (Button) findViewById(R.id.gift_detail_getcode_btn);
        getCode.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetDetailTask().execute();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gift_detail_back:
                this.finish();
                break;
//            case R.id.gift_detail_record:
//                Intent intent = new Intent();
//                intent.setClassName(this, "com.fmscreenrecord.activity.FMMainActivity");
//                startActivity(intent);
//                break;
            case R.id.gift_detail_copy:
                ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cmb.setText(codeTv.getText().toString());
                ToastUtils.showToast(this, "已复制到剪贴板");
                break;
            case R.id.gift_detail_getcode_btn:
                if (!"".equals(ExApplication.MEMBER_ID)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new GetGiftTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new GetGiftTask().execute();
                    }
                } else {
                    ToastUtils.showToast(getApplicationContext(), "请先登录！");
                }
                break;
        }
    }

    private GiftEntity giftEntity;

    /**
     * 自动获取礼包详情 异步方法*
     */
    private class GetDetailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            giftEntity = JsonHelper.getAutoGift(id, ExApplication.MEMBER_ID);
            if (giftEntity != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DialogUtils.cancelLoadingDialog();
            if (s.equals("")) {
                ToastUtils.showToast(GiftAtuoDetailActivity.this, "获取数据失败");
                return;
            }
            if (s.equals("s")) {
                exApplication.imageLoader.displayImage(giftEntity.getImgPath(), headImg, exApplication.getOptions());
                titleTv.setText(giftEntity.getTitle());
                endTimeTv.setText("领取期限:" + giftEntity.getStarttime() + "至" + giftEntity.getEndtime());
                int max = Integer.parseInt(giftEntity.getNum());
                int progress = Integer.parseInt(giftEntity.getCount());
                countTv.setText((int) (((float) progress / max) * 100) + "%");
                progressBar.setMax(max);
                progressBar.setProgress(progress);
                changTv.setText(giftEntity.getTrade_type());
                contentTv.setText(giftEntity.getContent());

                if (!giftEntity.getActivity_code().equals("")) {
                    codeTv.setText(giftEntity.getActivity_code());
                    codeTitleTv.setVisibility(View.VISIBLE);
                    codeLayoutTv.setVisibility(View.VISIBLE);
                    getCode.setVisibility(View.GONE);
                } else {
                    getCode.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 领取礼包
     */
    private class GetGiftTask extends AsyncTask<Void, Void, Boolean> {

        public GetGiftTask() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.getGiftResponse(ExApplication.MEMBER_ID, giftEntity.getId());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                ToastUtils.showToast(getApplicationContext(), "领取成功");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetDetailTask().execute();
                }
                codeTitleTv.setVisibility(View.VISIBLE);
                codeLayoutTv.setVisibility(View.VISIBLE);
                getCode.setVisibility(View.GONE);
            } else {
                ToastUtils.showToast(getApplicationContext(), "领取失败");
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
