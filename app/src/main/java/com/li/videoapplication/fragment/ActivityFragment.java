package com.li.videoapplication.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.utils.Titles;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页 活动
 */
public class ActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";
    private View view;

    private Context context;
    private static boolean isFirst = true;

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

    public static String[] tabTitle;
    private LayoutInflater mInflater;
    private int count;// 屏幕显示的标签个数
    private int indicatorWidth;// 每个标签所占的宽度
    private int currentIndicatorLeft = 0;
    private static int checkedkposition = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity, null);
        initView();
        setListener();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        //滑动布局
        rl_nav = (RelativeLayout) view.findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) view.findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) view.findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) view.findViewById(R.id.iv_nav_indicator);
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        {
            indicatorWidth = dm.widthPixels / 2;
            tabTitle = Titles.activityTitle;
            count = tabTitle.length;
        }

        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;

        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, (Activity) context);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        fragments.add(HotActivityFragment.newInstance("", ""));
        fragments.add(MyActivityFragment.newInstance("", ""));

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getActivity().getSupportFragmentManager(), mViewPager, fragments);
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
                rb.setTextColor(context.getResources().getColor(R.color.main_title_bg));
            }
            rb.setText(tabTitle[i]);
            rb.setTextSize(16);

            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
        }
        RadioButton rb = (RadioButton) mInflater.inflate(
                R.layout.nav_radiogroup_item, null);
        rg_nav_content.addView(rb);
    }

    private void setListener() {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton) rg_nav_content.getChildAt(position)).performClick();

                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) rg_nav_content.getChildAt(i);
                        tempButton.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    RadioButton tempButton = (RadioButton) rg_nav_content.getChildAt(position);
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

                if (rg_nav_content.getChildAt(checkedId) != null) {
                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) group.getChildAt(i);
                        tempButton.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    RadioButton tempButton = (RadioButton) group.getChildAt(checkedId);
                    tempButton.setTextColor(context.getResources().getColor(R.color.master_focus_tv));

                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft,
                            indicatorWidth * checkedId, 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    //执行位移动画
                    iv_nav_indicator.startAnimation(animation);

                    mViewPager.setCurrentItem(checkedId);    //ViewPager 跟随一起 切换

                    //记录当前 被点击下标 的位置
                    checkedkposition = checkedId;
                    //记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = indicatorWidth * checkedId;
                    mHsv.smoothScrollTo(
                            (checkedId > 1 ? currentIndicatorLeft : 0) - indicatorWidth * 2, 0);
                }
            }
        });
    }

}
