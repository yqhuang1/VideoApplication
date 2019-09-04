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
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.DiscoverVideoEntity;
import com.li.videoapplication.utils.ToastUtils;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 主页 发现 适配器
 */
public class DiscoverVideoListAdapter extends BaseAdapter {

    private List<DiscoverVideoEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private VideoThumbnailLoader videoThumbnailLoader;

    public DiscoverVideoListAdapter(Context context, List<DiscoverVideoEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);
    }

    public void update(List<DiscoverVideoEntity> list) {
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
            convertView = inflater.inflate(R.layout.discover_video_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.discover_video_item_img);
            holder.flag = (CircularImage) convertView.findViewById(R.id.discover_video_item_flag);
            holder.comment = (TextView) convertView.findViewById(R.id.discover_video_item_comment);
            holder.name = (TextView) convertView.findViewById(R.id.discover_video_item_name);
            holder.title = (TextView) convertView.findViewById(R.id.discover_video_item_title);
            holder.flower = (TextView) convertView.findViewById(R.id.discover_video_item_flower);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.discover_video_item_ll);
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
        holder.flower.setText(list.get(position).getFlower_count());
        holder.comment.setText(list.get(position).getComment_count());
        holder.title.setText(list.get(position).getName());
        holder.name.setText(list.get(position).getNickName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) context).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("title", list.get(position).getName());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        LinearLayout layout;
        ImageView img;
        CircularImage flag;
        TextView flower;
        TextView comment;
        TextView title;
        TextView name;

    }
}
