package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.li.videoapplication.Adapter.ShowPointAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by feimoyuangong on 2015/5/8.
 * 首页 小编荐 页面
 */
public class ShowPointActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener,RefreshListView.IXListViewListener{

    private RefreshListView refreshListView;
    private int pageId;
    private ShowPointAdapter showAdapter;
    private List<VideoEntity> showList;
    private List<VideoEntity> reponseList;
    private List<VideoEntity> connecList;
    private Context context;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private static boolean isFirst = true;
    private SimpleDateFormat dateFormat = null;
    private ImageButton baceBtn,recordBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_showpoint);
        if (isFirst) {
            DialogUtils.createLoadingDialog(ShowPointActivity.this,"");
            isFirst = false;
        }
        init();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RefreshTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new RefreshTokenTask().execute();
        }
    }

    private void init() {
        context = ShowPointActivity.this;
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        showList = new ArrayList<VideoEntity>();
        reponseList = new ArrayList<VideoEntity>();
        connecList = new ArrayList<VideoEntity>();
        showAdapter = new ShowPointAdapter(context, showList);
    }

    public void initView() {
        refreshListView = (RefreshListView) findViewById(R.id.show_point_list);
        baceBtn=(ImageButton)findViewById(R.id.show_point_back);
        baceBtn.setOnClickListener(this);
        recordBtn=(ImageButton)findViewById(R.id.show_point_record);
        recordBtn.setOnClickListener(this);

        refreshListView.setAdapter(showAdapter);
        refreshListView.setOnItemClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show_point_back:
                finish();
                break;
            case R.id.show_point_record:
                Intent intent = new Intent();
                intent.setClassName(this, "com.fmscreenrecord.activity.FMMainActivity");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,VideoPlayActivity.class);
        intent.putExtra("id",showList.get(position-1).getId());
        startActivity(intent);
    }

    /**
     * 异步获取
     */
    public class RecomVideoAsync extends AsyncTask<Void, Void, String> {

        String page = "";

        public RecomVideoAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getShowPointList(context, page);
            if (connecList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DialogUtils.cancelLoadingDialog();
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    refreshListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    showList.clear();
                    showList.addAll(connecList);
                } else {
                    ToastUtils.showToast(context, "没有更多数据");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        showList.addAll(connecList);
                    }

                } else {
                    ToastUtils.showToast(context, "没有更多数据");
                }
            }
            showAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }

    public class RefreshTokenTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {

            return JsonHelper.refreshToken(context);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Log.e("---------", "refresh");
            }
            super.onPostExecute(aBoolean);
        }
    }
}
