package com.li.videoapplication.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.VideoAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.GameEntity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.AlertDialog;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.MyApplication;
import com.li.videoapplication.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 搜索结果页面 相关视频*
 */
public class SearchVideoFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    //    private Button hotBtn,newBtn;
    private TextView hotTv, timeTv;
    private ImageView hotIv, timeIv;
    private RefreshListView hotListView;
    private RefreshListView newListView;
    private ListView gameListView;
    private List<VideoEntity> hotlist;
    private List<VideoEntity> newlist;
    private VideoAdapter hotAdapter;
    private VideoAdapter newAdapter;

    private int hpageId;
    private List<VideoEntity> hconnectList;
    private List<GameEntity> gameList;
    private int hotType = 0;
    private static final int HOT_REFRESH = 0;
    private static final int HOT_LOADMORE = 1;
    private SimpleDateFormat hot_dateFormat = null;

    private int npageId;
    private List<VideoEntity> nconnectList;
    private int newType = 0;
    private static final int NEW_REFRESH = 0;
    private static final int NEW_LOADMORE = 1;
    private SimpleDateFormat new_dateFormat = null;

    private FrameLayout video_gameLayout, videoLayout, gameLayout, gameDetailLayout;
    private RelativeLayout gameAbstractLayout;
    private ImageView frameBackIv, gameDetailIv, AbstractGameIv;
    private TextView gameNameTv, downloadCountTv, gameDescriptionTv, AbstractNameTv, AbstractCountTv;
    private RatingBar gameScoreRb, AbstractScoreRb;
    private Button downloadBtn, AbstractDownloadBtn;
    private ProgressBar AbstractPb, downloadPb;
    private DownloadManager downloadManager;
    private boolean flag = true;

    // TODO: Rename and change types and number of parameters
    public static SearchVideoFragment newInstance(String param1, String param2) {
        SearchVideoFragment fragment = new SearchVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        init();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        onRefresh();

        hpageId = 1;
        hotType = HOT_REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new HotVideoAsync(mParam1, "flower", hpageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new HotVideoAsync(mParam1, "flower", hpageId + "").execute();
        }

        npageId = 1;
        newType = NEW_REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new NewVideoAsync(mParam1, "time", npageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new NewVideoAsync(mParam1, "time", npageId + "").execute();
        }

    }

    private void init() {
        hot_dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        new_dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        hotlist = new ArrayList<VideoEntity>();
        hconnectList = new ArrayList<VideoEntity>();
        gameList = new ArrayList<GameEntity>();
        newlist = new ArrayList<VideoEntity>();
//        for (int i=0;i<5;i++){
//            VideoEntity entity=new VideoEntity();
//            entity.setSimg_url("http://img5.imgtn.bdimg.com/it/u=1726668589,450500620&fm=11&gp=0.jpg");
//            entity.setBimg_url("http://www.yyjia.com/attachment/news/20140617/0939511237-1.jpg");
//            entity.setTitle("忍者必须死");
//            entity.setAll_content("忍者必须死，忍者必须死，忍者必须死");
//            entity.setFlower(i+"");
//            entity.setComment(i+"");
//            entity.setTime("01:25");
//            hotlist.add(entity);
//        }
//        for (int i=0;i<5;i++){
//            VideoEntity entity=new VideoEntity();
//            entity.setSimg_url("http://img5.imgtn.bdimg.com/it/u=1726668589,450500620&fm=11&gp=0.jpg");
//            entity.setBimg_url("http://c.hiphotos.baidu.com/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=1f5f57372a34349b600b66d7a8837eab/7e3e6709c93d70cf01b51c20fadcd100baa12b4b.jpg");
//            entity.setTitle("现在战争4");
//            entity.setAll_content("现在战争4，现在战争4，现在战争4");
//            entity.setFlower(i+"");
//            entity.setComment(i+"");
//            entity.setTime("01:25");
//            newlist.add(entity);
//        }
        hotAdapter = new VideoAdapter(getActivity(), hotlist);
        newAdapter = new VideoAdapter(getActivity(), newlist);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video, null);
        hotTv = (TextView) view.findViewById(R.id.video_hot_tv);
        hotTv.setOnClickListener(this);
        timeTv = (TextView) view.findViewById(R.id.video_time_tv);
        timeTv.setOnClickListener(this);
        hotIv = (ImageView) view.findViewById(R.id.video_hot_iv);
        timeIv = (ImageView) view.findViewById(R.id.video_time_iv);

        hotListView = (RefreshListView) view.findViewById(R.id.video_hot_list);
//        gameListView=(ListView)view.findViewById(R.id.fragment_game_fl_game_lv);
//        gameListView.setOnItemClickListener(this);

        hotListView.setAdapter(hotAdapter);

        hotListView.setPullLoadEnable(true);
//        hotListView.setXListViewListener(this);

        hotListView.setXListViewListener(new RefreshListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                hpageId = 1;
                hotType = HOT_REFRESH;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new HotVideoAsync(mParam1, "flower", hpageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new HotVideoAsync(mParam1, "flower", hpageId + "").execute();
                }
            }

            @Override
            public void onLoadMore() {
                hpageId += 1;
                hotType = HOT_LOADMORE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new HotVideoAsync(mParam1, "flower", hpageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new HotVideoAsync(mParam1, "flower", hpageId + "").execute();
                }
            }
        });

        hotListView.setPullRefreshEnable(true);
        hotListView.setOnItemClickListener(this);
        newListView = (RefreshListView) view.findViewById(R.id.video_newest_list);
        newListView.setAdapter(newAdapter);
        newListView.setPullLoadEnable(true);
//        newListView.setXListViewListener(this);
        newListView.setXListViewListener(new RefreshListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                npageId = 1;
                newType = NEW_REFRESH;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new NewVideoAsync(mParam1, "time", npageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new NewVideoAsync(mParam1, "time", npageId + "").execute();
                }
            }

            @Override
            public void onLoadMore() {
                npageId += 1;
                newType = NEW_LOADMORE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new NewVideoAsync(mParam1, "time", npageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new NewVideoAsync(mParam1, "time", npageId + "").execute();
                }
            }
        });
        newListView.setPullRefreshEnable(true);
        newListView.setOnItemClickListener(this);

        //搜索结果帧布局
        video_gameLayout = (FrameLayout) view.findViewById(R.id.fragment_video_game_fl);
        videoLayout = (FrameLayout) view.findViewById(R.id.frament_video_list_fl);
        gameLayout = (FrameLayout) view.findViewById(R.id.fragment_game_fl);
        gameDetailLayout = (FrameLayout) view.findViewById(R.id.fragment_game_detail_fl);
        gameAbstractLayout = (RelativeLayout) view.findViewById(R.id.fragment_game_rl);
        gameAbstractLayout.setOnClickListener(this);

        AbstractGameIv = (ImageView) view.findViewById(R.id.fragment_game_iv);
        AbstractNameTv = (TextView) view.findViewById(R.id.fragment_game_name);
        AbstractCountTv = (TextView) view.findViewById(R.id.fragment_game_downcount_tv);
        AbstractScoreRb = (RatingBar) view.findViewById(R.id.fragment_game_score_rb);
        AbstractDownloadBtn = (Button) view.findViewById(R.id.fragment_game_download_btn);
        AbstractPb = (ProgressBar) view.findViewById(R.id.fragment_game_download_pb);

        frameBackIv = (ImageView) view.findViewById(R.id.fragment_game_fl_back_iv);
        gameDetailIv = (ImageView) view.findViewById(R.id.fragment_game_fl_game_iv);
        gameNameTv = (TextView) view.findViewById(R.id.fragment_game_fl_game_name);
        downloadCountTv = (TextView) view.findViewById(R.id.fragment_game_fl_down_count_tv);
        gameDescriptionTv = (TextView) view.findViewById(R.id.fragment_game_fl_introduction_tv);
        gameScoreRb = (RatingBar) view.findViewById(R.id.fragment_game_fl_score_rb);
        downloadBtn = (Button) view.findViewById(R.id.fragment_game_fl_game_download_btn);
        downloadPb = (ProgressBar) view.findViewById(R.id.fragment_game_fl_download_pb);
        frameBackIv.setOnClickListener(this);
        return view;
    }


//    @Override
//    public void onRefresh() {
//        pageId=1;
//        hotType=HOT_REFRESH;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
//            new HotVideoAsync(mParam1,"flower",pageId+"").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }else{
//            new HotVideoAsync.execute();
//        }
//    }
//
//    @Override
//    public void onLoadMore() {
//
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_time_tv:
                hotListView.setVisibility(View.GONE);
                newListView.setVisibility(View.VISIBLE);
                timeTv.setTextColor(Color.parseColor("#ffffff"));
                timeIv.setBackgroundResource(R.drawable.segmentred_left);
                hotTv.setTextColor(Color.parseColor("#8e8e8e"));
                hotIv.setBackgroundResource(R.drawable.segmentwhite_right);
                break;
            case R.id.video_hot_tv:
                hotListView.setVisibility(View.VISIBLE);
                newListView.setVisibility(View.GONE);
                timeTv.setTextColor(Color.parseColor("#8e8e8e"));
                timeIv.setBackgroundResource(R.drawable.segmentwhite_left);
                hotTv.setTextColor(Color.parseColor("#ffffff"));
                hotIv.setBackgroundResource(R.drawable.segmentred_right);
                break;
            case R.id.fragment_game_fl_back_iv:
                gameDetailLayout.setVisibility(View.GONE);
                video_gameLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_game_rl:
                gameDetailLayout.setVisibility(View.VISIBLE);
                video_gameLayout.setVisibility(View.GONE);
                ExApplication exApplication = new ExApplication(getActivity());
                exApplication.imageLoader.displayImage(gameList.get(0).getGame_icon(),
                        gameDetailIv, exApplication.getOptions());
                gameNameTv.setText(gameList.get(0).getName());
                downloadCountTv.setText(gameList.get(0).getDown_count() + "玩家下载");
                gameDescriptionTv.setText(gameList.get(0).getDescription());
                gameScoreRb.setRating(Float.parseFloat(gameList.get(0).getTotal() + ""));
                break;
            case R.id.fragment_game_download_btn:

                break;
            case R.id.fragment_game_fl_game_download_btn:

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.video_hot_list:
                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra("id", hotlist.get(i - 1).getId());
                getActivity().startActivity(intent);
                break;
            case R.id.video_newest_list:
                intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra("id", newlist.get(i - 1).getId());
                getActivity().startActivity(intent);
                break;
        }
    }

    /**
     * 热门视频
     */
    public class HotVideoAsync extends AsyncTask<Void, Void, String> {

        String page = "";
        String sort = "";
        String key = "";

        public HotVideoAsync(String key, String sort, String page) {
            this.sort = sort;
            this.key = key;
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Object> all = JsonHelper.getSearchVideo(key, sort, page);
            hconnectList = (List<VideoEntity>) all.get(0);
            gameList = (List<GameEntity>) all.get(1);
            if (all != null) {
                if (hconnectList.size() > 0 || gameList.size() > 0) {
                    return "s";
                }
                return "o";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (hotType == HOT_REFRESH) {
                hotListView.setRefreshTime(hot_dateFormat.format(new Date(System.currentTimeMillis())));
                if (s.equals("s")) {
                    if (hconnectList.size() == 0) {
                        videoLayout.setVisibility(View.GONE);
                        gameLayout.setVisibility(View.VISIBLE);
                        if (gameList.size() > 0) {
                            gameAbstractLayout.setVisibility(View.VISIBLE);
                        }
                        ExApplication exApplication = new ExApplication(getActivity());
                        exApplication.imageLoader.displayImage(gameList.get(0).getGame_icon(),
                                AbstractGameIv, exApplication.getOptions());
                        AbstractNameTv.setText(gameList.get(0).getName());
                        AbstractCountTv.setText(gameList.get(0).getDown_count() + "\t玩家下载");
                        AbstractScoreRb.setRating(Float.parseFloat(gameList.get(0).getTotal() + ""));
                        MyDownLoadThread down = new MyDownLoadThread(gameList.get(0).getDown_address(), gameList.get(0).getName(), getActivity());
                        AbstractDownloadBtn.setOnClickListener(down);
                        MyDownLoadThread down1 = new MyDownLoadThread(gameList.get(0).getDown_address(), gameList.get(0).getName(), getActivity());
                        downloadBtn.setOnClickListener(down1);
                        hotListView.setFooterText(1);
                    } else {
                        videoLayout.setVisibility(View.VISIBLE);
                        gameLayout.setVisibility(View.GONE);
                        hotListView.setFooterText(0);
                        hotlist.clear();
                        hotlist.addAll(hconnectList);
                    }


                } else if (s.equals("o")) {
                    ToastUtils.showToast(getActivity(), "无相关数据");
                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (hconnectList.size() == 0) {
                        hotListView.setPullLoadEnable(false);
                        ToastUtils.showToast(getActivity(), "已经加载全部数据");
                    } else {
                        hotListView.setFooterText(0);
                        hotlist.addAll(hconnectList);
                    }

                } else if (s.equals("o")) {
                    ToastUtils.showToast(getActivity(), "无相关数据");
                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
            }
            hotAdapter.notifyDataSetChanged();
            hotListView.stopRefresh();
            hotListView.stopLoadMore();
        }
    }


    /**
     * 最新视频
     */
    public class NewVideoAsync extends AsyncTask<Void, Void, String> {

        String page = "";
        String sort = "";
        String key = "";

        public NewVideoAsync(String key, String sort, String page) {
            this.sort = sort;
            this.key = key;
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Object> all = JsonHelper.getSearchVideo(key, sort, page);
            nconnectList = (List<VideoEntity>) all.get(0);
            if (nconnectList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (newType == NEW_REFRESH) {
                if (s.equals("s")) {
                    newListView.setRefreshTime(new_dateFormat.format(new Date(System.currentTimeMillis())));
                    if (nconnectList.size() == 0) {
                        newListView.setFooterText(1);
                    } else {
                        newListView.setFooterText(0);
                        newlist.clear();
                        newlist.addAll(nconnectList);
                    }


                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (nconnectList.size() == 0) {
                        newListView.setPullLoadEnable(false);
                        ToastUtils.showToast(getActivity(), "已经加载全部数据");
                    } else {
                        newListView.setFooterText(0);
                        newlist.addAll(nconnectList);
                    }

                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
            }
            newAdapter.notifyDataSetChanged();
            newListView.stopRefresh();
            newListView.stopLoadMore();
        }
    }

    /**
     * 游戏下载量统计
     */
    private class DownloadCountTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return JsonHelper.submitDownloadCount(gameList.get(0).getGame_id() + "");
        }
    }

    public class MyDownLoadThread extends Thread implements View.OnClickListener {

        private ProgressBar pBar;
        private Button btn;
        private boolean isStart = false;
        private String url, path, gameName;
        int fileSize;
        int downLoadFileSize;
        String filename;
        Context mContext;

        public Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        AbstractDownloadBtn.setText("下载中..");
                        AbstractPb.setVisibility(View.VISIBLE);
                        downloadBtn.setText("下载中..");
                        downloadPb.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        AbstractPb.setMax(fileSize);
                        AbstractPb.setProgress(downLoadFileSize);
                        downloadPb.setMax(fileSize);
                        downloadPb.setProgress(downLoadFileSize);
                        break;
                    case 2:
                        AbstractDownloadBtn.setText("完成");
                        AbstractPb.setVisibility(View.GONE);
                        downloadBtn.setText("完成");
                        downloadPb.setVisibility(View.GONE);
                        Intent intent = new Intent(MyApplication.getAppContext(), AlertDialog.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("title", "安装提示");
                        intent.putExtra("msg", gameName + "下载完成，是否马上安装？");
                        intent.putExtra("path", path);
                        MyApplication.getAppContext().startActivity(intent);
                        break;
                }

            }

        };

        public MyDownLoadThread(String url, String gameName, Context context) {
            this.url = url;
            this.mContext = context;
            this.gameName = gameName;
        }

        @Override
        public void onClick(View v) {
            if (!isStart) {
                isStart = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new DownloadCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new DownloadCountTask().execute();
                }
                AbstractCountTv.setText((gameList.get(0).getDown_count() + 1) + "\t玩家下载");
                this.start();
            }

        }

        @Override
        public void run() {

            //下载
            while (flag) {
                filename = url.substring(url.lastIndexOf("/") + 1);//获取文件名
                URL myURL = null;
                try {
                    handler.sendEmptyMessage(0);
                    myURL = new URL(url);
                    URLConnection conn = myURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    fileSize = conn.getContentLength();//根据响应获取文件大小
                    if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                    if (is == null) throw new RuntimeException("stream is null");
                    path = Environment.getExternalStorageDirectory() + File.separator + filename;
                    System.out.println("path======" + path);
                    FileOutputStream fos = new FileOutputStream(path);
                    //把数据存入路径+文件名
                    byte buf[] = new byte[1024];
                    downLoadFileSize = 0;
                    do {
                        //循环读取
                        int numread = is.read(buf);
                        if (numread == -1) {
                            break;
                        }
                        fos.write(buf, 0, numread);
                        downLoadFileSize += numread;
                        //更新进度条
                        handler.sendEmptyMessage(1);
                    } while (true);
                    //下载完成
                    handler.sendEmptyMessage(2);
                    flag = false;
                    try {
                        is.close();
                    } catch (Exception ex) {
                        Log.e("tag", "error: " + ex.getMessage(), ex);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }


}
