package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.fragment.CollectVideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 视频管理 已收藏（视频）适配器
 */
public class CollectAdapter extends BaseAdapter {

    private List<VideoEntity> mList;
    private Context context;
    ViewHolder holder = null;
    private LayoutInflater inflater;
    private ExApplication exApplication;

    private static boolean isSingleNum = true;

    public static List<Boolean> ListDelcheck;// 选择要删除的文件列表选


    public CollectAdapter(Context context, List<VideoEntity> list) {
        this.context = context;
        this.mList = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);

        ListDelcheck = new ArrayList<Boolean>();
        for (int i = 0; i < mList.size(); i++) {
            ListDelcheck.add(false);
        }

        if (mList.size() % 2 == 1) {
            isSingleNum = true;//奇数
        } else {
            isSingleNum = false;//偶数
        }
    }

    public void update(List<VideoEntity> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (mList != null) {
            return mList.size() / 2 + mList.size() % 2;
        } else {
            return 0;
        }

    }

    @Override
    public Object getItem(int i) {
        return mList.get(mList.size() / 2 + mList.size() % 2);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.collect_item, null);
            holder = new ViewHolder();
            holder.leftLayout = (FrameLayout) convertView.findViewById(R.id.left_layout_l);
            holder.imgeIv1 = (ImageView) convertView.findViewById(R.id.home_item_img1);
            holder.timeTv1 = (TextView) convertView.findViewById(R.id.home_item_time1);
            holder.contentTv1 = (TextView) convertView.findViewById(R.id.home_item_introduce1);
            holder.playTimesTv1 = (TextView) convertView.findViewById(R.id.home_item_playcount1);
            holder.leftCheckIv = (ImageView) convertView.findViewById(R.id.collect_item_seletv_iv1);
            holder.leftCheckIv.setTag(position);
            holder.leftCheckBox = (CheckBox) convertView.findViewById(R.id.left_delete_checkbox);

            holder.rightLayout = (FrameLayout) convertView.findViewById(R.id.right_layout_r);
            holder.imgeIv2 = (ImageView) convertView.findViewById(R.id.home_item_img2);
            holder.timeTv2 = (TextView) convertView.findViewById(R.id.home_item_time2);
            holder.contentTv2 = (TextView) convertView.findViewById(R.id.home_item_introduce2);
            holder.playTimesTv2 = (TextView) convertView.findViewById(R.id.home_item_playcount2);
            holder.rightCheckIv = (ImageView) convertView.findViewById(R.id.collect_item_seletv_iv2);
            holder.rightCheckIv.setTag(position);
            holder.RightCheckBox = (CheckBox) convertView.findViewById(R.id.right_delete_checkbox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.leftCheckIv.setTag(position);
            holder.rightCheckIv.setTag(position);
        }

        // 如果处于批量删除状态
        if (VideoManagerActivity.inEditorState == true) {
            InTheDelState(position);
        } else {
            NoInTheDelState();
        }

        //单
        if (!mList.get(position * 2).getSimg_url().equals("")) {
            exApplication.imageLoader.displayImage(mList.get(position * 2).getSimg_url(), holder.imgeIv1, exApplication.getOptions());
            holder.imgeIv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("id", mList.get(position * 2).getId());
                    intent.putExtra("title", mList.get(position * 2).getTitle_content());
                    context.startActivity(intent);
                }
            });
            holder.timeTv1.setText(mList.get(position * 2).getTime());
            holder.contentTv1.setText(mList.get(position * 2).getTitle_content());
            holder.playTimesTv1.setText(mList.get(position * 2).getViewCount());
        } else {
            holder.imgeIv1.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
        }


        //双
        if (position * 2 + 1 < mList.size()) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            if (!mList.get(position * 2 + 1).getSimg_url().equals("")) {
                exApplication.imageLoader.displayImage(mList.get(position * 2 + 1).getSimg_url(), holder.imgeIv2, exApplication.getOptions());
                holder.imgeIv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, VideoPlayActivity.class);
                        intent.putExtra("id", mList.get(position * 2 + 1).getId());
                        intent.putExtra("title", mList.get(position * 2 + 1).getTitle_content());
                        context.startActivity(intent);
                    }
                });
                holder.timeTv2.setText(mList.get(position * 2 + 1).getTime());
                holder.contentTv2.setText(mList.get(position * 2 + 1).getTitle_content());
                holder.playTimesTv2.setText(mList.get(position * 2 + 1).getViewCount());
            } else {
                holder.imgeIv2.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
            }
        } else {
            holder.rightLayout.setVisibility(View.INVISIBLE);
        }


        return convertView;

    }


    private class ViewHolder {
        FrameLayout leftLayout;
        //播放次数
        TextView playTimesTv1;
        //视频截图
        ImageView imgeIv1;
        //视频长度
        TextView timeTv1;
        //视频介绍
        TextView contentTv1;
        ImageView leftCheckIv;
        CheckBox leftCheckBox;

        FrameLayout rightLayout;
        //播放次数
        TextView playTimesTv2;
        //视频截图
        ImageView imgeIv2;
        //视频长度
        TextView timeTv2;
        //视频介绍
        TextView contentTv2;
        ImageView rightCheckIv;
        CheckBox RightCheckBox;
    }

    /**
     * 处于批量删除状态
     *
     * @param position
     */
    private void InTheDelState(final int position) {
        if (!mList.get(position * 2).getSimg_url().equals("")) {
            holder.leftCheckBox.setVisibility(View.VISIBLE);
            holder.leftCheckIv.setVisibility(View.VISIBLE);
            holder.leftCheckBox.setId(position * 2);

            if (ListDelcheck.get(position * 2)) {
                holder.leftCheckIv.setImageResource(MResource.getIdByName(context,
                        "drawable", "check_back_true"));

            } else {
                holder.leftCheckIv.setImageResource(MResource.getIdByName(context,
                        "drawable", "check_back_false"));

            }
            holder.leftCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ListDelcheck.get(position * 2)) {

                        ListDelcheck.set(position * 2, false);
                        CollectVideoFragment.collectVideoListCheckToDel.remove(mList
                                .get(position * 2));
                        // 发消息通知标题栏进行更改
                        VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                    } else {

                        ListDelcheck.set(position * 2, true);
                        CollectVideoFragment.collectVideoListCheckToDel.add(mList
                                .get(position * 2));
                        VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                    }
                    notifyDataSetChanged();

                }
            });
        } else {

        }

        if (position * 2 + 1 < mList.size()) {
            if (!mList.get(position * 2 + 1).getSimg_url().equals("")) {
                holder.RightCheckBox.setVisibility(View.VISIBLE);
                holder.rightCheckIv.setVisibility(View.VISIBLE);
                holder.RightCheckBox.setId(position * 2 + 1);

                if (ListDelcheck.get(position * 2 + 1)) {
                    holder.rightCheckIv.setImageResource(MResource.getIdByName(context,
                            "drawable", "check_back_true"));

                } else {
                    holder.rightCheckIv.setImageResource(MResource.getIdByName(context,
                            "drawable", "check_back_false"));

                }
                holder.RightCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ListDelcheck.get(position * 2 + 1)) {

                            ListDelcheck.set(position * 2 + 1, false);
                            CollectVideoFragment.collectVideoListCheckToDel.remove(mList
                                    .get(position * 2 + 1));
                            // 发消息通知标题栏进行更改
                            VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                        } else {

                            ListDelcheck.set(position * 2 + 1, true);
                            CollectVideoFragment.collectVideoListCheckToDel.add(mList
                                    .get(position * 2 + 1));
                            VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                        }
                        notifyDataSetChanged();

                    }
                });
            } else {

            }
        }
    }

    private void NoInTheDelState() {
        // 重新赋值checkbox状态
        ListDelcheck.clear();

        for (int i = 0; i < mList.size(); i++) {
            ListDelcheck.add(false);
        }

        holder.leftCheckBox.setVisibility(View.GONE);
        holder.leftCheckIv.setVisibility(View.GONE);

        holder.RightCheckBox.setVisibility(View.GONE);
        holder.rightCheckIv.setVisibility(View.GONE);

    }

}
