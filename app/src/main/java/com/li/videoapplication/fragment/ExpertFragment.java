package com.li.videoapplication.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.li.videoapplication.Adapter.ExpertAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.PersonalInfoActivity;
import com.li.videoapplication.entity.ExpertEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpertFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshListView.IXListViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private RefreshListView refreshListView;
    private TextView rankTv;
    private List<ExpertEntity> list;
    private List<ExpertEntity> responseList;
    private ExpertAdapter adapter;
    private int asyncType = 0;
    private int pageId;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    // TODO: Rename and change types and number of parameters
    public static ExpertFragment newInstance(String param1, String param2) {
        ExpertFragment fragment = new ExpertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        Log.e("param1", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpertFragment() {
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
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        list = new ArrayList<ExpertEntity>();
        responseList = new ArrayList<ExpertEntity>();
        adapter = new ExpertAdapter(getActivity(), list);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expert, null);
        refreshListView = (RefreshListView) view.findViewById(R.id.expert_list);
        refreshListView.setAdapter(adapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setOnItemClickListener(this);

        rankTv = (TextView) view.findViewById(R.id.fragment_expert_rank_tv);
        if (ExApplication.MEMBER_ID.equals("")){
            rankTv.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetRankTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetRankTask().execute();
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
    }

    @Override
    public void onRefresh() {

        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetExpertListTask(mParam1, pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetExpertListTask(mParam1, pageId + "").execute();
        }
    }

    @Override
    public void onLoadMore() {

        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetExpertListTask(mParam1, pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetExpertListTask(mParam1, pageId + "").execute();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
        intent.putExtra("flag", "videoplay");
        intent.putExtra("member_id", list.get(i - 1).getMember_id());
        startActivity(intent);
    }

    /**
     * 异步获取
     */
    private class GetExpertListTask extends AsyncTask<Void, Void, String> {

        String page = "";

        public GetExpertListTask(String nickname, String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            responseList = JsonHelper.getExpertList(ExApplication.MEMBER_ID, mParam1, page);
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
//                        ToastUtils.showToast(getActivity(),"没有找到相关数据");
                        refreshListView.setFooterText(1);
                    } else {
                        refreshListView.setFooterText(0);
                        list.addAll(responseList);
                    }
                } else {
                    ToastUtils.showToast(getActivity(), "获取数据失败");
                }
            } else {
                if (s.equals("s")) {
                    if (responseList.size() == 0) {
                        refreshListView.setPullLoadEnable(false);
                        ToastUtils.showToast(getActivity(), "到底啦");
                    } else {
                        refreshListView.setFooterText(0);
                        list.addAll(responseList);
                    }

                } else {
                    ToastUtils.showToast(getActivity(), "到底啦");
                }
            }
            adapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();

        }
    }

    /**
     * 个人排名
     */
    private class GetRankTask extends AsyncTask<Void, Void, String> {
        String rank;
        @Override
        protected String doInBackground(Void... params) {
            rank = JsonHelper.getExpertRank(mParam1);
            if (rank != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            rankTv.setText(rank);
        }
    }
}
