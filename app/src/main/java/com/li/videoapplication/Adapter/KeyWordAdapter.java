package com.li.videoapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.KeyWord;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2015/8/17 0017.
 * <p>
 * 关键字适配器*
 */
public class KeyWordAdapter extends BaseAdapter {
    private List<KeyWord> mList;
    private Context mContext;
    ViewHolder holder = null;
    private LayoutInflater inflater;

    private Random random = new Random();
    private int[] tvColor = {Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED};

    public KeyWordAdapter(Context context, List<KeyWord> list) {
        this.mContext = context;
        this.mList = list;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.keyword_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.keyword_text);

            int num1 = random.nextInt(10);
            int num2 = random.nextInt(5);
            if (num1 == position) {
                holder.textView.setTextColor(tvColor[num2]);
            } else {
                holder.textView.setTextColor(tvColor[0]);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mList.get(position).getWord());

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }


}
