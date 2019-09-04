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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.li.videoapplication.Adapter.KeyWordAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.MyGridview;
import com.li.videoapplication.entity.KeyWord;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.HttpUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索 界面
 */
public class SearchActivity extends Activity implements View.OnClickListener {

    private Context context;

    private RelativeLayout edtLayout, searchBtnLayout;
    private ImageView searchBtn;
    // 盛放关键字的layout的宽和高
    public int width = 0;
    public int height = 0;
    private boolean isViewCreated;

    private ImageButton backBtn;
    private ImageButton refreshBtn;
    private MyGridview mGridView;

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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        context = SearchActivity.this;

        getView();
        DialogUtils.createLoadingDialog(SearchActivity.this, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetKeyWordTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetKeyWordTask().execute();
        }

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

    }

    private void getView() {
        edtLayout = (RelativeLayout) findViewById(R.id.search_edt_layout);
        searchBtnLayout = (RelativeLayout) findViewById(R.id.search_btn_layout);

        searchBtn = (ImageView) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);

        backBtn = (ImageButton) findViewById(R.id.search_back);
        backBtn.setOnClickListener(this);

        searchRl = (RelativeLayout) findViewById(R.id.fragment_search_et_rl);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_edt);
//        edt=(EditText)findViewById(R.id.search_edt);
        cancleIv = (ImageView) findViewById(R.id.search_cancel);
        cancleIv.setOnClickListener(this);
        searchIv = (ImageView) findViewById(R.id.fragment_search_et_iv);

        refreshBtn = (ImageButton) findViewById(R.id.search_refreshBtn);
        refreshBtn.setOnClickListener(this);

        mGridView = (MyGridview) findViewById(R.id.search_gridview);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("key", list.get(i).getWord());
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                if (TextUtils.isEmpty(autoCompleteTextView.getText().toString().trim())) {
                    ToastUtils.showToast(this, "请输入搜索内容");
                    return;
                }
                Intent intent = new Intent(this, SearchResultActivity.class);
                intent.putExtra("key", autoCompleteTextView.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.search_cancel:
                autoCompleteTextView.setText("");
                break;
            case R.id.search_back:
                this.finish();
                break;
            case R.id.search_refreshBtn://刷新关键字
                refreshBtn.setBackgroundResource(R.drawable.refresh_btn_red);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetKeyWordTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetKeyWordTask().execute();
                }
                break;
        }
    }

    /**
     * 获取关键字
     */
    private class GetKeyWordTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            list = JsonHelper.getKeyWord();
            if (list != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DialogUtils.cancelLoadingDialog();

            keyAdapter = new KeyWordAdapter(context, list);
            mGridView.setAdapter(keyAdapter);
            keyAdapter.notifyDataSetChanged();
            refreshBtn.setBackgroundResource(R.drawable.refresh_btn_gray);
            if (s.equals("")) {
                ToastUtils.showToast(SearchActivity.this, "获取不到关键字");
                return;
            }
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
