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
import com.li.videoapplication.activity.GameActivity;
import com.li.videoapplication.activity.NewAssortActivity;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.HomeHotEntity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.StringUtils;

import java.util.List;

/**
 * 首页热门游戏、热门分类适配器
 * Created by li on 2014/8/15.
 */
public class HomeHotAdapter extends BaseAdapter {

    private List<HomeHotEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private int width;
    private RelativeLayout.LayoutParams layoutParams;

    public HomeHotAdapter(Context context, List<HomeHotEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        layoutParams = new RelativeLayout.LayoutParams(width / 2, (width / 32) * 9);
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.home_item2, null);
            holder = new ViewHolder();
            holder.titleLayout = (RelativeLayout) convertView.findViewById(R.id.title_layout);
            holder.titleIV = (ImageView) convertView.findViewById(R.id.title_iv);
            holder.titleTv = (TextView) convertView.findViewById(R.id.home_item2_title_tv);
            holder.moreTv = (TextView) convertView.findViewById(R.id.home_item2_more_tv);

            holder.leftLayout = (LinearLayout) convertView.findViewById(R.id.left_layout);
            holder.imgeIv1 = (ImageView) convertView.findViewById(R.id.home_item_img1);
            holder.imgeIv1.setLayoutParams(layoutParams);
//            holder.flowerTv1=(TextView)convertView.findViewById(R.id.home_item_flower1);
//            holder.commentTv1=(TextView)convertView.findViewById(R.id.home_item_comment1);
            holder.timeTv1 = (TextView) convertView.findViewById(R.id.home_item_time1);
            holder.contentTv1 = (TextView) convertView.findViewById(R.id.home_item_introduce1);
            holder.playTimesTv1 = (TextView) convertView.findViewById(R.id.home_item_playcount1);

            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.right_layout);
            holder.imgeIv2 = (ImageView) convertView.findViewById(R.id.home_item_img2);
            holder.imgeIv2.setLayoutParams(layoutParams);
//            holder.flowerTv2=(TextView)convertView.findViewById(R.id.home_item_flower2);
//            holder.commentTv2=(TextView)convertView.findViewById(R.id.home_item_comment2);
            holder.timeTv2 = (TextView) convertView.findViewById(R.id.home_item_time2);
            holder.contentTv2 = (TextView) convertView.findViewById(R.id.home_item_introduce2);
            holder.playTimesTv2 = (TextView) convertView.findViewById(R.id.home_item_playcount2);

            holder.leftLayout2 = (LinearLayout) convertView.findViewById(R.id.left_layout2);
            holder.imgeIv3 = (ImageView) convertView.findViewById(R.id.home_item_img3);
            holder.imgeIv3.setLayoutParams(layoutParams);
//            holder.flowerTv3=(TextView)convertView.findViewById(R.id.home_item_flower3);
//            holder.commentTv3=(TextView)convertView.findViewById(R.id.home_item_comment3);
            holder.timeTv3 = (TextView) convertView.findViewById(R.id.home_item_time3);
            holder.contentTv3 = (TextView) convertView.findViewById(R.id.home_item_introduce3);
            holder.playTimesTv3 = (TextView) convertView.findViewById(R.id.home_item_playcount3);

            holder.rightLayout2 = (LinearLayout) convertView.findViewById(R.id.right_layout2);
            holder.imgeIv4 = (ImageView) convertView.findViewById(R.id.home_item_img4);
            holder.imgeIv4.setLayoutParams(layoutParams);
//            holder.flowerTv4=(TextView)convertView.findViewById(R.id.home_item_flower4);
//            holder.commentTv4=(TextView)convertView.findViewById(R.id.home_item_comment4);
            holder.timeTv4 = (TextView) convertView.findViewById(R.id.home_item_time4);
            holder.contentTv4 = (TextView) convertView.findViewById(R.id.home_item_introduce4);
            holder.playTimesTv4 = (TextView) convertView.findViewById(R.id.home_item_playcount4);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final HomeHotEntity hotEntity = list.get(position);
        final List<VideoEntity> videoList = list.get(position).getHotList();
        holder.titleTv.setText(hotEntity.getColumn());
        holder.titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (hotEntity.getColumnId().equals("")) {
                    System.out.println("column========" + hotEntity.getColumnId());
                    intent = new Intent(context, GameActivity.class);
                    intent.putExtra("title", hotEntity.getColumn());
                    context.startActivity(intent);
                } else {
                    intent = new Intent(context, NewAssortActivity.class);
                    intent.putExtra("id", hotEntity.getColumnId());
                    intent.putExtra("title", hotEntity.getColumn());
                    context.startActivity(intent);
                }
                updateUmen(position);
            }
        });
//        holder.flowerTv1.setText(videoList.get(0).getFlower());
//        holder.commentTv1.setText(videoList.get(0).getComment());
        holder.timeTv1.setText(videoList.get(0).getTime());
        holder.contentTv1.setText(videoList.get(0).getTitle_content());
        String playcount = videoList.get(0).getViewCount();
        holder.playTimesTv1.setText(StringUtils.turnViewCount(playcount));
        if (!videoList.get(0).getSimg_url().equals("")) {
            exApplication.imageLoader.displayImage(videoList.get(0).getSimg_url(), holder.imgeIv1, exApplication.getOptions());
            holder.imgeIv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("id", videoList.get(0).getId());
                    intent.putExtra("title", videoList.get(0).getTitle_content());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.imgeIv1.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
        }
        holder.contentTv1.setText(videoList.get(0).getTitle_content());


//        holder.flowerTv2.setText(videoList.get(1).getFlower());
//        holder.commentTv2.setText(videoList.get(1).getComment());
        holder.timeTv2.setText(videoList.get(1).getTime());
        holder.contentTv2.setText(videoList.get(1).getTitle_content());
        playcount = videoList.get(1).getViewCount();
        holder.playTimesTv2.setText(StringUtils.turnViewCount(playcount));
        if (!videoList.get(1).getSimg_url().equals("")) {
            exApplication.imageLoader.displayImage(videoList.get(1).getSimg_url(), holder.imgeIv2, exApplication.getOptions());
            holder.imgeIv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("id", videoList.get(1).getId());
                    intent.putExtra("title", videoList.get(1).getTitle_content());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.imgeIv2.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
        }
        holder.contentTv2.setText(videoList.get(1).getTitle_content());


//        holder.flowerTv3.setText(videoList.get(2).getFlower());
//        holder.commentTv3.setText(videoList.get(2).getComment());
        holder.timeTv3.setText(videoList.get(2).getTime());
        holder.contentTv3.setText(videoList.get(2).getTitle_content());
        playcount = videoList.get(2).getViewCount();
        holder.playTimesTv3.setText(StringUtils.turnViewCount(playcount));
        if (!videoList.get(2).getSimg_url().equals("")) {
            exApplication.imageLoader.displayImage(videoList.get(2).getSimg_url(), holder.imgeIv3, exApplication.getOptions());
            holder.imgeIv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("id", videoList.get(2).getId());
                    intent.putExtra("title", videoList.get(2).getTitle_content());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.imgeIv3.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
        }
        holder.contentTv3.setText(videoList.get(2).getTitle_content());


//        holder.flowerTv4.setText(videoList.get(3).getFlower());
//        holder.commentTv4.setText(videoList.get(3).getComment());
        holder.timeTv4.setText(videoList.get(3).getTime());
        holder.contentTv4.setText(videoList.get(3).getTitle_content());
        playcount = videoList.get(3).getViewCount();
        holder.playTimesTv4.setText(StringUtils.turnViewCount(playcount));
        if (!videoList.get(3).getSimg_url().equals("")) {
            exApplication.imageLoader.displayImage(videoList.get(3).getSimg_url(), holder.imgeIv4, exApplication.getOptions());
            holder.imgeIv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("id", videoList.get(3).getId());
                    intent.putExtra("title", videoList.get(3).getTitle_content());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.imgeIv4.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
        }
        holder.contentTv4.setText(videoList.get(3).getTitle_content());

        return convertView;
    }


    private class ViewHolder {
        RelativeLayout titleLayout;
        ImageView titleIV;
        TextView titleTv;
        TextView moreTv;

        LinearLayout leftLayout;
        TextView playTimesTv1;
        ImageView imgeIv1;
        //        TextView flowerTv1;
//        TextView commentTv1;
        TextView timeTv1;
        TextView contentTv1;

        LinearLayout rightLayout;
        TextView playTimesTv2;
        ImageView imgeIv2;
        //        TextView flowerTv2;
//        TextView commentTv2;
        TextView timeTv2;
        TextView contentTv2;

        LinearLayout leftLayout2;
        TextView playTimesTv3;
        ImageView imgeIv3;
        //        TextView flowerTv3;
//        TextView commentTv3;
        TextView timeTv3;
        TextView contentTv3;

        LinearLayout rightLayout2;
        TextView playTimesTv4;
        ImageView imgeIv4;
        //        TextView flowerTv4;
//        TextView commentTv4;
        TextView timeTv4;
        TextView contentTv4;

    }

    private void updateUmen(int position) {
        switch (position) {
            case 0:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "海岛奇兵");
                break;
            case 1:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "自由之战");
                break;
            case 2:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "赛车跑酷");
                break;
            case 3:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "策略塔防");
                break;
            case 4:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "卡牌游戏");
                break;
            case 5:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "飞行射击");
                break;
            case 6:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "角色扮演");
                break;
            case 7:
                ExApplication.upUmenEventValueAssort(context, "首页游戏分类", "game_categories", "街机格斗");
                break;
        }
    }
}
