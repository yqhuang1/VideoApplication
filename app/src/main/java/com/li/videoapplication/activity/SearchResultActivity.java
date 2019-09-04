package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.Adapter.KeyWordAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.entity.KeyWord;
import com.li.videoapplication.fragment.SearchGiftFragment;
import com.li.videoapplication.fragment.SearchUserFragment;
import com.li.videoapplication.fragment.SearchVideoFragment;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.li.videoapplication.utils.HttpUtils;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.Titles;
import com.li.videoapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果 页面*
 */
public class SearchResultActivity extends FragmentActivity implements View.OnClickListener {

    private Context context;
    private ImageButton recordBtn, backBtn;
    private TextView titleTv;

    private RelativeLayout edtLayout, searchBtnLayout;
    private ImageView searchBtn;
    // 盛放关键字的layout的宽和高
    public int width = 0;
    public int height = 0;
    private boolean isViewCreated;

    //关键字
    private String[] searchKey = {};
    private List<KeyWord> list = new ArrayList<KeyWord>();

    private KeyWordAdapter keyAdapter;

    //搜索联想字
    private static String[] word = {};
    private List<String> suggest;
    private ArrayAdapter<String> adapter = null;
    private RelativeLayout searchRl;
    private AutoCompleteTextView autoCompleteTextView = null;
    private ImageView cancleIv, searchIv;


    private CompleteTaskUtils utils;

    String key = "";

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
    public static String[] tabTitle = Titles.tabSearchTitle;
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;
    private int position;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_result);

        context = SearchResultActivity.this;

        if (this.getIntent().getExtras() != null) {
            key = this.getIntent().getStringExtra("key");
            Log.e("key", key);
        }
        initHeadView();
        initView();
        setListener();

        if ("".equals(SharePreferenceUtil.getPreference(context, "10task_flag"))) {
            utils = new CompleteTaskUtils(this, "10");
            utils.completeMission();
            SharePreferenceUtil.setPreference(context, "10task_flag", "true");
        }
        ExApplication.upUmenEventValue(context, "搜索次数", "search_count");

    }


    private void initView() {
        rl_nav = (RelativeLayout) findViewById(R.id.search_rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.search_mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.search_rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.search_iv_nav_indicator);
        mViewPager = (ViewPager) findViewById(R.id.search_mViewPager);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / 3;

        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;

        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, (Activity) context);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

//		for(int i=1;i<tabTitle.length;i++){
        fragments.add(SearchVideoFragment.newInstance(key, ""));
        fragments.add(SearchGiftFragment.newInstance(key, ""));
//        fragments.add(SerachMissionFragment.newInstance(key,""));
        fragments.add(SearchUserFragment.newInstance(key, ""));

//		}

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                System.out.println("Extra...i: " + i);
            }
        });

    }

    private void initHeadView() {
        recordBtn = (ImageButton) findViewById(R.id.search_result_record);
        recordBtn.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.search_result_back);
        backBtn.setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.search_result_title);
        titleTv.setText(key);

        edtLayout = (RelativeLayout) findViewById(R.id.search_result_edt_layout);
        searchBtnLayout = (RelativeLayout) findViewById(R.id.search_result_btn_layout);

        searchBtn = (ImageView) findViewById(R.id.search_result_btn);
        searchBtn.setOnClickListener(this);

        searchRl = (RelativeLayout) findViewById(R.id.fragment_search_result_et_rl);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_result_edt);
        autoCompleteTextView.setText(key);
        autoCompleteTextView.setSelection(key.length());
//        edt=(EditText)findViewById(R.id.search_edt);
        cancleIv = (ImageView) findViewById(R.id.search_result_cancel);
        cancleIv.setOnClickListener(this);
        searchIv = (ImageView) findViewById(R.id.fragment_search_result_et_iv);
    }


    private void initNavigationHSV() {

        rg_nav_content.removeAllViews();

        for (int i = 0; i < tabTitle.length; i++) {

            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            if (i == 0) {
                rb.setTextColor(SearchResultActivity.this.getResources().getColor(R.color.search_result_focus));
            } else {
                rb.setTextColor(SearchResultActivity.this.getResources().getColor(R.color.search_result_default));
            }
            rb.setText(tabTitle[i]);
            rb.setTextSize(18);

            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
        }
    }


    private void setListener() {

        //设置输入多少字符后提示，默认值为1
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new getSuggestWord().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new getSuggestWord().execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (autoCompleteTextView.getText().toString().equals("")) {
                    cancleIv.setVisibility(View.GONE);
                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.main_body_bg));
                    searchBtn.setImageResource(R.drawable.searchfoundgray);
                    searchBtnLayout.setBackgroundResource(R.drawable.search_store_right_gray);
                } else {
                    cancleIv.setVisibility(View.VISIBLE);
                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.main_body_bg));
                    searchBtn.setImageResource(R.drawable.searchfoundred);
                    searchBtnLayout.setBackgroundResource(R.drawable.search_store_right_white);
                }
            }
        });


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton) rg_nav_content.getChildAt(position)).performClick();

                    for (int i = 0; i <= 2; i++) {
                        RadioButton tempButton = (RadioButton) findViewById(i);
                        tempButton.setTextColor(SearchResultActivity.this.getResources().getColor(R.color.search_result_default));
                    }
                    RadioButton tempButton = (RadioButton) findViewById(position);
                    tempButton.setTextColor(SearchResultActivity.this.getResources().getColor(R.color.search_result_focus));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_result_btn:
                if (TextUtils.isEmpty(autoCompleteTextView.getText().toString().trim())) {
                    ToastUtils.showToast(this, "请输入搜索内容");
                    return;
                }
                Intent intent = new Intent(this, SearchResultActivity.class);
                intent.putExtra("key", autoCompleteTextView.getText().toString().trim());
                startActivity(intent);
                this.finish();
                break;
            case R.id.search_result_cancel:
                autoCompleteTextView.setText("");
                break;
            case R.id.search_result_back:
                this.finish();
                break;
            case R.id.search_result_record:
                break;
        }
    }

    /**
     * 获取搜索联想词
     */
    private class getSuggestWord extends AsyncTask<String, String, String> {

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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.suggest_item, R.id.suggest_text, suggest);
            autoCompleteTextView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

}
