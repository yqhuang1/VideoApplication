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
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.entity.Game;
import com.li.videoapplication.fragment.GameCircleCommentFragment;
import com.li.videoapplication.fragment.GameCircleUserFragment;
import com.li.videoapplication.fragment.GameCircleVideoFragment;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.Titles;
import com.li.videoapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/7/4.
 * 游戏圈子 圈子详情 页面
 */
public class GameCircleDetailActivity extends FragmentActivity implements View.OnClickListener {

    private Context context;
    private String group_id;
    private String type_name;
    private Game game;
    private ExApplication exApplication;
    //头部布局
    private ImageButton backBtn;
    private TextView titleTv, gameTypeTv, focusTv, videoCountTv, descriptionTv, joinTv;
    private ImageView gameIcon, descriptionMore;
    private RelativeLayout descriptionLayout;
    private static final int VIDEO_CONTENT_DESC_MAX_LINE = 1;// 默认展示最大行数1行
    private static final int SHOW_CONTENT_NONE_STATE = 0;// 扩充
    private static final int SHRINK_UP_STATE = 1;// 收起状态
    private static final int SPREAD_STATE = 2;// 展开状态
    private static int mState = SHRINK_UP_STATE;//默认收起状态
    //viewpage布局
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
    public static String[] tabTitle = Titles.GameCircleDetail;
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;
    private int position;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_circle_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        context = GameCircleDetailActivity.this;
        init();
        initView();
        setListener();
    }

    private void init() {
        group_id = getIntent().getStringExtra("group_id");
        type_name = getIntent().getStringExtra("type_name");
        exApplication = new ExApplication(context);
    }

    private void initView() {
        //头部
        backBtn = (ImageButton) findViewById(R.id.game_circle_detail_back);
        backBtn.setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.game_circle_detail_title);
        gameTypeTv = (TextView) findViewById(R.id.game_circle_detail_type);
        focusTv = (TextView) findViewById(R.id.game_circle_detail_focus);
        videoCountTv = (TextView) findViewById(R.id.game_circle_detail_video_count);
        descriptionTv = (TextView) findViewById(R.id.game_circle_detail_description);
        joinTv = (TextView) findViewById(R.id.game_circle_game_join);
        joinTv.setOnClickListener(this);
        gameIcon = (ImageView) findViewById(R.id.game_circle_detail_img);
        descriptionMore = (ImageView) findViewById(R.id.game_circle_detail_description_more);
        descriptionLayout = (RelativeLayout) findViewById(R.id.game_circle_detail_description_layout);
        descriptionLayout.setOnClickListener(this);

        //viewpage
        rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
        mViewPager = (ViewPager) findViewById(R.id.game_circle_detail_mViewPager);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / 3;

        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;

        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, GameCircleDetailActivity.this);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

//        fragments.add(HotFragment.newInstance(type_id, ""));
        fragments.add(GameCircleVideoFragment.newInstance(group_id, ""));
        fragments.add(GameCircleUserFragment.newInstance(group_id, ""));
        fragments.add(GameCircleCommentFragment.newInstance(group_id, ""));
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                System.out.println("Extra...i: " + i);
            }
        });

        DialogUtils.createLoadingDialog(context, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameInfoTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_circle_detail_back:
                finish();
                break;
            case R.id.game_circle_detail_description_layout:
                if (mState == SPREAD_STATE) {
                    descriptionTv.setMaxLines(VIDEO_CONTENT_DESC_MAX_LINE);
                    descriptionTv.requestLayout();
                    descriptionMore.setBackgroundResource(R.drawable.triangle_up_bg);
                    mState = SHRINK_UP_STATE;
                } else if (mState == SHRINK_UP_STATE) {
                    descriptionTv.setMaxLines(Integer.MAX_VALUE);
                    descriptionTv.requestLayout();
                    descriptionMore.setBackgroundResource(R.drawable.triangle_down_bg);
                    mState = SPREAD_STATE;
                }
                break;
            case R.id.game_circle_game_join:
                if (ExApplication.MEMBER_ID.equals("") || ExApplication.MEMBER_ID == null) {
                    ToastUtils.showToast(context, "请先登录！");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new joinGameCircleTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new joinGameCircleTask().execute();
                }
                break;
        }
    }

    /**
     * 获取游戏圈子信息
     */
    private class GetGameInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            game = JsonHelper.getGameCircleInfo(group_id, ExApplication.MEMBER_ID, type_name);
            if (game != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(getApplicationContext(), "获取圈子信息失败");
                DialogUtils.cancelLoadingDialog();
                return;
            }
            titleTv.setText(game.getGroup_name());
            gameTypeTv.setText("类型:" + type_name);
            focusTv.setText(game.getAttention_num() + "人关注");
            videoCountTv.setText(game.getVideo_num() + "个视频");
            descriptionTv.setText(game.getDescription());
            exApplication.imageLoader.displayImage(game.getFlagPath(), gameIcon, exApplication.getOptions());
            if (game.getMark().equals("1")) {
                joinTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                joinTv.setText("已加入");
            }
            DialogUtils.cancelLoadingDialog();
        }
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
        ((RadioButton) rg_nav_content.getChildAt(2)).setChecked(true);
        ((RadioButton) rg_nav_content.getChildAt(0)).setChecked(true);
        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("checkId", checkedId + "");
                if (rg_nav_content.getChildAt(checkedId) != null) {

                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) findViewById(i);
                        tempButton.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    RadioButton tempButton = (RadioButton) findViewById(checkedId);
                    tempButton.setTextColor(context.getResources().getColor(R.color.master_focus_tv));

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

    /**
     * 加入游戏圈子
     */
    private class joinGameCircleTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            boolean b = JsonHelper.joinGameCircle(group_id, ExApplication.MEMBER_ID);
            if (b) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("s")) {
                if (game.getMark().equals("0")) {
                    joinTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    joinTv.setText("已加入");
                    ToastUtils.showToast(context, "加入成功");
                    game.setMark("1");
                } else {
                    joinTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    joinTv.setText("+加入");
                    ToastUtils.showToast(context, "退出圈子");
                    game.setMark("0");
                }
            }
        }
    }
}
