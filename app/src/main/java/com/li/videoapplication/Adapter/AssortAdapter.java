package com.li.videoapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.GameType;

import java.util.List;

/**
 * Created by li on 2014/8/15.
 * 找游戏 游戏分类 适配器
 */
public class AssortAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<GameType> list;
    private ExApplication exApplication;

    public AssortAdapter(Context context, List<GameType> list) {
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.game_assort_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.game_assort_item_iv);
            holder.titleTv = (TextView) convertView.findViewById(R.id.game_assort_item_tv);

            //设置参数提到这里，只有第一次的时候会执行，之后会复用
            holder.titleTv.setText(list.get(position).getName());
            exApplication.imageLoader.displayImage(list.get(position).getFlag(), holder.img, exApplication.getOptions());

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.img.setImageDrawable(context.getResources().getDrawable(icon[position]));
//        if (list.get(position).getFlagPath().equals("")||list.get(position).getFlagPath()==null) {
//            holder.img.setBackgroundResource(R.drawable.radio_fra_bottom_bg);
//            System.out.println("1===========");
//            System.out.println("1==========="+list.get(position).getFlagPath());
//        }else{
////            ExApplication.imageLoader.displayImage(list.get(position).getImgPath(),holder.img,
////                    ExApplication.getOptions());
//        }
        return convertView;
    }

    private class ViewHolder {
        ImageView img;
        TextView titleTv;
    }
}
