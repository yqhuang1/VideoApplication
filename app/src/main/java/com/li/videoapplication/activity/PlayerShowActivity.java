package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.li.videoapplication.Adapter.AdPlayerShowAdapter;
import com.li.videoapplication.Adapter.PlayerShowAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.CircleFlowIndicator;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.View.ViewFlow;
import com.li.videoapplication.entity.BannerEntity;
import com.li.videoapplication.entity.PlayerShowEntity;
import com.li.videoapplication.entity.RecommendEntity;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.HttpUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feimoyuangong on 2015/5/8.
 * 首页 玩家秀 页面
 */
public class PlayerShowActivity extends Activity implements View.OnClickListener, RefreshListView.IXListViewListener {
    private View view;
    private RefreshListView refreshListView;
    private PlayerShowAdapter playerShowAdapter;
    private List<PlayerShowEntity> playerList;
    private List<PlayerShowEntity> connecList;
    private Context context;
    private int pageId;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private static boolean isFirst = true;
    private SimpleDateFormat dateFormat = null;

    private ViewFlow viewFlow;
    private int count = 3;
    private CircleFlowIndicator indic;
    private AdPlayerShowAdapter adPlayerAdapter;
    private List<RecommendEntity> adList;
    private LayoutInflater inflater;
    private LinearLayout btnLl;
    private ImageButton baceBtn, searchBtn;
    private FrameLayout headFrame;

    private ExApplication exApplication;
    private List<BannerEntity> bannerList;
    //banner广告栏
    private RelativeLayout bannerRl;
    private ImageView banner;
    private CheckBox bannerCb;

    //搜索
    private AutoCompleteTextView autoCompleteTextView = null;
    private ArrayAdapter<String> keyAdapter = null;
    private List<String> suggest;
    private static String[] word = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_playershow);
        if (isFirst) {
            DialogUtils.createLoadingDialog(PlayerShowActivity.this, "");
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
        context = PlayerShowActivity.this;
        exApplication = new ExApplication(context);
        bannerList = new ArrayList<BannerEntity>();
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        playerList = new ArrayList<PlayerShowEntity>();
        connecList = new ArrayList<PlayerShowEntity>();
        adList = new ArrayList<RecommendEntity>();
        playerShowAdapter = new PlayerShowAdapter(context, playerList);
    }

    public void initView() {
        inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        refreshListView = (RefreshListView) findViewById(R.id.player_list);
        baceBtn = (ImageButton) findViewById(R.id.player_show_back);
        baceBtn.setOnClickListener(this);
        searchBtn = (ImageButton) findViewById(R.id.player_show_search);
        searchBtn.setOnClickListener(this);
        View view2 = inflater.inflate(R.layout.player_show_head, null);
        headFrame = (FrameLayout) view2.findViewById(R.id.framelayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width / 2);
        headFrame.setLayoutParams(layoutParams);
        viewFlow = (ViewFlow) view2.findViewById(R.id.viewflow);
        indic = (CircleFlowIndicator) view2.findViewById(R.id.viewflowindic);
        viewFlow.setAdapter(adPlayerAdapter);
        viewFlow.setmSideBuffer(4); // 实际图片张数
        viewFlow.setFlowIndicator(indic);
        viewFlow.setTimeSpan(4000);
        viewFlow.setSelection(1 * 1000);    //设置初始位置
        viewFlow.startAutoFlowTimer();  //启动自动播放

        bannerRl = (RelativeLayout) view2.findViewById(R.id.play_show_banner_layout);
        banner = (ImageView) view2.findViewById(R.id.play_show_banner);
        bannerCb = (CheckBox) view2.findViewById(R.id.play_show_banner_checkbox);
        bannerCb.setOnClickListener(this);

        refreshListView.addHeaderView(view2);

        refreshListView.setAdapter(playerShowAdapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AdTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new AdTask().execute();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new BannerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new BannerTask().execute();
        }
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
        Intent intent;
        switch (v.getId()) {
            case R.id.player_show_back:
                finish();
                break;
            case R.id.player_show_search:
                intent = new Intent(PlayerShowActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.play_show_banner_checkbox:
                bannerRl.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 异步获取 玩家秀 列表
     */
    public class RecomVideoAsync extends AsyncTask<Void, Void, String> {

        String page = "";

        public RecomVideoAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getPlayerShowList(page);
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
                    playerList.clear();
                    playerList.addAll(connecList);
                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        playerList.addAll(connecList);
                    }
                } else {
                    ToastUtils.showToast(context, "已加载全部数据");
                }
            }
            playerShowAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }

    //异步获取 顶部滚动广告栏
    private class AdTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            adList = JsonHelper.getAdPlayerShowList();
            if (adList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(context, "连接服务器失败");
                return;
            }

            if (s.equals("s")) {
                adPlayerAdapter = new AdPlayerShowAdapter(context, adList);
                viewFlow.setAdapter(adPlayerAdapter);
            }
        }
    }

    //异步获取 Banner广告栏
    private class BannerTask extends AsyncTask<Void, Void, String> {
        private String situation = "game_show_ad";

        @Override
        protected String doInBackground(Void... params) {
            bannerList = JsonHelper.getBannerAd(situation);
            if (bannerList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(context, "连接服务器失败");
                return;
            }

            if (s.equals("s")) {
                bannerRl.setVisibility(View.VISIBLE);
                String flagPath = bannerList.get(0).getFlagPath();
                exApplication.imageLoader.displayImage(flagPath, banner, exApplication.getOptions());
            }
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

    /**
     * 获取搜索联想词
     */
    class getSuggestWord extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            keyAdapter = new ArrayAdapter<String>(PlayerShowActivity.this, R.layout.suggest_item, R.id.suggest_text, suggest);
            autoCompleteTextView.setAdapter(keyAdapter);
            keyAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... key) {
            try {
                word = HttpUtils.httpGet(HttpUtils.getSearchKeyWord(), autoCompleteTextView.getText().toString());
                suggest = new ArrayList<String>();
                for (int i = 0; i < word.length; i++) {
                    suggest.add(word[i]);
                    System.out.println(suggest.get(i));
                }

            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            }
            return null;
        }

    }

}
