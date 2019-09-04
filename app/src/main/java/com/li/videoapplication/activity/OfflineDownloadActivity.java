package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;

import com.li.videoapplication.Adapter.OLDLAdapter;
import com.li.videoapplication.DB.DBManager;
import com.li.videoapplication.R;
import com.li.videoapplication.entity.DownloadVideo;

import com.umeng.message.PushAgent;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 断点离线下载 页面*
 */
public class OfflineDownloadActivity extends Activity implements View.OnClickListener {
    private Context context;


    private String key = "";
    private String url = "";
    private String name = "";

    private File file;
    private String reallyUrl = "";//最终确定下载的地址
    private String finalPath = "";//文件最终保存的路径
    private String saveDir = "videoapplication/download";//文件最终保存的文件夹
    private String saveName = "";//文件最终保存的名字

    private ImageButton backBtn;
    private ListView listView;
    private OLDLAdapter adapter;
    private List<DownloadVideo> dlVideoList;

    private DBManager dbManager;

    private Handler viewhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dlVideoList = dbManager.getDownloadVideo();
                    Collections.reverse(dlVideoList);//反转下载视频的列表
                    System.out.println("dlVideoList===" + dlVideoList.toString());
                    adapter = new OLDLAdapter(context, dlVideoList);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_offline_download);
        context = OfflineDownloadActivity.this;
        key = getIntent().getStringExtra("qn_key");
        url = getIntent().getStringExtra("qn_url");
        name = getIntent().getStringExtra("name");

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        dbManager = new DBManager(context);
        initView();
        viewhandle.sendEmptyMessage(0);

        reallyUrl = url + "?attname=";
        finalPath = Environment.getExternalStorageDirectory() + File.separator + saveDir;
        saveName = key + ".mp4";

        System.out.println("reallyUrl===" + reallyUrl);
    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.activity_oldl_back);
        backBtn.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.activity_oldl_listview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_oldl_back:
                this.finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewhandle.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
