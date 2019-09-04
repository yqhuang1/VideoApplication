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
import com.li.videoapplication.utils.JsonHelper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by li on 2014/8/18.
 * 游戏圈子 评论适配器
 */
public class GameCircleCommentAdapter extends BaseAdapter {

    private List<CommentEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private ExApplication exApplication;
    private String[] faceArray;
    private String[] faceCnArray;
    private String[] expressionArray;
    private String[] expressionCnArray;

    public GameCircleCommentAdapter(Context context, List<CommentEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.play_comment_item1, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.comment_item_img);
            holder.nameTv = (TextView) convertView.findViewById(R.id.comment_item_name);
            holder.timeTv = (TextView) convertView.findViewById(R.id.comment_item_time);
            holder.contentTv = (TextView) convertView.findViewById(R.id.comment_item_content);
            holder.levelIv = (ImageView) convertView.findViewById(R.id.comment_item_level_iv);

            holder.levelTv = (TextView) convertView.findViewById(R.id.comment_item_level_tv);
            holder.priseTv = (TextView) convertView.findViewById(R.id.play_comment_item_prise_tv);
            holder.priseIb = (ImageButton) convertView.findViewById(R.id.play_comment_item_prise_ib);
            holder.priseLayout = (RelativeLayout) convertView.findViewById(R.id.play_comment_item_prise_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).getImgPath().equals("") || list.get(position).getImgPath() == null) {
            holder.img.setVisibility(View.GONE);
        } else {
            holder.img.setVisibility(View.VISIBLE);
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

        holder.nameTv.setText(list.get(position).getName());
        holder.timeTv.setText(list.get(position).getTime());
        holder.levelTv.setText(list.get(position).getLevel() + "");
        int level = list.get(position).getLevel();
        if (level <= 6) {
            holder.levelIv.setBackgroundResource(R.drawable.level_green_bg);
        } else if (level > 6 && level <= 12) {
            holder.levelIv.setBackgroundResource(R.drawable.level_blue_bg);
        } else {
            holder.levelIv.setBackgroundResource(R.drawable.level_orange_bg);
        }
        String comment = list.get(position).getContent();
        commentInflater(comment, holder.contentTv);
        if (list.get(position).getMemberId().equals("0")) {
            holder.levelIv.setVisibility(View.GONE);
            holder.levelTv.setVisibility(View.GONE);
        } else {
            holder.levelIv.setVisibility(View.VISIBLE);
            holder.levelTv.setVisibility(View.VISIBLE);
        }

        holder.priseIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new submitPriseTask(holder.priseIb, holder.priseTv, list.get(position)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new submitPriseTask(holder.priseIb, holder.priseTv, list.get(position)).execute();
                }
            }
        });

        holder.priseTv.setText(list.get(position).getLikeNum());

        if (ExApplication.MEMBER_ID != null && !"".equals(ExApplication.MEMBER_ID)) {
            if (list.get(position).getLikeMark().equals("1")) {
                holder.priseIb.setBackgroundResource(R.drawable.love_pressed);
            }
        }
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
     * 游戏圈评论点赞
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
            this.comment_id = commentEntity.getReview_id();
            this.likeCount = commentEntity.getLike();

        }

        @Override
        protected String doInBackground(Void... voids) {
            return JsonHelper.submitGameCirclePrise(comment_id, ExApplication.MEMBER_ID);
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);

            if (b.equals("s")) {
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

        TextView levelTv;
        ImageView levelIv;

        RelativeLayout priseLayout;
        ImageButton priseIb;
        TextView priseTv;
    }

}
