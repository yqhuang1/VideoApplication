package com.li.videoapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 首页 小编荐适配器
 */
public class ShowPointAdapter extends BaseAdapter {

    private List<VideoEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private VideoThumbnailLoader videoThumbnailLoader;
    private RelativeLayout.LayoutParams layoutParams;

    public ShowPointAdapter(Context context, List<VideoEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        layoutParams = new RelativeLayout.LayoutParams(width - 20, ((width - 20) / 16) * 9);
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
            convertView = inflater.inflate(R.layout.show_point_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.show_point_item_image);
            holder.img.setLayoutParams(layoutParams);
            holder.flowerTv = (TextView) convertView.findViewById(R.id.show_point_item_flower);
            holder.commentTv = (TextView) convertView.findViewById(R.id.show_point_item_comment);
            holder.viewTv = (TextView) convertView.findViewById(R.id.show_point_item_view);
            holder.timeTv = (TextView) convertView.findViewById(R.id.show_point_item_time);
            holder.contentTv = (TextView) convertView.findViewById(R.id.show_point_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!list.get(position).getFlagPath().equals("")) {
            videoThumbnailLoader.DisplayImage(list.get(position).getFlagPath(), holder.img);
        } else {
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
        }
        holder.flowerTv.setText(list.get(position).getFlower_count());
        holder.commentTv.setText(list.get(position).getComment_count());
        holder.viewTv.setText(list.get(position).getView_count());
        holder.timeTv.setText(list.get(position).getTime());
        holder.contentTv.setText(list.get(position).getTitle());
        return convertView;

    }


    private class ViewHolder {
        ImageView img;
        TextView flowerTv;
        TextView commentTv;
        TextView viewTv;
        TextView timeTv;
        TextView contentTv;
    }
}
