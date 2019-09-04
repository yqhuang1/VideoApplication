package com.li.videoapplication.Adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.MessageEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/6.
 * 我的消息适配器
 */
public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<MessageEntity> list;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ExApplication exApplication;

    public MessageAdapter(Context context, List<MessageEntity> list) {
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
            convertView = inflater.inflate(R.layout.message_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.message_item_flag);
            holder.nameTv = (TextView) convertView.findViewById(R.id.message_item_name);
            holder.contentTv = (TextView) convertView.findViewById(R.id.message_item_content);
            holder.timeTv = (TextView) convertView.findViewById(R.id.message_item_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTv.setText(list.get(position).getNickname());
        holder.contentTv.setText(list.get(position).getContent());
        exApplication.imageLoader.displayImage(list.get(position).getAvatar(), holder.flag, exApplication.getOptions());
        holder.timeTv.setText(list.get(position).getTime());
        return convertView;
    }

    /**
     * 关注大神
     */
    private class submitFocusTask extends AsyncTask<Void, Void, String> {

        int position;
        ImageView focusIv;

        public submitFocusTask(int position, ImageView focusIv) {
            this.position = position;
            this.focusIv = focusIv;
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
//                focusIv.setBackgroundResource(R.drawable.focus_success);
                focusIv.setClickable(false);
                ToastUtils.showToast(context, "关注成功");
            }
        }
    }

    class ViewHolder {
        ImageView flag;
        TextView nameTv;
        TextView contentTv;
        TextView timeTv;
    }
}
