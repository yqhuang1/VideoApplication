package com.li.videoapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.GameType;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/1/21.
 * 游戏圈子 分类列表适配器
 */
public class GameCircleTypeAdapter extends BaseAdapter {

    private Context context;
    private List<GameType> list;
    private LayoutInflater inflater;
    public ViewHolder holder;

    public GameCircleTypeAdapter(Context context, List<GameType> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void update(List<GameType> list) {
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_game_circle_type_item, null);
            holder = new ViewHolder();
            holder.lineIv = (ImageView) convertView.findViewById(R.id.game_circle_type_item_line);
            holder.gameNameTv = (TextView) convertView.findViewById(R.id.game_circle_type_item_name);
            holder.gameTypeRl = (RelativeLayout) convertView.findViewById(R.id.game_circle_type_item_layout);
            holder.lineIv.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.lineIv.setTag(position);
        }
        holder.gameNameTv.setText(list.get(position).getName());

        if (list.get(position).getIsCheck()) {
            holder.gameTypeRl.setBackgroundColor(context.getResources().getColor(R.color.game_circle_type_selector));
            holder.gameNameTv.setTextColor(context.getResources().getColor(R.color.game_circle_type_name_selector));
            holder.lineIv.setVisibility(View.GONE);
        } else {
            holder.gameTypeRl.setBackgroundColor(context.getResources().getColor(R.color.game_circle_type_default));
            holder.gameNameTv.setTextColor(context.getResources().getColor(R.color.game_circle_type_name_default));
            holder.lineIv.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView gameNameTv;
        ImageView lineIv;
        RelativeLayout gameTypeRl;
    }

}
