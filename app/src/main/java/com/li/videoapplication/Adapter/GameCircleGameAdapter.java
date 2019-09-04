package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.li.videoapplication.activity.GameCircleDetailActivity;
import com.li.videoapplication.entity.Game;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/6.
 * 游戏圈子 游戏列表适配器
 */
public class GameCircleGameAdapter extends BaseAdapter {

    private Context context;
    private List<Game> list;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ExApplication exApplication;
    private int lastMark;

    public GameCircleGameAdapter(Context context, List<Game> list) {
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
            convertView = inflater.inflate(R.layout.activity_game_circle_game_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.game_circle_game_img);
            holder.nameTv = (TextView) convertView.findViewById(R.id.game_circle_game_name);
            holder.typeTv = (TextView) convertView.findViewById(R.id.game_circle_game_type);
            holder.joinCountTv = (TextView) convertView.findViewById(R.id.game_circle_game_join_count);
            holder.joinTv = (TextView) convertView.findViewById(R.id.game_circle_game_join);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lastMark = Integer.parseInt(list.get(position).getMark());

        holder.nameTv.setText(list.get(position).getGroup_name());
        holder.typeTv.setText("类型:" + list.get(position).getGroup_type());
        holder.joinCountTv.setText(list.get(position).getAttention_num() + " 关注");
        exApplication.imageLoader.displayImage(list.get(position).getFlagPath(), holder.flag, exApplication.getOptions());
        holder.joinTv.setVisibility(View.VISIBLE);
        holder.joinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExApplication.MEMBER_ID.equals("") || ExApplication.MEMBER_ID == null) {
                    ToastUtils.showToast(context, "请先登录！");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new joinGameCircleTask(position, (TextView) v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new joinGameCircleTask(position, (TextView) v).execute();
                }
            }
        });
        if (lastMark == 0) {
            holder.joinTv.setBackgroundResource(R.drawable.corner_red_stroke);
            holder.joinTv.setText("+加入");
        } else {
            holder.joinTv.setBackgroundResource(R.drawable.corner_gray_stroke);
            holder.joinTv.setText("已加入");
        }
        return convertView;
    }

    /**
     * 加入游戏圈子
     */
    private class joinGameCircleTask extends AsyncTask<Void, Void, String> {

        int position;
        TextView joinTv;

        public joinGameCircleTask(int position, TextView joinTv) {
            this.position = position;
            this.joinTv = joinTv;
        }

        @Override
        protected String doInBackground(Void... voids) {
            boolean b = JsonHelper.joinGameCircle(list.get(position).getGroup_id(), ExApplication.MEMBER_ID);
            if (b) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("s")) {
                if (list.get(position).getMark().equals("0")) {
                    joinTv.setBackgroundResource(R.drawable.corner_gray_stroke);
                    joinTv.setText("已加入");
                    ToastUtils.showToast(context, "加入成功");
                    list.get(position).setMark("1");

                    /**加入成功后，直接跳转到游戏圈子
                     * Context中有一个startActivity方法，Activity继承自Context，重载了startActivity方法。
                     * 如果使用Activity的startActivity方法，不会有任何限制，
                     * 而如果使用Context的startActivity方法的话，就需要开启一个新的task，
                     * 遇到异常，都是因为使用了Context的startActivity方法。
                     * 解决办法是，加一个flag。
                     **/
                    Intent intent = new Intent(context, GameCircleDetailActivity.class);
                    intent.putExtra("group_id", list.get(position).getGroup_id());
                    intent.putExtra("type_name", list.get(position).getGroup_type());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    joinTv.setBackgroundResource(R.drawable.corner_red_stroke);
                    joinTv.setText("+加入");
                    ToastUtils.showToast(context, "退出圈子");
                    list.get(position).setMark("0");
                }
            }
        }
    }

    class ViewHolder {
        ImageView flag;
        TextView nameTv;
        TextView typeTv;
        TextView joinCountTv;
        TextView joinTv;
    }
}
