package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.View.grid.ExtendableListView.LayoutParams;
import com.li.videoapplication.View.grid.util.DynamicHeightImageView;
import com.li.videoapplication.activity.ActivityDetailActivity;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.utils.ToastUtils;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/5/14.
 * <p>
 * 主页活动 热门活动 适配器
 */
public class FragmentHotActivityAdapter extends BaseAdapter {

    private List<MatchEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private VideoThumbnailLoader videoThumbnailLoader;

    public FragmentHotActivityAdapter(Context context, List<MatchEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_activity_hot_item, null);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(lp);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.fragment_activity_item_layout);
            holder.img = (DynamicHeightImageView) convertView.findViewById(R.id.fragment_activity_item_img);
            holder.title = (TextView) convertView.findViewById(R.id.fragment_activity_item_title);
            holder.border = (DynamicHeightImageView) convertView.findViewById(R.id.fragment_activity_item_border);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!list.get(position).getPic_600_x().equals("")) {
//            videoThumbnailLoader.DisplayImage(list.get(position).getPic_600_x(),holder.img);
            System.out.println("list.get(position).getPic_600_x()" + list.get(position).getPic_600_x());
            exApplication.imageLoader.displayImage(list.get(position).getPic_600_x(), holder.img, exApplication.getOptions());

            //设定图片显示比例
            int width = list.get(position).getWidth();
            int height = list.get(position).getHeight();
            float ratio = (float) height / (float) width;
            System.out.println("图片显示宽度" + width);
            System.out.println("图片显示高度" + height);
            System.out.println("设定图片显示比例" + ratio);
            holder.img.setHeightRatio(ratio);
            holder.border.setHeightRatio(ratio);
        } else {
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
        }
        holder.title.setText(list.get(position).getName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) context).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra("id", list.get(position).getMatch_id() + "");
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout layout;
        DynamicHeightImageView img;
        TextView title;
        DynamicHeightImageView border;
    }
}
