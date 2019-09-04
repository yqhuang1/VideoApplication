package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.li.videoapplication.Adapter.MasterColumnAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.MasterEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/6.
 * 首页发现 大神专栏 页面
 */
public class MasterColumnActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshListView.IXListViewListener {

    private ImageButton masterBack;
    private RefreshListView refreshListView;
    private MasterColumnAdapter masterColumnAdapter;
    private List<MasterEntity> masterEntityList;
    private List<MasterEntity> connecList;
    private int pageId;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_master_column);
        init();
    }

    public void init() {
        masterBack = (ImageButton) findViewById(R.id.master_column_back);
        masterBack.setOnClickListener(this);
        refreshListView = (RefreshListView) findViewById(R.id.master_column_rlv);

        dateFormat = new SimpleDateFormat();
        masterEntityList = new ArrayList<MasterEntity>();
        connecList = new ArrayList<MasterEntity>();
        masterColumnAdapter = new MasterColumnAdapter(MasterColumnActivity.this, masterEntityList);
        refreshListView.setAdapter(masterColumnAdapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setOnItemClickListener(this);
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.master_column_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RecomVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new RecomVideoAsync(pageId + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RecomVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new RecomVideoAsync(pageId + "").execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!"".equals(masterEntityList.get(position - 1).getUrl()) && masterEntityList.get(position - 1).getUrl() != null) {
//            Intent intent = new Intent(MasterColumnActivity.this, WebActivity.class);
//            intent.putExtra("url", masterEntityList.get(position-1).getUrl()+"&member_id="+ExApplication.MEMBER_ID);
//            intent.putExtra("nickname", masterEntityList.get(position-1).getNickname());
//            startActivity(intent);
            Intent intent = new Intent(MasterColumnActivity.this, MasterActivity.class);
            intent.putExtra("id", masterEntityList.get(position - 1).getMember_id());
            startActivity(intent);
        }
    }

    /**
     * 异步获取 大神列表
     */
    public class RecomVideoAsync extends AsyncTask<Void, Void, String> {

        String page = "";

        public RecomVideoAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getMasterColumnList(page, ExApplication.MEMBER_ID);
            if (connecList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    refreshListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    masterEntityList.clear();
                    masterEntityList.addAll(connecList);
                } else {
                    ToastUtils.showToast(MasterColumnActivity.this, "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        refreshListView.onHiddenFooterView();
                        ToastUtils.showToast(MasterColumnActivity.this, "已经加载全部数据");
                    } else {
                        masterEntityList.addAll(connecList);
                    }
                } else {
                    ToastUtils.showToast(MasterColumnActivity.this, "已加载全部数据");
                }
            }
            masterColumnAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }
}
