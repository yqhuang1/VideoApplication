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
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.activity.NewAssortActivity;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.HomeColumnEntity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/5.
 * <p>
 * 主页 首页专栏 适配器
 */
public class HomeColumnAdapter extends BaseAdapter {

    private Context context;
    private List<HomeColumnEntity> list;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private ViewHolder holder;
    private RelativeLayout.LayoutParams layoutParams;

    public HomeColumnAdapter(Context context, List<HomeColumnEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        layoutParams = new RelativeLayout.LayoutParams(width / 3, width / 3);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams.setMargins(20, 20, 20, 20);

    }

    public void update(List<HomeColumnEntity> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_column_item2, null);
            holder.titleLayout = (RelativeLayout) convertView.findViewById(R.id.title_layout);
            holder.titleIV = (ImageView) convertView.findViewById(R.id.title_iv);
            holder.flagIV = (ImageView) convertView.findViewById(R.id.title_flagIV);
            holder.titleTV = (TextView) convertView.findViewById(R.id.home_column_item2_title_tv);
            holder.moreTV = (TextView) convertView.findViewById(R.id.home_column_item2_more_tv);

            holder.leftLL1 = (LinearLayout) convertView.findViewById(R.id.left_layout);
            holder.img1 = (ImageView) convertView.findViewById(R.id.home_column_item_img1);
            holder.view1 = (TextView) convertView.findViewById(R.id.home_column_item_view1);
            holder.flower1 = (TextView) convertView.findViewById(R.id.home_column_item_flower1);
            holder.comment1 = (TextView) convertView.findViewById(R.id.home_column_item_comment1);
            holder.time1 = (TextView) convertView.findViewById(R.id.home_column_item_time1);
            holder.introduce1 = (TextView) convertView.findViewById(R.id.home_column_item_introduce1);

            holder.rightLL1 = (LinearLayout) convertView.findViewById(R.id.right_layout);
            holder.img2 = (ImageView) convertView.findViewById(R.id.home_column_item_img2);
            holder.view2 = (TextView) convertView.findViewById(R.id.home_column_item_view2);
            holder.flower2 = (TextView) convertView.findViewById(R.id.home_column_item_flower2);
            holder.comment2 = (TextView) convertView.findViewById(R.id.home_column_item_comment2);
            holder.time2 = (TextView) convertView.findViewById(R.id.home_column_item_time2);
            holder.introduce2 = (TextView) convertView.findViewById(R.id.home_column_item_introduce2);

            holder.leftLL2 = (LinearLayout) convertView.findViewById(R.id.left_layout2);
            holder.img3 = (ImageView) convertView.findViewById(R.id.home_column_item_img3);
            holder.view3 = (TextView) convertView.findViewById(R.id.home_column_item_view3);
            holder.flower3 = (TextView) convertView.findViewById(R.id.home_column_item_flower3);
            holder.comment3 = (TextView) convertView.findViewById(R.id.home_column_item_comment3);
            holder.time3 = (TextView) convertView.findViewById(R.id.home_column_item_time3);
            holder.introduce3 = (TextView) convertView.findViewById(R.id.home_column_item_introduce3);

            holder.rightLL2 = (LinearLayout) convertView.findViewById(R.id.right_layout2);
            holder.img4 = (ImageView) convertView.findViewById(R.id.home_column_item_img4);
            holder.view4 = (TextView) convertView.findViewById(R.id.home_column_item_view4);
            holder.flower4 = (TextView) convertView.findViewById(R.id.home_column_item_flower4);
            holder.comment4 = (TextView) convertView.findViewById(R.id.home_column_item_comment4);
            holder.time4 = (TextView) convertView.findViewById(R.id.home_column_item_time4);
            holder.introduce4 = (TextView) convertView.findViewById(R.id.home_column_item_introduce4);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).getIcon_pic().trim().equals("")) {
            holder.titleIV.setVisibility(View.VISIBLE);
            holder.flagIV.setVisibility(View.GONE);
        } else {
            holder.titleIV.setVisibility(View.GONE);
            holder.flagIV.setVisibility(View.VISIBLE);
            exApplication.imageLoader.displayImage(list.get(position).getIcon_pic(), holder.flagIV, exApplication.getOptions());
        }
        holder.titleTV.setText(list.get(position).getTitle());
        holder.titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewAssortActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title", list.get(position).getTitle());
                intent.putExtra("more_mark", list.get(position).getMore_mark());
                intent.putExtra("previous", "HomeColumn");
                System.out.println("title=====" + list.get(position).getTitle());
                System.out.println("more_mark=====" + list.get(position).getMore_mark());
                context.startActivity(intent);

//                updateUmen(position);
            }
        });

        final List<VideoEntity> videoList = list.get(position).getColumnList();

        exApplication.imageLoader.displayImage(videoList.get(0).getFlagPath(), holder.img1, exApplication.getOptions());
        holder.view1.setText(videoList.get(0).getViewCount());
        holder.flower1.setText(videoList.get(0).getFlower());
        holder.comment1.setText(videoList.get(0).getComment());
        holder.time1.setText(videoList.get(0).getTime());
        holder.introduce1.setText(videoList.get(0).getTitle());
        holder.leftLL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", videoList.get(0).getId());
                context.startActivity(intent);
            }
        });

        exApplication.imageLoader.displayImage(videoList.get(1).getFlagPath(), holder.img2, exApplication.getOptions());
        holder.view2.setText(videoList.get(1).getViewCount());
        holder.flower2.setText(videoList.get(1).getFlower());
        holder.comment2.setText(videoList.get(1).getComment());
        holder.time2.setText(videoList.get(1).getTime());
        holder.introduce2.setText(videoList.get(1).getTitle());
        holder.rightLL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", videoList.get(1).getId());
                context.startActivity(intent);
            }
        });

        exApplication.imageLoader.displayImage(videoList.get(2).getFlagPath(), holder.img3, exApplication.getOptions());
        holder.view3.setText(videoList.get(2).getViewCount());
        holder.flower3.setText(videoList.get(2).getFlower());
        holder.comment3.setText(videoList.get(2).getComment());
        holder.time3.setText(videoList.get(2).getTime());
        holder.introduce3.setText(videoList.get(2).getTitle());
        holder.leftLL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", videoList.get(2).getId());
                context.startActivity(intent);
            }
        });

        exApplication.imageLoader.displayImage(videoList.get(3).getFlagPath(), holder.img4, exApplication.getOptions());
        holder.view4.setText(videoList.get(3).getViewCount());
        holder.flower4.setText(videoList.get(3).getFlower());
        holder.comment4.setText(videoList.get(3).getComment());
        holder.time4.setText(videoList.get(3).getTime());
        holder.introduce4.setText(videoList.get(3).getTitle());
        holder.rightLL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(context, "当前网络断开，请重新链接后重试！");
                    return;
                }
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", videoList.get(3).getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        RelativeLayout titleLayout;
        ImageView titleIV;
        ImageView flagIV;
        TextView titleTV;
        TextView moreTV;

        LinearLayout leftLL1;
        ImageView img1;
        TextView view1;//播放数
        TextView flower1;//喜好数
        TextView comment1;//留言数
        TextView time1;//视频时长
        TextView introduce1;//标题

        LinearLayout rightLL1;
        ImageView img2;
        TextView view2;//播放数
        TextView flower2;//喜好数
        TextView comment2;//留言数
        TextView time2;//视频时长
        TextView introduce2;//标题

        LinearLayout leftLL2;
        ImageView img3;
        TextView view3;//播放数
        TextView flower3;//喜好数
        TextView comment3;//留言数
        TextView time3;//视频时长
        TextView introduce3;//标题

        LinearLayout rightLL2;
        ImageView img4;
        TextView view4;//播放数
        TextView flower4;//喜好数
        TextView comment4;//留言数
        TextView time4;//视频时长
        TextView introduce4;//标题
    }

}