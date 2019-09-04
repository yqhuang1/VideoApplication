
package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ActivityDetailActivity;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.GiftAtuoDetailActivity;
import com.li.videoapplication.activity.MainActivity;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.RecommendEntity;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

/**
 * 主页首页
 * 广告条内容适配器
 *
 * @author li_zhuonan
 */
public class AdImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<RecommendEntity> files;
    private ExApplication exApplication;
    private int width;
    private RelativeLayout.LayoutParams layoutParams;

//	private static final int[] ids = {R.drawable.test1, R.drawable.test2, R.drawable.test3 };


    public AdImageAdapter(Context context) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    public AdImageAdapter(Context context, List<RecommendEntity> files) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.files = files;
        exApplication = new ExApplication(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        layoutParams = new RelativeLayout.LayoutParams(width, width / 2);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;   //返回很大的值使得getView中的position不断增大来实现循环
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //使用 convertView 重用
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.head_item, null);
            holder = new ViewHolder();
            holder.ad_imge = (ImageView) convertView.findViewById(R.id.head_imgView);
//			holder.titleTv=(TextView)convertView.findViewById(R.id.head_item_focus_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (files.size() != 0) {
//			holder.titleTv.setText(files.get(position%files.size()).getTitle());
            if (!files.get(position % files.size()).getFlagPath().equals("")) {
                Log.d("imgPath", files.get(position % files.size()).getFlagPath() + files.get(position % files.size()).getFlagPath());
//				ExApplication.imageLoader.displayImage(files.get(position%files.size()).getImg_url(),
//                        holder.ad_imge, ExApplication.getOptions());
                holder.ad_imge.setLayoutParams(layoutParams);
                exApplication.imageLoader.displayImage(files.get(position % files.size()).getFlagPath(),
                        holder.ad_imge, exApplication.getOptions());
            } else {
                holder.ad_imge.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home_ad_default_bg));
            }

        }


//		if (files.get(position%files.size()).getBimg_url().equals("")||files.get(position%files.size()).getBimg_url()==null) {
//			
//			
//		}else{

//		}

//			ZquApplication.imageLoader.displayImage(adurl[position%adurl.length],holder.ad_imge,ZquApplication.getOptions());
//		holder.ad_imge.setBackground(mContext.getResources().getDrawable(ids[position%ids.length]));
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((MainActivity) mContext).netState.equals("DISCONNECTED")) {
                    ToastUtils.showToast(mContext, "当前网络断开，请重新链接后重试！");
                    return;
                }

                if (files.size() != 0) {
                    //判断焦点图是否为活动，如果是使用webview加载html页面
                    if ("video".equals(files.get(position % files.size()).getType())) {
                        Intent intent = new Intent(mContext, VideoPlayActivity.class);
                        intent.putExtra("id", files.get(position % files.size()).getVideo_id());
                        mContext.startActivity(intent);
                    }
                    if ("activity".equals(files.get(position % files.size()).getType())) {
                        Intent intent = new Intent(mContext, ActivityDetailActivity.class);
                        intent.putExtra("id", files.get(position % files.size()).getActivity_id());
                        mContext.startActivity(intent);
                    }
                    if ("package".equals(files.get(position % files.size()).getType())) {
                        Intent intent = new Intent(mContext, GiftAtuoDetailActivity.class);
                        intent.putExtra("id", files.get(position % files.size()).getPackage_id());
                        mContext.startActivity(intent);
                    }

                }

            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView ad_imge;
//		TextView titleTv;

    }
}
