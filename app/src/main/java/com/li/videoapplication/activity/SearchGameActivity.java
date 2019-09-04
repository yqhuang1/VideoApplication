package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.UploadGameEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/**
 * 查找游戏名称
 */
public class SearchGameActivity extends Activity implements TextWatcher,
        OnClickListener {
    private EditText SearchEdt;
    private ListView listView;
    private LinearLayout cancel;
    // 关键词
    private String keyword;
    // 搜索框
    private ImageView search;

    // 搜索结果资料
    private ArrayList<UploadGameEntity> data;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ImageView back;
    // 异步加载图片
    private ExApplication exApplication;
    private DisplayImageOptions options;
    MyAdapter myAdapter;
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    myAdapter = new MyAdapter(data);
                    listView.setAdapter(myAdapter);
                    break;

            }

        }
    };

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_game);
        mContext = this;
        layoutInflater = LayoutInflater.from(mContext);
        // 初始化图片下载(采用开源框架Image-Loader)
        exApplication = new ExApplication(mContext);

        findViews();
        setonclick();
//        loadData("");
    }

    private void setonclick() {
        SearchEdt.addTextChangedListener(this);
        cancel.setOnClickListener(this);
        search.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    private void findViews() {
        SearchEdt = (EditText) findViewById(R.id.search_edt);
        listView = (ListView) findViewById(R.id.search_listview);
        cancel = (LinearLayout) findViewById(R.id.cancel_layout);
        search = (ImageView) findViewById(R.id.search_btn);
        back = (ImageView) findViewById(R.id.search_game_back);

    }

    class MyAdapter extends BaseAdapter {
        ViewHodler hodler;
        ArrayList<UploadGameEntity> data;

        public MyAdapter(ArrayList<UploadGameEntity> data) {
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

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                hodler = new ViewHodler();

                convertView = layoutInflater
                        .inflate(R.layout.search_game_item, null);
                hodler.name = (TextView) convertView.findViewById(R.id.search_textview);
                hodler.imageview = (ImageView) convertView
                        .findViewById(R.id.search_imageview);
                hodler.selectgame = (LinearLayout) convertView
                        .findViewById(R.id.select_game_type);

                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            hodler.name.setText(data.get(position).getDisplayName());
            String imageurl = data.get(position).getImageUrl();
            exApplication.imageLoader.displayImage(imageurl, hodler.imageview, options);
            hodler.selectgame.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String gameid = data.get(position).getGameTypeId();
                    String gamename = data.get(position).getDisplayName();
                    Intent intent = new Intent();
                    // 把返回数据存入Intent
                    intent.putExtra("game_id", gameid);
                    intent.putExtra("game_name", gamename);
                    // 设置返回数据
                    ((Activity) mContext).setResult(RESULT_OK, intent);
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
//        loadData(s.toString());
        keyword = s.toString();
    }

//    public void loadData(final String name) {
//        new Thread() {
//
//            public void run() {
//                data = JsonHelper.getSearchGameNameData("", name);
//                myHandler.sendEmptyMessage(1);
//            }
//        }.start();
//    }

    @Override
    public void onClick(View v) {
        if (v == cancel) {
            SearchEdt.setText("");
        } else if (v == search) {
            if (SearchEdt.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "请输入要搜索的游戏名", Toast.LENGTH_SHORT).show();
            } else {
//                loadData(keyword);
            }

        } else if (v == back) {
            finish();
        }

    }

}
