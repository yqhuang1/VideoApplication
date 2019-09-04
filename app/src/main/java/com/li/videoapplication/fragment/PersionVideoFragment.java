package com.li.videoapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.li.videoapplication.Adapter.HomeAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 个人资料 页面 作品
 */
public class PersionVideoFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private HomeAdapter uploadAdapter;
    private List<VideoEntity> uploadList;
    private List<VideoEntity> connecList;
    private RefreshListView uploadListView;
//    private ListView uploadLv;
    private Context context;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;
    private int page;
    private View view;

    public static PersionVideoFragment newInstance(String param1, String param2) {
        PersionVideoFragment fragment = new PersionVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PersionVideoFragment() {
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
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        connecList = new ArrayList<VideoEntity>();
        uploadList = new ArrayList<VideoEntity>();
        uploadAdapter = new HomeAdapter(context, uploadList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_hot, null);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        page = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetUploadListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetUploadListTask().execute();
        }
    }

    public void initView(View view) {
//        uploadLv = (ListView)view.findViewById(R.id.id_stickynavlayout_innerscrollview);
//        uploadLv.setAdapter(uploadAdapter);
//        uploadLv.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//
//            }
//        });

        uploadListView = (RefreshListView) view.findViewById(R.id.id_stickynavlayout_innerscrollview);
        uploadListView.setAdapter(uploadAdapter);
        uploadListView.setPullRefreshEnable(true);
        uploadListView.setPullLoadEnable(true);

        uploadListView.setXListViewListener(new RefreshListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                page = 1;
                asyncType = REFRESH;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetUploadListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetUploadListTask().execute();
                }
            }

            @Override
            public void onLoadMore() {
                page += 1;
                asyncType = LOADMORE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetUploadListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetUploadListTask().execute();
                }
            }
        });
        uploadListView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("id", uploadList.get(i - 1).getId());
        context.startActivity(intent);
    }

    /**
     * 异步获取 个人（上传）视频 列表
     */
    public class GetUploadListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            connecList = JsonHelper.getUploadList(page + "", mParam1);
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
//                    uploadListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    uploadList.clear();
                    uploadList.addAll(connecList);
                }
            } else {
                if (s.equals("s")) {
                    if (connecList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        uploadList.addAll(connecList);
                    }

                } else {
                    ToastUtils.showToast(context, "没有更多数据");
                }
            }
            uploadAdapter.notifyDataSetChanged();
            uploadListView.stopRefresh();
            uploadListView.stopLoadMore();
        }
    }
}
