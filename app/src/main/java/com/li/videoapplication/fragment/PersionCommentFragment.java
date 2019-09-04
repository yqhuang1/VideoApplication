package com.li.videoapplication.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.li.videoapplication.Adapter.CommentAdapter;
import com.li.videoapplication.Adapter.FaceAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.CommentEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 个人资料 页面 留言
 */
public class PersionCommentFragment extends Fragment implements View.OnClickListener, RefreshListView.IXListViewListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RefreshListView refreshListView;
    private List<CommentEntity> commentEntityList;
    private List<CommentEntity> connList;
    private CommentAdapter commentAdapter;
    private Context context;
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;
    private int page;
    private View view;

    //评论
    private Button faceBtn;
    private TextView submitTv;
    private EditText commentEdt;
    private GridView gridFace = null;
    private boolean hasFace = false;
    private List<Integer> faceList = null;//表情的资源ID集合集合
    private FaceAdapter faceAdapter = null;//表情适配器

    public static PersionCommentFragment newInstance(String param1, String param2) {
        PersionCommentFragment fragment = new PersionCommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PersionCommentFragment() {
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
        commentEntityList = new ArrayList<CommentEntity>();
        commentAdapter = new CommentAdapter(getActivity(), commentEntityList, "person", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_persion_comment, null);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        page = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetCommentAsync(mParam1, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCommentAsync(mParam1, page + "").execute();
        }
    }

    public void initView(View view) {
        submitTv = (TextView) view.findViewById(R.id.play_sumbit);
        submitTv.setOnClickListener(this);
        faceBtn = (Button) view.findViewById(R.id.fragment_persion_comment_face_btn);
        faceBtn.setOnClickListener(this);
        gridFace = (GridView) view.findViewById(R.id.gridview_face);
        commentEdt = (EditText) view.findViewById(R.id.fragment_persion_comment_edt);
        commentEdt.setOnClickListener(this);
        commentEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    gridFace.setVisibility(View.GONE);
                    faceBtn.setBackgroundResource(R.drawable.face_nomal);
                    hasFace = false;
                }
            }
        });

        refreshListView = (RefreshListView) view.findViewById(R.id.id_stickynavlayout_innerscrollview);
        refreshListView.setAdapter(commentAdapter);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setPullLoadEnable(true);

        refreshListView.setXListViewListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_sumbit:

                if (TextUtils.isEmpty(commentEdt.getText().toString().trim())) {
                    ToastUtils.showToast(context, "评论不能为空");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new SubmitPersonTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new SubmitPersonTask().execute();
                }

                //取消输入框焦点
                commentEdt.clearFocus();
                gridFace.setVisibility(View.GONE);
                faceBtn.setBackgroundResource(R.drawable.face_nomal);
                hasFace = false;
                break;
            case R.id.fragment_persion_comment_face_btn:
                InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFace == false) {
                    //在点击表情按钮后隐藏软键盘
                    im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    int resouseId = 0;
                    faceList = new ArrayList<Integer>();
                    Field field;
//                    String[] faceArray = getResources().getStringArray(R.array.faceArray);
                    String[] faceArray = getResources().getStringArray(R.array.expressionArray);
                    for (int i = 0; i < 34; i++) {
                        try {
                            // 从R.drawable类中获得相应资源ID（静态变量）的Field对象
                            field = R.drawable.class.getDeclaredField(faceArray[i]);
                            resouseId = Integer.parseInt(field.get(null).toString());
                            faceList.add(resouseId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    faceAdapter = new FaceAdapter(context, faceList, commentEdt);
                    gridFace.setAdapter(faceAdapter);
                    gridFace.setVisibility(View.VISIBLE);
                    faceBtn.setBackgroundResource(R.drawable.face_touch);
                    hasFace = true;
                } else {
                    //再一次点击表情按钮显示键盘隐藏表情
                    im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    gridFace.setVisibility(View.GONE);
                    faceBtn.setBackgroundResource(R.drawable.face_nomal);
                    hasFace = false;
                }
                break;
            case R.id.fragment_persion_comment_edt:
                hasFace = false;
                gridFace.setVisibility(View.GONE);
                faceBtn.setBackgroundResource(R.drawable.face_nomal);
                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetCommentAsync(mParam1, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCommentAsync(mParam1, page + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        page += 1;
        asyncType = LOADMORE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetCommentAsync(mParam1, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCommentAsync(mParam1, page + "").execute();
        }
    }

    /**
     * 获取评论列表
     */
    private class GetCommentAsync extends AsyncTask<Void, Void, String> {
        String id = "";
        String page = "";

        public GetCommentAsync(String id, String page) {
            this.id = id;
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            connList = JsonHelper.getPersonCommentList(id, page, ExApplication.MEMBER_ID);
            Log.e("conList", connList + "1");
            if (connList != null) {
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
                    commentEntityList.clear();
                    commentEntityList.addAll(connList);
                } else {
//                    ToastUtils.showToast(context, "加载失败");
                }
            } else {
                if (s.equals("s")) {
                    if (connList.size() == 0) {
                        ToastUtils.showToast(context, "无更多评论");
                    } else {
                        commentEntityList.addAll(connList);
                    }
                } else {
//                    ToastUtils.showToast(context, "加载失败");
                }
            }
            commentAdapter.notifyDataSetChanged();
            refreshListView.stopRefresh();
            refreshListView.stopLoadMore();
        }
    }

    /**
     * 提交评论
     */
    private class SubmitPersonTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.submitPersonComment(ExApplication.MEMBER_ID, mParam1, commentEdt.getText().toString().trim());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                ToastUtils.showToast(context, "评论成功");
                commentEdt.setText("");
                onRefresh();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentEdt.getWindowToken(), 0);
            } else {
                ToastUtils.showToast(context, "评论失败,请登录后再重试");
            }
        }
    }


}
