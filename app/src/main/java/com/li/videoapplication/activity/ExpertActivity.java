package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
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
import com.li.videoapplication.R;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.fragment.ExpertFragment;
import com.li.videoapplication.utils.Titles;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/29.
 * 首页发现 达人榜 页面
 */
public class ExpertActivity extends FragmentActivity implements View.OnClickListener {
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
    public static String[] tabTitle = Titles.ExpertTitle;
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;
    private int position;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private Context context;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expert);
        context = ExpertActivity.this;
        initView();
        setListener();
    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.activity_expert_back);
        backBtn.setOnClickListener(this);
        rl_nav = (RelativeLayout) findViewById(R.id.activity_expert_rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.activity_expert_mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.activity_expert_rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.activity_expert_iv_nav_indicator);
        mViewPager = (ViewPager) findViewById(R.id.activity_expert_mViewPager);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / 3;
        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, (Activity) context);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        fragments.add(ExpertFragment.newInstance("video", ""));
        fragments.add(ExpertFragment.newInstance("fan", ""));
        fragments.add(ExpertFragment.newInstance("rank", ""));

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                System.out.println("Extra...i: " + i);
            }
        });

    }

    private void initNavigationHSV() {

        rg_nav_content.removeAllViews();

        for (int i = 0; i < tabTitle.length; i++) {

            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            if (i == 0) {
                rb.setTextColor(ExpertActivity.this.getResources().getColor(R.color.search_result_focus));
            } else {
                rb.setTextColor(ExpertActivity.this.getResources().getColor(R.color.search_result_default));
            }
            rb.setText(tabTitle[i]);
            rb.setTextSize(18);

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

                    for (int i = 0; i <= 2; i++) {
                        RadioButton tempButton = (RadioButton) findViewById(i);
                        tempButton.setTextColor(ExpertActivity.this.getResources().getColor(R.color.search_result_default));
                    }
                    RadioButton tempButton = (RadioButton) findViewById(position);
                    tempButton.setTextColor(ExpertActivity.this.getResources().getColor(R.color.search_result_focus));
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

                if (rg_nav_content.getChildAt(checkedId) != null) {


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
                            (checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_expert_back:
                this.finish();
                break;
        }
    }
}
