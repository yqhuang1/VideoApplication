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
import com.li.videoapplication.entity.MasterEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/6.
 * 大神专栏 适配器
 */
public class MasterColumnAdapter extends BaseAdapter {

    private Context context;
    private List<MasterEntity> list;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ExApplication exApplication;
    private int lastMark;

    public MasterColumnAdapter(Context context, List<MasterEntity> list) {
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.master_column_list_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.master_column_list_item_flag);
            holder.nameTv = (TextView) convertView.findViewById(R.id.master_column_list_item_name);
            holder.manifestoTv = (TextView) convertView.findViewById(R.id.master_column_list_item_manifesto);
            holder.focusTv = (TextView) convertView.findViewById(R.id.master_column_list_item_focus_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lastMark = list.get(position).getMark();

        holder.nameTv.setText(list.get(position).getNickname());
        holder.manifestoTv.setText(list.get(position).getManifesto());
        exApplication.imageLoader.displayImage(list.get(position).getFlagPath(), holder.flag, exApplication.getOptions());
        holder.focusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExApplication.MEMBER_ID.equals("") || ExApplication.MEMBER_ID == null) {
                    ToastUtils.showToast(context, "请先登录！");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitFocusTask(position, (TextView) v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitFocusTask(position, (TextView) v).execute();
                }
            }
        });
        if (lastMark == 0) {
            holder.focusTv.setBackgroundResource(R.drawable.corner_red_stroke);
            holder.focusTv.setText("+关注");
        } else {
            holder.focusTv.setBackgroundResource(R.drawable.corner_gray_stroke);
            holder.focusTv.setText("已关注");
        }
        return convertView;
    }

    /**
     * 关注大神
     */
    private class submitFocusTask extends AsyncTask<Void, Void, String> {

        int position;
        TextView focusTv;

        public submitFocusTask(int position, TextView focusTv) {
            this.position = position;
            this.focusTv = focusTv;
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
                if (list.get(position).getMark() == 0) {
                    focusTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    focusTv.setText("已关注");
                    ToastUtils.showToast(context, "关注成功");
                    list.get(position).setMark(1);
                } else {
                    focusTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    focusTv.setText("+关注");
                    ToastUtils.showToast(context, "取消关注");
                    list.get(position).setMark(0);
                }
            }
        }
    }

    class ViewHolder {
        ImageView flag;
        TextView nameTv;
        TextView manifestoTv;
        TextView focusTv;
    }
}
