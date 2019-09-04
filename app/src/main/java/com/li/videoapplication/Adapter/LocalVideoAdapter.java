package com.li.videoapplication.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.ShareActivity;
import com.li.videoapplication.activity.VideoEditorActivity;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.fragment.LocalVideoFragment;
import com.li.videoapplication.utils.VideoTimeThumbnailLoader;
import com.li.videoapplication.videomanager.VideoThumbnailLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/7/21.
 * 视频管理 本地视频适配器
 */
public class LocalVideoAdapter extends BaseAdapter {

    private Context mContext;
    List<VideoInfo> mList = new ArrayList<VideoInfo>();
    private LayoutInflater inflater;
    private ExApplication exApplication;
    ViewHolder holder = null;
    private VideoTimeThumbnailLoader videoTimeThumbnailLoader;
    private VideoThumbnailLoader videoThumbnailLoader;

    /**
     * 分享按钮监听
     */
    View.OnClickListener shareListener;
    /**
     * 编辑按钮监听
     */
    View.OnClickListener editoListener;

    public List<Boolean> ListDelcheck;// 选择要删除的文件列表

    private VideoDB videoDB;
    private SharedPreferences sharedPreferences;

    public LocalVideoAdapter(Context context, List<VideoInfo> list) {
        this.mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
        exApplication = new ExApplication(context);
        videoTimeThumbnailLoader = new VideoTimeThumbnailLoader(context);
        videoThumbnailLoader = new VideoThumbnailLoader(context);

        ListDelcheck = new ArrayList<Boolean>();

        for (int i = 0; i < mList.size(); i++) {
            ListDelcheck.add(false);
        }

        videoDB = new VideoDB(mContext);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        // 初始化监听方法
        initSetonClick();
    }

    /**
     * 初始化监听
     */
    private void initSetonClick() {

        shareListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = view.getId();

                if (!ExApplication.MEMBER_ID.equals("")) {// 检查用户是否已登陆
                    // 检查视频安全性
                    if (!getVideoinGegrity(mList.get(position).getPath())) {
                        return;
                    }

                    // 获取当前网络环境
                    int netType = MinUtil.getNetworkType(mContext);
                    if (netType == 0) {
                        MinUtil.showToast(mContext, "当前网络不可用，请检查后再上传.");
                    } else if (netType == 1) {// wifi
                        GotoShareActivity(ExApplication.MEMBER_ID, mList.get(position).getPath(),
                                mList.get(position).getDisplayName());
                    } else {
                        upVideodialog(position);
                    }

                } else {
                    MinUtil.showToast(mContext, "请先登录");
                }
            }
        };

        editoListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = mList.get(view.getId()).getPath();
                String title = mList.get(view.getId()).getDisplayName();
                Intent intnet = new Intent(mContext, VideoEditorActivity.class);
                intnet.putExtra("filePath", filePath);
                intnet.putExtra("videoTitle", title);
                mContext.startActivity(intnet);
            }
        };

    }

    public void refresh(List<VideoInfo> list) {
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
            convertView = inflater.inflate(R.layout.local_video_item, null);
            holder.flag = (ImageView) convertView.findViewById(R.id.local_video_item_flag);
            holder.rename = (ImageView) convertView.findViewById(R.id.local_video_item_rename);
            holder.playImg = (ImageView) convertView.findViewById(R.id.local_video_item_play);
            holder.title = (TextView) convertView.findViewById(R.id.local_video_item_title);
            holder.time = (TextView) convertView.findViewById(R.id.local_video_item_record_time);
            holder.duration = (TextView) convertView.findViewById(R.id.local_video_item_duration);
            holder.size = (TextView) convertView.findViewById(R.id.local_video_item_size);
            holder.share = (TextView) convertView.findViewById(R.id.local_video_item_share);
            holder.edit = (TextView) convertView.findViewById(R.id.local_video_item_edit);

            holder.check = (ImageView) convertView.findViewById(R.id.local_video_item_check);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.local_video_item_delete_checkbox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // 截取视频文件后缀名前的字符串
        String videoname = mList.get(position).getDisplayName();
        final String[] fileName = videoname.split("\\.mp4");
        // 视频名称
        holder.title.setText(fileName[0]);

        //视频录制时间
        holder.time.setText(getFileLastModifiedTime(mList.get(position).getPath()));

        // 视频截图
        videoThumbnailLoader.DisplayThumbnailForLocalVideo(mList.get(position).getPath(), holder.flag);

        // 视频时长
        videoTimeThumbnailLoader.DisplayThumbnailForLocalTime(mList.get(position).getPath(), holder.duration);
        // 视频大小
        holder.size.setText(FormetFileSize(getFileSizes((mList.get(position).getPath()))));

        // 如果处于批量删除状态
        if (VideoManagerActivity.inEditorState == true) {
            InTheDelState(position);
        } else {
            NoInTheDelState();
        }


        // 播放监听
        holder.flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(mList.get(position).getPath())), "video/*");
                mContext.startActivity(intent);
            }
        });

        //重命名监听
        holder.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("mList==before========" + mList.toString());
                createDialog(position, fileName[0], holder.title);
                System.out.println("mList==after========" + mList.toString());
            }
        });

        // 分享监听
        holder.share.setId(position);
        holder.share.setOnClickListener(shareListener);

        if (sharedPreferences.getBoolean("videoUploading", false)) {
            // 检测到有视频处于上传状态，分享按钮设置为不可点状态
            holder.share.setClickable(false);
            holder.share.setBackground(mContext.getResources().getDrawable(
                    MResource.getIdByName(mContext, "drawable",
                            "corner_gray_stroke")));

        } else {
            holder.share.setClickable(true);
            holder.share.setText("分享");
            holder.share.setBackground(mContext.getResources().getDrawable(
                    MResource.getIdByName(mContext, "drawable", "corner_red_stroke")));

        }

        if (mList.get(position).getVideoStation().toString()
                .equals("uploading")) {
            // 检测到当前视频处于上传状态，分享按钮设置为“上传中”
            holder.share.setClickable(true);
            holder.share.setText("上传中");
            holder.share.setBackground(mContext.getResources().getDrawable(
                    MResource.getIdByName(mContext, "drawable", "corner_blue_stroke")));
        }

        // 编辑监听
        holder.edit.setId(position);
        holder.edit.setOnClickListener(editoListener);

        return convertView;
    }

    /**
     * @param memberID   用户ID
     * @param fileSrc    视频地址
     * @param videoTitle 视频标题
     */
    public void GotoShareActivity(String memberID, String fileSrc,
                                  String videoTitle) {

        Intent intent = new Intent();
        intent.putExtra("memberID", memberID);
        intent.putExtra("videosrc", fileSrc);
        intent.putExtra("videoTitle", videoTitle);

        intent.setClass(mContext, ShareActivity.class);
        mContext.startActivity(intent);
    }


    /**
     * 文件创建时间
     *
     * @param filePath
     * @return
     */
    private String getFileLastModifiedTime(String filePath) {
        String path = filePath.toString();
        File f = new File(path);
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime());
    }

    class ViewHolder {
        //视频截图
        private ImageView flag;
        //重命名图标
        private ImageView rename;
        //视频名称
        private TextView title;
        //播放
        private ImageView playImg;
        // 视频时间
        private TextView time;
        // 视频长度
        private TextView duration;
        // 视频大小
        private TextView size;
        // 分享按钮
        private TextView share;
        // 编辑按钮
        private TextView edit;
        // 显示勾选状态的视频
        private ImageView check;
        // 批量删除勾选框
        private CheckBox checkBox;
    }

    /**
     * 处于批量删除状态
     *
     * @param position
     */
    private void InTheDelState(final int position) {
        holder.playImg.setVisibility(View.GONE);
        holder.check.setVisibility(View.VISIBLE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.check.setId(position);

        if (ListDelcheck.get(position)) {
            holder.check.setImageResource(MResource.getIdByName(mContext,
                    "drawable", "check_back_true"));

        } else {
            holder.check.setImageResource(MResource.getIdByName(mContext,
                    "drawable", "check_back_false"));

        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ListDelcheck.get(position)) {

                    ListDelcheck.set(position, false);
                    LocalVideoFragment.localVideoListCheckToDel.remove(mList
                            .get(position));
                    // 发消息通知标题栏进行更改
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(2);
                } else {

                    ListDelcheck.set(position, true);
                    LocalVideoFragment.localVideoListCheckToDel.add(mList
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

    /**
     * 检查视频完整性*
     */
    private boolean getVideoinGegrity(String filepath) {
        // 检查文件是否存在
        File file = new File(filepath);
        if (!file.exists()) {
            Toast.makeText(mContext, "视频文件不存在", 0).show();
            // 删除该视频
            videoDB.DelectForFilePath(filepath);
            return false;
        }
        // 获取视频长度
        VideoTimeThumbnailLoader videoTimeThumbnailLoader = new VideoTimeThumbnailLoader(
                mContext);
        String videoLength = videoTimeThumbnailLoader
                .GetThumbnailForLocalVideo(filepath);
        //获取视频长度（毫秒）
        long videosecond = Integer.valueOf(videoLength);
        if (videosecond < 10000) {
            MinUtil.showToast(mContext, "视频长度不能少于10秒");
            return false;
        } else if (videosecond > 1800000) {
            MinUtil.showToast(mContext, "视频长度不能大于30分钟");
            return false;
        }
        // 计算视频文件的大小
        long filelong = LocalVideoAdapter.getFileSizes(filepath);
        if ((filelong / 1048576) > 800) {
            MinUtil.showToast(mContext, "视频长度不能大于800M");
            return false;
        }
        return true;
    }

    private void upVideodialog(final int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("注意")
                .setMessage("当前手机处于非WIFI环境，上传视频将消耗一定的手机流量,是否上传视频？")
                .setPositiveButton("确定",
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                GotoShareActivity(ExApplication.MEMBER_ID, mList.get(position).getPath(),
                                        mList.get(position).getDisplayName());

                            }
                        }).setNegativeButton("取消", null).show();
    }


    /**
     * 计算视频大小
     *
     * @param f
     * @return
     */
    public static long getFileSizes(String f) {
        File file = new File(f);
        long s = 0;
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
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return s;
    }

    /**
     * 将文件大小（字节）转为M,K,B
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 重命名自定义对话框
     *
     * @param position
     * @param name
     */
    public void createDialog(final int position, final String filename, final TextView name) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("重命名");
        View content = inflater.inflate(R.layout.set_info_view, null);
        dialog.setView(content);
        final EditText editText = (EditText) content.findViewById(R.id.set_info_view_et);

        editText.setHintTextColor(Color.parseColor("#c6c6c6"));
        // 设置名称
        editText.setText(filename);
        final File olefile = new File(mList.get(position).getPath());
        final String videoid = mList.get(position).getVideoId();
        new AlertDialog.Builder(mContext)
                .setTitle("重命名文件")
                .setView(content)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String newName = editText.getText()
                                        .toString();
                                if (!newName.isEmpty()) {
                                    // 获得视频的父路径
                                    final String pFile = olefile
                                            .getParentFile().getPath()
                                            + File.separator;
                                    File newfile = new File(pFile
                                            + newName + ".mp4");
                                    // 修改数据库中的文件名,文件路径
                                    int i = videoDB.renameForVideoName(
                                            newName + ".mp4", videoid,
                                            newfile.getPath());
                                    if (i == 0) {
                                        Toast.makeText(mContext,
                                                "文件名已存在，请重新命名", 0)
                                                .show();
                                    } else {

                                        // 组拼新文件路径并重命名
                                        olefile.renameTo(newfile);
                                        Toast.makeText(mContext,
                                                "修改成功", 0).show();
                                        holder.title
                                                .setText(newName);
                                        // 获取数据库中数据并刷新列表
                                        List<VideoInfo> list = videoDB
                                                .GetVideoList();
                                        refresh(list);
                                    }
                                } else {
                                    Toast.makeText(mContext, "不能为空", 0)
                                            .show();
                                }

                            }
                        }).setNegativeButton("取消", null).show();
    }


}
