package com.li.videoapplication.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.li.videoapplication.Adapter.FragmentMyActivityAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.utils.JsonHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MyActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 主页活动 我的活动 分页
 */
public class MyActivityFragment extends Fragment implements RefreshListView.IXListViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private int pageId = 1;
    private Context context;

    private List<MatchEntity> myActivityList;
    private List<MatchEntity> myConnecList;

    private FragmentMyActivityAdapter myActivityAdapter;

    RefreshListView mListView;
    TextView emptyTv;

    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    private Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mListView.refreshVisible();
                    break;
            }
        }
    };

    public static MyActivityFragment newInstance(String param1, String param2) {
        MyActivityFragment fragment = new MyActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyActivityFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_activity, container, false);
        mListView = (RefreshListView) view.findViewById(R.id.fragment_my_activity_rListView);
        emptyTv = (TextView) view.findViewById(R.id.fragment_my_activity_empty);

        myActivityList = new ArrayList<MatchEntity>();
        myConnecList = new ArrayList<MatchEntity>();

        myActivityAdapter = new FragmentMyActivityAdapter(context, myActivityList);
        mListView.setAdapter(myActivityAdapter);
        mListView.setXListViewListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new MyMatchAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new MyMatchAsync().execute();
        }

        return view;
    }

    @Override
    public void onRefresh() {
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new MyMatchAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new MyMatchAsync().execute();
        }
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 异步获取 我的活动 列表
     */
    public class MyMatchAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            myConnecList = JsonHelper.getMyMatchList();
            if (myConnecList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                mListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                myActivityList.clear();
                myActivityList.addAll(myConnecList);
            } else {
//                ToastUtils.showToast(context, "没有参加活动的数据");
                emptyTv.setVisibility(View.VISIBLE);
            }
            myActivityAdapter.notifyDataSetChanged();
            mListView.stopRefresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewHandler.post(runnable);
    }

    //刷新UI
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewHandler.sendEmptyMessage(0);
        }
    };
}
