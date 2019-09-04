package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.li.videoapplication.DB.DBManager;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.DownloadVideo;


import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 断点离线下载 适配器
 */
public class OLDLAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<DownloadVideo> list;
    private ExApplication exApplication;
    private DBManager dbManager;

    public OLDLAdapter(Context context, List<DownloadVideo> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        dbManager = new DBManager(context);
    }


    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public void refresh(List<DownloadVideo> list) {
        this.list = list;
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.oldl_item, null);
            holder = new ViewHolder();
            holder.imgeIv = (ImageView) convertView.findViewById(R.id.oldl_Image);
            holder.titleTv = (TextView) convertView.findViewById(R.id.oldl_Title);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.oldl_ProgressBar);
            holder.percentTv = (TextView) convertView.findViewById(R.id.oldl_Percent);
            holder.state = (TextView) convertView.findViewById(R.id.oldl_State);
            holder.operate = (TextView) convertView.findViewById(R.id.oldl_operate);
            holder.operate.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.operate.setTag(position);
        }

        if (list.get(position).getImgUrl().equals("") || list.get(position).getImgUrl() == null) {
            holder.imgeIv.setImageResource(R.drawable.tubiao);
        } else {
            holder.imgeIv.setVisibility(View.VISIBLE);
            exApplication.imageLoader.displayImage(list.get(position).getImgUrl(), holder.imgeIv, exApplication.getOptions());
        }

        holder.titleTv.setText(list.get(position).getTitle());

        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(0);

        String state = dbManager.queryDownloadState(list.get(position).getVideo_id());
        if (state.equals("SUCCESS")) {
            holder.progressBar.setProgress(100);
            holder.percentTv.setText("100%");
            holder.state.setText("下载完成");
            holder.operate.setText("下载成功");
            holder.operate.setClickable(false);
        }

        holder.imgeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.operate.getText().equals("完成")) {
                    System.out.println("list.get(position).getPlayUrl()===" + list.get(position).getPlayUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String type = "video/mp4";
                    Uri uri = Uri.parse(list.get(position).getPlayUrl());
                    intent.setDataAndType(uri, type);
                    context.startActivity(intent);
                }
            }
        });

        holder.operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.operate.getText().equals("暂停")) {//下载状态中，操作按钮显示暂停，启用暂停下载

                    holder.operate.setText("下载");
                } else if (holder.operate.getText().equals("下载")) {//暂停状态中，操作按钮显示下载，启用继续下载

                    holder.operate.setText("暂停");
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView imgeIv;
        TextView titleTv;
        ProgressBar progressBar;
        TextView percentTv;
        TextView state;
        TextView operate;
    }

}
