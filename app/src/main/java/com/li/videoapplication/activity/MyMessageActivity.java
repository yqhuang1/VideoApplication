package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.li.videoapplication.Adapter.MessageAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.MessageEntity;
import com.li.videoapplication.utils.JsonHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feimoyuangong on 2015/6/10.
 * <p/>
 * 我的消息 页面
 */
public class MyMessageActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshListView.IXListViewListener {

    private ImageButton backBtn;
    private RefreshListView refreshListView;
    private List<MessageEntity> list;
    private List<MessageEntity> responseList;
    private MessageAdapter adapter;
    private int asyncType = 0;
    private int pageId;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_message);
        init();
    }

    private void init() {
        list = new ArrayList<MessageEntity>();
        responseList = new ArrayList<MessageEntity>();
        adapter = new MessageAdapter(getApplicationContext(), list);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        backBtn = (ImageButton) findViewById(R.id.activity_message_back);
        backBtn.setOnClickListener(this);
        refreshListView = (RefreshListView) findViewById(R.id.activity_message_list);
        refreshListView.setAdapter(adapter);
        refreshListView.setPullLoadEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setOnItemClickListener(this);
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_message_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMessageListTask("2", ExApplication.MEMBER_ID, pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMessageListTask("2", ExApplication.MEMBER_ID, pageId + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        pageId += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMessageListTask("2", ExApplication.MEMBER_ID, pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetMessageListTask("2", ExApplication.MEMBER_ID, pageId + "").execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        if (list.get(position - 1).getFlag().equals("0")) {
            intent = new Intent(MyMessageActivity.this, VideoPlayActivity.class);
            intent.putExtra("id", list.get(position - 1).getVideo_id());
            startActivity(intent);
        } else {
            intent = new Intent(MyMessageActivity.this, PersonalInfoActivity.class);
            intent.putExtra("flag", "persion");
            intent.putExtra("toComment", true);
            startActivity(intent);
        }
    }

    /**
     * 异步获取 我的信息 列表
     */
    private class GetMessageListTask extends AsyncTask<Void, Void, String> {

        String type_id = "";
        String member_id = "";
        String page = "";

        public GetMessageListTask(String type_id, String member_id, String page) {
            this.type_id = type_id;
            this.member_id = member_id;
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            responseList = JsonHelper.getMessageList(type_id, member_id, page);
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
//                        ToastUtils.showToast(MyMessageActivity.this, "没有新消息");
                    } else {
                        list.addAll(responseList);
                    }
                } else {
//                    ToastUtils.showToast(MyMessageActivity.this, "加载失败");
                }
            } else {
                if (s.equals("s")) {
                    if (responseList.size() == 0) {
//                        ToastUtils.showToast(MyMessageActivity.this,"已经加载全部");
                    } else {
                        list.addAll(responseList);
                    }

                } else {
//                    ToastUtils.showToast(MyMessageActivity.this,"加载失败");
                }
            }
            adapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
//            DialogUtils.cancelWaitDialog();
        }
    }
}
