package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.li.videoapplication.Adapter.RelaxeAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feimoyuangong on 2015/5/25.
 * 轻松一刻 页面
 */
public class RelaxeActivity extends Activity implements View.OnClickListener, RefreshListView.IXListViewListener {

    private RefreshListView refreshListView;
    private SimpleDateFormat dateFormat = null;
    private RelaxeAdapter relaxeAdapter;
    private List<VideoEntity> relaxeList;
    private List<VideoEntity> connecList;
    private Context context;
    private int pageId;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relaxe);
        init();
        initView();
    }

    private void init() {
//        ExApplication.MEMBER_ID="5";
        context = getApplicationContext();
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        relaxeList = new ArrayList<VideoEntity>();
        connecList = new ArrayList<VideoEntity>();
        relaxeAdapter = new RelaxeAdapter(context, relaxeList);
    }

    private void initView() {
        refreshListView = (RefreshListView) findViewById(R.id.more_recommend_list);
        backBtn = (ImageButton) findViewById(R.id.more_recommend_back);
        backBtn.setOnClickListener(this);

        refreshListView.setAdapter(relaxeAdapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetRelaxeVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetRelaxeVideoAsync(pageId + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetRelaxeVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetRelaxeVideoAsync(pageId + "").execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_recommend_back:
                finish();
                break;
        }
    }

    /**
     * 异步视频列表
     */
    public class GetRelaxeVideoAsync extends AsyncTask<Void, Void, String> {

        private String page;

        public GetRelaxeVideoAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getRelaxeVedioList(page);
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
                    relaxeList.clear();
                    relaxeList.addAll(connecList);
                } else {
                    ToastUtils.showToast(context, "加载失败，请检查网络");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        relaxeList.addAll(connecList);
                    }

                } else {
                    ToastUtils.showToast(context, "无更多数据");
                }
            }
            relaxeAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }
}
