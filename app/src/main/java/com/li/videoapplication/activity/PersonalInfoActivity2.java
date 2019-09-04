package com.li.videoapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.li.videoapplication.Adapter.FragmentViewPagerAdapter;
import com.li.videoapplication.R;
import com.li.videoapplication.View.CircularImage;
import com.li.videoapplication.View.SyncHorizontalScrollView;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.fragment.PersionCommentFragment;
import com.li.videoapplication.fragment.PersionInfoFragment;
import com.li.videoapplication.fragment.PersionVideoFragment;
import com.li.videoapplication.utils.ImgUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.Titles;
import com.li.videoapplication.utils.ToastUtils;
import com.umeng.message.PushAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;

/**
 * 玩家 个人资料 页面*
 */
public class PersonalInfoActivity2 extends FragmentActivity implements View.OnClickListener {

    private ImageButton backBtn;
    /**
     * 滑动按钮的外围布局
     */
    private RelativeLayout rl_nav;

    /**
     * 水平滑动控件
     */
    private SyncHorizontalScrollView mHsv;

    /**
     * 标题选项
     */
    private RadioGroup rg_nav_content;
    /**
     * 标题滚动下标
     */
    private ImageView iv_nav_indicator;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    private ViewPager mViewPager;

    public static String[] tabTitle;
    private LayoutInflater mInflater;
    private int count;// 屏幕显示的标签个数
    private int indicatorWidth;// 每个标签所占的宽度
    private int currentIndicatorLeft = 0;
    private int position;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    //头部
    private static final int PHOTO_REQUEST_GALLERY = 0;
    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static String state = Environment.getExternalStorageState();
    private static File tempFile;
    private ExApplication application;
    private UserEntity u = null;
    private static final String filename = "/headimg.jpg";
    private CircularImage headImg;
    public ImageView personSexIv;
    private ProgressBar personExpPb;
    public TextView personNameTv;
    private TextView personExpTv, personLevelTv, attentionTv, fansTv, focusTv;
    public TextView signatureTv;
    private ExApplication exApplication;
    private String flag = "";
    private boolean toComment = false;
    private static String member_id = "";
    private int lastMark = 0;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉android头部label的方法
        setContentView(R.layout.activity_personal_info);
        context = PersonalInfoActivity2.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        ShareSDK.initSDK(this);
        flag = getIntent().getStringExtra("flag");
        if ("persion".equals(flag)) {
            member_id = ExApplication.MEMBER_ID;
        } else if ("videoplay".equals(flag)) {
            member_id = getIntent().getStringExtra("member_id");
        }
//        Log.e("member_id===", member_id);
//        Log.e("ExApplication.MEMBER_ID===", ExApplication.MEMBER_ID);
        toComment = getIntent().getBooleanExtra("toComment", false);
//        System.out.println("toComment========" + toComment);

        initView();
        setListener();

        if ("persion".equals(flag)) {
            u = SharePreferenceUtil.getUserEntity(this);
            if (u == null) {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                ExApplication.upUmenEventValue(getApplicationContext(), "手机登陆次数", "phone_login_count");
                this.finish();
            }
        }

        ExApplication.upUmenEventValue(getApplicationContext(), "点击个人中心次数", "click_persional_info_count");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        exApplication = new ExApplication(context);
        //头部
        headImg = (CircularImage) findViewById(R.id.person_info_head_img);
        headImg.setOnClickListener(this);
        personNameTv = (TextView) findViewById(R.id.person_info_name_tv);
        personSexIv = (ImageView) findViewById(R.id.person_info_sex_Iv);
        personLevelTv = (TextView) findViewById(R.id.person_info_degree_tv);
        personExpTv = (TextView) findViewById(R.id.person_info_exp_tv);
        personExpPb = (ProgressBar) findViewById(R.id.person_info_exp_pb);
        signatureTv = (TextView) findViewById(R.id.personal_info_signature_tv);
        attentionTv = (TextView) findViewById(R.id.persion_info_focus_count_tv);
        attentionTv.setOnClickListener(this);
        fansTv = (TextView) findViewById(R.id.persion_info_fans_count_tv);
        fansTv.setOnClickListener(this);
        //滑动布局
        rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        backBtn = (ImageButton) findViewById(R.id.person_info_back);
        backBtn.setOnClickListener(this);
        focusTv = (TextView) findViewById(R.id.activity_persion_focus_tv);
        focusTv.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if ("persion".equals(flag)) {
            indicatorWidth = dm.widthPixels / 3;
            tabTitle = Titles.PERSIONAL;
            count = tabTitle.length;
        } else if ("videoplay".equals(flag)) {
            if (ExApplication.MEMBER_ID.equals(member_id)) {
                indicatorWidth = dm.widthPixels / 3;
                tabTitle = Titles.PERSIONAL;
                count = tabTitle.length;
            } else {
                indicatorWidth = dm.widthPixels / 2;
                tabTitle = Titles.VIDEOUPLOAD;
                count = tabTitle.length;
                headImg.setClickable(false);
            }
        }

        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;

        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, (Activity) context);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        if ("persion".equals(flag)) {//跳转个人资料
            fragments.add(PersionInfoFragment.newInstance(member_id, ""));
            focusTv.setVisibility(View.GONE);
        } else if ("videoplay".equals(flag)) {
            if (ExApplication.MEMBER_ID.equals(member_id)) {
                fragments.add(PersionInfoFragment.newInstance(member_id, ""));
                focusTv.setVisibility(View.GONE);
            }
        }
        fragments.add(PersionVideoFragment.newInstance(member_id, ""));
        fragments.add(PersionCommentFragment.newInstance(member_id, ""));

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                System.out.println("Extra...i: " + i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new getInfoDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new getInfoDetailTask().execute();
        }
    }

    private void initNavigationHSV() {

        rg_nav_content.removeAllViews();

        for (int i = 0; i < tabTitle.length; i++) {

            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            if (i == 0) {
                rb.setTextColor(PersonalInfoActivity2.this.getResources().getColor(R.color.main_title_bg));
            }
            rb.setText(tabTitle[i]);
            rb.setTextSize(16);

            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
        }
        RadioButton rb = (RadioButton) mInflater.inflate(
                R.layout.nav_radiogroup_item, null);
        rg_nav_content.addView(rb);
    }

    private void setListener() {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton) rg_nav_content.getChildAt(position)).performClick();

                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) rg_nav_content.getChildAt(i);
                        tempButton.setTextColor(PersonalInfoActivity2.this.getResources().getColor(R.color.black));
                    }
                    RadioButton tempButton = (RadioButton) rg_nav_content.getChildAt(position);
                    tempButton.setTextColor(PersonalInfoActivity2.this.getResources().getColor(R.color.main_title_bg));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        ((RadioButton) rg_nav_content.getChildAt(2)).setChecked(true);
        ((RadioButton) rg_nav_content.getChildAt(0)).setChecked(true);
        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (rg_nav_content.getChildAt(checkedId) != null) {
                    for (int i = 0; i < tabTitle.length; i++) {
                        RadioButton tempButton = (RadioButton) group.getChildAt(i);
                        tempButton.setTextColor(PersonalInfoActivity2.this.getResources().getColor(R.color.black));
                    }
                    RadioButton tempButton = (RadioButton) group.getChildAt(checkedId);
                    tempButton.setTextColor(PersonalInfoActivity2.this.getResources().getColor(R.color.master_focus_tv));

                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft,
                            indicatorWidth * checkedId, 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    //执行位移动画
                    iv_nav_indicator.startAnimation(animation);

                    mViewPager.setCurrentItem(checkedId);    //ViewPager 跟随一起 切换

                    //记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = indicatorWidth * checkedId;
                    mHsv.smoothScrollTo(
                            (checkedId > 1 ? currentIndicatorLeft : 0) - indicatorWidth * 2, 0);
                }
            }
        });

        if (toComment) {
            ((RadioButton) rg_nav_content.getChildAt(2)).setChecked(true);
        } else {
            ((RadioButton) rg_nav_content.getChildAt(0)).setChecked(true);
        }

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.person_info_back:
                this.finish();
                break;
            case R.id.activity_persion_focus_tv:
                if (ExApplication.MEMBER_ID.equals("")) {
                    ToastUtils.showToast(context, "请先登录");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitFocusTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitFocusTask().execute();
                }
                break;
            case R.id.person_info_head_img:
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.tubiao_top);
                builder.setItems(R.array.image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                camera();
                                break;
                            case 1:
                                gallery();
                                break;
                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.persion_info_focus_count_tv://查看关注 （自己关注的人）
                intent = new Intent(context, MyAttentionActivity.class);
                intent.putExtra("title", "关注");
                intent.putExtra("flag", flag);
                intent.putExtra("member_id", member_id);
                startActivity(intent);
                break;
            case R.id.persion_info_fans_count_tv://查看粉丝 （关注自己的人）
                intent = new Intent(context, MyFansActivity.class);
                intent.putExtra("title", "粉丝");
                intent.putExtra("member_id", member_id);
                startActivity(intent);
                break;
        }
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 从相机获取
     */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    "/head.jpg");
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                ImgUtils.crop(PersonalInfoActivity2.this, uri, PHOTO_REQUEST_CUT);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                ImgUtils.crop(PersonalInfoActivity2.this, Uri.fromFile(tempFile), PHOTO_REQUEST_CUT);
            } else {
                Toast.makeText(PersonalInfoActivity2.this, "未找到存储卡，无法存储照片！",
                        Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                ExApplication.headImg = bitmap;
                headImg.setImageBitmap(bitmap);
                if (bitmap != null) {
                    if (ImgUtils.saveBitmap2file(bitmap, filename)) {
                        ExApplication.headImgUrl = Environment.getExternalStorageDirectory() + "/yc/img"
                                + filename;
                        Log.e("----", ExApplication.headImgUrl);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new UploadImgTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new UploadImgTask().execute();
                        }
                    }
                } else {
                    Log.e("saveFile_False", "");
                }

            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                Log.e("tempFile_clear_fail", "临时文件删除失败");
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 更新头像
     */
    public class UploadImgTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.uploadFile(context, ExApplication.MEMBER_ID, ExApplication.headImgUrl);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    /**
     * 异步获取 个人信息
     */
    private static UserEntity user = new UserEntity();

    private class getInfoDetailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            user = JsonHelper.getUserDetailInfo(context, member_id, ExApplication.MEMBER_ID, flag);
//            Log.e("name",user.getTitle());
            if (user != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("s")) {
                lastMark = user.getMark();
                if (lastMark == 1) {
                    focusTv.setBackgroundResource(R.drawable.unfocus_tv_bg);
                    focusTv.setText("已关注");
                    lastMark = 1;
                } else {
                    focusTv.setBackgroundResource(R.drawable.focus_tv_bg);
                    focusTv.setText("+关注");
                    lastMark = 0;
                }
                exApplication.imageLoader.displayImage(user.getImgPath(), headImg, exApplication.getOptions());
                if (ExApplication.headImg != null) {
                    headImg.setImageBitmap(ExApplication.headImg);
                } else {
                    exApplication.imageLoader.displayImage(user.getImgPath(), headImg, exApplication.getOptions());
                }

                personNameTv.setText(user.getTitle());
                if (user.getSex().equals("1")) {//男性
                    personSexIv.setBackgroundResource(R.drawable.sex_person_male);
                } else if (user.getSex().equals("2")) {//女性
                    personSexIv.setBackgroundResource(R.drawable.sex_person_female);
                }
                personLevelTv.setText("Lv." + user.getDegree());
                if (!"".equals(user.getSignature()) && !user.getSignature().equals("null")) {
                    signatureTv.setText("个性签名:" + user.getSignature());
                }
                attentionTv.setText("关注 " + user.getAttention());
                fansTv.setText("粉丝 " + user.getFans());
                personExpPb.setMax(user.getNext_exp());
                personExpPb.setProgress(Integer.parseInt(user.getRank()));
                personExpTv.setText(user.getRank() + "/" + user.getNext_exp());
                personExpPb.setVisibility(View.VISIBLE);

            }

        }
    }

    /**
     * 关注玩家
     */
    private class submitFocusTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            boolean b = JsonHelper.submitFocus(ExApplication.MEMBER_ID, member_id);
            if (b) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("s")) {
                if (lastMark == 0) {
                    focusTv.setBackgroundResource(R.drawable.unfocus_tv_bg);
                    focusTv.setText("已关注");
                    lastMark = 1;
                    fansTv.setText("粉丝 " + (Integer.parseInt(user.getFans()) + 1));
                    ToastUtils.showToast(context, "关注成功");
                } else {
                    focusTv.setBackgroundResource(R.drawable.focus_tv_bg);
                    focusTv.setText("+关注");
                    lastMark = 0;
                    ToastUtils.showToast(context, "取消关注");
                }
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
