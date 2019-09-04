package com.li.videoapplication.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.li.videoapplication.Adapter.CollectAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feimoyuangong on 2015/6/29.
 * 视频管理 已收藏
 */
public class CollectVideoFragment extends Fragment implements RefreshListView.IXListViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static Context context;

    /**
     * 当前云端收藏视频数据
     */
    public static List<VideoEntity> videoList = null;

    /**
     * 云端收藏视频数据(新加载的数据)
     */
    private static List<VideoEntity> connecList = null;

    /**
     * 已选择，将要操作的云端收藏视频列表
     */
    public static List<VideoEntity> collectVideoListCheckToDel;

    private static RefreshListView refreshListView;
    public static CollectAdapter collectAdapter;
    private View view;

    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private int page = 1;
    private SimpleDateFormat dateFormat = null;


    public static Handler collectVideoHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:// 取消全选
                    for (int i = 0; i < videoList.size(); i++) {
                        CollectAdapter.ListDelcheck.set(i, false);
                        collectVideoListCheckToDel.clear();
                    }
                    collectAdapter.notifyDataSetChanged();
                    break;

                case 2:

                    break;

                case 3://取消按钮
                    collectVideoListCheckToDel.clear();
                    collectAdapter.notifyDataSetChanged();
                    // 开放上拉下拉刷新功能
                    refreshListView.closeRefresh(false);
                    break;

                case 4://编辑按钮
                    // 关闭上拉下拉刷新功能
                    refreshListView.closeRefresh(true);

                    break;

                case 5://确定删除
                    // 开放上拉下拉刷新功能
                    collectAdapter.notifyDataSetChanged();
                    refreshListView.closeRefresh(false);
                    break;

                case 6://删除按钮

                    break;

                case 7://确定删除
                    break;
            }
        }
    };

    // TODO: Rename and change types and number of parameters
    public static CollectVideoFragment newInstance(String param1, String param2) {
        CollectVideoFragment fragment = new CollectVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CollectVideoFragment() {
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

        // 已选择，将要操作的云端收藏视频列表
        collectVideoListCheckToDel = new ArrayList<VideoEntity>();
        // 当前云端收藏视频数据列表
        videoList = new ArrayList<VideoEntity>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_collect_video, null);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        videoList = new ArrayList<VideoEntity>();
        connecList = new ArrayList<VideoEntity>();
        collectAdapter = new CollectAdapter(context, videoList);

        refreshListView = (RefreshListView) view.findViewById(R.id.fragment_collect_video_rlv);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setAdapter(collectAdapter);

        onRefresh();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        page = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetCollectVideoListTask(page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCollectVideoListTask(page + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        page += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetCollectVideoListTask(page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCollectVideoListTask(page + "").execute();
        }
    }


    /**
     * 异步获取 已收藏 云端视频列表
     */
    private class GetCollectVideoListTask extends AsyncTask<Void, Void, String> {

        String page;

        public GetCollectVideoListTask(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getCollectList(context, ExApplication.MEMBER_ID, page);
            if (connecList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    refreshListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    videoList.clear();
                    videoList.addAll(connecList);
                } else {
//                    ToastUtils.showToast(context, "获取数据失败");
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "到底啦！");
                    } else {
                        videoList.addAll(connecList);
                    }

                } else {
                    ToastUtils.showToast(context, "到底啦！");
                }
            }
            collectAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }

    }

}
