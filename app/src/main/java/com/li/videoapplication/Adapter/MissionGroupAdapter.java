package com.li.videoapplication.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.MissionCenterActivity;
import com.li.videoapplication.entity.MissionEntity;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * <p>
 * 我的任务 分组可折叠显示 适配器
 */
public class MissionGroupAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    CompleteTaskUtils utils;

    private List<String> group_list;
    private List<List<MissionEntity>> children_list;

    public MissionGroupAdapter(Context context, List<String> group_list, List<List<MissionEntity>> children_list) {
        this.context = context;

        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);

        this.group_list = group_list;
        this.children_list = children_list;
    }

    /**
     * 获取组的个数
     *
     * @return
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {
        return group_list.size();
    }

    /**
     * 获取指定组中的子元素个数
     *
     * @param groupPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return children_list.get(groupPosition).size();
    }

    /**
     * 获取指定组中的数据
     *
     * @param groupPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public Object getGroup(int groupPosition) {
        return group_list.get(groupPosition);
    }

    /**
     * 获取指定组中的指定子元素数据。
     *
     * @param groupPosition
     * @param childPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getChild(int, int)
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children_list.get(groupPosition).get(childPosition);
    }

    /**
     * 获取指定组的ID，这个组ID必须是唯一的
     *
     * @param groupPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取指定组中的指定子元素ID
     *
     * @param groupPosition
     * @param childPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getChildId(int, int)
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
     *
     * @return
     * @see android.widget.ExpandableListAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded    该组是展开状态还是伸缩状态
     * @param convertView   重用已有的视图对象
     * @param parent        返回的视图对象始终依附于的视图组
     * @return
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mission_head, null);
            groupHolder = new GroupHolder();
            groupHolder.groupRl = (RelativeLayout) convertView.findViewById(R.id.mission_head_RL);
            groupHolder.questionIv = (ImageView) convertView.findViewById(R.id.mission_head_question_Iv);
            groupHolder.tag = (TextView) convertView.findViewById(R.id.mission_head_tag);
            groupHolder.more = (TextView) convertView.findViewById(R.id.mission_head_more);
            groupHolder.img = (ImageView) convertView.findViewById(R.id.mission_head_img);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        groupHolder.tag.setText(group_list.get(groupPosition));
        if (group_list.get(groupPosition).equals("成长任务")) {
            groupHolder.questionIv.setVisibility(View.VISIBLE);
        } else {
            groupHolder.questionIv.setVisibility(View.GONE);
        }

        groupHolder.tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group_list.get(groupPosition).equals("成长任务")) {
                    answerDialog();
                }
            }
        });
        //成长问题解疑
        groupHolder.questionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerDialog();
            }
        });

        if (!isExpanded) {
            groupHolder.more.setText("更多");
            groupHolder.img.setImageResource(R.drawable.ic_vidcontrol_fullscreen_off);
        } else {
            groupHolder.more.setText("收起");
            groupHolder.img.setImageResource(R.drawable.ic_vidcontrol_fullscreen_on);
        }

        if (children_list.get(groupPosition).size() == 0) {
            groupHolder.groupRl.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 获取一个视图对象，显示指定组中的指定子元素数据。
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild   子元素是否处于组中的最后一个
     * @param convertView   重用已有的视图(View)对象
     * @param parent        返回的视图(View)对象始终依附于的视图组
     * @return
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final ItemHolder itemHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mission_item, null);
            itemHolder = new ItemHolder();
            itemHolder.itemRl = (RelativeLayout) convertView.findViewById(R.id.mission_item_RL);
            itemHolder.imgeIv = (ImageView) convertView.findViewById(R.id.mission_item_img);
            itemHolder.titleTv = (TextView) convertView.findViewById(R.id.mission_item_title);
            itemHolder.contentTv = (TextView) convertView.findViewById(R.id.mission_item_content);
            itemHolder.stateIv = (ImageView) convertView.findViewById(R.id.mission_item_state_iv);
            itemHolder.stateTv = (TextView) convertView.findViewById(R.id.mission_item_state_tv);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }

        final MissionEntity item_list = children_list.get(groupPosition).get(childPosition);

        if (item_list.getImgPath().equals("") || item_list.getImgPath() == null) {
            itemHolder.imgeIv.setVisibility(View.GONE);
        } else {
            itemHolder.imgeIv.setVisibility(View.VISIBLE);
            exApplication.imageLoader.displayImage(item_list.getImgPath(), itemHolder.imgeIv, exApplication.getOptions());
        }

        itemHolder.titleTv.setText(item_list.getTitle());

        if (groupPosition == 0) {
            itemHolder.contentTv.setText(Html.fromHtml(item_list.getContent() +
                    "，经验值<font color=\"#fb3d2e\">+" + item_list.getAdd_exp() + "</font>，" +
                    "进度<font color=\"#fb3d2e\">" + item_list.getFlaging() + "/" + item_list.getNum() + "</font>"));
        } else if (groupPosition == 1) {
            itemHolder.contentTv.setText(Html.fromHtml(item_list.getContent() +
                    "，经验值<font color=\"#fb3d2e\">+" + item_list.getAdd_exp() + "</font>"));
        }

        utils = new CompleteTaskUtils(context, item_list.getId());

        final String isAccept = item_list.getIs_accept();//是否接受任务；1已接受，0没接受
        final String taskFlag = item_list.getTask_flag();//是否完成任务；1任务已完成，0任务进行中
        final String isGet = item_list.getIs_get();//是否领取；1已领取，0没领取
        final String statusTxt = item_list.getStatus_txt();//现在状态；分四种："接受任务","进行中","领取奖励","已领取"

        itemHolder.stateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ExApplication.MEMBER_ID.equals("")) {
                    if ("0".equals(item_list.getIs_accept())) {//未接受任务
                        //去接受任务 异步方法
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new AcceptMissionTask(item_list, itemHolder.stateTv, itemHolder.stateIv, childPosition).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new AcceptMissionTask(item_list, itemHolder.stateTv, itemHolder.stateIv, childPosition).execute();
                        }
                    } else {//已接受任务
                        if ("0".equals(item_list.getTask_flag())) {//未完成任务，任务进行中
                            ((MissionCenterActivity) context).missionIntent(groupPosition, childPosition);
                        } else {
                            if ("0".equals(item_list.getIs_get())) {//已完成任务,且未领取奖励
                                //去领取记录 异步方法
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new GetRewardTask(item_list, itemHolder.stateTv, itemHolder.stateIv, childPosition).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new GetRewardTask(item_list, itemHolder.stateTv, itemHolder.stateIv, childPosition).execute();
                                }
                            }
                        }
                    }
                } else {
                    ToastUtils.showToast(context, "请先登录");
//            Intent intent = new Intent(this, RegisterActivity.class);
//            startActivity(intent);
//            finish();
//            ExApplication.upUmenEventValue(getApplicationContext(), "手机登陆次数", "phone_login_count");
                }
            }
        });

        if ("0".equals(item_list.getIs_accept())) {//未接受任务
            itemHolder.stateTv.setBackgroundResource(R.drawable.corner_red_stroke);
            itemHolder.stateTv.setText("接受任务");
        } else {
            if ("0".equals(item_list.getTask_flag())) {//任务进行中
                itemHolder.stateTv.setBackgroundResource(R.drawable.corner_orange_stroke);
                itemHolder.stateTv.setText("前往完成");
            } else {
                if ("0".equals(item_list.getIs_get())) {//未领取奖励
                    itemHolder.stateTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    itemHolder.stateTv.setText("领取奖励");
                } else {//已领取奖励
                    itemHolder.stateTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    itemHolder.stateTv.setText("已完成");
                }
            }
        }

//        //当任务为新手任务且已领取奖励时，子项不可见
//        if (item_list.getTaskTypeName().equals("新手任务") && "1".equals(isGet)) {
//            convertView.setVisibility(View.GONE);
//        }

//        holder.stateTv.setText(flag);

        SharePreferenceUtil.setPreference(context, item_list.getId() + "taskflag", item_list.getTask_flag());
        return convertView;
    }

    /**
     * 是否选中指定位置上的子元素。
     *
     * @param groupPosition
     * @param childPosition
     * @return
     * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class GroupHolder {
        RelativeLayout groupRl;
        public TextView tag;
        public ImageView questionIv;
        public TextView more;
        public ImageView img;
    }

    class ItemHolder {
        RelativeLayout itemRl;
        ImageView imgeIv;
        TextView titleTv;
        TextView contentTv;
        ImageView stateIv;
        TextView stateTv;
    }

    /**
     * 问题解疑对话框
     */
    public void answerDialog() {
        final Dialog answerDialog = new Dialog(context, R.style.loading_dialog);
        /**这里用getLayoutInflater解决bug**/
        View view = ((MissionCenterActivity) context).getLayoutInflater().inflate(R.layout.alter_answer_dialog, null);
        answerDialog.setContentView(view);
        TextView tipTextView = (TextView) view.findViewById(R.id.alter_answer_dialog_tv);
        tipTextView.setText(Html.fromHtml("<p style=\"text-align:center;\">\n" +
                "\t<u><span style=\"font-size:14px;\"><strong>成长任务</strong></span></u> \n" +
                "</p>\n" +
                "<p style=\"text-align:left;\">\n" +
                "\t&nbsp; &nbsp; &nbsp; 成长任务是每日更新的日常任务，帮助玩家通过任务获取经验值，提升等级。视友可根据指引完成当天所有的任务，领取一定的经验值奖励。\n" +
                "</p>\n" +
                "<p style=\"text-align:left;\">\n" +
                "\t<span style=\"line-height:1.5;\">&nbsp; &nbsp; &nbsp; 成长任务每日24时会进行清零，发布次日的新一轮成长任务。</span>\n" +
                "</p>"));
        ImageView tipImageview = (ImageView) view.findViewById(R.id.alter_answer_dialog_iv);
        tipImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerDialog.cancel();
            }
        });

        answerDialog.setCancelable(true);// 可以用“返回键”取消
        answerDialog.show();
    }

    /**
     * 接受任务异步请求
     */
    private class AcceptMissionTask extends AsyncTask<Void, Void, String> {

        MissionEntity item_list;
        String task_id = "";
        String reward = "";
        TextView stateTv;
        ImageView stateIv;
        int position;

        public AcceptMissionTask(MissionEntity item_list, TextView stateTv, ImageView stateIv, int position) {
            this.item_list = item_list;
            this.task_id = item_list.getId();
            this.reward = item_list.getReward();
            this.stateTv = stateTv;
            this.stateIv = stateIv;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            boolean b = JsonHelper.acceptMission(task_id);
            if (b == true) {
                if (task_id.equals("17")) {//新手任务——推广手游视界APP
                    SharePreferenceUtil.setPreference(context, "17task_flag", "进行中");
                }
                if (task_id.equals("22")) {//每日任务——分享2个视频给好友
                    SharePreferenceUtil.setPreference(context, "22task_flag", "进行中");
                }
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                ToastUtils.showToast(context, "接受任务成功");
                stateTv.setBackgroundResource(R.drawable.corner_orange_stroke);
                stateTv.setText("前往完成");
                item_list.setTask_flag("0");//设置任务未完成
                item_list.setIs_accept("1");//设置已接受任务
                item_list.setStatus_txt("进行中");
            }
        }
    }


    /**
     * 领取任务奖励异步请求
     */
    private class GetRewardTask extends AsyncTask<Void, Void, String> {

        MissionEntity item_list;
        String task_id = "";
        String reward = "";
        TextView statedTv;
        ImageView stateIv;
        int position;

        public GetRewardTask(MissionEntity item_list, TextView statedTv, ImageView stateIv, int position) {
            this.item_list = item_list;
            this.task_id = item_list.getId();
            this.reward = item_list.getReward();
            this.statedTv = statedTv;
            this.stateIv = stateIv;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            boolean b = JsonHelper.getMissionReward(task_id);
            if (b == true) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                ToastUtils.showToast(context, "成功领取" + reward);
                statedTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                statedTv.setText("已完成");//设置已领取奖励
                item_list.setTask_flag("1");//设置任务已完成
                item_list.setIs_get("1");//设置已领取

                //刷新MissionCenterActivity
                MissionCenterActivity.refreshHandle.sendEmptyMessage(1);
            }
        }
    }

}
