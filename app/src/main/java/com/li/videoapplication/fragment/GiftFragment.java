package com.li.videoapplication.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;

import com.li.videoapplication.Adapter.RefreshRecyclerAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshItemDecoration;
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

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;

/**
 * 主页 礼包
 * *
 */
public class GiftFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static MainActivity mainActivity;
    private View view;
    private static RecyclerView listView;
    private static SwipeRefreshLayout swipeRefreshView;
    private List<GiftEntity> list;
    private List<GiftEntity> responseList;
    private RefreshRecyclerAdapter adapter;
    private AnimationAdapter animAdapter;


    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
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

    // TODO: Rename and change types and number of parameters
    public static GiftFragment newInstance(String param1, String param2) {
        GiftFragment fragment = new GiftFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public GiftFragment() {
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
        adapter = new RefreshRecyclerAdapter(getActivity(), list, false);

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager = new GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, GridLayoutManager.VERTICAL);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        mainActivity = (MainActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_gift, null);
        swipeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.gift_swipe);

        listView = (RecyclerView) view.findViewById(R.id.gift_list);


        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshView.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        /**
         添加item动画;
         插入或移除item时，item显示的动画效果  */
        listView.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));

        //添加分割线
        listView.addItemDecoration(new RefreshItemDecoration(context, RefreshItemDecoration.VERTICAL_LIST));

        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);
//        setAnimAdapter();

        swipeRefreshView.setOnRefreshListener(this);
        initLoadMoreListener();
        return view;
    }

    /**
     * RecyclerView 滑动时，item的动画展现效果
     */
    private void setAnimAdapter() {
        animAdapter = new AnimationAdapter(adapter) {
            @Override
            protected Animator[] getAnimators(View view) {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
                ObjectAnimator scaleA = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
                return new Animator[]{scaleX, scaleY, scaleA};
            }
        };
        animAdapter.setDuration(500);
        animAdapter.setInterpolator(new OvershootInterpolator());
        animAdapter.setFirstOnly(false);
        listView.setAdapter(animAdapter);

//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter, 0);
//        scaleAdapter.setDuration(500);
//        scaleAdapter.setInterpolator(new OvershootInterpolator());
//        scaleAdapter.setFirstOnly(false);
//        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(scaleAdapter);
//        alphaAdapter.setDuration(500);
//        scaleAdapter.setInterpolator(new OvershootInterpolator());
//        scaleAdapter.setFirstOnly(false);
//        listView.setAdapter(alphaAdapter);
    }


    @Override
    public void onRefresh() {
//        swipeRefreshView.setRefreshing(true);
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGiftTask("", pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGiftTask("", pageId + "").execute();
        }

        if (mainActivity.netState.equals("DISCONNECTED")) {
            ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
            //停止刷新
            swipeRefreshView.setRefreshing(false);
            return;
        }

    }


    private void initLoadMoreListener() {

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {

                    //设置正在加载更多
                    adapter.changeMoreStatus(adapter.LOADING_MORE);

                    if (mainActivity.netState.equals("DISCONNECTED")) {
                        ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
//                        listView.stopLoadMore();
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

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

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
                    ToastUtils.showLongToast(context, dateFormat.format(new Date(System.currentTimeMillis())));
                    list.clear();
                    if (responseList.size() == 0) {
//                        ToastUtils.showToast(getActivity(), "没有找到相关数据");
                    } else {
//                        list.addAll(responseList);
                        adapter.AddHeaderItem(responseList);
                        for (GiftEntity entity : responseList) {
                            System.out.println("entity标题:" + entity.getTitle());
                        }
                        ToastUtils.showToast(getActivity(), "刷新数据成功");
                        //刷新完成
                        swipeRefreshView.setRefreshing(false);
                        setAnimAdapter();

                    }
                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                }
//                // 加载完数据设置为不刷新状态，将下拉进度收起来
//                swipeRefreshView.setRefreshing(false);
            } else {
                if (s.equals("s")) {
                    if (responseList.size() == 0) {
                        ToastUtils.showToast(getActivity(), "已经加载全部数据");
                        //没有更多数据加载了
                        adapter.changeMoreStatus(adapter.NO_LOAD_MORE);
                    } else {//加载更多数据
//                        list.addAll(responseList);
                        adapter.AddFooterItem(responseList);
                        ToastUtils.showToast(getActivity(), "加载更多数据完成");
                        //设置回到上拉加载更多
                        adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
                    }

                } else {
                    ToastUtils.showToast(getActivity(), "连接服务器失败");
                    //设置回到上拉加载更多
                    adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
                }

            }

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
