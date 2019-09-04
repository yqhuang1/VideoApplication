package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.li.videoapplication.utils.ButtonUtils;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.umeng.message.PushAgent;

/**
 * 礼包详情 页面*
 */
public class GiftDetailActivity extends Activity implements View.OnClickListener {

    private Context context;
    private ImageView headImg;
    private ImageButton backBtn, recordBtn;
    private TextView titleTv, endTimeTv, countTv, changTv, contentTv;
    private ExApplication exApplication;
    private TextView codeTitleTv, codeTv;
    private ProgressBar progressBar;
    private LinearLayout codeLayoutTv;
    private Button copy, getCode;
    private GiftEntity giftEntity;
    private int position;
    private String id;

    private Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://刷新显示游戏礼包激活码
                    ToastUtils.showToast(context, "领取成功");
                    codeLayoutTv.setVisibility(View.VISIBLE);
                    codeTv.setText(giftEntity.getActivity_code());
                    codeTitleTv.setVisibility(View.VISIBLE);
                    getCode.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gift_detail);
        context = GiftDetailActivity.this;
        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();
        exApplication = new ExApplication(this);
        initView();

        if (this.getIntent().getExtras() != null) {
            giftEntity = (GiftEntity) this.getIntent().getExtras().get("gift");
            position = this.getIntent().getIntExtra("position", 0);
//            ExApplication.imageLoader.displayImage(giftEntity.getImgPath(),headImg,
//                    ExApplication.getOptions());
            exApplication.imageLoader.displayImage(giftEntity.getImgPath(), headImg, exApplication.getOptions());
            titleTv.setText(giftEntity.getTitle());
            endTimeTv.setText("领取期限：" + giftEntity.getStarttime() + "至" + giftEntity.getEndtime());
            int max = Integer.parseInt(giftEntity.getNum());
            int progress = Integer.parseInt(giftEntity.getCount());
            countTv.setText((int) (((float) progress / max) * 100) + "%");
            progressBar.setMax(max);
            progressBar.setProgress(progress);
//            countTv.setText(giftEntity.getCount() + "个");
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

    private void initView() {
//        Log.e("member_id",ExApplication.MEMBER_ID);
        headImg = (ImageView) findViewById(R.id.gift_detail_img);
        backBtn = (ImageButton) findViewById(R.id.gift_detail_back);
        backBtn.setOnClickListener(this);
//        recordBtn = (ImageButton) findViewById(R.id.gift_detail_record);
//        recordBtn.setOnClickListener(this);
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
        ButtonUtils.buttonEffect(copy, R.drawable.install2, R.drawable.install);
        getCode = (Button) findViewById(R.id.gift_detail_getcode_btn);
        getCode.setOnClickListener(this);
//        ButtonUtils.buttonEffect(getCode, R.drawable.get2, R.drawable.get);
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
                if (ExApplication.MEMBER_ID.equals("")) {
                    ToastUtils.showMidToast(context, "请先登录");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetGiftTask(getApplicationContext(), giftEntity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetGiftTask(getApplicationContext(), giftEntity).execute();
                }
                break;
        }
    }

    /**
     * 自动获取礼包详情 异步方法
     * 主要用来刷新显示游戏礼包激活码
     */
    private class GetDetailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            giftEntity = JsonHelper.getAutoGift(giftEntity.getId(), ExApplication.MEMBER_ID);
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
                ToastUtils.showToast(GiftDetailActivity.this, "获取数据失败");
                return;
            }
            if (s.equals("s")) {
                viewHandler.sendEmptyMessage(0);
            }
        }
    }

    /**
     * 领取礼包 异步方法
     */
    private class GetGiftTask extends AsyncTask<Void, Void, Boolean> {
        GiftEntity giftEntity;
        Context context;

        public GetGiftTask(Context context, GiftEntity giftEntity) {
            this.giftEntity = giftEntity;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.getGiftResponse(ExApplication.MEMBER_ID, giftEntity.getId());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetDetailTask().execute();
                }
            } else {
                ToastUtils.showToast(context, "领取失败");
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
