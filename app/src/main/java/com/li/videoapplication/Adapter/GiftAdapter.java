package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.GiftAtuoDetailActivity;
import com.li.videoapplication.entity.GiftEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2014/8/15.
 * 礼包列表 适配器
 * 主页左侧栏 我的礼包适配器
 * 搜索结果页面 相关礼包适配器
 */
public class GiftAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<GiftEntity> list;
    private ExApplication exApplication;
    private boolean isMygift;
    private int max, progress;

    private Animation animation;

    private Map<Integer, Boolean> isFrist;

//    private Animation itemAnim;

    public GiftAdapter(Context context, List<GiftEntity> list, boolean isMygift) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.isMygift = isMygift;
        exApplication = new ExApplication(context);

        animation = AnimationUtils.loadAnimation(context, R.anim.woniu_list_item);
        isFrist = new HashMap<>();
//        itemAnim = AnimationUtils.loadAnimation(context, R.anim.woniu_list_item);
    }


    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public void refresh(List<GiftEntity> list) {
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
            convertView = inflater.inflate(R.layout.gift_item, null);
            holder = new ViewHolder();
            holder.imgeIv = (ImageView) convertView.findViewById(R.id.gift_item_img);
            holder.titleTv = (TextView) convertView.findViewById(R.id.gift_item_title);
            holder.contentTv = (TextView) convertView.findViewById(R.id.gift_item_introduce);
            holder.countTv = (TextView) convertView.findViewById(R.id.gift_item_count);
            holder.getBtn = (Button) convertView.findViewById(R.id.gift_item_getBtn);
            holder.getBtn.setTag(position);
            holder.bar = (ProgressBar) convertView.findViewById(R.id.gift_item_progressbar);
            holder.getLl = (LinearLayout) convertView.findViewById(R.id.gift_item_getBtn_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.getBtn.setTag(position);
        }

        // 如果是第一次加载该view，则使用动画
        if (isFrist.get(position) == null || isFrist.get(position)) {

            convertView.startAnimation(animation);
            isFrist.put(position, false);
        }

//        convertView.setAnimation(itemAnim);

        if (list.get(position).getImgPath().equals("") || list.get(position).getImgPath() == null) {
            holder.imgeIv.setVisibility(View.GONE);
        } else {
            holder.imgeIv.setVisibility(View.VISIBLE);
            Log.e("gift_img", list.get(position).getImgPath());
//            ExApplication.imageLoader.displayImage(list.get(position).getImgPath(), holder.imgeIv,
//                    ExApplication.getOptions());
            exApplication.imageLoader.displayImage(list.get(position).getImgPath(), holder.imgeIv, exApplication.getOptions());
        }

        holder.titleTv.setText(list.get(position).getTitle());
        holder.contentTv.setText(list.get(position).getContent());
        //领取礼包的进度条
        max = Integer.parseInt(list.get(position).getNum());
        progress = Integer.parseInt(list.get(position).getCount());
        holder.countTv.setText((int) (((float) progress / max) * 100) + "%");
        holder.bar.setMax(max);
        holder.bar.setProgress(progress);

        if (isMygift) {      //个人中心我的礼包界面隐藏按钮
            holder.getLl.setVisibility(View.GONE);
        }

        holder.getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ExApplication.MEMBER_ID.equals("")) {
                    ToastUtils.showToast(context, "请先登录");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetGiftTask(holder, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetGiftTask(holder, position).execute();
                }
            }
        });


        String value = list.get(position).getActivity_code();

        if (!"".equals(value)) {            //根据礼包码字段设置礼包界面领取按钮状态
            holder.getBtn.setText("已领取");
            holder.getBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
            holder.getBtn.setClickable(false);
        } else {
            holder.getBtn.setText("领取");
            holder.getBtn.setBackgroundResource(R.drawable.corner_red_stroke);
        }
        return convertView;
    }


    private class ViewHolder {
        ImageView imgeIv;
        TextView titleTv;
        TextView contentTv;
        TextView countTv;
        Button getBtn;
        ProgressBar bar;

        LinearLayout getLl;
    }

    /**
     * 领取礼包
     */
    private class GetGiftTask extends AsyncTask<Void, Void, Boolean> {
        int position = 0;
        ViewHolder mholder;

        public GetGiftTask(ViewHolder mholder, int position) {
            this.position = position;
            this.mholder = mholder;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return JsonHelper.getGiftResponse(ExApplication.MEMBER_ID, list.get(position).getId());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                ToastUtils.showToast(context, "领取成功");
                mholder.getBtn.setText("已领取");
                mholder.getBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
                mholder.getBtn.setEnabled(false);

                int position = (Integer) mholder.getBtn.getTag();
                list.get(position).setHasTake("true");

                Intent intent = new Intent(context, GiftAtuoDetailActivity.class);
                intent.putExtra("id", list.get(position).getId());
                context.startActivity(intent);

            } else {
                ToastUtils.showToast(context, "领取失败");
            }
        }
    }
}
