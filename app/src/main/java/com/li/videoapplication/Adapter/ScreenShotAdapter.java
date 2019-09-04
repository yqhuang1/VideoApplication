package com.li.videoapplication.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.video.ImageInfo;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.fragment.ScreenShotFragment;
import com.li.videoapplication.utils.ToastUtils;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Leo on 2015/7/21.
 * 视频管理 截图适配器
 */
public class ScreenShotAdapter extends BaseAdapter {

    private Context context;
    private List<ImageInfo> mlist;
    private LayoutInflater inflater;
    private ViewHolder holder = null;
    private ExApplication exApplication;
    // 用来保存批量删除checkbox的勾选状态值，防止划屏时错位
    List<Boolean> checkList;

    private SharedPreferences sharedPreferences;

    public List<Boolean> ListDelcheck;// 选择要删除的文件列表选

    public List<Boolean> ListShareCheck; // 选择要分享的文件列表

    private VideoThumbnailLoader videoThumbnailLoader;

    public ScreenShotAdapter(Context context, List<ImageInfo> list) {
        this.context = context;
        this.mlist = list;
        exApplication = new ExApplication(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);
        inflater = LayoutInflater.from(context);

        checkList = new ArrayList<Boolean>();
        for (int i = 0; i < list.size(); i++) {
            checkList.add(false);
        }

        ListDelcheck = new ArrayList<Boolean>();
        for (int i = 0; i < list.size(); i++) {
            ListDelcheck.add(false);
        }
        ListShareCheck = new ArrayList<Boolean>();
        for (int i = 0; i < list.size(); i++) {
            ListShareCheck.add(false);
        }

    }

    public void update(List<ImageInfo> list) {
        this.mlist = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.screen_shot_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.screen_shot_item_flag);
            holder.editor = (ImageView) convertView.findViewById(R.id.screen_shot_item_editor);
            holder.title = (TextView) convertView.findViewById(R.id.screen_shot_item_title);
            holder.time = (TextView) convertView.findViewById(R.id.screen_shot_item_shot_time);
            holder.size = (TextView) convertView.findViewById(R.id.screen_shot_item_size);
            holder.share = (TextView) convertView.findViewById(R.id.screen_shot_item_share);
            holder.check = (ImageView) convertView.findViewById(R.id.screen_shot_item_check);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.screen_shot_item_delete_checkbox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (VideoManagerActivity.inEditorState) {
            InTheDelState(position);
        } else {
            NoInTheDelState();
        }

        // 截取视频文件后缀名前的字符串
        String videoname = mlist.get(position).getDisplayName();
        final String[] fileName = videoname.split("\\.png");
        // 图片名称
        holder.title.setText(fileName[0]);


        // 加载截图图片
        videoThumbnailLoader.DisplayLoaclImage(mlist.get(position).getPath(), holder.flag);

        final String absolutePath = mlist.get(position).getPath();

        // 截图时间
        holder.time.setText("截图于:" + getModifiedTime(absolutePath));
        // 文件大小
        holder.size.setText("大小:" + getFileSizes(absolutePath));

        //分享监听
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(mlist.get(position).getPath());
            }
        });

        //显示图片监听
        holder.flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(absolutePath)), "image/*");
                context.startActivity(intent);
            }
        });

        //重命名监听
        holder.editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(position, holder.title);
            }
        });
        return convertView;
    }

    class ViewHolder {
        //图片
        private ImageView flag;
        //重命名图标
        private ImageView editor;
        //图片名称
        private TextView title;
        // 截图时间
        private TextView time;
        // 图片大小
        private TextView size;
        // 分享按钮
        private TextView share;
        // 显示勾选状态的图片
        private ImageView check;
        // 批量删除勾选框
        private CheckBox checkBox;
    }

    /**
     * 文件创建时间
     *
     * @param filePath
     * @return
     */
    private String getModifiedTime(String filePath) {
        String path = filePath.toString();
        File file = new File(path);
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(new Date(time));
    }

    /**
     * 计算文件大小
     *
     * @param f
     * @return
     */
    public static String getFileSizes(String f) {
        File file = new File(f);
        long s = 0;
        String fileSizeString = "";
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                s = fis.available();
                DecimalFormat df = new DecimalFormat("#");
                if (s < 1024) {
                    fileSizeString = df.format((double) s) + "B";
                } else if (s < 1048576) {
                    fileSizeString = df.format((double) s / 1024) + "K";
                } else if (s < 1073741824) {
                    fileSizeString = df.format((double) s / 1048576) + "M";
                } else {
                    fileSizeString = df.format((double) s / 1073741824) + "G";
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return fileSizeString;
    }

    /**
     * 分享
     *
     * @param filePath
     */
    private void showShare(String filePath) {
//        final String url = ExApplication.youkuURL;
        final String url = ExApplication.shareURL;
        ShareSDK.initSDK(context);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setImagePath(filePath);

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
        holder.check.setVisibility(View.VISIBLE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setId(position);

        if (ListDelcheck.get(position)) {
            holder.check.setImageResource(MResource.getIdByName(
                    context, "drawable", "check_back_true"));

        } else {
            holder.check.setImageResource(MResource.getIdByName(
                    context, "drawable", "check_back_false"));

        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stubS
                if (ListDelcheck.get(position)) {
                    int id = v.getId();
                    ListDelcheck.set(position, false);
                    ScreenShotFragment.imageListCheckToDel.remove(mlist.get(position));
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                } else {
                    int id = v.getId();
                    ListDelcheck.set(position, true);
                    ScreenShotFragment.imageListCheckToDel.add(mlist.get(position));
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                }
                notifyDataSetChanged();
            }
        });
    }

    /**
     * 不处于批量删除状态
     *
     * @param
     */
    private void NoInTheDelState() {
        holder.check.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.GONE);
        holder.flag.setClickable(true);

        // 重新赋值checkbox状态
        ListDelcheck.clear();
        for (int i = 0; i < mlist.size(); i++) {
            ListDelcheck.add(false);
        }
        ListShareCheck.clear();
        for (int i = 0; i < mlist.size(); i++) {
            ListShareCheck.add(false);
        }
    }

    /**
     * 重命名自定义对话框
     *
     * @param position
     * @param name
     */
    public void createDialog(final int position, final TextView name) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("重命名");
        View content = inflater.inflate(R.layout.set_info_view, null);
        dialog.setView(content);
        final EditText editText = (EditText) content.findViewById(R.id.set_info_view_et);
        editText.setText(mlist.get(position).getDisplayName());
        editText.setHintTextColor(Color.parseColor("#c6c6c6"));
        dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim() == "") {
                    ToastUtils.showToast(context, "文件名不能为空");
                    return;
                }
                for (int i = 0; i < mlist.size(); i++) {
                    if (mlist.get(i).getDisplayName().equals(editText.getText().toString().trim())) {
                        ToastUtils.showToast(context, "文件名已存在");
                        return;
                    }
                }
                File file = new File(ExApplication.screenShotPath + "/" + mlist.get(position).getDisplayName());
                file.renameTo(new File(ExApplication.screenShotPath + "/" + editText.getText().toString().trim()));
                mlist.get(position).setDisplayName(editText.getText().toString().trim());
                name.setText(editText.getText().toString().trim());
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create().show();
    }
}
