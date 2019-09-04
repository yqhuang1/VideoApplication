package com.li.videoapplication.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;

import com.li.videoapplication.Adapter.GiftAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.activity.GiftDetailActivity;
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.entity.GiftEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 主页 礼包
 * *
 */
public class GiftFragment2 extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshListView.IXListViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static MainActivity mainActivity;
    private View view;
    private static RefreshListView refreshListView;
    private List<GiftEntity> list;
    private List<GiftEntity> responseList;
    private GiftAdapter adapter;
    //    private ViewFlow viewFlow;
//    private int count = 3;
//    CircleFlowIndicator indic;
    private int asyncType = 0;
    private int pageId;
    private Context context;
    private LayoutInflater inflater;
    //    private AdGiftAdapter adGiftAdapter;
//    private List<Advertisement> adList;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    public static Handler viewhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    refreshListView.setPullRefreshEnable(false);
                    refreshListView.setPullLoadEnable(false);
                    break;
                case 1:
                    refreshListView.setPullRefreshEnable(true);
                    refreshListView.setPullLoadEnable(true);
                    break;
                case 2:

                    break;
            }
        }
    };

    // TODO: Rename and change types and number of parameters
    public static GiftFragment2 newInstance(String param1, String param2) {
        GiftFragment2 fragment = new GiftFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GiftFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        init();
    }

    private void init() {
        context = getActivity();
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        list = new ArrayList<GiftEntity>();
//        adList = new ArrayList<Advertisement>();
        responseList = new ArrayList<GiftEntity>();
        adapter = new GiftAdapter(getActivity(), list, false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        mainActivity = (MainActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_gift, null);
        refreshListView = (RefreshListView) view.findViewById(R.id.gift_list);
        refreshListView.setAdapter(adapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setOnItemClickListener(this);
//        viewFlow = (ViewFlow) view.findViewById(R.id.gift_viewflow);
//        indic = (CircleFlowIndicator) view.findViewById(R.id.gift_viewflowindic);
//        viewFlow.setAdapter(adGiftAdapter);
//        viewFlow.setmSideBuffer(4); // 实际图片张数
//        viewFlow.setFlowIndicator(indic);
//        viewFlow.setTimeSpan(4000);
//        viewFlow.setSelection(1 * 1000);    //设置初始位置
//        viewFlow.startAutoFlowTimer();  //启动自动播放
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            new AdTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            new AdTask().execute();
//        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            new AdTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            new AdTask().execute();
//        }
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGiftTask("", pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGiftTask("", pageId + "").execute();
        }

        if (mainActivity.netState.equals("DISCONNECTED")) {
            ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
            refreshListView.stopRefresh();
            return;
        }
    }

    @Override
    public void onLoadMore() {

        if (mainActivity.netState.equals("DISCONNECTED")) {
            ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
            refreshListView.stopLoadMore();
            return;
        }
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGiftTask("", pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGiftTask("", pageId + "").execute();
        }

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), GiftDetailActivity.class);
        intent.putExtra("gift", list.get(i - 1));
        intent.putExtra("position", i - 1);
        startActivity(intent);
    }

    /**
     * 异步获取 礼包 礼包列表
     */
    private class GetGiftTask extends AsyncTask<Void, Void, String> {

        String nickname = "";
        String page = "";

        public GetGiftTask(String nickname, String page) {
            this.page = page;
            this.nickname = nickname;
        }

        @Override
        protected String doInBackground(Void... params) {
            responseList = JsonHelper.getPakage(context, nickname, page);
            if (responseList != null) {
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
                    list.clear();
                    if (responseList.size() == 0) {
//                        ToastUtils.showToast(getActivity(), "没有找到相关数据");
                    } else {
                        list.addAll(responseList);
                    }
                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (responseList.size() == 0) {
                        ToastUtils.showToast(getActivity(), "已经加载全部数据");
                    } else {
                        list.addAll(responseList);
                    }

                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
            }
            adapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
//            DialogUtils.cancelWaitDialog();
        }
    }

    private class AdTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
//            adList = JsonHelper.getAdvertiseList(getActivity());
//            if (adList != null) {
//                return "s";
//            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(getActivity(), "连接服务器失败");
                return;
            }

            if (s.equals("s")) {
//                adGiftAdapter = new AdGiftAdapter(context, adList);
//                viewFlow.setAdapter(adGiftAdapter);
            }
        }
    }
}
