package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ActivityDetailActivity;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.utils.CompleteTaskUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import static com.li.videoapplication.R.id;

/**
 * Created by li on 2015/8/21.
 * <p/>
 * 主页活动 我的活动 适配器
 */
public class FragmentMyActivityAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflater;
    private List<MatchEntity> list;
    private ExApplication exApplication;
    CompleteTaskUtils utils;

    public FragmentMyActivityAdapter(Context context, List<MatchEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_activity_my_item, null);
            holder = new ViewHolder();
            holder.itemRl = (RelativeLayout) convertView.findViewById(R.id.my_activity_item_RL);
            holder.imgeIv = (ImageView) convertView.findViewById(id.my_activity_item_img);
            holder.titleTv = (TextView) convertView.findViewById(id.my_activity_item_title);
            holder.timeTv = (TextView) convertView.findViewById(id.my_activity_item_time);
            holder.contentTxt1 = (TextView) convertView.findViewById(id.my_activity_item_contentTxt1);
            holder.contentTxt2 = (TextView) convertView.findViewById(id.my_activity_item_contentTxt2);
            holder.stateIv = (ImageView) convertView.findViewById(id.my_activity_item_state_iv);
            holder.stateTv = (TextView) convertView.findViewById(id.my_activity_item_state_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).getPic_110_110() == "") {
            holder.imgeIv.setImageResource(R.drawable.image_load_default);
        } else {
            exApplication.imageLoader.displayImage(list.get(position).getPic_110_110(), holder.imgeIv, exApplication.getOptions(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    holder.imgeIv.setImageResource(R.drawable.image_load_default);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        holder.titleTv.setText(list.get(position).getName());
        String startTime = list.get(position).getStarttime().substring(0, 10);
        String endTime = list.get(position).getEndtime().substring(0, 10);
        holder.timeTv.setText("活动时间：" + startTime + " ~ " + endTime);
        if (list.get(position).getStatus() == 1) {
            holder.contentTxt1.setText("你还未上传视频，");
            holder.contentTxt2.setText("立即上传！");
            holder.contentTxt2.setTextColor(Color.RED);
        } else if (list.get(position).getStatus() == 2) {
            holder.contentTxt1.setText("视频已上传，");
            holder.contentTxt2.setText("静待大礼发放！");
            holder.contentTxt2.setTextColor(Color.BLUE);
        }
        if (list.get(position).getMark() == 0) {//0表示未过期
            holder.stateTv.setText("进行中");
            holder.stateTv.setBackgroundResource(R.drawable.corner_red_stroke);
        } else if (list.get(position).getMark() == 1) {//1表示过期
            holder.stateTv.setText("已结束");
            holder.stateTv.setBackgroundResource(R.drawable.corner_gray_stroke);
        }

        holder.itemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra("id", list.get(position).getMatch_id() + "");
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        RelativeLayout itemRl;
        ImageView imgeIv;
        ImageView stateIv;
        TextView titleTv;
        TextView timeTv;
        TextView contentTxt1;
        TextView contentTxt2;
        TextView stateTv;
    }

}
