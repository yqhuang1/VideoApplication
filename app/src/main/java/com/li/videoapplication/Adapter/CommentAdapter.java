package com.li.videoapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.PersonalInfoActivity;
import com.li.videoapplication.entity.CommentEntity;
import com.li.videoapplication.utils.DateUtils;
import com.li.videoapplication.utils.JsonHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2014/8/18.
 */
public class CommentAdapter extends BaseAdapter {

    private List<CommentEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private String[] faceArray;
    private String[] faceCnArray;
    private String[] expressionArray;
    private String[] expressionCnArray;
    private String flag;

    private static int SPREAD_STATE = 1;// 闭合状态1，展开状态2


    private ReplyCallback replyCallback;

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface ReplyCallback {
        public void replyClick(int positon, boolean markFlag);
    }

    public CommentAdapter(Context context, List<CommentEntity> list, String flag, ReplyCallback callback) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.flag = flag;
        this.replyCallback = callback;
        exApplication = new ExApplication(context);
        faceArray = context.getResources().getStringArray(R.array.faceArray);
        faceCnArray = context.getResources().getStringArray(R.array.faceCnArray);
        expressionArray = context.getResources().getStringArray(R.array.expressionArray);
        expressionCnArray = context.getResources().getStringArray(R.array.expressionCnArray);
    }

    public void update(List<CommentEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
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
            if (flag.equals("videoplay")) {
                convertView = inflater.inflate(R.layout.play_comment_item1, null);
            } else {
                convertView = inflater.inflate(R.layout.play_comment_item2, null);
            }
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.comment_item_img);
            holder.nameTv = (TextView) convertView.findViewById(R.id.comment_item_name);
            holder.timeTv = (TextView) convertView.findViewById(R.id.comment_item_time);
            holder.contentTv = (TextView) convertView.findViewById(R.id.comment_item_content);
            holder.drawIv = (ImageView) convertView.findViewById(R.id.comment_item_draw);
            holder.levelIv = (ImageView) convertView.findViewById(R.id.comment_item_level_iv);
//            holder.lineIv=(ImageView)convertView.findViewById(R.id.line_iv);
            holder.levelTv = (TextView) convertView.findViewById(R.id.comment_item_level_tv);
            holder.priseLayout = (RelativeLayout) convertView.findViewById(R.id.play_comment_item_prise_layout);
            holder.priseIb = (ImageButton) convertView.findViewById(R.id.play_comment_item_prise_ib);
            holder.priseTv = (TextView) convertView.findViewById(R.id.play_comment_item_prise_tv);
            holder.replyIb = (ImageButton) convertView.findViewById(R.id.play_comment_item_reply_ib);
//            holder.honourTv=(TextView)convertView.findViewById(R.id.comment_item_honour);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).getImgPath().equals("") || list.get(position).getImgPath() == null) {
            holder.img.setVisibility(View.VISIBLE);
        } else {
            holder.img.setVisibility(View.VISIBLE);
//            ExApplication.imageLoader.displayImage(list.get(position).getImgPath(),holder.img,
//                    ExApplication.getOptions());
            exApplication.imageLoader.displayImage(list.get(position).getImgPath(), holder.img, exApplication.getOptions());

        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalInfoActivity.class);
                intent.putExtra("flag", "videoplay");
                intent.putExtra("member_id", list.get(position).getMemberId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
//        if (flag.equals("person")){
//            holder.priseLayout.setVisibility(View.GONE);
//        }
        holder.nameTv.setText(list.get(position).getName());

        if (flag.equals("videoplay")) {
            long timeStamp = Long.parseLong(list.get(position).getTime_x());
            String timeString = DateUtils.convertTimeToFormat(timeStamp);
            holder.timeTv.setText(timeString);
        } else {
            holder.timeTv.setText(list.get(position).getTime());
        }


        holder.levelTv.setText(list.get(position).getLevel() + "");
//        holder.honourTv.setText(list.get(position).getHonour());
        int level = list.get(position).getLevel();
        if (level <= 6) {
            holder.levelIv.setBackgroundResource(R.drawable.level_green_bg);
//            holder.honourTv.setTextColor(context.getResources().getColor(R.color.video_play_comment_tv));
        } else if (level > 6 && level <= 12) {
            holder.levelIv.setBackgroundResource(R.drawable.level_blue_bg);
//            holder.honourTv.setTextColor(context.getResources().getColor(R.color.video_play_comment_tv));
        } else {
            holder.levelIv.setBackgroundResource(R.drawable.level_orange_bg);
//            holder.honourTv.setTextColor(context.getResources().getColor(R.color.video_play_comment_tv));
        }
        String comment = list.get(position).getContent();
        commentInflater(comment, holder.contentTv);
//        holder.contentTv.setText(list.get(position).getContent());

        if (flag.equals("videoplay")) {
            int length = holder.contentTv.getText().length();
            if (length > 35) {
                holder.drawIv.setVisibility(View.VISIBLE);
                holder.contentTv.setMaxLines(2);
                holder.contentTv.requestLayout();
                SPREAD_STATE = 1;
            } else {
                holder.drawIv.setVisibility(View.GONE);
            }
        } else {
            holder.drawIv.setVisibility(View.GONE);
        }

        holder.drawIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SPREAD_STATE == 1) {//闭合状态
                    holder.contentTv.setMaxLines(10);
                    holder.contentTv.requestLayout();
                    holder.drawIv.setBackgroundResource(R.drawable.content_draw_up);
                    SPREAD_STATE = 2;
                } else if (SPREAD_STATE == 2) {//展开状态
                    holder.contentTv.setMaxLines(2);
                    holder.contentTv.requestLayout();
                    holder.drawIv.setBackgroundResource(R.drawable.content_draw_down);
                    SPREAD_STATE = 1;
                }
            }
        });

        if (list.get(position).getMemberId().equals("0")) {
//            holder.honourTv.setVisibility(View.GONE);
            holder.levelIv.setVisibility(View.GONE);
            holder.levelTv.setVisibility(View.GONE);
        } else {
//            holder.honourTv.setVisibility(View.VISIBLE);
            holder.levelIv.setVisibility(View.VISIBLE);
            holder.levelTv.setVisibility(View.VISIBLE);
        }

        holder.priseIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("videoplay")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new submitPriseTask(holder.priseIb, holder.priseTv, list.get(position)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new submitPriseTask(holder.priseIb, holder.priseTv, list.get(position)).execute();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new subMasterCommentPriseTask(holder.priseIb, holder.priseTv, list.get(position)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new subMasterCommentPriseTask(holder.priseIb, holder.priseTv, list.get(position)).execute();
                    }
                }
            }
        });

        if (flag.equals("videoplay")) {
            holder.priseTv.setText(list.get(position).getLike());
        } else {
            holder.priseTv.setText(list.get(position).getLikeNum());
        }

        if (ExApplication.MEMBER_ID != null && !"".equals(ExApplication.MEMBER_ID)) {
            if (flag.equals("videoplay")) {
                if (list.get(position).getMark().equals("1")) {
                    holder.priseIb.setBackgroundResource(R.drawable.love_pressed);
                }
            } else {
                if (list.get(position).getLikeMark().equals("1")) {
                    holder.priseIb.setBackgroundResource(R.drawable.love_pressed);
                }
            }
        }

        if (flag.equals("videoplay")) {
            holder.replyIb.setVisibility(View.VISIBLE);
        } else {
            holder.replyIb.setVisibility(View.GONE);
        }

        holder.replyIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag.equals("videoplay")) {
                    /*执行回调监听接口ReplyCallback中的replyClick具体方法*/
                    replyCallback.replyClick(position, true);
                } else {

                }
            }
        });

        return convertView;
    }

    /**
     * 过滤评论内容，如果是表情串码则替换显示相应表情
     *
     * @param comment
     * @param textView
     */
    public void commentInflater(String comment, TextView textView) {
        //处理显示表情
        int len = 0;
        int starts = 0;
        int end = 0;
        SpannableString spannableString = new SpannableString(comment);
        while (len < comment.length()) {
            if (comment.indexOf("[", starts) != -1 && comment.indexOf("]", end) != -1) {
                starts = comment.indexOf("[", starts);
                end = comment.indexOf("]", end);
                String face = comment.substring(starts + 1, end);
                for (int j = 0; j < faceCnArray.length; j++) {
                    if (face.equals(faceCnArray[j])) {
                        face = faceArray[j];
                        break;
                    }
                }
                for (int i = 0; i < expressionCnArray.length; i++) {
                    if (face.equals(expressionCnArray[i])) {
                        face = expressionArray[i];
                        break;
                    }
                }
                try {
                    Field f = R.drawable.class.getDeclaredField(face);
                    int i = f.getInt(R.drawable.class);
                    Drawable drawable = context.getResources().getDrawable(i);
                    if (drawable != null) {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
                        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                        spannableString.setSpan(span, starts, end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {

                }
                starts = end;
                len = end;
                end++;
            } else {
                starts++;
                end++;
                len = end;
            }
        }

        textView.setText(spannableString);

    }

    /**
     * 为 视频评论 点赞*
     */
    private class submitPriseTask extends AsyncTask<Void, Void, String> {

        ImageButton priseIb;
        TextView priseTv;
        String comment_id;
        String likeCount = "0";
        CommentEntity commentEntity;

        public submitPriseTask(ImageButton priseIb, TextView priseTv, CommentEntity commentEntity) {
            this.priseIb = priseIb;
            this.priseTv = priseTv;
            this.commentEntity = commentEntity;
            this.comment_id = commentEntity.getComment_id();
            this.likeCount = commentEntity.getLike();

        }

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.submitCommentPrise(comment_id, ExApplication.MEMBER_ID);
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);

            if (b.equals("s")) {
//                priseIb.setBackgroundResource(R.drawable.love_pressed);
//                priseTv.setText((Integer.parseInt(likeCount)+1)+"");

                int prise = Integer.parseInt(commentEntity.getLike());
                if (commentEntity.getMark().equals("0")) {
                    priseIb.setBackgroundResource(R.drawable.love_pressed);
                    priseTv.setText((prise + 1) + "");
                    commentEntity.setMark("1");
                    commentEntity.setLike((prise + 1) + "");
                } else {
                    priseIb.setBackgroundResource(R.drawable.love_normal);
                    priseTv.setText((prise - 1) + "");
                    commentEntity.setMark("0");
                    commentEntity.setLike((prise - 1) + "");
                }


            }
        }

    }

    /**
     * 为 大神信息 给TA留言 点赞*
     */
    private class subMasterCommentPriseTask extends AsyncTask<Void, Void, String> {

        ImageButton priseIb;
        TextView priseTv;
        String comment_id;
        String likeCount = "0";
        CommentEntity commentEntity;

        public subMasterCommentPriseTask(ImageButton priseIb, TextView priseTv, CommentEntity commentEntity) {
            this.priseIb = priseIb;
            this.priseTv = priseTv;
            this.commentEntity = commentEntity;
            this.comment_id = commentEntity.getReview_id();
            this.likeCount = commentEntity.getLikeNum();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.submitMasterCommentPrise(comment_id, ExApplication.MEMBER_ID);
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);

            if (b.equals("s")) {
//                priseIb.setBackgroundResource(R.drawable.love_pressed);
//                priseTv.setText((Integer.parseInt(likeCount)+1)+"");
                int prise = Integer.parseInt(commentEntity.getLikeNum());
                if (commentEntity.getLikeMark().equals("0")) {
                    priseIb.setBackgroundResource(R.drawable.love_pressed);
                    priseTv.setText((prise + 1) + "");
                    commentEntity.setLikeMark("1");
                    commentEntity.setLikeNum((prise + 1) + "");
                } else {
                    priseIb.setBackgroundResource(R.drawable.love_normal);
                    priseTv.setText((prise - 1) + "");
                    commentEntity.setLikeMark("0");
                    commentEntity.setLikeNum((prise - 1) + "");
                }
            }
        }
    }

    private class ViewHolder {

        ImageView img;
        TextView nameTv;
        TextView timeTv;
        TextView contentTv;
        ImageView drawIv;
        //        TextView honourTv;
        TextView levelTv;
        ImageView levelIv;
        //        ImageView lineIv;
        RelativeLayout priseLayout;
        ImageButton priseIb;
        TextView priseTv;

        ImageButton replyIb;
    }

}
