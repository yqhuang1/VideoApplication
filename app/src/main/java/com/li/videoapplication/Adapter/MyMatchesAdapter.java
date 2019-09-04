package com.li.videoapplication.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.ShareActivity;
import com.li.videoapplication.entity.MatchEntity;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import static com.li.videoapplication.R.id;

/**
 * Created by li on 2015/8/21.
 * <p/>
 * 视频上传 我参加的可上传视频的活动列表 适配器
 */
public class MyMatchesAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflater;
    private List<MatchEntity> list;
    private ExApplication exApplication;


    public MyMatchesAdapter(Context context, List<MatchEntity> list) {
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
            convertView = inflater.inflate(R.layout.my_matches_item, null);
            holder = new ViewHolder();
            holder.itemRl = (RelativeLayout) convertView.findViewById(id.my_matches_item_RL);
            holder.imgeIv = (ImageView) convertView.findViewById(id.my_matches_item_img);
            holder.titleTv = (TextView) convertView.findViewById(id.my_matches_item_title);
            holder.contentTxt = (TextView) convertView.findViewById(id.my_matches_item_contentTxt);
            holder.stateIv = (ImageView) convertView.findViewById(id.my_matches_item_state_iv);
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

        if (list.get(position).isCleckedFlag()) {
            holder.stateIv.setImageResource(MResource.getIdByName(context,
                    "drawable", "fm_checkbox_true"));
        } else {
            holder.stateIv.setImageResource(MResource.getIdByName(context,
                    "drawable", "fm_checkbox_false"));
        }

        holder.itemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list.get(position).isCleckedFlag()) {//原来就被选中
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setCleckedFlag(false);
                    }
                    ShareActivity.matchID = null;
                    ShareActivity.match_name = null;
                    ShareActivity.gameName = null;
                    ShareActivity.game_name.setText("");
                } else if (!list.get(position).isCleckedFlag()) {//原来未被选中
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setCleckedFlag(false);
                    }
                    list.get(position).setCleckedFlag(true);
                    ShareActivity.matchID = list.get(position).getMatch_id() + "";
                    ShareActivity.match_name = list.get(position).getName();
                    ShareActivity.gameName = list.get(position).getGame_name();
                    ShareActivity.game_name.setText(list.get(position).getGame_name());
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    private class ViewHolder {
        RelativeLayout itemRl;
        ImageView imgeIv;
        TextView titleTv;
        TextView contentTxt;
        ImageView stateIv;
    }

}
