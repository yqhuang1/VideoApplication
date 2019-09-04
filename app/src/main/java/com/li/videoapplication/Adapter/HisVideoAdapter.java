package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.StringUtils;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 视频播放 TA的视频 适配器
 */
public class HisVideoAdapter extends BaseAdapter {

    private List<VideoEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private VideoThumbnailLoader videoThumbnailLoader;

    public HisVideoAdapter(Context context, List<VideoEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);
    }

    public void update(List<VideoEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.hisvideo_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.hisvideo_item_img);
            holder.title = (TextView) convertView.findViewById(R.id.hisvideo_item_title);
            holder.time = (TextView) convertView.findViewById(R.id.hisvideo_item_time);
            holder.flower = (TextView) convertView.findViewById(R.id.hisvideo_item_flower);
            holder.comment = (TextView) convertView.findViewById(R.id.hisvideo_item_comment);
            holder.playcount = (TextView) convertView.findViewById(R.id.hisvideo_item_playcount);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.hisvideo_item_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!list.get(position).getSimg_url().equals("")) {
            exApplication.imageLoader.displayImage(list.get(position).getSimg_url(), holder.img, exApplication.getOptions());
        } else {
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
        }

        holder.time.setText(list.get(position).getTime());
        holder.flower.setText(list.get(position).getFlower());
        holder.comment.setText(list.get(position).getComment());
        String playcount = list.get(position).getViewCount();
        holder.playcount.setText(StringUtils.turnViewCount(playcount));

        holder.title.setText(list.get(position).getTitle_content());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("title", list.get(position).getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((VideoPlayActivity) context).finish();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        LinearLayout layout;
        ImageView img;
        TextView title;
        TextView time;
        TextView flower;
        TextView comment;
        TextView playcount;
    }
}
