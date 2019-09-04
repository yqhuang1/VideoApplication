package com.li.videoapplication.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmscreenrecord.VideoList.ExternalImageLoader;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.activity.VideoManagerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/11 0011.
 * 视频管理页面 导入适配器
 */
public class ExtVideoListAdapter extends BaseAdapter {

    List<Boolean> checkList = null;
    List<VideoInfo> list = null;
    Context context;
    LayoutInflater inflater;
    ViewHolder holder = null;
    ExternalImageLoader imageLoader; // 外部视频缩略图加载
    private List<Boolean> exList;


    public ExtVideoListAdapter(List<VideoInfo> list, Context context) {

        checkList = new ArrayList<Boolean>();
        this.list = list;
        this.context = context;
        //imageLoaderLoad = new ExternalImageLoader(context);
        imageLoader = new ExternalImageLoader(context);
        inflater = LayoutInflater.from(context);
        exList = new ArrayList<Boolean>();

        for (int i = 0; i < list.size(); i++) {
            exList.add(VideoManagerActivity.spExtVideoCheck.getBoolean(list.get(i).getPath(), false));
        }
        for (int i = 0; i < list.size(); i++) {
            checkList.add(false);
        }

        VideoManagerActivity.listCheckToLead.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int postion) {
        return list.get(postion);
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public View getView(final int postion, View convertView,
                        ViewGroup parent) {
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(MResource.getIdByName(context,
                    "layout", "fm_video_item_load"), null);
            holder.image = (ImageView) convertView.findViewById(MResource
                    .getIdByName(context, "id", "iv_list_ext"));
            holder.title = (TextView) convertView.findViewById(MResource
                    .getIdByName(context, "id", "name_list_ext"));
            holder.state = (TextView) convertView.findViewById(MResource
                    .getIdByName(context, "id", "tv_list_ext"));
            holder.cb = (CheckBox) convertView.findViewById(MResource
                    .getIdByName(context, "id", "cb_list_ext"));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        File f = new File(list.get(postion).getPath());
        holder.title.setText(f.getName());
        imageLoader.DisplayImage(list.get(postion).getPath(), holder.image);

        // 对某个外部视频是否导入进行判断
        if (exList.get(postion)) {
            holder.cb.setVisibility(View.GONE);
            holder.state.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.VISIBLE);
            holder.state.setVisibility(View.GONE);
        }
        holder.cb.setId(postion);
        holder.cb.setChecked(checkList.get(postion));
        /*holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
			{
				if (isChecked)
				{
					int id = buttonView.getId();
					//将选择结果写入配置，以备下次读取
					SharedPreferences.Editor editor = VideoManager.spExtVideoCheck.edit();
					editor.putBoolean(list.get(id).getPath(), true);
					editor.commit();
					//将选择结果放入列表，用于刷新页面
					VideoManager.listCheckToLead.add(list.get(id));
					checkList.set(id, isChecked);
				}
				else
				{
					int id = buttonView.getId();
					SharedPreferences.Editor editor = VideoManager.spExtVideoCheck.edit();
					editor.remove(list.get(id).getPath());
					editor.commit();
					VideoManager.listCheckToLead.remove(list.get(id));
					checkList.set(id, isChecked);
				}
			}
		});*/
        holder.cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkList.get(postion)) {
                    checkList.set(postion, false);

                    VideoManagerActivity.listCheckToLead.remove(list.get(postion));

                } else {
                    checkList.set(postion, true);
                    //将选择结果放入列表，用于刷新页面
                    VideoManagerActivity.listCheckToLead.add(list.get(postion));
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView image;
        TextView title;
        TextView state;
        CheckBox cb;
    }
}