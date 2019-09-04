package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.utils.RecordCheckUtils;
import com.li.videoapplication.utils.StringUtils;
import com.li.videoapplication.utils.TextTypeUtils;

import java.util.List;

/**
 * Created by li on 2014/8/18.
 * 浏览视频 观看记录 适配器
 */
public class RecordAdapter extends BaseAdapter {

    private Context context;
    private List<VideoEntity> list;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private Typeface typeface;
    //判断是否是第一次点击,onLongclck 会影响 onclick
    public boolean isFirstCheck = false;
    private OnCheckListener listener;
    private TextView titleTv;


    public RecordAdapter(Context context, List<VideoEntity> list, OnCheckListener listener, TextView titleTv) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        typeface = TextTypeUtils.getTypeface(context);
        this.listener = listener;
        this.titleTv = titleTv;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
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
            convertView = inflater.inflate(R.layout.record_item, null);
            holder = new ViewHolder();
            holder.imgeIv = (ImageView) convertView.findViewById(R.id.video_item_img);
            holder.stateIv = (ImageView) convertView.findViewById(R.id.record_item_select_state_iv);
            holder.playTimesTv = (TextView) convertView.findViewById(R.id.video_item_playcount);
//            holder.flowerTv=(TextView)convertView.findViewById(R.id.video_item_flower);
//            holder.commentTv=(TextView)convertView.findViewById(R.id.video_item_comment);
            holder.timeTv = (TextView) convertView.findViewById(R.id.video_item_time);
            holder.contentTv = (TextView) convertView.findViewById(R.id.video_item_content);
//            holder.titleTv=(TextView)convertView.findViewById(R.id.video_item_title);
//            holder.checkTv=(TextView)convertView.findViewById(R.id.collect_item_gv_item_check_img);
//            holder.checkTv.setTypeface(typeface);
            holder.stateIv.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.stateIv.setTag(position);
        }

//        ExApplication.imageLoader.displayImage(list.get(position).getFlagPath(),holder.imgeIv,
//                ExApplication.getOptions());

        if (list.size() > 0) {
            exApplication.imageLoader.displayImage(list.get(position).getSimg_url(),
                    holder.imgeIv, exApplication.getOptions());
        }

//        holder.flowerTv.setText(list.get(position).getFlower());
//        holder.commentTv.setText(list.get(position).getComment());
        holder.timeTv.setText(list.get(position).getTime());
        holder.contentTv.setText(list.get(position).getTitle_content());
//        holder.titleTv.setText(list.get(position).getTitle());
        holder.playTimesTv.setText(StringUtils.turnViewCount(list.get(position).getViewCount()));

        holder.imgeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("", RecordCheckUtils.isCheckState ? "true" : "false");
                if (RecordCheckUtils.isCheckState) {
                    if (isFirstCheck) {
                        isFirstCheck = false;//已经过了长按的第一次
                    } else {
                        int i = (Integer) holder.stateIv.getTag();
                        if ("true".equals(list.get(i).getIsCheck())) {
                            holder.stateIv.setBackgroundResource(R.drawable.unselect_bg);
                            RecordCheckUtils.removeCollectProduce(list.get(i));
                            list.get(i).setIsCheck("false");
                        } else {
                            holder.stateIv.setBackgroundResource(R.drawable.select_bg);
                            RecordCheckUtils.addCollectProduct(list.get(i));
//                            int i=(Integer)holder.stateIv.getTag();
                            list.get(i).setIsCheck("true");
                        }
                        setTitleTv("已选中 " + RecordCheckUtils.productList.size() + " 项");
                    }
                } else {
                    if (list.get(position).getId().equals("")) {
                        return;
                    }
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("id", list.get(position).getId());
                    intent.putExtra("title", list.get(position).getTitle_content());
                    context.startActivity(intent);
                }
            }
        });

        if (RecordCheckUtils.isCheckState) {
            holder.stateIv.setVisibility(View.VISIBLE);
        } else {
            holder.stateIv.setVisibility(View.GONE);
        }

//        holder.imgeIv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (!RecordCheckUtils.isCheckState){
//                    RecordCheckUtils.isCheckState=true;
//                    isFirstCheck=true;
//                    holder.checkTv.setVisibility(View.VISIBLE);
//                    int i=(Integer)holder.checkTv.getTag();
//                    list.get(i).setIsCheck("true");
//                    RecordCheckUtils.addCollectProduct(list.get(position));
//                    listener.onCheck();
//                }
//                return false;
//            }
//        });

        String value = list.get(position).getIsCheck();
        if (value != null && !"".equals(value) && "true".equals(value)) {
            holder.stateIv.setBackgroundResource(R.drawable.select_bg);
        } else {
            holder.stateIv.setBackgroundResource(R.drawable.unselect_bg);
        }

        return convertView;
    }

    public void setTitleTv(String str) {
        int fstart = str.length() - 4;
        int fend = str.length() - 2;
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.RED), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        titleTv.setText(style);
    }

    private class ViewHolder {
        TextView playTimesTv;
        ImageView imgeIv;
        ImageView stateIv;
        //        TextView flowerTv;
//        TextView commentTv;
        TextView timeTv;
        TextView contentTv;
//        TextView titleTv;
//        TextView checkTv;
    }

    /**
     * 选中接口
     */
    public interface OnCheckListener {
        public void onCheck();
    }

}
