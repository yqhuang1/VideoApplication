package com.li.videoapplication.Adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.ExpertEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/29.
 */
public class ExpertAdapter extends BaseAdapter {

    private Context context;
    private List<ExpertEntity> list;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private String lastMark;

    public ExpertAdapter(Context context, List<ExpertEntity> list) {
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
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.expert_list_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.expert_list_item_flag);
            holder.nameTv = (TextView) convertView.findViewById(R.id.expert_list_item_name);
            holder.sexIv = (ImageView) convertView.findViewById(R.id.expert_list_item_sex);
            holder.videoTv = (TextView) convertView.findViewById(R.id.expert_list_item_video_tv);
            holder.fansTv = (TextView) convertView.findViewById(R.id.expert_list_item_fans_tv);
            holder.rankTv = (TextView) convertView.findViewById(R.id.expert_list_item_rank_tv);
            holder.manifestoTv = (TextView) convertView.findViewById(R.id.expert_list_item_manifesto);
            holder.focusTv = (TextView) convertView.findViewById(R.id.expert_list_item_focus_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lastMark = list.get(position).getMark();

        holder.nameTv.setText(list.get(position).getNickname());
        if (list.get(position).getSex().equals("0")) {
            holder.sexIv.setImageResource(R.drawable.sex_female);
        } else if (list.get(position).getSex().equals("1")) {
            holder.sexIv.setImageResource(R.drawable.sex_male);
        }
        holder.videoTv.setText("视频" + list.get(position).getNum());
        holder.fansTv.setText("粉丝" + list.get(position).getFans());
        holder.rankTv.setText("等级" + list.get(position).getRank());
        holder.manifestoTv.setText(list.get(position).getDescription());
        exApplication.imageLoader.displayImage(list.get(position).getAvatar(), holder.flag, exApplication.getOptions());
        holder.focusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExApplication.MEMBER_ID.equals("") || ExApplication.MEMBER_ID == null) {
                    ToastUtils.showToast(context, "请先登录！");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitFocusTask(position, (TextView) v, holder.fansTv).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitFocusTask(position, (TextView) v, holder.fansTv).execute();
                }
            }
        });
        if ("0".equals(lastMark)) {
            holder.focusTv.setBackgroundResource(R.drawable.corner_red_stroke);
            holder.focusTv.setText("+关注");
        } else {
            holder.focusTv.setBackgroundResource(R.drawable.corner_gray_stroke);
            holder.focusTv.setText("已关注");
        }
        return convertView;
    }

    /**
     * 加关注
     */
    private class submitFocusTask extends AsyncTask<Void, Void, String> {

        int position;
        int fans;
        TextView focusTv;
        TextView fansTv;

        public submitFocusTask(int position, TextView focusTv, TextView fansTv) {
            this.position = position;
            this.focusTv = focusTv;
            this.fansTv = fansTv;
            fans = Integer.parseInt(fansTv.getText().toString().split("丝")[1]);
        }

        @Override
        protected String doInBackground(Void... voids) {
            boolean b = JsonHelper.submitFocus(ExApplication.MEMBER_ID, list.get(position).getMember_id());
            if (b) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("s")) {
                if ("0".equals(list.get(position).getMark())) {
                    focusTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    focusTv.setText("已关注");
                    ToastUtils.showToast(context, "关注成功");
                    fans += 1;
                    fansTv.setText("粉丝" + fans);
                    list.get(position).setMark("1");
                } else {
                    focusTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    focusTv.setText("+关注");
                    ToastUtils.showToast(context, "取消关注");
                    fans -= 1;
                    fansTv.setText("粉丝" + fans);
                    list.get(position).setMark("0");
                }
            }
        }
    }

    class ViewHolder {
        ImageView flag;
        TextView nameTv;
        ImageView sexIv;
        TextView videoTv;
        TextView fansTv;
        TextView rankTv;
        TextView manifestoTv;
        TextView focusTv;
    }
}
