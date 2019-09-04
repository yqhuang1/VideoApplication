package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.RecordAdapter;
import com.li.videoapplication.DB.DBManager;
import com.li.videoapplication.R;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.RecordCheckUtils;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 左侧栏 观看记录 页面*
 */
public class RecordActivity extends Activity implements View.OnClickListener, RecordAdapter.OnCheckListener {
    private Context context;
    private ImageButton backBtn;
    private GridView videoGv;
    private List<VideoEntity> list;
    private RecordAdapter adapter;
    private DBManager dbManager;
    //    private TextView delectTv;
    private TextView titleTv;
    private ImageView delectTv;
    private RelativeLayout bottomLayout;
    private Button allSelectBtn, delectBtn;
    private RelativeLayout recordRl;
    private TextView cancelTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        context = RecordActivity.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        initView();
    }

    private void initView() {
        list = new ArrayList<VideoEntity>();
        dbManager = new DBManager(this);
        list = dbManager.getRecordVideo();
        backBtn = (ImageButton) findViewById(R.id.record_back);
        backBtn.setOnClickListener(this);
        videoGv = (GridView) findViewById(R.id.record_gv);

        delectTv = (ImageView) findViewById(R.id.record_delect_img);
//        delectTv.setTypeface(TextTypeUtils.getTypeface(this));
        delectTv.setOnClickListener(this);

        bottomLayout = (RelativeLayout) findViewById(R.id.record_bottom);
        delectBtn = (Button) findViewById(R.id.record_delect_btn);
        delectBtn.setOnClickListener(this);

        allSelectBtn = (Button) findViewById(R.id.record_all_select_btn);
        allSelectBtn.setOnClickListener(this);

//        delectTv=(TextView)findViewById(R.id.record_delect);
//        delectTv.setTypeface(TextTypeUtils.getTypeface(this));
//        delectTv.setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.record_title);
        titleTv.setText("观看记录");
        cancelTv = (TextView) findViewById(R.id.record_cancel_tv);
        cancelTv.setOnClickListener(this);
        recordRl = (RelativeLayout) findViewById(R.id.record_title_rl);

        adapter = new RecordAdapter(this, list, this, titleTv);
        videoGv.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_back:
                this.finish();
                break;
            case R.id.record_delect_img:
                RecordCheckUtils.isCheckState = true;
                delectTv.setVisibility(View.GONE);
                cancelTv.setVisibility(View.VISIBLE);
                recordRl.setBackgroundColor(Color.parseColor("#ffffff"));
                setTitleTv("已选中 0 项");
                titleTv.setTextColor(Color.parseColor("#6c6c6c"));
                bottomLayout.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
//                Dialog dialog = new AlertDialog.Builder(DownLoadActivity.this)
//                        .setTitle("温馨提示")
//                        .setMessage("是否删除选中视频")
//                        .setIcon(getResources().getDrawable(R.drawable.icon))
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
////                                dbManager.delectDownloadVideo(list.get(position).getPlayUrl());
////                                list=dbManager.getDownloadVideo();
////                                adapter=new DownloadVideoAdapter(DownLoadActivity.this,list);
////                                videoGv.setAdapter(adapter);
//
//                                for (int j=0;j<RecordCheckUtils.getProductList().size();j++){
//                                    list.remove(RecordCheckUtils.getProductList().get(j));
//                                }
//                                dbManager.delectDownloadVideo(RecordCheckUtils.getProductList());
//
//
//                                adapter.notifyDataSetChanged();
//
//                                RecordCheckUtils.clearAllCollectProduce();
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .create();
//                dialog.show();
                break;
            case R.id.record_delect_btn://删除
                for (int j = 0; j < RecordCheckUtils.getProductList().size(); j++) {
                    list.remove(RecordCheckUtils.getProductList().get(j));
                }
                dbManager.delRecordVideo(RecordCheckUtils.getProductList());
                adapter.notifyDataSetChanged();
                RecordCheckUtils.clearAllCollectProduce();
                setTitleTv("已选中 0 项");
                break;
            case R.id.record_all_select_btn://全选
                RecordCheckUtils.clearAllCollectProduce();
                for (int j = 0; j < list.size(); j++) {
                    list.get(j).setIsCheck("true");
                    RecordCheckUtils.addCollectProduct(list.get(j));
                }
                setTitleTv("已选中 " + list.size() + " 项");
                adapter.notifyDataSetChanged();
                break;
            case R.id.record_cancel_tv://取消
                bottomLayout.setVisibility(View.GONE);
                delectTv.setVisibility(View.VISIBLE);
                cancelTv.setVisibility(View.GONE);
                titleTv.setText("观看记录");
                titleTv.setTextColor(Color.parseColor("#000000"));
                recordRl.setBackgroundColor(Color.parseColor("#ffffff"));

                RecordCheckUtils.clearAllCollectProduce();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setIsCheck("false");
                }
                RecordCheckUtils.isCheckState = false;
                adapter.notifyDataSetChanged();
                break;
        }
    }

    public void setTitleTv(String str) {
        int fstart = str.length() - 4;
        int fend = str.length() - 2;
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.RED), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        titleTv.setText(style);
    }

    @Override
    public void onCheck() {
        delectTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordCheckUtils.isCheckState = false;
        RecordCheckUtils.clearAllCollectProduce();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (RecordCheckUtils.getProductList().size() == 0 && delectTv.getVisibility() == View.VISIBLE) {
                RecordCheckUtils.isCheckState = false;
                RecordCheckUtils.clearAllCollectProduce();
                delectTv.setVisibility(View.GONE);
                return false;
            } else if (delectTv.getVisibility() == View.GONE) {
                return super.onKeyDown(keyCode, event);
            } else {
                Dialog dialog = new AlertDialog.Builder(RecordActivity.this)
                        .setTitle("温馨提示")
                        .setMessage("是否取消选中")
                        .setIcon(getResources().getDrawable(R.drawable.icon))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dbManager.delectDownloadVideo(list.get(position).getPlayUrl());
//                                list=dbManager.getDownloadVideo();
//                                adapter=new DownloadVideoAdapter(DownLoadActivity.this,list);
//                                videoGv.setAdapter(adapter);
                                RecordCheckUtils.clearAllCollectProduce();
                                RecordCheckUtils.isCheckState = false;
                                adapter.notifyDataSetChanged();
                                delectTv.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
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
