package com.li.videoapplication.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.PersonalInfoActivity;
import com.li.videoapplication.entity.GameType;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 个人资料 页面 资料
 */
public class PersionInfoFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context context;

    private List<GameType> glist = new ArrayList<GameType>();
    private ImageButton backBtn;
    private TextView nicknameTv, sexTv, addressTv, selectlikeTypeTv,
            phoneTv, emailTv, signatureTv;
    //    private EditText phoneTv;
    private ImageView likeGameLine;

    private TextView disconTv;

    private LinearLayout likeLayout;
    private boolean[] checkState;
    private boolean[] oldCheckState;
    private boolean likegame = false;
    private int likegamecount = 0;
    private int[] likeGame = new int[4];
    private List<GameType> likeList;
    private GridView gridView;
    private LikeGameGridAdapter adapter;
    private String likeId = "";
    private String phoneNum = "";
    private String email = "";
    private String nickname = "";
    private String address = "";
    private String signature = "";
    private View view;

    private String flag = "";

    public static PersionInfoFragment newInstance(String param1, String param2) {
        PersionInfoFragment fragment = new PersionInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PersionInfoFragment() {
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
        //Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_persion_info, null);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initView(View view) {
        nicknameTv = (TextView) view.findViewById(R.id.personal_info_nickname);
        nicknameTv.setOnClickListener(this);
        sexTv = (TextView) view.findViewById(R.id.personal_info_sex);
        sexTv.setOnClickListener(this);
        addressTv = (TextView) view.findViewById(R.id.info_address);
        addressTv.setOnClickListener(this);
        signatureTv = (TextView) view.findViewById(R.id.info_signature);
        signatureTv.setOnClickListener(this);

        phoneTv = (TextView) view.findViewById(R.id.personal_info_phone);
        phoneTv.setOnClickListener(this);
        emailTv = (TextView) view.findViewById(R.id.personal_info_email);
        emailTv.setOnClickListener(this);

        selectlikeTypeTv = (TextView) view.findViewById(R.id.personal_info_select_like_type);

        selectlikeTypeTv.setOnClickListener(this);
        disconTv = (TextView) view.findViewById(R.id.info_discon_tv);
        disconTv.setOnClickListener(this);

        likeGameLine = (ImageView) view.findViewById(R.id.fragment_persion_info_likegame_line);

        likeLayout = (LinearLayout) view.findViewById(R.id.persional_like_ll);
        likeList = new ArrayList<GameType>(4);
        gridView = (GridView) view.findViewById(R.id.persional_like_gv);
        adapter = new LikeGameGridAdapter(context, likeList);
        gridView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetGameTask().execute();
        }
    }


    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {
            case R.id.personal_info_sex:
                Dialog dialog = null;
                builder = new AlertDialog.Builder(context);
                builder.setItems(R.array.sex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                sexTv.setText("男");
                                break;
                            case 1:
                                sexTv.setText("女");
                                break;
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new UpdateInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new UpdateInfoTask().execute();
                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.info_discon_tv:
                SharePreferenceUtil.setUserEntity(context, "");
                ExApplication.MEMBER_ID = "";

                Platform qq = ShareSDK.getPlatform(context, QQ.NAME);
                if (qq.isValid()) {
                    ShareSDK.removeCookieOnAuthorize(true);
                    qq.removeAccount();
                }

                Platform wechat = ShareSDK.getPlatform(context, Wechat.NAME);
                if (wechat.isValid()) {
                    ShareSDK.removeCookieOnAuthorize(true);
                    wechat.removeAccount();
                }

                Platform weibo = ShareSDK.getPlatform(context, SinaWeibo.NAME);
                if (weibo.isValid()) {
                    ShareSDK.removeCookieOnAuthorize(true);
                    weibo.removeAccount();
                }

                getActivity().finish();
                break;
            case R.id.personal_info_nickname:
                createDialog("修改昵称", "请输入昵称", "nickname");
                break;
            case R.id.personal_info_select_like_type:
                builder = new AlertDialog.Builder(context);
                builder.setAdapter(new LikeGameTypeAdapter(context), null);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkState = oldCheckState;
                    }
                });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        likeList.clear();
                        likegamecount = 0;
                        for (int i = 0; i < checkState.length; i++) {
                            if (checkState[i]) {
                                likegamecount++;
                                if (likegamecount <= 4) {
                                    likeList.add(glist.get(i));
                                }
                            }
                        }
                        if (likegamecount > 4) {
                            ToastUtils.showToast(context, "最多选择4个类型");
                            return;
                        }
                        if (likegamecount >= 0) {
                            for (int i = 0; i < likeList.size(); i++) {
                                if (i == 0)
                                    likeId = likeList.get(i).getId();
                                else
                                    likeId = likeId + "," + likeList.get(i).getId();
                            }
                            System.out.println("likeId========" + likeId);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                new UpdateInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                new UpdateInfoTask().execute();
                            }
                        }

                    }
                });
                builder.create().show();
                break;
            case R.id.info_address:
                createDialog("设置地区", "请输入联系地址", "address");
                break;
            case R.id.personal_info_phone:
                createDialog("设置手机", "请输入手机号", "phone");
                break;
            case R.id.personal_info_email:
                createDialog("设置邮箱", "请输入邮箱", "email");
                break;
            case R.id.info_signature:
                createDialog("个性签名", "个性签名", "signature");
                break;
        }
    }

    /**
     * 设置个人信息自定义对话框
     *
     * @param title
     * @param msg
     * @param flag
     */
    public void createDialog(String title, String msg, final String flag) {
        this.flag = flag;
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        View content = getActivity().getLayoutInflater().inflate(R.layout.set_info_view, null);
        dialog.setView(content);
        final EditText editText = (EditText) content.findViewById(R.id.set_info_view_et);
        editText.setHint(msg);
        editText.setHintTextColor(Color.parseColor("#c6c6c6"));
        dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (flag.equals("nickname")) {
                    if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim() == "") {
                        ToastUtils.showToast(context, "昵称不能为空");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else {
                        nickname = editText.getText().toString().trim();
                        dismissDialog(dialogInterface);
                    }
                } else if (flag.equals("address")) {
                    if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim() == "") {
                        ToastUtils.showToast(context, "地址不能为空");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else {
                        address = editText.getText().toString().trim();
                        dismissDialog(dialogInterface);
                    }
                } else if (flag.equals("phone")) {
                    if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim() == "") {
                        ToastUtils.showToast(context, "手机不能为空");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else if (!isMobileNO(editText.getText().toString().trim())) {
                        ToastUtils.showToast(context, "请输入合法的手机号码");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else {
                        phoneNum = editText.getText().toString().trim();
                        dismissDialog(dialogInterface);
                    }
                } else if (flag.equals("signature")) {
                    signature = editText.getText().toString().trim();
                    dismissDialog(dialogInterface);
                } else {
                    if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim() == "") {
                        ToastUtils.showToast(context, "邮箱不能为空");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else if (!isEmail(editText.getText().toString().trim())) {
                        ToastUtils.showToast(context, "请输入合法的电子邮箱");
                        preventDismissDialog(dialogInterface);
                        return;
                    } else {
                        email = editText.getText().toString().trim();
                        dismissDialog(dialogInterface);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new UpdateInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new UpdateInfoTask().execute();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismissDialog(dialogInterface);
            }
        });
        dialog.create().show();
    }

    /**
     * 关闭对话框
     */
    private void dismissDialog(DialogInterface dialogInterface) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, true);
        } catch (Exception e) {
        }
        dialogInterface.dismiss();
    }

    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog(DialogInterface dialogInterface) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(dialogInterface, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断手机号码是否合法*
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 更新个人信息
     */
    private class UpdateInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.getUpdateInfo(ExApplication.MEMBER_ID, nickname, phoneNum, nickname,
                    sexTv.getText().toString().equals("男") ? "1" : "0", address, likeId, email, signature, context);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PersonalInfoActivity personalInfoActivity;
            if (s.equals("s")) {
                ToastUtils.showToast(context, "修改成功");

                if (!phoneNum.equals(""))
                    phoneTv.setText(phoneNum);
                if (!email.equals(""))
                    emailTv.setText(email);
                if (!nickname.equals("")) {
                    nicknameTv.setText(nickname);
                    personalInfoActivity = (PersonalInfoActivity) getActivity();
                    personalInfoActivity.personNameTv.setText(nickname);
                }
                if (!sexTv.getText().equals("")) {
                    if (sexTv.getText().equals("男")) {
                        personalInfoActivity = (PersonalInfoActivity) getActivity();
                        personalInfoActivity.personSexIv.setBackgroundResource(R.drawable.sex_person_male);
                    } else if (sexTv.getText().equals("女")) {
                        personalInfoActivity = (PersonalInfoActivity) getActivity();
                        personalInfoActivity.personSexIv.setBackgroundResource(R.drawable.sex_person_female);
                    }
                }
                if (!address.equals(""))
                    addressTv.setText(address);
                if (!signature.equals("")) {
                    signatureTv.setText(signature);
                    personalInfoActivity = (PersonalInfoActivity) getActivity();
                    personalInfoActivity.signatureTv.setText("个性签名:" + signature);
                } else {
                    signatureTv.setText(signature);
                    personalInfoActivity = (PersonalInfoActivity) getActivity();
                    personalInfoActivity.signatureTv.setText("这家伙很懒，什么也没留下");
                }
                if (likeList.size() == 0) {
                    likeLayout.setVisibility(View.GONE);
                    likeGameLine.setVisibility(View.GONE);
                } else {
                    likeLayout.setVisibility(View.VISIBLE);
                    likeGameLine.setVisibility(View.VISIBLE);
                }
                adapter.update(likeList);
                //新手任务——完善个人资料
                CompleteTaskUtils utils;
                if ("".equals(SharePreferenceUtil.getPreference(context, "16task_flag"))) {
                    utils = new CompleteTaskUtils(context, "16");
                    utils.completeMission();
                    SharePreferenceUtil.setPreference(context, "16task_flag", "true");
                }

            } else {
                if ("nickname".equals(flag)) {
                    ToastUtils.showToast(context, "昵称已经存在");
                }
            }
        }

    }

    /**
     * 获取视频分类列表
     */
    private class GetGameTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            glist = JsonHelper.getGameTypeList();
            if (glist != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                ToastUtils.showToast(context, "连接服务器失败");
                return;
            }

            if (s.equals("s")) {
//                gAdapter=new GameTypeAdapter(getActivity(),glist);
//                gameGv.setAdapter(gAdapter);
                checkState = new boolean[glist.size()];
                oldCheckState = new boolean[glist.size()];
                for (int i = 0; i < checkState.length; i++) {
                    checkState[i] = false;
                    oldCheckState[i] = false;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new getInfoDetailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new getInfoDetailTask().execute();
            }
        }
    }

    private static UserEntity user = new UserEntity();

    /**
     * 个人信息
     */
    private class getInfoDetailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            user = JsonHelper.getUserDetailInfo(context, ExApplication.MEMBER_ID, ExApplication.MEMBER_ID, "");
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
                nicknameTv.setText(user.getTitle());

                sexTv.setText(user.getSex().equals("1") ? "男" : "女");
//                sexTv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        onCreateDialog(1);
//                    }
//                });
                addressTv.setText(user.getAddress());
                phoneTv.setText(user.getMobile());
                emailTv.setText(user.getEmail());
                if (!"".equals(user.getSignature()) && !user.getSignature().equals("null")) {
                    signatureTv.setText(user.getSignature());
                }
                likeGame = user.getLikeArray();
                if (likeGame != null && likeGame.length > 0) {
                    likeLayout.setVisibility(View.VISIBLE);
                    likeGameLine.setVisibility(View.VISIBLE);
                    for (int i = 0; i < likeGame.length; i++) {
                        for (int j = 0; j < glist.size(); j++) {
                            System.out.println("likelength===" + likeGame.length + "     glistlength====" + glist.size());
                            if (likeGame[i] == Integer.parseInt(glist.get(j).getId())) {
                                likeList.add(glist.get(j));
                                checkState[j] = true;
                                oldCheckState[j] = true;
                            }
                        }
                    }
                    adapter.update(likeList);
                } else {
                    likeLayout.setVisibility(View.GONE);
                    likeGameLine.setVisibility(View.GONE);
                }
            }

        }
    }

    /**
     * 喜爱游戏选择适配器
     */
    private class LikeGameTypeAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context context;
        private ViewHolder holder = null;
        private int checkCount = likeList.size();

        public LikeGameTypeAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            System.out.println("checount=====" + checkCount);
        }

        @Override
        public int getCount() {
            return glist.size();
        }

        @Override
        public Object getItem(int position) {
            return glist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.like_game_item, null);
                holder.gameName = (TextView) convertView.findViewById(R.id.like_game_item_tv);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.like_game_item_cb);
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.like_game_item_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.gameName.setText(glist.get(position).getName());

            holder.checkBox.setOnClickListener(new CompoundButton.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (checkState[position]) {
                        checkCount--;
                        checkState[position] = false;
                        System.out.println("1====" + checkCount);
                    } else {
                        checkCount++;
                        if (checkCount <= 4) {
                            checkState[position] = true;
                        } else {
                            ToastUtils.showToast(context, "最多只能选择4个");
                            System.out.println("is====" + checkCount);
                            checkCount--;
                            holder.checkBox.setChecked(false);
                            notifyDataSetChanged();
                        }
                        System.out.println("2====" + checkCount);
                    }

                }
            });

            if (checkState[position]) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            return convertView;
        }

        class ViewHolder {
            TextView gameName;
            CheckBox checkBox;
            LinearLayout linearLayout;
        }
    }

    private class LikeGameGridAdapter extends BaseAdapter {

        private List<GameType> list;
        private Context context;
        private LayoutInflater inflater;
        private ViewHolder holder;

        public LikeGameGridAdapter(Context context, List<GameType> list) {

            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        public void update(List<GameType> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            holder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.like_game_tv_item, null);
                holder.game = (TextView) convertView.findViewById(R.id.like_game_tv_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.game.setText(list.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView game;
        }
    }
}
