package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.StringUtils;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 轻松一刻 适配器
 */
public class RelaxeAdapter extends BaseAdapter {

    private List<VideoEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private int width;
    private RelativeLayout.LayoutParams layoutParams, layoutParams1;

    public RelaxeAdapter(Context context, List<VideoEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        layoutParams = new RelativeLayout.LayoutParams(width, ((width) / 16) * 9);
        layoutParams1 = new RelativeLayout.LayoutParams(width / 2, ((width) / 32) * 9);
    }

    public void update(List<VideoEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (list != null) {
            return list.size() / 3 == 0 ? list.size() / 3 : list.size() / 3 + 1;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return list.get(i / 3 + i % 3);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_relaxe_item, null);
            holder = new ViewHolder();

            holder.bigLayout = (LinearLayout) convertView.findViewById(R.id.big_layout);
            holder.imgeIv = (ImageView) convertView.findViewById(R.id.activity_relaxe_item_img);
            holder.imgeIv.setLayoutParams(layoutParams);
            holder.flowerTv = (TextView) convertView.findViewById(R.id.activity_relaxe_item_flower);
            holder.commentTv = (TextView) convertView.findViewById(R.id.activity_relaxe_item_comment);
            holder.timeTv = (TextView) convertView.findViewById(R.id.activity_relaxe_item_time);
            holder.contentTv = (TextView) convertView.findViewById(R.id.activity_relaxe_item_introduce);
            holder.playTimesTv = (TextView) convertView.findViewById(R.id.activity_relaxe_item_playcount);

            holder.leftLayout = (LinearLayout) convertView.findViewById(R.id.left_layout);
            holder.imgeIv1 = (ImageView) convertView.findViewById(R.id.activity_relaxe_item_img1);
            holder.imgeIv1.setLayoutParams(layoutParams1);
            holder.flowerTv1 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_flower1);
            holder.commentTv1 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_comment1);
            holder.timeTv1 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_time1);
            holder.contentTv1 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_introduce1);
            holder.playTimesTv1 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_playcount1);

            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.right_layout);
            holder.imgeIv2 = (ImageView) convertView.findViewById(R.id.activity_relaxe_item_img2);
            holder.imgeIv2.setLayoutParams(layoutParams1);
            holder.flowerTv2 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_flower2);
            holder.commentTv2 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_comment2);
            holder.timeTv2 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_time2);
            holder.contentTv2 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_introduce2);
            holder.playTimesTv2 = (TextView) convertView.findViewById(R.id.activity_relaxe_item_playcount2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /**
         * 大图
         */
        if (position * 3 < list.size()) {
            holder.bigLayout.setVisibility(View.VISIBLE);
            holder.flowerTv.setText(list.get(position * 3).getFlower());
            holder.commentTv.setText(list.get(position * 3).getComment());
            holder.timeTv.setText(list.get(position * 3).getTime());
            holder.contentTv.setText(list.get(position * 3).getTitle_content());
            String playcount = list.get(position * 3).getViewCount();
            holder.playTimesTv.setText(StringUtils.turnViewCount(playcount));
            if (!list.get(position * 3).getSimg_url().equals("")) {
                exApplication.imageLoader.displayImage(list.get(position * 3).getSimg_url(), holder.imgeIv, exApplication.getOptions());
                holder.bigLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, VideoPlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", list.get(position * 3).getId());
                        intent.putExtra("title", list.get(position * 3).getTitle_content());
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.imgeIv.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
            }
            holder.contentTv.setText(list.get(position * 3).getTitle_content());
        } else {
            holder.bigLayout.setVisibility(View.GONE);
        }

        /**
         * 第一列
         */
        if (position * 3 + 1 < list.size()) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.flowerTv1.setText(list.get(position * 3 + 1).getFlower());
            holder.commentTv1.setText(list.get(position * 3 + 1).getComment());
            holder.timeTv1.setText(list.get(position * 3 + 1).getTime());
            holder.contentTv1.setText(list.get(position * 3 + 1).getTitle_content());
            String playcount1 = list.get(position * 3 + 1).getViewCount();
            holder.playTimesTv1.setText(StringUtils.turnViewCount(playcount1));

            if (!list.get(position * 3 + 1).getSimg_url().equals("")) {
                exApplication.imageLoader.displayImage(list.get(position * 3 + 1).getSimg_url(), holder.imgeIv1, exApplication.getOptions());
                holder.leftLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, VideoPlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", list.get(position * 3 + 1).getId());
                        intent.putExtra("title", list.get(position * 3 + 1).getTitle_content());
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.imgeIv1.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
            }
            holder.contentTv1.setText(list.get(position * 3 + 1).getTitle_content());
        } else {
            holder.leftLayout.setVisibility(View.GONE);
        }

        /**
         * 第二列
         */
        if (position * 3 + 2 < list.size()) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            if (!list.get(position * 3 + 2).getSimg_url().equals("")) {
                exApplication.imageLoader.displayImage(list.get(position * 3 + 2).getSimg_url(), holder.imgeIv2, exApplication.getOptions());
                holder.rightLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, VideoPlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", list.get(position * 3 + 2).getId());
                        intent.putExtra("title", list.get(position * 3 + 2).getTitle_content());
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.imgeIv2.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
            }
            holder.flowerTv2.setText(list.get(position * 3 + 2).getFlower());
            holder.commentTv2.setText(list.get(position * 3 + 2).getComment());
            holder.timeTv2.setText(list.get(position * 3 + 2).getTime());
            holder.contentTv2.setText(list.get(position * 3 + 2).getTitle_content());
            //播放数过万处理
            String playcount2 = list.get(position * 3 + 2).getViewCount();
            holder.playTimesTv2.setText(StringUtils.turnViewCount(playcount2));
        } else {
            if (position * 3 + 1 > list.size()) {
                holder.rightLayout.setVisibility(View.GONE);
            }
        }

        return convertView;

    }


    private class ViewHolder {

        LinearLayout bigLayout;
        TextView playTimesTv;
        ImageView imgeIv;
        TextView flowerTv;
        TextView commentTv;
        TextView timeTv;
        TextView contentTv;

        LinearLayout leftLayout;
        TextView playTimesTv1;
        ImageView imgeIv1;
        TextView flowerTv1;
        TextView commentTv1;
        TextView timeTv1;
        TextView contentTv1;

        LinearLayout rightLayout;
        TextView playTimesTv2;
        ImageView imgeIv2;
        TextView flowerTv2;
        TextView commentTv2;
        TextView timeTv2;
        TextView contentTv2;

    }
}
