package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.li.videoapplication.Adapter.AttentionAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.ExpertEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feimoyuangong on 2015/6/29.
 * 玩家 个人资料 查看（个人）粉丝页面
 */
public class MyFansActivity extends Activity implements View.OnClickListener, RefreshListView.IXListViewListener, AdapterView.OnItemClickListener {

    private Context context;
    private ImageButton backBtn;
    private TextView title;
    private RefreshListView refreshListView;
    private List<ExpertEntity> list;
    private List<ExpertEntity> responseList;
    private AttentionAdapter adapter;
    private int asyncType = 0;
    private int pageId;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_attention_fans);
        init();
        initView();
    }

    private void init() {
        context = this;
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        list = new ArrayList<ExpertEntity>();
        responseList = new ArrayList<ExpertEntity>();
        adapter = new AttentionAdapter(context, list, "fans");
    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.activity_attention_fans_back);
        backBtn.setOnClickListener(this);
        title = (TextView) findViewById(R.id.activity_attention_fans_title);
        title.setText(getIntent().getStringExtra("title"));

        refreshListView = (RefreshListView) findViewById(R.id.activity_attention_fans_rlv);
        refreshListView.setAdapter(adapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setOnItemClickListener(this);
        onRefresh();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_attention_fans_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetFansTask(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetFansTask(pageId + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetFansTask(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetFansTask(pageId + "").execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MyFansActivity.this, PersonalInfoActivity.class);
        intent.putExtra("flag", "videoplay");
        intent.putExtra("member_id", list.get(position - 1).getMember_id());
        startActivity(intent);
    }

    /**
     * 异步获取
     */
    private class GetFansTask extends AsyncTask<Void, Void, String> {

        String page = "";

        public GetFansTask(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            responseList = JsonHelper.getMyFansList(getIntent().getStringExtra("member_id"), page);
            if (responseList != null) {
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
                    list.clear();
                    if (responseList.size() == 0) {
//                        ToastUtils.showToast(getActivity(),"没有找到相关数据");
                        refreshListView.setFooterText(1);
                    } else {
                        refreshListView.setFooterText(0);
                        list.addAll(responseList);
                    }
                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (responseList.size() == 0) {
                        refreshListView.setPullLoadEnable(false);
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        refreshListView.setFooterText(0);
                        list.addAll(responseList);
                    }

                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            }
            adapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();

        }
    }
}
