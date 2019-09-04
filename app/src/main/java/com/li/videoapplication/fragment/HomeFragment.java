package com.li.videoapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.AdImageAdapter;
import com.li.videoapplication.Adapter.HomeColumnAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.CircleFlowIndicator;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.View.ViewFlow;
import com.li.videoapplication.View.grid.util.DynamicHeightImageView;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.FoundGameActivity;
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.activity.PlayerShowActivity;
import com.li.videoapplication.activity.RecommendActivity;
import com.li.videoapplication.activity.ShowPointActivity;
import com.li.videoapplication.entity.HomeColumnEntity;
import com.li.videoapplication.entity.RecommendEntity;
import com.li.videoapplication.entity.Update;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.TimeUtils;
import com.li.videoapplication.utils.ToastUtils;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * 主页 首页
 */
public class HomeFragment extends Fragment implements OnClickListener, RefreshListView.IXListViewListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static MainActivity mainActivity;
    private View view;
    public static RefreshListView refreshListView;
    private Button toTopBtn;
    private DynamicHeightImageView endcontentIv;
    private LinearLayout layout;
    private ViewFlow viewFlow;
    private TextView moreRecomTv;
    private int count = 3;
    CircleFlowIndicator indic;
    private int pageId = 1;
    private SimpleDateFormat dateFormat = null;
    private AdImageAdapter imageAdapter;

    //主页 首页专栏
    private List<HomeColumnEntity> homeColumn_list;
    private List<HomeColumnEntity> homeColumn_connectList;
    private HomeColumnAdapter homeColumn_adapter;

    private List<RecommendEntity> adList;//广告推荐位
    private Context context;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private static boolean isFirst = true;
    private static boolean isFirstCheck = true;

    private String lastOpenTime;
    private String currentTime;
    private Update update;

    private boolean scrollFlag = false;// 标记是否滑动
    private float lastVisibleItemPosition = 1;// 标记上次滑动位置
    private RelativeLayout mainTabRl;

    private ImageView foundIb, showIb, showPointIb, recommendIb;
    private FrameLayout headFrame;
    private LayoutInflater inflater;
    private String columnName = "";

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
            }
        }
    };

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .penaltyDialog() ////打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
//        .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
//        .penaltyLog()
//        .build());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        init();

        if (SharePreferenceUtil.getPreference(getActivity(), "token").equals("")) {
            SharePreferenceUtil.setPreference(getActivity(), "token", "71e59191aad5f24ca65d5d2e021466b1");
            SharePreferenceUtil.setPreference(getActivity(), "refresh", "04db992f0105f535f29389dfcb617118");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RefreshTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new RefreshTokenTask().execute();
        }

    }


    private void init() {
//        ExApplication.MEMBER_ID="5";
        context = getActivity();
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        adList = new ArrayList<RecommendEntity>();

        homeColumn_list = new ArrayList<HomeColumnEntity>();
        homeColumn_connectList = new ArrayList<HomeColumnEntity>();
        homeColumn_adapter = new HomeColumnAdapter(context, homeColumn_list);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        mainActivity = (MainActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_home, null);
        refreshListView = (RefreshListView) view.findViewById(R.id.home_list);
        toTopBtn = (Button) view.findViewById(R.id.home_top_btn);
        toTopBtn.setOnClickListener(this);

        View view2 = inflater.inflate(R.layout.head, null);
        headFrame = (FrameLayout) view2.findViewById(R.id.framelayout);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width / 2);
        headFrame.setLayoutParams(layoutParams);
        foundIb = (ImageView) view2.findViewById(R.id.home_found_iv);
        foundIb.setOnClickListener(this);
        showIb = (ImageView) view2.findViewById(R.id.home_show_iv);
        showIb.setOnClickListener(this);
        showPointIb = (ImageView) view2.findViewById(R.id.home_showpoint_iv);
        showPointIb.setOnClickListener(this);
        recommendIb = (ImageView) view2.findViewById(R.id.home_recommend_iv);
        recommendIb.setOnClickListener(this);

        layout = (LinearLayout) view2.findViewById(R.id.layout);
        viewFlow = (ViewFlow) view2.findViewById(R.id.viewflow);
        indic = (CircleFlowIndicator) view2.findViewById(R.id.viewflowindic);

        viewFlow.setAdapter(imageAdapter);

        viewFlow.setmSideBuffer(4); // 实际图片张数

        viewFlow.setFlowIndicator(indic);
        viewFlow.setTimeSpan(4000);
        viewFlow.setSelection(0);    //设置初始位置
        viewFlow.startAutoFlowTimer();  //启动自动播放

        View view3 = inflater.inflate(R.layout.end, null);
        endcontentIv = (DynamicHeightImageView) view3.findViewById(R.id.home_end_content);
        endcontentIv.setHeightRatio(0.27);

        //首页专栏
        refreshListView.addHeaderView(view2);
        refreshListView.addFooterView(view3);
        refreshListView.setAdapter(homeColumn_adapter);
        refreshListView.setOnScrollListener(new PauseOnScrollListener
                (ExApplication.imageLoader, true, true));//滑动暂停加载图片

        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);


        //首页底部Tab隐藏及Top按钮的应用
        refreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://当屏幕停止滚动时
                        scrollFlag = false;
                        //判断列表位置
                        if (refreshListView.getLastVisiblePosition() >= 3) {
                            toTopBtn.setVisibility(View.VISIBLE);
                        }
                        //判断滚动到顶部
//                        if (refreshListView.getLastVisiblePosition()<=3){
//                            toTopBtn.setVisibility(View.GONE);
//                            mainActivity.onShowTab();
//                        }
                        if (refreshListView.getFirstVisiblePosition() <= 0) {//滚动到顶部?
                            toTopBtn.setVisibility(View.GONE);
                            mainActivity.onShowTab();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                        scrollFlag = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
                if (scrollFlag) {
                    System.out.println("firstVisibleItem=======" + firstVisibleItem);
                    System.out.println("lastVisibleItemPosition=======" + lastVisibleItemPosition);
                    if (firstVisibleItem > lastVisibleItemPosition) {// 上滑
                        mainActivity.onHiddenTab();
                    } else if (firstVisibleItem < lastVisibleItemPosition) {// 下滑
                        mainActivity.onShowTab();
                    } else {
                        return;
                    }
                    if (firstVisibleItem != 0) {
                        lastVisibleItemPosition = firstVisibleItem;
                    }
                }
            }
        });

        refreshListView.setVisibility(View.GONE);
        DialogUtils.createLoadingDialog(context, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AdTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new AdTask().execute();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new getHomeColumnAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new getHomeColumnAsync(pageId + "").execute();
        }

        return view;
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserEntity userInfo = SharePreferenceUtil.getUserEntity(getActivity());
        if (userInfo != null) {
            ExApplication.MEMBER_ID = userInfo.getId();
            Log.e("id", ExApplication.MEMBER_ID);
        }

    }

    @Override
    public void onRefresh() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AdTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new AdTask().execute();
        }

        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new getHomeColumnAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new getHomeColumnAsync(pageId + "").execute();
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
            new getHomeColumnAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new getHomeColumnAsync(pageId + "").execute();
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.home_top_btn:
                refreshListView.setSelectionFromTop(0, 0);//返回列表顶部
                toTopBtn.setVisibility(View.GONE);
                break;
            case R.id.home_found_iv://找游戏
                intent = new Intent(context, FoundGameActivity.class);
                startActivity(intent);
                ExApplication.upUmenEventValue(context, "找游戏次数", "find_game_count");
                break;
            case R.id.home_show_iv://玩家秀
                intent = new Intent(context, PlayerShowActivity.class);
                startActivity(intent);
                ExApplication.upUmenEventValue(context, "玩家秀次数", "player_show_count");
                break;
            case R.id.home_showpoint_iv://小编荐
                intent = new Intent(context, ShowPointActivity.class);
                startActivity(intent);
                ExApplication.upUmenEventValue(context, "小编荐次数", "editor_recommend_count");
                break;
            case R.id.home_recommend_iv://抢福利
                intent = new Intent(context, RecommendActivity.class);
                startActivity(intent);
                ExApplication.upUmenEventValue(context, "抢福利次数", "rob_welfare_count");
                break;
        }
    }


    /**
     * 检测新版本
     */
    public void doUpdateCheck() {
        if ("open".equals(SharePreferenceUtil.getPreference(context, "isMessageOpen"))) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            currentTime = simpleDateFormat.format(date);
            lastOpenTime = SharePreferenceUtil.getPreference(context, "isFirstDaysOpen");
            if (lastOpenTime == "") {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new UpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new UpdateTask().execute();
                }
            } else {
//                SharePreferenceUtil.setPreference(context, "isFirstDaysOpen", currentTime);
                if (TimeUtils.isFirstDaysOpen(lastOpenTime, currentTime)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new UpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new UpdateTask().execute();
                    }
                }
            }
        }
    }

    /**
     * 异步获取 主页首页 专栏列表
     * 含：
     * 热门视频
     * 视界专栏 （舞大大学堂/小小舞玩新游/阿沫爱品评）
     * 热门游戏、热门分类
     */
    public class getHomeColumnAsync extends AsyncTask<Void, Void, String> {
        String page = "";

        public getHomeColumnAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            homeColumn_connectList = JsonHelper.getHomeColumnList(context, page);
            if (homeColumn_connectList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            refreshListView.setVisibility(View.VISIBLE);
            if (isFirstCheck) {
                mainActivity.doNetworkTypeCheck();
                doUpdateCheck();
                isFirstCheck = false;
            }

            if (s.equals("s")) {
                if (asyncType == REFRESH) {
                    refreshListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    endcontentIv.setVisibility(View.GONE);
                    refreshListView.onVisibleFooterView();
                    homeColumn_list.clear();
                }
                if (homeColumn_connectList.size() == 0) {
                    ToastUtils.showToast(getActivity(), "已经加载全部数据");
                    endcontentIv.setVisibility(View.VISIBLE);
                    refreshListView.onHiddenFooterView();
                    toTopBtn.setVisibility(View.VISIBLE);
                    mainActivity.onShowTab();
                } else {
                    homeColumn_list.addAll(homeColumn_connectList);
                }
            } else {
                ToastUtils.showToast(getActivity(), "加载失败，请检查网络");
            }
            homeColumn_adapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }

    /**
     * 获取首页广告位 异步方法
     */
    private class AdTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            adList = JsonHelper.getHomeAdList(context);
            if (adList != null) {
                Set adIdSet = new HashSet();
                for (RecommendEntity entity : adList) {
                    adIdSet.add(entity.getVideo_id());
                }
                SharePreferenceUtil.setHomeAdId(context, adIdSet);
                return "s";
            }
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
                imageAdapter = new AdImageAdapter(context, adList);
                viewFlow.setAdapter(imageAdapter);
            }
            DialogUtils.cancelLoadingDialog();
        }
    }

    public class RefreshTokenTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {

            return JsonHelper.refreshToken(getActivity());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Log.e("---------", "refresh");
            }
            super.onPostExecute(aBoolean);
        }
    }

    /**
     * 版本更新自动检测
     */
    private class UpdateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            update = JsonHelper.getUpdate(context);
            if (update != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                return;
            }
            if (s.equals("s")) {
                SharePreferenceUtil.setPreference(context, "isFirstDaysOpen", currentTime);
                if ("U".equals(update.getUpdate_flag())) {
                    String changelog = update.getChange_log();
                    String[] changeArray = changelog.split(";");
                    changelog = "";
                    for (int i = 0; i < changeArray.length; i++) {
                        if (i != changeArray.length) {
                            changelog += changeArray[i] + "\n";
                        } else {
                            changelog += changeArray[i];
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("更新提示");
                    builder.setMessage("手游视界" + update.getVersion_str() + "更新日志：\n" +
                            changelog);
                    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(update.getUpdate_url());
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(it);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                } else if ("A".equals(update.getUpdate_flag())) {
                    String changelog = update.getChange_log();
                    String[] changeArray = changelog.split(";");
                    changelog = "";
                    for (int i = 0; i < changeArray.length; i++) {
                        if (i != changeArray.length) {
                            changelog += changeArray[i] + "\n";
                        } else {
                            changelog += changeArray[i];
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("必须更新");
                    builder.setMessage("手游视界" + update.getVersion_str() + "更新日志：\n" +
                            changelog);
                    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(update.getUpdate_url());
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(it);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    builder.create().show();
                } else {
//                    Toast.makeText(context,"当前已经是最新版本", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        DialogUtils.cancelLoadingDialog();//防止APP奔溃后重新调用加载对话框
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
