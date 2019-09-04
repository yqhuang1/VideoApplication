package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.MImageLoader;
import com.li.videoapplication.utils.SearchGameJsonHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频分享页面 类型选择 查找手机游戏
 */
public class SearchGameName extends Activity implements TextWatcher,
        OnClickListener {
    private EditText SearchEdt;

    // 显示最近游戏和热门游戏的listview
    private ListView historyListView, netListView;
    private LinearLayout cancel;
    // 本地已经搜索过的数据记录
    SearchGameJsonHelp searchGameJsonHelp = new SearchGameJsonHelp();
    SharedPreferences sp;

    private ViewPager mPager;// 页卡内容
    private List<View> listViews; // Tab页面列表
    private ImageView cursor;// 动画图片
    private TextView t1, t2;// 页卡头标
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    // 搜索类型(手机游戏，精彩生活)
    String type;
    // 搜索结果资料
    private ArrayList<VideoInfo> data;
    private Context mContext;
    LayoutInflater layoutInflater;
    LinearLayout back;
    MImageLoader mImageLoader;
    MyAdapter myAdapter;
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (myAdapter == null) {
                        myAdapter = new MyAdapter(data);
                    }
                    myAdapter.refresh(data);
                    historyListView.setAdapter(myAdapter);

                    break;
                case 2:
                    if (myAdapter == null) {
                        myAdapter = new MyAdapter(data);
                    }
                    myAdapter.refresh(data);
                    netListView.setAdapter(myAdapter);
                    break;
            }

        }
    };

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(MResource.getIdByName(getApplication(), "layout",
                "fm_searchgame"));
        mContext = this;
        layoutInflater = LayoutInflater.from(mContext);
        // 初始化图片下载(采用开源框架Image-Loader)
        mImageLoader = new MImageLoader(mContext);

        /**
         * 页卡初始化
         **/
        InitViewPager();
        InitImageView();
        InitTextView();

        findViews();
        setonclick();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        sp = SharedPreferencesUtils.getMinJieKaiFaPreferences(mContext);
        String jsonString = sp.getString("SearchGameJson", null);
        if (jsonString != null) {
            data = searchGameJsonHelp.parseJSONString(jsonString);
            myHandler.sendEmptyMessage(1);
        }
    }

    private void setonclick() {
        SearchEdt.addTextChangedListener(this);
        cancel.setOnClickListener(this);

        back.setOnClickListener(this);

    }

    private void findViews() {
        SearchEdt = (EditText) findViewById(MResource.getIdByName(
                getApplication(), "id", "search_edt"));

        historyListView = (ListView) listViews.get(0).findViewById(
                MResource.getIdByName(getApplication(), "id",
                        "fm_search_listview_history"));
        netListView = (ListView) listViews.get(1).findViewById(
                MResource.getIdByName(getApplication(), "id",
                        "fm_search_listview_net"));
        cancel = (LinearLayout) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_cancel"));

        back = (LinearLayout) findViewById(MResource.getIdByName(
                getApplication(), "id", "search_back"));

    }

    /**
     * 适配器
     *
     * @author WYX
     */
    class MyAdapter extends BaseAdapter {
        ViewHodler hodler;
        ArrayList<VideoInfo> data;

        public MyAdapter(ArrayList<VideoInfo> data) {
            this.data = data;
        }

        public int getCount() {

            return data.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        public void refresh(ArrayList<VideoInfo> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                hodler = new ViewHodler();

                convertView = layoutInflater
                        .inflate(MResource.getIdByName(mContext, "layout",
                                "fm_searchgamename_listview"), null);
                hodler.name = (TextView) convertView.findViewById(MResource
                        .getIdByName(mContext, "id", "fm_search_textview"));
                hodler.imageview = (ImageView) convertView
                        .findViewById(MResource.getIdByName(mContext, "id",
                                "fm_search_imageview"));
                hodler.selectgame = (LinearLayout) convertView
                        .findViewById(MResource.getIdByName(mContext, "id",
                                "fm_select_game_type"));

                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            hodler.name.setText(data.get(position).getGamename());
            String imageurl = data.get(position).getImageUrl();
            mImageLoader.loader.displayImage(imageurl, hodler.imageview,
                    mImageLoader.options);
            hodler.selectgame.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MinUtil.upUmenEventValue(mContext, "手机游戏", "bt_videoTypeUpload");
                    String gameid = data.get(position).getGameTypeId();
                    String gamename = data.get(position).getGamename();
                    Intent intent = new Intent();

                    // 把返回数据存入Intent
                    intent.putExtra("game_id", gameid);
                    intent.putExtra("game_name", gamename);

                    // 设置返回数据
                    ((Activity) mContext).setResult(RESULT_OK, intent);

                    // TODO
                    /**
                     * 将用户选择的数据记录进SharedPreferences
                     */
                    // 先获取SharedPreferences中的数据
                    String jsonString = sp.getString("SearchGameJson", null);
                    ArrayList<VideoInfo> newData;
                    // 将String转换成ArrayList<VideoInfo>
                    if (jsonString == null) {
                        newData = new ArrayList<VideoInfo>();
                    } else {
                        newData = searchGameJsonHelp
                                .parseJSONString(jsonString);
                    }
                    // 遍历该搜索记录是否已经存进SharedPreferences
                    int length = newData.size();
                    for (int i = 0; i < length; i++) {
                        if (gameid.equals(newData.get(i).getGameTypeId())) {
                            finish();
                            return;
                        }
                    }

                    /**
                     * 如果搜索记录不存在重复
                     */
                    // 将要添加的数据放进数组之中
                    newData.add(data.get(position));
                    // 提交数据
                    sp.edit()
                            .putString(
                                    "SearchGameJson",
                                    searchGameJsonHelp.toJSON(newData)
                                            .toString()).commit();

                    finish();
                }
            });
            return convertView;
        }

        class ViewHodler {
            TextView name;
            ImageView imageview;
            LinearLayout selectgame;

        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        loadData(s.toString());

        if (s.toString().isEmpty()) {
            cancel.setVisibility(View.GONE);

        } else {
            cancel.setVisibility(View.VISIBLE);

        }
        t2.performClick();
    }

    public void loadData(final String name) {
        new Thread() {

            public void run() {
                data = JsonHelper.getSearchGameNameData(type, name);
                myHandler.sendEmptyMessage(2);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v == cancel) {

            SearchEdt.setText("");
        } else if (v == back) {
            finish();
        }

    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {

                        String jsonString = sp.getString("SearchGameJson", null);
                        if (jsonString != null) {
                            data = searchGameJsonHelp.parseJSONString(jsonString);
                            myHandler.sendEmptyMessage(1);
                        }
                        animation = new TranslateAnimation(offset, one, 0, 0);
                        t1.setTextColor(0xff1ba1d8);
                        t2.setTextColor(0xffbfbfbf);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {

                        loadData(SearchEdt.getText().toString() + "");
                        animation = new TranslateAnimation(one, offset, 0, 0);

                        t2.setTextColor(0xff1ba1d8);
                        t1.setTextColor(0xffbfbfbf);
                    }
                    break;

            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }

    ;

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        mPager = (ViewPager) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_file_vPager"));
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(MResource.getIdByName(getApplication(),
                "layout", "fm_search_history_listview"), null));
        listViews.add(mInflater.inflate(MResource.getIdByName(getApplication(),
                "layout", "fm_search_net_listview"), null));
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 初始化头标
     */
    private void InitTextView() {
        t1 = (TextView) (TextView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_file_text1"));
        t2 = (TextView) (TextView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_file_text2"));

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }

    /**
     * 初始化动画
     */
    private void InitImageView() {
        cursor = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_file_cursor"));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenW = dm.widthPixels;// 获取分辨率宽度
        // 将游标的尺寸宽度设为屏幕的二分之一
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                screenW / 2, 3);
        cursor.setLayoutParams(param);

        offset = screenW / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
}
