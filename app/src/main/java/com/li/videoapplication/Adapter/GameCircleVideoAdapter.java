package com.li.videoapplication.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.li.videoapplication.activity.VideoPlayActivity;
import com.li.videoapplication.entity.GameCircleVideoEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.li.videoapplication.utils.ToastUtils;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by feimoyuangong on 2015/6/6.
 * 游戏圈子 视频适配器
 */
public class GameCircleVideoAdapter extends BaseAdapter {

    private Context context;
    private List<GameCircleVideoEntity> list;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ExApplication exApplication;
    private int lastMark;

    public GameCircleVideoAdapter(Context context, List<GameCircleVideoEntity> list) {
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
            convertView = inflater.inflate(R.layout.fragment_game_circle_video_item, null);
            holder.nameTv = (TextView) convertView.findViewById(R.id.game_circle_video_name);
            holder.timeTv = (TextView) convertView.findViewById(R.id.game_circle_video_updatetime);
            holder.titleTv = (TextView) convertView.findViewById(R.id.game_circle_video_title);
            holder.focusTv = (TextView) convertView.findViewById(R.id.game_circle_video_focus);

            holder.headIv = (ImageView) convertView.findViewById(R.id.game_circle_video_head);
            holder.flagIv = (ImageView) convertView.findViewById(R.id.game_circle_video_flag);
            holder.priseIv = (ImageView) convertView.findViewById(R.id.game_circle_video_prise);
            holder.collectIv = (ImageView) convertView.findViewById(R.id.game_circle_video_collect);
            holder.shareIv = (ImageView) convertView.findViewById(R.id.game_circle_video_share);
            holder.commentIv = (ImageView) convertView.findViewById(R.id.game_circle_video_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTv.setText(list.get(position).getNickname());
        holder.timeTv.setText(list.get(position).getVideo_time());
        holder.titleTv.setText(list.get(position).getVideo_name());
        exApplication.imageLoader.displayImage(list.get(position).getAvatar(), holder.headIv, exApplication.getOptions());
        exApplication.imageLoader.displayImage(list.get(position).getFlagPath(), holder.flagIv, exApplication.getOptions());

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
        if (list.get(position).getAttentionMark().equals("0")) {
            holder.focusTv.setBackgroundResource(R.drawable.unfocus_stroke_bg);
            holder.focusTv.setText("+关注");
            holder.focusTv.setTextColor(Color.parseColor("#fb3d2e"));
        } else {
            holder.focusTv.setBackgroundResource(R.drawable.focus_stroke_bg);
            holder.focusTv.setText("已关注");
            holder.focusTv.setTextColor(Color.parseColor("#8e8e8e"));
        }

        //点赞 或 取消点赞
        holder.priseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new FlowerTask(list.get(position), (ImageView) v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new FlowerTask(list.get(position), (ImageView) v).execute();
                }
            }
        });
        if (list.get(position).getFlowerMark().equals("1")) {
            holder.priseIv.setBackgroundResource(R.drawable.love_pressed);
        } else if (list.get(position).getFlowerMark().equals("0")) {
            holder.priseIv.setBackgroundResource(R.drawable.love_normal);
        }

        //收藏 或 取消收藏
        holder.collectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExApplication.MEMBER_ID.equals("")) {
                    ToastUtils.showToast(context, "请先登陆");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new CollectTask(list.get(position), (ImageView) v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new CollectTask(list.get(position), (ImageView) v).execute();
                }
            }
        });
        if (list.get(position).getCollectionMark().equals("0")) {
            holder.collectIv.setBackgroundResource(R.drawable.collect_normal);
        } else if (list.get(position).getCollectionMark().equals("1")) {
            holder.collectIv.setBackgroundResource(R.drawable.collect_pressed);
        }

        holder.shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(list.get(position));
            }
        });

        holder.commentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", list.get(position).getVideo_id());
                context.startActivity(intent);
            }
        });

        holder.flagIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", list.get(position).getVideo_id());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    /**
     * 加关注
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
                if ("0".equals(list.get(position).getAttentionMark())) {
                    focusTv.setBackgroundResource(R.drawable.focus_stroke_bg);
                    focusTv.setText("已关注");
                    focusTv.setTextColor(context.getResources().getColor(R.color.search_result_default));
                    ToastUtils.showToast(context, "关注成功");
                    list.get(position).setAttentionMark("1");
                } else {
                    focusTv.setBackgroundResource(R.drawable.unfocus_stroke_bg);
                    focusTv.setText("+关注");
                    ToastUtils.showToast(context, "取消关注");
                    focusTv.setTextColor(context.getResources().getColor(R.color.search_result_focus));
                    list.get(position).setAttentionMark("0");
                }
            }
        }
    }

    /**
     * 视频点赞
     */
    private class FlowerTask extends AsyncTask<Void, Void, String> {

        private GameCircleVideoEntity game;
        private ImageView priseIv;

        public FlowerTask(GameCircleVideoEntity game, ImageView priseIv) {
            this.game = game;
            this.priseIv = priseIv;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (game.getFlowerMark().equals("1")) {//已点赞
                return JsonHelper.cancelFlower(game.getVideo_id(), ExApplication.MEMBER_ID);//取消点赞
            } else if (game.getFlowerMark().equals("0")) {//未点赞
                return JsonHelper.giveFlower(game.getVideo_id(), ExApplication.MEMBER_ID);//进行点赞
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);

            if (b.equals("c") && game.getFlowerMark().equals("1")) {//已点赞，重复点赞
                ToastUtils.showToast(context, "你已经赞过了该视频");
                return;
            }

            if (game.getFlowerMark().equals("0")) {//未点赞
                if (b.equals("s")) {//成功点赞
                    game.setFlowerMark("1");
                    if (ExApplication.MEMBER_ID.equals("")) {
                        SharePreferenceUtil.setPreference(context, game.getVideo_id() + "unlogin_flower", "flowered");
                    }
                    ExApplication.upUmenEventValue(context, "点赞次数", "praise_count");
                    priseIv.setBackgroundResource(R.drawable.love_pressed);
//                    ToastUtils.showToast(context, "视频点赞成功");
                } else {
//                    ToastUtils.showToast(context, "点赞视频失败");
                }
            } else if (game.getFlowerMark().equals("1")) {//已点赞
                if (b.equals("s")) {//取消点赞成功
                    game.setFlowerMark("0");
                    if (ExApplication.MEMBER_ID.equals("")) {
                        SharePreferenceUtil.setPreference(context, game.getVideo_id() + "unlogin_flower", "unflowered");//
                    }
                    ExApplication.upUmenEventValue(context, "取消点赞次数", "unpraise_count");
                    priseIv.setBackgroundResource(R.drawable.love_normal);
//                    ToastUtils.showToast(context, "取消视频点赞成功");
                } else {
//                    ToastUtils.showToast(context, "取消点赞视频失败");
                }
            }
        }
    }

    /**
     * 视频收藏 或 取消收藏
     */
    private class CollectTask extends AsyncTask<Void, Void, String> {

        private GameCircleVideoEntity game;
        private ImageView collectIv;

        public CollectTask(GameCircleVideoEntity game, ImageView collectIv) {
            this.game = game;
            this.collectIv = collectIv;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (game.getCollectionMark().equals("1")) {//已收藏
                return JsonHelper.getCancelCollectVideo(game.getVideo_id(), ExApplication.MEMBER_ID);//取消收藏
            } else if (game.getCollectionMark().equals("0")) {//未收藏
                return JsonHelper.getCollectVideo(game.getVideo_id(), ExApplication.MEMBER_ID);//进行收藏
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);
            if (b.equals("c") && game.getCollectionMark().equals("1")) {//已收藏，重复收藏
                ToastUtils.showToast(context, "你已收藏过该视频");
                return;
            }

            if (game.getCollectionMark().equals("0")) {//未收藏
                if (b.equals("s")) {//成功收藏
                    collectIv.setBackgroundResource(R.drawable.collect_pressed);
                    game.setCollectionMark("1");
//                    ToastUtils.showToast(context, "收藏成功");
                    ExApplication.upUmenEventValue(context, "收藏次数", "collection_count");
                } else {
//                    ToastUtils.showToast(context, "收藏失败");
                }
            } else if (game.getCollectionMark().equals("1")) {//已收藏
                if (b.equals("s")) {//取消收藏
                    collectIv.setBackgroundResource(R.drawable.collect_normal);
                    game.setCollectionMark("0");
//                    ToastUtils.showToast(context, "取消收藏成功");
                    ExApplication.upUmenEventValue(context, "取消收藏次数", "uncollection_count");
                } else {
//                    ToastUtils.showToast(context, "取消收藏失败");
                }
            }

        }
    }

    private void showShare(final GameCircleVideoEntity game) {
        if (game != null) {
            final String url = ExApplication.youkuURL;
            ShareSDK.initSDK(context);
            final OnekeyShare oks = new OnekeyShare();
            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_copy);
            // 定义图标的标签
            String label = context.getResources().getString(R.string.share_copy);
            // 图标点击后会通过Toast提示消息
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(url + game.getVideo_id());
                    ToastUtils.showToast(context, "视频链接已复制");
//                    oks.finish();
                }
            };
            oks.setCustomerLogo(logo,null, label, listener);
//            oks.show(context);
            //关闭sso授权
            oks.disableSSOWhenAuthorize();
//            // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//            oks.setNotification(R.drawable.tubiao_top, context.getString(R.string.app_name));
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(context.getString(R.string.share));
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url + game.getVideo_id());
//            oks.setTitleUrl(youkuDetail.getLink());
            // text是分享文本，所有平台都需要这个字段
//            oks.setText("快来看看" + detail.getName() + youkuDetail.getLink());
            oks.setText("快来看看" + game.getVideo_name() + url + game.getVideo_id());
            /** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
            oks.setImageUrl(game.getFlagPath());
            // url仅在微信（包括好友和朋友圈）中使用
//            oks.setUrl(youkuDetail.getLink());
            oks.setUrl("快来看看" + game.getVideo_name() + url + game.getVideo_id());
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(context.getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url + game.getVideo_id());
//            oks.setSiteUrl(youkuDetail.getLink());

            // 启动分享GUI
            oks.show(context);
            ExApplication.upUmenEventValue(context, "分享次数", "share_count");
        }
    }

    class ViewHolder {
        ImageView headIv;
        TextView nameTv;
        TextView timeTv;
        TextView titleTv;
        TextView focusTv;
        ImageView flagIv;
        ImageView priseIv;
        ImageView collectIv;
        ImageView shareIv;
        ImageView commentIv;
    }
}
