package com.li.videoapplication.activity;

import android.app.Activity;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.li.videoapplication.Adapter.AssortAdapter;
import com.li.videoapplication.Adapter.GameTypeAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.entity.Game;
import com.li.videoapplication.entity.GameType;
import com.li.videoapplication.utils.HttpUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/5/7.
 * <p>
 * 首页 找游戏 界面
 */
public class FoundGameActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private List<GameType> list;
    private AssortAdapter adapter;
    private List<Game> glist;
    private GameTypeAdapter gAdapter;
    private GridView assortGv, gameGv;
    private ImageButton foundBack, foundRecord;
    private LinearLayout foundLl;
    //搜索
    private AutoCompleteTextView autoCompleteTextView = null;
    private Button searchBtn;
    private RelativeLayout searchRl;
    private ImageView searchIv;

    private ArrayAdapter<String> keyAdapter = null;
    private List<String> suggest;
    private static String[] word = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_foundgame);
        initView();
        init();
    }

    public void init() {
        list = new ArrayList<GameType>();
        adapter = new AssortAdapter(getApplicationContext(), list);
        glist = new ArrayList<Game>();
        gAdapter = new GameTypeAdapter(getApplicationContext(), glist);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameTypeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameTypeTask().execute();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameTask().execute();
        }
    }

    public void initView() {
        assortGv = (GridView) findViewById(R.id.found_assort_gv);
        assortGv.setOnItemClickListener(this);
        assortGv.setAdapter(adapter);

        gameGv = (GridView) findViewById(R.id.found_game_gv);
        gameGv.setOnItemClickListener(this);
        gameGv.setAdapter(gAdapter);
        gameGv.clearFocus();

        foundBack = (ImageButton) findViewById(R.id.found_back);
        foundBack.setOnClickListener(this);
        foundRecord = (ImageButton) findViewById(R.id.found_video);
        foundRecord.setOnClickListener(this);
        foundLl = (LinearLayout) findViewById(R.id.activity_foundgame_ll);
        foundLl.setOnClickListener(this);

        searchBtn = (Button) findViewById(R.id.activity_foundgame_btn);
        searchBtn.setOnClickListener(this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.activity_foundgame_edt);
        searchRl = (RelativeLayout) findViewById(R.id.activity_foundgame_search_et_rl);
        searchIv = (ImageView) findViewById(R.id.activity_foundgame_search_et_iv);
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
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.found_back:
                finish();
                break;
            case R.id.activity_foundgame_btn:
                if (TextUtils.isEmpty(autoCompleteTextView.getText().toString().trim())) {
                    ToastUtils.showToast(FoundGameActivity.this, "请输入搜索内容");
                    return;
                }
                intent = new Intent(FoundGameActivity.this, SearchResultActivity.class);
                intent.putExtra("key", autoCompleteTextView.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.activity_foundgame_ll:
                autoCompleteTextView.clearFocus();
                break;
            case R.id.found_video:
                intent = new Intent();
                intent.setClassName(this, "com.fmscreenrecord.activity.FMMainActivity");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.found_assort_gv://游戏分类
                Intent intent = new Intent(getApplicationContext(), NewAssortActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("title", list.get(position).getName());
                startActivity(intent);
                break;
            case R.id.found_game_gv://热门游戏
                intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("title", glist.get(position).getName());
                startActivity(intent);
                break;
        }
    }

    /**
     * 异步获取 游戏分类 列表
     */
    private class GetGameTypeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            list = JsonHelper.getGameType();
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
                adapter = new AssortAdapter(getApplicationContext(), list);
                assortGv.setAdapter(adapter);
            }
        }
    }

    /**
     * 异步获取 热门游戏 列表
     */
    private class GetGameTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            glist = JsonHelper.getHotGameList();
            if (glist != null) {
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
                gAdapter = new GameTypeAdapter(getApplicationContext(), glist);
                gameGv.setAdapter(gAdapter);
            }
        }
    }

    /**
     * 获取搜索联想词
     */
    class getSuggestWord extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            keyAdapter = new ArrayAdapter<String>(FoundGameActivity.this, R.layout.suggest_item, R.id.suggest_text, suggest);
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
