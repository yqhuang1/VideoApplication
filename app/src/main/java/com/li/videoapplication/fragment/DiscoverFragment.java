package com.li.videoapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.li.videoapplication.Adapter.DiscoverVideoListAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExpertActivity;
import com.li.videoapplication.activity.GameCircleActivity;
import com.li.videoapplication.activity.MasterColumnActivity;
import com.li.videoapplication.activity.RelaxeActivity;
import com.li.videoapplication.entity.DiscoverVideoEntity;
import com.li.videoapplication.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页 发现
 * *
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    //-------------------------------------------
    private ListView listView;
    private List<DiscoverVideoEntity> videoList;
    private DiscoverVideoListAdapter videoListAdapter;
    private Context context;
    private TextView changeTv;
    private ImageView expertIv, masterIv, relaxeIv, gameCircleIv;


    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DiscoverFragment() {
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
        videoList = new ArrayList<DiscoverVideoEntity>();
        videoListAdapter = new DiscoverVideoListAdapter(context, videoList);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discover, null);
        listView = (ListView) view.findViewById(R.id.fragment_discover_list);

        View view2 = inflater.inflate(R.layout.fragment_discovery_head, null);

        changeTv = (TextView) view2.findViewById(R.id.fragment_discover_change_tv);
        changeTv.setOnClickListener(this);
        expertIv = (ImageView) view2.findViewById(R.id.fragment_discover_expert_iv);
        expertIv.setOnClickListener(this);
        masterIv = (ImageView) view2.findViewById(R.id.fragment_discover_master_iv);
        masterIv.setOnClickListener(this);
        relaxeIv = (ImageView) view2.findViewById(R.id.fragment_discover_relaxe_iv);
        relaxeIv.setOnClickListener(this);
        gameCircleIv = (ImageView) view2.findViewById(R.id.fragment_discover_game_circle_iv);
        gameCircleIv.setOnClickListener(this);

        listView.addHeaderView(view2);
        listView.setAdapter(videoListAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetVideoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetVideoAsync().execute();
        }
        return view;
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.fragment_discover_change_tv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetVideoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetVideoAsync().execute();
                }
                break;
            case R.id.fragment_discover_expert_iv://达人榜
                intent = new Intent(context, ExpertActivity.class);
                startActivity(intent);
                break;
            case R.id.fragment_discover_master_iv://大神专栏
                intent = new Intent(context, MasterColumnActivity.class);
                startActivity(intent);
                break;
            case R.id.fragment_discover_relaxe_iv://轻松一刻
                intent = new Intent(context, RelaxeActivity.class);
                startActivity(intent);
                break;
            case R.id.fragment_discover_game_circle_iv://游戏圈子
                intent = new Intent(context, GameCircleActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 异步获取 发现页面 （大家还在看）视频列表
     */
    public class GetVideoAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            videoList = JsonHelper.getDiscoverVideoList(context);
            if (videoList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                videoListAdapter.update(videoList);
            }
        }
    }

}

