package com.li.videoapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.FragmentHotActivityAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.grid.StaggeredGridView;
import com.li.videoapplication.activity.ActivityDetailActivity;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HotActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 主页活动 热门活动 分页
 */
public class HotActivityFragment extends Fragment implements View.OnClickListener {
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

    private View headView;
    private LinearLayout layout;
    private TextView textView1, textView2, textView3, textView4;
    private static String[] tvString = new String[4];

    private List<MatchEntity> hotActivityList;
    private List<MatchEntity> hotConnecList;

    private FragmentHotActivityAdapter hotActivityAdapter;

    StaggeredGridView mGridView;

    private boolean mHasRequestedMore;

    private Handler viewhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    layout.setVisibility(View.VISIBLE);
                    textView1.setText(tvString[0]);
                    textView2.setText(tvString[1]);
                    textView3.setText(tvString[2]);
                    textView4.setText(tvString[3]);
                    break;
                case 1:
                    layout.setVisibility(View.GONE);
                    break;

            }
        }
    };

    public static HotActivityFragment newInstance(String param1, String param2) {
        HotActivityFragment fragment = new HotActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HotActivityFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_hot_activity, container, false);

        headView = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_hotactivity_head, null);
        layout = (LinearLayout) headView.findViewById(R.id.fragment_hotactivity_head_layout);
        textView1 = (TextView) headView.findViewById(R.id.fragment_hotactivity_head_Tv1);
        textView1.setOnClickListener(this);
        textView2 = (TextView) headView.findViewById(R.id.fragment_hotactivity_head_Tv2);
        textView2.setOnClickListener(this);
        textView3 = (TextView) headView.findViewById(R.id.fragment_hotactivity_head_Tv3);
        textView3.setOnClickListener(this);
        textView4 = (TextView) headView.findViewById(R.id.fragment_hotactivity_head_Tv4);
        textView4.setOnClickListener(this);

        mGridView = (StaggeredGridView) view.findViewById(R.id.fragment_hot_activity_sGridView);
        mGridView.addHeaderView(headView);

        hotActivityList = new ArrayList<MatchEntity>();
        hotConnecList = new ArrayList<MatchEntity>();

        hotActivityAdapter = new FragmentHotActivityAdapter(context, hotActivityList);
        mGridView.setAdapter(hotActivityAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new HotMatchAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new HotMatchAsync(pageId + "").execute();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.fragment_hotactivity_head_Tv1:
                intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra("id", hotConnecList.get(0).getMatch_id() + "");
                context.startActivity(intent);
                break;
            case R.id.fragment_hotactivity_head_Tv2:
                intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra("id", hotConnecList.get(1).getMatch_id() + "");
                context.startActivity(intent);
                break;
            case R.id.fragment_hotactivity_head_Tv3:
                intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra("id", hotConnecList.get(2).getMatch_id() + "");
                context.startActivity(intent);
                break;
            case R.id.fragment_hotactivity_head_Tv4:
                intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra("id", hotConnecList.get(3).getMatch_id() + "");
                context.startActivity(intent);
                break;
        }
    }

    private void onLoadMoreItems() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new HotMatchAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new HotMatchAsync(pageId + "").execute();
        }
        mHasRequestedMore = false;
    }


    /**
     * 异步获取 热门活动 列表
     */
    public class HotMatchAsync extends AsyncTask<Void, Void, String> {

        String page = "";

        public HotMatchAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            hotConnecList = JsonHelper.getHotMatchList(context, page);
            if (hotConnecList != null) {
                for (int i = 0; i < 4; i++) {
                    tvString[i] = hotConnecList.get(i).getName();
                }
                if (!ExApplication.MEMBER_ID.equals("") && hotConnecList.size() >= 10) {
                    viewhandle.sendEmptyMessage(0);
                } else {
                    viewhandle.sendEmptyMessage(1);
                }
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                hotActivityList.clear();
                hotActivityList.addAll(hotConnecList);
            } else {
                ToastUtils.showToast(context, "连接服务器失败");
            }
            hotActivityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
