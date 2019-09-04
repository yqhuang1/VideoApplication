package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.li.videoapplication.Adapter.GameCircleGameAdapter;
import com.li.videoapplication.Adapter.GameCircleTypeAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.Game;
import com.li.videoapplication.entity.GameType;
import com.li.videoapplication.utils.HttpUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/7/2.
 * 游戏圈子 页面
 */
public class GameCircleActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshListView.IXListViewListener {

    private List<GameType> list;
    private ListView gameTypeLV;
    private GameCircleTypeAdapter typeAdapter;
    private int lastItem = 0;//上一次点击类型位置

    private Context context;
    private ImageButton gameCircleBack;

    private RefreshListView refreshListView;
    private GameCircleGameAdapter gameAdapter;
    private List<Game> gameList;
    private List<Game> connecList;

    private int pageId;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    //搜索
    private AutoCompleteTextView autoCompleteTextView = null;
    private Button searchBtn;
    private RelativeLayout searchRl;
    private ImageView searchIv;
    private ArrayAdapter<String> keyAdapter = null;
    private List<String> suggest;
    private static String[] word = {};
    private static List<String> keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_circle);
        init();
        initView();
    }

    private void init() {
        context = getApplicationContext();
        dateFormat = new SimpleDateFormat();
        list = new ArrayList<GameType>();
        typeAdapter = new GameCircleTypeAdapter(context, list);

        gameList = new ArrayList<Game>();
        connecList = new ArrayList<Game>();
        gameAdapter = new GameCircleGameAdapter(context, gameList);
    }

    private void initView() {
        gameCircleBack = (ImageButton) findViewById(R.id.game_circle_back);
        gameCircleBack.setOnClickListener(this);
        gameTypeLV = (ListView) findViewById(R.id.activity_game_circle_game_type_lv);
        gameTypeLV.setOnItemClickListener(this);
        gameTypeLV.setAdapter(typeAdapter);

        refreshListView = (RefreshListView) findViewById(R.id.activity_game_circle_game_rlv);
        refreshListView.setAdapter(gameAdapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setOnItemClickListener(this);

        searchBtn = (Button) findViewById(R.id.activity_gamecircle_btn);
        searchBtn.setOnClickListener(this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.activity_gamecircle_edt);
        searchRl = (RelativeLayout) findViewById(R.id.activity_game_circle_search_et_rl);
        searchIv = (ImageView) findViewById(R.id.activity_gamecircle_search_et_iv);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchRl.setBackgroundResource(R.drawable.search_focus_store);
                    searchBtn.setBackgroundResource(R.drawable.search_focus);
                    searchIv.setBackgroundColor(getResources().getColor(R.color.search_focus));
                } else {
                    searchRl.setBackgroundResource(R.drawable.search_store);
                    searchBtn.setBackgroundResource(R.drawable.search);
                    searchIv.setBackgroundColor(getResources().getColor(R.color.search_et_bg));
                }
            }
        });
        //设置输入多少字符后提示，默认值为1
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new getSuggestWord().execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameTypeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameTypeTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_circle_back:
                finish();
                break;
            case R.id.activity_gamecircle_btn:
                if (TextUtils.isEmpty(autoCompleteTextView.getText().toString().trim())) {
                    ToastUtils.showToast(context, "请输入搜索内容");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new getSearchWord(autoCompleteTextView.getText().toString().trim()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new getSearchWord(autoCompleteTextView.getText().toString().trim()).execute();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.activity_game_circle_game_rlv://右侧RefreshListView
                Intent intent = new Intent(context, GameCircleDetailActivity.class);
                intent.putExtra("group_id", gameList.get(position - 1).getGroup_id());
                intent.putExtra("type_name", gameList.get(position - 1).getGroup_type());
                startActivity(intent);
                break;
            case R.id.activity_game_circle_game_type_lv://左侧ListView
                if (position != lastItem) {
                    list.get(position).setIsCheck(true);
                    list.get(lastItem).setIsCheck(false);
                    lastItem = position;
                    typeAdapter.update(list);
                    onRefresh();
                }
                break;
        }

    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameListTask(pageId + "", list.get(lastItem).getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameListTask(pageId + "", list.get(lastItem).getId()).execute();
        }
    }

    @Override
    public void onLoadMore() {
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameListTask(pageId + "", list.get(lastItem).getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameListTask(pageId + "", list.get(lastItem).getId()).execute();
        }
    }

    /**
     * 获取圈子分类列表
     */
    private class GetGameTypeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            list = JsonHelper.getGameCircleType();
            if (list != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(getApplicationContext(), "连接服务器失败");
                return;
            }

            if (s.equals("s")) {
                list.get(0).setIsCheck(true);
                typeAdapter.update(list);
                onRefresh();
            }
        }
    }

    /**
     * 获取游戏圈子列表
     */
    private class GetGameListTask extends AsyncTask<Void, Void, String> {

        String page = "";
        String type_id = "";

        public GetGameListTask(String page, String type_id) {
            this.page = page;
            this.type_id = type_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getGameCircleGameList(page, type_id, ExApplication.MEMBER_ID);
            if (connecList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(getApplicationContext(), "连接服务器失败");
                return;
            }
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    refreshListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    gameList.clear();
                    gameList.addAll(connecList);
                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        gameList.addAll(connecList);
                    }
                } else {
                    ToastUtils.showToast(context, "已加载全部");
                }
            }
            gameAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }

    /**
     * 获取搜索联想词
     */
    class getSuggestWord extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            keyAdapter = new ArrayAdapter<String>(context, R.layout.suggest_item, R.id.suggest_text, suggest);
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

    class getSearchWord extends AsyncTask<Void, Void, String> {

        private String keyword;

        public getSearchWord(String keyword) {
            this.keyword = keyword;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                Intent intent = new Intent(context, GameCircleDetailActivity.class);
                intent.putExtra("group_id", keyWord.get(0));
                intent.putExtra("type_name", keyWord.get(1));
                startActivity(intent);
            } else {
                ToastUtils.showToast(context, "没有该游戏圈！");
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            keyWord = new ArrayList<String>();
            keyWord = JsonHelper.getGameCircleSearchkey(keyword);
            if (keyWord != null) {
                return "s";
            }
            return "";
        }

    }

}
