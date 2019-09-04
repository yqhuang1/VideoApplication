package com.li.videoapplication.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.Adapter.HomeAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.fragment.HotFragment;
import com.li.videoapplication.fragment.TimeFragment;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.Titles;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feimoyuangong on 2015/5/25.
 * 热门视频
 */
public class MoreRecommendActivity extends FragmentActivity implements View.OnClickListener, RefreshListView.IXListViewListener {

    /**
     * 滑动按钮的外围布局
     */
    private RelativeLayout rl_nav;

    /**
     * 水平滑动控件
     */
    private SyncHorizontalScrollView mHsv;

    /**
     * 标题选项
     */
    private RadioGroup rg_nav_content;
    /**
     * 标题滚动下标
     */
    private ImageView iv_nav_indicator;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    private ViewPager mViewPager;

    /**
     * 标题滚动下标长度
     */
    private int indicatorWidth;
    public static String[] tabTitle = Titles.ASSORTTITLE;
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;
    private int position;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private RefreshListView refreshListView;
    private SimpleDateFormat dateFormat = null;
    private HomeAdapter homeAdapter;
    private List<VideoEntity> homeList;
    private List<VideoEntity> connecList;
    private Context context;
    private int pageId;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private ImageButton backBtn;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more_recommend);
        init();
        initView();
        setListener();
    }

    private void init() {
//        ExApplication.MEMBER_ID="5";
        context = getApplicationContext();
        flag = getIntent().getStringExtra("flag");
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        homeList = new ArrayList<VideoEntity>();
        connecList = new ArrayList<VideoEntity>();
        homeAdapter = new HomeAdapter(context, homeList);
    }

    private void initView() {
        refreshListView = (RefreshListView) findViewById(R.id.more_recommend_list);
        backBtn = (ImageButton) findViewById(R.id.more_recommend_back);
        backBtn.setOnClickListener(this);

        rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
        mViewPager = (ViewPager) findViewById(R.id.more_recommend_mViewPager);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / 2;

        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;

        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, MoreRecommendActivity.this);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        fragments.add(TimeFragment.newInstance(flag, "recommend"));
        fragments.add(HotFragment.newInstance(flag, "recommend"));

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                System.out.println("Extra...i: " + i);
            }
        });

//        refreshListView.setAdapter(homeAdapter);
//        refreshListView.setPullLoadEnable(true);
//        refreshListView.setXListViewListener(this);
//        refreshListView.setPullRefreshEnable(true);

//        onRefresh();
    }

    private void initNavigationHSV() {

        rg_nav_content.removeAllViews();

        for (int i = 0; i < tabTitle.length; i++) {

            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            if (i == 0) {
                rb.setTextColor(context.getResources().getColor(R.color.main_title_bg));
            } else {
                rb.setTextColor(context.getResources().getColor(R.color.search_result_default));
            }
            rb.setText(tabTitle[i]);
            rb.setTextSize(15);

            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
        }
    }


    private void setListener() {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton) rg_nav_content.getChildAt(position)).performClick();

                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) findViewById(i);
                        tempButton.setTextColor(context.getResources().getColor(R.color.search_result_default));
                    }
                    RadioButton tempButton = (RadioButton) findViewById(position);
                    tempButton.setTextColor(context.getResources().getColor(R.color.main_title_bg));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        ((RadioButton) rg_nav_content.getChildAt(1)).setChecked(true);
        ((RadioButton) rg_nav_content.getChildAt(0)).setChecked(true);
        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("checkId", checkedId + "");
                if (rg_nav_content.getChildAt(checkedId) != null) {

                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) findViewById(i);
                        tempButton.setTextColor(context.getResources().getColor(R.color.search_result_default));
                    }
                    RadioButton tempButton = (RadioButton) findViewById(checkedId);
                    tempButton.setTextColor(context.getResources().getColor(R.color.main_title_bg));

                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft,
                            ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    //执行位移动画
                    iv_nav_indicator.startAnimation(animation);

                    mViewPager.setCurrentItem(checkedId);    //ViewPager 跟随一起 切换

                    //记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();

                    mHsv.smoothScrollTo(
                            (checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(1)).getLeft(), 0);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMoreRecomVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMoreRecomVideoAsync(pageId + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMoreRecomVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMoreRecomVideoAsync(pageId + "").execute();
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
     * 异步获取更多热门视频
     */
    public class GetMoreRecomVideoAsync extends AsyncTask<Void, Void, String> {

        private String page;

        public GetMoreRecomVideoAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getMoreRecomVedioList(page);
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
                    homeList.clear();
                    homeList.addAll(connecList);
                } else {
                    ToastUtils.showToast(context, "加载失败，请检查网络");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        homeList.addAll(connecList);
                    }

                } else {
                    ToastUtils.showToast(context, "无更多数据");
                }
            }
            homeAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }
}
