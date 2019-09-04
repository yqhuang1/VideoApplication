package com.li.videoapplication.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.VideoEntity;
import com.li.videoapplication.fragment.ColudVideoFragment;
import com.li.videoapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by feimoyuangong on 2015/7/21.
 * 视频管理 云端视频适配器
 */
public class ColudVideoAdapter extends BaseAdapter {

    private List<VideoEntity> mList;
    private Context context;
    ViewHolder holder = null;
    private LayoutInflater inflater;
    private ExApplication exApplication;

    public static List<Boolean> ListDelcheck;// 选择要删除的文件列表选

    public ColudVideoAdapter(Context context, List<VideoEntity> list) {
        this.context = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);

        ListDelcheck = new ArrayList<Boolean>();
        for (int i = 0; i < mList.size(); i++) {
            ListDelcheck.add(false);
        }
    }

    public void update(List<VideoEntity> list) {
        this.mList = list;
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.colud_video_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.colud_video_item_flag);
            holder.playImg = (ImageView) convertView.findViewById(R.id.colud_video_item_playIV);
            holder.check = (ImageView) convertView.findViewById(R.id.colud_video_item_check);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.colud_video_item_delete_checkbox);
            holder.title = (TextView) convertView.findViewById(R.id.colud_video_item_title);
            holder.uploadTime = (TextView) convertView.findViewById(R.id.colud_video_item_upload_time);
            holder.playCount = (TextView) convertView.findViewById(R.id.colud_video_item_play);
            holder.commentCount = (TextView) convertView.findViewById(R.id.colud_video_item_comment);
            holder.priseCount = (TextView) convertView.findViewById(R.id.colud_video_item_prise);
            holder.shareCount = (TextView) convertView.findViewById(R.id.colud_video_item_share);
            holder.shareTv = (TextView) convertView.findViewById(R.id.colud_video_item_share_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(mList.get(position).getTitle());
        holder.uploadTime.setText("上传于:" + mList.get(position).getUpload_time());
        holder.playCount.setText("播放:" + mList.get(position).getViewCount());
        holder.commentCount.setText("评论:" + mList.get(position).getComment_count());
        holder.priseCount.setText("点赞:" + mList.get(position).getFlower_count());
        holder.shareCount.setText("分享:" + mList.get(position).getShare_count());
        exApplication.imageLoader.displayImage(mList.get(position).getFlagPath(), holder.flag, exApplication.getOptions());

        // 如果处于批量删除状态
        if (VideoManagerActivity.inEditorState == true) {
            InTheDelState(position);
        } else {
            NoInTheDelState();
        }

        //分享监听
        holder.shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(mList.get(position));
            }
        });

        //播放监听
        holder.flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", mList.get(position).getId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        private ImageView flag;
        private TextView title;
        private TextView uploadTime;
        private TextView playCount;
        private TextView commentCount;
        private TextView priseCount;
        private TextView shareCount;
        private TextView shareTv;
        private ImageView playImg;
        private ImageView check;
        private CheckBox checkBox;
    }

    private void showShare(final VideoEntity videoEntity) {

//        final String url = ExApplication.youkuURL;
        final String url = ExApplication.shareURL;
        String videoUrl = "";
        if (videoEntity.getQn_key().equals("")) {
            videoUrl = url + videoEntity.getUrl();
        } else {
            videoUrl = url + videoEntity.getQn_key();
        }

        ShareSDK.initSDK(context);
        final OnekeyShare oks = new OnekeyShare();
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_copy);
        // 定义图标的标签
        String label = context.getResources().getString(R.string.share_copy);
        // 图标点击后会通过Toast提示消息
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                String videoUrl = "";
                if (videoEntity.getQn_key().equals("")) {
                    videoUrl = url + videoEntity.getUrl();
                } else {
                    videoUrl = url + videoEntity.getQn_key();
                }
                clip.setText(videoUrl);

                ToastUtils.showToast(context, "视频链接已复制");
//                oks.finish();
            }
        };
        oks.setCustomerLogo(logo, null, label, listener);
//            oks.show(context);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        oks.setNotification(R.drawable.tubiao_top, context.getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(context.getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(videoUrl);
//            oks.setTitleUrl(youkuDetail.getLink());
        // text是分享文本，所有平台都需要这个字段
//            oks.setText("快来看看" + detail.getName() + youkuDetail.getLink());
        oks.setText("快来看看：" + videoEntity.getTitle() + "\n" + videoUrl);
        /** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
        oks.setImageUrl(videoEntity.getFlagPath());
        // url仅在微信（包括好友和朋友圈）中使用
//            oks.setUrl(youkuDetail.getLink());
        oks.setUrl("快来看看" + videoEntity.getTitle() + videoUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(videoUrl);
//            oks.setSiteUrl(youkuDetail.getLink());

        // 启动分享GUI
        oks.show(context);
        ExApplication.upUmenEventValue(context, "分享次数", "share_count");

    }

    /**
     * 处于批量删除状态
     *
     * @param position
     */
    private void InTheDelState(final int position) {
        holder.playImg.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.check.setVisibility(View.VISIBLE);
        holder.checkBox.setId(position);

        if (ListDelcheck.get(position)) {
            holder.check.setImageResource(MResource.getIdByName(context,
                    "drawable", "check_back_true"));

        } else {
            holder.check.setImageResource(MResource.getIdByName(context,
                    "drawable", "check_back_false"));

        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ListDelcheck.get(position)) {

                    ListDelcheck.set(position, false);
                    ColudVideoFragment.coludVideoListCheckToDel.remove(mList
                            .get(position));
                    // 发消息通知标题栏进行更改
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                } else {

                    ListDelcheck.set(position, true);
                    ColudVideoFragment.coludVideoListCheckToDel.add(mList
                            .get(position));
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                }
                notifyDataSetChanged();

            }
        });
    }

    private void NoInTheDelState() {
        // 重新赋值checkbox状态
        ListDelcheck.clear();

        for (int i = 0; i < mList.size(); i++) {
            ListDelcheck.add(false);
        }

        holder.playImg.setVisibility(View.VISIBLE);
        holder.checkBox.setVisibility(View.GONE);
        holder.check.setVisibility(View.GONE);

    }
}
