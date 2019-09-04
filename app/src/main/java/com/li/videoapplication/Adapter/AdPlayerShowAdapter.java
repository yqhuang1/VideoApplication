package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.RecommendEntity;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/5/7.
 * 主页首页 玩家秀
 * 广告条内容适配器
 */
public class AdPlayerShowAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<RecommendEntity> files;
    private ExApplication exApplication;

    public AdPlayerShowAdapter(Context context, List<RecommendEntity> files) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.files = files;
        exApplication = new ExApplication(context);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.head_item, null);
            holder = new ViewHolder();
            holder.ad_imge = (ImageView) convertView.findViewById(R.id.head_imgView);
//            holder.titleTv = (TextView) convertView.findViewById(R.id.head_item_focus_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d("files.size()", files.size() + "");

        if (files.size() != 0) {
//            holder.titleTv.setText(files.get(position % files.size()).getTitle());
            if (!files.get(position % files.size()).getFlagPath().equals("")) {
                Log.d("imgPath", files.get(position % files.size()).getFlagPath() + files.get(position % files.size()).getFlagPath());
                exApplication.imageLoader.displayImage(files.get(position % files.size()).getFlagPath(),
                        holder.ad_imge, exApplication.getOptions());
            } else {
                holder.ad_imge.setImageDrawable(mContext.getResources().getDrawable(R.drawable.radio_fra_bottom_bg));
            }

        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (files.size() != 0) {
                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
                    intent.putExtra("id", files.get(position % files.size()).getVideo_id());
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView ad_imge;
//        TextView titleTv;
    }
}
