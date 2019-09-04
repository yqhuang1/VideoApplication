package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.MImageLoader;

import java.util.ArrayList;

/**
 * 视频分享页面 类型选择 查找精彩生活
 */
public class SearchLifeName extends Activity implements OnClickListener {

    private ListView listListView;

    // 搜索类型(手机游戏，精彩生活)
    String type;
    // 搜索结果资料
    private ArrayList<VideoInfo> data;
    private Context mContext;
    LayoutInflater layoutInflater;
    LinearLayout back;
    MImageLoader mImageLoader;
    MyAdapter myAdapter;
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    myAdapter = new MyAdapter(data);
                    listListView.setAdapter(myAdapter);
                    break;

            }

        }
    };

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(MResource.getIdByName(getApplication(), "layout",
                "fm_searchlife"));
        mContext = this;
        layoutInflater = LayoutInflater.from(mContext);
        // 初始化图片下载(采用开源框架Image-Loader)
        mImageLoader = new MImageLoader(mContext);

        findViews();
        setonclick();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        loadData();

    }

    public void loadData() {
        new Thread() {

            public void run() {
                data = JsonHelper.getSearchGameNameData(type, "空");
                myHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private void setonclick() {

        back.setOnClickListener(this);

    }

    private void findViews() {

        listListView = (ListView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_searchlife_listview"));

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
            mImageLoader.loader.displayImage(imageurl, hodler.imageview, mImageLoader.options);
            hodler.selectgame.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MinUtil.upUmenEventValue(mContext, "精彩生活", "bt_videoTypeUpload");
                    String gameid = data.get(position).getGameTypeId();
                    String gamename = data.get(position).getGamename();
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
    public void onClick(View v) {
        if (v == back) {
            finish();
        }

    }

}
