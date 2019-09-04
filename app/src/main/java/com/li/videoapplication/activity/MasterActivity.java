package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.Adapter.CommentAdapter;
import com.li.videoapplication.Adapter.FaceAdapter;
import com.li.videoapplication.Adapter.HomeAdapter;
import com.li.videoapplication.Adapter.MasterUpdateVideoAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.CircularImage;
import com.li.videoapplication.View.RefreshListView;
import com.li.videoapplication.entity.CommentEntity;
import com.li.videoapplication.entity.MasterEntity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.DialogUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by feimoyuangong on 2015/6/30.
 * 大神信息 页面
 */
public class MasterActivity extends Activity implements View.OnClickListener, RefreshListView.IXListViewListener {

    private Context context;
    private LayoutInflater inflater;
    private ImageButton backBtn;
    private RelativeLayout videoLayout;
    private LinearLayout commentLayout;

    //大神更新
    private RefreshListView updateVideoReListView;
    private MasterUpdateVideoAdapter updateVideoAdapter;
    private List<VideoEntity> updateVideo_list;
    private List<VideoEntity> updateVideo_connectList;

    //给（大神）TA留言
    private RefreshListView messageListView;
    private List<CommentEntity> comment_list;
    private List<CommentEntity> comment_connectList;
    private CommentAdapter commentAdapter;

    private ListView videoListView;
    private HomeAdapter videoAdapter; //四个视频适配器
    private List<VideoEntity> videoList; //四个视频集合
    private List<Object> headList;
    private MasterEntity masterEntity;
    private String id;
    private ExApplication exApplication;
    private int pageId;//大神更新页标
    private int page;//评论列表页标
    private int asyncType = 0;
    private static final int REFRESH = 0;
    private static final int LOADMORE = 1;
    private SimpleDateFormat dateFormat = null;
    private String state = "video";

    //////头部布局/////
    private CircularImage headImg;
    private TextView priseNumTv, fansTv, nameTv, gameTv, hobbyTv, gameCareerTv, mottoTv;
    private TextView videoTv, messageTv;
    private RelativeLayout priseRl, focusRl, shareRl;
    private ImageView priseIv, focusIv;
    private TextView priseTv, focusTv;
    private ImageView videoIv, messageIv;
    private RelativeLayout hotRl;

    //评论
    private Button faceBtn;
    private TextView submitTv;
    private EditText commentEdt;
    private GridView gridFace = null;
    private boolean hasFace = false;
    private List<Integer> faceList = null;//表情的资源ID集合集合
    private FaceAdapter faceAdapter = null;//表情适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_master);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init();
        initView();
    }

    private void init() {
        context = MasterActivity.this;
        inflater = LayoutInflater.from(context);
        id = getIntent().getStringExtra("id");
        exApplication = new ExApplication(context);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        ShareSDK.initSDK(this);

        comment_list = new ArrayList<CommentEntity>();
        comment_connectList = new ArrayList<CommentEntity>();
        commentAdapter = new CommentAdapter(context, comment_list, "person", null);
        masterEntity = new MasterEntity();
        headList = new ArrayList<Object>();
        videoList = new ArrayList<VideoEntity>();
        videoAdapter = new HomeAdapter(context, videoList);
        updateVideo_list = new ArrayList<VideoEntity>();
        updateVideoAdapter = new MasterUpdateVideoAdapter(context, updateVideo_list);
    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.activity_master_back);
        backBtn.setOnClickListener(this);

        commentLayout = (LinearLayout) findViewById(R.id.activity_master_comment_layout);
        submitTv = (TextView) findViewById(R.id.activity_master_play_sumbit);
        submitTv.setOnClickListener(this);
        faceBtn = (Button) findViewById(R.id.activity_master_comment_face_btn);
        faceBtn.setOnClickListener(this);
        gridFace = (GridView) findViewById(R.id.activity_master_gridview_face);
        commentEdt = (EditText) findViewById(R.id.activity_master_comment_edt);
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

        updateVideoReListView = (RefreshListView) findViewById(R.id.activity_master_video_lv);
        messageListView = (RefreshListView) findViewById(R.id.activity_master_message_lv);

        View view = inflater.inflate(R.layout.activity_master_head, null);
        hotRl = (RelativeLayout) view.findViewById(R.id.head_hot_rl);
        videoListView = (ListView) view.findViewById(R.id.master_head_video_list);
        videoListView.setAdapter(videoAdapter);
        headImg = (CircularImage) view.findViewById(R.id.master_head_img);
        priseNumTv = (TextView) view.findViewById(R.id.master_head_prise_tv);
        fansTv = (TextView) view.findViewById(R.id.master_head_fans_tv);
        nameTv = (TextView) view.findViewById(R.id.master_head_name_tv);
        gameTv = (TextView) view.findViewById(R.id.master_head_game_tv);
        hobbyTv = (TextView) view.findViewById(R.id.master_head_hobby_tv);
        gameCareerTv = (TextView) view.findViewById(R.id.master_head_game_career_tv);
        mottoTv = (TextView) view.findViewById(R.id.master_head_motto_tv);

        priseTv = (TextView) view.findViewById(R.id.master_head_prise_layout_prise_tv);
        focusTv = (TextView) view.findViewById(R.id.master_head_focus_layout_focus_tv);

        videoTv = (TextView) view.findViewById(R.id.master_head_video_tv);
        videoTv.setOnClickListener(this);
        messageTv = (TextView) view.findViewById(R.id.master_head_message_tv);
        messageTv.setOnClickListener(this);

        priseRl = (RelativeLayout) view.findViewById(R.id.master_head_prise_layout);
        priseRl.setOnClickListener(this);
        focusRl = (RelativeLayout) view.findViewById(R.id.master_head_focus_layout);
        focusRl.setOnClickListener(this);
        shareRl = (RelativeLayout) view.findViewById(R.id.master_head_share_layout);
        shareRl.setOnClickListener(this);

        priseIv = (ImageView) view.findViewById(R.id.master_head_prise_layout_prise_iv);
        focusIv = (ImageView) view.findViewById(R.id.master_head_focus_layout_focus_iv);
        videoIv = (ImageView) view.findViewById(R.id.master_head_video_iv);
        messageIv = (ImageView) view.findViewById(R.id.master_head_message_iv);

        updateVideoReListView.addHeaderView(view);
        updateVideoReListView.setAdapter(updateVideoAdapter);
        updateVideoReListView.setPullLoadEnable(true);
        updateVideoReListView.setXListViewListener(this);
        updateVideoReListView.setPullRefreshEnable(true);

        messageListView.addHeaderView(view);
        messageListView.setAdapter(commentAdapter);
        messageListView.setPullLoadEnable(true);
        messageListView.setXListViewListener(this);
        messageListView.setPullRefreshEnable(true);

        DialogUtils.createLoadingDialog(context, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetHeadInfoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetHeadInfoAsync().execute();
        }

        onRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_master_back:
                finish();
                break;
            case R.id.master_head_video_tv:
                state = "video";
                videoTv.setTextColor(Color.parseColor("#fe4545"));
                videoIv.setBackgroundColor(Color.parseColor("#fe4545"));
                messageTv.setTextColor(Color.parseColor("#424141"));
                messageIv.setBackgroundColor(Color.parseColor("#cacaca"));
                updateVideoReListView.setVisibility(View.VISIBLE);
                messageListView.setVisibility(View.GONE);
                videoListView.setVisibility(View.VISIBLE);
                commentLayout.setVisibility(View.GONE);
                hotRl.setVisibility(View.VISIBLE);
                break;
            case R.id.master_head_message_tv:
                state = "message";
                messageTv.setTextColor(Color.parseColor("#fe4545"));
                messageIv.setBackgroundColor(Color.parseColor("#fe4545"));
                videoTv.setTextColor(Color.parseColor("#424141"));
                videoIv.setBackgroundColor(Color.parseColor("#cacaca"));
                videoListView.setVisibility(View.GONE);
                hotRl.setVisibility(View.GONE);
                updateVideoReListView.setVisibility(View.GONE);
                messageListView.setVisibility(View.VISIBLE);
                commentLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_master_play_sumbit://发送评论

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
            case R.id.activity_master_comment_face_btn:
                commentEdt.requestFocus();
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFace == false) {
                    //在点击表情按钮后隐藏软键盘
                    im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            case R.id.activity_master_comment_edt:
                hasFace = false;
                gridFace.setVisibility(View.GONE);
                faceBtn.setBackgroundResource(R.drawable.face_nomal);
                break;
            case R.id.master_head_prise_layout:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitPriseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitPriseTask().execute();
                }
                break;
            case R.id.master_head_focus_layout:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitFocusTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitFocusTask().execute();
                }
                break;
            case R.id.master_head_share_layout:
                showShare();
                break;
        }

    }

    @Override
    public void onRefresh() {
        pageId = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetUpdateVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetUpdateVideoAsync(pageId + "").execute();
        }

        page = 1;
        asyncType = REFRESH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetCommentAsync(id, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCommentAsync(id, page + "").execute();
        }
    }

    @Override
    public void onLoadMore() {
        if ("video".equals(state)) {
            pageId += 1;
            asyncType = LOADMORE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetUpdateVideoAsync(pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetUpdateVideoAsync(pageId + "").execute();
            }
        } else {
            page += 1;
            asyncType = LOADMORE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetCommentAsync(id, page + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetCommentAsync(id, page + "").execute();
            }
        }
    }

    private void showShare() {
        if (masterEntity != null) {
            final String url = masterEntity.getUrl();
            ShareSDK.initSDK(this);
            final OnekeyShare oks = new OnekeyShare();
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_copy);
            // 定义图标的标签
            String label = getResources().getString(R.string.share_copy);
            // 图标点击后会通过Toast提示消息
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(url);
                    ToastUtils.showToast(getApplicationContext(), "视频链接已复制");
//                    oks.finish();
                }
            };
            oks.setCustomerLogo(logo, null, label, listener);
//            oks.show(context);
            //关闭sso授权
            oks.disableSSOWhenAuthorize();
//            // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//            oks.setNotification(R.drawable.tubiao_top, getString(R.string.app_name));
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(getString(R.string.share));
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url);
//            oks.setTitleUrl(youkuDetail.getLink());
            // text是分享文本，所有平台都需要这个字段
//            oks.setText("快来看看" + detail.getName() + youkuDetail.getLink());
            oks.setText("快来看看 " + masterEntity.getNickname() + " 大神的神作" + url);
            /** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
            oks.setImageUrl(masterEntity.getFlagPath());
            // url仅在微信（包括好友和朋友圈）中使用
//            oks.setUrl(youkuDetail.getLink());
            oks.setUrl("快来看看 " + masterEntity.getNickname() + " 大神的神作" + url);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url);
//            oks.setSiteUrl(youkuDetail.getLink());

            // 启动分享GUI
            oks.show(this);
            ExApplication.upUmenEventValue(context, "分享次数", "share_count");
        }
    }

    /**
     * 异步获取 大神更新 列表
     */
    public class GetUpdateVideoAsync extends AsyncTask<Void, Void, String> {

        String page = "";

        public GetUpdateVideoAsync(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            updateVideo_connectList = JsonHelper.getMasterUpdateVideoList(id, page);
            if (updateVideo_connectList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    updateVideoReListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    updateVideo_list.clear();
                    updateVideo_list.addAll(updateVideo_connectList);
                } else {
                    ToastUtils.showToast(context, "连接服务器失败");
                }
            } else {
                if (s.equals("s")) {
                    if (updateVideo_connectList.size() == 0) {
                        ToastUtils.showToast(context, "已经加载全部数据");
                    } else {
                        updateVideo_list.addAll(updateVideo_connectList);
                    }
                } else {
                    ToastUtils.showToast(context, "已加载全部数据");
                }
            }
            hotRl.setVisibility(View.VISIBLE);
            updateVideoAdapter.update(updateVideo_list);
            updateVideoReListView.stopRefresh();
            updateVideoReListView.stopLoadMore();
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
            comment_connectList = JsonHelper.getPersonCommentList(id, page, ExApplication.MEMBER_ID);
            Log.e("conList", comment_connectList + "1");
            if (comment_connectList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (asyncType == REFRESH) {
                if (s.equals("s")) {
                    messageListView.setRefreshTime(dateFormat.format(new Date(System.currentTimeMillis())));
                    comment_list.clear();
                    comment_list.addAll(comment_connectList);
                } else {
//                    ToastUtils.showToast(context, "加载失败");
                }
            } else {
                if (s.equals("s")) {
                    if (comment_connectList.size() == 0) {
                        ToastUtils.showToast(context, "无更多评论");
                    } else {
                        comment_list.addAll(comment_connectList);
                    }
                } else {
//                    ToastUtils.showToast(context, "加载失败");
                }
            }
            commentAdapter.update(comment_list);
            messageListView.stopRefresh();
            messageListView.stopLoadMore();
        }
    }


    /**
     * 异步获取 大神信息 头部信息，包含4个视频
     */
    public class GetHeadInfoAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            headList = JsonHelper.getMasterHeadInfo(ExApplication.MEMBER_ID, id);
            if (headList != null) {
                masterEntity = (MasterEntity) headList.get(0);
                videoList = (List<VideoEntity>) headList.get(1);
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            exApplication.imageLoader.displayImage(masterEntity.getTopic_avatar(), headImg, exApplication.getOptions());
            priseNumTv.setText("赞：" + masterEntity.getPraise());
            fansTv.setText("粉丝：" + masterEntity.getFans());
            nameTv.setText(masterEntity.getNickname());
            gameTv.setText(masterEntity.getOften_game());
            hobbyTv.setText(masterEntity.getHobby());
            gameCareerTv.setText(masterEntity.getGame_career());
            mottoTv.setText(masterEntity.getManifesto());
            if (masterEntity.getMark() == 0) {
                focusIv.setBackgroundResource(R.drawable.collect_normal);
            } else {
                focusIv.setBackgroundResource(R.drawable.collect_pressed);
            }
            if (masterEntity.getLikeMark() == 0) {
                priseIv.setBackgroundResource(R.drawable.love_normal);
            } else {
                priseIv.setBackgroundResource(R.drawable.love_pressed);
            }

            DialogUtils.cancelLoadingDialog();
            videoAdapter.update(videoList);
            setPullLvHeight(videoListView);
            updateVideoReListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 提交评论
     */
    private class SubmitPersonTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.submitPersonComment(ExApplication.MEMBER_ID, id, commentEdt.getText().toString().trim());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                ToastUtils.showToast(context, "评论成功");
                commentEdt.setText("");
                onRefresh();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentEdt.getWindowToken(), 0);
            } else {
                ToastUtils.showToast(context, "评论失败,请登录后再重试");
            }
        }
    }

    /**
     * 点赞
     */
    private class submitPriseTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.submitMasterPrise(ExApplication.MEMBER_ID, id);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                int prise = Integer.parseInt(masterEntity.getPraise());
                if (masterEntity.getLikeMark() == 0) {
                    priseIv.setBackgroundResource(R.drawable.love_pressed);
                    priseTv.setTextColor(Color.parseColor("#fe4545"));
                    priseNumTv.setText("赞：" + (prise + 1));
                    masterEntity.setLikeMark(1);
                    masterEntity.setPraise((prise + 1) + "");
                } else {
                    priseIv.setBackgroundResource(R.drawable.love_normal);
                    priseNumTv.setText("赞：" + (prise - 1));
                    priseTv.setTextColor(Color.parseColor("#a9a9a9"));
                    masterEntity.setLikeMark(0);
                    masterEntity.setPraise((prise - 1) + "");
                }

            }
        }
    }

    /**
     * 关注
     */
    private class submitFocusTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.submitMasterFocus(ExApplication.MEMBER_ID, id);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                int fans = Integer.parseInt(masterEntity.getFans());
                if (masterEntity.getMark() == 0) {
                    focusIv.setBackgroundResource(R.drawable.collect_pressed);
                    fansTv.setText("粉丝：" + (fans + 1));
                    focusTv.setTextColor(Color.parseColor("#fe4545"));
                    masterEntity.setMark(1);
                    masterEntity.setFans((fans + 1) + "");
                } else {
                    focusIv.setBackgroundResource(R.drawable.collect_normal);
                    fansTv.setText("粉丝：" + (fans - 1));
                    focusTv.setTextColor(Color.parseColor("#a9a9a9"));
                    masterEntity.setMark(0);
                    masterEntity.setFans((fans - 1) + "");
                }

            }
        }
    }

    /**
     * 动态改变listView的高度
     *
     * @param pull
     */
    private void setPullLvHeight(ListView pull) {
        int totalHeight = 0;
        for (int i = 0, len = videoAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = videoAdapter.getView(i, null, pull);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = pull.getLayoutParams();
        params.height = totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
        pull.setLayoutParams(params);
    }
}
