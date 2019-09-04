package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.HomeAdapter;
import com.li.videoapplication.Adapter.HomeHotAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.HomeHotEntity;
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
 * 主页侧滑栏 精彩推荐 页面*
 */

public class FavorRecommendActivity extends Activity implements View.OnClickListener, RefreshListView.IXListViewListener {

    private Context context;

    //热门视频适配器
    //没有选择喜欢类型，获取数据成功
    private HomeAdapter homeAdapter;
    private List<VideoEntity> homeList;
    private List<VideoEntity> homeConnectList;

    //偏好视频适配器
    //有选择喜欢类型，获取数据成功
    private HomeHotAdapter homeHotAdapter;
    private List<HomeHotEntity> homeHotList;
    private List<HomeHotEntity> homeHotConnectList;

    private static String state = "";

    private RelativeLayout headRL;
    private TextView head_titleTV;
    private TextView head_moreTV;

    private ImageButton backIB;

    private RefreshListView mListView;

    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_favor_recommend);
        context = FavorRecommendActivity.this;
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);

        initView();

        DialogUtils.createLoadingDialog(context, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new FavorStateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new FavorStateTask().execute();
        }
    }

    private void initView() {
        headRL = (RelativeLayout) findViewById(R.id.favor_recommend_headRL);
        headRL.setOnClickListener(this);
        head_titleTV = (TextView) findViewById(R.id.favor_recommend_head_titleTV);
        head_moreTV = (TextView) findViewById(R.id.favor_recommend_head_moreTV);

        backIB = (ImageButton) findViewById(R.id.favor_recommend_back);
        backIB.setOnClickListener(this);

        mListView = (RefreshListView) findViewById(R.id.favor_recommend_listview);
        mListView.setXListViewListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(false);

        homeList = new ArrayList<VideoEntity>();
        homeConnectList = new ArrayList<VideoEntity>();
        homeAdapter = new HomeAdapter(context, homeList);

        homeHotList = new ArrayList<HomeHotEntity>();
        homeHotConnectList = new ArrayList<HomeHotEntity>();
        homeHotAdapter = new HomeHotAdapter(context, homeHotList);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.favor_recommend_headRL://热门视频 更多
                intent = new Intent(context, MoreRecommendActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("flag", "home");
                startActivity(intent);
                break;

            case R.id.favor_recommend_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new FavorStateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new FavorStateTask().execute();
        }
    }

    @Override
    public void onLoadMore() {

    }


    /**
     * 异步获取10个精彩推荐视频
     * <p/>
     * 没有选择喜欢类型，获取数据成功
     */
    public class RecommendListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            homeConnectList = (List<VideoEntity>) JsonHelper.getFavorVedioList();
            if (homeConnectList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            headRL.setVisibility(View.VISIBLE);
            if (s.equals("s")) {
                mListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                homeList.clear();
                homeList.addAll(homeConnectList);
            } else {
                ToastUtils.showToast(context, "加载失败，请检查网络(没有选择喜欢类型)");
            }
            homeAdapter.notifyDataSetChanged();
            mListView.stopRefresh();
            DialogUtils.cancelLoadingDialog();
        }
    }

    /**
     * 异步获取个人喜好视频
     * <p/>
     * 有选择喜欢类型，获取数据成功
     */
    public class FavorListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            homeHotConnectList = (List<HomeHotEntity>) JsonHelper.getFavorVedioList();
            if (homeHotConnectList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            headRL.setVisibility(View.GONE);
            if (s.equals("s")) {
                mListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                homeHotList.clear();
                homeHotList.addAll(homeHotConnectList);
            } else {
                ToastUtils.showToast(context, "加载失败，请检查网络(有选择喜欢类型)");
            }
            homeHotAdapter.notifyDataSetChanged();
            mListView.stopRefresh();
            DialogUtils.cancelLoadingDialog();
        }
    }

    /**
     * 异步获取个人喜好的状态
     * <p/>
     */
    public class FavorStateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            state = JsonHelper.getFavorState();
            System.out.println("state=======" + state);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (state.equals("yes")) {
                //有选择喜好的游戏类型
                mListView.setAdapter(homeHotAdapter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new FavorListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new FavorListTask().execute();
                }
            } else if (state.equals("no")) {
                //没有选择喜好的游戏类型
                mListView.setAdapter(homeAdapter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new RecommendListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new RecommendListTask().execute();
                }
            }

        }
    }

}
