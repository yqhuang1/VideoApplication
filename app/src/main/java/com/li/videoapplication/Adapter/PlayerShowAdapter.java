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
import com.li.videoapplication.View.CircularImage;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.PlayerShowEntity;
import com.li.videoapplication.utils.StringUtils;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 玩家秀 适配器
 */
public class PlayerShowAdapter extends BaseAdapter {

    private List<PlayerShowEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private VideoThumbnailLoader videoThumbnailLoader;

    public PlayerShowAdapter(Context context, List<PlayerShowEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);
    }

    public void update(List<PlayerShowEntity> list) {
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
            convertView = inflater.inflate(R.layout.player_show_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.player_show_item_img);
            holder.flag = (CircularImage) convertView.findViewById(R.id.player_show_item_flag);
            holder.name = (TextView) convertView.findViewById(R.id.player_show_item_name);
            holder.title = (TextView) convertView.findViewById(R.id.player_show_item_title);
            holder.time = (TextView) convertView.findViewById(R.id.player_show_item_time);
            holder.playcount = (TextView) convertView.findViewById(R.id.player_show_item_playcount);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.player_show_item_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!list.get(position).getFlagPath().equals("")) {
//              videoThumbnailLoader.DisplayImage(list.get(position).getFlagPath(),holder.img);
            exApplication.imageLoader.displayImage(list.get(position).getFlagPath(), holder.img, exApplication.getOptions());
        } else {
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
        }
//        videoThumbnailLoader.DisplayImage(list.get(position).getAvatar(),holder.flag);
        exApplication.imageLoader.displayImage(list.get(position).getAvatar(), holder.flag, exApplication.getOptions());

        holder.time.setText(list.get(position).getTime_length());
        String viewCount = list.get(position).getView_count();
        holder.playcount.setText(StringUtils.turnViewCount(viewCount));

        holder.title.setText(list.get(position).getName());
        holder.name.setText(list.get(position).getNickName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("title", list.get(position).getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        LinearLayout layout;
        ImageView img;
        CircularImage flag;
        TextView title;
        TextView name;
        TextView time;
        TextView playcount;

    }
}
