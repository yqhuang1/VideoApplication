package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by 刘楠 on 2016/9/10 0010.18:06
 */
public class RefreshRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<GiftEntity> list;
    private ExApplication exApplication;
    private boolean isMygift;
    private int max, progress;
    private Map<Integer, Boolean> isFrist;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public RefreshRecyclerAdapter(Context context, List<GiftEntity> list, boolean isMygift) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;

        this.isMygift = isMygift;
        exApplication = new ExApplication(context);
        isFrist = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.gift_item, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            return itemViewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.slistview_footer, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            FooterViewHolder footViewHolder = new FooterViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ItemViewHolder) {

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.itemView.setTag(position);

            {
                if (list.get(position).getImgPath().equals("") || list.get(position).getImgPath() == null) {
                    itemViewHolder.imgeIv.setVisibility(View.GONE);
                } else {
                    itemViewHolder.imgeIv.setVisibility(View.VISIBLE);
                    Log.e("gift_img", list.get(position).getImgPath());
                    //            ExApplication.imageLoader.displayImage(list.get(position).getImgPath(), holder.imgeIv,
                    //                    ExApplication.getOptions());
                    exApplication.imageLoader.displayImage(list.get(position).getImgPath(), itemViewHolder.imgeIv, exApplication.getOptions());
                }

                itemViewHolder.titleTv.setText(list.get(position).getTitle());
                itemViewHolder.contentTv.setText(list.get(position).getContent());
                //领取礼包的进度条
                max = Integer.parseInt(list.get(position).getNum());
                progress = Integer.parseInt(list.get(position).getCount());
                itemViewHolder.countTv.setText((int) (((float) progress / max) * 100) + "%");
                itemViewHolder.bar.setMax(max);
                itemViewHolder.bar.setProgress(progress);

                if (isMygift) {      //个人中心我的礼包界面隐藏按钮
                    itemViewHolder.getLl.setVisibility(View.GONE);
                }

                itemViewHolder.getBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ExApplication.MEMBER_ID.equals("")) {
                            ToastUtils.showToast(mContext, "请先登录");
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new GetGiftTask(itemViewHolder, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new GetGiftTask(itemViewHolder, position).execute();
                        }
                    }
                });


                String value = list.get(position).getActivity_code();

                if (!"".equals(value)) {            //根据礼包码字段设置礼包界面领取按钮状态
                    itemViewHolder.getBtn.setText("已领取");
                    itemViewHolder.getBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
                    itemViewHolder.getBtn.setClickable(false);
                } else {
                    itemViewHolder.getBtn.setText("领取");
                    itemViewHolder.getBtn.setBackgroundResource(R.drawable.corner_red_stroke);
                }
            }

        } else if (holder instanceof FooterViewHolder) {

            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.footRt.setVisibility(View.VISIBLE);
                    footerViewHolder.footTv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.footRt.setVisibility(View.VISIBLE);
                    footerViewHolder.footTv.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.footRt.setVisibility(View.VISIBLE);
                    footerViewHolder.footTv.setText("已经全部加载完成.");
                    break;
            }
        }

    }

    /**
     * 得到RecyclerView的总列数(+1)
     **/
    @Override
    public int getItemCount() {
        //RecyclerView的count设置为数据总条数+ 1（footerView）
        if (list != null) {
            return list.size() + 1;
        }
        return 1;
    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgeIv;
        TextView titleTv;
        TextView contentTv;
        TextView countTv;
        Button getBtn;
        ProgressBar bar;

        LinearLayout getLl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imgeIv = (ImageView) itemView.findViewById(R.id.gift_item_img);
            titleTv = (TextView) itemView.findViewById(R.id.gift_item_title);
            contentTv = (TextView) itemView.findViewById(R.id.gift_item_introduce);
            countTv = (TextView) itemView.findViewById(R.id.gift_item_count);
            getBtn = (Button) itemView.findViewById(R.id.gift_item_getBtn);
            bar = (ProgressBar) itemView.findViewById(R.id.gift_item_progressbar);
            getLl = (LinearLayout) itemView.findViewById(R.id.gift_item_getBtn_ll);
//            itemView.setOnClickListener(this);
            initListener(itemView);
        }

        private void initListener(final View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "点击poistion ：" + getAdapterPosition(), Toast.LENGTH_SHORT).show();

                    /**
                     点击item在点击出插入新的item
                     移除使用，notifyItemRemoved(position)
                     */
                    list.add(getAdapterPosition(), list.get(getAdapterPosition()));
                    notifyItemInserted(getAdapterPosition());
                    /**
                     * 更新数据集不是用adapter.notifyDataSetChanged()
                     * 而是notifyItemInserted(position)与notifyItemRemoved(position)
                     * 否则没有动画效果。
                     * **/
//                    list.remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar footBar;
        TextView footTv;

        RelativeLayout footRt;

        public FooterViewHolder(View itemView) {
            super(itemView);
            footBar = (ProgressBar) itemView.findViewById(R.id.load_progress);
            footTv = (TextView) itemView.findViewById(R.id.load_textview);
            footRt = (RelativeLayout) itemView.findViewById(R.id.load_relativelayout);
        }

    }


    public void AddHeaderItem(List<GiftEntity> lists) {
        list.addAll(0, lists);
        notifyDataSetChanged();
    }

    public void AddFooterItem(List<GiftEntity> lists) {
        list.addAll(lists);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    /**
     * 领取礼包
     */
    private class GetGiftTask extends AsyncTask<Void, Void, Boolean> {
        int position = 0;
        RefreshRecyclerAdapter.ItemViewHolder mholder;

        public GetGiftTask(RefreshRecyclerAdapter.ItemViewHolder mholder, int position) {
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
                ToastUtils.showToast(mContext, "领取成功");
                mholder.getBtn.setText("已领取");
                mholder.getBtn.setBackgroundResource(R.drawable.corner_gray_stroke);
                mholder.getBtn.setEnabled(false);

                int position = (Integer) mholder.getBtn.getTag();
                list.get(position).setHasTake("true");

                Intent intent = new Intent(mContext, GiftAtuoDetailActivity.class);
                intent.putExtra("id", list.get(position).getId());
                mContext.startActivity(intent);

            } else {
                ToastUtils.showToast(mContext, "领取失败");
            }
        }
    }
}